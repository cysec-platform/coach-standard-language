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

import java.math.BigDecimal;

/**
 * <div class="command-doc">
 *   <div class="command-header">
 *     <h2 class="command-name">greaterThanOrEq</h2>
 *   </div>
 *
 *   <div class="command-signature">
 *     <code><span class="return-type">BOOL</span> greaterThanOrEq(<span class="params">number1: INTEGER|FLOAT, number2: INTEGER|FLOAT</span>)</code>
 *   </div>
 *
 *   <div class="command-description">
 *     <p>This command checks if the first number is greater than or equal to the second number.</p>
 *     <p>It takes two numeric parameters (either INTEGER or FLOAT) and returns <code>TRUE</code> if the first number is numerically larger than or equal to the second; otherwise, it returns <code>FALSE</code>.</p>
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
 *           <td><code>number1</code></td>
 *           <td><code>INTEGER</code> or <code>FLOAT</code></td>
 *           <td>Yes</td>
 *           <td>The first number (left-hand side) for the comparison.</td>
 *         </tr>
 *         <tr>
 *           <td><code>number2</code></td>
 *           <td><code>INTEGER</code> or <code>FLOAT</code></td>
 *           <td>Yes</td>
 *           <td>The second number (right-hand side) for the comparison.</td>
 *         </tr>
 *       </tbody>
 *     </table>
 *   </div>
 *
 *   <div class="command-section">
 *     <h3>Return Value</h3>
 *     <p><code>BOOL</code> - <code>TRUE</code> if <code>number1 >= number2</code>, <code>FALSE</code> otherwise.</p>
 *   </div>
 *
 *   <div class="command-section">
 *     <h3>Examples</h3>
 *     <div class="example">
 *       <h4>Comparing integers (greater)</h4>
 *       <pre><code>greaterThanOrEq(10, 5) // TRUE</code></pre>
 *       <p class="example-description">Returns TRUE as 10 is greater than 5.</p>
 *     </div>
 *     <div class="example">
 *       <h4>Comparing integers (equal)</h4>
 *       <pre><code>greaterThanOrEq(10, 10) // TRUE</code></pre>
 *       <p class="example-description">Returns TRUE as 10 is equal to 10.</p>
 *     </div>
 *     <div class="example">
 *       <h4>Comparing floats</h4>
 *       <pre><code>greaterThanOrEq(3.141, 3.14) // TRUE</code></pre>
 *       <p class="example-description">Returns TRUE as 3.141 is greater than 3.14.</p>
 *     </div>
 *   </div>
 *
 *   <div class="command-section">
 *     <h3>Notes</h3>
 *     <ul>
 *       <li>Both parameters must evaluate to a numeric type (INTEGER or FLOAT). Passing non-numeric types will result in an <code>ExecutorException</code>.</li>
 *       <li>Floating-point comparisons are handled consistently but might be subject to standard precision limitations.</li>
 *     </ul>
 *   </div>
 * </div>
 */
public class CommandGreaterThanOrEquals extends CommandNumberBinaryPredicate {
    @Override
    public boolean test(BigDecimal lhs, BigDecimal rhs) {
        return lhs.compareTo(rhs) >= 0;
    }
}
