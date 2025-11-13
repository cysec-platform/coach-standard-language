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
 *     <h2 class="command-name">arrayContains</h2>
 *   </div>
 *
 *   <div class="command-signature">
 *     <code><span class="return-type">BOOL</span> arrayContains(<span class="params">arrayVarName: STRING, element: STRING</span>)</code>
 *   </div>
 *
 *   <div class="command-description">
 *     <p>This command checks if an array-like variable (represented as a comma-separated string) contains a specific element.</p>
 *     <p>It takes the <code>arrayVarName</code> holding the array and the <code>element</code> string to search for. It returns <code>TRUE</code> if the element is found in the array, and <code>FALSE</code> otherwise.</p>
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
 *           <td>The name of the variable (comma-separated string) to search within.</td>
 *         </tr>
 *         <tr>
 *           <td><code>element</code></td>
 *           <td><code>STRING</code></td>
 *           <td>Yes</td>
 *           <td>The string element to search for in the array.</td>
 *         </tr>
 *       </tbody>
 *     </table>
 *   </div>
 *
 *   <div class="command-section">
 *     <h3>Return Value</h3>
 *     <p><code>BOOL</code> - <code>TRUE</code> if the array contains the element, <code>FALSE</code> otherwise.</p>
 *   </div>
 *
 *   <div class="command-section">
 *     <h3>Examples</h3>
 *     <div class="example">
 *       <h4>Checking if an array contains an element</h4>
 *       <pre><code>set("myTags", "technical,legal,quickwin");
 * arrayContains("myTags", "legal"); // TRUE
 * arrayContains("myTags", "financial"); // FALSE</code></pre>
 *       <p class="example-description">Checks for the presence of "legal" and "financial" tags in "myTags".</p>
 *     </div>
 *     <div class="example">
 *       <h4>Conditional logic with array contents</h4>
 *       <pre><code>if(arrayContains(get("selectedFeatures"), "encryption"), set("hasEncryption", TRUE));</code></pre>
 *       <p class="example-description">Sets "hasEncryption" to TRUE if "encryption" is found in the "selectedFeatures" array.</p>
 *     </div>
 *   </div>
 *
 *   <div class="command-section">
 *     <h3>Notes</h3>
 *     <ul>
 *       <li>Both <code>arrayVarName</code> and <code>element</code> must evaluate to <code>STRING</code> types.</li>
 *       <li>The variable specified by <code>arrayVarName</code> must exist and ideally hold a comma-separated string. If it doesn't exist, an <code>ExecutorException</code> will be thrown (as it cannot retrieve the variable).</li>
 *       <li>The comparison is case-sensitive.</li>
 *     </ul>
 *   </div>
 * </div>
 */
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
        checkNumParams(aList, 2, 2);

        // evaluate parameters
        Atom arr = checkAtomType(aList.get(0), Arrays.asList(AtomType.STRING), true, coachContext, "ArrayList");
        Atom elem = checkAtomType(aList.get(1), Arrays.asList(AtomType.STRING), true, coachContext, "arrayElement");

        List<String> tempList = stringToList(
                coachContext.getContext().getVariable(arr.getId(), null).getId());

        return tempList.contains(elem.getId()) ? Atom.TRUE : Atom.FALSE;
    }
}
