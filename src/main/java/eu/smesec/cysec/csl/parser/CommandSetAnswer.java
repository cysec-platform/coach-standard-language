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
import eu.smesec.cysec.platform.bridge.ILibCal;
import eu.smesec.cysec.platform.bridge.execptions.CacheException;
import eu.smesec.cysec.platform.bridge.generated.Answer;
import eu.smesec.cysec.platform.bridge.generated.Question;
import eu.smesec.cysec.platform.bridge.generated.QuestionType;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

/**
 * <div class="command-doc">
 *   <div class="command-header">
 *     <h2 class="command-name">setAnswer</h2>
 *   </div>
 *
 *   <div class="command-signature">
 *     <code><span class="return-type">NULL</span> setAnswer(<span class="params">questionId: STRING, answerValue: STRING|NULL</span>)</code>
 *   </div>
 *
 *   <div class="command-description">
 *     <p>This command allows setting an answer for a specific question programmatically.</p>
 *     <p>It takes the ID of the question to answer and the value for that answer. For 'Astar' type questions (multiple-choice or select-many), multiple selected options can be provided as a space-separated string of option IDs. Note that this command will <b>overwrite</b> any existing answer values for the specified question.</p>
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
 *           <td>The ID of the question for which to set the answer.</td>
 *         </tr>
 *         <tr>
 *           <td><code>answerValue</code></td>
 *           <td><code>STRING</code> or <code>NULL</code></td>
 *           <td>Yes</td>
 *           <td>The value of the answer. For Astar questions, use space-separated option IDs. Use <code>NULL</code> to clear an answer.</td>
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
 *       <h4>Setting a single-option answer</h4>
 *       <pre><code>setAnswer("q30oEmailOption", "yes");</code></pre>
 *       <p class="example-description">Sets the answer for question "q30oEmailOption" to "yes".</p>
 *     </div>
 *     <div class="example">
 *       <h4>Setting multiple options for an Astar question</h4>
 *       <pre><code>setAnswer("q40oProtocols", "HTTP HTTPS FTP");</code></pre>
 *       <p class="example-description">Selects HTTP, HTTPS, and FTP options for the "q40oProtocols" question.</p>
 *     </div>
 *     <div class="example">
 *       <h4>Clearing an answer</h4>
 *       <pre><code>setAnswer("q50oChoice", NULL);</code></pre>
 *       <p class="example-description">Clears the answer for question "q50oChoice".</p>
 *     </div>
 *   </div>
 *
 *   <div class="command-section">
 *     <h3>Notes</h3>
 *     <ul>
 *       <li>Both <code>questionId</code> and <code>answerValue</code> must be of type <code>STRING</code> (or <code>NULL</code> for <code>answerValue</code>).</li>
 *       <li>This command directly manipulates stored answers, which can trigger re-evaluation of coach logic.</li>
 *       <li>Be cautious when using this command, as it overrides existing answers. Ensure this is the intended behavior.</li>
 *     </ul>
 *   </div>
 * </div>
 */
public class CommandSetAnswer extends Command {

    public Atom execute(List<Atom> aList, CoachContext coachContext) throws ExecutorException {

        // expects 2 parameters: name and value of an answer
        checkNumParams(aList, 2);

        // evaluate parameters
        Atom questionId = checkAtomType(aList.get(0), Arrays.asList(AtomType.STRING), true, coachContext, "questionID");
        Atom answerValue = checkAtomType(
                aList.get(1), Arrays.asList(AtomType.STRING, AtomType.NULL), true, coachContext, "answerValue");

        if (answerValue.getType() != AtomType.STRING || questionId.getType() != AtomType.STRING) {
            // TODO is type string really enogh for answer values?
            throw new ExecutorException("Invalid types for parameters: Provide [0] String and [1] String");
        }

        String value = answerValue.getId(); // use getId over toString since toString adds unnecessary "
        // around the
        // value
        String qid = questionId.getId();

        // determine provided option is selected
        ILibCal cal = coachContext.getCal();

        try {
            Answer answer = cal.getAnswer(coachContext.getFqcn().toString(), qid);
            Question question = coachContext.getCoach().getQuestions().getQuestion().stream()
                    .filter(q -> q.getId().equals(qid))
                    .findAny()
                    .orElseThrow(
                            () -> new ExecutorException(String.format("question with id %s does not exist.", qid)));

            if (answer != null) {
                // update existing
                if (EnumSet.of(QuestionType.ASTAR, QuestionType.ASTAREXCL).contains(question.getType())) {
                    answer.setAidList(value);
                    answer.setText(value.split(" ")[0]);
                } else {
                    answer.setText(value);
                }

                // FIXME call missing
                // cal.updateAnswer(coachContext.getFqcn().getCoachId(), answer);
            } else {
                // create new answer
                answer = new Answer();
                answer.setQid(qid);
                answer.setText(value);

                if (EnumSet.of(QuestionType.ASTAR, QuestionType.ASTAREXCL).contains(question.getType())) {
                    answer.setAidList(value);
                }

                // FIXME call missing
                // cal.createAnswer(coachContext.getFqcn().getCoachId(), answer);
            }

        } catch (CacheException e) {
            throw new ExecutorException("error while setting answer of question");
        }

        return Atom.NULL_ATOM;
    }
}
