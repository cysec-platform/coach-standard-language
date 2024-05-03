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

}
