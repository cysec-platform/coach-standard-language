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
 *     <h2 class="command-name">addBadgeClass</h2>
 *   </div>
 *
 *   <div class="command-signature">
 *     <code><span class="return-type">NULL</span> addBadgeClass(<span class="params">badgeName: STRING, className: STRING, order: INTEGER, urlImg: STRING, altImg: STRING, description: STRING, urlLink: STRING</span>)</code>
 *   </div>
 *
 *   <div class="command-description">
 *     <p>This command adds a new "class" or level to an already defined badge.</p>
 *     <p>A badge class represents a specific achievement level (e.g., Bronze, Silver, Gold for a "ServerSavior" badge), each with its own visual appearance, description, and link. This command must be executed after <code>addBadge</code> has defined the base badge.</p>
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
 *           <td>The unique identifier of the parent badge to which this class belongs.</td>
 *         </tr>
 *         <tr>
 *           <td><code>className</code></td>
 *           <td><code>STRING</code></td>
 *           <td>Yes</td>
 *           <td>A unique string identifier for this specific class/level of the badge (e.g., "Bronze", "Silver").</td>
 *         </tr>
 *         <tr>
 *           <td><code>order</code></td>
 *           <td><code>INTEGER</code></td>
 *           <td>Yes</td>
 *           <td>An integer value used for sorting badge classes within a badge (e.g., 1 for Bronze, 2 for Silver).</td>
 *         </tr>
 *         <tr>
 *           <td><code>urlImg</code></td>
 *           <td><code>STRING</code></td>
 *           <td>Yes</td>
 *           <td>The URL or path to the image specific to this badge class (e.g., "serversaviorbronze.svg").</td>
 *         </tr>
 *         <tr>
 *           <td><code>altImg</code></td>
 *           <td><code>STRING</code></td>
 *           <td>Yes</td>
 *           <td>Alternative text for this badge class's image.</td>
 *         </tr>
 *         <tr>
 *           <td><code>description</code></td>
 *           <td><code>STRING</code></td>
 *           <td>Yes</td>
 *           <td>A description specific to this badge class (e.g., "Level 1 server expert").</td>
 *         </tr>
 *         <tr>
 *           <td><code>urlLink</code></td>
 *           <td><code>STRING</code></td>
 *           <td>Yes</td>
 *           <td>A URL or internal coach path providing more information about this specific badge class.</td>
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
 *       <h4>Adding a "Bronze" class to "ServerSavior" badge</h4>
 *       <pre><code>addBadgeClass("ServerSavior", "Bronze", 1, "assets/images/serversaviorbronze.svg", "Bronze Server Savior Badge", "Can resolve basic server issues", "lib-backup,q10");</code></pre>
 *       <p class="example-description">Defines the "Bronze" level for the "ServerSavior" badge.</p>
 *     </div>
 *   </div>
 *
 *   <div class="command-section">
 *     <h3>Notes</h3>
 *     <ul>
 *       <li>The badge identified by <code>badgeName</code> must already exist, otherwise an <code>ExecutorException</code> will be thrown.</li>
 *       <li>All parameters must evaluate to the specified types.</li>
 *       <li>See also: <code>addBadge</code>, <code>awardBadge</code>, <code>revokeBadge</code>.</li>
 *     </ul>
 *   </div>
 * </div>
 */
public class CommandAddBadgeClass extends Command {

    @Override
    public Atom execute(List<Atom> aList, CoachContext coachContext) throws ExecutorException {
        // expects 7 parameters
        checkNumParams(aList, 7);

        // evaluate parameters
        Atom badgeName =
                checkAtomType(aList.get(0), Arrays.asList(Atom.AtomType.STRING), true, coachContext, "BadgeName");
        Atom badgeClassName =
                checkAtomType(aList.get(1), Arrays.asList(Atom.AtomType.STRING), true, coachContext, "BadgeClass");
        Atom order =
                checkAtomType(aList.get(2), Arrays.asList(Atom.AtomType.INTEGER), true, coachContext, "BadgeOrder");
        Atom urlImg = checkAtomType(aList.get(3), Arrays.asList(Atom.AtomType.STRING), true, coachContext, "ImageURL");
        Atom altImg =
                checkAtomType(aList.get(4), Arrays.asList(Atom.AtomType.STRING), true, coachContext, "ImageAlternate");
        Atom description =
                checkAtomType(aList.get(5), Arrays.asList(Atom.AtomType.STRING), true, coachContext, "Description");
        Atom urlLink = checkAtomType(aList.get(6), Arrays.asList(Atom.AtomType.STRING), true, coachContext, "LinkURL");

        CySeCExecutorContextFactory.CySeCExecutorContext c =
                (CySeCExecutorContextFactory.CySeCExecutorContext) (coachContext.getContext());
        BadgeFactory.Badge b = c.getBadge(badgeName.getId());
        if (b == null) {
            throw new ExecutorException("Badge id " + badgeName.getId() + " is not known");
        }
        b.addBadgeClass(new BadgeFactory.BadgeClass(
                badgeClassName.getId(),
                Integer.valueOf(order.getId()),
                urlImg.getId(),
                altImg.getId(),
                description.getId(),
                urlLink.getId()));

        return null;
    }
}
