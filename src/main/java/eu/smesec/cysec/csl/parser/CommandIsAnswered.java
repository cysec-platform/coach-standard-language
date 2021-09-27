/*-
 * #%L
 * CYSEC Standard Coach Language
 * %%
 * Copyright (C) 2020 - 2021 FHNW (University of Applied Sciences and Arts Northwestern Switzerland)
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

import java.util.List;

public class CommandIsAnswered extends Command {

  public Atom execute(List<Atom> aList, CoachContext coachContext) throws ExecutorException {

    // expects 3 parameters: name, context of var and value
    if (aList.size() != 1) {
      throw new ExecutorException("Invalid number of arguments. Expected 1 parameter.");
    }

    // evaluate parameters
    Atom varContent = aList.get(0).execute(coachContext);

    // assert type of parameters
    if (varContent.getType() != Atom.AtomType.STRING) {
      throw new ExecutorException("Invalid types for parameters: Provide [0] String");
    }

    // determine provided option is selected
    ILibCal cal = coachContext.getCal();
    Answer answer = null;
    try {
      // Attention: Use question ID instead of question! getAnswer accepts Object.
      // Answer object in CoachContext is answer of evaluated question, isAnswered may be executed for another question
      // which is not in the current context.
      answer = cal.getAnswer(coachContext.getFqcn().toString(), varContent.getId());
    } catch (CacheException e) {
        throw new NullPointerException();
    }
    String boolResult;
    if(answer != null) {
      boolResult = "TRUE";
    } else {
      boolResult = "FALSE";
    }

    return new Atom(Atom.AtomType.BOOL, boolResult, null);
  }

}
