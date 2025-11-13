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

import eu.smesec.cysec.csl.skills.BadgeFactory;
import java.util.Arrays;
import java.util.List;

/**
 * <div class="command-doc">
 *   <div class="command-header">
 *     <h2 class="command-name">awardBadge</h2>
 *   </div>
 *
 *   <div class="command-signature">
 *     <code><span class="return-type">NULL</span> awardBadge(<span class="params">badgeName: STRING, className: STRING</span>)</code>
 *   </div>
 *
 *   <div class="command-description">
 *     <p>This command awards a specific class (level) of an already defined badge to the user.</p>
 *     <p>Both the base badge (defined by <code>addBadge</code>) and the specific badge class (defined by <code>addBadgeClass</code>) must exist prior to calling this command. Awarding a badge makes its visual representation and description for that class visible to the user, typically on a summary page.</p>
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
 *           <td><code>badgeName</code></td>
 *           <td><code>STRING</code></td>
 *           <td>Yes</td>
 *           <td>The unique identifier of the badge to award.</td>
 *         </tr>
 *         <tr>
 *           <td><code>className</code></td>
 *           <td><code>STRING</code></td>
 *           <td>Yes</td>
 *           <td>The unique identifier of the specific class/level of the badge to award (e.g., "Bronze", "Gold").</td>
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
 *       <h4>Awarding a "Bronze" level badge</h4>
 *       <pre><code>awardBadge("ServerSavior", "Bronze");</code></pre>
 *       <p class="example-description">Awards the "Bronze" class of the "ServerSavior" badge.</p>
 *     </div>
 *     <div class="example">
 *       <h4>Conditional badge awarding</h4>
 *       <pre><code>if(greaterThan(get("serverExpertise"), 75), awardBadge("ServerSavior", "Gold"));</code></pre>
 *       <p class="example-description">Awards the "Gold" class of "ServerSavior" badge if the "serverExpertise" score is above 75.</p>
 *     </div>
 *   </div>
 *
 *   <div class="command-section">
 *     <h3>Notes</h3>
 *     <ul>
 *       <li>If the <code>badgeName</code> or <code>className</code> do not correspond to an existing badge or badge class, an <code>ExecutorException</code> will be thrown.</li>
 *       <li>If a badge is already awarded at a certain level and this command attempts to award it at a different level, the new level will override the previous one.</li>
 *       <li>See also: <code>addBadge</code>, <code>addBadgeClass</code>, <code>revokeBadge</code>.</li>
 *     </ul>
 *   </div>
 * </div>
 */
public class CommandAwardBadge extends Command {
    @Override
    public Atom execute(List<Atom> aList, CoachContext coachContext) throws ExecutorException {
        checkNumParams(aList, 2);

        // evaluate parameters
        Atom badgeName =
                checkAtomType(aList.get(0), Arrays.asList(Atom.AtomType.STRING), true, coachContext, "BadgeName");
        Atom badgeClass =
                checkAtomType(aList.get(1), Arrays.asList(Atom.AtomType.STRING), true, coachContext, "BadgeClass");

        // execute command
        CySeCExecutorContextFactory.CySeCExecutorContext c =
                (CySeCExecutorContextFactory.CySeCExecutorContext) (coachContext.getContext());
        BadgeFactory.Badge b = c.getBadge(badgeName.getId());
        if (b == null) {
            throw new ExecutorException("Badge id " + badgeName.getId() + " doesn't exist");
        }
        b.awardBadgeClass(badgeClass.getId());

        return Atom.NULL_ATOM;
    }
}
