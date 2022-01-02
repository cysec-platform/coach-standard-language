/*-
 * #%L
 * CYSEC Standard Coach Language
 * %%
 * Copyright (C) 2020 - 2022 FHNW (University of Applied Sciences and Arts Northwestern Switzerland)
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
 * This command adds/subtracts a value to a given skill
 *
 * <p>The name can be arbitrary but must be unique within the coach context. The platform might only support a skills with a certain name prefix. The sign of the value determines whether
 * the score should be added (+) or subtracted (-). Note that the value returned by
 * {@link CySeCExecutorContextFactory.CySeCExecutorContext#getScore(String)}
 * won't change if a cap has previously been applied to that score</p>
 *
 * <p>Syntax: addScore("skill", "value");</p>
 * <p>Example: addScore("strength", 50);</p>
 * <p>Example: addScore("knowhow", -10);</p>
 * @see CommandCapScore
 */
public class CommandAddScore extends CommandAbstractScore {

  @Override
  void score(String scoreId, String questionId, double value, ExecutorContext context) {
    context.getScore(scoreId).add(questionId, value);

    // TODO: Decide if all questions should contribute to parent score
    /*if(context.getParent() != null) {
      context.getParent().getScore(scoreId).add(questionId, value);
    }*/
  }

}

