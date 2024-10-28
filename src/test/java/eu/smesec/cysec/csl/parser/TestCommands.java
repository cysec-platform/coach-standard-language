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

import eu.smesec.cysec.csl.parser.CySeCExecutorContextFactory.CySeCExecutorContext;
import eu.smesec.cysec.platform.bridge.FQCN;
import eu.smesec.cysec.platform.bridge.ILibCal;
import eu.smesec.cysec.platform.bridge.CoachLibrary;
import eu.smesec.cysec.platform.bridge.execptions.CacheException;
import eu.smesec.cysec.platform.bridge.generated.Answer;
import eu.smesec.cysec.platform.bridge.generated.Dictionary;
import eu.smesec.cysec.platform.bridge.generated.DictionaryEntry;
import eu.smesec.cysec.platform.bridge.generated.Question;
import eu.smesec.cysec.platform.bridge.generated.Questionnaire;
import eu.smesec.cysec.platform.bridge.generated.Questions;
import eu.smesec.cysec.csl.AbstractLib;
import eu.smesec.cysec.csl.demo.MockLibrary;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.anyString;

public class TestCommands extends AbstractTestCommands {
  @Test
  public void testBasicAddCommand() throws Exception {
    ExecutorContext context = CySeCExecutorContextFactory.getExecutorContext("test");
    context.reset();
    Command.registerCommand("add", new CommandAdd());
    Atom a = new ParserLine("add(5,5.5,add(-.25,-1.25));").getAtom();
    String expected = "add( 5, 5.5, add( -0.25, -1.25 ) )";
    assertTrue("String is not as expected \"" + a + "\"!=\"" + expected + "\"", expected.equals(a.toString()));
    assertTrue("Execution result is not as expected (" + a.execute(coachContext) + ")", "9.0".equals(a.execute(coachContext).toString()));
  }

  @Test
  @Ignore(value = "ToDo Aaron")
  public void testIsSelectedFalseCommand() throws Exception {
    //For testSelectedFalse
    when(coachContext.getCal().getAnswer(fqcn.toString(), coachContext.getQuestionContext())).thenReturn(null);

    ExecutorContext context = CySeCExecutorContextFactory.getExecutorContext("test");
    context.reset();
    Command.registerCommand("arrayAdd", new CommandArrayAdd());
    try {
      StringBuilder s = new StringBuilder();
      s.append("TRUE : bla :  {" + System.lineSeparator());
      s.append("                 arrayAdd(\"myScore\",\"100\");" + System.lineSeparator());
      s.append("                 arrayAdd(\"myScore\",\"100\");" + System.lineSeparator());
      s.append("              }; // This is a silly comment");
      System.out.println("testing " + s);
      List<CySeCLineAtom> l = new ParserLine(s.toString()).getCySeCListing();

      context.executeQuestion(l, coachContext);
      //Atom result = l.get(0).execute(coachContext);
      assertTrue(context.getScore("myScore").getValue() == 0);
    } catch (Exception pe) {
      pe.printStackTrace();
      fail("got unexpected exception " + pe);
    }
  }

  @Test
  public void testEqualsEmptyStrings() throws Exception {
    //For testSelectedFalse
    ExecutorContext context = CySeCExecutorContextFactory.getExecutorContext("test");
    context.reset();
    Command.registerCommand("equals", new CommandEquals());
    try {
      StringBuilder s = new StringBuilder();
      s.append("equals(\"\",\"\") : bla :  {" + System.lineSeparator());
      s.append("                 addScore(\"myScore\",100);" + System.lineSeparator());
      s.append("              };");
      List<CySeCLineAtom> l = new ParserLine(s.toString()).getCySeCListing();
      context.executeQuestion(l, coachContext);
      assertTrue(context.getScore("myScore").getValue() == 100);
    } catch (Exception pe) {
      pe.printStackTrace();
      fail("got unexpected exception " + pe);
    }
  }

  @Test
  public void testEqualsInequalStrings() throws Exception {
    //For testSelectedFalse
    ExecutorContext context = CySeCExecutorContextFactory.getExecutorContext("testInequalStrings");
    context.reset();
    Command.registerCommand("equals", new CommandEquals());
    try {
      StringBuilder s = new StringBuilder();
      s.append("equals(\"\",\"test\") : bla :  {" + System.lineSeparator());
      s.append("                 addScore(\"myScore\",100);" + System.lineSeparator());
      s.append("              };");
      List<CySeCLineAtom> l = new ParserLine(s.toString()).getCySeCListing();
      context.executeQuestion(l, coachContext);
      assertTrue(context.getScore("myScore").getValue() == 0);
    } catch (Exception pe) {
      pe.printStackTrace();
      fail("got unexpected exception " + pe);
    }
  }

