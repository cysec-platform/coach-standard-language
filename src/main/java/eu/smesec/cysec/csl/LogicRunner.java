/*-
 * #%L
 * CYSEC Standard Coach Language
 * %%
 * Copyright (C) 2020 - 2021 FHNW (University of Applied Sciences and Arts Northwestern Switzerland)
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

import eu.smesec.cysec.platform.bridge.FQCN;
import eu.smesec.cysec.platform.bridge.ILibCal;
import eu.smesec.cysec.platform.bridge.generated.Metadata;
import eu.smesec.cysec.platform.bridge.generated.Mvalue;
import eu.smesec.cysec.platform.bridge.generated.Question;
import eu.smesec.cysec.platform.bridge.md.MetadataUtils;
import eu.smesec.cysec.csl.parser.CoachContext;
import eu.smesec.cysec.csl.parser.CySeCExecutorContextFactory;
import eu.smesec.cysec.csl.parser.CySeCLineAtom;
import eu.smesec.cysec.csl.parser.ExecutorContext;
import eu.smesec.cysec.csl.parser.ExecutorException;
import eu.smesec.cysec.csl.parser.ParserException;
import eu.smesec.cysec.csl.parser.ParserLine;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class LogicRunner {
    Optional<MetadataUtils.SimpleMvalue> logicPre;
    Optional<MetadataUtils.SimpleMvalue> logicPost;
    Optional<MetadataUtils.SimpleMvalue> logicOnBegin;
    private ILibCal cal;
    private Logger logger;
    // TODO: Replace with PersistenceManager
    private AbstractLib library;
    private String logicFQDN;
    private String logicMetadataKey;
    private String logicMvalueKey;

    public LogicRunner(Logger logger, ILibCal cal, AbstractLib library, List<Metadata> metadataList) {
        this.cal = cal;
        this.library = library;
        this.logger = logger;
        logicMetadataKey = library.prop.getProperty("coach.metadata.logic");
        logicMvalueKey = library.prop.getProperty("coach.mvalue.logic");
        logicFQDN = logicMetadataKey + "." + logicMvalueKey;
        List<Mvalue> logicMvalueList = metadataList.stream()
                .filter(metadata -> metadata.getKey().startsWith(logicMetadataKey))
                .flatMap(metadata -> metadata.getMvalue().stream())
                .collect(Collectors.toList());
        Map<String, MetadataUtils.SimpleMvalue> logicMvalues = MetadataUtils.parseMvalues(logicMvalueList);
        logicPre = Optional.ofNullable(logicMvalues.get(library.prop.getProperty("coach.mvalue.logicPreQuestion")));
        logicPost = Optional.ofNullable(logicMvalues.get(library.prop.getProperty("coach.mvalue.logicPostQuestion")));
        logicOnBegin = Optional.ofNullable(logicMvalues.get(library.prop.getProperty("coach.mvalue.logicOnBegin")));
    }

    public void runOnBegin(FQCN fqcn) throws ParserException, ExecutorException {
        // Problem: runLogic requires LibQuestion object (for the question ID)
        // Use artifical question to fill CoachContext
        Question fakeQuestion = new Question();
        fakeQuestion.setId("qOnBegin");

        runLogic(fakeQuestion, logicOnBegin, fqcn);
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
        logger.info("Extracting logic from question");
        Map<String, MetadataUtils.SimpleMvalue> map = MetadataUtils.parseMvalues(
                question.getMetadata().stream()
                        .filter(metadata -> metadata.getKey().equals(logicMetadataKey))
                        .flatMap(metadata -> metadata.getMvalue()
                                .stream())
                        .collect(Collectors.toList())
        );

        Optional<MetadataUtils.SimpleMvalue> logic = Optional.ofNullable(map.get(logicMvalueKey));
        runLogic(question, logic, fqcn);
    }

    /**
     * Combines pre, post and question logic and runs its value
     *
     * <p>Logic doesn't run if it is a blank line, otherwise ParserError occurs</p>
     *
     * @param question The question object
     * @param logic the logic
     * @param fqcn The fqcn of the coach
     * @throws ParserException   If the composed logic line is malformed
     * @throws ExecutorException If there is an error running the logic
     */
    public void runLogic(Question question, Optional<MetadataUtils.SimpleMvalue> logic, FQCN fqcn) throws ParserException, ExecutorException {
        String logicComposition = "";
        if (logicPre.isPresent()) logicComposition += logicPre.get().getValue();
        if (logic.isPresent()) logicComposition += logic.get().getValue();
        if (logicPost.isPresent()) logicComposition += logicPost.get().getValue();

        if(logicComposition.isEmpty()) {
            logger.info("No logic to run");
        } else {
            logger.info("Running logic: " + logicComposition);
            List<CySeCLineAtom> lines = new ParserLine(logicComposition).getCySeCListing();
            ExecutorContext context = CySeCExecutorContextFactory.getExecutorContext(library.getQuestionnaire().getId());
            CoachContext coachContext = new CoachContext(
                    context,
                    cal,
                    question,
                    library.getPersistanceManager().getAnswer(fqcn, question.getId()),
                    library.getQuestionnaire(),
                    fqcn);
            coachContext.setLogger(logger);
            context.executeQuestion(lines, coachContext);
        }

    }
}
