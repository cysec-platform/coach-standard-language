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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * This question represents the astar (aka multiple choice) type questions.
 */
public class AstarexclQuestion extends AstarQuestion {

    /**
     * <p>Default constructor for new version of object instantiation.</p>
     *
     * @param question the question id
     * @param lib th library
     */
    public AstarexclQuestion(Question question, AbstractLib lib) {
        super(question, lib);
    }

    public AstarexclQuestion(String id, String nextQid, boolean hide, Collection<LibSelectOption> options) {
        super(id, nextQid, hide, options);
    }

    public AstarexclQuestion(String id, String nextQid, Collection<LibSelectOption> options) {
        this(id, nextQid, false, options);
    }


    @Override
    protected Modifier updateState(String optionId, String qid) {
        LibSelectOption o = getOptions().get(optionId);
        LibSelectOption OptionNone = getOptions().get(qid + "oNone");
        if (o == null) {
            throw new IllegalArgumentException("Cannot find option " + optionId + " in question " + this.getId());
        }

        // Same behaviour for all option when deselected
        if (o.isSelected()) {
//            nextQid = nextQidDefault;
            return Modifier.UNSELECTED;
        } else {
            // Case option none
            if(o.getId().equals(OptionNone.getId())) {
                // deselect all other
                getOptions().forEach((key, option) -> option.setSelected(false));
                o.setSelected(true);// only oNone active
            } else {
                // Case regular option
                OptionNone.setSelected(false);// throw out oNone
                o.setSelected(true);
            }
            return Modifier.SELECTED;
        }
    }

    @Override
    public Optional<String> getSuccessor() {
        Optional<String> next = Optional.empty();
        List<String> nextMap = new LinkedList<>();
        LibSelectOption optionNone = getOptions().get(getId() + "oNone");
        if(optionNone.isSelected()) {
            // Option none is selected, return its next or default
            return (optionNone.getNextQid() == null)
                    ? Optional.ofNullable(getDefaultNextQid())
                    : Optional.ofNullable(optionNone.getNextQid());

        } else {
            for (Map.Entry<String, LibSelectOption> entry : getOptions().entrySet()) {
                LibSelectOption option = entry.getValue();
                // Option selected and next qid present
                if (option.isSelected() && option.getNextQid() != null) {
                    Optional<String> candidate = Optional.ofNullable(option.getNextQid());
                    candidate.ifPresent(cand -> nextMap.add(candidate.get()));
                    // Case selected options points to default next
                } else {
                    // Make sure previously selected option doesn't overshadow new option
                    next = Optional.ofNullable(getDefaultNextQid());
                }
            }
            // return multiple options as string or default nextQid
            return nextMap.size() > 0 ? Optional.of(nextMap.stream()
                    .map(Object::toString)
                    .collect(Collectors.joining(","))) : Optional.ofNullable(getDefaultNextQid());
        }
    }
}