  @Test
  public void testEqualsNull() throws Exception {
    ExecutorContext context = CySeCExecutorContextFactory.getExecutorContext("test");
    context.reset();
    Command.registerCommand("equals", new CommandEquals());
    try {
      StringBuilder s = new StringBuilder();
      s.append("equals(NULL,NULL) : bla :  {" + System.lineSeparator());
      s.append("                 addScore(\"myScore\",100);" + System.lineSeparator());
      s.append("              };");
      List<CySeCLineAtom> l = new ParserLine(s.toString()).getCySeCListing();
      context.executeQuestion(l, coachContext);
      assertEquals(100, context.getScore("myScore").getValue(),0.1);
    } catch (Exception pe) {
      pe.printStackTrace();
      fail("got unexpected exception " + pe);
    }
  }

  @Test
  public void testEqualsAgainstNull() throws Exception {
    ExecutorContext context = CySeCExecutorContextFactory.getExecutorContext("test");
    context.reset();
    Command.registerCommand("equals", new CommandEquals());
    try {
      StringBuilder s = new StringBuilder();
      s.append("equals(NULL,TRUE) : bla :  {" + System.lineSeparator());
      s.append("                 addScore(\"myScore\",100);" + System.lineSeparator());
      s.append("              };");
      List<CySeCLineAtom> l = new ParserLine(s.toString()).getCySeCListing();
      context.executeQuestion(l, coachContext);
      assertEquals(0, context.getScore("myScore").getValue(),0.1);
    } catch (Exception pe) {
      pe.printStackTrace();
      fail("got unexpected exception " + pe);
    }
  }

  @Test
  public void testEqualsNullAgainstFunction() throws Exception {
    ExecutorContext context = CySeCExecutorContextFactory.getExecutorContext("test");
    context.reset();
    Command.registerCommand("equals", new CommandEquals());
    Command.registerCommand("get", new CommandGetVar());
    try {
      StringBuilder s = new StringBuilder();
      s.append("equals(NULL,get(\"inexistent\")) : bla :  {" + System.lineSeparator());
      s.append("                 addScore(\"myScore\",100);" + System.lineSeparator());
      s.append("              };");
      List<CySeCLineAtom> l = new ParserLine(s.toString()).getCySeCListing();
      context.executeQuestion(l, coachContext);
      assertEquals(100, context.getScore("myScore").getValue(),0.1);
    } catch (Exception pe) {
      pe.printStackTrace();
      fail("got unexpected exception " + pe);
    }
  }

  // FIXME: equals tests are incomplete!

  @Test
  public void testSetVariable() throws Exception {
    ExecutorContext context = CySeCExecutorContextFactory.getExecutorContext("test");
    context.reset();
    Command.registerCommand("set", new CommandSetVar());
    Command.registerCommand("get", new CommandGetVar());
    try {
      StringBuilder s = new StringBuilder();
      s.append("TRUE : bla :  {" + System.lineSeparator());
      s.append("                 set(\"myVar\",100);" + System.lineSeparator());
      s.append("                 addScore(\"myScore\",get(\"myVar\"));" + System.lineSeparator());
      s.append("              }; // This is a silly comment");
      System.out.println("testing " + s);
      List<CySeCLineAtom> l = new ParserLine(s.toString()).getCySeCListing();

      context.executeQuestion(l, coachContext);
      //Atom result = l.get(0).execute(coachContext);
      assertTrue(context.getScore("myScore").getValue() == 100);
    } catch (Exception pe) {
      pe.printStackTrace();
      fail("got unexpected exception " + pe);
    }
  }

  @Test
  public void testGetVariableDefault() throws Exception {
    ExecutorContext context = CySeCExecutorContextFactory.getExecutorContext("test");
    context.reset();
    Command.registerCommand("set", new CommandSetVar());
    Command.registerCommand("get", new CommandGetVar());
    try {
      StringBuilder s = new StringBuilder();
      s.append("TRUE : bla :  {" + System.lineSeparator());
      s.append("                 addScore(\"myScore\",get(\"myVar\", 100 )); // the variable is unset... should return default value" + System.lineSeparator());
      s.append("              };");
      System.out.println("testing " + s);
      List<CySeCLineAtom> l = new ParserLine(s.toString()).getCySeCListing();

      context.executeQuestion(l, coachContext);
      //Atom result = l.get(0).execute(coachContext);
      assertTrue(context.getScore("myScore").getValue() == 100);
    } catch (Exception pe) {
      pe.printStackTrace();
      fail("got unexpected exception " + pe);
    }
  }

  @Test
  public void test() {
    String content = "company-q10oNone";
    String content2 = "company-q10o1";
    String expected = "company-q10";

    String regex = "[^0-9]*[q]\\d+";

    Pattern pattern = Pattern.compile(regex);
    Matcher match = pattern.matcher(content);
    match.find();
    Assert.assertEquals(expected, content.substring(match.start(),match.end()));
    Matcher match2 = pattern.matcher(content2);
    match2.find();
    Assert.assertEquals(expected, content2.substring(match2.start(),match2.end()));
  }

