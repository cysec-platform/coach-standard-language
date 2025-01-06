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
package eu.smesec.cysec.csl.parser;

import static eu.smesec.cysec.csl.parser.CommandAbstractList.deescape;
import static eu.smesec.cysec.csl.parser.CommandAbstractList.escape;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.junit.Test;

public class TestArrayCommands extends AbstractTestCommands {

  String[] strs = new String[] {
      ",","\\,",
      ",,","\\,\\,",
      ",","\\,",
      "\\","\\\\"
  };

  @Test
  public void testEscaping() throws Exception {
    for(int i=0; i<strs.length-2;i+=2) {
      assertEquals( "Mismatch when escaping \""+strs[i]+"\" detected", strs[i+1],escape(strs[i]) );
    }
  }

  @Test
  public void testDeEscaping() throws Exception {
    for(int i=0; i<strs.length-2;i+=2) {
      assertEquals( "Mismatch when deescaping \""+strs[i+1]+"\" detected", strs[i],deescape(strs[i+1]) );
    }
  }

  public static Map<String,List<String>> m = Map.ofEntries(
      Map.entry( "100, 100", Arrays.asList("100","100") )
  );

  @Test
  public void testSimpleArrayAdd() throws Exception {
    //For testSelectedFalse
    when(coachContext.getCal().getAnswer(fqcn.toString(), coachContext.getQuestionContext())).thenReturn(null);

    ExecutorContext context = CySeCExecutorContextFactory.getExecutorContext("test");
    context.reset();
    Command.registerCommand("arrayAdd", new CommandArrayAdd());
    Command.registerCommand("set", new CommandSetVar());
    Command.registerCommand("get", new CommandGetVar());
    try {
      StringBuilder s = new StringBuilder();
      s.append("TRUE : bla :  {" + System.lineSeparator());
      s.append("                 arrayAdd(\"arr\",\"100\");" + System.lineSeparator());
      s.append("                 arrayAdd(\"arr\",\"200\");" + System.lineSeparator());
      s.append("                 arrayAdd(\"arr\",\"100\");" + System.lineSeparator());
      s.append("                 set(\"arr2\",get(\"arr\"));" + System.lineSeparator());
      s.append("                 arrayAdd(\"arr2\",\"100\");" + System.lineSeparator());
      s.append("              }; // This is a silly comment");
      System.out.println("testing " + s);
      List<CySeCLineAtom> l = new ParserLine(s.toString()).getCySeCListing();

      context.executeQuestion(l, coachContext);
      //Atom result = l.get(0).execute(coachContext);
      assertEquals("unexpected Value in Array", "100, 200, 100",context.getVariable("arr",null).getId());
      assertEquals("unexpected Value in Array", "100, 200, 100, 100",context.getVariable("arr2",null).getId());
    } catch (Exception pe) {
      pe.printStackTrace();
      fail("got unexpected exception " + pe);
    }
  }

  @Test
  public void testSimpleArrayAddUnique() throws Exception {
    //For testSelectedFalse
    when(coachContext.getCal().getAnswer(fqcn.toString(), coachContext.getQuestionContext())).thenReturn(null);

    ExecutorContext context = CySeCExecutorContextFactory.getExecutorContext("test");
    context.reset();
    Command.registerCommand("arrayAdd", new CommandArrayAdd());
    try {
      StringBuilder s = new StringBuilder();
      s.append("TRUE : bla :  {" + System.lineSeparator());
      s.append("                 arrayAdd(\"arr\",\"100\");" + System.lineSeparator());
      s.append("                 arrayAdd(\"arr\",\"100\");" + System.lineSeparator());
      s.append("                 arrayAdd(\"arr\",\"200\");" + System.lineSeparator());
      s.append("                 arrayAdd(\"arr\",\"200\",TRUE);" + System.lineSeparator());
      s.append("              }; // This is a silly comment");
      System.out.println("testing " + s);
      List<CySeCLineAtom> l = new ParserLine(s.toString()).getCySeCListing();

      context.executeQuestion(l, coachContext);
      //Atom result = l.get(0).execute(coachContext);
      assertEquals("unexpected Value in Array", "100, 200",context.getVariable("arr",null).getId());
    } catch (Exception pe) {
      pe.printStackTrace();
      fail("got unexpected exception " + pe);
    }
  }

