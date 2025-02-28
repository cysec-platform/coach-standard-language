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

import eu.smesec.cysec.platform.bridge.generated.Question;

import java.util.List;

/**
 * This command modifies the display status of a question from hidden to show.
 *
 * <p>Remember to add a condition to hide a question again, if it should not be displayed all the
 * time once it is unhidden</p>
 *
 * <p>Syntax: setHidden(id, hidden);</p>
 * <p>Example: setHidden("q20", FALSE);</p>
 */
public class CommandSetHidden extends Command {

  public Atom execute(List<Atom> aList, CoachContext coachContext) throws ExecutorException {

    // expects 2 parameters: name and hidden state
    checkNumParams(aList, 2);

    // evaluate parameters
    Atom questionID = checkAtomType(aList.get(0), Atom.AtomType.STRING, true, coachContext, "questionID");
    Atom varContentBool = checkAtomType(aList.get(1), Atom.AtomType.BOOL, true, coachContext, "hideState");

    // Update question hidden status
    Question question = coachContext.getCoach().getQuestions().getQuestion().stream()
        .filter(question1 -> question1.getId().equals(questionID.getId()))
        .findFirst()
        .orElseThrow(
            () -> new ExecutorException("Question id " + questionID.getId() + " doesn't exist"));
    if(question.isHidden() != Boolean.parseBoolean(varContentBool.getId())) {
      coachContext.getLogger().info(String.format("question %s is new set to hidden=%s (setHidden)", question.getId(), varContentBool.getId()));
      question.setHidden(Boolean.parseBoolean(varContentBool.getId()));
    }

    return Atom.NULL_ATOM;
  }
}