  @Test
  @Ignore(value = "ToDo Aaron")
  public void testIsSelectedTrueCommand() throws Exception {
    Answer answerQ20 = new Answer();
    answerQ20.setText("user-q20o1");
    // Override answer in coachContext
    coachContext = new CoachContext(context, cal, question, Optional.ofNullable(answerQ20), coach, fqcn);
    coachContext.setLogger(Logger.getGlobal());
    when(coachContext.getCal().getAnswer(fqcn.toString(), "user-q20")).thenReturn(answerQ20);

    ExecutorContext context = CySeCExecutorContextFactory.getExecutorContext("test");
    context.reset();
    Command.registerCommand("isSelected", new CommandIsSelected());
    try {
      StringBuilder s = new StringBuilder();
      s.append("isSelected(\"user-q20o1\") : bla :  {" + System.lineSeparator());
      s.append("                 addScore(\"myScore\",100);" + System.lineSeparator());
      s.append("              }; // This is a silly comment");
      System.out.println("testing " + s);
      List<CySeCLineAtom> l = new ParserLine(s.toString()).getCySeCListing();

      context.executeQuestion(l, coachContext);
      //Atom result = l.get(0).execute(coachContext);
      assertTrue(context.getScore("myScore").getValue() == 100);
    } catch (Exception pe) {
      pe.printStackTrace();
      fail("got unexpected exception " + pe);
    }
  }

  @Test
  @Ignore(value = "ToDo Aaron")
  public void testIsSelectedMultiOptionsCommand() throws Exception {
    Answer answerQ70 = new Answer();
    answerQ70.setAidList("user-q70o1 user-q70o2");
    coachContext = new CoachContext(context, cal, question, Optional.ofNullable(answerQ70), coach, fqcn);
    coachContext.setLogger(Logger.getGlobal());
    when(coachContext.getCal().getAnswer(fqcn.toString(), "user-q70")).thenReturn(answerQ70);

    ExecutorContext context = CySeCExecutorContextFactory.getExecutorContext("test");
    context.reset();
    Command.registerCommand("isSelected", new CommandIsSelected());
    try {
      StringBuilder s = new StringBuilder();
      s.append("isSelected(\"user-q70o1\") : bla :  {" + System.lineSeparator());
      s.append("                 addScore(\"myScore\",100);" + System.lineSeparator());
      s.append("              }; // This is a silly comment");
      System.out.println("testing " + s);
      List<CySeCLineAtom> l = new ParserLine(s.toString()).getCySeCListing();

      context.executeQuestion(l, coachContext);
      //Atom result = l.get(0).execute(coachContext);
      assertTrue(context.getScore("myScore").getValue() == 100);
    } catch (Exception pe) {
      pe.printStackTrace();
      fail("got unexpected exception " + pe);
    }
  }


  @Test
  @Ignore(value = "ToDo Aaron")
  public void testIsAnsweredCommand() throws Exception {
    // Needs different coachcontext (answer == null)
    coachContext = new CoachContext(context, cal, question, Optional.ofNullable(answer), coach, fqcn);
    coachContext.setLogger(Logger.getGlobal());

    when(coachContext.getCal().getAnswer(fqcn.toString(), "user-q10")).thenReturn(new Answer());
    when(coachContext.getCal().getAnswer(fqcn.toString(), "user-q50")).thenReturn(null);

    ExecutorContext context = CySeCExecutorContextFactory.getExecutorContext("test");
    context.reset();
    Command.registerCommand("isAnswered", new CommandIsAnswered());
    try {
      StringBuilder s = new StringBuilder();
      s.append("isAnswered(\"user-q10\") : condition1 : {" + System.lineSeparator());
      s.append("                 addScore(\"myScore\", 20);" + System.lineSeparator());
      s.append("              };" + System.lineSeparator());
      s.append("not(isAnswered(\"user-q10\")) : condition2 : {" + System.lineSeparator());
      s.append("                 addScore(\"myScore\", 100);" + System.lineSeparator());
      s.append("              };"  + System.lineSeparator());
      s.append("isAnswered(\"user-q50\") : condition3 : {" + System.lineSeparator());
      s.append("                 addScore(\"myScore\", 30);" + System.lineSeparator());
      s.append("              };");

      System.out.println("testing " + s);
      List<CySeCLineAtom> l = new ParserLine(s.toString()).getCySeCListing();

      context.executeQuestion(l, coachContext);
      // first block executed, but not second and not 3rd, resulting in score == 20
      assertTrue(context.getScore("myScore").getValue() == 20);
    } catch (Exception pe) {
      pe.printStackTrace();
      fail("got unexpected exception " + pe);
    }
  }

