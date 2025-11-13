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
 *     <h2 class="command-name">revokeBadge</h2>
 *   </div>
 *
 *   <div class="command-signature">
 *     <code><span class="return-type">NULL</span> revokeBadge(<span class="params">badgeName: STRING</span>)</code>
 *   </div>
 *
 *   <div class="command-description">
 *     <p>This command revokes the currently awarded class (level) of a specified badge, effectively un-awarding it.</p>
 *     <p>The badge must have been previously awarded using <code>awardBadge</code>. If no badge with the given ID exists or has been awarded, the command logs a message but does not throw an exception (i.e., it's a no-op if the badge isn't active).</p>
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
 *           <td>The unique identifier of the badge whose awarded class is to be revoked.</td>
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
 *       <h4>Revoking a badge</h4>
 *       <pre><code>revokeBadge("ServerSavior");</code></pre>
 *       <p class="example-description">Revokes the currently awarded class of the "ServerSavior" badge.</p>
 *     </div>
 *     <div class="example">
 *       <h4>Conditional badge revocation</h4>
 *       <pre><code>if(lowerThan(get("serverExpertise"), 50), revokeBadge("ServerSavior"));</code></pre>
 *       <p class="example-description">Revokes the "ServerSavior" badge if the "serverExpertise" score drops below 50.</p>
 *     </div>
 *   </div>
 *
 *   <div class="command-section">
 *     <h3>Notes</h3>
 *     <ul>
 *       <li>If the <code>badgeName</code> does not correspond to an existing badge definition, an <code>ExecutorException</code> will be thrown.</li>
 *       <li>This command only revokes the *awarded class* of a badge, not the badge definition itself. The badge and its classes remain defined and can be re-awarded later.</li>
 *       <li>See also: <code>addBadge</code>, <code>addBadgeClass</code>, <code>awardBadge</code>.</li>
 *     </ul>
 *   </div>
 * </div>
 */
public class CommandRevokeBadge extends Command {
    @Override
    public Atom execute(List<Atom> aList, CoachContext coachContext) throws ExecutorException {
        checkNumParams(aList, 1, 1);

        // evaluate parameters
        Atom badgeName =
                checkAtomType(aList.get(0), Arrays.asList(Atom.AtomType.STRING), true, coachContext, "BadgeName");

        // execute command
        CySeCExecutorContextFactory.CySeCExecutorContext c =
                (CySeCExecutorContextFactory.CySeCExecutorContext) (coachContext.getContext());
        BadgeFactory.Badge badge = c.getBadge(badgeName.getId());
        if (badge == null) {
            throw new ExecutorException("Badge id " + badgeName.getId() + " doesn't exist");
        } else {
            badge.revokeAwardedBadge();
        }

        return Atom.NULL_ATOM;
    }
}
