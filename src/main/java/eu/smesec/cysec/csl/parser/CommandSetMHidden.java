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
 * This command modifies the display status of multiple question from hidden to show (or vice
 * versa).
 *
 * <p>Remember to add a condition to hide a question again, if it should not be displayed all the
 * time once it is unhidden.</p>
 *
 * <p>Syntax: setMHidden(lowID, highID, hidden);</p>
 * <p>Example: setMHidden("q20", "q40", FALSE); // this updates all question ids starting with
 * "q20"
 * (inclusive) and "q40" (exclusive).</p>
 */
public class CommandSetMHidden extends Command {

  public Atom execute(List<Atom> aList, CoachContext coachContext) throws ExecutorException {

    // Three parameters expected: Lower name (inclusive), higher value (exclusive) and hiding value
    checkNumParams(aList, 3);

    // evaluate parameters
    Atom varLowId = checkAtomType(aList.get(0), Arrays.asList(Atom.AtomType.STRING), true, coachContext, "lowID");
    Atom varHighId = checkAtomType(aList.get(1), Arrays.asList(Atom.AtomType.STRING), true, coachContext, "highID");
    Atom varContentBool = checkAtomType(aList.get(2), Arrays.asList(Atom.AtomType.BOOL), true, coachContext,"hideState");
    coachContext.getLogger().info(String.format("Set questions in range from %s to %s to hidden=%s", varLowId.getId(), varHighId.getId(), varContentBool.getId()));

    // Update question hidden status
    int cnt = 0;
    for (Question question : coachContext.getCoach().getQuestions().getQuestion()) {
      coachContext.getLogger().info(String.format("    low:  %s?=%s=%s", varLowId.getId(),question.getId(), varLowId.getId().compareTo(question.getId())));
      coachContext.getLogger().info(String.format("    high: %s?=%s=%s", varHighId.getId(),question.getId(), varHighId.getId().compareTo(question.getId())));
      if (varLowId.toString().compareTo(question.getId()) <= 0
          && varHighId.getId().compareTo(question.getId()) > 0) {
        question.setHidden(Boolean.valueOf(varContentBool.getId()));
        coachContext.getLogger().info(String.format("  Set question %s to hidden=%s", question.getId(), varContentBool.getId()));
        cnt++;
      }
    }
    return new Atom(AtomType.INTEGER, "" + cnt, null);
  }

}