  @Test
  public void testSetHiddenCommand() {
    Question question = coachContext.getQuestionContext();
    question.setHidden(true);
    Command.registerCommand("setHidden", new CommandSetHidden());

    assertTrue(coachContext.getQuestionContext().isHidden());
    try {
      // hide question
      StringBuilder s = new StringBuilder();
      s.append("TRUE : bla :  {" + System.lineSeparator());
      s.append("                 setHidden(\"user-q20\", TRUE);" + System.lineSeparator());
      s.append("              }; // This is a silly comment");
      System.out.println("testing " + s);
      List<CySeCLineAtom> l = new ParserLine(s.toString()).getCySeCListing();

      context.executeQuestion(l, coachContext);
      assertTrue(coachContext.getQuestionContext().isHidden());

      // unhide question
      StringBuilder s2 = new StringBuilder();
      s2.append("TRUE : bla :  {" + System.lineSeparator());
      s2.append("                 setHidden(\"user-q20\", FALSE);" + System.lineSeparator());
      s2.append("              }; // This is a silly comment");
      System.out.println("testing " + s2);
      List<CySeCLineAtom> l2 = new ParserLine(s2.toString()).getCySeCListing();

      context.executeQuestion(l2, coachContext);
      assertFalse(coachContext.getQuestionContext().isHidden());

      //Atom result = l.get(0).execute(coachContext);
    } catch (Exception pe) {
      pe.printStackTrace();
      fail("got unexpected exception " + pe);
    }
  }

  @Test
  public void testIfCommand() {
    Command.registerCommand("if", new CommandIf());
    try {
      StringBuilder s = new StringBuilder();
            /*
               Syntax:
               if(<condition>, <true>, <false>)
             */
      s.append("TRUE : bla :  {" + System.lineSeparator());
      s.append("                 if(TRUE, addScore(\"myScore\",100), addScore(\"myScore\",200));" + System.lineSeparator());
      s.append("              };");
      System.out.println("testing " + s);
      List<CySeCLineAtom> l = new ParserLine(s.toString()).getCySeCListing();

      context.executeQuestion(l, coachContext);
      assertTrue(context.getScore("myScore").getValue() == 100);
    } catch (Exception pe) {
      pe.printStackTrace();
      fail("got unexpected exception " + pe);
    }

  }

  @Test
  public void testIfCommandSingle() {
    try {
      StringBuilder s = new StringBuilder();
            /*
               Syntax:
               if(<condition>, <true>, <false>)
             */
      s.append("TRUE : bla :  {" + System.lineSeparator());
      s.append("                 if(FALSE, addScore(\"myScore\",100));" + System.lineSeparator());
      s.append("              };");
      System.out.println("testing " + s);
      List<CySeCLineAtom> l = new ParserLine(s.toString()).getCySeCListing();

      context.executeQuestion(l, coachContext);
      assertTrue(context.getScore("myScore").getValue() == 0);
    } catch (Exception pe) {
      pe.printStackTrace();
      fail("got unexpected exception " + pe);
    }

  }

  @Test
  public void testAppend() {
    try {
      StringBuilder s = new StringBuilder();
      s.append("TRUE : bla :  {" + System.lineSeparator());
      s.append("                 set(\"hello\",\"you\");" + System.lineSeparator());
      s.append("                 append(\"hello\",\"There\");" + System.lineSeparator());
      s.append("              };");
      System.out.println("testing " + s);
      List<CySeCLineAtom> l = new ParserLine(s.toString()).getCySeCListing();

      context.executeQuestion(l, coachContext);
      assertEquals("youThere",context.getVariable("hello",null).getId());
    } catch (Exception pe) {
      pe.printStackTrace();
      fail("got unexpected exception " + pe);
    }

  }

  @Test
  public void testContains() {
    try {
      StringBuilder s = new StringBuilder();
      s.append("TRUE : bla :  {" + System.lineSeparator());
      s.append("                 set(\"hello\",\"you\");" + System.lineSeparator());
      s.append("              };");
      s.append("contains(get(\"hello\"),\"ou\") : bla2 :  {" + System.lineSeparator());
      s.append("                 set(\"hello\",\"me\");" + System.lineSeparator());
      s.append("              };");
      System.out.println("testing " + s);
      List<CySeCLineAtom> l = new ParserLine(s.toString()).getCySeCListing();

      context.executeQuestion(l, coachContext);
      assertEquals("me",context.getVariable("hello",null).getId());
    } catch (Exception pe) {
      pe.printStackTrace();
      fail("got unexpected exception " + pe);
    }

  }

