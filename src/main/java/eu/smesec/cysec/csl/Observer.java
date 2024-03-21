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
package eu.smesec.cysec.csl;

import eu.smesec.cysec.csl.questions.LibQuestion;
import eu.smesec.cysec.csl.questions.Modifier;

import java.util.Collection;
import java.util.function.BiConsumer;

/**
 * Aims to unify the behaviour of the observing Objects within the Framework
 */
public interface Observer {
    /**
     * Pass the information from the observed object to the consumer function.
     * @param mod The modifier indicating (De)Selection/Update
     * @param data The changed question option
     */
    void update(Modifier mod, String data);

    /**
     * Register an action to be invoked on the observed question
     * @param action The action to perform upon update()
     * @param lib The lib to handle
     * @param questions The observed object
     */
    @Deprecated
    void init(BiConsumer<Modifier, String> action, AbstractLib lib, Collection<LibQuestion> questions);

    /**
     * Init method for Version two of the framework. This method is intended to be used for questions created
     * using reflection. It takes no lib parameter as this is passed via constructor of the question
     *
     * @param action The action to perform upon update()
     * @param questions The observed object
     */
    void init(BiConsumer<Modifier, String> action, Collection<String> questions);
}
