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

/**
 * <div class="command-doc">
 *   <div class="command-header">
 *     <h2 class="command-name">capScore</h2>
 *   </div>
 *
 *   <div class="command-signature">
 *     <code><span class="return-type">NULL</span> capScore(<span class="params">scoreId: STRING, value: INTEGER|FLOAT</span>)</code>
 *   </div>
 *
 *   <div class="command-description">
 *     <p>This command locks the value of a specified score (skill) to a given numeric value.</p>
 *     <p>The library will continue to record internal contributions (additions/subtractions) to the said skill, but when the score is retrieved, the capped value will be returned. There is no explicit "removeCap" command; the cap is implicitly removed when the condition that triggered it no longer holds, causing the coach logic to reevaluate.</p>
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
 *           <td><code>scoreId</code></td>
 *           <td><code>STRING</code></td>
 *           <td>Yes</td>
 *           <td>The unique identifier for the score (skill) to cap.</td>
 *         </tr>
 *         <tr>
 *           <td><code>value</code></td>
 *           <td><code>INTEGER</code> or <code>FLOAT</code></td>
 *           <td>Yes</td>
 *           <td>The maximum numeric value the score should be capped at.</td>
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
 *       <h4>Capping a score conditionally</h4>
 *       <pre><code>isSelected("q10o3") : q10o3 : {
 *     capScore("knowhow", 100);
 * };</code></pre>
 *       <p class="example-description">If option "q10o3" is selected, the "knowhow" score will be capped at 100. If "q10o3" is deselected, the cap will be removed during reevaluation.</p>
 *     </div>
 *   </div>
 *
 *   <div class="command-section">
 *     <h3>Notes</h3>
 *     <ul>
 *       <li>The cap is active only as long as the condition under which `capScore` was called remains true.</li>
 *       <li>Subsequent additions to the score beyond the cap will still be recorded internally but will not increase the visible score. If the cap is removed, the score will reflect its true accumulated value.</li>
 *     </ul>
 *   </div>
 * </div>
 */
public class CommandCapScore extends CommandAbstractScore {

    @Override
    void score(String scoreId, String questionId, double value, ExecutorContext context) {
        context.getScore(scoreId).cap(questionId, value);
    }
}
