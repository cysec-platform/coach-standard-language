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
 *     <h2 class="command-name">or</h2>
 *   </div>
 *
 *   <div class="command-signature">
 *     <code><span class="return-type">BOOL</span> or(<span class="params">value1: BOOL, value2: BOOL, ...</span>)</code>
 *   </div>
 *
 *   <div class="command-description">
 *     <p>Performs a logical OR operation on an arbitrary number of boolean values.</p>
 *     <p>This command returns <code>TRUE</code> if at least one of the provided boolean values is true; otherwise, it returns <code>FALSE</code>.</p>
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
 *     <p><code>BOOL</code> - <code>TRUE</code> if any parameter evaluates to true, <code>FALSE</code> otherwise.</p>
 *   </div>
 *
 *   <div class="command-section">
 *     <h3>Examples</h3>
 *     <div class="example">
 *       <h4>Basic Usage</h4>
 *       <pre><code>or(TRUE, TRUE, FALSE) // TRUE</code></pre>
 *       <p class="example-description">Evaluates to TRUE because at least one of the inputs is TRUE.</p>
 *     </div>
 *     <div class="example">
 *       <h4>Using with other commands</h4>
 *       <pre><code>or(
 *   isSelected("q10o1"),
 *   greaterThan(get("scoreA"), 50)
 * )</code></pre>
 *       <p class="example-description">Returns TRUE if "q10o1" is selected OR "scoreA" is greater than 50 (or both).</p>
 *     </div>
 *   </div>
 *
 *   <div class="command-section">
 *     <h3>Notes</h3>
 *     <ul>
 *       <li>If no parameters are provided, the behavior is undefined and may result in an error.</li>
 *       <li>Parameters are evaluated sequentially, and if any `TRUE` is encountered, evaluation may short-circuit (not all parameters might be evaluated if the result is already determined).</li>
 *     </ul>
 *   </div>
 * </div>
 */
public class CommandOr extends CommandAbstractBoolOp {

    @Override
    boolean evaluate(List<Boolean> list, ExecutorContext context) {
        for (boolean b : list) {
            if (b) {
                return true;
            }
        }
        return false;
    }
}
