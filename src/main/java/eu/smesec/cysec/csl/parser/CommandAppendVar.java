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
 *     <h2 class="command-name">append</h2>
 *   </div>
 *
 *   <div class="command-signature">
 *     <code><span class="return-type">NULL</span> append(<span class="params">varName: STRING, content: STRING</span>)</code>
 *   </div>
 *
 *   <div class="command-description">
 *     <p>This command appends a string <code>content</code> to the existing string value of a variable named <code>varName</code>.</p>
 *     <p>The variable is assumed to be in the current question's context. If the variable does not exist, an error will occur.</p>
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
 *           <td><code>varName</code></td>
 *           <td><code>STRING</code></td>
 *           <td>Yes</td>
 *           <td>The name of the variable to append to. Must be a string.</td>
 *         </tr>
 *         <tr>
 *           <td><code>content</code></td>
 *           <td><code>STRING</code></td>
 *           <td>Yes</td>
 *           <td>The string content to append to the variable's current value.</td>
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
 *       <h4>Appending to an existing string variable</h4>
 *       <pre><code>set("logMessage", "Initial message: ");
 * append("logMessage", "Step 1 complete."); // "logMessage" now holds "Initial message: Step 1 complete."</code></pre>
 *       <p class="example-description">Demonstrates appending a string to a variable previously set in the same context.</p>
 *     </div>
 *     <div class="example">
 *       <h4>Chaining appends</h4>
 *       <pre><code>set("path", "/data");
 * append("path", "/users");
 * append("path", "/docs"); // "path" now holds "/data/users/docs"</code></pre>
 *       <p class="example-description">Successive calls to append to build a file path.</p>
 *     </div>
 *   </div>
 *
 *   <div class="command-section">
 *     <h3>Notes</h3>
 *     <ul>
 *       <li>Both <code>varName</code> and <code>content</code> must evaluate to <code>STRING</code> types. An <code>ExecutorException</code> will be thrown for other types.</li>
 *       <li>This command modifies the variable in the current question's context. If the variable was unset or held a non-string value, it might lead to unexpected behavior or an error.</li>
 *     </ul>
 *   </div>
 * </div>
 */
public class CommandAppendVar extends Command {

    public Atom execute(List<Atom> aList, CoachContext coachContext) throws ExecutorException {

        // expects 2 or 3 parameters
        checkNumParams(aList, 2, 2);

        // evaluate parameters
        Atom varName = aList.get(0).execute(coachContext);
        Atom varContent =
                checkAtomType(aList.get(1), Arrays.asList(Atom.AtomType.STRING), true, coachContext, "content");

        // set the variable
        coachContext
                .getContext()
                .setVariable(
                        varName.getId(),
                        new Atom(
                                AtomType.STRING,
                                coachContext
                                                .getContext()
                                                .getVariable(varName.getId(), null)
                                                .getId()
                                        + varContent.getId(),
                                null),
                        null);
        coachContext
                .getLogger()
                .fine(String.format(
                        "Set variable %s to %s in context %s",
                        varName.getId(), varContent.getId(), varContent.getId()));

        return Atom.NULL_ATOM;
    }
}
