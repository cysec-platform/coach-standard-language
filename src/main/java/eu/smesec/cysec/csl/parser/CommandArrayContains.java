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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CommandArrayContains extends CommandAbstractList {

  @Override
  /**
   * Checks if an array contains the element specified.
   *
   * <p>This command has two mandatory parameter:
   *   <ul>
   *     <li>(arrayList; String)The array to append to.</li>
   *     <li>(arrayElement; String)The element to be appended.</li>
   *   </ul>
   * </p>
   * @returns Always true
   */
  public Atom execute(List<Atom> aList, CoachContext coachContext) throws ExecutorException {
    // expects 2 parameter
    checkNumParams(aList, 2,2);

    // evaluate parameters
    Atom arr = checkAtomType(aList.get(0), Arrays.asList(AtomType.STRING), true, coachContext, "ArrayList" );
    Atom elem = checkAtomType(aList.get(1), Arrays.asList(AtomType.STRING), true, coachContext, "arrayElement" );

    List<String> tempList = stringToList(coachContext.getContext().getVariable(arr.getId(),null ).getId());

    return tempList.contains(elem.getId())?Atom.TRUE:Atom.FALSE;
  }

}
