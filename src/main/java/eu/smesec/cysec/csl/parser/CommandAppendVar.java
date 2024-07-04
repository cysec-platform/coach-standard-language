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
import java.util.Arrays;
import java.util.List;

public class CommandAppendVar extends Command {

  public Atom execute(List<Atom> aList, CoachContext coachContext) throws ExecutorException {

    // expects 2 or 3 parameters
    checkNumParams(aList, 2,2);

    // evaluate parameters
    Atom varName = aList.get(0).execute(coachContext);
    Atom varContent = checkAtomType(aList.get(1), Arrays.asList(Atom.AtomType.STRING), true, coachContext, "content");

    // set the variable
    coachContext.getContext().setVariable(varName.getId(),new Atom(AtomType.STRING,coachContext.getContext().getVariable(varName.getId(),null).getId()+varContent.getId(),null), null);
    coachContext.getLogger().info(String.format("Set variable %s to %s in context %s", varName.getId(), varContent.getId(), varContent.getId()));

    return Atom.NULL_ATOM;
  }

}
