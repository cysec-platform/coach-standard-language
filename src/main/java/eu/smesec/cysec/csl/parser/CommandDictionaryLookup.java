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

import eu.smesec.cysec.platform.bridge.generated.Dictionary;

import java.util.List;

/**
 * A command that replaces the provided key with the referenced entry in the coach dictionary
 */
public class CommandDictionaryLookup extends Command {

    @Override
    public Atom execute(List<Atom> args, CoachContext coachContext) throws ExecutorException {
        if (args == null || args.size() != 1) {
            throw new ExecutorException("The identity command requires exactly one argument");
        }

        // determine key of the entry to be found in the dictionary
        Atom a = args.get(0);
        if (a.getType() == Atom.AtomType.METHODE) {
            a = a.execute(coachContext);
        }
        if (a.getType() != Atom.AtomType.STRING) {
            throw new ExecutorException("Argument must be a STRING or evaluate to a STRING but was " + a.getType());
        }
        final String key = a.getId();
        if (key == null || key.isEmpty()) {
            return Atom.NULL_ATOM;
        }

        // handle non-existent dictionary
        final Dictionary dictionary = coachContext.getCoach().getDictionary();
        if (dictionary == null || dictionary.getEntry() == null) {
            return Atom.NULL_ATOM;
        }

        // extract value corresponding to key or return null-atom
        return dictionary.getEntry().stream()
                .filter(e -> key.equals(e.getKey()))
                .findFirst()
                .map(e -> new Atom(Atom.AtomType.STRING, e.getValue(), null))
                .orElse(Atom.NULL_ATOM);
    }
}
