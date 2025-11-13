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
 *     <h2 class="command-name">arrayLength</h2>
 *   </div>
 *
 *   <div class="command-signature">
 *     <code><span class="return-type">INTEGER</span> arrayLength(<span class="params">arrayVarName: STRING</span>)</code>
 *   </div>
 *
 *   <div class="command-description">
 *     <p>This command returns the number of elements in an array-like variable, which is represented as a comma-separated string.</p>
 *     <p>It takes the <code>arrayVarName</code> and returns an <code>INTEGER</code> representing the count of elements found by splitting the string by a comma delimiter.</p>
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
 *           <td>The name of the variable (comma-separated string) whose length is to be determined.</td>
 *         </tr>
 *       </tbody>
 *     </table>
 *   </div>
 *
 *   <div class="command-section">
 *     <h3>Return Value</h3>
 *     <p><code>INTEGER</code> - The number of elements in the array. Returns 0 for an empty string or if the variable is not found.</p>
 *   </div>
 *
 *   <div class="command-section">
 *     <h3>Examples</h3>
 *     <div class="example">
 *       <h4>Getting array length</h4>
 *       <pre><code>set("myItems", "apple,banana,orange");
 * arrayLength("myItems"); // 3</code></pre>
 *       <p class="example-description">Returns 3, as there are three elements in "myItems".</p>
 *     </div>
 *     <div class="example">
 *       <h4>Getting length of an empty array</h4>
 *       <pre><code>set("emptyList", "");
 * arrayLength("emptyList"); // 0</code></pre>
 *       <p class="example-description">Returns 0 for an empty string. Note that "a," will return 2 (as in "a", "").</p>
 *     </div>
 *     <div class="example">
 *       <h4>Using the length in an 'if' condition</h4>
 *       <pre><code>if(equals(arrayLength(get("userSelections")), 0), set("noSelectionsMade", TRUE));</code></pre>
 *       <p class="example-description">Sets "noSelectionsMade" to TRUE if the "userSelections" array is empty.</p>
 *     </div>
 *   </div>
 *
 *   <div class="command-section">
 *     <h3>Notes</h3>
 *     <ul>
 *       <li><code>arrayVarName</code> must be a <code>STRING</code>.</li>
 *       <li>The variable specified by <code>arrayVarName</code> must exist and ideally hold a comma-separated string. If it doesn't exist, an <code>ExecutorException</code> will be thrown (as it cannot retrieve the variable's value).</li>
 *       <li>Be aware of how trailing commas or multiple consecutive commas might affect the reported length when manually constructing array strings.</li>
 *     </ul>
 *   </div>
 * </div>
 */
public class CommandArrayLength extends CommandAbstractList {

    @Override
    /**
     * Returns the size of the specified array as Integer.
     *
     * <p>This command has one mandatory parameter:
     *   <ul>
     *     <li>(arrayList; String)The array to append to.</li>
     *   </ul>
     * </p>
     * @returns Always true
     */
    public Atom execute(List<Atom> aList, CoachContext coachContext) throws ExecutorException {
        // expects 2 parameter
        checkNumParams(aList, 1, 1);

        // evaluate parameters
        Atom arr = checkAtomType(aList.get(0), Arrays.asList(AtomType.STRING), true, coachContext, "arrayList");

        List<String> tempList = stringToList(
                coachContext.getContext().getVariable(arr.getId(), null).getId());

        return new Atom(AtomType.INTEGER, "" + tempList.size(), null);
    }
}
