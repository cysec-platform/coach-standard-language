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
package eu.smesec.cysec.csl.parser;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import eu.smesec.cysec.csl.parser.CySeCExecutorContextFactory.CySeCExecutorContext;
import eu.smesec.cysec.platform.bridge.FQCN;
import eu.smesec.cysec.platform.bridge.ILibCal;
import eu.smesec.cysec.platform.bridge.generated.Answer;
import eu.smesec.cysec.platform.bridge.generated.Dictionary;
import eu.smesec.cysec.platform.bridge.generated.DictionaryEntry;
import eu.smesec.cysec.platform.bridge.generated.Question;
import eu.smesec.cysec.platform.bridge.generated.Questionnaire;
import eu.smesec.cysec.platform.bridge.generated.Questions;
import java.util.Optional;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.Before;
import org.mockito.Mockito;

public class AbstractTestCommands {
  public CoachContext coachContext;
  public CySeCExecutorContext context;
  public Questionnaire coach;
  public ILibCal cal;
  public Question question;
  public Answer answer;
  public FQCN fqcn = FQCN.fromString("lib-user");

  @Before
  public void setup() throws Exception{
    cal = Mockito.mock(ILibCal.class);
    context = CySeCExecutorContextFactory.getExecutorContext("test");
    answer = new Answer();
    answer.setQid("user-q20");
    answer.setText("user-q20o1");
    question = new Question();
    question.setId("user-q20");
    Questions questions = new Questions();
    questions.getQuestion().add(question);
    Dictionary dictionary = new Dictionary();
    final DictionaryEntry dictionaryEntry = new DictionaryEntry();
    dictionaryEntry.setKey("key-abc");
    dictionaryEntry.setValue("Value ABC");
    dictionary.getEntry().add(dictionaryEntry);
    coach = Mockito.mock(Questionnaire.class);
    when(coach.getQuestions()).thenReturn(questions);
    when(coach.getDictionary()).thenReturn(dictionary);
    coachContext = new CoachContext(context, cal, question, Optional.ofNullable(answer), coach, fqcn);
    // pass global logger
    coachContext.setLogger(Logger.getGlobal());
  }

  @After
  public void tearDown() {
    context.reset();
    coachContext = null;

  }

}
