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

import java.util.List;

/**
 * {@code if(condition, trueBranch[, falseBranch = FALSE])} evaluates the condition and checks whether
 * that evaluates to {@link Atom#TRUE}. If so, evaluates and returns the value of the second parameter,
 * if not, it evaluates the false branch in the third parameter.
 */
public class CommandIf extends Command {

  /**
   * @return the value 1 to only evaluate the condition, not the alternatives.
   */
  @Override
  public int getNumberOfNormalizedParams() {
    return 1;
  }

  @Override
  public Atom execute(List<Atom> aList, CoachContext coachContext) throws ExecutorException {
    checkNumParams(aList, 2, 3);

    Atom condition = aList.get(0);
    Atom trueBranch = aList.get(1);
    Atom falseBranch = Atom.FALSE;
    if(aList.size() > 2) {
      falseBranch = aList.get(2);
    }

    boolean isTrue;
    try {
      isTrue = condition.isTrue(coachContext);
    } catch(ExecutorException e) {
      throw new ExecutorException("Error while executing if condition " + condition, e);
    }

    // Evaluates the correct branch and returns its result.
    if (isTrue) {
      return trueBranch.execute(coachContext);
    } else {
      return falseBranch.execute(coachContext);
    }
  }
}
