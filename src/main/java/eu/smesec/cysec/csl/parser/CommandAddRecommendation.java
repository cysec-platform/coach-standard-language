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
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * <div class="command-doc">
 *   <div class="command-header">
 *     <h2 class="command-name">addRecommendation</h2>
 *   </div>
 *
 *   <div class="command-signature">
 *     <code><span class="return-type">NULL</span> addRecommendation(<span class="params">recommendationName: STRING, order: INTEGER, urlImg: STRING, altImg: STRING, title: STRING, description: STRING, textLink: STRING, urlLink: STRING, [tags: STRING]</span>)</code>
 *   </div>
 *
 *   <div class="command-description">
 *     <p>This command creates a new recommendation.</p>
 *     <p>Each recommendation is identified by a unique <code>recommendationName</code> and includes various display properties. Optionally, a comma-separated list of tags can be provided for categorization.</p>
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
 *           <td>A unique string identifier for the recommendation.</td>
 *         </tr>
 *         <tr>
 *           <td><code>order</code></td>
 *           <td><code>INTEGER</code></td>
 *           <td>Yes</td>
 *           <td>An integer value used for sorting recommendations when displayed.</td>
 *         </tr>
 *         <tr>
 *           <td><code>urlImg</code></td>
 *           <td><code>STRING</code></td>
 *           <td>Yes</td>
 *           <td>The URL or path to an image associated with the recommendation.</td>
 *         </tr>
 *         <tr>
 *           <td><code>altImg</code></td>
 *           <td><code>STRING</code></td>
 *           <td>Yes</td>
 *           <td>Alternative text for the recommendation image.</td>
 *         </tr>
 *         <tr>
 *           <td><code>title</code></td>
 *           <td><code>STRING</code></td>
 *           <td>Yes</td>
 *           <td>The title of the recommendation, displayed prominently.</td>
 *         </tr>
 *         <tr>
 *           <td><code>description</code></td>
 *           <td><code>STRING</code></td>
 *           <td>Yes</td>
 *           <td>A detailed description explaining the recommendation.</td>
 *         </tr>
 *         <tr>
 *           <td><code>textLink</code></td>
 *           <td><code>STRING</code></td>
 *           <td>Yes</td>
 *           <td>The display text for a link associated with the recommendation.</td>
 *         </tr>
 *         <tr>
 *           <td><code>urlLink</code></td>
 *           <td><code>STRING</code></td>
 *           <td>Yes</td>
 *           <td>The URL or internal coach path that the link points to.</td>
 *         </tr>
 *         <tr>
 *           <td><code>tags</code></td>
 *           <td><code>STRING</code></td>
 *           <td>No</td>
 *           <td>A comma-separated string of tags (e.g., "technical,quickwin") for categorization. Interpreted as a list of <code>RecommendationFactory.Tag</code> objects.</td>
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
 *       <h4>Adding a basic recommendation</h4>
 *       <pre><code>addRecommendation("TightenLooseEnds", 1, "assets/images/loose_ends.svg", "Loose Ends Icon", "Review Unanswered Questions", "Consider answering all questions for a comprehensive assessment.", "Answer Now", "lib-demo-sub,q10");</code></pre>
 *       <p class="example-description">Creates a recommendation prompting the user to answer outstanding questions.</p>
 *     </div>
 *     <div class="example">
 *       <h4>Adding a recommendation with tags</h4>
 *       <pre><code>addRecommendation("SecurityHardening", 2, "assets/images/hardening.svg", "Hardening Icon", "Implement Security Baselines", "Ensure your systems meet recommended security baselines.", "Learn More", "link-to-baseline-docs", "technical,legal");</code></pre>
 *       <p class="example-description">Adds a technical and legal recommendation with specific tags.</p>
 *     </div>
 *   </div>
 *
 *   <div class="command-section">
 *     <h3>Notes</h3>
 *     <ul>
 *       <li>All parameters must evaluate to the specified types. An <code>ExecutorException</code> will be thrown for invalid types.</li>
 *       <li>The <code>recommendationName</code> must be unique within the context; attempting to add a recommendation with an existing name will cause an error.</li>
 *       <li>See also: <code>revokeRecommendation</code>.</li>
 *     </ul>
 *   </div>
 * </div>
 */
public class CommandAddRecommendation extends Command {
    @Override
    public Atom execute(List<Atom> aList, CoachContext coachContext) throws ExecutorException {
        checkNumParams(aList, 8, 9);

        // evaluate parameters
        Atom recommendationName = checkAtomType(
                aList.get(0), Arrays.asList(Atom.AtomType.STRING), true, coachContext, "recommendationName");
        Atom order = checkAtomType(aList.get(1), Arrays.asList(Atom.AtomType.INTEGER), true, coachContext, "order");
        Atom urlImg = checkAtomType(aList.get(2), Arrays.asList(Atom.AtomType.STRING), true, coachContext, "urlImg");
        Atom altImg = checkAtomType(aList.get(3), Arrays.asList(Atom.AtomType.STRING), true, coachContext, "altImg");
        Atom title = checkAtomType(aList.get(4), Arrays.asList(Atom.AtomType.STRING), true, coachContext, "title");
        Atom description =
                checkAtomType(aList.get(5), Arrays.asList(Atom.AtomType.STRING), true, coachContext, "description");
        Atom textLink =
                checkAtomType(aList.get(6), Arrays.asList(Atom.AtomType.STRING), true, coachContext, "textLink");
        Atom urlLink = checkAtomType(aList.get(7), Arrays.asList(Atom.AtomType.STRING), true, coachContext, "urlLink");

        // execute command
        CySeCExecutorContextFactory.CySeCExecutorContext c =
                (CySeCExecutorContextFactory.CySeCExecutorContext) (coachContext.getContext());

        // Check if the user passed tags
        if (aList.size() == 9) {
            Atom tags = checkAtomType(aList.get(8), Arrays.asList(Atom.AtomType.STRING), true, coachContext, "tags");
            List<RecommendationFactory.Tag> parsedTags = CommandArrayElements.stringToList(tags.getId()).stream()
                    .map(RecommendationFactory.Tag::parse)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toList());
            c.addRecommendation(new RecommendationFactory.Recommendation(
                    recommendationName.getId(),
                    Integer.valueOf(order.getId()),
                    urlImg.getId(),
                    altImg.getId(),
                    title.getId(),
                    description.getId(),
                    textLink.getId(),
                    urlLink.getId(),
                    parsedTags));
        } else {
            c.addRecommendation(new RecommendationFactory.Recommendation(
                    recommendationName.getId(),
                    Integer.valueOf(order.getId()),
                    urlImg.getId(),
                    altImg.getId(),
                    title.getId(),
                    description.getId(),
                    textLink.getId(),
                    urlLink.getId()));
        }

        return null;
    }
}
