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

import eu.smesec.cysec.platform.bridge.ILibCal;
import eu.smesec.cysec.platform.bridge.execptions.CacheException;
import eu.smesec.cysec.platform.bridge.generated.Answer;
import eu.smesec.cysec.platform.bridge.generated.Question;
import java.util.Arrays;
import java.util.List;

/**
 * <div class="command-doc">
 *   <div class="command-header">
 *     <h2 class="command-name">isAnswered</h2>
 *   </div>
 *
 *   <div class="command-signature">
 *     <code><span class="return-type">BOOL</span> isAnswered(<span class="params">questionId: STRING</span>)</code>
 *   </div>
 *
 *   <div class="command-description">
 *     <p>This command checks if a specific question has received an answer from the user.</p>
 *     <p>It takes a <code>questionId</code> and returns <code>TRUE</code> if the question has a recorded answer; otherwise, it returns <code>FALSE</code>.</p>
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
 *           <td>The ID of the question to check.</td>
 *         </tr>
 *       </tbody>
 *     </table>
 *   </div>
 *
 *   <div class="command-section">
 *     <h3>Return Value</h3>
 *     <p><code>BOOL</code> - <code>TRUE</code> if the specified question has been answered, <code>FALSE</code> otherwise.</p>
 *   </div>
 *
 *   <div class="command-section">
 *     <h3>Examples</h3>
 *     <div class="example">
 *       <h4>Checking if a question has been answered</h4>
 *       <pre><code>isAnswered("q100") // TRUE if question q100 has an answer</code></pre>
 *       <p class="example-description">Returns TRUE if question "q100" has any answer value set.</p>
 *     </div>
 *     <div class="example">
 *       <h4>Conditional logic based on answer status</h4>
 *       <pre><code>if(isAnswered("q200"), set("hasProgress", TRUE));</code></pre>
 *       <p class="example-description">Sets the "hasProgress" variable to TRUE if question "q200" has been answered.</p>
 *     </div>
 *   </div>
 *
 *   <div class="command-section">
 *     <h3>Notes</h3>
 *     <ul>
 *       <li>The <code>questionId</code> must be a string.</li>
 *       <li>If the question corresponding to the <code>questionId</code> is currently hidden, this command will return <code>FALSE</code>, as hidden questions are considered unanswerable.</li>
 *       <li>This command checks for the existence of *any* answer, not for a specific answer value. To check for specific selections, use <code>isSelected()</code>.</li>
 *     </ul>
 *   </div>
 * </div>
 */
public class CommandIsAnswered extends Command {

    public Atom execute(List<Atom> aList, CoachContext coachContext) throws ExecutorException {

        // expects 3 parameters: name, context of var and value
        checkNumParams(aList, 1);

        // evaluate parameters
        Atom varContent =
                checkAtomType(aList.get(0), Arrays.asList(Atom.AtomType.STRING), true, coachContext, "varContent");

        // Check if the question is even visible. If the question is hidden there's no way that it
        // can
        // be answered
        if (coachContext.getCoach().getQuestions().getQuestion().stream()
                .filter(q -> q.getId().equals(varContent.getId()))
                .findFirst()
                .map(Question::isHidden)
                .orElseThrow(() -> new ExecutorException("Question id " + varContent.getId() + " doesn't exist"))) {
            return new Atom(Atom.AtomType.BOOL, "FALSE", null);
        }

        // determine provided option is selected
        ILibCal cal = coachContext.getCal();
        Answer answer = null;
        try {
            // Attention: Use question ID instead of question! getAnswer accepts Object.
            // Answer object in CoachContext is answer of evaluated question, isAnswered may be
            // executed
            // for another
            // question
            // which is not in the current context.
            answer = cal.getAnswer(coachContext.getFqcn().toString(), varContent.getId());
        } catch (CacheException e) {
            throw new NullPointerException();
        }
        String boolResult;
        if (answer != null) {
            boolResult = "TRUE";
        } else {
            boolResult = "FALSE";
        }

        return new Atom(Atom.AtomType.BOOL, boolResult, null);
    }
}
