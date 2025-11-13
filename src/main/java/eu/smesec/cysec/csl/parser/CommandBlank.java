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
 *     <h2 class="command-name">null</h2>
 *   </div>
 *
 *   <div class="command-signature">
 *     <code><span class="return-type">STRING</span> null()</code>
 *   </div>
 *
 *   <div class="command-description">
 *     <p>This command returns an empty string. It can be useful as a placeholder or to explicitly represent an empty value where a string is expected.</p>
 *   </div>
 *
 *   <div class="command-section">
 *     <h3>Parameters</h3>
 *     <em>No Parameters</em>
 *   </div>
 *
 *   <div class="command-section">
 *     <h3>Return Value</h3>
 *     <p><code>STRING</code> - An empty string ("").</p>
 *   </div>
 *
 *   <div class="command-section">
 *     <h3>Examples</h3>
 *     <div class="example">
 *       <h4>Basic Usage</h4>
 *       <pre><code>set("myVariable", null()) // Sets "myVariable" to ""</code></pre>
 *       <p class="example-description">Sets a variable to an empty string.</p>
 *     </div>
 *   </div>
 *
 *   <div class="command-section">
 *     <h3>Notes</h3>
 *     <ul>
 *       <li>This command always returns an empty string, not the <code>NULL</code> Atom type unless explicitly handled by the context where it's used.</li>
 *     </ul>
 *   </div>
 * </div>
 */
public class CommandBlank extends Command {

    public Atom execute(List<Atom> a, CoachContext coachContext) {
        return new Atom(Atom.AtomType.STRING, "", null);
    }
}
