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
package eu.smesec.cysec.csl.demo.questions;

import eu.smesec.cysec.platform.bridge.generated.Question;
import eu.smesec.cysec.csl.AbstractLib;
import eu.smesec.cysec.csl.questions.Modifier;
import eu.smesec.cysec.csl.questions.SelectQuestion;

import java.util.Arrays;

public class Q100 extends SelectQuestion {

    public Q100(Question question, AbstractLib lib) {
        super(question, lib);

        init((modifier, optionId) -> { // Enable if q10o1 selected
            if (optionId.equals("q70o1")) {
                if(modifier.equals(Modifier.SELECTED)) {
                    lib.getLogger().fine(String.format("enabling %s", question.getId()));
                    setHide(false);
                } else {
                    setHide(true);
                    lib.getLogger().fine(String.format("disabling %s", question.getId()));

                }
            }
        }, Arrays.asList(("q70")));
    }

}
