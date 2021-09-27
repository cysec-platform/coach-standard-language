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
package eu.smesec.cysec.csl.parser;

import java.util.List;

public class CommandIf extends Command {

  public CommandIf() {
    super();
    numberOfNormalizedParams = 1;
  }


  @Override
  public Atom execute(List<Atom> list, CoachContext coachContext) throws ExecutorException {
    /*
     At this point both the if and else branch are already evaluated. Reproduce by running the test case
     TestCommands#testIfCommand()
     Also: the if and else branch content must be swapped. Otherwise the reverse of the intended behaviour happens.
      */
    if (list.size() != 2 && list.size() != 3) {
      throw new ExecutorException("An \"if\" command always requires two or three patarmeters");
    }

    Atom cond = list.get(0);
    if (cond.isTrue(coachContext)) {
      return list.get(1).execute(coachContext);
    } else {
      Atom ret = list.get(2).execute(coachContext);
      if (ret != null) {
        return ret;
      } else {
        return new Atom(Atom.AtomType.BOOL, "FALSE", null);
      }
    }
  }
}
