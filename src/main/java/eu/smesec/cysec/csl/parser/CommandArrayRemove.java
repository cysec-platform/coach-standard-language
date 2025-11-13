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

/**
 * <div class="command-doc">
 *   <div class="command-header">
 *     <h2 class="command-name">arrayRemove</h2>
 *   </div>
 *
 *   <div class="command-signature">
 *     <code><span class="return-type">BOOL</span> arrayRemove(<span class="params">arrayVarName: STRING, element: STRING</span>)</code>
 *   </div>
 *
 *   <div class="command-description">
 *     <p>This command removes a specified element from an existing array-like variable (represented as a comma-separated string).</p>
 *     <p>The <code>arrayVarName</code> identifies the variable holding the array, and <code>element</code> is the string to be removed. If the element appears multiple times, all occurrences will be removed.</p>
 *   </div>
 *
 *   <div class="command-section">
 *     <h3>Parameters</h3>
 *     <table class="params-table">
 *       <thead>
 *         <tr>
 *           <th>Name</th>
 *           <th>Type</th>
 *           <th>Required</th>
 *           <th>Description</th>
 *         </tr>
 *       </thead>
 *       <tbody>
 *         <tr>
 *           <td><code>arrayVarName</code></td>
 *           <td><code>STRING</code></td>
 *           <td>Yes</td>
 *           <td>The name of the variable (comma-separated string) from which the element will be removed.</td>
 *         </tr>
 *         <tr>
 *           <td><code>element</code></td>
 *           <td><code>STRING</code></td>
 *           <td>Yes</td>
 *           <td>The string element to remove from the array.</td>
 *         </tr>
 *       </tbody>
 *     </table>
 *   </div>
 *
 *   <div class="command-section">
 *     <h3>Return Value</h3>
 *     <p><code>BOOL</code> - Always returns <code>TRUE</code> upon successful execution.</p>
 *   </div>
 *
 *   <div class="command-section">
 *     <h3>Examples</h3>
 *     <div class="example">
 *       <h4>Removing an element from an array</h4>
 *       <pre><code>set("myList", "a,b,c,b");
 * arrayRemove("myList", "b"); // "myList" is now "a,c"</code></pre>
 *       <p class="example-description">Removes all occurrences of "b" from "myList".</p>
 *     </div>
 *     <div class="example">
 *       <h4>Removing non-existent element</h4>
 *       <pre><code>set("myList", "a,b,c");
 * arrayRemove("myList", "d"); // "myList" remains "a,b,c"</code></pre>
 *       <p class="example-description">If the element is not found, the array remains unchanged.</p>
 *     </div>
 *   </div>
 *
 *   <div class="command-section">
 *     <h3>Notes</h3>
 *     <ul>
 *       <li>Both <code>arrayVarName</code> and <code>element</code> must evaluate to <code>STRING</code> types.</li>
 *       <li>The variable specified by <code>arrayVarName</code> must exist and ideally hold a comma-separated string. If it doesn't exist, an <code>ExecutorException</code> will be thrown.</li>
 *       <li>This command manipulates a string as if it were an array, based on comma delimiters.</li>
 *     </ul>
 *   </div>
 * </div>
 */
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