  @Test
  public void testBadgeCommands() {
    try {
      StringBuilder s = new StringBuilder();
      s.append("TRUE : bla :  {" + System.lineSeparator());
      s.append("                 addBadge(\"Badge1\",0,\"\",\"\",\"\",\"\");" + System.lineSeparator());
      s.append("                 addBadgeClass(\"Badge1\",\"gold\",0,\"\",\"\",\"\",\"\");" + System.lineSeparator());
      s.append("                 addBadgeClass(\"Badge1\",\"silver\",0,\"\",\"\",\"\",\"\");" + System.lineSeparator());
      s.append("                 addBadgeClass(\"Badge1\",\"bronce\",0,\"\",\"\",\"\",\"\");" + System.lineSeparator());
      s.append("                 awardBadge(\"Badge1\",\"silver\");" + System.lineSeparator());
      s.append("              };");
      List<CySeCLineAtom> l = new ParserLine(s.toString()).getCySeCListing();
      context.executeQuestion(l, coachContext);
      assertTrue("bad badge is awarded (is:"+context.getBadge("Badge1").getAwardedBadgeClass()+")","silver".equals(context.getBadge("badge1").getAwardedBadgeClass().getId()));

      // award another badge
      s = new StringBuilder();
      s.append("TRUE : bla :  {" + System.lineSeparator());
      s.append("                 awardBadge(\"Badge1\",\"gold\");" + System.lineSeparator());
      s.append("              };");
      l = new ParserLine(s.toString()).getCySeCListing();
      context.executeQuestion(l, coachContext);
      assertTrue("bad Badge is awarded (is:"+context.getBadge("Badge1").getAwardedBadgeClass()+")","gold".equals(context.getBadge("Badge1").getAwardedBadgeClass().getId()));
      assertTrue("Badge list is bad (is:"+context.getBadgeList().length+")",context.getBadgeList().length==1);
    } catch (Exception pe) {
      pe.printStackTrace();
      fail("got unexpected exception " + pe);
    }
  }

    @Test
    public void testRevokeBadgeCommand() {
        try {
            StringBuilder s = new StringBuilder();
            s.append("TRUE : bla :  {" + System.lineSeparator());
            s.append("                 addBadge(\"Badge1\",0,\"\",\"\",\"\",\"\");" + System.lineSeparator());
            s.append("                 addBadgeClass(\"Badge1\",\"gold\",0,\"\",\"\",\"\",\"\");" + System.lineSeparator());
            s.append("                 awardBadge(\"Badge1\",\"gold\");" + System.lineSeparator());
            s.append("              };");
            List<CySeCLineAtom> l = new ParserLine(s.toString()).getCySeCListing();
            context.executeQuestion(l, coachContext);
            assertTrue("bad badge! is awarded (is:"+context.getBadge("Badge1").getAwardedBadgeClass()+")","gold".equals(context.getBadge("badge1").getAwardedBadgeClass().getId()));

            // revoke existing badeClass
            s = new StringBuilder();
            s.append("TRUE : bla :  {" + System.lineSeparator());
            s.append("                 revokeBadge(\"Badge1\");" + System.lineSeparator());
            s.append("              };");
            l = new ParserLine(s.toString()).getCySeCListing();
            context.executeQuestion(l, coachContext);
            // expect current class to be null now
            assertNull(
                    "bad Badge! is awarded (is:"+context.getBadge("Badge1").getAwardedBadgeClass()+")",
                    context.getBadge("Badge1").getAwardedBadgeClass());

            // expect badge list to still hold 1 entry
            assertTrue("Badge list is bad! (is:"+context.getBadgeList().length+")",
                    context.getBadgeList().length==1);
        } catch (Exception pe) {
            pe.printStackTrace();
            fail("got unexpected exception " + pe);
        }
    }

    @Test
    // @Ignore(value = "Pending change to remove this behavior")
    public void testRevokeNonExistingBadge() {
      CySeCExecutorContext context = CySeCExecutorContextFactory.getExecutorContext("badgeTest");
      try {
          // try to revoke class from non-existing badge
          StringBuilder s = new StringBuilder();
          s.append("TRUE : bla :  {" + System.lineSeparator());
          s.append("                 revokeBadge(\"Badge1\");" + System.lineSeparator());
          s.append("              };");
          List<CySeCLineAtom> l = new ParserLine(s.toString()).getCySeCListing();
          context.executeQuestion(l, coachContext);

          // Satify constructor call from ParserLine and executeQuestion()
      } catch (ParserException e) {
          e.printStackTrace();
          fail("got unexpected parser exception");
      } catch (ExecutorException e) {
          // expect badge list to be empty
          e.printStackTrace();
          assertTrue("Badge list is bad! (is:"+context.getBadgeList().length+")",
                  context.getBadgeList().length==0);
          return;
      }
      fail("no exception when revoking nonexistent badge");
    }

