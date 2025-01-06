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
 * Awards a badge class to a badge instance in {@link BadgeFactory}. This method may only be executed after a Badge is created using {@link CommandAddBadge}
 * and a class created using {@link CommandAddBadgeClass}
 * <p>Syntax: awardBadge( badgeName, className );</p>
 *  <p>Example: awardBadge("ServerSavior", "Bronze");</p>
 *
 * @see CommandAddBadge
 * @see CommandAwardBadge
 * @see CommandRevokeBadge
 *
 */
public class CommandAwardBadge extends Command {
  @Override
  public Atom execute(List<Atom> aList, CoachContext coachContext) throws ExecutorException {
    checkNumParams(aList, 2);

    // evaluate parameters
    Atom badgeName = checkAtomType(aList.get(0), Arrays.asList(Atom.AtomType.STRING), true, coachContext, "BadgeName");
    Atom badgeClass = checkAtomType(aList.get(1), Arrays.asList(Atom.AtomType.STRING), true, coachContext, "BadgeClass");

    // execute command
    CySeCExecutorContextFactory.CySeCExecutorContext c = (CySeCExecutorContextFactory.CySeCExecutorContext) (coachContext.getContext());
    BadgeFactory.Badge b = c.getBadge(badgeName.getId());
    if (b == null) {
      throw new ExecutorException("Badge id "+badgeName.getId()+" doesn't exist");
    }
    b.awardBadgeClass(badgeClass.getId());

    return Atom.NULL_ATOM;
  }

}
