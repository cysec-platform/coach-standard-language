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

import eu.smesec.cysec.platform.bridge.execptions.CacheException;
import eu.smesec.cysec.platform.bridge.generated.Answer;
import eu.smesec.cysec.platform.bridge.generated.Question;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandIsSelected extends Command {

  public Atom execute(List<Atom> aList, CoachContext coachContext) throws ExecutorException {
    // expects 1 parameter: answer id
    checkNumParams(aList, 1);

    // evaluate parameters
    Atom queriedAnswerId = checkAtomType(aList.get(0), Atom.AtomType.STRING, true, coachContext, "answer id");

    Answer answer = null;
    try {
      //            v
      //q110oNone
      // split id of "company-q10o4" to "q10o4"
      //Problem: question id might contain "o" thus splitting into more than 2 parts!
      String regex = "[^0-9]*[q]\\d+";
      Pattern pattern = Pattern.compile(regex);
      Matcher match = pattern.matcher(queriedAnswerId.getId());

      if(match.find()) {
        String questionId = queriedAnswerId.getId().substring(match.start(), match.end());

        // Check if question is hidden and if so immediately return false since answers of hidden questions cannot be selected
        if (coachContext.getCoach().getQuestions().getQuestion().stream()
                .filter(q -> q.getId().equals(questionId))
                .findFirst()
                .map(Question::isHidden)
                .orElseThrow(() -> new ExecutorException("Question id " + questionId + " doesn't exist"))) {
          return Atom.FALSE;
        }

        // find answer value for the parsed question id (company-q10o4 resulted in q10)
        answer = coachContext.getCal().getAnswer(coachContext.getFqcn().toString(), questionId);
      } else {
        throw new ExecutorException("question id doesn't match pattern [^0-9]*[q]\\d+: " + queriedAnswerId.getId());
      }

    } catch (CacheException e) {
      coachContext.getLogger().log(Level.SEVERE, String.format("Error loading answer %s", varContent.getId()));
    }

    // determine provided option is selected
    Atom result;
    String actualAnswerText;
    if(answer != null) {
      String queriedAnswerText = queriedAnswerId.getId();
      actualAnswerText = " " + (answer.getAidList() == null ? answer.getText() : answer.getAidList()) + " ";

      // don't use ans.contains directly to avoid unintended matches (e.g. q10HTTP should not match when q10HTTPS is choosen)
      result = Atom.fromBoolean(
              Arrays.asList(actualAnswerText.split(" ")).contains(queriedAnswerText)
      );
    } else {
      actualAnswerText = "<UNSET>";
      result = Atom.FALSE;
    }
    coachContext.getLogger().info(String.format("isSelected(%s) == currently:%s ==> %s", queriedAnswerId.getId(), actualAnswerText, result.getId()));

    return result;
  }
}
