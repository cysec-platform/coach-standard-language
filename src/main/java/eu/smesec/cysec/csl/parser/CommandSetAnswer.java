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

import eu.smesec.cysec.csl.parser.Atom.AtomType;
import eu.smesec.cysec.platform.bridge.ILibCal;
import eu.smesec.cysec.platform.bridge.execptions.CacheException;
import eu.smesec.cysec.platform.bridge.generated.Answer;
import eu.smesec.cysec.platform.bridge.generated.Question;
import eu.smesec.cysec.platform.bridge.generated.QuestionType;

import java.util.EnumSet;
import java.util.List;

/**
 * Set an answer for another question by passing the question id as first parameter and the decired value
 * as second parameter.
 * <br><br>
 * <b>Astar questions:</b>
 * <br><br>
 * To select multiple values pass the option ids space seperated. Note that the answer value will
 * <b>overwrite</b> any existing values (so already selected options are not respected).
 */
public class CommandSetAnswer extends Command {

  public Atom execute(List<Atom> aList, CoachContext coachContext) throws ExecutorException {
    // expects 2 parameters: question id and answer id
    checkNumParams(aList, 2);

    // evaluate parameters
    Atom questionId = checkAtomType(aList.get(0), AtomType.STRING, true, coachContext, "question ID");
    Atom answerValue = checkAtomType(aList.get(1), AtomType.STRING, true, coachContext, "answer id(s)");

    String qid = questionId.getId();
    String value = answerValue.getId(); // use getId over toString since toString adds unnecessary " around the value

    // determine provided option is selected
    ILibCal cal = coachContext.getCal();

    try {
      Answer answer = cal.getAnswer(coachContext.getFqcn().toString(), qid);
      Question question = coachContext.getCoach().getQuestions().getQuestion().stream()
          .filter(q -> q.getId().equals(qid))
          .findAny()
          .orElseThrow(() -> new ExecutorException(String.format("question with id %s does not exist.", qid)));

      if (answer != null) {
        // update existing
        if (EnumSet.of(QuestionType.ASTAR, QuestionType.ASTAREXCL).contains(question.getType())) {
          answer.setAidList(value);
          answer.setText(value.split(" ")[0]);
        } else {
          answer.setText(value);
        }

        // FIXME call missing
        // cal.updateAnswer(coachContext.getFqcn().getCoachId(), answer);
      } else {
        // create new answer
        answer = new Answer();
        answer.setQid(qid);
        answer.setText(value);

        if (EnumSet.of(QuestionType.ASTAR, QuestionType.ASTAREXCL).contains(question.getType())) {
          answer.setAidList(value);
        }

        // FIXME call missing
        // cal.createAnswer(coachContext.getFqcn().getCoachId(), answer);
      }

    } catch (CacheException e) {
      throw new ExecutorException("error while setting answer of question");
    }

    return Atom.NULL_ATOM;
  }

}
