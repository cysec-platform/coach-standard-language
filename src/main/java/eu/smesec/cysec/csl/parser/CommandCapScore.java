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

/**
 * This command locks the value of a certain skill to a given value.
 *
 * <p>The library will keep recording contributions to said skill internally but will return the capped value upon access.
 * There is no corresponding command "removeCap" because coach logic is reevaluated when a question changes. That means
 * the cap on a skill is removed, once the condition that implied the cap does not hold anymore</p>
 *
 * <p>Syntax: capScore("skill", "value");</p>
 * <p>Example: capScore("strength", 50);</p>
 * <p>If q10o3 is selected, knowhow will be limited to 100. Once that condition fails, the cap won't be added again.</p>
 * <pre>
 *     <code>
 *         isSelected("q10o3") : q10o3 : {
 *             capScore("knowhow", 100);
 *          };
 *     </code>
 * </pre>
 *
 * @see CommandAddScore
 */
public class CommandCapScore extends CommandAbstractScore {

  @Override
  void score(String scoreId, String questionId, double value, ExecutorContext context) {
    context.getScore(scoreId).cap(questionId, value);
  }


}

