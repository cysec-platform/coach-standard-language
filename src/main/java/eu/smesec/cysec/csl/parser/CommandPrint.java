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

public class CommandPrint extends Command {

    @Override
    public Atom execute(List<Atom> list, CoachContext coachContext) throws ExecutorException {
        // expects 0-1 parameters
        checkNumParams(list, 0, 1);

        if(list.size() == 0) {
            coachContext.getLogger().info("");

        } else {
            coachContext.getLogger().info(list.get(0).getId());

        }

        return new Atom(Atom.AtomType.NULL, null, null);
    }

}
