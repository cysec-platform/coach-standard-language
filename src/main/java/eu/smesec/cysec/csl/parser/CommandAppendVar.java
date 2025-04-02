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

import java.util.Arrays;
import java.util.List;

/**
 * {@code append("name", ["context",] "value")} appends the given string, optionally under the specified
 * context, to the specified variable. The context defaults to the question ID.
 */
public class CommandAppendVar extends Command {

  public Atom execute(List<Atom> aList, CoachContext coachContext) throws ExecutorException {
    // expects 2-3 parameters
    checkNumParams(aList, 2, 3);

    // evaluate parameters
    Atom varName = checkAtomType(aList.get(0), Atom.AtomType.STRING, true, coachContext, "variable name");
    Atom varContext;
    Atom varContent;
    if (aList.size() == 2) {
      // No explicit context.
      varContext = Atom.fromString(coachContext.getQuestionContext().getId());
      varContent = checkAtomType(aList.get(1), Atom.AtomType.STRING, true, coachContext, "value");
    } else {
      // Explicit context given.
      varContext = checkAtomType(aList.get(1), Arrays.asList(Atom.AtomType.STRING, Atom.AtomType.NULL), true, coachContext, "variable context");
      varContent = checkAtomType(aList.get(2), Atom.AtomType.STRING, true, coachContext, "value");
    }

    String context = varContext == Atom.NULL_ATOM ? null : varContext.getId();
    Atom oldValue = coachContext.getContext().getVariable(varName.getId(), context);
    // TODO: If old value was NULL, this prepends "null" to the varContent's text. This might cause issues?
    Atom newValue = Atom.fromString(oldValue.getId() + varContent.getId());

    // set the variable
    coachContext.getContext().setVariable(varName.getId(), newValue, context);
    coachContext.getLogger().info(String.format("Appended %s to variable %s in context %s.", varContent.getId(), varName.getId(), varContext.getId()));


    return Atom.NULL_ATOM;
  }
}
