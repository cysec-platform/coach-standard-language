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
 *     <h2 class="command-name">print</h2>
 *   </div>
 *
 *   <div class="command-signature">
 *     <code><span class="return-type">NULL</span> print(<span class="params">[message: ANY]</span>)</code>
 *   </div>
 *
 *   <div class="command-description">
 *     <p>This command prints a message to the CSL logger.</p>
 *     <p>It can take one optional parameter. If a parameter is provided, its string representation will be logged. If no parameter is provided, an empty line will be logged.</p>
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
 *           <td><code>message</code></td>
 *           <td><code>ANY</code></td>
 *           <td>No</td>
 *           <td>The value to print. It can be a String, Integer, Float, Bool, or the result of another command. It will be converted to its string representation.</td>
 *         </tr>
 *       </tbody>
 *     </table>
 *   </div>
 *
 *   <div class="command-section">
 *     <h3>Return Value</h3>
 *     <p><code>NULL</code> - The return value is not meaningful.</p>
 *   </div>
 *
 *   <div class="command-section">
 *     <h3>Examples</h3>
 *     <div class="example">
 *       <h4>Printing a simple string</h4>
 *       <pre><code>print("Hello CSL!");</code></pre>
 *       <p class="example-description">Logs "Hello CSL!" to the console.</p>
 *     </div>
 *     <div class="example">
 *       <h4>Printing the value of a variable</h4>
 *       <pre><code>print(get("currentScore"));</code></pre>
 *       <p class="example-description">Logs the current value of the "currentScore" variable.</p>
 *     </div>
 *     <div class="example">
 *       <h4>Printing an empty line</h4>
 *       <pre><code>print();</code></pre>
 *       <p class="example-description">Logs an empty line.</p>
 *     </div>
 *   </div>
 *
 *   <div class="command-section">
 *     <h3>Notes</h3>
 *     <ul>
 *       <li>The output of this command is typically directed to the server-side logs (e.g., Java console or log files), not to the user interface.</li>
 *       <li>This command is useful for debugging CSL logic during development.</li>
 *     </ul>
 *   </div>
 * </div>
 */
public class CommandPrint extends Command {

    @Override
    public Atom execute(List<Atom> list, CoachContext coachContext) throws ExecutorException {

        if (list.size() == 0) {
            coachContext.getLogger().info("");

        } else {
            coachContext.getLogger().info(list.get(0).getId());
        }

        return new Atom(Atom.AtomType.NULL, null, null);
    }
}
