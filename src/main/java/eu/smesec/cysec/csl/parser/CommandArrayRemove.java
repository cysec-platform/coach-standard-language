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

public class CommandArrayRemove extends CommandAbstractList {

    @Override
    /**
     * Adds an element to an existing array.
     *
     * <p>This command has two mandatory parameter:
     *   <ul>
     *     <li>(arrayList; String)The array to append to.</li>
     *     <li>(arrayElement; String)The element to be appended.</li>
     *   </ul>
     * </p>
     * <p>This Command has one optional parameter:
     *   <ul>
     *     <li>(boolean; default false) Remove duplicated entries and sort.</li>
     *   </ul>
     * </p>
     * @returns Always true
     */
    public Atom execute(List<Atom> aList, CoachContext coachContext) throws ExecutorException {
        // expects 1 parameter
        checkNumParams(aList, 2, 3);

        // evaluate parameters
        Atom arr = checkAtomType(aList.get(0), Arrays.asList(AtomType.STRING), true, coachContext, "ArrayList");
        Atom elem = checkAtomType(aList.get(1), Arrays.asList(AtomType.STRING), true, coachContext, "ArrayList");
        Atom unique = Atom.FALSE;

        List<String> tempList = stringToList(
                coachContext.getContext().getVariable(arr.getId(), null).getId());
        tempList.remove(elem.getId());

        Atom result = new Atom(AtomType.STRING, listToString(tempList), null);

        coachContext.getContext().setVariable(arr.getId(), result, null);

        return Atom.TRUE;
    }
}
