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

import eu.smesec.cysec.platform.bridge.execptions.CacheException;
import eu.smesec.cysec.platform.bridge.generated.Answer;
import eu.smesec.cysec.platform.bridge.generated.Question;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <div class="command-doc">
 *   <div class="command-header">
 *     <h2 class="command-name">isSelected</h2>
 *   </div>
 *
 *   <div class="command-signature">
 *     <code><span class="return-type">BOOL</span> isSelected(<span class="params">optionId: STRING</span>)</code>
 *   </div>
 *
 *   <div class="command-description">
 *     <p>This command checks if a specific option of a question has been selected by the user.</p>
 *     <p>It takes an <code>optionId</code> (e.g., "q10o1") and returns <code>TRUE</code> if that option is currently selected for its respective question; otherwise, it returns <code>FALSE</code>.</p>
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
 *           <td><code>optionId</code></td>
 *           <td><code>STRING</code></td>
 *           <td>Yes</td>
 *           <td>The full ID of the option to check, typically in the format "questionIDoOptionValue".</td>
 *         </tr>
 *       </tbody>
 *     </table>
 *   </div>
 *
 *   <div class="command-section">
 *     <h3>Return Value</h3>
 *     <p><code>BOOL</code> - <code>TRUE</code> if the specified option is selected, <code>FALSE</code> otherwise.</p>
 *   </div>
 *
 *   <div class="command-section">
 *     <h3>Examples</h3>
 *     <div class="example">
 *       <h4>Checking a single option</h4>
 *       <pre><code>isSelected("q8200oTLS13") // TRUE if TLS1.3 is selected for question q8200</code></pre>
 *       <p class="example-description">Checks if the "TLS13" option for question "q8200" is selected.</p>
 *     </div>
 *     <div class="example">
 *       <h4>Using in a conditional statement</h4>
 *       <pre><code>if(isSelected("q10oYes"), setHidden("q20", FALSE));</code></pre>
 *       <p class="example-description">If "q10oYes" is selected, question "q20" becomes visible.</p>
 *     </div>
 *     <div class="example">
 *       <h4>Combining with logical operators</h4>
 *       <pre><code>or(isSelected("q8200oSSL1"), isSelected("q8200oSSL2")) : isOldSSL : set("isOldSSL", TRUE);</code></pre>
 *       <p class="example-description">Sets "isOldSSL" to TRUE if either SSL1 or SSL2 is selected.</p>
 *     </div>
 *   </div>
 *
 *   <div class="command-section">
 *     <h3>Notes</h3>
 *     <ul>
 *       <li>The <code>optionId</code> must be a string.</li>
 *       <li>If the question corresponding to the <code>optionId</code> is currently hidden, this command will return <code>FALSE</code>, as hidden questions cannot have selected answers.</li>
 *       <li>The <code>optionId</code> is parsed to extract the parent question ID using a regex pattern <code>[^0-9]*[q]\\d+</code>. Ensure your option IDs adhere to this format.</li>
 *     </ul>
 *   </div>
 * </div>
 */
public class CommandIsSelected extends Command {

    public Atom execute(List<Atom> aList, CoachContext coachContext) throws ExecutorException {

        // expects 3 parameters: name, context of var and value
        checkNumParams(aList, 1);

        // evaluate parameters
        Atom varContent =
                checkAtomType(aList.get(0), Arrays.asList(Atom.AtomType.STRING), true, coachContext, "varContent");

        Answer answer = null;
        try {
            //            v
            // q110oNone
            // split id of "company-q10o4" to "q10o4"
            // Problem: question id might contain "o" thus splitting into more than 2 parts!
            String regex = "[^0-9]*[q]\\d+";
            Pattern pattern = Pattern.compile(regex);
            Matcher match = pattern.matcher(varContent.getId());

            if (match.find()) {
                String questionId = varContent.getId().substring(match.start(), match.end());

                // disassemble option into question and option by splitting with "o": q10o1
                answer = coachContext.getCal().getAnswer(coachContext.getFqcn().toString(), questionId);

                // Check if question is hidden and if so immediately return false since answers of
                // hidden
                // questions
                // cannot be selected
                if (coachContext.getCoach().getQuestions().getQuestion().stream()
                        .filter(q -> q.getId().equals(questionId))
                        .findFirst()
                        .map(Question::isHidden)
                        .orElseThrow(() -> new ExecutorException("Question id " + questionId + " doesn't exist"))) {
                    return new Atom(Atom.AtomType.BOOL, "FALSE", null);
                }

            } else
                throw new ExecutorException("question id doesn't match pattern [^0-9]*[q]\\d+: " + varContent.getId());

        } catch (CacheException e) {
            coachContext.getLogger().log(Level.SEVERE, String.format("Error loading answer %s", varContent.getId()));
        }

        // determine provided option is selected
        String boolResult;
        String ans = null;
        if (answer != null) {
            String vc = varContent.getId();
            ans = " " + (answer.getAidList() == null ? answer.getText() : answer.getAidList()) + " ";

            // don't use ans.contains to avoid unintended matches (e.g. q10HTTP should not match
            // when
            // q10HTTPS is
            // choosen)
            if (Arrays.stream(ans.split(" ")).anyMatch(it -> it.equals(vc))) {
                boolResult = "TRUE";
            } else {
                boolResult = "FALSE";
            }
        } else {
            ans = "<UNSET>";
            boolResult = "FALSE";
        }
        coachContext
                .getLogger()
                .fine(String.format("isSelected(%s) == currently:%s ==> %s", varContent.getId(), ans, boolResult));

        return new Atom(Atom.AtomType.BOOL, boolResult, null);
    }
}
