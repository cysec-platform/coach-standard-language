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
package eu.smesec.cysec.csl.parser;

import java.io.IOException;

/**
 * The parser throws this exception if it encounters a syntax error in the coach logic.
 * <p>This is different from ParserException in that the syntax of the command is correct,
 * however, it tries to do something that can't be executed, e.g hiding a question with invalid ID.</p>
 * @see ParserBlankLineException
 * @see ParserNullLineException
 * @see ParserUnexpectedTokenException
 */
public class ParserException extends IOException {
  private ParserLine line;
  // reason is a detailed string that marks the spot that caused the parser to throw the exception
  private String reason;

  public ParserException(String reason, ParserLine line) {
    super("Parser throws exception: " + reason + (line!=null?" ("+line.getContext()+")":""));
    this.reason = reason;
    this.line = line;
  }

  /***
   * <p>Returns the ParserLine throowing the exception.</p>
   *
   * @return the requested parser line
   */
  public ParserLine getParserLine() {
    return line;
  }

  public String toString() {
    return "Parser throws exception: " + reason + (line!=null?" ("+line.getContext()+")":"");
  }
}
