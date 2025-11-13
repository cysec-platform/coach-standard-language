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
import eu.smesec.cysec.platform.bridge.generated.Question;
import java.util.Arrays;
import java.util.List;

/**
 * <div class="command-doc">
 *   <div class="command-header">
 *     <h2 class="command-name">setMHidden</h2>
 *   </div>
 *
 *   <div class="command-signature">
 *     <code><span class="return-type">INTEGER</span> setMHidden(<span class="params">lowId: STRING, highId: STRING, hiddenState: BOOL</span>)</code>
 *   </div>
 *
 *   <div class="command-description">
 *     <p>This command modifies the display status (hidden or visible) for a range of questions identified by their IDs.</p>
 *     <p>It takes a starting question ID (inclusive), an ending question ID (exclusive), and a boolean value to determine whether they should be hidden or shown. Question IDs are compared lexicographically.</p>
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
 *           <td><code>lowId</code></td>
 *           <td><code>STRING</code></td>
 *           <td>Yes</td>
 *           <td>The ID of the first question in the range (inclusive).</td>
 *         </tr>
 *         <tr>
 *           <td><code>highId</code></td>
 *           <td><code>STRING</code></td>
 *           <td>Yes</td>
 *           <td>The ID of the question marking the end of the range (exclusive).</td>
 *         </tr>
 *         <tr>
 *           <td><code>hiddenState</code></td>
 *           <td><code>BOOL</code></td>
 *           <td>Yes</td>
 *           <td>The desired hidden state. <code>TRUE</code> to hide questions, <code>FALSE</code> to show them.</td>
 *         </tr>
 *       </tbody>
 *     </table>
 *   </div>
 *
 *   <div class="command-section">
 *     <h3>Return Value</h3>
 *     <p><code>INTEGER</code> - The number of questions whose hidden state was actually changed by the command.</p>
 *   </div>
 *
 *   <div class="command-section">
 *     <h3>Examples</h3>
 *     <div class="example">
 *       <h4>Hiding a range of questions</h4>
 *       <pre><code>setMHidden("q8200", "q8300", TRUE);</code></pre>
 *       <p class="example-description">Hides all questions with IDs from "q8200" up to (but not including) "q8300".</p>
 *     </div>
 *     <div class="example">
 *       <h4>Showing a range of questions conditionally</h4>
 *       <pre><code>equals(getParentArgument(), "https") : protocolSelection : {
 *   setMHidden("q8600", "q8700", FALSE); // Show HTTP questions
 * };</code></pre>
 *       <p class="example-description">When the parent argument is "https", this statement shows questions from "q8600" to "q8700".</p>
 *     </div>
 *   </div>
 *
 *   <div class="command-section">
 *     <h3>Notes</h3>
 *     <ul>
 *       <li>Question IDs are compared as strings. Ensure your IDs follow a consistent lexicographical order for proper range selection (e.g., "q10" < "q100" < "q11").</li>
 *       <li>To prevent a question from being re-hidden/re-shown unexpectedly, ensure that the conditions around <code>setMHidden</code> (and <code>setHidden</code>) are carefully managed.</li>
 *     </ul>
 *   </div>
 * </div>
 */
public class CommandSetMHidden extends Command {

    public Atom execute(List<Atom> aList, CoachContext coachContext) throws ExecutorException {

        // Three parameters expected: Lower name (inclusive), higher value (exclusive) and hiding
        // value
        checkNumParams(aList, 3);

        // evaluate parameters
        Atom varLowId = checkAtomType(aList.get(0), Arrays.asList(Atom.AtomType.STRING), true, coachContext, "lowID");
        Atom varHighId = checkAtomType(aList.get(1), Arrays.asList(Atom.AtomType.STRING), true, coachContext, "highID");
        boolean varContentBool = Boolean.valueOf(
                checkAtomType(aList.get(2), Arrays.asList(Atom.AtomType.BOOL), true, coachContext, "hideState")
                        .getId());
        coachContext
                .getLogger()
                .fine(String.format(
                        "Set questions in range from %s to %s to hidden=%s",
                        varLowId.getId(), varHighId.getId(), varContentBool));

        // Update question hidden status
        int cnt = 0;
        for (Question question : coachContext.getCoach().getQuestions().getQuestion()) {
            // coachContext.getLogger().info(String.format("    low:  %s?=%s=%s",
            // varLowId.getId(),question.getId(),
            // varLowId.getId().compareTo(question.getId())));
            // coachContext.getLogger().info(String.format("    high: %s?=%s=%s",
            // varHighId.getId(),question.getId(),
            // varHighId.getId().compareTo(question.getId())));
            if (varLowId.getId().compareTo(question.getId()) <= 0
                    && varHighId.getId().compareTo(question.getId()) > 0
                    && question.isHidden() != varContentBool) {
                question.setHidden(varContentBool);
                coachContext
                        .getLogger()
                        .fine(String.format(
                                "  question %s is new set to hidden=%s (%d/%d)",
                                question.getId(),
                                varContentBool ? "HIDDEN" : "VISIBLE",
                                varLowId.getId().compareTo(question.getId()),
                                varHighId.getId().compareTo(question.getId())));
                cnt++;
            }
        }
        return new Atom(AtomType.INTEGER, "" + cnt, null);
    }
}
