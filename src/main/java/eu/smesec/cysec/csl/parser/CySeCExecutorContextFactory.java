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
package eu.smesec.cysec.csl.parser;

import static eu.smesec.cysec.csl.parser.Atom.NULL_ATOM;

import eu.smesec.cysec.csl.skills.BadgeEventListener;
import eu.smesec.cysec.csl.skills.BadgeFactory;
import eu.smesec.cysec.csl.skills.BadgeFactory.Badge;
import eu.smesec.cysec.csl.skills.RecommendationEventListener;
import eu.smesec.cysec.csl.skills.RecommendationFactory;
import eu.smesec.cysec.csl.skills.ScoreFactory;
import java.io.Serializable;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CySeCExecutorContextFactory {

    private static class Variable {
        private Map<String, Atom> var = new HashMap<>();
        private Atom lastval = null;

        public Atom setVariable(String context, Atom value) {
            Atom ret = getVariable(context);
            if (context != null) {
                var.put(context, value);
            }
            lastval = value;
            return ret;
        }

        public Atom getVariable(String context) {
            return var.get(context) == null ? lastval : var.get(context);
        }

        public Collection<Atom> getAll() {
            return var.values();
        }
    }

    /**
     * Exists once per coach library and holds all relevant information about the context.
     *
     * <p>This includes all created badges and badge classes, recommendations, scores and variables.</p>
     * <p>In addition the context is linked to its parent context and may perform manipulation on
     * the parent instead of itw own context.</p>
     */
    public static class CySeCExecutorContext implements ExecutorContext, Serializable {
        private static final long serialVersionUID = 2365600923596387762L;
        private List<String> executedNames = new Vector<>();
        private Logger logger = Logger.getLogger((new Throwable()).getStackTrace()[0].getClassName());
        private ScoreFactory scores = new ScoreFactory();
        private final Object executorLock = new Object();
        private Map<String, Variable> variables = new HashMap<>();
        private ExecutorContext parent = null;
        private RecommendationFactory recommendations = new RecommendationFactory();
        private BadgeFactory badges = new BadgeFactory();
        private String contextId;
        private Map<String, Map<String, Atom>> subcoachVariableCache = new HashMap<>();
        private Map<String, List<String>> subcoachActiveQuestionsCache = new HashMap<>();
        private Map<String, RecommendationFactory> subcoachRecommendationsCache = new HashMap<>();
        private String activeInstance;

        public CySeCExecutorContext(String contextId, Logger log) {
            if (log != null) {
                this.contextId = contextId;
                logger = log;
            }
        }

        public void printVariables(Logger logger) {
            variables.forEach((key, value) -> logger.fine(
                    String.format("%s : %s", key, Arrays.toString(value.getAll().toArray()))));
        }

        public void reset() {
            variables.clear();
            executedNames.clear();
            scores.reset();
            badges.reset();
            recommendations.reset();
            // contextMap.clear();
        }

        public ScoreFactory.Score getScore(String scoreId) {
            return scores.getIntScore(scoreId);
        }

        @Override
        public List<ScoreFactory.Score> getScores() {
            ScoreFactory.Score[] scoreArray = scores.getScoreList(false);
            if (scoreArray != null && scoreArray.length > 0) {
                return Arrays.asList(scoreArray);
            } else {
                return Collections.emptyList();
            }
        }

        @Override
        public String getContextId() {
            return contextId;
        }

        public RecommendationFactory.Recommendation getRecommendation(String id) {
            if (parent != null && parent instanceof CySeCExecutorContext) {
                CySeCExecutorContext parentContext = (CySeCExecutorContext) parent;
                return parentContext
                        .subcoachRecommendationsCache
                        .computeIfAbsent(activeInstance, f -> new RecommendationFactory())
                        .getRecommendation(id);
            } else {
                return recommendations.getRecommendation(id);
            }
        }

        public RecommendationFactory.Recommendation[] getRecommendationList() {
            if (parent != null && parent instanceof CySeCExecutorContext) {
                CySeCExecutorContext parentContext = (CySeCExecutorContext) parent;
                return parentContext
                        .subcoachRecommendationsCache
                        .computeIfAbsent(activeInstance, f -> new RecommendationFactory())
                        .getRecommendationList();
            } else {
                return recommendations.getRecommendationList();
            }
        }

        public List<RecommendationFactory.Recommendation> getRecommendationListIncludingSubcoaches() {
            if (parent != null && parent instanceof CySeCExecutorContext) {
                return Arrays.asList(getRecommendationList());
            } else {
                // The following code returns the recommendations of all subcoaches.
                // Since all the subcoaches contain all the recommendations there are many
                // duplicates. We
                // deduplicate by
                // only
                // selecting one of each recommendation making sure that the chosen recommendation
                // is an
                // "active" one if
                // there's an active one amongst the recommendation with the same ID.
                RecommendationFactory.Tag active = new RecommendationFactory.Tag(
                        "active", new RecommendationFactory.TagCategory("_meta", "#000000"));
                return new ArrayList<>(Stream.concat(
                                Stream.of(recommendations), subcoachRecommendationsCache.values().stream())
                        .flatMap(r -> Arrays.stream(r.getRecommendationList()))
                        .collect(Collectors.toMap(RecommendationFactory.Recommendation::getId, r -> r, (a, b) -> {
                            if (a.getTags().contains(active)) return a;
                            if (b.getTags().contains(active)) return b;
                            return a;
                        }))
                        .values());
            }
        }

        public void addRecommendation(RecommendationFactory.Recommendation r) {
            if (parent != null && parent instanceof CySeCExecutorContext) {
                CySeCExecutorContext parentContext = (CySeCExecutorContext) parent;
                parentContext
                        .subcoachRecommendationsCache
                        .computeIfAbsent(activeInstance, f -> new RecommendationFactory())
                        .addRecommendation(r);
            } else {
                recommendations.addRecommendation(r);
            }
        }

        public RecommendationFactory.Recommendation removeRecommendation(String id) {
            if (parent != null && parent instanceof CySeCExecutorContext) {
                CySeCExecutorContext parentContext = (CySeCExecutorContext) parent;
                return parentContext
                        .subcoachRecommendationsCache
                        .computeIfAbsent(activeInstance, f -> new RecommendationFactory())
                        .removeRecommendation(id);
            } else {
                return recommendations.removeRecommendation(id);
            }
        }

        public void setRecommendationListener(RecommendationEventListener listener) {
            if (parent != null && parent instanceof CySeCExecutorContext) {
                CySeCExecutorContext parentContext = (CySeCExecutorContext) parent;
                parentContext
                        .subcoachRecommendationsCache
                        .computeIfAbsent(activeInstance, f -> new RecommendationFactory())
                        .setListener(listener);
            } else {
                recommendations.setListener(listener);
            }
        }

        public BadgeFactory.Badge getBadge(String id) {
            return badges.getBadge(id);
        }

        public BadgeFactory.Badge[] getBadgeList() {
            return badges.getBadgeList();
        }

        public void setBadge(Badge b) {
            badges.setBadge(b);
        }

        public void setBadgeListener(BadgeEventListener listener) {
            badges.setListener(listener);
        }

        public double resetScore(String scoreId) {
            double ret = scores.getScore(scoreId);
            scores.removeScore(scoreId);
            return ret;
        }

        public void revertQuestionScore(String questionId) {
            for (ScoreFactory.Score s : scores.getScoreList(true)) {
                s.revertQuestion(questionId);
            }
        }

        @Override
        public Atom getVariable(String name, String context) {
            synchronized (variables) {
                return variables.get(name) == null
                        ? NULL_ATOM
                        : variables.get(name).getVariable(context);
            }
        }

        @Override
        public Map<String, Atom> getVariables(String context) {
            synchronized (variables) {
                return variables.entrySet().stream()
                        .filter(kv -> kv.getValue().getVariable(context) != null)
                        .collect(Collectors.toMap(
                                kv -> kv.getKey(), kv -> kv.getValue().getVariable(context)));
            }
        }

        @Override
        public Atom setVariable(String name, Atom value, String context) {
            synchronized (variables) {
                if (variables.get(name) == null) {
                    variables.put(name, new Variable());
                }
                if (value == null) {
                    value = NULL_ATOM;
                }
                Atom ret = getVariable(name, context);
                variables.get(name).setVariable(context, value);
                return ret;
            }
        }

        @Override
        public void clearVariables() {
            variables.clear();
        }

        @Override
        public int executeQuestion(List<CySeCLineAtom> atomList, CoachContext coachContext) throws ExecutorException {
            CySeCExecutorContext ec = (CySeCExecutorContext) (coachContext.getContext());
            synchronized (ec.executorLock) {
                ec.executedNames.clear();
                ec.scores.revertQuestion(coachContext.getQuestionContext().getId());

                // this should clear previously set variables from that question
                // ec.variables.remove(coachContext.getQuestionContext().getId());
                for (Map.Entry<String, Variable> variable : ec.variables.entrySet()) {
                    // Make sure system variables are not cleared since they are being set from outside
                    if (variable.getKey().startsWith("__SYSTEM")) continue;

                    variable.getValue().var.remove(coachContext.getQuestionContext().getId());
                    variable.getValue().lastval = null;
                }

                //
                if (ec.getParent() != null) {
                    // One idea could be adding the subcoach id as a prefix when adding to a parent
                    // context
                    ((CySeCExecutorContext) (ec.getParent()))
                            .scores.revertQuestion(
                                    coachContext.getQuestionContext().getId());
                    // todo clear variables from this question too
                    // todo clear executedNames
                }
                return ec.execute(atomList, coachContext);
            }
        }

        @Override
        public ExecutorContext getParent() {
            return parent;
        }

        @Override
        public ExecutorContext setParent(ExecutorContext context) {
            ExecutorContext ret = parent;
            this.parent = context;
            return ret;
        }

        public int execute(List<CySeCLineAtom> atomList, CoachContext coachContext) throws ExecutorException {
            synchronized (executorLock) {
                int ret = 0;
                executedNames.clear();
                ExecutorException retException = null;
                for (CySeCLineAtom la : atomList) {
                    try {
                        Atom condResult = la.getCond().execute(coachContext);
                        if (condResult.isTrue(coachContext) && !executedNames.contains(la.getName())) {
                            la.execute(coachContext);
                            ret++;
                            executedNames.add(la.getName());
                        }
                    } catch (ExecutorException ee) {
                        logger.log(Level.WARNING, "Exception during execution of " + la.getCond(), ee);
                        retException = new ExecutorException(ee.getReason(), retException);
                    }
                }
                if (retException != null) {
                    throw retException;
                }
                return ret;
            }
        }

        @Override
        public void updateSubcoachVariablesCache(String coachId, String instanceName, Map<String, Atom> variables) {
            subcoachVariableCache.put(coachId + "." + instanceName, variables);
        }

        @Override
        public Map<String, Map<String, Atom>> getSubcoachVariablesCache() {
            return subcoachVariableCache;
        }

        @Override
        public void updateSubcoachActiveQuestionsCache(
                String coachId, String instanceName, List<String> activeQuestions) {
            subcoachActiveQuestionsCache.put(coachId + "." + instanceName, new ArrayList<>(activeQuestions));
        }

        @Override
        public Map<String, List<String>> getSubcoachActiveQuestionsCache() {
            return subcoachActiveQuestionsCache;
        }

        public void setActiveInstance(String activeInstance) {
            this.activeInstance = activeInstance;
        }

        public Map<String, RecommendationFactory> getSubcoachRecommendationsCache() {
            return subcoachRecommendationsCache;
        }
    }

    // Since the context exists once per classloader, the map isn't necessary as there will always
    // be
    // a 1:1 relation
    // of library and context. This is an implementation detail of the platform though, therefore we
    // keep the hashmap
    // to remain independant of future change
    private static final Map<String, CySeCExecutorContext> contextMap = new HashMap<>();

    public static CySeCExecutorContext getExecutorContext(String contextId) {
        return getExecutorContext(contextId, null);
    }

    public static CySeCExecutorContext getExecutorContext(String contextId, Logger log) {
        synchronized (contextMap) {
            if (contextMap.get(contextId.toLowerCase()) == null) {
                contextMap.put(contextId.toLowerCase(), new CySeCExecutorContext(contextId, log));
            }
            return contextMap.get(contextId.toLowerCase());
        }
    }
}
