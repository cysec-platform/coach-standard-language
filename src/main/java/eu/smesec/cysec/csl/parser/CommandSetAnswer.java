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
import eu.smesec.cysec.platform.bridge.generated.Option;
import eu.smesec.cysec.platform.bridge.generated.Question;
import java.util.Arrays;
import java.util.List;

public class CommandSetAnswer extends Command {

  public Atom execute(List<Atom> aList, CoachContext coachContext) throws ExecutorException {

    // expects 3 parameters: name, context of var and value
    checkNumParams(aList, 2);

    // evaluate parameters
    Atom questionId = checkAtomType(aList.get(0), Arrays.asList(AtomType.STRING), true, coachContext, "questionID");
    Atom answerValue = checkAtomType(aList.get(1), Arrays.asList(AtomType.STRING, AtomType.NULL), true, coachContext, "answerValue");

    // determine provided option is selected
    ILibCal cal = coachContext.getCal();
    try {
      Answer answer = cal.getAnswer(coachContext.getFqcn().toString(), questionId);

      // set value
      answer.setAidList(answerValue.getId());
      // FIXME: writing does not work

    } catch( CacheException e) {
      throw new ExecutorException("error while setting answer of question");
    }

    return Atom.NULL_ATOM;
  }

}
