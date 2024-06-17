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

import eu.smesec.cysec.platform.bridge.execptions.CacheException;
import eu.smesec.cysec.platform.bridge.generated.Answer;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandIsSelected extends Command {

  public Atom execute(List<Atom> aList, CoachContext coachContext) throws ExecutorException {

    // expects 3 parameters: name, context of var and value
    checkNumParams(aList, 1);

    // evaluate parameters
    Atom varContent = checkAtomType(aList.get(0), Arrays.asList(Atom.AtomType.STRING), true, coachContext, "varContent");

    Answer answer = null;
    try {
      //            v
      //q110oNone
      // split id of "company-q10o4" to "q10o4"
      //Problem: question id might contain "o" thus splitting into more than 2 parts!
      String regex = "[^0-9]*[q]\\d+";
      Pattern pattern = Pattern.compile(regex);
      Matcher match = pattern.matcher(varContent.getId());

      if(match.find()) {
        String questionId = varContent.getId().substring(match.start(), match.end());

        // disassemble option into question and option by splitting with "o": q10o1
        answer = coachContext.getCal().getAnswer(coachContext.getFqcn().toString(), questionId);

      } else throw new ExecutorException("question id doesn't match pattern [^0-9]*[q]\\d+");

    } catch (CacheException e) {
      coachContext.getLogger().log(Level.SEVERE, String.format("Error loading answer %s", varContent.getId()));
    }

    // determine provided option is selected
    String boolResult;
    String ans=null;
    if(answer != null) {
      String vc=varContent.getId();
      ans=" "+(answer.getAidList() == null?answer.getText():answer.getAidList())+" ";
      if(ans.contains(vc)) {
        boolResult = "TRUE";
      } else {
        boolResult = "FALSE";
      }
    } else {
      ans="<UNSET>";
      boolResult = "FALSE";
    }
    coachContext.getLogger().info(String.format("isSelected(%s) == currently:%s ==> %s", varContent.getId(), ans, boolResult));

    return new Atom(Atom.AtomType.BOOL, boolResult, null);
  }

}
