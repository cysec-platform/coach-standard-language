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

import eu.smesec.cysec.csl.MetadataBuilder;
import eu.smesec.cysec.platform.bridge.CoachLibrary;
import eu.smesec.cysec.platform.bridge.execptions.CacheException;
import eu.smesec.cysec.platform.bridge.generated.Metadata;
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
  public Atom execute(List<Atom> aList, CoachContext coachContext) throws ExecutorException {
    // expects 2-3 parameters
    checkNumParams(aList, 2, 3);

    // evaluate parameters
    Atom coachID = checkAtomType(aList.get(0), Atom.AtomType.STRING, true, coachContext, "coachID");
    Atom fileIdentifier = checkAtomType(aList.get(1), Atom.AtomType.STRING, true, coachContext, "fileIdentifier");
    Atom parentArgument;
    if (aList.size() == 3) {
      parentArgument = checkAtomType(aList.get(2), Atom.AtomType.STRING, true, coachContext, "parentArgument");
    } else {
      parentArgument = Atom.NULL_ATOM;
    }

    try {
      Questionnaire subcoach = coachContext.getCal().getCoach(coachID.getId());
      if (subcoach == null) {
        throw new ExecutorException("Coach id " + coachID.getId() +" does not exist");
      }
      // Append current coach id to segment: e.g lib-company.lib-subcoach-backup
      Set<String> segment = new HashSet<>();
      segment.add(fileIdentifier.getId());
      coachContext.getLogger().info("Creating subcoach " + subcoach.getId() + "" + fileIdentifier.getId());
      // pass FQCN of parent. CoachContext contains the fqcn of the current coach, which is the parent.
      CoachLibrary subcoachLibrary = coachContext.getCal().getLibraries(subcoach.getId()).get(0);
      Metadata metadata = MetadataBuilder
              .newInstance(subcoachLibrary)
              .setMvalue("parent-argument", parentArgument.getId() == null ? "" : parentArgument.getId())
              .buildCustom("subcoach-data");
      coachContext.getCal().instantiateSubCoach(subcoach, segment, metadata);

      // set parent context of new subcoach
      coachContext.getLogger().info("Setting " + coachContext.getCoach().getId() + " as parent for " + subcoach.getId());

      subcoachLibrary.setParent(coachContext.getContext());
    } catch (CacheException e) {
      coachContext.getLogger().log(Level.SEVERE, "Couldn't create subcoach via CAL", e);
      // setup coach relation for already existing coaches
      try {
        Questionnaire subcoach = coachContext.getCal().getCoach(coachID.getId());
        CoachLibrary subcoachLibrary = coachContext.getCal().getLibraries(subcoach.getId()).get(0);
        subcoachLibrary.setParent(coachContext.getContext());
      } catch (CacheException ex) {
        coachContext.getLogger().log(Level.SEVERE, "Error trying to setup parent relation for existing coach", e);
      }
    }

    return null;
  }

}
