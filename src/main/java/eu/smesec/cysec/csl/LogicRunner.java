/*-
 * #%L
 * CYSEC Standard Coach Language
 * %%
 * Copyright (C) 2020 - 2025 FHNW (University of Applied Sciences and Arts Northwestern Switzerland)
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package eu.smesec.cysec.csl;

import eu.smesec.cysec.csl.parser.*;
import eu.smesec.cysec.platform.bridge.FQCN;
import eu.smesec.cysec.platform.bridge.ILibCal;
import eu.smesec.cysec.platform.bridge.QuestionAnswerState;
import eu.smesec.cysec.platform.bridge.execptions.CacheException;
import eu.smesec.cysec.platform.bridge.generated.Metadata;
import eu.smesec.cysec.platform.bridge.generated.Mvalue;
import eu.smesec.cysec.platform.bridge.generated.Question;
import eu.smesec.cysec.platform.bridge.md.MetadataUtils;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class LogicRunner {
    private final ILibCal cal;
    private final Logger logger;
    // TODO: Replace with PersistenceManager
    private final AbstractLib library;
    private final String logicMetadataKey;
    private final String logicMvalueKey;

    private final List<CySeCLineAtom> logicPreAst = new ArrayList<>();
    private final List<CySeCLineAtom> logicPostAst = new ArrayList<>();
    private final List<CySeCLineAtom> logicOnBeginAst = new ArrayList<>();
    private final Map<String, List<CySeCLineAtom>> questionsAst;

    private final CompletableFuture<Void> parseTask;

    public LogicRunner(Logger logger, ILibCal cal, AbstractLib library, List<Metadata> metadataList) {
        this.cal = cal;
        this.library = library;
        this.logger = logger;
        logicMetadataKey = AbstractLib.prop.getProperty("coach.metadata.logic");
        logicMvalueKey = AbstractLib.prop.getProperty("coach.mvalue.logic");
        questionsAst = Collections.synchronizedMap(new LinkedHashMap<>(1000, 0.75f, false) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, List<CySeCLineAtom>> eldest) {
                return size() > 1000;
            }
        });

        // Parse the logic of the coach, this is done async because it can take a few seconds to
        // parse
        parseTask = CompletableFuture.runAsync(() -> parseLogic(metadataList));
    }

    public void runOnBegin(FQCN fqcn) throws ParserException, ExecutorException {
        // Problem: runLogic requires LibQuestion object (for the question ID)
        // Use artifical question to fill CoachContext
        Question fakeQuestion = new Question();
        fakeQuestion.setId("qOnBegin");

        runAst(fakeQuestion, logicOnBeginAst, fqcn);
    }

    /**
     * Run logic of a question contained in its logic mvalue
     *
     * @param question The question object
     * @param fqcn the fqcn of the coach
     * @throws ParserException   If a logic line is malformed
     * @throws ExecutorException If there is an error running the logic
     */
    public void runLogic(Question question, FQCN fqcn) throws ParserException, ExecutorException {
        // First, we build up a complete ast containing pre, post and question logic
        List<CySeCLineAtom> ast = new ArrayList<>(logicPreAst);
        ast.addAll(questionsAst.getOrDefault(question.getId(), new ArrayList<>()));
        ast.addAll(logicPostAst);

        // Then we can run it
        runAst(question, ast, fqcn);
    }

    /**
     * This method actually runs the AST. It waits for the parsing to finish and then creates a coach context and
     * executes the AST in this context.
     * @param question The question that whose logic is being run
     * @param ast the AST to execute
     * @param fqcn the FQCN of the coach instance
     * @throws ExecutorException
     */
    private void runAst(Question question, List<CySeCLineAtom> ast, FQCN fqcn) throws ExecutorException {
        // Make sure paring has finished, otherwise we cannot run the logic
        parseTask.join();

        ExecutorContext context = CySeCExecutorContextFactory.getExecutorContext(
                library.getQuestionnaire().getId());

        // Set the unanswered question count system variable
        try {
            long unansweredCount = cal.getQuestionsAnsweredStates()
                    .values()
                    .stream()
                    .filter(s -> s == QuestionAnswerState.UNANSWERED)
                    .count();
            context.setVariable("__SYSTEM_UNANSWERED_COUNT", new Atom(Atom.AtomType.INTEGER, Long.toString(unansweredCount), Collections.emptyList()), null);
        } catch (CacheException e) {
            throw new ExecutorException("There was an error setting system variables: " + e.getMessage());
        }

        CoachContext coachContext = new CoachContext(
                context,
                cal,
                question,
                library.getPersistanceManager().getAnswer(fqcn, question.getId()),
                library.getQuestionnaire(),
                fqcn);
        coachContext.setLogger(logger);
        context.executeQuestion(ast, coachContext);
    }

    /**
     * This method parses the logic of the coach into AST, so it's ready to execute when needed.
     * The coach contains pre-question logic, post-question logic, onBegin-logic and question-specific logic.
     * All of those are parsed and stored in a map.
     * @param metadataList contains the metadata of the questionnaire, which in turn contains the CSL code
     */
    private void parseLogic(List<Metadata> metadataList) {
        try {
            // Parse pre, post and on-begin logic
            logger.info("Parsing pre, post and onBegin logic of coach " + library.getId());
            List<Mvalue> logicMvalueList = metadataList.stream()
                    .filter(metadata -> metadata.getKey().startsWith(logicMetadataKey))
                    .flatMap(metadata -> metadata.getMvalue().stream())
                    .collect(Collectors.toList());
            Function<String, String> logicExtractor = metadataKey -> Optional.of(
                            MetadataUtils.parseMvalues(logicMvalueList).get(AbstractLib.prop.getProperty(metadataKey)))
                    .map(MetadataUtils.SimpleMvalue::getValue)
                    .orElse("");
            logicPreAst.addAll(getAstOfCode(logicExtractor.apply("coach.mvalue.logicPreQuestion")));
            logicPostAst.addAll(getAstOfCode(logicExtractor.apply("coach.mvalue.logicPostQuestion")));
            logicOnBeginAst.addAll(getAstOfCode(logicExtractor.apply("coach.mvalue.logicOnBegin")));

            // Parse question logic
            logger.info("Parsing questions logic of coach " + library.getId());

            for (Question question : library.getQuestionnaire().getQuestions().getQuestion()) {
                logger.fine("Parsing question logic of question with QID" + question.getId());
                Map<String, MetadataUtils.SimpleMvalue> map = MetadataUtils.parseMvalues(question.getMetadata().stream()
                        .filter(metadata -> metadata.getKey().equals(logicMetadataKey))
                        .flatMap(metadata -> metadata.getMvalue().stream())
                        .collect(Collectors.toList()));
                Optional<MetadataUtils.SimpleMvalue> logic = Optional.ofNullable(map.get(logicMvalueKey));
                if (logic.isPresent()) {
                    List<CySeCLineAtom> ast = getAstOfCode(logic.get().getValue());
                    questionsAst.put(question.getId(), ast);
                }
            }

            logger.info("Finished parsing of coach " + library.getId());
        } catch (ParserException e) {
            logger.severe("Error parsing CSL: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * Parses CSL logic from source code to AST
     * @param code the source code to parse
     * @return AST of the passed code
     * @throws ParserException if something goes wrong
     */
    private List<CySeCLineAtom> getAstOfCode(String code) throws ParserException {
        if (code == null) return new ArrayList<>();
        return new ParserLine(code).getCySeCListing();
    }
}
