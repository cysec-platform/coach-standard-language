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
 * Creates a new instance of Recommendation in {@link RecommendationFactory}.
 * Note that the ID must be unique as this string will be used to retrieve the recommendation from the context.
 * <p>Syntax: AddRecommendation( recommendationName, order, urlImg, altImg, description, textLink, urlLink );</p>
 * <p>Example: addRecommendation("TieUpLooseEnds", 1, "lib-demo-sub,q10", "TestAlt", "Tie up loose ends", "Consider answering this question", "TextLink", "lib-demo-sub,q10");</p>
 *
 * @see CommandRevokeRecommendation
 */
public class CommandAddRecommendation extends Command {
    @Override
    public Atom execute(List<Atom> aList, CoachContext coachContext) throws ExecutorException {
        checkNumParams(aList, 8, 9);

        // evaluate parameters
        Atom recommendationName = checkAtomType(aList.get(0), Arrays.asList(Atom.AtomType.STRING), true, coachContext, "recommendationName");
        Atom order = checkAtomType(aList.get(1), Arrays.asList(Atom.AtomType.INTEGER), true, coachContext, "order");
        Atom urlImg = checkAtomType(aList.get(2), Arrays.asList(Atom.AtomType.STRING), true, coachContext, "urlImg");
        Atom altImg = checkAtomType(aList.get(3), Arrays.asList(Atom.AtomType.STRING), true, coachContext, "altImg");
        Atom title = checkAtomType(aList.get(4), Arrays.asList(Atom.AtomType.STRING), true, coachContext, "title");
        Atom description = checkAtomType(aList.get(5), Arrays.asList(Atom.AtomType.STRING), true, coachContext, "description");
        Atom textLink = checkAtomType(aList.get(6), Arrays.asList(Atom.AtomType.STRING), true, coachContext, "textLink");
        Atom urlLink = checkAtomType(aList.get(7), Arrays.asList(Atom.AtomType.STRING), true, coachContext, "urlLink");

        // execute command
        CySeCExecutorContextFactory.CySeCExecutorContext c = (CySeCExecutorContextFactory.CySeCExecutorContext) (coachContext.getContext());

        // Check if the user passed tags
        if (aList.size() == 9) {
            Atom tags = checkAtomType(aList.get(8), Arrays.asList(Atom.AtomType.STRING), true, coachContext, "tags");
            List<RecommendationFactory.Tag> parsedTags = CommandArrayElements.stringToList(tags.getId())
                    .stream()
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
                    parsedTags
            ));
        } else {
            c.addRecommendation(new RecommendationFactory.Recommendation(recommendationName.getId(), Integer.valueOf(order.getId()), urlImg.getId(), altImg.getId(), title.getId(), description.getId(), textLink.getId(), urlLink.getId()));
        }

        return null;
    }
}
