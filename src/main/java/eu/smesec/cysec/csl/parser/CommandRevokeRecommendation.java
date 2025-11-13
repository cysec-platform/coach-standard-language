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

import eu.smesec.cysec.csl.skills.RecommendationFactory;
import java.util.Arrays;
import java.util.List;

/**
 * <div class="command-doc">
 *   <div class="command-header">
 *     <h2 class="command-name">revokeRecommendation</h2>
 *   </div>
 *
 *   <div class="command-signature">
 *     <code><span class="return-type">NULL</span> revokeRecommendation(<span class="params">recommendationName: STRING</span>)</code>
 *   </div>
 *
 *   <div class="command-description">
 *     <p>This command removes an existing recommendation from the <code>RecommendationFactory</code>.</p>
 *     <p>It takes the unique <code>recommendationName</code> as a parameter. If a recommendation with the given name does not exist, the command will silently do nothing (it will not throw an <code>ExecutorException</code>).</p>
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
 *           <td><code>recommendationName</code></td>
 *           <td><code>STRING</code></td>
 *           <td>Yes</td>
 *           <td>The unique identifier of the recommendation to remove.</td>
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
 *       <h4>Removing a specific recommendation</h4>
 *       <pre><code>revokeRecommendation("TightenLooseEnds");</code></pre>
 *       <p class="example-description">Removes the recommendation named "TightenLooseEnds".</p>
 *     </div>
 *     <div class="example">
 *       <h4>Conditional recommendation removal</h4>
 *       <pre><code>if(get("allQuestionsAnswered"), revokeRecommendation("TightenLooseEnds"));</code></pre>
 *       <p class="example-description">Removes the "TightenLooseEnds" recommendation if all questions have been answered.</p>
 *     </div>
 *   </div>
 *
 *   <div class="command-section">
 *     <h3>Notes</h3>
 *     <ul>
 *       <li>The <code>recommendationName</code> must be a string.</li>
 *       <li>This command only removes the recommendation from the active context; it does not affect its definition in the XML.</li>
 *       <li>See also: <code>addRecommendation</code>.</li>
 *     </ul>
 *   </div>
 * </div>
 */
public class CommandRevokeRecommendation extends Command {
    @Override
    public Atom execute(List<Atom> aList, CoachContext coachContext) throws ExecutorException {
        checkNumParams(aList, 1);

        // evaluate parameters
        Atom recommendationName = checkAtomType(
                aList.get(0), Arrays.asList(Atom.AtomType.STRING), true, coachContext, "recommendationName");

        CySeCExecutorContextFactory.CySeCExecutorContext c =
                (CySeCExecutorContextFactory.CySeCExecutorContext) (coachContext.getContext());
        RecommendationFactory.Recommendation recommendation = c.getRecommendation(recommendationName.getId());
        if (recommendation == null) {
            // no operation
            // throw new ExecutorException("Recommendation id "+ recommendationName.getId()+"
            // doesn't
            // exist");
        } else {
            c.removeRecommendation(recommendationName.getId());
        }

        return Atom.NULL_ATOM;
    }
}
