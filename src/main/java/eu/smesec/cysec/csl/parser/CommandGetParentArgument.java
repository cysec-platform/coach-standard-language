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

import eu.smesec.cysec.platform.bridge.execptions.CacheException;
import eu.smesec.cysec.platform.bridge.generated.Metadata;

import java.util.List;

public class CommandGetParentArgument extends Command {

  public Atom execute(List<Atom> a, CoachContext coachContext) {

    try {
      Metadata parentMetadata = coachContext.getCal().getMetadata(coachContext.getFqcn(), "subcoach-data");
      if (parentMetadata != null) {
        String value = parentMetadata.getMvalue().get(0).getStringValueOrBinaryValue().getValue();
        return new Atom(Atom.AtomType.STRING,value,null);
      }
    } catch (CacheException e) {
      coachContext.getLogger().severe("There was an error while executing command 'getParentArgument': " + e.getMessage());
    }
    return new Atom(Atom.AtomType.STRING,"",null);
  }

}
