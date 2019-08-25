/*
 * TestDifficultSituations.java: JUnit test for a Tokenizer
 *
 * Copyright (C) 2002 Heiko Blau
 *
 * This file belongs to the JTopas test suite.
 * The JTopas test suite is free software; you can redistribute it and/or modify it 
 * under the terms of the GNU Lesser General Public License as published by the 
 * Free Software Foundation; either version 2.1 of the License, or (at your option) 
 * any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or 
 * FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along 
 * with the JTopas test suite. If not, write to the
 *
 *   Free Software Foundation, Inc.
 *   59 Temple Place, Suite 330, 
 *   Boston, MA 02111-1307 
 *   USA
 *
 * or check the Internet: http://www.fsf.org
 *
 * The JTopas test suite uses the test framework JUnit by Kent Beck and Erich Gamma.
 * You should have received a copy of their JUnit licence agreement along with 
 * the JTopas test suite.
 *
 * We do NOT provide the JUnit archive junit.jar nessecary to compile and run 
 * our tests, since we assume, that You  either have it already or would like 
 * to get the current release Yourself. 
 * Please visit either:
 *   http://sourceforge.net/projects/junit
 * or
 *   http://junit.org
 * to obtain JUnit.
 *
 * Contact:
 *   email: heiko@susebox.de 
 */

package de.susebox.jtopas;

//-----------------------------------------------------------------------------
// Imports
//
import java.io.Reader;
import java.io.StringReader;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.framework.Assert;
import junit.swingui.TestRunner;


//-----------------------------------------------------------------------------
// Class TestDifficultSituations
//

/**<p>
 * The class contains a number of test cases that are supposed to be difficult
 * to handle for a {@link Tokenizer}, e.g. EOF conditions inside strings etc.
 *</p>
 *
 * @see     Tokenizer
 * @see     StandardTokenizer
 * @see     StandardTokenizerProperties
 * @author  Heiko Blau
 */
public class TestDifficultSituations extends TestCase {
  
  //---------------------------------------------------------------------------
  // properties
  //

  
  //---------------------------------------------------------------------------
  // main method
  //
  
  /**
   * call this method to invoke the tests
   */
  public static void main(String[] args) {
    String[]   tests = { TestDifficultSituations.class.getName() };
    TestRunner runner = new TestRunner();
    
    runner.main(tests);
  }
  

  //---------------------------------------------------------------------------
  // suite method
  //
  
  /**
   * Implementation of the JUnit method <code>suite</code>. For each set of test
   * properties one or more tests are instantiated.
   *
   * @return a test suite
   */
  public static Test suite() {
    TestSuite   suite = new TestSuite();
    
    suite.addTest(new TestDifficultSituations("testEOFInLineComment"));
    suite.addTest(new TestDifficultSituations("testEOFInBlockComment"));
    suite.addTest(new TestDifficultSituations("testEOFInString"));
    suite.addTest(new TestDifficultSituations("testStringEscapes1"));
    suite.addTest(new TestDifficultSituations("testStringEscapes2"));
    suite.addTest(new TestDifficultSituations("testNestedComments"));
    suite.addTest(new TestDifficultSituations("testReaderSwitching"));
    suite.addTest(new TestDifficultSituations("testDOSEOL"));
    suite.addTest(new TestDifficultSituations("testMACEOL"));
    suite.addTest(new TestDifficultSituations("testSpecialCalls"));
    return suite;
  }
  
  
  //---------------------------------------------------------------------------
  // Constructor
  //
  
  /**
   * Default constructor. Standard input {@link java.lang.System#in} is used
   * to construct the input stream reader.
   */  
  public TestDifficultSituations(String test) {
    super(test);
  }

  
  //---------------------------------------------------------------------------
  // Fixture setup and release
  //
  
  /**
   * Sets up the fixture, for example, open a network connection.
   * This method is called before a test is executed.
   */
  protected void setUp() throws Exception {}

  
  /**
   * Tears down the fixture, for example, close a network connection.
   * This method is called after a test is executed.
   */
  protected void tearDown() throws Exception {}
  
  
  //---------------------------------------------------------------------------
  // test cases
  //
 
  /**
   * Test the case, when a line comment is not terminated by a newline character.
   * This happens when the last line of a file is a line comment without a 
   * newline on its end.
   * This is a rather common situation.
   */
  public void testEOFInLineComment() throws Throwable {
    Reader              reader    = new StringReader("// end of file occurs in line comment.");
    TokenizerProperties props     = new StandardTokenizerProperties();
    StandardTokenizer   tokenizer = new StandardTokenizer(props);
    Token               token;

    props.setParseFlags(TokenizerProperties.F_RETURN_WHITESPACES);
    props.addLineComment("//");
    tokenizer.setSource(reader);

    assertTrue(tokenizer.hasMoreToken());
    token = tokenizer.nextToken();
    assertTrue(token.getType() == Token.LINE_COMMENT);
    assertTrue(tokenizer.hasMoreToken());
    token = tokenizer.nextToken();
    assertTrue(token.getType() == Token.EOF);
  }

