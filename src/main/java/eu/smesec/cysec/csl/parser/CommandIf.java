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

    Atom cond = aList.get(0);

    Atom ret = null;

    boolean isTrue;
    try {
      isTrue = cond.isTrue(coachContext);
    } catch(ExecutorException e) {
      throw new ExecutorException("Error while executing if condition "+aList.get(0) ,e);
    }
    if (isTrue) {
      ret = aList.get(1).execute(coachContext);
    } else {
      if(aList.size()==3) {
        ret = aList.get(2).execute(coachContext);
      } else {
        ret = Atom.FALSE;
      }
    }
    if (ret != null) {
      return ret;
    } else {
      return Atom.FALSE;
    }
  }
}