  @Test
  public void testRecommendationCommands() {
    try {
      StringBuilder s = new StringBuilder();
      s.append("TRUE : bla :  {" + System.lineSeparator());
      s.append("                 addRecommendation(\"rec1\",0,\"TestUrl\",\"TestAlt\",\"TestTitle\",\"TestDescription\",\"TextLink\",\"TestLink\");" + System.lineSeparator());
      s.append("                 addRecommendation(\"rec2\",0,\"TestUrl2\",\"TestAlt2\",\"TestTitle2\",\"TestDescription2\",\"TextLink2\",\"TestLink2\");" + System.lineSeparator());
      s.append("              };");
      List<CySeCLineAtom> l = new ParserLine(s.toString()).getCySeCListing();
      context.executeQuestion(l, coachContext);
      assertTrue("Recommendation is not (is:"+context.getRecommendation("rec1")+")",context.getRecommendation("rec1")!=null);

      assertTrue("Recommendation list is bad (is:"+context.getRecommendationList().length+")",context.getRecommendationList().length==1);
      assertTrue("Recommendation content is bad","TestTitle".equals(context.getRecommendation("rec1").getTitle()));
      assertTrue("Recommendation content is bad (2)","TestTitle2".equals(context.getRecommendation("rec2").getTitle()));
    } catch (Exception pe) {
      pe.printStackTrace();
      fail("got unexpected exception " + pe);
    }
  }

  @Test
  public void testCreateSubcoachCommand() throws CacheException {
    Questionnaire coach = new Questionnaire();
    coach.setId("my-subcoach-one");
    coach.setParent("lib-company");
    Mockito.when(coachContext.getCal().getCoach(anyString())).thenReturn(coach);
    Mockito.doNothing().when(coachContext.getCal()).instantiateSubCoach(any(Questionnaire.class), any(Set.class));
    CoachLibrary mockLibrary = new MockLibrary();
    mockLibrary.init("eu.smesec.cysec.coach.MockLibrary", coach, cal, Logger.getGlobal());

    List<CoachLibrary> libraries = new ArrayList<>();
    libraries.add(mockLibrary);
    Mockito.when(coachContext.getCal().getLibraries(anyString())).thenReturn(libraries);

    Command.registerCommand("createSubcoach", new CommandCreateSubcoach());
    try {
      StringBuilder s = new StringBuilder();

      s.append("TRUE : bla :  {" + System.lineSeparator());
      s.append("                 createSubcoach(\"my-subcoach\", \"file-segment\");" + System.lineSeparator());
      s.append("              };");
      System.out.println("testing " + s);
      List<CySeCLineAtom> l = new ParserLine(s.toString()).getCySeCListing();

      context.executeQuestion(l, coachContext);
      // assert libcal called


      // assert parent context is correct
      assertEquals(((AbstractLib)mockLibrary).getExecutorContext().getParent(), context);

    } catch (Exception pe) {
      pe.printStackTrace();
      fail("got unexpected exception " + pe);
    }

  }

  @Test
  public void testSetNext() {
    try {
      StringBuilder s = new StringBuilder();
      s.append("TRUE : bla :  {" + System.lineSeparator());
      s.append("                 setNext(\"q30\");" + System.lineSeparator());
      s.append("              };");
      List<CySeCLineAtom> l = new ParserLine(s.toString()).getCySeCListing();
      context.executeQuestion(l, coachContext);
      assertEquals("q30", context.getVariable("_coach.nextPage", question.getId()).getId());
    } catch (Exception pe) {
      pe.printStackTrace();
      fail("got unexpected exception " + pe);
    }

  }

  @Test
  public void testCreateInexistantSubcoachCommand() throws CacheException {
    Command.registerCommand("createSubcoach", new CommandCreateSubcoach());

    Mockito.when(coachContext.getCal().getCoach(anyString())).thenReturn(null);
    try {
      StringBuilder s = new StringBuilder();

      s.append("TRUE : bla :  {" + System.lineSeparator());
      s.append("                 createSubcoach(\"my-subcoach-fail\", \"file-segment\");" + System.lineSeparator());
      s.append("              };");
      System.out.println("testing " + s);
      List<CySeCLineAtom> l = new ParserLine(s.toString()).getCySeCListing();

      context.executeQuestion(l, coachContext);
      fail();
    } catch (ParserException | ExecutorException pe) {
      assertEquals("Coach id my-subcoach-fail does not exist", pe.getMessage());
    }
  }

  @Test
  public void testCommandDictionaryLookup() throws ExecutorException, ParserException {
    Command.registerCommand("tn", new CommandDictionaryLookup());

    Atom existent = new ParserLine("tn(\"key-abc\");").getAtom().execute(coachContext);
    assertEquals(Atom.AtomType.STRING, existent.getType());
    assertEquals("Value ABC", existent.getId());

    Atom nonExistent = new ParserLine("tn(\"key-non-existent\");").getAtom().execute(coachContext);
    assertEquals(Atom.AtomType.NULL, nonExistent.getType());

    assertThrows(ExecutorException.class, () -> new ParserLine("tn();").getAtom().execute(coachContext));

    when(coach.getDictionary()).thenReturn(null);
    Atom noDictionary = new ParserLine("tn(\"any-key\");").getAtom().execute(coachContext);
    assertEquals(Atom.AtomType.NULL, noDictionary.getType());
  }