  /**
   * Test the case, when a block comment is not terminated. That means EOF
   * occurs unexpectedly in a block comment.
   */
  public void testEOFInBlockComment() throws Throwable {
    Reader              reader    = new StringReader("/* end of file occurs\nin a block comment.");
    TokenizerProperties props     = new StandardTokenizerProperties();
    StandardTokenizer   tokenizer = new StandardTokenizer(props);
    Token               token;

    props.setParseFlags(TokenizerProperties.F_RETURN_WHITESPACES);
    props.addBlockComment("/*", "*/");
    tokenizer.setSource(reader);

    assertTrue(tokenizer.hasMoreToken());
    token = tokenizer.nextToken();
    assertTrue(token.getType() == Token.BLOCK_COMMENT);
    assertTrue(tokenizer.hasMoreToken());
    token = tokenizer.nextToken();
    assertTrue(token.getType() == Token.EOF);
  }

  /**
   * Test the case, when a block comment is not terminated. That means EOF
   * occurs unexpectedly in a block comment.
   */
  public void testEOFInString() throws Throwable {
    Reader              reader    = new StringReader("-- end of file in String\n\"Thats the string, but rather unterminated |-(");
    TokenizerProperties props     = new StandardTokenizerProperties();
    StandardTokenizer   tokenizer = new StandardTokenizer(props);
    Token               token;

    props.addLineComment("--");
    props.addString("\"", "\"", "\"");
    tokenizer.setSource(reader);

    assertTrue(tokenizer.hasMoreToken());
    token = tokenizer.nextToken();
    assertTrue(token.getType() == Token.STRING);
    assertTrue(tokenizer.hasMoreToken());
    token = tokenizer.nextToken();
    assertTrue(token.getType() == Token.EOF);
  }
  
  /**
   * Test various calls to methods with a special contract.
   */
  public void testSpecialCalls() throws Throwable {
    Reader              reader    = new StringReader("A simple text");
    TokenizerProperties props     = new StandardTokenizerProperties();
    StandardTokenizer   tokenizer = new StandardTokenizer(props);
    Token               token     = null;

    tokenizer.setSource(reader);

    assertTrue(tokenizer.currentToken() == null);
    assertTrue(tokenizer.currentImage() == null);
    
    while (tokenizer.hasMoreToken()) {
      Token newToken = tokenizer.nextToken();
      assertTrue( ! tokenizer.currentToken().equals(token));
      assertTrue(tokenizer.currentToken() != null);
      assertTrue(tokenizer.currentToken().equals(newToken));
      assertTrue(tokenizer.currentToken().equals(tokenizer.currentToken()));
      if (newToken.getType() != Token.EOF) {
        assertTrue(tokenizer.currentImage() != null);
        assertTrue(tokenizer.currentImage().equals(tokenizer.currentImage()));
      } else {
        assertTrue(tokenizer.currentImage() == null);
        assertTrue( ! tokenizer.hasMoreToken());
      }
      token = newToken;
    }
  }
  
  /**
   * Test various situations of string escapes, if the escape character is the
   * backslash (not equal to the string character).
   * This test takes a number of lines each with a string including escapes in
   * it. It passes if the right number of strings is returned and also the line
   * counting is ok.
   */
  public void testStringEscapes1() throws Throwable {
    Reader reader = new StringReader(
      "\"String escape \\\" in the middle\"\n"
    + "\"String escape on end \\\"\"\n"
    + "\"\\\" String escape on begin\"\n"
    + "\"Two string escapes \\\"\\\" after each other\"\n"
    + "\"Two string escapes on end \\\"\\\"\"\n");
    
    int                 lines     = 5;
    TokenizerProperties props     = new StandardTokenizerProperties();
    StandardTokenizer   tokenizer = new StandardTokenizer(props);
    Token               token;

    props.setParseFlags(TokenizerProperties.F_RETURN_WHITESPACES | TokenizerProperties.F_COUNT_LINES);
    props.addString("\"", "\"", "\\");
    tokenizer.setSource(reader);

    for (int line = 0; line < lines; ++line) {
      assertTrue("(1) No more token at line " + line, tokenizer.hasMoreToken());
      token = tokenizer.nextToken();
      assertTrue("String not recognized at line " + line, token.getType() == Token.STRING);
      assertTrue("(2) No more token at line " + line, tokenizer.hasMoreToken());
      token = tokenizer.nextToken();
      assertTrue("Newline not recognized as whitespace at line " + line, token.getType() == Token.WHITESPACE);
    }
    assertTrue(tokenizer.hasMoreToken());
    token = tokenizer.nextToken();
    assertTrue(token.getType() == Token.EOF);
  }

