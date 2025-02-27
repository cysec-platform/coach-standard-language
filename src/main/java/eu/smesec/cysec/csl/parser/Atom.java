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

import java.util.ArrayList;
import java.util.List;

public class Atom {

  public static final Atom NULL_ATOM = new Atom(AtomType.NULL, null, null);
  public static final Atom TRUE = new Atom(AtomType.BOOL, "TRUE", null);
  public static final Atom FALSE = new Atom(AtomType.BOOL, "FALSE", null);

  public enum AtomType {METHODE, INTEGER, FLOAT, BOOL, STRING, NULL}

  private final AtomType type;
  private final String id;
  private final List<Atom> parameters;
  private int parentPointer = 0;

  private Atom(AtomType type, String id, List<Atom> parameters) {
    this.type = type;
    this.id = id;
    this.parameters = parameters;
  }

  public static Atom fromCommand(String commandName, List<Atom> parameters) {
    return new Atom(AtomType.METHODE, commandName, parameters);
  }

  public static Atom fromInteger(int value) {
    return new Atom(AtomType.INTEGER, "" + value, null);
  }

  public static Atom fromFloat(double value) {
    return new Atom(AtomType.FLOAT, "" + value, null);
  }

  public static Atom fromBoolean(boolean b) {
    if(b) return Atom.TRUE;
    else return Atom.FALSE;
  }

  public static Atom fromString(String s) {
    return new Atom(Atom.AtomType.STRING, s, null);
  }

  private ExecutorContext getExecutorContext(CoachContext cc) {
    int i = parentPointer;
    ExecutorContext context = cc.getContext();
    while (i != 0 && context.getParent() != null) {
      context = context.getParent();
      i--;
    }
    return context;
  }

  public Atom execute(CoachContext coachContext) throws ExecutorException {
    return execute(coachContext, getExecutorContext(coachContext));
  }

  /**
   * Executes this Atom if it represents a method call, looking up the appropriate command.
   * If this is not a method call Atom, returns itself unchanged.
   *
   * @throws ExecutorException if a method name is unknown, or the evaluation fails otherwise.
   */
  private Atom execute(CoachContext coachContext, ExecutorContext context) throws ExecutorException {
    if (type == AtomType.METHODE) {
      // execute here
      Command command = Command.getCommand(id);
      if (command != null) {
        int numberOfNormalizedParams = command.getNumberOfNormalizedParams();

        // normalize parameter list as far as we can
        List<Atom> pl = new ArrayList<>(parameters.size());
        int i = 0;
        for (Atom a : parameters) {
          i++;
          // Execute the parameter up to the limit of normalized parameters
          if (a.getType() == AtomType.METHODE && i <= numberOfNormalizedParams) {
            a = a.execute(coachContext);
          }
          pl.add(a);
        }

        // execute command
        // FIXME: getExecutorContext(coachContext) is equivalent to the context parameter we have. Redundant?
        return command.execute(pl, coachContext, getExecutorContext(coachContext));
      } else {
        throw new ExecutorException("Tried executing unknown method: \"" + id + "\"");
      }
    } else {
      return this;
    }
  }

  public static boolean validateCommand(String name) {
    return Command.getCommand(name) != null;
  }

  public int setParent(int parentPointer) {
    int ret = this.parentPointer;
    this.parentPointer = parentPointer;
    return ret;
  }

  public AtomType getType() {
    return type;
  }

  public String getId() {
    return id;
  }

  public String toString() {
    String ret;
    switch (type) {
      case METHODE:
        StringBuilder methodCall = new StringBuilder(id);

        // If no parameters are present, simply "method()"
        if(parameters.isEmpty()) {
          methodCall.append("()");
        } else {
          // Parameters are present, so we do "method( arg1, arg2, arg3 )"
          methodCall.append("( ");
          boolean first = true;
          for (Atom a : parameters) {
            if(!first)
              methodCall.append(", ");
            methodCall.append(a.toString());
            first = false;
          }
          methodCall.append(" )");
        }
        ret = methodCall.toString();
        break;
      case STRING:
        ret = "\"" + id + "\"";
        break;
      case NULL:
        ret = "NULL";
        break;
      case INTEGER:
      case FLOAT:
      case BOOL:
        ret = id;
        break;
      default:
        throw new NullPointerException("type " + type + " cannot be printed (Not implemented)");
    }
    return ret;
  }

  /**
   * Evaluates this Atom and returns {@code true} if it results in the boolean atom {@link Atom#TRUE}.
   *
   * @throws ExecutorException if this is not a boolean atom (or a method that didn't return a boolean atom)
   */
  public boolean isTrue(CoachContext coachContext) throws ExecutorException {
    Atom eval = this.execute(coachContext);
    if (eval.getType() != AtomType.BOOL) {
      throw new ExecutorException("Condition \"" + this + "\" does not evaluate to BOOL (is: " + eval + ")");
    }
    if ("TRUE".equals(eval.getId())) {
      return true;
    } else if ("FALSE".equals(eval.getId())) {
      return false;
    } else {
      throw new ExecutorException("boolean value is illegal \"" + eval + "\" (OUCH! How did that happen)");
    }
  }
}
