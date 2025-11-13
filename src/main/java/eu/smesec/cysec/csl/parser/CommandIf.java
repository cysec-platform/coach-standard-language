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
 *     <h2 class="command-name">if</h2>
 *   </div>
 *
 *   <div class="command-signature">
 *     <code><span class="return-type">ANY</span> if(<span class="params">condition: BOOL, trueBody: ANY, [falseBody: ANY]</span>)</code>
 *   </div>
 *
 *   <div class="command-description">
 *     <p>This command executes either the <code>trueBody</code> or <code>falseBody</code> based on the evaluation of a boolean <code>condition</code>.</p>
 *     <p>If the <code>condition</code> evaluates to <code>TRUE</code>, the <code>trueBody</code> is executed and its result is returned. If the <code>condition</code> evaluates to <code>FALSE</code>, and an optional <code>falseBody</code> is provided, the <code>falseBody</code> is executed and its result is returned. If no <code>falseBody</code> is provided and the condition is <code>FALSE</code>, it returns a <code>NULL</code> Atom (which often behaves as <code>FALSE</code> in boolean contexts).</p>
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
 *           <td><code>condition</code></td>
 *           <td><code>BOOL</code></td>
 *           <td>Yes</td>
 *           <td>A boolean expression that determines which body to execute.</td>
 *         </tr>
 *         <tr>
 *           <td><code>trueBody</code></td>
 *           <td><code>ANY</code></td>
 *           <td>Yes</td>
 *           <td>The expression or command to execute if the <code>condition</code> is <code>TRUE</code>.</td>
 *         </tr>
 *         <tr>
 *           <td><code>falseBody</code></td>
 *           <td><code>ANY</code></td>
 *           <td>No</td>
 *           <td>The expression or command to execute if the <code>condition</code> is <code>FALSE</code>. If omitted, and the condition is FALSE, a NULL Atom is returned.</td>
 *         </tr>
 *       </tbody>
 *     </table>
 *   </div>
 *
 *   <div class="command-section">
 *     <h3>Return Value</h3>
 *     <p><code>ANY</code> - The result of executing either the <code>trueBody</code> or <code>falseBody</code>. Returns a <code>NULL</code> Atom if <code>falseBody</code> is omitted and the condition is <code>FALSE</code>.</p>
 *   </div>
 *
 *   <div class="command-section">
 *     <h3>Examples</h3>
 *     <div class="example">
 *       <h4>Conditional variable setting</h4>
 *       <pre><code>if(isSelected("q10o1"), set("myVar", "Option1Selected"), set("myVar", "Option1NotSelected"))</code></pre>
 *       <p class="example-description">Sets "myVar" to "Option1Selected" if "q10o1" is selected, otherwise to "Option1NotSelected".</p>
 *     </div>
 *     <div class="example">
 *       <h4>Conditional scoring</h4>
 *       <pre><code>if(greaterThan(get("currentScore"), 100), addScore("bonus", 10))</code></pre>
 *       <p class="example-description">Adds 10 to the "bonus" score only if "currentScore" is greater than 100.</p>
 *     </div>
 *   </div>
 *
 *   <div class="command-section">
 *     <h3>Notes</h3>
 *     <ul>
 *       <li>The <code>condition</code> must evaluate to a <code>BOOL</code> type. An <code>ExecutorException</code> will be thrown if it does not.</li>
 *       <li>Only the selected branch (<code>trueBody</code> or <code>falseBody</code>) is executed. The other branch is not evaluated, which can be useful for performance or to prevent errors from unfulfilled conditions.</li>
 *     </ul>
 *   </div>
 * </div>
 */
public class CommandIf extends Command {

    public CommandIf() {
        super();
        numberOfNormalizedParams = 1;
    }

    @Override
    public Atom execute(List<Atom> aList, CoachContext coachContext) throws ExecutorException {
        checkNumParams(aList, 2, 3);

        Atom cond = aList.get(0);

        Atom ret = null;

        boolean isTrue;
        try {
            isTrue = cond.isTrue(coachContext);
        } catch (ExecutorException e) {
            throw new ExecutorException("Error while executing if condition " + aList.get(0), e);
        }
        if (isTrue) {
            ret = aList.get(1).execute(coachContext);
        } else {
            if (aList.size() == 3) {
                ret = aList.get(2).execute(coachContext);
            } else {
                ret = null;
            }
        }
        if (ret != null) {
            return ret;
        } else {
            return new Atom(Atom.AtomType.BOOL, "FALSE", null);
        }
    }
}
