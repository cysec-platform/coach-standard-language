/*-
 * #%L
 * CYSEC Standard Coach Language
 * %%
 * Copyright (C) 2020 - 2022 FHNW (University of Applied Sciences and Arts Northwestern Switzerland)
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
    registerCommand("if", new CommandIf());
    registerCommand("concat", new CommandConcat());
    registerCommand("set", new CommandSetVar());
    registerCommand("get", new CommandGetVar());
    registerCommand("setHidden", new CommandSetHidden());
    registerCommand("setNext", new CommandNext());
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
  }

  public static void registerCommand(String commandName, Command command) {
    commands.put(commandName, command);
  }

  public static Command getCommand(String commandName) {
    return commandName == null ? null : commands.get(commandName);
  }

  protected int numberOfNormalizedParams = -1; /* -1 denotes "all" */

  public int getNumberOfNormalizedParams() {
    return numberOfNormalizedParams;
  }

  public Atom execute(List<Atom> list, CoachContext coachContext, ExecutorContext eContext) throws ExecutorException {
    CoachContext cc = coachContext.copy();
    cc.setContext(eContext);
    return execute(list, cc);
  }

  public abstract Atom execute(List<Atom> list, CoachContext coachContext) throws ExecutorException;

}