  @Test
  public void testSimpleArrayRemove() throws Exception {
    //For testSelectedFalse
    when(coachContext.getCal().getAnswer(fqcn.toString(), coachContext.getQuestionContext())).thenReturn(null);

    ExecutorContext context = CySeCExecutorContextFactory.getExecutorContext("test");
    context.reset();
    Command.registerCommand("arrayAdd", new CommandArrayAdd());
    Command.registerCommand("arrayRemove", new CommandArrayRemove());
    Command.registerCommand("set", new CommandSetVar());
    Command.registerCommand("get", new CommandGetVar());
    try {
      StringBuilder s = new StringBuilder();
      s.append("TRUE : bla :  {" + System.lineSeparator());
      s.append("                 arrayAdd(\"arr\",\"100\");" + System.lineSeparator());
      s.append("                 arrayAdd(\"arr\",\"100\");" + System.lineSeparator());
      s.append("                 arrayAdd(\"arr\",\"200\");" + System.lineSeparator());
      s.append("                 set(\"arr2\",get(\"arr\"));" + System.lineSeparator());
      s.append("                 arrayRemove(\"arr\",\"100\");" + System.lineSeparator());
      s.append("              }; // This is a silly comment");
      System.out.println("testing " + s);
      List<CySeCLineAtom> l = new ParserLine(s.toString()).getCySeCListing();

      context.executeQuestion(l, coachContext);
      //Atom result = l.get(0).execute(coachContext);
      assertEquals("unexpected Value in Array", "100, 200",context.getVariable("arr",null).getId());
      assertEquals("unexpected Value in first tempArray", "100, 100, 200",context.getVariable("arr2",null).getId());
    } catch (Exception pe) {
      pe.printStackTrace();
      fail("got unexpected exception " + pe);
    }
  }

  @Test
  public void testSimpleArrayContains() throws Exception {
    //For testSelectedFalse
    when(coachContext.getCal().getAnswer(fqcn.toString(), coachContext.getQuestionContext())).thenReturn(null);

    ExecutorContext context = CySeCExecutorContextFactory.getExecutorContext("test");
    context.reset();
    Command.registerCommand("set", new CommandSetVar());
    Command.registerCommand("arrayAdd", new CommandArrayAdd());
    Command.registerCommand("arrayContains", new CommandArrayContains());
    try {
      StringBuilder s = new StringBuilder();
      s.append("TRUE : bla :  {" + System.lineSeparator());
      s.append("                 set(\"arr\",NULL); // clear an array" + System.lineSeparator());
      s.append("                 arrayAdd(\"arr\",\"100\");" + System.lineSeparator());
      s.append("                 arrayAdd(\"arr\",\"200\");" + System.lineSeparator());
      s.append("              }; // This is a silly comment" + System.lineSeparator());
      s.append("arrayContains(\"arr\",\"100\") : bla2 :  {" + System.lineSeparator());
      s.append("                 set(\"to1\",TRUE);" + System.lineSeparator());
      s.append("              }; " + System.lineSeparator());
      s.append("arrayContains(\"arr\",\"150\") : bla3 :  {" + System.lineSeparator());
      s.append("                 set(\"to2\",TRUE);" + System.lineSeparator());
      s.append("              }; " + System.lineSeparator());
      System.out.println("testing " + s);
      List<CySeCLineAtom> l = new ParserLine(s.toString()).getCySeCListing();

      context.executeQuestion(l, coachContext);
      //Atom result = l.get(0).execute(coachContext);
      assertEquals("unexpected Value in Array", "100, 200",context.getVariable("arr",null).getId());
      assertEquals("visible value found in Array", "TRUE",context.getVariable("to1",null).getId());
      assertEquals("nonexisting value unexpectedly found in Array", null,context.getVariable("to2",null).getId());
    } catch (Exception pe) {
      pe.printStackTrace();
      fail("got unexpected exception " + pe);
    }
  }

