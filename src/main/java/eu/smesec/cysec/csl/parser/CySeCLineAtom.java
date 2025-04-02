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

import java.util.Arrays;
import java.util.List;
import java.util.Vector;

public class CySeCLineAtom {

  Atom cond;
  String name;
  Atom[] statements;

  public CySeCLineAtom(Atom cond, String name, Atom[] a) {
    this.cond = cond;
    this.name = name;
    this.statements = a;
  }

  public Atom getCond() {
    return cond;
  }

  public String getName() {
    return name;
  }

  public List<Atom> getStatements() {
    return Arrays.asList(statements);
  }

  public Atom execute(CoachContext coachContext) throws ExecutorException {
    Atom lastResult = Atom.TRUE;
    for (Atom a : getStatements()) {
      lastResult = a.execute(coachContext);
    }
    return lastResult;
  }
}