  /**
   * Test various situations of string escapes, if the escape character is equal
   * to the string character).
   * This test takes a number of lines each with a string including escapes in
   * it. It passes if the right number of strings is returned and also the line
   * counting is ok.
   */
  public void testStringEscapes2() throws Throwable {
    Reader reader = new StringReader(
      "'String escape '' in the middle'\n"
    + "'String escape on end '''\n"
    + "''' String escape on begin'\n"
    + "'Two string escapes '''' after each other'\n"
    + "'Two string escapes on end '''''\n");
    
    int       lines     = 5;
    
    TokenizerProperties props     = new StandardTokenizerProperties();
    StandardTokenizer   tokenizer = new StandardTokenizer(props);
    Token     token;

    props.setParseFlags(TokenizerProperties.F_RETURN_WHITESPACES | TokenizerProperties.F_COUNT_LINES);
    props.addString("'", "'", "'");
    tokenizer.setSource(reader);

    for (int line = 0; line < lines; ++line) {
      assertTrue("(1) No more token at line " + line, tokenizer.hasMoreToken());
      token = tokenizer.nextToken();
      assertTrue("String not recognized at line " + line, token.getType() == Token.STRING);
      assertTrue("(2) No more token at line " + line, tokenizer.hasMoreToken());
      token = tokenizer.nextToken();
      assertTrue("Newline not recognized as whitespace at line " + line, token.getType() == Token.WHITESPACE);
    }
    assertTrue(tokenizer.hasMoreToken());
    token = tokenizer.nextToken();
    assertTrue(token.getType() == Token.EOF);
  }

