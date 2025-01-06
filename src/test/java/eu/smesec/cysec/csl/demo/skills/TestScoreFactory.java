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
package eu.smesec.cysec.csl.demo.skills;

import eu.smesec.cysec.csl.parser.CySeCExecutorContextFactory;
import eu.smesec.cysec.csl.parser.ExecutorContext;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class TestScoreFactory {

  @Test
  public void testBasicFunctionality() {
    ExecutorContext context = CySeCExecutorContextFactory.getExecutorContext("test");
    context.reset();

    // testing capsulation of scores
    context.getScore("test1").add("qid1",1);
    context.getScore("test2").add("qid1",2);
    assertTrue("value of score test1 is bad (1)",context.getScore("test1").getValue()==1);
    assertTrue("value of score test2 is bad (1)",context.getScore("test2").getValue()==2);
    context.getScore("test1").reset();
    assertTrue("value of score test1 is bad (2)",context.getScore("test1").getValue()==0);
    assertTrue("value of score test2 is bad (2; should:2; is:"+context.getScore("test1").getValue()+")",context.getScore("test2").getValue()==2);
    context.getScore("test2").reset();
    assertTrue("value of score test1 is bad (3)",context.getScore("test1").getValue()==0);
    assertTrue("value of score test2 is bad (3)",context.getScore("test2").getValue()==0);

    // testing adding of scores
    context.reset();
    context.getScore("test1").add("qid1",1);
    context.getScore("test1").add("qid2",2);
    context.getScore("test1").add("qid2",3);
    assertTrue("value of score test1 is bad (4; should:6; is:"+context.getScore("test1")+")",context.getScore("test1").getValue()==6);

    // testing capping of scores
    context.reset();
    context.getScore("test1").add("qid1",1);
    context.getScore("test1").cap("qid1",2);
    context.getScore("test1").add("qid2",2);
    context.getScore("test1").add("qid2",3);
    context.getScore("test1").cap("qid1",3);
    assertTrue("value of score test1 is bad (5; should:2; is:"+context.getScore("test1")+")",context.getScore("test1").getValue()==2);

    // testing score Reversion of question
    context.reset();
    context.getScore("test1").add("qid1",1);
    context.getScore("test1").add("qid2",2);
    context.getScore("test1").add("qid3",3);
    assertTrue("value of score before reversion test1 is bad (6; should:6; is:"+context.getScore("test1").getValue()+")",context.getScore("test1").getValue()==6);
    context.revertQuestionScore("qid2");
    assertTrue("value of score after reversion test1 is bad (6; should:4; is:"+context.getScore("test1").getValue()+")",context.getScore("test1").getValue()==4);

  }
}
