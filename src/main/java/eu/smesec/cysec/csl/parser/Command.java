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
import eu.smesec.cysec.platform.bridge.execptions.CacheException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Command {

  private static Map<String, Command> commands = new HashMap<>();

  static {
    registerCommands();
  }

  public static void registerCommands() {
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

  protected int numberOfNormalizedParams = -1; /* -1 denotes "all" */

  public int getNumberOfNormalizedParams() {
    return numberOfNormalizedParams;
  }

  public Atom execute(List<Atom> list, CoachContext coachContext, ExecutorContext eContext)
      throws ExecutorException {
    CoachContext cc = coachContext.copy();
    cc.setContext(eContext);
    return execute(list, cc);
  }

  public Atom checkAtomType(Atom atom, List<AtomType> type, boolean evaluate, CoachContext context,
      String parameterName)
      throws ExecutorException {
    if (atom == null) {
      throw new RuntimeException("SEVERE ERROR: ATOM was passed as null when checking atom type... please check calling function");
    }
    // evaluate once if required and allowed
    if (atom.getType() == AtomType.METHODE && evaluate) {
      atom = atom.execute(context);
    }

    // check for appropriate type
    if (!type.contains(atom.getType())) {

      // concatenate allowed types
      StringBuffer typeString = new StringBuffer();
      for (int i = 0; i < type.size(); i++) {
        if (i > 0 && i < type.size() - 1) {
          typeString.append(", ");
        } else if (i == type.size() - 1) {
          typeString.append(", or ");
        }
        typeString.append(type.get(i).name());
      }

      // build exception message
      String msg;
      if (parameterName != null && !"".equals(parameterName)) {
        msg = "Illegal type for Parameter " + parameterName + " (should: " + typeString + "; was: "
            + atom.getType() + ")";
      } else {
        msg = "Illegal type for parameter (should:" + typeString + "; was: " + atom.getType() + ")";
      }

      // throw exception
      throw new ExecutorException(msg);
    }
    return atom;
  }

  public void checkNumParams(List<Atom> aList, int num) throws ExecutorException {
    checkNumParams(aList, num, num);
  }

  public void checkNumParams(List<Atom> aList, int lowNum, int highNum) throws ExecutorException {
    if (aList.size() > highNum || aList.size() < lowNum) {
      if (highNum == lowNum) {
        throw new ExecutorException(
            "Invalid number of arguments. Expected " + lowNum + " parameters but got "
                + aList.size() + " parameters in command " + getCommandName() + ".");
      } else {
        throw new ExecutorException(
            "Invalid number of arguments. Expected between " + lowNum + " and " + highNum
                + " parameters but got "
                + aList.size() + " parameters in command " + getCommandName() + ".");
      }
    }
  }

  public abstract Atom execute(List<Atom> list, CoachContext coachContext) throws ExecutorException;

}