  /**
   * Test nested comments.
   */
  public void testNestedComments() throws Throwable {
    Reader reader = new StringReader(
      "// line comment including // line comment sequence\n"
    + "/* block comment with\n"
    + "  /* a nested block\n"
    + "     comment\n"
    + "  */\n"
    + "  normal token or not ?\n" 
    + "*/\n"
    + "// line comment with /* block comment */\n"
    + "'a string with // line comment'\n"
    + "'a string with /* block comment */'\n");
    
    int                 lines     = 10;
    TokenizerProperties props     = new StandardTokenizerProperties();
    StandardTokenizer   tokenizer = new StandardTokenizer(props);
    Token               token;

    props.setParseFlags(TokenizerProperties.F_RETURN_WHITESPACES 
                      | TokenizerProperties.F_COUNT_LINES
                      | TokenizerProperties.F_ALLOW_NESTED_COMMENTS);
    props.addLineComment(TokenizerProperties.DEFAULT_LINE_COMMENT);
    props.addBlockComment(TokenizerProperties.DEFAULT_BLOCK_COMMENT_START, TokenizerProperties.DEFAULT_BLOCK_COMMENT_END);
    props.addString("'", "'", "'");
    tokenizer.setSource(reader);

    // first line comment
    assertTrue(tokenizer.hasMoreToken());
    token = tokenizer.nextToken();
    assertTrue("(1) line comment not recognized", token.getType() == Token.LINE_COMMENT);
    assertTrue("(2) wrong start position  " + token.getStartPosition(), token.getStartPosition() == 0);
    assertTrue("(3) wrong start line " + token.getStartLine(), token.getStartLine() == 0);
    assertTrue("(4) wrong start column" + token.getStartColumn(), token.getStartColumn() == 0);
    assertTrue("(5) wrong end line " + token.getEndLine(), token.getEndLine() == token.getStartLine() + 1);
    assertTrue("(6) wrong end column" + token.getEndColumn(), token.getEndColumn() == 0);
    
    // block comment
    assertTrue(tokenizer.hasMoreToken());
    token = tokenizer.nextToken();
    assertTrue("(10) block comment not recognized", token.getType() == Token.BLOCK_COMMENT);
    assertTrue("(11) wrong start line " + token.getStartLine(), token.getStartLine() == 1);
    assertTrue("(12) wrong start column" + token.getStartColumn(), token.getStartColumn() == 0);
    assertTrue("(13) wrong end line " + token.getEndLine(), token.getEndLine() == token.getStartLine() + 5);
    assertTrue("(14) wrong end column" + token.getEndColumn(), token.getEndColumn() == 2);
    assertTrue(tokenizer.hasMoreToken());
    token = tokenizer.nextToken();
    assertTrue("(15) newline behind block comment not recognized as whitespace", token.getType() == Token.WHITESPACE);
    assertTrue("(16) newline behind block comment not recognized as literal", tokenizer.currentImage().equals("\n"));

    // second line comment
    assertTrue(tokenizer.hasMoreToken());
    token = tokenizer.nextToken();
    assertTrue("(21) line comment not recognized", token.getType() == Token.LINE_COMMENT);
    assertTrue("(22) wrong start line " + token.getStartLine(), token.getStartLine() == 7);
    assertTrue("(23) wrong end line " + token.getEndLine(), token.getEndLine() == token.getStartLine() + 1);
    
    // string 1
    assertTrue(tokenizer.hasMoreToken());
    token = tokenizer.nextToken();
    assertTrue("(31) string not recognized", token.getType() == Token.STRING);
    assertTrue("(32) wrong start line " + token.getStartLine(), token.getStartLine() == 8);
    assertTrue("(33) wrong start column" + token.getStartColumn(), token.getStartColumn() == 0);
    assertTrue("(34) wrong end line " + token.getEndLine(), token.getEndLine() == token.getStartLine());
    assertTrue(tokenizer.hasMoreToken());
    token = tokenizer.nextToken();
    assertTrue("(35) newline behind string not recognized as whitespace", token.getType() == Token.WHITESPACE);
    assertTrue("(36) newline behind string not recognized as literal", tokenizer.currentImage().equals("\n"));
    
    // string 2
    assertTrue(tokenizer.hasMoreToken());
    token = tokenizer.nextToken();
    assertTrue("(41) string not recognized", token.getType() == Token.STRING);
    assertTrue("(42) wrong start line " + token.getStartLine(), token.getStartLine() == 9);
    assertTrue("(43) wrong start column" + token.getStartColumn(), token.getStartColumn() == 0);
    assertTrue("(44) wrong end line " + token.getEndLine(), token.getEndLine() == token.getStartLine());
    assertTrue(tokenizer.hasMoreToken());
    token = tokenizer.nextToken();
    assertTrue("(45) newline behind string not recognized as whitespace", token.getType() == Token.WHITESPACE);
    assertTrue("(46) newline behind string not recognized as literal", tokenizer.currentImage().equals("\n"));
    
    // EOF should be reached here
    token = tokenizer.nextToken();
    assertTrue(token.getType() == Token.EOF);
  }
  
  
  /**
   * Test reader switching
   */
  public void testReaderSwitching() throws Throwable {
    Reader reader1 = new StringReader("0/2 4/6 8/10");
    Reader reader2 = new StringReader("0/2 4/6 8/10");
    Reader reader3 = new StringReader("0/2 4/6 8/10");
    Reader[] readers = { reader1, reader2, reader3 };
    
    TokenizerProperties props     = new StandardTokenizerProperties();
    StandardTokenizer   tokenizer = new StandardTokenizer(props);
    Token               token;

    for (int readerIdx = 0; readerIdx < readers.length; ++readerIdx) {
      tokenizer.setSource(readers[readerIdx]);
      for (int ii = 0; ii <= 8; ii += 4) {
        assertTrue(tokenizer.hasMoreToken());
        token = tokenizer.nextToken();
        assertTrue("Wrong start position " + token.getStartPosition(), token.getStartPosition() == ii);
        assertTrue("Wrong type " + token.getType(), token.getType() == Token.NORMAL);
        assertTrue("Token not recognized as literal", tokenizer.currentImage().equals(Integer.toString(ii)));
        assertTrue(tokenizer.hasMoreToken());
        token = tokenizer.nextToken();
        assertTrue("Wrong start position " + token.getStartPosition(), token.getStartPosition() == ii + 1);
        assertTrue("Wrong type " + token.getType(), token.getType() == Token.SEPARATOR);
        assertTrue("Separator not recognized as literal", tokenizer.currentImage().equals("/"));
        assertTrue(tokenizer.hasMoreToken());
        token = tokenizer.nextToken();
        assertTrue("Wrong start position " + token.getStartPosition(), token.getStartPosition() == ii + 2);
        assertTrue("Wrong type " + token.getType(), token.getType() == Token.NORMAL);
        assertTrue("Token not recognized as literal", tokenizer.currentImage().equals(Integer.toString(ii + 2)));
      }
    }
  }


