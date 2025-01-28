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

import eu.smesec.cysec.platform.bridge.FQCN;
import eu.smesec.cysec.platform.bridge.execptions.CacheException;

import java.util.*;
import java.util.logging.Level;

/**
 * Removes an existing sub-coach
 *
 * <p>Example: removeSubcoach("protocol-coach", "ssh")</p>
 */
public class CommandRemoveSubcoach extends Command {

  @Override
  public Atom execute(List<Atom> aList, CoachContext coachContext) throws ExecutorException {
    checkNumParams(aList, 2);

    // evaluate parameters
    Atom coachID = checkAtomType(aList.get(0), Atom.AtomType.STRING, true, coachContext, "coachID");
    Atom fileIdentifier = checkAtomType(aList.get(1), Atom.AtomType.STRING, true, coachContext, "fileIdentifier");

    FQCN fqcn = FQCN.fromString(String.join(".", Arrays.asList(coachContext.getCoach().getId(), coachID.getId(), fileIdentifier.getId())));

    try {
      coachContext.getCal().removeSubCoach(fqcn);
    } catch (CacheException e) {
      coachContext.getLogger().log(Level.SEVERE, "Error trying to remove sub-coach", e);
    }

    return null;
  }

}
