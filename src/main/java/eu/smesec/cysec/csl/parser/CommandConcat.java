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
 *     <h2 class="command-name">concat</h2>
 *   </div>
 *
 *   <div class="command-signature">
 *     <code><span class="return-type">STRING</span> concat(<span class="params">value1: ANY, [value2: ANY, ...]</span>)</code>
 *   </div>
 *
 *   <div class="command-description">
 *     <p>This command concatenates one or more values into a single string.</p>
 *     <p>It takes an arbitrary number of parameters of any type, converts each to its string representation, and joins them together. Method calls within the parameters are executed first.</p>
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
 *           <td><code>ANY</code></td>
 *           <td>Yes (at least one)</td>
 *           <td>One or more values (STRING, INTEGER, FLOAT, BOOL, or results of other commands) to concatenate.</td>
 *         </tr>
 *       </tbody>
 *     </table>
 *   </div>
 *
 *   <div class="command-section">
 *     <h3>Return Value</h3>
 *     <p><code>STRING</code> - A single string formed by joining the string representations of all input values.</p>
 *   </div>
 *
 *   <div class="command-section">
 *     <h3>Examples</h3>
 *     <div class="example">
 *       <h4>Concatenating strings</h4>
 *       <pre><code>concat("hello", " ", "world") // "hello world"</code></pre>
 *       <p class="example-description">Joins three string literals.</p>
 *     </div>
 *     <div class="example">
 *       <h4>Concatenating with variables and numbers</h4>
 *       <pre><code>concat("Score: ", get("currentScore"), " out of ", 100)</code></pre>
 *       <p class="example-description">Concatenates a string literal with the value of a variable and an integer, and another string literal.</p>
 *     </div>
 *     <div class="example">
 *       <h4>Using with other commands to build dynamic strings</h4>
 *       <pre><code>set("questionID", concat("q", get("questionNumber"), "o", get("optionValue")));</code></pre>
 *       <p class="example-description">Constructs a question ID string dynamically based on variable values.</p>
 *     </div>
 *   </div>
 *
 *   <div class="command-section">
 *     <h3>Notes</h3>
 *     <ul>
 *       <li>All parameters are converted to their string representation before concatenation. For <code>STRING</code> atoms, the raw ID is used (without quotes). For other types, their standard string representation (e.g., "TRUE", "123", "3.14") is used.</li>
 *       <li>If any parameter is a method (command call), it will be executed first to resolve its value, which is then converted to a string.</li>
 *     </ul>
 *   </div>
 * </div>
 */
public class CommandConcat extends Command {

    @Override
    public Atom execute(List<Atom> aList, CoachContext coachContext) throws ExecutorException {
        checkNumParams(aList, 1, Integer.MAX_VALUE);

        StringBuilder sb = new StringBuilder();
        for (Atom a : aList) {
            if (a.getType() == Atom.AtomType.METHODE) {
                a = a.execute(coachContext);
            }
            sb.append(a.getType() == Atom.AtomType.STRING ? a.getId() : a.toString());
        }
        return new Atom(Atom.AtomType.STRING, sb.toString(), null);
    }
}
