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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * {@code arrayAdd("arrName", "element"[, unique = FALSE])} adds the given element to the end of the array
 * identified by the name. If the optional parameter {@code unique} is provided and set to {@link Atom#TRUE},
 * duplicate entries are removed from the Array. Always returns {@link Atom#TRUE}.
 */
public class CommandArrayAdd extends CommandAbstractList {

  @Override
  public Atom execute(List<Atom> aList, CoachContext coachContext) throws ExecutorException {
    // expects 2-3 parameters
    checkNumParams(aList, 2, 3);

    // evaluate parameters
    Atom arr = checkAtomType(aList.get(0), AtomType.STRING, true, coachContext, "array");
    Atom elem = checkAtomType(aList.get(1), AtomType.STRING, true, coachContext, "element");
    Atom unique = Atom.FALSE;
    if(aList.size() > 2) {
      unique = checkAtomType(aList.get(2), AtomType.BOOL, true, coachContext, "unique");
    }

    Atom arrayVar = coachContext.getContext().getVariable(arr.getId(), null);
    if (arrayVar == null) {
      throw new ExecutorException(String.format("arrayAdd failed because the array '%s' couldn't be found. You cannot add an element too an array that does not exist!\n", arr.getId()));
    }
    List<String> tempList = stringToList(arrayVar.getId());
    tempList.add(elem.getId());

    // process unique modifier
    if(unique.isTrue(coachContext)) {
      Set<String> tempSet = new HashSet<>(tempList);
      tempList.clear();
      tempList.addAll(tempSet);
    }

    coachContext.getContext().setVariable(arr.getId(), Atom.fromString(listToString(tempList)), null);

    return Atom.TRUE;
  }
}
