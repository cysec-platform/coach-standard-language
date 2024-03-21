/*-
 * #%L
 * CYSEC Standard Coach Language
 * %%
 * Copyright (C) 2020 - 2024 FHNW (University of Applied Sciences and Arts Northwestern Switzerland)
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
 * Revokes the current badgeClass of a badge class in {@link BadgeFactory}. This method may only be executed after a Badge is awarded using {@link CommandAwardBadge}.
 * If no Badge with the given ID or class exist, Executor throws an exception.
 *
 * <p>Syntax: revokeBadge( badgeName);</p>
 *  <p>Example: revokeBadge("ServerSavior");</p>
 *
 * @see CommandAddBadge
 * @see CommandAwardBadge
 * @see CommandAddBadgeClass
 *
 */
public class CommandRevokeBadge extends Command {
  @Override
  public Atom execute(List<Atom> aList, CoachContext coachContext) throws ExecutorException {
    checkNumParams(aList, 1, 1);

    // evaluate parameters
    Atom badgeName = checkAtomType(aList.get(0), Arrays.asList(Atom.AtomType.STRING), true, coachContext, "BadgeName" );

    // execute command
    CySeCExecutorContextFactory.CySeCExecutorContext c = (CySeCExecutorContextFactory.CySeCExecutorContext) (coachContext.getContext());
    BadgeFactory.Badge badge = c.getBadge(badgeName.getId());
    if (badge == null) {
      //throw new ExecutorException("Badge id "+badgeName.getId()+" doesn't exist");
    } else {
      badge.revokeAwardedBadge();
    }

    return Atom.NULL_ATOM;
  }

}
