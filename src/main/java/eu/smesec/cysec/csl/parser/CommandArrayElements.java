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

public class CommandArrayElements extends CommandAbstractList {

  @Override
  /**
   * Checks if an array has the specified size.
   *
   * <p>This command has two mandatory parameter:
   *   <ul>
   *     <li>(arrayList; String)The array to append to.</li>
   *     <li>(arrayLength; String)The element to be appended.</li>
   *   </ul>
   * </p>
   * @returns Always true
   */
  public Atom execute(List<Atom> aList, CoachContext coachContext) throws ExecutorException {
    // expects 2 parameter
    checkNumParams(aList, 2,2);

    // evaluate parameters
    Atom arr = checkAtomType(aList.get(0), Arrays.asList(AtomType.STRING), true, coachContext, "arrayList" );
    Atom noelem = checkAtomType(aList.get(1), Arrays.asList(AtomType.INTEGER), true, coachContext, "arrayLength" );

    List<String> tempList = stringToList(coachContext.getContext().getVariable(arr.getId(),null ).getId());

    return tempList.size()==Integer.valueOf(noelem.getId())?Atom.TRUE:Atom.FALSE;
  }

}
