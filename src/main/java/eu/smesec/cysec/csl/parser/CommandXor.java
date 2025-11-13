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

import java.util.List;

/**
 * <div class="command-doc">
 *   <div class="command-header">
 *     <h2 class="command-name">xor</h2>
 *   </div>
 *
 *   <div class="command-signature">
 *     <code><span class="return-type">BOOL</span> xor(<span class="params">value1: BOOL, value2: BOOL, ...</span>)</code>
 *   </div>
 *
 *   <div class="command-description">
 *     <p>Performs a logical exclusive OR (XOR) operation on an arbitrary number of boolean values.</p>
 *     <p>This command returns <code>TRUE</code> if exactly one of the provided boolean values is true; otherwise, it returns <code>FALSE</code>.</p>
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
 *           <td><code>value1, value2, ...</code></td>
 *           <td><code>BOOL</code></td>
 *           <td>Yes (at least one)</td>
 *           <td>One or more boolean expressions to evaluate.</td>
 *         </tr>
 *       </tbody>
 *     </table>
 *   </div>
 *
 *   <div class="command-section">
 *     <h3>Return Value</h3>
 *     <p><code>BOOL</code> - <code>TRUE</code> if exactly one parameter evaluates to true, <code>FALSE</code> otherwise.</p>
 *   </div>
 *
 *   <div class="command-section">
 *     <h3>Examples</h3>
 *     <div class="example">
 *       <h4>Basic Usage</h4>
 *       <pre><code>xor(TRUE, FALSE) // TRUE</code></pre>
 *       <p class="example-description">Evaluates to TRUE because exactly one input is TRUE.</p>
 *     </div>
 *     <div class="example">
 *       <h4>Multiple parameters</h4>
 *       <pre><code>xor(TRUE, TRUE, FALSE) // FALSE</code></pre>
 *       <p class="example-description">Evaluates to FALSE because two inputs are TRUE (not exactly one).</p>
 *     </div>
 *     <div class="example">
 *       <h4>Using with other commands</h4>
 *       <pre><code>xor(isSelected("q10o1"), isSelected("q10o2"))</code></pre>
 *       <p class="example-description">Returns TRUE if either "q10o1" is selected OR "q10o2" is selected, but not both.</p>
 *     </div>
 *   </div>
 *
 *   <div class="command-section">
 *     <h3>Notes</h3>
 *     <ul>
 *       <li>If no parameters are provided, the behavior is undefined and may result in an error.</li>
 *     </ul>
 *   </div>
 * </div>
 */
public class CommandXor extends CommandAbstractBoolOp {

    @Override
    boolean evaluate(List<Boolean> list, ExecutorContext context) {
        int ret = 0;
        for (boolean b : list) {
            if (b) {
                ret++;
            }
        }
        return ret == 1;
    }
}
