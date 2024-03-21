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

import eu.smesec.cysec.csl.parser.Atom.AtomType;
import eu.smesec.cysec.platform.bridge.generated.Question;
import java.util.Arrays;
import java.util.List;

/**
 * This command checks if two atoms are equivalent in type and content.
 *
 * <p>Syntax: equals(atom1, atom2);</p>
 * <p>Example: setMHidden("q20", "q40"); // returns FALSE</p>
 */
public class CommandEquals extends Command {

  public Atom execute(List<Atom> aList, CoachContext coachContext) throws ExecutorException {

    // Three parameters expected: Lower name (inclusive), higher value (exclusive) and hiding value
    checkNumParams(aList, 2);

    // evaluate parameters
    Atom atom1 = aList.get(0).execute(coachContext);
    Atom atom2 = aList.get(1).execute(coachContext);

    // Check equivalence
    if(atom1.getType().equals(atom2.getType())) {
      if(AtomType.NULL==atom1.getType()) {
        return Atom.TRUE;
      } else if( atom1.getType()!=AtomType.METHODE ) {
        if(atom1.getId().equals(atom2.getId())) {
          return Atom.TRUE;
        } else {
          return Atom.FALSE;
        }
      } else {
        return Atom.FALSE;
      }
    } else {
      // types are inequal
      return Atom.TRUE;
    }
  }

}
