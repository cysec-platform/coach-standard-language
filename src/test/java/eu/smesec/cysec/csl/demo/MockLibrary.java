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
package eu.smesec.cysec.csl.demo;

import eu.smesec.cysec.csl.AbstractLib;
import eu.smesec.cysec.platform.bridge.ILibCal;
import eu.smesec.cysec.platform.bridge.generated.Answer;
import eu.smesec.cysec.platform.bridge.generated.Question;
import eu.smesec.cysec.platform.bridge.generated.Questionnaire;

/**
 * Test library for the framework. A library doesnt need any functionality, if all it does is use the frameworks
 * base features. Therefore all methods are empty implementations.
 */
public class MockLibrary extends AbstractLib {

    @Override
    protected void initHook(String id, Questionnaire questionnaire, ILibCal libCal) {}

    @Override
    protected void onBeginHook() {}

    @Override
    protected void onResumeHook(String qId) {}

    @Override
    protected void onResponseChangeHook(Question question, Answer answer) {}
}
