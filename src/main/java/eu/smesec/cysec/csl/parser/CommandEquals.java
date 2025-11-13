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
 *     <h2 class="command-name">equals</h2>
 *   </div>
 *
 *   <div class="command-signature">
 *     <code><span class="return-type">BOOL</span> equals(<span class="params">atom1: ANY, atom2: ANY</span>)</code>
 *   </div>
 *
 *   <div class="command-description">
 *     <p>This command checks if two atoms are equivalent in both type and content.</p>
 *     <p>It returns <code>TRUE</code> if both atoms have the same type (e.g., both are STRING, both are INTEGER, etc.) and their values are identical. For <code>NULL</code> atoms, it returns <code>TRUE</code> if both are <code>NULL</code>.</p>
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
 *           <td><code>atom1</code></td>
 *           <td><code>ANY</code></td>
 *           <td>Yes</td>
 *           <td>The first atom to compare. Can be of type STRING, INTEGER, FLOAT, BOOL, or NULL.</td>
 *         </tr>
 *         <tr>
 *           <td><code>atom2</code></td>
 *           <td><code>ANY</code></td>
 *           <td>Yes</td>
 *           <td>The second atom to compare. Can be of type STRING, INTEGER, FLOAT, BOOL, or NULL.</td>
 *         </tr>
 *       </tbody>
 *     </table>
 *   </div>
 *
 *   <div class="command-section">
 *     <h3>Return Value</h3>
 *     <p><code>BOOL</code> - <code>TRUE</code> if both atoms are of the same type and have equal content, <code>FALSE</code> otherwise.</p>
 *   </div>
 *
 *   <div class="command-section">
 *     <h3>Examples</h3>
 *     <div class="example">
 *       <h4>Comparing identical strings</h4>
 *       <pre><code>equals("q20", "q20") // TRUE</code></pre>
 *       <p class="example-description">Returns TRUE because both are STRING atoms with the same value.</p>
 *     </div>
 *     <div class="example">
 *       <h4>Comparing different strings</h4>
 *       <pre><code>equals("q20", "q40") // FALSE</code></pre>
 *       <p class="example-description">Returns FALSE because the string values are different.</p>
 *     </div>
 *     <div class="example">
 *       <h4>Comparing different types</h4>
 *       <pre><code>equals("10", 10) // FALSE</code></pre>
 *       <p class="example-description">Returns FALSE because one is a STRING and the other is an INTEGER.</p>
 *     </div>
 *     <div class="example">
 *       <h4>Comparing NULL atoms</h4>
 *       <pre><code>equals(NULL, NULL) // TRUE</code></pre>
 *       <p class="example-description">Returns TRUE because both are NULL atoms.</p>
 *     </div>
 *   </div>
 *
 *   <div class="command-section">
 *     <h3>Notes</h3>
 *     <ul>
 *       <li>This command carefully distinguishes between types. `equals("1", 1)` will return `FALSE`.</li>
 *       <li>Method atoms (commands) are generally not suitable for direct comparison by this command and usually evaluate to `FALSE`.</li>
 *     </ul>
 *   </div>
 * </div>
 */
public class CommandEquals extends Command {

    public Atom execute(List<Atom> aList, CoachContext coachContext) throws ExecutorException {

        // Two parameters expected: Two atoms of any type meant to compare
        checkNumParams(aList, 2);

        // evaluate parameters
        Atom atom1 = checkAtomType(
                aList.get(0),
                Arrays.asList(Atom.AtomType.STRING, AtomType.INTEGER, AtomType.FLOAT, AtomType.BOOL, AtomType.NULL),
                true,
                coachContext,
                "leftValue");
        Atom atom2 = checkAtomType(
                aList.get(1),
                Arrays.asList(Atom.AtomType.STRING, AtomType.INTEGER, AtomType.FLOAT, AtomType.BOOL, AtomType.NULL),
                true,
                coachContext,
                "rightValue");

        // Check equivalence
        if (atom1.getType().equals(atom2.getType())) {
            if (AtomType.NULL.equals(atom1.getType())) {
                // Return TRUE if both atoms are of a NULL type
                return Atom.TRUE;
            } else if (atom1.getType() != AtomType.METHODE) {
                if (atom1.getId().equals(atom2.getId())) {
                    // Both atoms are of same type and value
                    return Atom.TRUE;
                } else {
                    // The atoms are of the same type but contain different values
                    return Atom.FALSE;
                }
            } else {
                return Atom.FALSE;
            }
        } else {
            // types are inequal
            return Atom.FALSE;
        }
    }
}
