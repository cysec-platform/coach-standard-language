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

import eu.smesec.cysec.csl.parser.Atom.AtomType;

import java.util.Arrays;
import java.util.List;

public class CommandGetVar extends Command {

  public Atom execute(List<Atom> aList, CoachContext coachContext) throws ExecutorException {
    // expects 1-3 parameters
    checkNumParams(aList, 1, 3);

    // evaluate parameters
    Atom varName = checkAtomType(aList.get(0), Atom.AtomType.STRING, true, coachContext, "variable");
    Atom varDefault = Atom.NULL_ATOM;
    if(aList.size() > 1) {
      varDefault = checkAtomType(
        aList.get(1),
        Arrays.asList(AtomType.STRING, AtomType.INTEGER, AtomType.BOOL, AtomType.FLOAT),
        true,
        coachContext,
        "default"
      );
    }
    Atom varContext = Atom.NULL_ATOM;
    if(aList.size() > 2) {
      varContext = checkAtomType(
        aList.get(2),
        Arrays.asList(AtomType.STRING, AtomType.NULL),
        true,
        coachContext,
        "context"
      );
    }

    // set the score
    Atom ret = coachContext.getContext().getVariable(varName.getId(), varContext == Atom.NULL_ATOM ? null : varContext.getId());

    if(ret==NULL_ATOM || ret==null) {
      ret=varDefault;
    }

    if(ret==null) {
      ret=NULL_ATOM;
    }

    return ret;
  }

}
