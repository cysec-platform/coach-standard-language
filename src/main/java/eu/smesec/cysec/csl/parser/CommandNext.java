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

import static eu.smesec.cysec.csl.parser.Atom.NULL_ATOM;

/**
 * This command signals the platform to display a certain question instead of the next one in sequence.
 *
 * <p>Note: Don't forget to "unhide" the question beforehand, otherwise the sequence is in inconsistent state.
 * The ExecutorContext clears all variables set by the question context before each evaluation. Therefore it is not necessary
 * to "unset" a variable.</p>
 *
 * <p>Syntax: setNext(id);</p>
 * <p>Example: setNext("q20");</p>
 * @see CommandSetHidden
 */
public class CommandNext extends Command {

  public Atom execute(List<Atom> aList, CoachContext coachContext) throws ExecutorException {
    // expects 1 parameter: next page string
    checkNumParams(aList, 1);

    // evaluate parameters
    Atom varContent = checkAtomType(aList.get(0), Atom.AtomType.STRING, true, coachContext, "Question ID");

    // set the next page
    coachContext.getContext().setVariable("_coach.nextPage", varContent, coachContext.getQuestionContext().getId());

    return NULL_ATOM;
  }

}
