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

import eu.smesec.cysec.csl.skills.ScoreFactory;

import java.util.List;
import java.util.Map;

public interface ExecutorContext {

  /***
   * <p>Resets the entire context and all its inner states to the initial state.</p>
   */
  void reset();

  /***
   * <p>gets a Score object from the current context.</p>
   *
   * <p>If a score does not exist a new Score object will be created.</p>
   *
   * @param scoreId the score ID to be returned
   * @return the requested score
   */
  ScoreFactory.Score getScore(String scoreId);

  /***
   * <p>Creates a List of Score objects from the current context.</p>
   *
   * @return the List object containing all currently existing scores.
   */
  List<ScoreFactory.Score> getScores();

  /***
   * <p>reset the respective score forr all question IDs contributing to it.</p>
   *
   * @param scoreId the score to be reset
   * @return the previously contained score
   */
  double resetScore(String scoreId);

  /***
   * <p>Reset all contributions of a question in all scores.</p>
   *
   * @param questionId the ID to be reverted in all scores
   */
  void revertQuestionScore(String questionId);

  /***
   * <p>get a variable content.</p>
   * @param name name of the variable
   * @param context a context for the variable (if null the content of the last set variable is returned)
   * @return the variable content
   */
  Atom getVariable(String name, String context);

  /**
   * <p>get all variables.</p>
   * @param context a context for the variable
   * @return the variable contents mapped to the variable names
   */
  Map<String, Atom> getVariables(String context); 
  
  /***
   * <p>Set a variable content.</p>
   * @param name the name of the variable
   * @param value the value of the variable
   * @param context the context of the variable may be null
   * @return the previous value of the variable
   */
  Atom setVariable(String name, Atom value, String context);

  /**
   * Clears variables of the executor context.
   */
  void clearVariables();

  /***
   * <p>Execute a question script.</p>
   *
   * @param atomList List of CySeCLineAtoms to be executed
   * @param coachContext the context object to be used for the script
   * @return the number of executed lines
   * @throws ExecutorException if an exception happens
   */
  int executeQuestion(List<CySeCLineAtom> atomList, CoachContext coachContext)
      throws ExecutorException;

  /***
   * <p>Get the parent executor context</p>
   *
   * @return the requested context
   */
  ExecutorContext getParent();

  /***
   * <p>Set the parent executor context</p>
   *
   * @param context the context of the variable may be null
   * @return the previously set context
   */
  ExecutorContext setParent(ExecutorContext context);

  /***
   * <p>Exposes the id of the executor context which equals the library id</p>
   *
   * @return the id of the library tied to this context
   */
  String getContextId();

  /**
   * This method can be used to update the subcoach variable cache of the executor context.
   * The subcoach variable cache stores the executor variables of its subcoaches.
   * This is really convenient because it allows us to read out the executor variables of a coach
   * and it's subcoaches very easily when we need it at the end to render the summary page.
   * @param coachId The ID of the subcoach
   * @param instanceName The instance name of the subcoach
   * @param variables The new variable state of the subcoach
   */
  void updateSubcoachVariablesCache(String coachId, String instanceName, Map<String, Atom> variables);

  /**
   * Returns the whole subcoach variables cache of the executor context.
   * @return The entire subcoach variables cache
   */
  Map<String, Map<String, Atom>> getSubcoachVariablesCache();

  /**
   * The subcoach active questions cache contains the current active questions
   * of all subcoaches (if any). This is useful because there's only one executor context
   * per subcoach instance which means if there are multiple instances, it cannot hold the active
   * questions for all types.
   *
   * This method updates the active questions cache for one subcoach instance.
   * @param coachId The subcoach id
   * @param instanceName the subcoach instance id
   * @param activeQuestions the new active questions of the subcoach
   */
  void updateSubcoachActiveQuestionsCache(String coachId, String instanceName, List<String> activeQuestions);

  /**
   * Returns the whole subcoach active questions cache.
   * @return subcoach active questions cache
   */
  Map<String, List<String>> getSubcoachActiveQuestionsCache();

}