  /**
   * Line counting and line comments in DOS files
   */
  public void testDOSEOL() throws Throwable {
    Reader reader = new StringReader(
      "// line comment with DOS line ending\r\n"
    + "void main(int argc)\r\n"
    + "{\r\n"
    + "  // another line comment\r\n"
    + "  /* a block comment\r\n"
    + "     with more than one line\r\n" 
    + "  */\r\n"
    + "}\r\n");
    
    int       lines     = 8;
    TokenizerProperties props     = new StandardTokenizerProperties();
    StandardTokenizer   tokenizer = new StandardTokenizer(props);
    Token     token;

    props.setParseFlags(TokenizerProperties.F_RETURN_WHITESPACES | TokenizerProperties.F_COUNT_LINES);
    props.addLineComment(TokenizerProperties.DEFAULT_LINE_COMMENT);
    props.addBlockComment(TokenizerProperties.DEFAULT_BLOCK_COMMENT_START, TokenizerProperties.DEFAULT_BLOCK_COMMENT_END);
    props.addString("\"", "\"", "\\");
    tokenizer.setSource(reader);

    // zero line comment
    assertTrue(tokenizer.hasMoreToken());
    token = tokenizer.nextToken();
    assertTrue("(1) line comment not recognized", token.getType() == Token.LINE_COMMENT);
    assertTrue("(2) start line wrong", token.getStartLine() == 0);
    assertTrue("(3) start column wrong", token.getStartColumn() == 0);
    assertTrue("(4) end line wrong", token.getEndLine() == 1);
    assertTrue("(5) end column wrong", token.getEndColumn() == 0);

    // first line: void
    assertTrue(tokenizer.hasMoreToken());
    token = tokenizer.nextToken();
    assertTrue("(10) token \"void\" not recognized.", token.getType() == Token.NORMAL && token.getImage().equals("void"));
    assertTrue("(11) start line wrong", token.getStartLine() == 1);
    assertTrue("(12) start column wrong", token.getStartColumn() == 0);
    assertTrue("(13) end line wrong", token.getEndLine() == 1);
    assertTrue("(14) end column wrong", token.getEndColumn() == 4);

    assertTrue(tokenizer.hasMoreToken());
    token = tokenizer.nextToken();
    assertTrue("(15) whitespace not recognized", token.getType() == Token.WHITESPACE);
    
    // first line: main
    assertTrue(tokenizer.hasMoreToken());
    token = tokenizer.nextToken();
    assertTrue("(20) token \"main\" not recognized.", token.getType() == Token.NORMAL && token.getImage().equals("main"));
    assertTrue("(21) start line wrong", token.getStartLine() == 1);
    assertTrue("(22) start column wrong", token.getStartColumn() == 5);
    assertTrue("(23) end line wrong", token.getEndLine() == 1);
    assertTrue("(24) end column wrong", token.getEndColumn() == 9);

    // first line: (
    assertTrue(tokenizer.hasMoreToken());
    token = tokenizer.nextToken();
    assertTrue("(30) token \"(\" not recognized.", token.getType() == Token.SEPARATOR && token.getImage().equals("("));
    assertTrue("(31) start line wrong", token.getStartLine() == 1);
    assertTrue("(32) start column wrong", token.getStartColumn() == 9);
    assertTrue("(33) end line wrong", token.getEndLine() == 1);
    assertTrue("(34) end column wrong", token.getEndColumn() == 10);

    // first line: int
    assertTrue(tokenizer.hasMoreToken());
    token = tokenizer.nextToken();
    assertTrue("(40) token \"int\" not recognized.", token.getType() == Token.NORMAL && token.getImage().equals("int"));
    assertTrue("(41) start line wrong", token.getStartLine() == 1);
    assertTrue("(42) start column wrong", token.getStartColumn() == 10);
    assertTrue("(43) end line wrong", token.getEndLine() == 1);
    assertTrue("(44) end column wrong", token.getEndColumn() == 13);

    assertTrue(tokenizer.hasMoreToken());
    token = tokenizer.nextToken();
    assertTrue("(45) whitespace not recognized", token.getType() == Token.WHITESPACE);

    // first line: argc
    assertTrue(tokenizer.hasMoreToken());
    token = tokenizer.nextToken();
    assertTrue("(50) token \"argc\" not recognized.", token.getType() == Token.NORMAL && token.getImage().equals("argc"));
    assertTrue("(51) start line wrong", token.getStartLine() == 1);
    assertTrue("(52) start column wrong", token.getStartColumn() == 14);
    assertTrue("(53) end line wrong", token.getEndLine() == 1);
    assertTrue("(54) end column wrong", token.getEndColumn() == 18);

    // first line: )
    assertTrue(tokenizer.hasMoreToken());
    token = tokenizer.nextToken();
    assertTrue("(60) token \")\" not recognized.", token.getType() == Token.SEPARATOR && token.getImage().equals(")"));
    assertTrue("(61) start line wrong", token.getStartLine() == 1);
    assertTrue("(62) start column wrong", token.getStartColumn() == 18);
    assertTrue("(63) end line wrong", token.getEndLine() == 1);
    assertTrue("(64) end column wrong", token.getEndColumn() == 19);

    // first line: EOL
    assertTrue(tokenizer.hasMoreToken());
    token = tokenizer.nextToken();
    assertTrue("(60) token \"\\r\\n\" not recognized.", token.getType() == Token.WHITESPACE && token.getImage().equals("\r\n"));
    assertTrue("(61) start line wrong", token.getStartLine() == 1);
    assertTrue("(62) start column wrong", token.getStartColumn() == 19);
    assertTrue("(63) end line wrong", token.getEndLine() == 2);
    assertTrue("(64) end column wrong", token.getEndColumn() == 0);
    assertTrue("(65) wrong length", token.getLength() == 2);

    // second line: {
    assertTrue(tokenizer.hasMoreToken());
    token = tokenizer.nextToken();
    assertTrue("(70) token \"{\" not recognized.", token.getType() == Token.SEPARATOR && token.getImage().equals("{"));
    assertTrue("(71) start line wrong", token.getStartLine() == 2);
    assertTrue("(72) start column wrong", token.getStartColumn() == 0);
    assertTrue("(73) end line wrong", token.getEndLine() == 2);
    assertTrue("(74) end column wrong", token.getEndColumn() == 1);

    // second/third line: EOL + whitespaces
    assertTrue(tokenizer.hasMoreToken());
    token = tokenizer.nextToken();
    assertTrue("(80) token \"\\r\\n  \" not recognized.", token.getType() == Token.WHITESPACE && token.getImage().equals("\r\n  "));
    assertTrue("(81) start line wrong", token.getStartLine() == 2);
    assertTrue("(82) start column wrong", token.getStartColumn() == 1);
    assertTrue("(83) end line wrong", token.getEndLine() == 3);
    assertTrue("(84) end column wrong", token.getEndColumn() == 2);
    assertTrue("(85) wrong length", token.getLength() == 4);

    // third line: line comment
    assertTrue(tokenizer.hasMoreToken());
    token = tokenizer.nextToken();
    assertTrue("(91) line comment not recognized", token.getType() == Token.LINE_COMMENT);
    assertTrue("(92) start line wrong", token.getStartLine() == 3);
    assertTrue("(93) start column wrong", token.getStartColumn() == 2);
    assertTrue("(94) end line wrong", token.getEndLine() == 4);
    assertTrue("(95) end column wrong", token.getEndColumn() == 0);

    assertTrue(tokenizer.hasMoreToken());
    token = tokenizer.nextToken();
    assertTrue("(96) whitespace not recognized", token.getType() == Token.WHITESPACE);

    // forth line: block comment
    assertTrue(tokenizer.hasMoreToken());
    token = tokenizer.nextToken();
    assertTrue("(101) block comment not recognized", token.getType() == Token.BLOCK_COMMENT);
    assertTrue("(102) start line wrong", token.getStartLine() == 4);
    assertTrue("(103) start column wrong", token.getStartColumn() == 2);
    assertTrue("(104) end line wrong", token.getEndLine() == 6);
    assertTrue("(105) end column wrong", token.getEndColumn() == 4);

    // 6th line: EOL
    assertTrue(tokenizer.hasMoreToken());
    token = tokenizer.nextToken();
    assertTrue("(110) token \"\\r\\n\" not recognized.", token.getType() == Token.WHITESPACE && token.getImage().equals("\r\n"));
    assertTrue("(111) start line wrong", token.getStartLine() == 6);
    assertTrue("(112) start column wrong", token.getStartColumn() == 4);
    assertTrue("(113) end line wrong", token.getEndLine() == 7);
    assertTrue("(114) end column wrong", token.getEndColumn() == 0);
    assertTrue("(115) wrong length", token.getLength() == 2);

    // 7th line: }
    assertTrue(tokenizer.hasMoreToken());
    token = tokenizer.nextToken();
    assertTrue("(120) token \"}\" not recognized.", token.getType() == Token.SEPARATOR && token.getImage().equals("}"));
    assertTrue("(121) start line wrong", token.getStartLine() == 7);
    assertTrue("(122) start column wrong", token.getStartColumn() == 0);
    assertTrue("(123) end line wrong", token.getEndLine() == 7);
    assertTrue("(124) end column wrong", token.getEndColumn() == 1);

    // 7th line: EOL
    assertTrue(tokenizer.hasMoreToken());
    token = tokenizer.nextToken();
    assertTrue("(130) token \"\\r\\n\" not recognized.", token.getType() == Token.WHITESPACE && token.getImage().equals("\r\n"));
    assertTrue("(131) start line wrong", token.getStartLine() == 7);
    assertTrue("(132) start column wrong", token.getStartColumn() == 1);
    assertTrue("(133) end line wrong", token.getEndLine() == 8);
    assertTrue("(134) end column wrong", token.getEndColumn() == 0);
    assertTrue("(135) wrong length", token.getLength() == 2);
  }

