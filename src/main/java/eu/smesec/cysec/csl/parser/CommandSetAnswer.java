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
import eu.smesec.cysec.platform.bridge.ILibCal;
import eu.smesec.cysec.platform.bridge.execptions.CacheException;
import eu.smesec.cysec.platform.bridge.generated.Answer;
import eu.smesec.cysec.platform.bridge.generated.Question;

import java.util.Arrays;
import java.util.List;

public class CommandSetAnswer extends Command {

  public Atom execute(List<Atom> aList, CoachContext coachContext) throws ExecutorException {

    // expects 3 parameters: name, context of var and value
    checkNumParams(aList, 2);

    // evaluate parameters
    Atom questionId = checkAtomType(aList.get(0), Arrays.asList(AtomType.STRING), true, coachContext, "questionID");
    Atom answerValue = checkAtomType(aList.get(1), Arrays.asList(AtomType.STRING, AtomType.NULL), true, coachContext,
        "answerValue");

    if (answerValue.getType() != AtomType.STRING || questionId.getType() != AtomType.STRING) {
      // TODO probably thorw an exception?
    }

    String value = answerValue.getId(); // use getId over toString since toString adds unnecessary " around the value
    String qid = questionId.getId();

    // determine provided option is selected
    ILibCal cal = coachContext.getCal();

    try {
      Answer answer = cal.getAnswer(coachContext.getFqcn().toString(), qid);

      if (answer != null) {
        // update existing
        // TODO handle Astar

        answer.setText(value);
        cal.updateAnswer(coachContext.getFqcn().getCoachId(), answer);
      } else {
        // create new answer
        answer = new Answer();
        answer.setQid(qid);
        answer.setText(value);

        // TODO handle Astar
        // if (question.getType().startsWith("Astar")) {
        // answer.setAidList(value);
        // }

        cal.createAnswer(coachContext.getFqcn().getCoachId(), answer);
      }

    } catch (CacheException e) {
      throw new ExecutorException("error while setting answer of question");
    }

    return Atom.NULL_ATOM;
  }

}
