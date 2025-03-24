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

import java.util.List;
import java.util.Vector;

public sealed abstract class CommandAbstractBoolOp extends Command {

  @Override
  public Atom execute(List<Atom> list, CoachContext coachContext) throws ExecutorException {
    if (list == null || list.isEmpty()) {
      throw new ExecutorException("boolean operations require at least one argument");
    }
    List<Boolean> blist = new Vector<>();
    for (Atom atom : list) {
        try {
        // Check whether the atom is or evaluates to TRUE.
        blist.add(atom.isTrue(coachContext));
      } catch(ExecutorException e) {
        throw new ExecutorException("Exception while evaluating parameter " + atom + " in boolean op " + getCommandName(), e);
      }
    }
    return Atom.fromBoolean(evaluate(blist, coachContext.getContext()));
  }

  abstract boolean evaluate(List<Boolean> list, ExecutorContext context) throws ExecutorException;

  /**
   * {@code and(...args)} evaluates to {@link Atom#TRUE} if all arguments evaluate to {@link Atom#TRUE}.
   */
  public static final class CommandAnd extends CommandAbstractBoolOp {

    @Override
    boolean evaluate(List<Boolean> list, ExecutorContext context) {
      return list.stream().allMatch(Boolean::booleanValue);
    }
  }

  /**
   * {@code or(...args)} evaluates to {@link Atom#TRUE} if at least one argument evaluates to {@link Atom#TRUE}.
   */
  public static final class CommandOr extends CommandAbstractBoolOp {

    @Override
    boolean evaluate(List<Boolean> list, ExecutorContext context) {
      return list.stream().anyMatch(Boolean::booleanValue);
    }
  }

  /**
   * {@code not(arg)} evaluates to {@link Atom#TRUE} if its argument evaluates to {@link Atom#FALSE}.
   */
  public static final class CommandNot extends CommandAbstractBoolOp {

    @Override
    boolean evaluate(List<Boolean> list, ExecutorContext context) throws ExecutorException {
      if(list.size() != 1) {
        throw new ExecutorException("NOT supports only one parameter");
      }
      return !list.get(0);
    }
  }

  /**
   * {@code xor(...args)} evaluates to {@link Atom#TRUE} if exactly one argument evaluates to {@link Atom#TRUE}.
   */
  public static final class CommandXor extends CommandAbstractBoolOp {

    @Override
    boolean evaluate(List<Boolean> list, ExecutorContext context) {
      return list.stream().filter(Boolean::booleanValue).count() == 1L;
    }
  }
}
