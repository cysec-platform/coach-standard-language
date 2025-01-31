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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.util.regex.Pattern;

public abstract class CommandAbstractList extends Command {

  public static final char[] ESCAPES = { '\\', ',' };
  public static final char ESCAPE = '\\';

  public static String escape(String s) {
    for(char c:ESCAPES) {
      String regex = Pattern.compile("[{}()\\[\\].+*?^$\\\\|]").matcher(""+c).replaceAll("\\\\$0");
      //String regex = (new StringBuilder()).append(ESCAPE).append(c).toString();
      String repl = (new StringBuilder()).append(ESCAPE).append(c).toString();
      s=s.replace( regex,repl);
    }
    return s;
  }

  public static String deescape(String s) {
    for(char c:ESCAPES) {
      // String regex = Pattern.compile("[{}()\\[\\].+*?^$\\\\|]").matcher((new StringBuilder()).append(ESCAPE).append(c).toString()).replaceAll("\\\\$0");
      String regex = (new StringBuilder()).append(ESCAPE).append(c).toString();
      String repl = ""+c;
      s=s.replace( regex, repl);
    }
    return s;
  }

  public static List<String> stringToList(String s) {
    List<String> l = new ArrayList();
    if(s==null) return l;
    for(String st:Arrays.asList(s.split("(?<!\\\\), "))) {
      l.add(deescape(st));
    }
    return l;
  }

  public static String listToString(List<String> list) {
    StringBuilder result = new StringBuilder();
    boolean first = true;
    for(String string : list) {
      String t=escape(string);
      if(first) {
        result.append(t);
        first=false;
      } else {
        result.append(", ").append(t);
      }
    }
    return result.toString();
  }
}
