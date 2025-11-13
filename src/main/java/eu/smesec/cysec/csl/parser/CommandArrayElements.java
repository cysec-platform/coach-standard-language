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
 *     <h2 class="command-name">arrayElements</h2>
 *   </div>
 *
 *   <div class="command-signature">
 *     <code><span class="return-type">BOOL</span> arrayElements(<span class="params">arrayVarName: STRING, expectedLength: INTEGER</span>)</code>
 *   </div>
 *
 *   <div class="command-description">
 *     <p>This command checks if an array-like variable (represented as a comma-separated string) has a specific number of elements.</p>
 *     <p>It takes the <code>arrayVarName</code> and an <code>expectedLength</code> integer. It returns <code>TRUE</code> if the number of elements in the array matches the <code>expectedLength</code>, and <code>FALSE</code> otherwise.</p>
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
 *           <td>The name of the variable (comma-separated string) representing the array.</td>
 *         </tr>
 *         <tr>
 *           <td><code>expectedLength</code></td>
 *           <td><code>INTEGER</code></td>
 *           <td>Yes</td>
 *           <td>The integer value for the expected number of elements in the array.</td>
 *         </tr>
 *       </tbody>
 *     </table>
 *   </div>
 *
 *   <div class="command-section">
 *     <h3>Return Value</h3>
 *     <p><code>BOOL</code> - <code>TRUE</code> if the array contains exactly <code>expectedLength</code> elements, <code>FALSE</code> otherwise.</p>
 *   </div>
 *
 *   <div class="command-section">
 *     <h3>Examples</h3>
 *     <div class="example">
 *       <h4>Checking array size</h4>
 *       <pre><code>set("myOptions", "opt1,opt2,opt3");
 * arrayElements("myOptions", 3); // TRUE
 * arrayElements("myOptions", 2); // FALSE</code></pre>
 *       <p class="example-description">Checks if "myOptions" has 3 elements, then if it has 2.</p>
 *     </div>
 *     <div class="example">
 *       <h4>Conditional logic based on array length</h4>
 *       <pre><code>if(arrayElements(get("chosenProtocols"), 1), set("singleProtocolChosen", TRUE));</code></pre>
 *       <p class="example-description">Sets "singleProtocolChosen" to TRUE if exactly one protocol was chosen.</p>
 *     </div>
 *   </div>
 *
 *   <div class="command-section">
 *     <h3>Notes</h3>
 *     <ul>
 *       <li><code>arrayVarName</code> must be a <code>STRING</code>, and <code>expectedLength</code> must be an <code>INTEGER</code>.</li>
 *       <li>The variable specified by <code>arrayVarName</code> must exist and ideally hold a comma-separated string. If it doesn't exist, an <code>ExecutorException</code> will be thrown (as it cannot retrieve the variable).</li>
 *       <li>An empty string for an array variable will be treated as an array of 0 elements.</li>
 *     </ul>
 *   </div>
 * </div>
 */
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
        checkNumParams(aList, 2, 2);

        // evaluate parameters
        Atom arr = checkAtomType(aList.get(0), Arrays.asList(AtomType.STRING), true, coachContext, "arrayList");
        Atom noelem = checkAtomType(aList.get(1), Arrays.asList(AtomType.INTEGER), true, coachContext, "arrayLength");

        List<String> tempList = stringToList(
                coachContext.getContext().getVariable(arr.getId(), null).getId());

        return tempList.size() == Integer.valueOf(noelem.getId()) ? Atom.TRUE : Atom.FALSE;
    }
}
