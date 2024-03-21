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
package eu.smesec.cysec.csl.questions;

import eu.smesec.cysec.platform.bridge.generated.Option;
import eu.smesec.cysec.platform.bridge.generated.Question;
import eu.smesec.cysec.platform.bridge.md.MetadataUtils;
import eu.smesec.cysec.csl.AbstractLib;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * This question represents the astar (aka multiple choice) type questions.
 */
public class AstarQuestion extends LibQuestion {

    private Map<String, LibSelectOption> options = new HashMap<>();

    /**
     * <p>Constructor for builder.</p>
     *
     * @param question the question id
     * @param lib th library
     */
    public AstarQuestion(Question question, AbstractLib lib) {
        super(question, lib);
        for (Option option : question.getOptions().getOption()) {
            lib.getLogger().fine(String.format("Adding option %s to %s", option.getId(), question.getId()));
            LibSelectOption product = SelectOptionBuilderImpl.newInstance()
                    .setId(option.getId())
                    .setScore(getOptionScore(option.getId()))
                    .setNext(getOptionNext(option.getId())).build();
            options.put(option.getId(), product);
        }
    }

    /**
     * <p>Constructor for builder.</p>
     *
     * @param id the question id the
     * @param nextQid next question id th library
     * @param hide denotes f the question is hidden
     * @param options the current options file
     */
    public AstarQuestion(String id, String nextQid, boolean hide, Collection<LibSelectOption> options) {
        super(id, nextQid, hide);
        this.options = options.stream().collect(Collectors.toMap(LibSelectOption::getId, o -> o));
    }

    /**
     * <p>Constructor for builder.</p>
     *
     *
     * @param id the question id the
     * @param nextQid next question id th library
     * @param options the current options file
     */
    public AstarQuestion(String id, String nextQid, Collection<LibSelectOption> options) {
        this(id, nextQid, false, options);
    }


    @Override
    protected Modifier updateState(String optionId, String qid) {
        LibSelectOption o = options.get(optionId);
        if (o == null) {
            throw new IllegalArgumentException("Cannot find option " + optionId + " in question " + this.getId());
        }

        if (o.isSelected()) {// deselected
//            nextQid = nextQidDefault;
            o.setSelected(false);
            return Modifier.UNSELECTED;
        } else {//selected
//            String nqid = o.getNextQid();
            o.setSelected(true);
//            if (nqid != null) {
//                nextQid = nqid;
//            }
            return Modifier.SELECTED;
        }
    }

    /**
     * Once at least one option is selected, the question is considered answered.
     *
     * @return true if there is a selected option, false if there are none
     */
    @Override
    public boolean isAnswered() {
        return getOptions().values().stream().anyMatch(option -> option.isSelected());
    }

    public Map<String, LibSelectOption> getOptions() {
        return options;
    }

    /**
     * Utility method to access the score value of an option
     *
     * @param optionId The option id.
     * @return The mvalue from the xml or 0 as default
     */
    public int getOptionScore(String optionId) {
        MetadataUtils.SimpleMvalue scoreOptional = searchMvalue("scores." + optionId);
        return (scoreOptional == null) ? 0 : Integer.parseInt(scoreOptional.getValue());
    }

    /**
     * Utility method to access the nextQid value of an a question
     *
     * @param id the id of th question
     * @return the next question id or null if none
     */
    public String getOptionNext(String id) {
        MetadataUtils.SimpleMvalue scoreOptional = searchMvalue("nextQid." + id);
        return (scoreOptional == null) ? null : scoreOptional.getValue();
    }

    @Override
    public Optional<String> getSuccessor() {
        Optional<String> next = Optional.empty();
        List<String> nextMap = new LinkedList<>();
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
                .collect(Collectors.joining(","))) : next;
    }
}
