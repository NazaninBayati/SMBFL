/*
 * TestStandardTokenizer.java: JUnit test for the StandardTokenizer
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
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Vector;
import java.util.Properties;
import java.net.URL;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.framework.Assert;
import junit.swingui.TestRunner;

import de.susebox.java.lang.ExtRuntimeException;


//-----------------------------------------------------------------------------
// Class TestStandardTokenizer
//

/**<p>
 * This test suite works with a test configuration file. This file contains some
 * sets of properties, each set for one or more different test runs.
 *</p><p>
 * The properties are defined as class constants. In the configuration file, a 
 * property consists of the property name and a number identifying the property
 * set. 
 *</p>
 *
 * @see     Tokenizer
 * @see     AbstractTokenizer
 * @see     java.io.InputStreamReader
 * @author  Heiko Blau
 */
public class TestStandardTokenizer extends TestCase {
  
  //---------------------------------------------------------------------------
  // properties
  //

  /**
   * The name of the test configuration file. This file will be read by 
   * {@link java.lang.Class#getResourceAsStream}.
   */
  public static final String CONFIG_FILE = "TestStandardTokenizer.conf";
  
  /**
   * Property for the tests {@link #testLinkParsing} and {@link #testContentsParsing}
   */
  public static final String PROP_PATH = "Path";
  
  /**
   * Property for the test {@link #testLineCounting}.
   */
  public static final String PROP_COUNTLINES_PATH = "CountLinesPath";
  
  
  //---------------------------------------------------------------------------
  // main method
  //
  
  /**
   * call this method to invoke the tests
   */
  public static void main(String[] args) {
    String[]   tests = { TestStandardTokenizer.class.getName() };
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
    Properties  props = new Properties();
    int         count = 1;
    String      path;
    URL         url;
    
    try {
      props.load(TestStandardTokenizer.class.getResourceAsStream(CONFIG_FILE));
    } catch (Exception ex) {
      throw new ExtRuntimeException(ex);
    }
    
    while ((path = props.getProperty(PROP_PATH + count)) != null) {
      if ((url = TestStandardTokenizer.class.getResource(path)) != null) {
        path = url.getFile();
      }
      suite.addTest(new TestStandardTokenizer("testLinkParsing",        path));
      suite.addTest(new TestStandardTokenizer("testContentsParsing",    path));
      suite.addTest(new TestStandardTokenizer("testContentsFormatting", path));
      count++;
    }
    count = 1;
    while ((path = props.getProperty(PROP_COUNTLINES_PATH + count)) != null) {
      if ((url = TestStandardTokenizer.class.getResource(path)) != null) {
        path = url.getFile();
      }
      suite.addTest(new TestStandardTokenizer("testLineCounting", path));
      count++;
    }
    return suite;
  }
  
  
  //---------------------------------------------------------------------------
  // Constructor
  //
  
  /**
   * Default constructor. Standard input {@link java.lang.System#in} is used
   * to construct the input stream reader.
   */  
  public TestStandardTokenizer(String test, String path) {
    super(test);
    _path = path;
  }

  
  //---------------------------------------------------------------------------
  // Fixture setup and release
  //
  
	/**
	 * Sets up the fixture, for example, open a network connection.
	 * This method is called before a test is executed.
	 */
  protected void setUp() throws Exception {
    InputStream  stream = new FileInputStream(_path);
    
    _reader = new InputStreamReader(stream);
	}

  
	/**
	 * Tears down the fixture, for example, close a network connection.
	 * This method is called after a test is executed.
	 */
	protected void tearDown() throws Exception {
    _reader.close();
	}
  
  //---------------------------------------------------------------------------
  // test cases
  //
  
