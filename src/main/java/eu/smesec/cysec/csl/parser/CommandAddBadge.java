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
 *     <h2 class="command-name">addBadge</h2>
 *   </div>
 *
 *   <div class="command-signature">
 *     <code><span class="return-type">NULL</span> addBadge(<span class="params">badgeName: STRING, order: INTEGER, urlImg: STRING, altImg: STRING, description: STRING, urlLink: STRING</span>)</code>
 *   </div>
 *
 *   <div class="command-description">
 *     <p>This command creates and adds a new "empty" badge definition to the <code>BadgeFactory</code>. A badge must be defined using this command before any badge classes can be added to it (using <code>addBadgeClass</code>) or awarded (using <code>awardBadge</code>).</p>
 *     <p>The <code>badgeName</code> serves as a unique identifier for the badge.</p>
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
 *           <td>A unique string identifier for the badge.</td>
 *         </tr>
 *         <tr>
 *           <td><code>order</code></td>
 *           <td><code>INTEGER</code></td>
 *           <td>Yes</td>
 *           <td>An integer value used for sorting badges when displayed.</td>
 *         </tr>
 *         <tr>
 *           <td><code>urlImg</code></td>
 *           <td><code>STRING</code></td>
 *           <td>Yes</td>
 *           <td>The URL or path to the default image for the badge.</td>
 *         </tr>
 *         <tr>
 *           <td><code>altImg</code></td>
 *           <td><code>STRING</code></td>
 *           <td>Yes</td>
 *           <td>Alternative text for the badge image, important for accessibility.</td>
 *         </tr>
 *         <tr>
 *           <td><code>description</code></td>
 *           <td><code>STRING</code></td>
 *           <td>Yes</td>
 *           <td>A brief description of the badge, typically displayed when the badge is not yet assigned.</td>
 *         </tr>
 *         <tr>
 *           <td><code>urlLink</code></td>
 *           <td><code>STRING</code></td>
 *           <td>Yes</td>
 *           <td>A URL or internal coach path providing more information about the badge, or "Not assigned yet".</td>
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
 *       <h4>Defining a new badge</h4>
 *       <pre><code>addBadge("ServerSavior", 1, "assets/images/serversavior.svg", "Server Savior Badge", "Not assigned yet", "lib-backup,q20");</code></pre>
 *       <p class="example-description">Creates a badge named "ServerSavior" with a default image, alt text, description, and link.</p>
 *     </div>
 *   </div>
 *
 *   <div class="command-section">
 *     <h3>Notes</h3>
 *     <ul>
 *       <li>All parameters must evaluate to the specified types. An <code>ExecutorException</code> will be thrown for invalid types or if a badge with the given <code>badgeName</code> already exists.</li>
 *       <li>This command only defines the badge structure; it does not award any specific level (class) of the badge.</li>
 *       <li>See also: <code>addBadgeClass</code>, <code>awardBadge</code>, <code>revokeBadge</code>.</li>
 *     </ul>
 *   </div>
 * </div>
 */
public class CommandAddBadge extends Command {

    @Override
    public Atom execute(List<Atom> list, CoachContext coachContext) throws ExecutorException {
        checkNumParams(list, 6);

        // evaluate parameters
        Atom badgeName =
                checkAtomType(list.get(0), Arrays.asList(Atom.AtomType.STRING), true, coachContext, "BadgeName");
        Atom order =
                checkAtomType(list.get(1), Arrays.asList(Atom.AtomType.INTEGER), true, coachContext, "BadgeClassName");
        Atom urlImg = checkAtomType(list.get(2), Arrays.asList(Atom.AtomType.STRING), true, coachContext, "ImageUrl");
        Atom altImg =
                checkAtomType(list.get(3), Arrays.asList(Atom.AtomType.STRING), true, coachContext, "ImageDescription");
        Atom description =
                checkAtomType(list.get(4), Arrays.asList(Atom.AtomType.STRING), true, coachContext, "Description");
        Atom urlLink = checkAtomType(list.get(5), Arrays.asList(Atom.AtomType.STRING), true, coachContext, "urlLink");

        CySeCExecutorContextFactory.CySeCExecutorContext c =
                (CySeCExecutorContextFactory.CySeCExecutorContext) (coachContext.getContext());
        BadgeFactory.Badge b = c.getBadge(badgeName.getId());
        if (b != null) {
            throw new ExecutorException("Badge id " + badgeName.getId() + " does already exist");
        }
        c.setBadge(new BadgeFactory.Badge(
                badgeName.getId(),
                Integer.valueOf(order.getId()),
                urlImg.getId(),
                altImg.getId(),
                description.getId(),
                urlLink.getId()));

        return null;
    }
}
