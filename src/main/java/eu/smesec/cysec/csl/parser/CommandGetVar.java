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

import static eu.smesec.cysec.csl.parser.Atom.NULL_ATOM;

public class CommandGetVar extends Command {

  public Atom execute(List<Atom> aList, CoachContext coachContext) throws ExecutorException {

    // expects 3 parameters: origin question id, score name and value
    if (aList.size() != 1) {
      throw new ExecutorException("Invalid number of arguments. Expected 2 parameters.");
    }

    // evaluate parameters
    Atom varName = aList.get(0).execute(coachContext);
    Atom varContext = aList.get(1).execute(coachContext);

    // assert type of parameters
    if (varName.getType() != Atom.AtomType.STRING) {
      throw new ExecutorException("Invalid types for parameters: Provide [0] String, [1] String and [2] Numeric value");
    }

    // set the score
    coachContext.getContext().getVariable(varName.getId(), varContext == NULL_ATOM ? null : varContext.toString());

    return NULL_ATOM;
  }

}