  public void testLinkParsing() throws Throwable {
    long                start     = System.currentTimeMillis();
    TokenizerProperties props     = new StandardTokenizerProperties();
    Tokenizer           tokenizer = new StandardTokenizer(props);
    Vector              links     = new Vector();
    Token               token;

    props.setParseFlags(TokenizerProperties.F_NO_CASE);
    props.setSeparators("=");
    props.addString("\"", "\"", "\\");
    props.addBlockComment(">", "<");            // overread everything outside of tags
    props.addBlockComment("SCRIPT", "/SCRIPT"); // overread script parts
    props.addBlockComment("!--", "--");         // overread HTML comments without < and >
    props.addKeyword("HREF");
    tokenizer.setSource(new ReaderSource(_reader));
    
    //    System.out.println("\nStart looking for links in \"" + _path + "\"");
    while (tokenizer.hasMoreToken()) {
      token = tokenizer.nextToken();
      if (token.getType() == Token.KEYWORD) {
        tokenizer.nextToken();               // should be the '=' character
	//        System.out.println("  " + tokenizer.nextImage());
	tokenizer.nextToken();
        assertTrue(tokenizer.currentImage() != null);
        assertTrue(tokenizer.currentToken().getType() == Token.STRING);
      }
    }

    long diff = System.currentTimeMillis() - start;
    //    System.out.println("Finished after " + diff + " milliseconds");
  }
  
  
  /**
   * Extracting the pure contents of a HTML stream.
   */
  public void testContentsParsing() throws Throwable {
    long                start     = System.currentTimeMillis();
    TokenizerProperties props     = new StandardTokenizerProperties();
    Tokenizer           tokenizer = new StandardTokenizer(props);

    tokenizer.setSource(new ReaderSource(_reader));
    //    System.out.println("\nStart extracting contents in \"" + _path + "\"");
    
    props.setParseFlags(TokenizerProperties.F_NO_CASE | TokenizerProperties.F_TOKEN_POS_ONLY);
    props.setWhitespaces(null);
    props.setSeparators(null);
    props.addBlockComment("<", ">");            // overread HTML tags
    props.addBlockComment("<HEAD>", "</HEAD>"); // overread HTML header
    props.addBlockComment("<!--", "-->");       // overread HTML comments

    while (tokenizer.hasMoreToken()) {
      tokenizer.nextToken();
      if (tokenizer.currentToken().getType() != Token.EOF) {
	  //        System.out.println(tokenizer.currentImage());
        assertTrue("Method currentImage() returned null.", tokenizer.currentImage() != null);
      }
      assertTrue("Found token type " + tokenizer.currentToken().getType() 
             + ", expected NORMAL (" + Token.NORMAL + ") or EOF (" + Token.EOF + ").",
             tokenizer.currentToken().getType() == Token.NORMAL 
             || tokenizer.currentToken().getType() == Token.EOF);
    }
    
    long diff = System.currentTimeMillis() - start;
    //    System.out.println("Finished after " + diff + " milliseconds");
  }
  
  
  /**
   * Testing the line and column counting correctness. This is done by using a
   * specially formatted file. At a line x and a column y, the method expects
   * the token "x/y", e.g. "0/0" at the very beginning of the file.
   */
  public void testLineCounting() throws Throwable {
    long                start     = System.currentTimeMillis();
    TokenizerProperties props     = new StandardTokenizerProperties();
    Tokenizer           tokenizer = new StandardTokenizer(props);
    Token               token;
    String              s;
    int                 delPos;
    int                 line;
    int                 col;

    //    System.out.println("\nStart counting lines in \"" + _path + "\"");
    
    tokenizer.setSource(new ReaderSource(_reader));
    props.setParseFlags(TokenizerProperties.F_TOKEN_POS_ONLY | TokenizerProperties.F_COUNT_LINES);
    props.setWhitespaces(TokenizerProperties.DEFAULT_WHITESPACES);
    props.setSeparators(TokenizerProperties.DEFAULT_SEPARATORS);
    props.addLineComment(TokenizerProperties.DEFAULT_LINE_COMMENT);
    props.addBlockComment(TokenizerProperties.DEFAULT_BLOCK_COMMENT_START, TokenizerProperties.DEFAULT_BLOCK_COMMENT_END);

    while (tokenizer.hasMoreToken()) {
      token = tokenizer.nextToken();
      switch (token.getType()) {
      case Token.NORMAL:
        s    = tokenizer.currentImage();
        line = Integer.parseInt(s);
        assertTrue("Missing separator \"/\".",
               tokenizer.nextToken().getType() == Token.SEPARATOR && tokenizer.currentImage().equals("/"));
        assertTrue("Missing column number",
               tokenizer.nextToken().getType() == Token.NORMAL);
        s   = tokenizer.currentImage();
        col = Integer.parseInt(s);
        assertTrue("Found line number " + token.getStartLine() + " does not match expected line number " + line, 
               line == token.getStartLine());
        assertTrue("Found column number " + token.getStartColumn() + " does not match expected column number " + col, 
               col == token.getStartColumn());
        assertTrue("Found token length " + tokenizer.currentToken().getLength() + " does not match expected length " + s.length(), 
               s.length() == tokenizer.currentToken().getLength());
        break;
      }
    }
    
    long diff = System.currentTimeMillis() - start;
    //    System.out.println("Finished after " + diff + " milliseconds");
  }
  

