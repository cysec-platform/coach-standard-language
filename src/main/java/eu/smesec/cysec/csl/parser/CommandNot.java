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
 *<div class="command-doc">
 *   <div class="command-header">
 *     <h2 class="command-name">not</h2>
 *   </div>
 *
 *   <div class="command-signature">
 *     <code><span class="return-type">BOOL</span> not(<span class="params">value: BOOL</span>)</code>
 *   </div>
 *
 *   <div class="command-description">
 *     <p>The not command can be used to invert a BOOL value. TRUE becomes FALSE and FALSE becomes TRUE.</p>
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
 *           <td><code>value</code></td>
 *           <td><code>BOOL</code></td>
 *           <td>Yes</td>
 *           <td>The value to invert</td>
 *         </tr>
 *       </tbody>
 *     </table>
 *   </div>
 *
 *   <div class="command-section">
 *     <h3>Return Value</h3>
 *     <p><code>BOOL</code> - The inversion of the passed value.</p>
 *   </div>
 *
 *   <div class="command-section">
 *     <h3>Examples</h3>
 *     <div class="example">
 *       <h4>Basic Usage</h4>
 *       <pre><code>not(TRUE) // FALSE</code></pre>
 *       <p class="example-description">The not command can be used to invert a BOOL value</p>
 *     </div>
 *     <div class="example">
 *       <h4>Advanced Usage</h4>
 *       <pre><code>and(
 *   not(someOtherCommand()),
 *   not(FALSE)
 * )</code></pre>
 *       <p class="example-description">The return value of the not command can be used as a BOOL expression.</p>
 *     </div>
 *   </div>
 *
 *   <div class="command-section">
 *     <h3>Notes</h3>
 *     <ul>
 *       <li>The command will fail if a non-BOOL value is passed</li>
 *     </ul>
 *   </div>
 * </div>
 */
public class CommandNot extends CommandAbstractBoolOp {

    @Override
    boolean evaluate(List<Boolean> list, ExecutorContext context) throws ExecutorException {
        if (list.size() != 1) {
            throw new ExecutorException("NOT supports only one parameter");
        }
        return !list.get(0);
    }
}
