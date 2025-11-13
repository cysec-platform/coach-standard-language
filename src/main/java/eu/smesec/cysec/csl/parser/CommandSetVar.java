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
 *     <h2 class="command-name">set</h2>
 *   </div>
 *
 *   <div class="command-signature">
 *     <code><span class="return-type">NULL</span> set(<span class="params">varName: STRING, value: ANY</span>)</code><br>
 *     <code><span class="return-type">NULL</span> set(<span class="params">varName: STRING, context: STRING|NULL, value: ANY</span>)</code>
 *   </div>
 *
 *   <div class="command-description">
 *     <p>This command sets the value of a variable within the coach's execution context.</p>
 *     <p>It can be used with two or three parameters. If two parameters are provided, the variable is set in the context of the current question. If three parameters are provided, the second parameter specifies an explicit context ID (a string) in which the variable should be set, or <code>NULL</code> for the global context.</p>
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
 *           <td>The name of the variable to set. Must be a string.</td>
 *         </tr>
 *         <tr>
 *           <td><code>context</code></td>
 *           <td><code>STRING</code> or <code>NULL</code></td>
 *           <td>No (defaults to current question's context if 2 parameters used)</td>
 *           <td>The ID of the context in which to set the variable. Use <code>NULL</code> for the global context.</td>
 *         </tr>
 *         <tr>
 *           <td><code>value</code></td>
 *           <td><code>ANY</code></td>
 *           <td>Yes</td>
 *           <td>The value to assign to the variable. Can be a STRING, INTEGER, FLOAT, BOOL, or the result of another command.</td>
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
 *       <h4>Setting a variable in the current question's context (2 params)</h4>
 *       <pre><code>set("myScore", 100);</code></pre>
 *       <p class="example-description">Sets the variable "myScore" to 100 in the current question's context.</p>
 *     </div>
 *     <div class="example">
 *       <h4>Setting a variable in the global context (3 params with NULL)</h4>
 *       <pre><code>set("globalFlag", NULL, TRUE);</code></pre>
 *       <p class="example-description">Sets the variable "globalFlag" to TRUE in the global context.</p>
 *     </div>
 *     <div class="example">
 *       <h4>Setting a variable in a specific context (3 params with STRING)</h4>
 *       <pre><code>set("protocolType", "connectionQ", "HTTPS");</code></pre>
 *       <p class="example-description">Sets the variable "protocolType" to "HTTPS" in the context identified by "connectionQ".</p>
 *     </div>
 *     <div class="example">
 *       <h4>Setting a variable with result of another command</h4>
 *       <pre><code>set("computedValue", add(get("valA"), get("valB")));</code></pre>
 *       <p class="example-description">Sets "computedValue" to the sum of "valA" and "valB".</p>
 *     </div>
 *   </div>
 *
 *   <div class="command-section">
 *     <h3>Notes</h3>
 *     <ul>
 *       <li>The <code>varName</code> must be a string. Other types for <code>varName</code> will result in an <code>ExecutorException</code>.</li>
 *       <li>The <code>context</code> parameter, if provided, must be a string or the <code>NULL</code> Atom.</li>
 *       <li>Variables set within a question's context are typically cleared before each re-evaluation, unless specifically designed to persist. Global variables (set with <code>NULL</code> context) usually persist for the duration of the coach session.</li>
 *     </ul>
 *   </div>
 * </div>
 */
public class CommandSetVar extends Command {

    public Atom execute(List<Atom> aList, CoachContext coachContext) throws ExecutorException {

        // expects 2 or 3 parameters
        checkNumParams(aList, 2, 3);

        // evaluate parameters
        Atom varName = aList.get(0).execute(coachContext);
        Atom varContext = aList.get(1).execute(coachContext);
        Atom varContent = null;
        if (aList.size() == 2) {
            varContext = new Atom(
                    Atom.AtomType.STRING, coachContext.getQuestionContext().getId(), null);
            varContent = aList.get(1).execute(coachContext);
        } else {
            varContent = aList.get(2).execute(coachContext);
        }

        // assert type of parameters
        if (varName.getType() != Atom.AtomType.STRING
                || (varContext.getType() != Atom.AtomType.STRING && varContext != Atom.NULL_ATOM)) {
            throw new ExecutorException(
                    "Invalid types for parameters: Provide [0] String, [1] String and [2] ANY or [0] String and [2] ANY");
        }

        // set the score
        coachContext
                .getContext()
                .setVariable(varName.getId(), varContent, varContext == Atom.NULL_ATOM ? null : varContext.getId());
        coachContext
                .getLogger()
                .fine(String.format(
                        "Set variable %s to %s in context %s",
                        varName.getId(), varContent.getId(), varContent.getId()));

        return Atom.NULL_ATOM;
    }
}