  /**
   * Advanced contents extracting. Lines will be around 80 characters, a basic
   * paragraph recognition takes place.
   */
  public void testContentsFormatting() throws Throwable{
    long                start     = System.currentTimeMillis();
    TokenizerProperties props     = new StandardTokenizerProperties();
    Tokenizer           tokenizer = new StandardTokenizer(props);
    Token               token;
    int                 len;
    int                 caseFlags; 
    Object              startPRE  = new Object();
    Object              endPRE    = new Object();
    int                 inPRE     = 0;
    
    // Counter for expected parts
    int       wsCount       = 0;
    int       normalCount   = 0;
    int       specCount     = 0;
    int       commentCount  = 0;

    //    System.out.println("\nStart formatting contents in \"" + _path + "\"");

    tokenizer.setSource(new ReaderSource(_reader));
    props.setParseFlags( TokenizerProperties.F_NO_CASE 
                       | TokenizerProperties.F_TOKEN_POS_ONLY 
                       | TokenizerProperties.F_RETURN_WHITESPACES);
    caseFlags = props.getParseFlags() & ~TokenizerProperties.F_NO_CASE;
    props.setSeparators(null);
    props.addBlockComment("<", ">");
    props.addBlockComment("<HEAD>", "</HEAD>");
    props.addBlockComment("<!--", "-->");
    props.addSpecialSequence("&amp;lt;", "<");
    props.addSpecialSequence("&amp;gt;", ">");                   
    props.addSpecialSequence("<b>", "");
    props.addSpecialSequence("</b>", "");
    props.addSpecialSequence("<i>", "");
    props.addSpecialSequence("</i>", "");
    props.addSpecialSequence("<code>", "");
    props.addSpecialSequence("</code>", "");
    props.addSpecialSequence("<pre>",  startPRE);
    props.addSpecialSequence("</pre>", endPRE);
    props.addSpecialSequence("&auml;",  "�", caseFlags);
    props.addSpecialSequence("&ouml;",  "�", caseFlags);
    props.addSpecialSequence("&uuml;",  "�", caseFlags);
    props.addSpecialSequence("&szlig;", "�", caseFlags);                   
    props.addSpecialSequence("&Auml;",  "�", caseFlags);
    props.addSpecialSequence("&ouml;",  "�", caseFlags);
    props.addSpecialSequence("&uuml;",  "�", caseFlags);

    len = 0;
    while (tokenizer.hasMoreToken()) {
      token = tokenizer.nextToken();
      switch (token.getType()) {
      case Token.NORMAL:
	  //        System.out.print(tokenizer.currentImage());
        if (inPRE <= 0) {
          len += token.getLength();
        }
        normalCount++;
        break;
        
      case Token.SPECIAL_SEQUENCE:
        if (token.getCompanion() == startPRE) {
	    //          System.out.println();
          len   = 0;
          inPRE++;
        } else if (token.getCompanion() == endPRE) {
	    //          System.out.println();
          len   = 0;
          inPRE--;
        } else {
	    //          System.out.print((String)token.getCompanion());
        }
        specCount++;
        break;
        
      case Token.BLOCK_COMMENT:
        if (len > 0) {
	    //          System.out.println();
          len = 0;
        }
        commentCount++;
        break;
        
      case Token.WHITESPACE:
        if (inPRE > 0) {
	    //          System.out.print(tokenizer.currentImage());
        } else if (len > 75) {
	    //          System.out.println();
          len = 0;
        } else if (len > 0) {
	    //          System.out.print(' ');
          len++;
        }
        wsCount++;
        break;
      }
    }
    
    // Where should have been something of each categorie
    assertTrue("Not one simple context part was found in file " + _path + ".", normalCount > 0);
    assertTrue("No HTML tag found " + _path + ".", commentCount > 0);
    assertTrue("No whitespaces found " + _path + ".", wsCount > 0);

    long diff = System.currentTimeMillis() - start;
    //    System.out.println("Finished after " + diff + " milliseconds");
  }
  
  //---------------------------------------------------------------------------
  // Members
  //
  protected InputStreamReader _reader = null;
  protected String            _path   = null;
}
