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
 *     <h2 class="command-name">contains</h2>
 *   </div>
 *
 *   <div class="command-signature">
 *     <code><span class="return-type">BOOL</span> contains(<span class="params">haystack: STRING, needle: STRING</span>)</code>
 *   </div>
 *
 *   <div class="command-description">
 *     <p>This command checks if a given "haystack" string contains a specified "needle" substring.</p>
 *     <p>It takes two string parameters and returns <code>TRUE</code> if the <code>haystack</code> string contains the <code>needle</code> string as a subsequence; otherwise, it returns <code>FALSE</code>.</p>
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
 *           <td><code>haystack</code></td>
 *           <td><code>STRING</code></td>
 *           <td>Yes</td>
 *           <td>The string within which to search.</td>
 *         </tr>
 *         <tr>
 *           <td><code>needle</code></td>
 *           <td><code>STRING</code></td>
 *           <td>Yes</td>
 *           <td>The substring to search for.</td>
 *         </tr>
 *       </tbody>
 *     </table>
 *   </div>
 *
 *   <div class="command-section">
 *     <h3>Return Value</h3>
 *     <p><code>BOOL</code> - <code>TRUE</code> if <code>haystack</code> contains <code>needle</code>, <code>FALSE</code> otherwise.</p>
 *   </div>
 *
 *   <div class="command-section">
 *     <h3>Examples</h3>
 *     <div class="example">
 *       <h4>Basic string containment</h4>
 *       <pre><code>contains("hello world", "world") // TRUE</code></pre>
 *       <p class="example-description">Returns TRUE because "hello world" contains "world".</p>
 *     </div>
 *     <div class="example">
 *       <h4>Non-containment</h4>
 *       <pre><code>contains("hello world", "foo") // FALSE</code></pre>
 *       <p class="example-description">Returns FALSE because "hello world" does not contain "foo".</p>
 *     </div>
 *     <div class="example">
 *       <h4>Using with variables</h4>
 *       <pre><code>contains(get("fullText"), get("searchKeyword"))</code></pre>
 *       <p class="example-description">Checks if the text in "fullText" contains the keyword from "searchKeyword".</p>
 *     </div>
 *   </div>
 *
 *   <div class="command-section">
 *     <h3>Notes</h3>
 *     <ul>
 *       <li>Both parameters must evaluate to a <code>STRING</code> type. Passing non-string types will result in an <code>ExecutorException</code>.</li>
 *       <li>The comparison is case-sensitive.</li>
 *     </ul>
 *   </div>
 * </div>
 */
public class CommandContains extends Command {

    public Atom execute(List<Atom> aList, CoachContext coachContext) throws ExecutorException {

        // expects 2 or 3 parameters
        checkNumParams(aList, 2, 2);

        // evaluate parameters
        Atom varHaystack = checkAtomType(aList.get(0), Arrays.asList(AtomType.STRING), true, coachContext, "content");
        Atom varNeedle = checkAtomType(aList.get(1), Arrays.asList(AtomType.STRING), true, coachContext, "content");

        return (varHaystack.getId().contains(varNeedle.getId()) ? Atom.TRUE : Atom.FALSE);
    }
}
