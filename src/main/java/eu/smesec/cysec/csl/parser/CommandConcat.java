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

import java.util.List;

public class CommandConcat extends Command {

  @Override
  public Atom execute(List<Atom> aList, CoachContext coachContext) throws ExecutorException {
    checkNumParams(aList, 1, Integer.MAX_VALUE);

    StringBuilder total = new StringBuilder();
    for (Atom a : aList) {
      // Execute the atom (no-op for non-method calls).
      a = a.execute(coachContext);
      // Append the atom's value to the builder.
      total.append(a.getType() == Atom.AtomType.STRING ? a.getId() : a.toString());
    }
    return Atom.fromString(total.toString());
  }
}