  @Test
  public void testSimpleArrayElements() throws Exception {
    //For testSelectedFalse
    when(coachContext.getCal().getAnswer(fqcn.toString(), coachContext.getQuestionContext())).thenReturn(null);

    ExecutorContext context = CySeCExecutorContextFactory.getExecutorContext("test");
    context.reset();
    Command.registerCommand("set", new CommandSetVar());
    Command.registerCommand("arrayAdd", new CommandArrayAdd());
    Command.registerCommand("arrayElements", new CommandArrayElements());
    try {
      StringBuilder s = new StringBuilder();
      s.append("TRUE : bla :  {" + System.lineSeparator());
      s.append("                 set(\"arr\",NULL); // clear an array" + System.lineSeparator());
      s.append("                 arrayAdd(\"arr\",\"100\");" + System.lineSeparator());
      s.append("                 arrayAdd(\"arr\",\"200\");" + System.lineSeparator());
      s.append("                 arrayAdd(\"arr\",\"200\");" + System.lineSeparator());
      s.append("              }; // This is a silly comment" + System.lineSeparator());
      s.append("arrayElements(\"arr\",3) : bla2 :  {" + System.lineSeparator());
      s.append("                 set(\"to1\",TRUE);" + System.lineSeparator());
      s.append("              }; " + System.lineSeparator());
      s.append("arrayElements(\"arr\",1) : bla3 :  {" + System.lineSeparator());
      s.append("                 set(\"to2\",TRUE);" + System.lineSeparator());
      s.append("              }; " + System.lineSeparator());
      s.append("arrayElements(\"arrNull\",1) : bla4 :  {" + System.lineSeparator());
      s.append("                 set(\"to3\",TRUE);" + System.lineSeparator());
      s.append("              }; " + System.lineSeparator());
      s.append("arrayElements(\"arrNull\",0) : bla5 :  {" + System.lineSeparator());
      s.append("                 set(\"to4\",TRUE);" + System.lineSeparator());
      s.append("              }; " + System.lineSeparator());
      s.append("arrayElements(\"\",0) : bla6 :  {" + System.lineSeparator());
      s.append("                 set(\"to5\",TRUE);" + System.lineSeparator());
      s.append("              }; " + System.lineSeparator());
      System.out.println("testing " + s);
      List<CySeCLineAtom> l = new ParserLine(s.toString()).getCySeCListing();

      context.executeQuestion(l, coachContext);
      //Atom result = l.get(0).execute(coachContext);
      assertEquals("unexpected Value in Array", "100, 200, 200",context.getVariable("arr",null).getId());
      assertEquals("Number of array elements missmatch (1)", "TRUE",context.getVariable("to1",null).getId());
      assertEquals("Number of array elements missmatch (2)", null,context.getVariable("to2",null).getId());
      assertEquals("Number of array elements missmatch (3)", null,context.getVariable("to3",null).getId());
      assertEquals("Number of array elements missmatch (4)", "TRUE",context.getVariable("to4",null).getId());
      assertEquals("Number of array elements missmatch (5)", "TRUE",context.getVariable("to5",null).getId());
    } catch (Exception pe) {
      pe.printStackTrace();
      fail("got unexpected exception " + pe);
    }
  }

  @Test
  public void testComplexArrayElements() throws Exception {
    //For testSelectedFalse
    when(coachContext.getCal().getAnswer(fqcn.toString(), coachContext.getQuestionContext())).thenReturn(null);

    ExecutorContext context = CySeCExecutorContextFactory.getExecutorContext("test");
    context.reset();
    Command.registerCommand("set", new CommandSetVar());
    Command.registerCommand("arrayAdd", new CommandArrayAdd());
    Command.registerCommand("arrayLength", new CommandArrayLength());
    Command.registerCommand("greaterThanOrEq", new CommandGreaterThanOrEquals());
    Command.registerCommand("equals", new CommandEquals());
    try {
      StringBuilder s = new StringBuilder();
      s.append("TRUE : bla :  {" + System.lineSeparator());
      s.append("                 set(\"arr\",NULL); // clear an array" + System.lineSeparator());
      s.append("                 arrayAdd(\"arr\",\"100\");" + System.lineSeparator());
      s.append("                 arrayAdd(\"arr\",\"200\");" + System.lineSeparator());
      s.append("                 arrayAdd(\"arr\",\"200\");" + System.lineSeparator());
      s.append("              }; // This is a silly comment" + System.lineSeparator());
      s.append("greaterThanOrEq(arrayLength(\"arr\"),3) : bla2 :  {" + System.lineSeparator());
      s.append("                 set(\"to1\",TRUE);" + System.lineSeparator());
      s.append("              }; " + System.lineSeparator());
      s.append("greaterThanOrEq(arrayLength(\"arr\"),2) : bla3 :  {" + System.lineSeparator());
      s.append("                 set(\"to2\",TRUE);" + System.lineSeparator());
      s.append("              }; " + System.lineSeparator());
      s.append("greaterThanOrEq(arrayLength(\"arr2\"),2) : bla4 :  {" + System.lineSeparator());
      s.append("                 set(\"to3\",TRUE);" + System.lineSeparator());
      s.append("              }; " + System.lineSeparator());
      s.append("equals(arrayLength(\"arrNone\"),0) : bla5 :  {" + System.lineSeparator());
      s.append("                 set(\"to4\",TRUE);" + System.lineSeparator());
      s.append("              }; " + System.lineSeparator());
      System.out.println("testing " + s);
      List<CySeCLineAtom> l = new ParserLine(s.toString()).getCySeCListing();

      context.executeQuestion(l, coachContext);
      //Atom result = l.get(0).execute(coachContext);
      assertEquals("unexpected Value in Array", "100, 200, 200",context.getVariable("arr",null).getId());
      assertEquals("Number of array elements missmatch (1)", "TRUE",context.getVariable("to1",null).getId());
      assertEquals("Number of array elements missmatch (2)", "TRUE",context.getVariable("to2",null).getId());
      assertEquals("Number of array elements missmatch (3)", null,context.getVariable("to3",null).getId());
      assertEquals("Number of array elements missmatch (4)", "TRUE",context.getVariable("to4",null).getId());
    } catch (Exception pe) {
      pe.printStackTrace();
      fail("got unexpected exception " + pe);
    }
  }

}
