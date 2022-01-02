/*-
 * #%L
 * CYSEC Standard Coach Language
 * %%
 * Copyright (C) 2020 - 2022 FHNW (University of Applied Sciences and Arts Northwestern Switzerland)
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

import java.util.List;

/**
 * Removes a Recommendation from {@link RecommendationFactory}
 *
 * <p>Syntax: removeRecommendation( recommendationName );</p>
 * <p>Example: removeRecommendation( "TieUpLooseEnds" );</p>
 *
 * @see CommandAddRecommendation
 */
public class CommandRevokeRecommendation extends Command {
  @Override
  public Atom execute(List<Atom> list, CoachContext coachContext) throws ExecutorException {
    if (list.size() != 1) {
      throw new ExecutorException("Invalid number of arguments. Expected 1 parameters.");
    }

    // evaluate parameters
    Atom recommendationName = list.get(0).execute(coachContext);

    if (recommendationName.getType() != Atom.AtomType.STRING) {
      throw new ExecutorException("Recommendation name must be of type STRING");
    }

    CySeCExecutorContextFactory.CySeCExecutorContext c = (CySeCExecutorContextFactory.CySeCExecutorContext) (coachContext.getContext());
    RecommendationFactory.Recommendation recommendation = c.getRecommendation(recommendationName.getId());
    if (recommendation == null) {
        // no operation
      //throw new ExecutorException("Recommendation id "+ recommendationName.getId()+" doesn't exist");
    } else {
        c.removeRecommendation(recommendationName.getId());
    }

    return Atom.NULL_ATOM;
  }

}