  @Test
  public void testCommandGreaterThanInt() throws ExecutorException, ParserException {
    ExecutorContext context = CySeCExecutorContextFactory.getExecutorContext("test");
    context.reset();
    Command.registerCommand("greaterThan", new CommandGreaterThan());

    String code = "greaterThan(10, 5) : foo : { addScore(`s`, 5); };".replace('`', '"');
    List<CySeCLineAtom> l = new ParserLine(code).getCySeCListing();
    context.executeQuestion(l, coachContext);
    assertEquals(5, context.getScore("s").getValue(), 1e-6);

    code = "greaterThan(5, 5) : foo : { addScore(`s`, 5); };".replace('`', '"');
    l = new ParserLine(code).getCySeCListing();
    context.executeQuestion(l, coachContext);
    assertEquals(0, context.getScore("s").getValue(), 1e-6);

    code = "greaterThan(-5, 2) : foo : { addScore(`s`, 5); };".replace('`', '"');
    l = new ParserLine(code).getCySeCListing();
    context.executeQuestion(l, coachContext);
    assertEquals(0, context.getScore("s").getValue(), 1e-6);
  }

  @Test
  public void testCommandGreaterThanFloat() throws ExecutorException, ParserException {
    ExecutorContext context = CySeCExecutorContextFactory.getExecutorContext("test");
    context.reset();
    Command.registerCommand("greaterThan", new CommandGreaterThan());

    String code = "greaterThan(10.5, 10.2) : foo : { addScore(`s`, 5); };".replace('`', '"');
    List<CySeCLineAtom> l = new ParserLine(code).getCySeCListing();
    context.executeQuestion(l, coachContext);
    assertEquals(5, context.getScore("s").getValue(), 1e-6);

    code = "greaterThan(10.5, 10.5) : foo : { addScore(`s`, 5); };".replace('`', '"');
    l = new ParserLine(code).getCySeCListing();
    context.executeQuestion(l, coachContext);
    assertEquals(0, context.getScore("s").getValue(), 1e-6);

    code = "greaterThan(-10.5, -10.2) : foo : { addScore(`s`, 5); };".replace('`', '"');
    l = new ParserLine(code).getCySeCListing();
    context.executeQuestion(l, coachContext);
    assertEquals(0, context.getScore("s").getValue(), 1e-6);
  }

  @Test
  public void testCommandGreaterThanOrEqualsInt() throws ExecutorException, ParserException {
    ExecutorContext context = CySeCExecutorContextFactory.getExecutorContext("test");
    context.reset();
    Command.registerCommand("greaterThanOrEq", new CommandGreaterThanOrEquals());

    String code = "greaterThanOrEq(10, 5) : foo : { addScore(`s`, 5); };".replace('`', '"');
    List<CySeCLineAtom> l = new ParserLine(code).getCySeCListing();
    context.executeQuestion(l, coachContext);
    assertEquals(5, context.getScore("s").getValue(), 1e-6);

    code = "greaterThanOrEq(5, 5) : foo : { addScore(`s`, 5); };".replace('`', '"');
    l = new ParserLine(code).getCySeCListing();
    context.executeQuestion(l, coachContext);
    assertEquals(5, context.getScore("s").getValue(), 1e-6);

    code = "greaterThanOrEq(-5, 2) : foo : { addScore(`s`, 5); };".replace('`', '"');
    l = new ParserLine(code).getCySeCListing();
    context.executeQuestion(l, coachContext);
    assertEquals(0, context.getScore("s").getValue(), 1e-6);
  }

  @Test
  public void testCommandGreaterThanOrEqualsFloat() throws ExecutorException, ParserException {
    ExecutorContext context = CySeCExecutorContextFactory.getExecutorContext("test");
    context.reset();
    Command.registerCommand("greaterThanOrEq", new CommandGreaterThanOrEquals());

    String code = "greaterThanOrEq(10.5, 10.2) : foo : { addScore(`s`, 5); };".replace('`', '"');
    List<CySeCLineAtom> l = new ParserLine(code).getCySeCListing();
    context.executeQuestion(l, coachContext);
    assertEquals(5, context.getScore("s").getValue(), 1e-6);

    code = "greaterThanOrEq(10.5, 10.5) : foo : { addScore(`s`, 5); };".replace('`', '"');
    l = new ParserLine(code).getCySeCListing();
    context.executeQuestion(l, coachContext);
    assertEquals(5, context.getScore("s").getValue(), 1e-6);

    code = "greaterThanOrEq(-10.5, -10.2) : foo : { addScore(`s`, 5); };".replace('`', '"');
    l = new ParserLine(code).getCySeCListing();
    context.executeQuestion(l, coachContext);
    assertEquals(0, context.getScore("s").getValue(), 1e-6);
  }

