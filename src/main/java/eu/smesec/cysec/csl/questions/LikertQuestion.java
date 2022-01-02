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
package eu.smesec.cysec.csl.questions;

import eu.smesec.cysec.platform.bridge.generated.Question;
import eu.smesec.cysec.csl.AbstractLib;

/**
 * This question represents the a likert type questions. out of multiple numeric options only one be may selected.
 */
public class LikertQuestion extends TypeAQuestion {

    /**
     * Constructor for question creation.
     * @param question The question object from xml
     * @param lib The actual library that handles the coach
     */
    public LikertQuestion(Question question, AbstractLib lib) {
        super(question, lib);
    }

    /**
     * Needs to overwrite super method as likert answer only return number value 1-5.
     * @param optionId the selected likert scale value
     * @param qid The id of the question
     * @return The question id with "o" prefix and selected value
     */
    @Override
    LibSelectOption findOption(String optionId, String qid) {
        return getOptions().get(qid + "o" + optionId);
    }
}
