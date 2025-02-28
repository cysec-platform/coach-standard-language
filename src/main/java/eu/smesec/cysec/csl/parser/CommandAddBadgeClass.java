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

import java.util.List;

/**
 * Adds a badge class to the {@link BadgeFactory}. This method must be executed before any Badges may be awarded with {@link CommandAwardBadge}
 * <p>Syntax: addBadgeClass( badgeName, className, order, urlImg, altImg, description,urlLink );</p>
 *  <p>Example: addBadgeClass( "ServerSavior", "Bronze", 1, "assets/images/serversaviorbronze.svg", "", "Superman of servers", "lib-backup,q10");</p>
 *
 * @see CommandAddBadge
 * @see CommandAwardBadge
 * @see CommandRevokeBadge
 *
 */
public class CommandAddBadgeClass extends Command {

  @Override
  public Atom execute(List<Atom> aList, CoachContext coachContext) throws ExecutorException {
    // expects 7 parameters
    checkNumParams(aList, 7);

    // evaluate parameters
    Atom badgeName = checkAtomType(aList.get(0), Atom.AtomType.STRING, true, coachContext, "BadgeName");
    Atom badgeClassName = checkAtomType(aList.get(1), Atom.AtomType.STRING, true, coachContext, "BadgeClass");
    Atom order = checkAtomType(aList.get(2), Atom.AtomType.INTEGER, true, coachContext,"BadgeOrder");
    Atom urlImg = checkAtomType(aList.get(3), Atom.AtomType.STRING, true, coachContext, "ImageURL");
    Atom altImg = checkAtomType(aList.get(4), Atom.AtomType.STRING, true, coachContext,"ImageAlternate" );
    Atom description = checkAtomType(aList.get(5), Atom.AtomType.STRING, true, coachContext,"Description");
    Atom urlLink = checkAtomType(aList.get(6), Atom.AtomType.STRING, true, coachContext,"LinkURL");

    CySeCExecutorContextFactory.CySeCExecutorContext c = (CySeCExecutorContextFactory.CySeCExecutorContext) (coachContext.getContext());
    BadgeFactory.Badge b = c.getBadge(badgeName.getId());
    if (b == null) {
      throw new ExecutorException("Badge id "+badgeName.getId()+" is not known");
    }
    b.addBadgeClass(new BadgeFactory.BadgeClass(badgeClassName.getId(), Integer.parseInt(order.getId()), urlImg.getId(), altImg.getId(), description.getId(), urlLink.getId()));

    return Atom.NULL_ATOM;
  }
}