  @Test
  public void testCommandLowerThanInt() throws ExecutorException, ParserException {
    ExecutorContext context = CySeCExecutorContextFactory.getExecutorContext("test");
    context.reset();
    Command.registerCommand("lowerThan", new CommandLowerThan());

    String code = "lowerThan(10, 5) : foo : { addScore(`s`, 5); };".replace('`', '"');
    List<CySeCLineAtom> l = new ParserLine(code).getCySeCListing();
    context.executeQuestion(l, coachContext);
    assertEquals(0, context.getScore("s").getValue(), 1e-6);

    code = "lowerThan(5, 5) : foo : { addScore(`s`, 5); };".replace('`', '"');
    l = new ParserLine(code).getCySeCListing();
    context.executeQuestion(l, coachContext);
    assertEquals(0, context.getScore("s").getValue(), 1e-6);

    code = "lowerThan(-5, 2) : foo : { addScore(`s`, 5); };".replace('`', '"');
    l = new ParserLine(code).getCySeCListing();
    context.executeQuestion(l, coachContext);
    assertEquals(5, context.getScore("s").getValue(), 1e-6);
  }

  @Test
  public void testCommandLowerThanFloat() throws ExecutorException, ParserException {
    ExecutorContext context = CySeCExecutorContextFactory.getExecutorContext("test");
    context.reset();
    Command.registerCommand("lowerThan", new CommandLowerThan());

    String code = "lowerThan(10.5, 10.2) : foo : { addScore(`s`, 5); };".replace('`', '"');
    List<CySeCLineAtom> l = new ParserLine(code).getCySeCListing();
    context.executeQuestion(l, coachContext);
    assertEquals(0, context.getScore("s").getValue(), 1e-6);

    code = "lowerThan(10.5, 10.5) : foo : { addScore(`s`, 5); };".replace('`', '"');
    l = new ParserLine(code).getCySeCListing();
    context.executeQuestion(l, coachContext);
    assertEquals(0, context.getScore("s").getValue(), 1e-6);

    code = "lowerThan(-10.5, -10.2) : foo : { addScore(`s`, 5); };".replace('`', '"');
    l = new ParserLine(code).getCySeCListing();
    context.executeQuestion(l, coachContext);
    assertEquals(5, context.getScore("s").getValue(), 1e-6);
  }

  @Test
  public void testCommandLowerThanOrEqualsInt() throws ExecutorException, ParserException {
    ExecutorContext context = CySeCExecutorContextFactory.getExecutorContext("test");
    context.reset();
    Command.registerCommand("lowerThanOrEq", new CommandLowerThanOrEquals());

    String code = "lowerThanOrEq(10, 5) : foo : { addScore(`s`, 5); };".replace('`', '"');
    List<CySeCLineAtom> l = new ParserLine(code).getCySeCListing();
    context.executeQuestion(l, coachContext);
    assertEquals(0, context.getScore("s").getValue(), 1e-6);

    code = "lowerThanOrEq(5, 5) : foo : { addScore(`s`, 5); };".replace('`', '"');
    l = new ParserLine(code).getCySeCListing();
    context.executeQuestion(l, coachContext);
    assertEquals(5, context.getScore("s").getValue(), 1e-6);

    code = "lowerThanOrEq(-5, 2) : foo : { addScore(`s`, 5); };".replace('`', '"');
    l = new ParserLine(code).getCySeCListing();
    context.executeQuestion(l, coachContext);
    assertEquals(5, context.getScore("s").getValue(), 1e-6);
  }

  @Test
  public void testCommandLowerThanOrEqualsFloat() throws ExecutorException, ParserException {
    ExecutorContext context = CySeCExecutorContextFactory.getExecutorContext("test");
    context.reset();
    Command.registerCommand("lowerThanOrEq", new CommandLowerThanOrEquals());

    String code = "lowerThanOrEq(10.5, 10.2) : foo : { addScore(`s`, 5); };".replace('`', '"');
    List<CySeCLineAtom> l = new ParserLine(code).getCySeCListing();
    context.executeQuestion(l, coachContext);
    assertEquals(0, context.getScore("s").getValue(), 1e-6);

    code = "lowerThanOrEq(10.5, 10.5) : foo : { addScore(`s`, 5); };".replace('`', '"');
    l = new ParserLine(code).getCySeCListing();
    context.executeQuestion(l, coachContext);
    assertEquals(5, context.getScore("s").getValue(), 1e-6);

    code = "lowerThanOrEq(-10.5, -10.2) : foo : { addScore(`s`, 5); };".replace('`', '"');
    l = new ParserLine(code).getCySeCListing();
    context.executeQuestion(l, coachContext);
    assertEquals(5, context.getScore("s").getValue(), 1e-6);
  }

}