  /**
   * Line counting and line comments in MAC files
   */
  public void testMACEOL() throws Throwable {
    Reader reader = new StringReader(
      "// line comment with DOS line ending\r"
    + "void main(int argc)\r"
    + "{\r"
    + "  // another line comment\r"
    + "  /* a block comment\r"
    + "     with more than one line\r" 
    + "  */\r"
    + "}\r");
    
    int                 lines     = 8;
    TokenizerProperties props     = new StandardTokenizerProperties();
    StandardTokenizer   tokenizer = new StandardTokenizer(props);
    Token               token;

    props.setParseFlags(TokenizerProperties.F_RETURN_WHITESPACES | TokenizerProperties.F_COUNT_LINES);
    props.addLineComment(TokenizerProperties.DEFAULT_LINE_COMMENT);
    props.addBlockComment(TokenizerProperties.DEFAULT_BLOCK_COMMENT_START, TokenizerProperties.DEFAULT_BLOCK_COMMENT_END);
    props.addString("\"", "\"", "\\");
    tokenizer.setSource(reader);

    // zero line comment
    assertTrue(tokenizer.hasMoreToken());
    token = tokenizer.nextToken();
    assertTrue("(1) line comment not recognized", token.getType() == Token.LINE_COMMENT);
    assertTrue("(2) start line wrong", token.getStartLine() == 0);
    assertTrue("(3) start column wrong", token.getStartColumn() == 0);
    assertTrue("(4) end line wrong", token.getEndLine() == 1);
    assertTrue("(5) end column wrong", token.getEndColumn() == 0);

    // first line: void
    assertTrue(tokenizer.hasMoreToken());
    token = tokenizer.nextToken();
    assertTrue("(10) token \"void\" not recognized.", token.getType() == Token.NORMAL && token.getImage().equals("void"));
    assertTrue("(11) start line wrong", token.getStartLine() == 1);
    assertTrue("(12) start column wrong", token.getStartColumn() == 0);
    assertTrue("(13) end line wrong", token.getEndLine() == 1);
    assertTrue("(14) end column wrong", token.getEndColumn() == 4);

    assertTrue(tokenizer.hasMoreToken());
    token = tokenizer.nextToken();
    assertTrue("(15) whitespace not recognized", token.getType() == Token.WHITESPACE);
    
    // first line: main
    assertTrue(tokenizer.hasMoreToken());
    token = tokenizer.nextToken();
    assertTrue("(20) token \"main\" not recognized.", token.getType() == Token.NORMAL && token.getImage().equals("main"));
    assertTrue("(21) start line wrong", token.getStartLine() == 1);
    assertTrue("(22) start column wrong", token.getStartColumn() == 5);
    assertTrue("(23) end line wrong", token.getEndLine() == 1);
    assertTrue("(24) end column wrong", token.getEndColumn() == 9);

    // first line: (
    assertTrue(tokenizer.hasMoreToken());
    token = tokenizer.nextToken();
    assertTrue("(30) token \"(\" not recognized.", token.getType() == Token.SEPARATOR && token.getImage().equals("("));
    assertTrue("(31) start line wrong", token.getStartLine() == 1);
    assertTrue("(32) start column wrong", token.getStartColumn() == 9);
    assertTrue("(33) end line wrong", token.getEndLine() == 1);
    assertTrue("(34) end column wrong", token.getEndColumn() == 10);

    // first line: int
    assertTrue(tokenizer.hasMoreToken());
    token = tokenizer.nextToken();
    assertTrue("(40) token \"int\" not recognized.", token.getType() == Token.NORMAL && token.getImage().equals("int"));
    assertTrue("(41) start line wrong", token.getStartLine() == 1);
    assertTrue("(42) start column wrong", token.getStartColumn() == 10);
    assertTrue("(43) end line wrong", token.getEndLine() == 1);
    assertTrue("(44) end column wrong", token.getEndColumn() == 13);

    assertTrue(tokenizer.hasMoreToken());
    token = tokenizer.nextToken();
    assertTrue("(45) whitespace not recognized", token.getType() == Token.WHITESPACE);

    // first line: argc
    assertTrue(tokenizer.hasMoreToken());
    token = tokenizer.nextToken();
    assertTrue("(50) token \"argc\" not recognized.", token.getType() == Token.NORMAL && token.getImage().equals("argc"));
    assertTrue("(51) start line wrong", token.getStartLine() == 1);
    assertTrue("(52) start column wrong", token.getStartColumn() == 14);
    assertTrue("(53) end line wrong", token.getEndLine() == 1);
    assertTrue("(54) end column wrong", token.getEndColumn() == 18);

    // first line: )
    assertTrue(tokenizer.hasMoreToken());
    token = tokenizer.nextToken();
    assertTrue("(60) token \")\" not recognized.", token.getType() == Token.SEPARATOR && token.getImage().equals(")"));
    assertTrue("(61) start line wrong", token.getStartLine() == 1);
    assertTrue("(62) start column wrong", token.getStartColumn() == 18);
    assertTrue("(63) end line wrong", token.getEndLine() == 1);
    assertTrue("(64) end column wrong", token.getEndColumn() == 19);

    // first line: EOL
    assertTrue(tokenizer.hasMoreToken());
    token = tokenizer.nextToken();
    assertTrue("(60) token \"\\r\" not recognized.", token.getType() == Token.WHITESPACE && token.getImage().equals("\r"));
    assertTrue("(61) start line wrong", token.getStartLine() == 1);
    assertTrue("(62) start column wrong", token.getStartColumn() == 19);
    assertTrue("(63) end line wrong", token.getEndLine() == 2);
    assertTrue("(64) end column wrong", token.getEndColumn() == 0);
    assertTrue("(65) wrong length", token.getLength() == 1);

    // second line: {
    assertTrue(tokenizer.hasMoreToken());
    token = tokenizer.nextToken();
    assertTrue("(70) token \"{\" not recognized.", token.getType() == Token.SEPARATOR && token.getImage().equals("{"));
    assertTrue("(71) start line wrong", token.getStartLine() == 2);
    assertTrue("(72) start column wrong", token.getStartColumn() == 0);
    assertTrue("(73) end line wrong", token.getEndLine() == 2);
    assertTrue("(74) end column wrong", token.getEndColumn() == 1);

    // second/third line: EOL + whitespaces
    assertTrue(tokenizer.hasMoreToken());
    token = tokenizer.nextToken();
    assertTrue("(80) token \"\\r  \" not recognized.", token.getType() == Token.WHITESPACE && token.getImage().equals("\r  "));
    assertTrue("(81) start line wrong", token.getStartLine() == 2);
    assertTrue("(82) start column wrong", token.getStartColumn() == 1);
    assertTrue("(83) end line wrong", token.getEndLine() == 3);
    assertTrue("(84) end column wrong", token.getEndColumn() == 2);
    assertTrue("(85) wrong length", token.getLength() == 3);

    // third line: line comment
    assertTrue(tokenizer.hasMoreToken());
    token = tokenizer.nextToken();
    assertTrue("(91) line comment not recognized", token.getType() == Token.LINE_COMMENT);
    assertTrue("(92) start line wrong", token.getStartLine() == 3);
    assertTrue("(93) start column wrong", token.getStartColumn() == 2);
    assertTrue("(94) end line wrong", token.getEndLine() == 4);
    assertTrue("(95) end column wrong", token.getEndColumn() == 0);

    assertTrue(tokenizer.hasMoreToken());
    token = tokenizer.nextToken();
    assertTrue("(96) whitespace not recognized", token.getType() == Token.WHITESPACE);

    // forth line: block comment
    assertTrue(tokenizer.hasMoreToken());
    token = tokenizer.nextToken();
    assertTrue("(101) block comment not recognized", token.getType() == Token.BLOCK_COMMENT);
    assertTrue("(102) start line wrong", token.getStartLine() == 4);
    assertTrue("(103) start column wrong", token.getStartColumn() == 2);
    assertTrue("(104) end line wrong", token.getEndLine() == 6);
    assertTrue("(105) end column wrong", token.getEndColumn() == 4);

    // 6th line: EOL
    assertTrue(tokenizer.hasMoreToken());
    token = tokenizer.nextToken();
    assertTrue("(110) token \"\\r\" not recognized.", token.getType() == Token.WHITESPACE && token.getImage().equals("\r"));
    assertTrue("(111) start line wrong", token.getStartLine() == 6);
    assertTrue("(112) start column wrong", token.getStartColumn() == 4);
    assertTrue("(113) end line wrong", token.getEndLine() == 7);
    assertTrue("(114) end column wrong", token.getEndColumn() == 0);
    assertTrue("(115) wrong length", token.getLength() == 1);

    // 7th line: }
    assertTrue(tokenizer.hasMoreToken());
    token = tokenizer.nextToken();
    assertTrue("(120) token \"}\" not recognized.", token.getType() == Token.SEPARATOR && token.getImage().equals("}"));
    assertTrue("(121) start line wrong", token.getStartLine() == 7);
    assertTrue("(122) start column wrong", token.getStartColumn() == 0);
    assertTrue("(123) end line wrong", token.getEndLine() == 7);
    assertTrue("(124) end column wrong", token.getEndColumn() == 1);

    // 7th line: EOL
    assertTrue(tokenizer.hasMoreToken());
    token = tokenizer.nextToken();
    assertTrue("(130) token \"\\r\" not recognized.", token.getType() == Token.WHITESPACE && token.getImage().equals("\r"));
    assertTrue("(131) start line wrong", token.getStartLine() == 7);
    assertTrue("(132) start column wrong", token.getStartColumn() == 1);
    assertTrue("(133) end line wrong", token.getEndLine() == 8);
    assertTrue("(134) end column wrong", token.getEndColumn() == 0);
    assertTrue("(135) wrong length", token.getLength() == 1);
  }
}  

