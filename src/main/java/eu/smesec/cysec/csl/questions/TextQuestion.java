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
import eu.smesec.cysec.platform.bridge.md.MetadataUtils;
import eu.smesec.cysec.csl.AbstractLib;

import java.util.Optional;

public class TextQuestion extends LibQuestion {
    private String content;
    private int scoreValue;

    public TextQuestion(Question question, AbstractLib lib) {
        super(question, lib);
        Optional<MetadataUtils.SimpleMvalue> scoreMeta = Optional.ofNullable(searchMvalue("scores.default"));

        if (scoreMeta.isPresent()) {
            scoreValue = Integer.valueOf(scoreMeta.get().getValue());
        } else {
            lib.getLogger().info("No score value available for question " + question.getId());
        }
    }

    @Override
    protected Modifier updateState(String data, String qid) {
        // When either text is added or removed, the answer is modified
        // Select/Unselect does not apply
        content = data;
        return Modifier.MODIFIED;
    }

    @Override
    public boolean isAnswered() {
        return (content != null && !content.isEmpty());
    }

    @Override
    public Optional<String> getSuccessor() {
        return Optional.ofNullable(getNextQid());
    }
}
