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
 *     <h2 class="command-name">addScore</h2>
 *   </div>
 *
 *   <div class="command-signature">
 *     <code><span class="return-type">NULL</span> addScore(<span class="params">scoreId: STRING, value: INTEGER|FLOAT</span>)</code>
 *   </div>
 *
 *   <div class="command-description">
 *     <p>This command adds or subtracts a value from a specified score (skill).</p>
 *     <p>The <code>scoreId</code> can be any string, but it must be unique within the coach context. The platform might only support skills with a certain name prefix. The sign of the <code>value</code> determines whether the score should be added (+) or subtracted (-). Note that the value returned by <code>CySeCExecutorContextFactory.CySeCExecutorContext.getScore(String)</code> won't change if a cap has previously been applied to that score.</p>
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
 *           <td>The unique identifier for the score (skill) to modify.</td>
 *         </tr>
 *         <tr>
 *           <td><code>value</code></td>
 *           <td><code>INTEGER</code> or <code>FLOAT</code></td>
 *           <td>Yes</td>
 *           <td>The numeric value to add to (if positive) or subtract from (if negative) the score.</td>
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
 *       <h4>Adding to a score</h4>
 *       <pre><code>addScore("strength", 50);</code></pre>
 *       <p class="example-description">Adds 50 to the "strength" score.</p>
 *     </div>
 *     <div class="example">
 *       <h4>Subtracting from a score</h4>
 *       <pre><code>addScore("knowhow", -10);</code></pre>
 *       <p class="example-description">Subtracts 10 from the "knowhow" score.</p>
 *     </div>
 *   </div>
 *
 *   <div class="command-section">
 *     <h3>Notes</h3>
 *     <ul>
 *       <li>The command implicitly uses the current question's ID as the source of the score contribution.</li>
 *       <li>If a score is capped using <code>capScore</code>, calling <code>addScore</code> will modify the internal value, but the externally visible score will remain at its capped value until the cap is removed or the condition for the cap no longer holds.</li>
 *     </ul>
 *   </div>
 * </div>
 */
public class CommandAddScore extends CommandAbstractScore {

    @Override
    void score(String scoreId, String questionId, double value, ExecutorContext context) {
        context.getScore(scoreId).add(questionId, value);

        // TODO: Decide if all questions should contribute to parent score
        /*if(context.getParent() != null) {
          context.getParent().getScore(scoreId).add(questionId, value);
        }*/
    }
}
