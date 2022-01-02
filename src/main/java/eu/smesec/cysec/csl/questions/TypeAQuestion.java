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

import eu.smesec.cysec.platform.bridge.generated.Option;
import eu.smesec.cysec.platform.bridge.generated.Question;
import eu.smesec.cysec.csl.AbstractLib;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

/**
 * This question represents the a type questions. There are multiple options but only one may be selected at a time
 */
public class TypeAQuestion extends AstarQuestion {

    /**
     * New constructor for question creation.
     * @param question The question object from xml
     * @param lib The actual library that handles the coach
     */
    public TypeAQuestion(Question question, AbstractLib lib) {
        super(question, lib);

    }

    public TypeAQuestion(String id, String nextQid, boolean hide, Collection<LibSelectOption> options) {
        super(id, nextQid, hide, options);
    }

    public TypeAQuestion(String id, String nextQid, Collection<LibSelectOption> options) {
        this(id, nextQid, false, options);
    }


    @Override
    protected Modifier updateState(String optionId, String qid) {
        LibSelectOption o = findOption(optionId, qid);
        if (o == null) {
            throw new IllegalArgumentException("Cannot find option " + optionId + " in question " + this.getId());
        }

        Optional<LibSelectOption> selectedOptional = getOptions().entrySet().stream()
                .map(Map.Entry::getValue)
                .filter(LibSelectOption::isSelected)
                .findFirst();
        // deselect existing option and subtract score
        selectedOptional.ifPresent(selected -> {
            selected.setSelected(false);
        });

        // update state of selected option
        o.setSelected(true);

        String nqid = o.getNextQid();
        if (nqid == null) {
            nextQid = nextQidDefault;
        } else {
            nextQid = nqid;
        }
        return Modifier.SELECTED;
    }

    @Override
    public Optional<String> getSuccessor() {
        // there should only
        //next = getOptions().values().stream().filter(option -> option.isSelected()).findFirst().map(LibSelectOption::getId);
        return Optional.ofNullable(getNextQid());
    }

    /**
     * Necessary for likert question to add option prefix to selected value.
     * @param optionId the selected value
     * @param qid the id of the question
     * @return the LibSelectOption from metadata
     */
    LibSelectOption findOption(String optionId, String qid) {
        return getOptions().get(optionId);
    }
}
