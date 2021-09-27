/*-
 * #%L
 * CYSEC Standard Coach Language
 * %%
 * Copyright (C) 2020 - 2021 FHNW (University of Applied Sciences and Arts Northwestern Switzerland)
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

import java.util.Arrays;

/**
 * Represents the type A and YesNo question types. The questions only differ in their options text property. Otherwise
 * they behave exactly the same which is why there are no separate classes for A and YesNo.
 */
public class SelectQuestion extends AstarQuestion {
    private LibSelectOption optionYes;
    private LibSelectOption optionNo;


    /**
     * New constructor for question creation. It assumes that for all Type A questions, o1 is the "Yes"
     * and o2 is the "No" option.
     * @param question The question object from xml
     * @param lib The actual library that handles the coach
     */
    public SelectQuestion(Question question, AbstractLib lib) {
        super(question, lib);
        optionYes = getOptions().get(question.getId() + "o1");
        optionNo = getOptions().get(question.getId() + "o2");

    }

    public SelectQuestion(String id, String nextQid, boolean hide, LibSelectOption yes, LibSelectOption no) {
        super(id, nextQid, hide, Arrays.asList(yes, no));
        // convenience access to both options
        optionYes = yes;
        optionNo = no;
    }

    public SelectQuestion(String id, String nextQid, LibSelectOption yes, LibSelectOption no) {
        this(id, nextQid, false, yes, no);
    }

    /**
     * Only one option may be selected at a time.
     * @param optionId the option
     * @param qid the question
     * @return the Modifier indicating what kind of change occured.
     */
    @Override
    protected Modifier updateState(String optionId, String qid) {
        LibSelectOption o = getOptions().get(optionId);
        if (o == null) {
            throw new IllegalArgumentException("Cannot find option " + optionId + " in question " + this.getId());
        }

        // 'Yes' selected
        if (optionYes.getId().equals(optionId)) {
            optionYes.setSelected(true);
            optionNo.setSelected(false);
            // remove other option score
        } else { // 'No' selected
            optionNo.setSelected(true);
            optionYes.setSelected(false);
        }

        String nqid = o.getNextQid();
        if (nqid == null) {
            nextQid = getDefaultNextQid();
        } else {
            nextQid = nqid;
        }
        return Modifier.SELECTED;
    }
}
