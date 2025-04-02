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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Command {

  private static final Map<String, Command> commands = new HashMap<>();

  static {
    registerCommands();
  }

  public static void registerCommands() {
    // TODO: null() resulting in "", while NULL resulting in the Null Atom, feels inconsistent.
    registerCommand("null", new CommandBlank());
    registerCommand("addScore", new CommandAddScore());
    registerCommand("capScore", new CommandCapScore());
    registerCommand("and", new CommandAnd());
    registerCommand("or", new CommandOr());
    registerCommand("not", new CommandNot());
    registerCommand("xor", new CommandXor());
    registerCommand("equals", new CommandEquals());
    registerCommand("greaterThan", new CommandGreaterThan());
    registerCommand("greaterThanOrEq", new CommandGreaterThanOrEquals());
    registerCommand("lowerThan", new CommandLowerThan());
    registerCommand("lowerThanOrEq", new CommandLowerThanOrEquals());
    registerCommand("if", new CommandIf());
    registerCommand("concat", new CommandConcat());
    registerCommand("contains", new CommandContains());
    registerCommand("set", new CommandSetVar());
    registerCommand("get", new CommandGetVar());
    registerCommand("append", new CommandAppendVar());
    registerCommand("setHidden", new CommandSetHidden());
    registerCommand("setMHidden", new CommandSetMHidden());
    registerCommand("setNext", new CommandNext());
    registerCommand("setAnswer", new CommandSetAnswer());
    registerCommand("isSelected", new CommandIsSelected());
    registerCommand("isAnswered", new CommandIsAnswered());
    registerCommand("addBadge", new CommandAddBadge());
    registerCommand("addBadgeClass", new CommandAddBadgeClass());
    registerCommand("awardBadge", new CommandAwardBadge());
    registerCommand("revokeBadge", new CommandRevokeBadge());
    registerCommand("addRecommendation", new CommandAddRecommendation());
    registerCommand("revokeRecommendation", new CommandRevokeRecommendation());
    registerCommand("createSubcoach", new CommandCreateSubcoach());
    registerCommand("print", new CommandPrint());
    registerCommand("tn", new CommandDictionaryLookup());
    registerCommand("arrayAdd", new CommandArrayAdd());
    registerCommand("arrayRemove", new CommandArrayRemove());
    registerCommand("arrayContains", new CommandArrayContains());
    registerCommand("arrayElements", new CommandArrayElements());
    registerCommand("arrayLength", new CommandArrayLength());
    registerCommand("removeSubcoach", new CommandRemoveSubcoach());
    registerCommand("getParentArgument", new CommandGetParentArgument());
  }

  public static void registerCommand(String commandName, Command command) {
    commands.put(commandName, command);
    command.setCommandName(commandName);
  }

  private String commandName = "UNKNOWN";

  private void setCommandName(String commandName) {
    this.commandName = commandName;
  }

  public String getCommandName() {
    return this.commandName;
  }

  public static Command getCommand(String commandName) {
    return commandName == null ? null : commands.get(commandName);
  }

  /**
   * Maximum amount of parameters that should be evaluated before evaluating this
   * command. The default value of {@link Integer#MAX_VALUE} ensures all parameters are maximally evaluated.
   */
  public int getNumberOfNormalizedParams() {
    return Integer.MAX_VALUE;
  }

  /**
   * Executes this Atom with the given list of parameter atoms, in the given contexts.
   */
  public Atom execute(List<Atom> list, CoachContext coachContext, ExecutorContext eContext)
      throws ExecutorException {
    CoachContext cc = coachContext.copy();
    cc.setContext(eContext);
    return execute(list, cc);
  }

  /**
   * Helper method that only checks for a singular Atom type, wrapping it in a Singleton List before passing
   * to {@link #checkAtomType(Atom, List, boolean, CoachContext, String)}.
   */
  public Atom checkAtomType(
          Atom atom, AtomType type, boolean evaluate, CoachContext context,
          String parameterName)
          throws ExecutorException {
    return checkAtomType(atom, Collections.singletonList(type), evaluate, context, parameterName);
  }

  /**
   * Verifies the given Atom is, or (if evaluate is true) evaluates to, one of the allowed atom types.
   * Returns the validated Atom (important if evaluation occurred).
   *
   * @throws ExecutorException if the atom or its evaluation result does not have one of the provided types.
   */
  public Atom checkAtomType(Atom atom, List<AtomType> types, boolean evaluate, CoachContext context,
      String parameterName)
      throws ExecutorException {
    if (atom == null) {
      throw new RuntimeException("SEVERE ERROR: ATOM was passed as null when checking atom type... please check calling function");
    }
    // evaluate if allowed
    if (evaluate) {
      atom = atom.execute(context);
    }

    // check for appropriate type
    if (!types.contains(atom.getType())) {

      // concatenate allowed types for exception message
      StringBuilder typeString = new StringBuilder();
      for (int i = 0; i < types.size(); i++) {
        // separate elements by ", " after the first.
        if (i > 0) {
          typeString.append(", ");
        }
        if (i == types.size() - 1) {
          // last one gets an additional "or " to its separator.
          typeString.append("or ");
        }
        typeString.append(types.get(i).name());
      }

      // build exception message
      String msg;
      if (parameterName != null && !parameterName.isEmpty()) {
        msg = String.format("Illegal type for Parameter \"%s\" (should be: %s; was: %s)", parameterName, typeString, atom.getType());
      } else {
        msg = String.format("Illegal type for Parameter (should be: %s; was: %s)", typeString, atom.getType());
      }

      // throw exception
      throw new ExecutorException(msg);
    }
    return atom;
  }

  /**
   * Verifies the amount of parameters in the list is exactly the given number.
   * @throws ExecutorException if there are fewer or more elements than are allowed.
   */
  public void checkNumParams(List<Atom> aList, int num) throws ExecutorException {
    checkNumParams(aList, num, num);
  }

  /**
   * Verifies the amount of parameters in the list is within the inclusive min-max range.
   * @throws ExecutorException if there are fewer or more elements than are allowed.
   */
  public void checkNumParams(List<Atom> aList, int minimum, int maximum) throws ExecutorException {
    if (aList.size() < minimum || maximum < aList.size()) {
      if (maximum == minimum) {
        throw new ExecutorException(
                String.format(
                        "Invalid number of arguments. Expected %d parameters but got %d parameters in command %s.",
                        minimum, aList.size(), getCommandName()
                )
        );
      } else {
        throw new ExecutorException(
                String.format(
                        "Invalid number of arguments. Expected between %d and %d parameters but got %d parameters in command %s.",
                        minimum, maximum, aList.size(), getCommandName()
                )
        );
      }
    }
  }

  public abstract Atom execute(List<Atom> list, CoachContext coachContext) throws ExecutorException;
}
