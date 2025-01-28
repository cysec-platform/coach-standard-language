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
 * Adds a new "empty" badge to the {@link BadgeFactory}. This method must be executed before any Badge classes may be assigned with {@link CommandAddBadgeClass}
 * <p>Syntax: addBadge( badgeName, order, urlImg, altImg, description,urlLink );</p>
 *  <p>Example: addBadge( "ServerSavior", 1, "assets/images/serversavior.svg", "", "Not assigned yet", "lib-backup,q20");</p>
 *
 * @see CommandAddBadgeClass
 * @see CommandAwardBadge
 * @see CommandRevokeBadge
 *
 */
public class CommandAddBadge extends Command {

  @Override
  public Atom execute(List<Atom> list, CoachContext coachContext) throws ExecutorException {
    checkNumParams(list,6);

    // evaluate parameters
    Atom badgeName = checkAtomType(list.get(0),  Atom.AtomType.STRING, true, coachContext, "BadgeName" );
    Atom order = checkAtomType(list.get(1), Atom.AtomType.INTEGER,true,  coachContext, "BadgeClassName" );
    Atom urlImg = checkAtomType(list.get(2), Atom.AtomType.STRING,true,  coachContext, "ImageUrl");
    Atom altImg = checkAtomType(list.get(3), Atom.AtomType.STRING,true,  coachContext, "ImageDescription");
    Atom description = checkAtomType(list.get(4), Atom.AtomType.STRING,true,  coachContext,"Description");
    Atom urlLink = checkAtomType(list.get(5), Atom.AtomType.STRING,true,  coachContext,"urlLink");

    CySeCExecutorContextFactory.CySeCExecutorContext c = (CySeCExecutorContextFactory.CySeCExecutorContext) coachContext.getContext();
    BadgeFactory.Badge b = c.getBadge(badgeName.getId());
    if (b != null) {
      throw new ExecutorException("Badge id "+badgeName.getId()+" does already exist");
    }
    c.setBadge(new BadgeFactory.Badge(badgeName.getId(), Integer.valueOf(order.getId()), urlImg.getId(), altImg.getId(), description.getId(), urlLink.getId()));

    return null;
  }

}
