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

import eu.smesec.cysec.csl.skills.BadgeFactory;

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
  public Atom execute(List<Atom> list, CoachContext coachContext) throws ExecutorException {
    if (list.size() != 2) {
      throw new ExecutorException("Invalid number of arguments. Expected 2 parameters.");
    }

    // evaluate parameters
    Atom badgeName = list.get(0).execute(coachContext);
    Atom badgeClassName = list.get(1).execute(coachContext);

    // verify type of parameters
    if (badgeName.getType() != Atom.AtomType.STRING) {
      throw new ExecutorException("Badge name must be of type STRING");
    }
    if (badgeClassName.getType() != Atom.AtomType.STRING) {
      throw new ExecutorException("Badge class name must be of type STRING");
    }

    // execute command
    CySeCExecutorContextFactory.CySeCExecutorContext c = (CySeCExecutorContextFactory.CySeCExecutorContext) (coachContext.getContext());
    BadgeFactory.Badge b = c.getBadge(badgeName.getId());
    if (b == null) {
      throw new ExecutorException("Badge id "+badgeName.getId()+" doesn't exist");
    }
    b.awardBadgeClass(badgeClassName.getId());

    return Atom.NULL_ATOM;
  }

}
