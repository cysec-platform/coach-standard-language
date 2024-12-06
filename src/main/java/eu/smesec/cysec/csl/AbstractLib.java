/*-
 * #%L
 * CYSEC Standard Coach Language
 * %%
 * Copyright (C) 2020 - 2024 FHNW (University of Applied Sciences and Arts Northwestern Switzerland)
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

import eu.smesec.cysec.platform.bridge.Command;
import eu.smesec.cysec.platform.bridge.Commands;
import eu.smesec.cysec.platform.bridge.FQCN;
import eu.smesec.cysec.platform.bridge.ILibCal;
import eu.smesec.cysec.platform.bridge.CoachLibrary;
import eu.smesec.cysec.platform.bridge.execptions.CacheException;
import eu.smesec.cysec.platform.bridge.generated.Answer;
import eu.smesec.cysec.platform.bridge.generated.Block;
import eu.smesec.cysec.platform.bridge.generated.Metadata;
import eu.smesec.cysec.platform.bridge.generated.Question;
import eu.smesec.cysec.platform.bridge.generated.Questionnaire;
import eu.smesec.cysec.platform.bridge.generated.Questions;
import eu.smesec.cysec.platform.bridge.md.MetadataUtils;
import eu.smesec.cysec.csl.parser.ExecutorContext;
import eu.smesec.cysec.csl.parser.ExecutorException;
import eu.smesec.cysec.csl.parser.ParserException;
import eu.smesec.cysec.csl.parser.Atom;
import eu.smesec.cysec.csl.parser.CySeCExecutorContextFactory;
import eu.smesec.cysec.csl.questions.QuestionTypes;
import eu.smesec.cysec.csl.skills.Endurance;
import eu.smesec.cysec.csl.skills.ScoreFactory;
import eu.smesec.cysec.csl.utils.Utils;
import eu.smesec.cysec.platform.bridge.utils.Tuple;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static eu.smesec.cysec.platform.bridge.md.MetadataUtils.parseMvalues;

public abstract class AbstractLib implements CoachLibrary {
    protected Questionnaire questionnaire;
    protected ILibCal cal;
    protected Endurance endurance;
    private ExecutorContext executorContext;
    private String id;
    private Map<String, List<String>> activeQuestionsPerInstance = new HashMap<>();
    private String[] gradeLetters = {"F", "E", "D", "C", "B", "A"};
    private Logger logger;
    public static Properties prop = new Properties();
    private LogicRunner logicRunner;
    private String activeInstance = "";


    private PersistanceManager persistanceManager;
    private boolean coldStart = true;

    private List<String> getActiveQuestions() {
        return activeQuestionsPerInstance.getOrDefault("___DEFAULT_INSTANCE___", new ArrayList<>());
    }

    private List<String> getActiveQuestions(FQCN fqcn) {
        if (fqcn.isTopLevel()) return getActiveQuestions();
        return getActiveQuestions(fqcn.getName());
    }

    private List<String> getActiveQuestions(String instanceName) {
        return activeQuestionsPerInstance.getOrDefault(instanceName, new ArrayList<>());
    }

    private void addActiveQuestion(String instanceName, String questionId) {
        activeQuestionsPerInstance.computeIfAbsent(instanceName, k -> new ArrayList<>()).add(questionId);
    }

    private void addActiveQuestion(String questionId) {
        addActiveQuestion("___DEFAULT_INSTANCE___", questionId);
    }

    @Override
    public String getActiveInstance() {
        return activeInstance;
    }

    @Override
    public void setActiveInstance(String instance) {
        activeInstance = instance;
    }

    @Override
    public void setParent(Object context) {
        logger.info(context.getClass().toString());
        if(!(context instanceof CySeCExecutorContextFactory.CySeCExecutorContext)) throw new IllegalArgumentException();
        CySeCExecutorContextFactory.CySeCExecutorContext parent = (CySeCExecutorContextFactory.CySeCExecutorContext)context;
        CySeCExecutorContextFactory.CySeCExecutorContext myContext = ((CySeCExecutorContextFactory.CySeCExecutorContext) executorContext);
        // may not set itself as parent
        if(myContext.equals(parent)) throw new IllegalStateException();
        // only allow setting parent if current is null
        // Use semantic: root coach doesnt't have a parent (coach.getId() == null)
        if(questionnaire.getParent() == null) throw new IllegalArgumentException();
        if(myContext.getParent() == null) {
            logger.info(String.format("Setting parent context of %s to %s", myContext, parent));
            myContext.setParent(parent);
        } else {
            //throw new IllegalStateException();
        }
    }

    @Override
    public ExecutorContext getParent() {
        return executorContext.getParent();
    }

    @Override
    public void init(String id, Questionnaire questionnaire, ILibCal libCal, Logger logger) {
        this.id = id;
        this.questionnaire = questionnaire;
        this.cal = libCal;
        this.logger = logger;
        executorContext = CySeCExecutorContextFactory.getExecutorContext(getQuestionnaire().getId());
        this.endurance = new Endurance(30);
        persistanceManager = new PersistanceManager(cal, logger, this);
        logger.info("inside AbstractLib");

        // load a properties file
        try {
            prop.load(getClass().getResourceAsStream("/config.properties"));
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to load framework resources bundle!", e);
            e.printStackTrace();
        }

        // register each resource in "resources/assets"
        try {
            libCal.registerResources(this);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to register resources on server! Make sure your resources are located in \"resources/assets\"", e);
            e.printStackTrace();
        }

        logicRunner = new LogicRunner(logger, cal, this, questionnaire.getMetadata());

        // initial setup of activeQuestions, add all visible questions
        Questions questions = questionnaire.getQuestions();
        if(questions != null) {
            for(Question question : questions.getQuestion()) {
                if(!question.isHidden()) {
                    addActiveQuestion(question.getId());
                }
            }
        }

        initHook(id, questionnaire, libCal);
    }

    /**
     * Hook method for sub classes of AbstractLibrary to run any custom initialization after the base class has finished.
     *
     * @param id            The library ID
     * @param questionnaire The coach object
     * @param libCal        the libCal interface
     */
    protected abstract void initHook(String id, Questionnaire questionnaire, ILibCal libCal);

    @Override
    public List<Command> onResponseChange(Question question, Answer response, FQCN fqcn) {
        // Increase Endurance
        endurance.add(1);

        onResponseChangeHook(question, response);

        // Run the questions logic contained in Mvalue
        try {
            logicRunner.runLogic(question, fqcn);

        } catch(Exception e) {
            logger.log(Level.SEVERE, "Error during execution of question logic", e);
        }

        persistanceManager.saveRating(fqcn);
        persistanceManager.saveSkills(fqcn);

        updateActiveQuestions(fqcn);

        // If this is a subcoach we have to update the variables cache in the parent coach
        if (!fqcn.isTopLevel() && executorContext.getParent() != null) {
            executorContext.getParent().updateSubcoachVariablesCache(fqcn.getCoachId(), fqcn.getName(), executorContext.getVariables(null));
        }

        List<Command> commands = new ArrayList<>();

        if (!fqcn.isTopLevel()) {
            commands.add(new Command(Commands.UPDATE_ACTIVE_QUESTIONS.toString(), getActiveQuestions(fqcn.getName()).toArray(new String[0])));
        } else {
            commands.add(new Command(Commands.UPDATE_ACTIVE_QUESTIONS.toString(), getActiveQuestions().toArray(new String[0])));
        }

        return commands;
    }

    @Override
    public Question getNextQuestion(Question question, FQCN fqcn) {
        ((CySeCExecutorContextFactory.CySeCExecutorContext) executorContext).printVariables(logger);
        try {
            // reevaluate question
            logicRunner.runLogic(question, fqcn);
        } catch (ParserException | ExecutorException e) {
            logger.log(Level.SEVERE, "Error querying next question", e);
        }

        // If the next questions was overridden by the usage of "setNext()" the variable "_coach.nextPage" will be
        // set. Otherwise, this variable is usually not set
        Atom nextVar = executorContext.getVariable("_coach.nextPage", question.getId());
        String nextId;
        ((CySeCExecutorContextFactory.CySeCExecutorContext) executorContext).printVariables(logger);

        if(nextVar == null || nextVar.getType().equals(Atom.AtomType.NULL)) {
            // regular next in line
            int indexOfCurrent = getActiveQuestions(fqcn).indexOf(question.getId());
            // what about last question getting last question?
            // then the setNext should be set to summaryPage!
            if(indexOfCurrent == getActiveQuestions(fqcn).size() - 1) {
                return null;
            }
            nextId = getActiveQuestions(fqcn).get(indexOfCurrent + 1);
        } else {
            // access next variable
            nextId = nextVar.getId();
        }
        Question next = Utils.findById(questionnaire, nextId);

        logger.info("Next is " + (next==null?"NULL":next.getId())+" (questionID: "+nextId+")");
        return next;
    }

    @Override
    public List<Question> peekQuestions(Question question) {
        return getActiveQuestions().stream()
                .map(id -> Utils.findById(questionnaire, id))
                .collect(Collectors.toList());
    }

    @Override
    public List<Tuple<FQCN, Question>> peekQuestionsIncludingSubcoaches(FQCN fqcn) {
        // Get active question ids and map them to question objects
        List<Question> questions = getActiveQuestions()
                .stream()
                .map(id -> Utils.findById(questionnaire, id))
                .collect(Collectors.toList());

        // Get subcoach questions
        Map<String, List<String>> subcoachQuestions = executorContext.getSubcoachActiveQuestionsCache();

        // Insert subcoach questions at each subcoach placeholder question position
        return questions.stream().flatMap(q -> {
            Tuple<FQCN, Question> fqcnQuestionTuple = new Tuple<>(fqcn, q);
            if (Objects.equals(q.getType(), "subcoach")) {
                try {
                    Questionnaire subCoach = cal.getCoach(q.getSubcoachId());
                    String subCoachKey = q.getSubcoachId() + "." + q.getInstanceName();
                    Stream<Tuple<FQCN, Question>> subQuestions = Optional
                            .ofNullable(subcoachQuestions.get(subCoachKey))
                            .stream().flatMap(qs -> qs.stream()
                                    .map(id -> new Tuple<>(FQCN.fromString(fqcn.getRootCoachId() + "." + subCoachKey), Utils.findById(subCoach, id))));
                    return Stream.concat(Stream.of(fqcnQuestionTuple), subQuestions);
                } catch (CacheException e) {
                    logger.severe("An error occurred while getting active questions of subcoaches");
                    return Stream.of(fqcnQuestionTuple);
                }

            } else {
                return Stream.of(fqcnQuestionTuple);
            }
        }).collect(Collectors.toList());
    }

    @Override
    public Question getLastQuestion() {
        String lastId = getActiveQuestions().get(getActiveQuestions().size() - 1);
        return Utils.findById(questionnaire, lastId);
    }

    public Question getFirstQuestion() {
        String firstId = getActiveQuestions().get(0);
        return Utils.findById(questionnaire, firstId);
    }

    public Map<String, Object> getJspModel(String file) {
        Map<String, Object> values = executorContext.getScores().stream()
                .collect(Collectors.toMap(
                        ScoreFactory.Score::getId,
                        ScoreFactory.Score::getValue));


        // Get the list of active subcoaches and insert their cached variables into the JSP model
        List<FQCN> activeSubcoaches = getActiveSubcoaches();
        Map<String, Map<String, Atom>> activeSubcoachVariables = executorContext.getSubcoachVariablesCache()
                .entrySet()
                .stream()
                .filter(entry -> activeSubcoaches.contains(FQCN.fromString(questionnaire.getId() + "." + entry.getKey())))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        values.put("subcoachVariables", activeSubcoachVariables);

        values.put(prop.getProperty("library.skills.endurance"), endurance.get());
        return values;
    }

    /**
     * Get the list of active (e.g. visible, selected) subcoaches in this coach.
     * @return A list of FQCNs
     */
    private List<FQCN> getActiveSubcoaches() {
        return questionnaire.getQuestions().getQuestion()
                .stream()
                .filter(q -> getActiveQuestions().contains(q.getId()))
                .filter(q -> q.getType().equals("subcoach"))
                .map(q -> String.format("%s.%s.%s", questionnaire.getId(), q.getSubcoachId(), q.getInstanceName()))
                .map(FQCN::fromString)
                .collect(Collectors.toList());
    }

    private boolean isEndOfCoach(String question, List<String> questions) {
        // determine end of coach reached: compare if current question is at the end of active question list
        return questions.size() > 0 && question.equals(questions.get(questions.size() - 1));
    }

    /**
     * Entry point for libraries to perform custom operations before abstract lib updates scores and notifies listeners.
     * @param question The answered question
     * @param answer The new answer object
     */
    protected abstract void onResponseChangeHook(Question question, Answer answer);

    @Override
    public List<Command> onBegin(FQCN fqcn) {
        getLogger().info("Running onBegin routine for " + fqcn.toString());
        List<Command> commands = new ArrayList<>();

        Command updateActiveQuestions = new Command(Commands.UPDATE_ACTIVE_QUESTIONS.toString(),
                getActiveQuestions().toArray(new String[0]));
        commands.add(updateActiveQuestions);
        commands.add(new Command(Commands.LOAD_BLOCK.toString(), new String[]{"b1"}));

        try {
            logicRunner.runOnBegin(fqcn);
        } catch (ParserException | ExecutorException e) {
            logger.log(Level.SEVERE, "Error running logic" ,e);
        }

        // Instantiate all sub-coaches
        questionnaire.getQuestions()
                .getQuestion()
                .stream()
                .filter(q -> Objects.equals(q.getType(), "subcoach"))
                .forEach(q -> {
                    try {
                        Questionnaire subcoach = cal.getCoach(q.getSubcoachId());
                        CoachLibrary subcoachLibrary = cal.getLibraries(subcoach.getId()).get(0);
                        Metadata parentArgument = MetadataBuilder
                                .newInstance(subcoachLibrary)
                                .setMvalue("parent-argument", q.getParentArgument())
                                .buildCustom("subcoach-data");
                        cal.instantiateSubCoach(subcoach, Set.of(q.getInstanceName()), parentArgument);
                    } catch (CacheException e) {
                        logger.severe("Error while instantiating sub-coaches: " + e.getMessage());
                    }
                });

        // Save max skill values
        String strengthScore = String.valueOf(executorContext.getScore(prop.getProperty("library.skills.strength")).getValue());
        String strengthMaxScore = String.valueOf(executorContext.getScore(prop.getProperty("library.skills.strengthMax")).getValue());
        String knowhowScore = String.valueOf(executorContext.getScore(prop.getProperty("library.skills.knowhow")).getValue());
        String knowhowMaxScore = String.valueOf(executorContext.getScore(prop.getProperty("library.skills.knowhowMax")).getValue());
        String uuScore = String.valueOf(executorContext.getScore(prop.getProperty("library.skills.uu")).getValue());
        String uuMaxScore = String.valueOf(executorContext.getScore(prop.getProperty("library.skills.uuMax")).getValue());

        /*try {
            getLogger().info("Writing max values to xml");
            cal.setMetadataOnCompany(MetadataUtils.createMetadata(MetadataUtils.MD_SKILLS, Arrays.asList(
                    MetadataUtils.createMvalueStr(MV_KNOW_HOW_MAX, String.valueOf(knowhowMaxScore)),
                    MetadataUtils.createMvalueStr(MV_KNOW_HOW, String.valueOf(knowhowScore)),
                    MetadataUtils.createMvalueStr(MV_STRENGTH_MAX, String.valueOf(strengthMaxScore)),
                    MetadataUtils.createMvalueStr(MV_STRENGTH, String.valueOf(strengthScore))
            )));

        } catch (CacheException e) {
            getLogger().severe(e.getMessage());
        }*/

        onBeginHook();

        return commands;
    }

    protected abstract void onBeginHook();

    @Override
    public synchronized List<Command> onResume(String questionId, FQCN fqcn) {
        getLogger().info("Resuming from question " + questionId);
        // run onBegin routine to execute onBegin logic
        onBegin(fqcn);
        // only run initialization of answers and skills after reboot
        if (isColdStart()) {
            getLogger().info("Resuming from cold start. Reading all answers and skills");
            // init answers. This has to happen here, because there is no context attribute during init()
            try {
                recoverAnswers(fqcn);
                // restore Score and Skills

                Metadata skills = cal.getMetadataOnCompany(MetadataUtils.MD_SKILLS);
                Map<String, MetadataUtils.SimpleMvalue> skillsMap =
                    skills != null ? parseMvalues(skills.getMvalue()) : new HashMap<>();

                String strengthScore = String.valueOf(executorContext.getScore(prop.getProperty("library.skills.strength")).getValue());
                String knowhowScore = String.valueOf(executorContext.getScore(prop.getProperty("library.skills.knowhow")).getValue());
                //
                this.endurance.restore(skillsMap.get(prop.getProperty("library.skills.enduranceState")));
                logger.info(String.format("Restored Strengt: %s, Knowhow: %s, Endurance: %s",
                        strengthScore, knowhowScore, endurance));
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error occured during recovery", e);
            }
            setColdStart(false);
        }
        updateActiveQuestions(fqcn);

        // Call library hook
        onResumeHook(questionId);

        List<Command> commands = new ArrayList<>();

        // find block that contains the passed questionId
        String blockId = "b1";
        if (questionnaire.getBlocks() != null) {
            for (Block blockCandidate : questionnaire.getBlocks().getBlock()) {
                Optional<Question> optional = blockCandidate.getQidList().stream()
                        .map(Question.class::cast)
                        .filter(question -> question.getId().equals(questionId))
                        .findFirst();
                if (optional.isPresent()) blockId = blockCandidate.getId();
            }
        }
        Command updateActiveQuestions = new Command(Commands.UPDATE_ACTIVE_QUESTIONS.toString(),
                getActiveQuestions().toArray(new String[0]));
        commands.add(updateActiveQuestions);
        commands.add(new Command(Commands.LOAD_BLOCK.toString(), new String[]{blockId}));
        return commands;
    }

    private void recoverAnswers(FQCN fqcn) throws CacheException{
        for (Answer answer : cal.getAllAnswers()) {
            String questionId = answer.getQid();
            getLogger().info(String.format("Restoring answer %s for question %s", answer.getText(), answer.getQid()));
            Question question = Utils.findById(questionnaire, questionId);

            if (question == null) continue;

            // for typeA questions all selected option are stored in Aid list
            // SelectQuestion and TypeA question unfortunately inherit from Astar but dont use the Aid list.
            String questionType = question.getType();
            if(questionType.equals(QuestionTypes.Astar) && !questionType.equals(QuestionTypes.yesno) && !(questionType.equals(QuestionTypes.A))) {
                String[] options = answer.getAidList().split(" ");
                for(String option : options) {
                    Answer answerOption = new Answer();
                    answerOption.setQid(questionId);
                    answerOption.setText(option);
                    // invoke onResponseChange as if each option as given separatedly
                    onResponseChange(question, answerOption, fqcn);
                }
            } else if(questionType.equals(QuestionTypes.text) || questionType.equals(QuestionTypes.date)) {
                onResponseChange(question, answer, fqcn);
            } else {
                onResponseChange(question, answer, fqcn);
            }
        }
    }

    protected abstract void onResumeHook(String qId);

    /**
     * Clear active questions and readd all visible questions.
     */
    @Override
    public void updateActiveQuestions(FQCN fqcn) {
        getActiveQuestions(fqcn.getName()).clear();
        getActiveQuestions().clear();
        for(Question question : questionnaire.getQuestions().getQuestion()) {
            if(!question.isHidden()) {
                addActiveQuestion(fqcn.getName(), question.getId());
                addActiveQuestion(question.getId());
            }
        }

        // Sub coaches have to update the active questions cache of it's parent
        if (getParent() != null) {
            getParent().updateSubcoachActiveQuestionsCache(fqcn.getCoachId(), fqcn.getName(), getActiveQuestions(fqcn.getName()));
        }
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void setId(String s) {
        this.id = s;
    }

    @Override
    public Questionnaire getQuestionnaire() {
        return this.questionnaire;
    }

    @Override
    public void setQuestionnaire(Questionnaire questionnaire) {
        this.questionnaire = questionnaire;
    }

    @Override
    public InputStream getResource(List<String> segments) {
        String location = "";

        for (int i = 0; i < segments.size(); i++) {
            // NEEDS the prefix slash
            location += "/" + segments.get(i);
        }
        logger.info(String.format("Loading file: %s", location));
        InputStream in = null;
        try {
            in = getClass().getResourceAsStream(location);
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
        return in;
    }

    @Override
    public void setCal(ILibCal iLibCal) {
        this.cal = iLibCal;
    }

    @Override
    public Object getContext() {
        return executorContext;
    }

    public Logger getLogger() {
        return logger;
    }

    public boolean isColdStart() {
        return coldStart;
    }

    public void setColdStart(boolean coldStart) {
        this.coldStart = coldStart;
    }

    // To ease and satisfy tests, can be removed in future
    public List<String> getQuestions() {
        return getActiveQuestions();
    }

    public PersistanceManager getPersistanceManager() {
        return persistanceManager;
    }

    public ExecutorContext getExecutorContext() {
        return executorContext;
    }

    /**
     * <p>Calculates the gpa with the current score and return appropriate grade letter.</p>
     *
     * The formula is copied from the swiss grading system.
     *
     * @return the current grade
     */
    public String getGrade() {
        // scores are saved as integers but doubles are required to calculate the grade point average
        // index corresponds to letter therefore result must be integer again.
        ScoreFactory.Score score = executorContext.getScore(prop.getProperty("library.skills.uu"));
        ScoreFactory.Score scoreMax = executorContext.getScore(prop.getProperty("library.skills.uuMax"));
        int gpa = (int) ((score.getValue()) / (scoreMax.getValue()) * 5);
        return gradeLetters[gpa];
    }
}
