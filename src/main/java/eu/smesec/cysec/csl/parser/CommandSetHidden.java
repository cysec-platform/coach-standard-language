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

import eu.smesec.cysec.platform.bridge.generated.Question;
import java.util.Arrays;
import java.util.List;

/**
 * <div class="command-doc">
 *   <div class="command-header">
 *     <h2 class="command-name">setHidden</h2>
 *   </div>
 *
 *   <div class="command-signature">
 *     <code><span class="return-type">NULL</span> setHidden(<span class="params">questionId: STRING, hiddenState: BOOL</span>)</code>
 *   </div>
 *
 *   <div class="command-description">
 *     <p>This command sets the hidden state of a given question.</p>
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
 *           <td><code>questionId</code></td>
 *           <td><code>STRING</code></td>
 *           <td>Yes</td>
 *           <td>ID of the question to set hidden state of</td>
 *         </tr>
 *         <tr>
 *           <td><code>hiddenState</code></td>
 *           <td><code>BOOL</code></td>
 *           <td>Yes</td>
 *           <td>The hidden state. TRUE means the question is hidden and FALSE means the question is visible.</td>
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
 *       <h4>Basic Usage</h4>
 *       <pre><code>setHidden("q1234", TRUE);</code></pre>
 *       <p class="example-description">hides question q1234</p>
 *     </div>
 *     <div class="example">
 *       <h4>Advanced Usage</h4>
 *       <pre><code>setHidden("q1234", not(someOtherCommand()));</code></pre>
 *       <p class="example-description">Arbitrary BOOL-expression can be used</p>
 *     </div>
 *   </div>
 *
 *   <div class="command-section">
 *     <h3>Notes</h3>
 *     <ul>
 *       <li>There's also setMHidden to hide a range of questions.</li>
 *     </ul>
 *   </div>
 * </div>
 */
public class CommandSetHidden extends Command {

    public Atom execute(List<Atom> aList, CoachContext coachContext) throws ExecutorException {

        // expects 2 parameters: name and hidden state
        checkNumParams(aList, 2);

        // evaluate parameters
        Atom questionID =
                checkAtomType(aList.get(0), Arrays.asList(Atom.AtomType.STRING), true, coachContext, "questionID");
        Atom varContentBool = checkAtomType(
                aList.get(1).execute(coachContext), Arrays.asList(Atom.AtomType.BOOL), true, coachContext, "hideState");

        // Update question hidden status
        Question question = coachContext.getCoach().getQuestions().getQuestion().stream()
                .filter(question1 -> question1.getId().equals(questionID.getId()))
                .findFirst()
                .orElseThrow(() -> new ExecutorException("Question id " + questionID.getId() + " doesn't exist"));
        if (question.isHidden() != Boolean.valueOf(varContentBool.getId())) {
            coachContext
                    .getLogger()
                    .fine(String.format(
                            "question %s is new set to hidden=%s (setHidden)",
                            question.getId(), varContentBool.getId()));
            question.setHidden(Boolean.valueOf(varContentBool.getId()));
        }

        return new Atom(Atom.AtomType.NULL, null, null);
    }
}
