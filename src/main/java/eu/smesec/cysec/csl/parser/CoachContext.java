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
package eu.smesec.cysec.csl.parser;

import eu.smesec.cysec.platform.bridge.FQCN;
import eu.smesec.cysec.platform.bridge.ILibCal;
import eu.smesec.cysec.platform.bridge.generated.Answer;
import eu.smesec.cysec.platform.bridge.generated.Option;
import eu.smesec.cysec.platform.bridge.generated.Question;
import eu.smesec.cysec.platform.bridge.generated.Questionnaire;

import java.util.Optional;
import java.util.logging.Logger;

/**
 * Capsules the context information needed for a command to execute
 */
public class CoachContext {
  private ExecutorContext context;
  private ILibCal cal;
  private Question questionContext;
  private Optional<Answer> answerContext;
  private Questionnaire coach;
  private Logger logger;
  private FQCN fqcn;


  public CoachContext(ExecutorContext context, ILibCal cal, Question questionContext, Optional<Answer> answerContext, Questionnaire coach, FQCN fqcn) {
    this.context = context;
    this.cal = cal;
    this.questionContext = questionContext;
    this.answerContext = answerContext;
    this.coach = coach;
    this.fqcn = fqcn;
  }

  public FQCN getFqcn() {
    return fqcn;
  }

  public Optional<Answer> getAnswerContext() {
    return answerContext;
  }

  public ExecutorContext getContext() {
    return context;
  }

  public void setContext(ExecutorContext context) {
    this.context = context;
  }

  public ILibCal getCal() {
    return cal;
  }

  public Question getQuestionContext() {
    return questionContext;
  }

  public Logger getLogger() {
    return logger;
  }

  public void setLogger(Logger logger) {
    this.logger = logger;
  }

  public Questionnaire getCoach() {
    return coach;
  }

  public CoachContext copy() {
    CoachContext coachContext = new CoachContext(context, cal, questionContext, answerContext, coach, fqcn);
    coachContext.setLogger(logger);
    return coachContext;
  }

}
