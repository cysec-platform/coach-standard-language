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

import java.util.List;
import java.util.Vector;

public abstract class CommandAbstractBoolOp extends Command {

  private final static Atom TRUE = new Atom(Atom.AtomType.BOOL, "TRUE", null);
  private final static Atom FALSE = new Atom(Atom.AtomType.BOOL, "FALSE", null);

  @Override
  public Atom execute(List<Atom> list, CoachContext coachContext) throws ExecutorException {
    if (list == null || list.size() < 1) {
      throw new ExecutorException("boolean operations require at least one argument");
    }
    List<Boolean> blist = new Vector<>();
    for (Atom a : list) {
      String old = a.toString();
      try{
        if (a.getType() == Atom.AtomType.METHODE) {
          a = a.execute(coachContext);
        }
        blist.add(a.isTrue(coachContext));
      } catch(ExecutorException e) {
        throw new ExecutorException("Exception while evaluating parameter "+old+"in boolean op "+getCommandName(),e);
      }
    }
    if (evaluate(blist, coachContext.getContext())) {
      return TRUE;
    } else {
      return FALSE;
    }
  }

  abstract boolean evaluate(List<Boolean> list, ExecutorContext context) throws ExecutorException;
}
