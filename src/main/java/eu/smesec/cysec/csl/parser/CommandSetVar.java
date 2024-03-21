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

import java.util.List;

public class CommandSetVar extends Command {

  public Atom execute(List<Atom> aList, CoachContext coachContext) throws ExecutorException {

    // expects 2 or 3 parameters
    checkNumParams(aList, 2,3);

    // evaluate parameters
    Atom varName = aList.get(0).execute(coachContext);
    Atom varContext = aList.get(1).execute(coachContext);
    Atom varContent = null;
    if (aList.size() == 2) {
      varContext = new Atom(Atom.AtomType.STRING, coachContext.getQuestionContext().getId(), null);
      varContent = aList.get(1).execute(coachContext);
    } else {
      varContent = aList.get(2).execute(coachContext);
    }

    // assert type of parameters
    if (varName.getType() != Atom.AtomType.STRING || (varContext.getType() != Atom.AtomType.STRING && varContext != Atom.NULL_ATOM)) {
      throw new ExecutorException("Invalid types for parameters: Provide [0] String, [1] String and [2] ANY or [0] String and [2] ANY");
    }

    // set the score
    coachContext.getContext().setVariable(varName.getId(), varContent, varContext == Atom.NULL_ATOM ? null : varContext.getId());
    coachContext.getLogger().info(String.format("Set variable %s to %s in context %s", varName.getId(), varContent.getId(), varContent.getId()));

    return Atom.NULL_ATOM;
  }

}
