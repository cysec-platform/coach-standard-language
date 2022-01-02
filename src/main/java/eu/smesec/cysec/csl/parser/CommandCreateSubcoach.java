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

import eu.smesec.cysec.platform.bridge.CoachLibrary;
import eu.smesec.cysec.platform.bridge.execptions.CacheException;
import eu.smesec.cysec.platform.bridge.generated.Questionnaire;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

/**
 * Invokes the creation of a new coach instance on the platform
 *
 * <p>The coach with the given ID must exist in the coach directory of the platform,
 * otherwise the command throws an ExecutorException.
 *
 * <p>Syntax: createSubcoach("coachId", "sub-id")</p>
 * <p>Example: createSubcoach("lib-subcoach-backup", "www.test.ch")</p>
 */
public class CommandCreateSubcoach extends Command {

  @Override
  public Atom execute(List<Atom> list, CoachContext coachContext) throws ExecutorException {
    if (list.size() != 2) {
      throw new ExecutorException("Invalid number of arguments. Expected 2 parameters.");
    }
    // evaluate parameters
    Atom coachId = list.get(0).execute(coachContext);
    Atom fileIdentifier = list.get(1).execute(coachContext);

    if (coachId.getType() != Atom.AtomType.STRING) {
      throw new ExecutorException("Coach ID must be of type STRING");
    }
    if (fileIdentifier.getType() != Atom.AtomType.STRING) {
      throw new ExecutorException("Subcoach segment must be of type STRING");
    }
    try {
      Questionnaire subcoach = coachContext.getCal().getCoach(coachId.getId());
      if (subcoach == null) {
        throw new ExecutorException("Coach id " + coachId.getId() +" does not exist");
      }
      // Append current coach id to segment: e.g lib-company.lib-subcoach-backup
      Set<String> segment = new HashSet<>();
      segment.add(fileIdentifier.getId());
      coachContext.getLogger().info("Creating subcoach " + subcoach.getId() + "" + fileIdentifier.getId());
      // pass FQCN of parent. CoachContext contains the fqcn of the current coach, which is the parent.
      coachContext.getCal().instantiateSubCoach(subcoach, segment);

      // set parent context of new subcoach
      CoachLibrary subcoachLibrary = coachContext.getCal().getLibraries(subcoach.getId()).get(0);
      coachContext.getLogger().info("Setting " + coachContext.getCoach().getId() + " as parent for " + subcoach.getId());

      subcoachLibrary.setParent(coachContext.getContext());
    } catch (CacheException e) {
      coachContext.getLogger().log(Level.SEVERE, "Couldn't create subcoach via CAL", e);
      // setup coach relation for already existing coaches
      try {
        Questionnaire subcoach = coachContext.getCal().getCoach(coachId.getId());
        CoachLibrary subcoachLibrary = coachContext.getCal().getLibraries(subcoach.getId()).get(0);
        subcoachLibrary.setParent(coachContext.getContext());
      } catch (CacheException ex) {
        coachContext.getLogger().log(Level.SEVERE, "Error trying to setup parent relation for existing coach", e);
      }
    }

    return null;
  }

}
