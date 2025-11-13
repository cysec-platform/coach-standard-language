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

import static eu.smesec.cysec.csl.parser.Atom.NULL_ATOM;

import eu.smesec.cysec.csl.parser.Atom.AtomType;
import java.util.Arrays;
import java.util.List;

/**
 * <div class="command-doc">
 *   <div class="command-header">
 *     <h2 class="command-name">get</h2>
 *   </div>
 *
 *   <div class="command-signature">
 *     <code><span class="return-type">ANY</span> get(<span class="params">varName: STRING</span>)</code><br>
 *     <code><span class="return-type">ANY</span> get(<span class="params">varName: STRING, defaultValue: ANY</span>)</code><br>
 *     <code><span class="return-type">ANY</span> get(<span class="params">varName: STRING, defaultValue: ANY, context: STRING|NULL</span>)</code>
 *   </div>
 *
 *   <div class="command-description">
 *     <p>This command retrieves the value of a variable from the coach's execution context. It can also provide a default value if the variable is not found.</p>
 *     <p>It supports one, two, or three parameters:</p>
 *     <ul>
 *       <li><b>1 parameter:</b> Retrieves the variable <code>varName</code> from the current question's context. Returns <code>NULL</code> if not found.</li>
 *       <li><b>2 parameters:</b> Retrieves <code>varName</code> from the current question's context. Returns <code>defaultValue</code> if not found.</li>
 *       <li><b>3 parameters:</b> Retrieves <code>varName</code> from the specified <code>context</code> (or global if <code>NULL</code> is provided for context). Returns <code>defaultValue</code> if not found.</li>
 *     </ul>
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
 *           <td>The name of the variable to retrieve. Must be a string.</td>
 *         </tr>
 *         <tr>
 *           <td><code>defaultValue</code></td>
 *           <td><code>ANY</code></td>
 *           <td>No</td>
 *           <td>A value to return if the variable is not found. Can be any atom type.</td>
 *         </tr>
 *         <tr>
 *           <td><code>context</code></td>
 *           <td><code>STRING</code> or <code>NULL</code></td>
 *           <td>No</td>
 *           <td>The ID of the context to search in. Use <code>NULL</code> for the global context.</td>
 *         </tr>
 *       </tbody>
 *     </table>
 *   </div>
 *
 *   <div class="command-section">
 *     <h3>Return Value</h3>
 *     <p><code>ANY</code> - The value of the variable, the <code>defaultValue</code> if specified and the variable is not found, or a <code>NULL</code> Atom if the variable is not found and no default is specified.</p>
 *   </div>
 *
 *   <div class="command-section">
 *     <h3>Examples</h3>
 *     <div class="example">
 *       <h4>Getting a variable without default</h4>
 *       <pre><code>get("myScore")</code></pre>
 *       <p class="example-description">Retrieves "myScore" from the current question context. Returns NULL if not found.</p>
 *     </div>
 *     <div class="example">
 *       <h4>Getting a variable with default value</h4>
 *       <pre><code>get("userName", "Guest")</code></pre>
 *       <p class="example-description">Retrieves "userName". If not found, returns "Guest".</p>
 *     </div>
 *     <div class="example">
 *       <h4>Getting a variable from a specific context with default</h4>
 *       <pre><code>get("protocol", "HTTPS", NULL)</code></pre>
 *       <p class="example-description">Retrieves "protocol" from the global context. If not found, returns "HTTPS".</p>
 *     </div>
 *   </div>
 *
 *   <div class="command-section">
 *     <h3>Notes</h3>
 *     <ul>
 *       <li>The <code>varName</code> must be a string.</li>
 *       <li>The <code>context</code> parameter, if provided, must be a string or the <code>NULL</code> Atom.</li>
 *       <li>If a variable is not found and no <code>defaultValue</code> is provided, the command returns a <code>NULL</code> Atom.</li>
 *     </ul>
 *   </div>
 * </div>
 */
public class CommandGetVar extends Command {

    public Atom execute(List<Atom> aList, CoachContext coachContext) throws ExecutorException {

        // expects 1 parameter
        checkNumParams(aList, 1, 3);

        // evaluate parameters
        Atom varName = checkAtomType(aList.get(0), Arrays.asList(Atom.AtomType.STRING), true, coachContext, "varName");
        Atom varDefault = null;
        if (aList.size() > 1) {
            varDefault = checkAtomType(
                    aList.get(1),
                    Arrays.asList(AtomType.STRING, AtomType.INTEGER, AtomType.BOOL, AtomType.FLOAT),
                    true,
                    coachContext,
                    "varDefault");
        }
        Atom varContext = NULL_ATOM;
        if (aList.size() > 2) {
            varContext = checkAtomType(
                    aList.get(2), Arrays.asList(AtomType.STRING, AtomType.NULL), true, coachContext, "varContext");
        }

        // set the score
        Atom ret = coachContext
                .getContext()
                .getVariable(varName.getId(), varContext == NULL_ATOM ? null : varContext.getId());

        if (ret == NULL_ATOM || ret == null) {
            ret = varDefault;
        }

        if (ret == null) {
            ret = NULL_ATOM;
        }

        return ret;
    }
}
