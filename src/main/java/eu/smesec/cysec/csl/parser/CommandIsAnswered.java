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

import eu.smesec.cysec.platform.bridge.ILibCal;
import eu.smesec.cysec.platform.bridge.execptions.CacheException;
import eu.smesec.cysec.platform.bridge.generated.Answer;
import eu.smesec.cysec.platform.bridge.generated.Question;

import java.util.List;

/**
 * {@code isAnswered(questionId)} checks whether the given question ID has been answered.
 * Hidden questions get treated as unanswered.
 *
 * @see CommandIsSelected
 */
public class CommandIsAnswered extends Command {

  public Atom execute(List<Atom> aList, CoachContext coachContext) throws ExecutorException {

    // expects 3 parameters: name, context of var and value
    checkNumParams(aList, 1);

    // evaluate parameters
    Atom questionId = checkAtomType(aList.get(0), Atom.AtomType.STRING, true, coachContext, "question ID");

    // Check if the question is even visible. If the question is hidden there's no way that it can be answered
    if (coachContext.getCoach().getQuestions().getQuestion().stream()
            .filter(q -> q.getId().equals(questionId.getId()))
            .findFirst()
            .map(Question::isHidden)
            .orElseThrow(() -> new ExecutorException("Question id " + questionId.getId() + " doesn't exist"))) {
      return Atom.FALSE;
    }

    // determine provided option is selected
    ILibCal cal = coachContext.getCal();
    Answer answer;
    try {
      // Attention: Use the inner Id of the questionId Atom. getAnswer accepts Object, unfortunately.
      // Answer object in CoachContext is answer of evaluated question, isAnswered may be executed for another question
      // which is not in the current context.
      answer = cal.getAnswer(coachContext.getFqcn().toString(), questionId.getId());
    } catch (CacheException e) {
      throw new NullPointerException("Could not get isAnswered state for: " + questionId.getId());
    }
    return Atom.fromBoolean(answer != null);
  }
}
