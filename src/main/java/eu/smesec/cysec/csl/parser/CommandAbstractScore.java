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
import java.util.Arrays;
import java.util.List;

public abstract class CommandAbstractScore extends Command {

  public Atom execute(List<Atom> aList, CoachContext coachContext) throws ExecutorException {

    // expects 3 parameters: origin question id, score name and value
    checkNumParams(aList, 2);

    // evaluate parameters
    Atom scoreName = checkAtomType(aList.get(0), AtomType.STRING, true, coachContext, "scoreName");
    Atom scoreValue = checkAtomType(aList.get(1), Arrays.asList(AtomType.INTEGER, AtomType.FLOAT), true, coachContext, "scoreValue");

    // set the score
    score(scoreName.getId(), coachContext.getQuestionContext().getId(), Double.valueOf(scoreValue.getId()), coachContext.getContext());
    coachContext.getLogger().info( String.format("Adding %s to score %s in context %s", scoreValue.getId(), scoreName.getId(), coachContext.getContext()));

    return Atom.NULL_ATOM;
  }

  abstract void score(String scoreId, String questionId, double value, ExecutorContext context);

}
