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

import java.util.List;

/**
 * {@code arrayElements(arrName, length)} checks whether the array is of the given length.
 */
public class CommandArrayElements extends CommandAbstractList {

  @Override
  public Atom execute(List<Atom> aList, CoachContext coachContext) throws ExecutorException {
    // expects 2 parameter
    checkNumParams(aList, 2);

    // evaluate parameters
    Atom array = checkAtomType(aList.get(0), AtomType.STRING, true, coachContext, "array");
    Atom expectedLength = checkAtomType(aList.get(1), AtomType.INTEGER, true, coachContext, "length");

    List<String> tempList = stringToList(coachContext.getContext().getVariable(array.getId(),null).getId());

    return Atom.fromBoolean(tempList.size() == Integer.parseInt(expectedLength.getId()));
  }
}
