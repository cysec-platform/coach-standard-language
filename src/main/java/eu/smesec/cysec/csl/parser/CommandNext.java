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

import java.util.Arrays;
import java.util.List;

/**
 * <div class="command-doc">
 *   <div class="command-header">
 *     <h2 class="command-name">setNext</h2>
 *   </div>
 *
 *   <div class="command-signature">
 *     <code><span class="return-type">NULL</span> setNext(<span class="params">questionId: STRING</span>)</code>
 *   </div>
 *
 *   <div class="command-description">
 *     <p>This command signals the platform to display a specific question with the given <code>questionId</code> next, instead of following the default sequential flow.</p>
 *     <p>It effectively overrides the normal navigation, allowing for conditional jumps within the questionnaire.</p>
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
 *           <td>The ID of the question to navigate to next.</td>
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
 *       <h4>Jumping to a specific question</h4>
 *       <pre><code>setNext("q20");</code></pre>
 *       <p class="example-description">When this command is executed, the user will be directed to question "q20".</p>
 *     </div>
 *     <div class="example">
 *       <h4>Conditional navigation</h4>
 *       <pre><code>if(isSelected("q10oYes"), setNext("q15"), setNext("q25"));</code></pre>
 *       <p class="example-description">Navigates to "q15" if "q10oYes" is selected, otherwise to "q25".</p>
 *     </div>
 *   </div>
 *
 *   <div class="command-section">
 *     <h3>Notes</h3>
 *     <ul>
 *       <li>The target question (<code>questionId</code>) must be a visible question. If it's hidden, the navigation might not work as expected, or the user might encounter an inconsistent state. Ensure that you have previously made the target question visible using <code>setHidden(questionId, FALSE)</code> if necessary.</li>
 *       <li>Variables set by <code>setNext</code> are automatically cleared by the <code>ExecutorContext</code> before each evaluation, so there's no need to explicitly "unset" them.</li>
 *     </ul>
 *   </div>
 * </div>
 */
public class CommandNext extends Command {

    public Atom execute(List<Atom> aList, CoachContext coachContext) throws ExecutorException {

        // expects 1 parameter: next page string
        checkNumParams(aList, 1);

        // evaluate parameters
        Atom varContent =
                checkAtomType(aList.get(0), Arrays.asList(Atom.AtomType.STRING), true, coachContext, "varContent");

        // assert type of parameters
        if (varContent.getType() != Atom.AtomType.STRING) {
            throw new ExecutorException("Invalid types for parameters: Provide [0] String");
        }

        // set the next page
        coachContext
                .getContext()
                .setVariable(
                        "_coach.nextPage",
                        varContent,
                        coachContext.getQuestionContext().getId());

        return NULL_ATOM;
    }
}
