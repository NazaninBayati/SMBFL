/*
 * TestEmbeddedTokenizer.java: JUnit test for the InputStreamTokenizer
 *
 * Copyright (C) 2001 Heiko Blau
 *
 * This file belongs to the Susebox Java core test suite.
 * The Susebox Java core test suite is free software; you can redistribute it 
 * and/or modify it under the terms of the GNU Lesser General Public License as 
 * published by the Free Software Foundation; either version 2.1 of the License, 
 * or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or 
 * FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along 
 * with the Susebox Java core test suite. If not, write to the
 *
 *   Free Software Foundation, Inc.
 *   59 Temple Place, Suite 330, 
 *   Boston, MA 02111-1307 
 *   USA
 *
 * or check the Internet: http://www.fsf.org
 *
 * The Susebox Java core test suite uses the test framework JUnit by Kent Beck 
 * and Erich Gamma. You should have received a copy of their JUnit licence 
 * agreement along with the Susebox Java test suite.
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

package de.susebox.java.util;

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
// Class TestEmbeddedTokenizer
//

/**<p>
 * This unit test checks the embedded-tokenizer feature of the class {@link AbstractTokenizer}.
 * With this technique it is possible to parse multipart documents like HTML with
 * embedded CSS and script parts, Java and javadoc comments etc.
 *</p><p>
 * This test suite works with a test configuration file. This file contains some
 * sets of properties, each set for one or more different test runs.
 *</p><p>
 * The properties are defined as class constants. In the configuration file, a 
 * property consists of the property name and a number identifying the property
 * set. 
 *</p>
 *
 * @see     AbstractTokenizer
 * @author  Heiko Blau
 */
public class TestEmbeddedTokenizer extends TestCase {
  
  //---------------------------------------------------------------------------
  // properties
  //

  /**
   * The name of the test configuration file. This file will be read by 
   * {@link java.lang.Class#getResourceAsStream}.
   */
  public static final String CONFIG_FILE = "TestEmbeddedTokenizer.conf";
  
  /**
   * Property for the test {@link #testEmbeddedTokenizer}
   */
  public static final String PROP_PATH = "Path";
  
  /**
   * Property for the test {@link #testJavaTokenizer}
   */
  public static final String PROP_JAVAPATH = "JavaPath";
  
  
  //---------------------------------------------------------------------------
  // main method
  //
  
  /**
   * call this method to invoke the tests.
   *
   * @param args  unused
   */
  public static void main(String[] args) {
    String[]   tests = { TestEmbeddedTokenizer.class.getName() };
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
      props.load(TestEmbeddedTokenizer.class.getResourceAsStream(CONFIG_FILE));
    } catch (Exception ex) {
      throw new ExtRuntimeException(ex);
    }

    // test on HTML files
    while ((path = props.getProperty(PROP_PATH + count)) != null) {
      if ((url = TestEmbeddedTokenizer.class.getResource(path)) != null) {
        path = url.getFile();
      }
      suite.addTest(new TestEmbeddedTokenizer("testEmbeddedTokenizer", path));
      count++;
    }

    // tests on Java files
    count = 1;
    while ((path = props.getProperty(PROP_JAVAPATH + count)) != null) {
      if ((url = TestEmbeddedTokenizer.class.getResource(path)) != null) {
        path = url.getFile();
      }
      suite.addTest(new TestEmbeddedTokenizer("testJavaTokenizer", path));
      count++;
    }
    return suite;
  }
  
  
  //---------------------------------------------------------------------------
  // Constructor
  //
  
  /**
   * Initializing the instance with the test file path
   *
   * @param test  which test method should be invoked
   * @param path  name of test configuration file  
   */  
  public TestEmbeddedTokenizer(String test, String path) {
    super(test);
    m_path = path;
  }

  
  //---------------------------------------------------------------------------
  // Fixture setup and release
  //
  
	/**
	 * Sets up the fixture, for example, open a network connection.
	 * This method is called before a test is executed.
   *
   * @throws Exception for anything that might go wrong
	 */
  protected void setUp() throws Exception {
    InputStream  stream = new FileInputStream(m_path);
    
    m_reader = new InputStreamReader(stream);
	}

  
	/**
	 * Tears down the fixture, for example, close a network connection.
	 * This method is called after a test is executed.
   *
   * @throws Exception for anything that might go wrong
	 */
	protected void tearDown() throws Exception {
    m_reader.close();
	}
  
  //---------------------------------------------------------------------------
  // test cases
  //
  
  
  /**
   * This method reads the given stream as a Java source. It extracts javadoc
   * comments and source code.
   * There should be a class or interface name in every Java source. The opening
   * and closing brackets should match etc.
   *
   * @throws Throwable   for anything that might go wrong
   * @see   #testEmbeddedTokenizer
   */
  public void testJavaTokenizer() throws Throwable {
    long                  start         = System.currentTimeMillis();
    InputStreamTokenizer  javaTokenizer = new InputStreamTokenizer(m_reader);
    InputStreamTokenizer  docTokenizer  = new InputStreamTokenizer(m_reader);
    AbstractTokenizer     currTokenizer = javaTokenizer;
    Object                openBlock     = new Object();
    Object                closeBlock    = new Object();
    Object                atSign        = new Object();
    int                   blockBalance  = 0;
    Token                 token;

    javaTokenizer.addTokenizer(docTokenizer);

    javaTokenizer.setParseFlags(Tokenizer.F_TOKEN_POS_ONLY | Tokenizer.F_KEEP_DATA);
    docTokenizer.setParseFlags(Tokenizer.F_NO_CASE);
    
    javaTokenizer.addSpecialSequence("/**", docTokenizer);
    javaTokenizer.addSpecialSequence("{", openBlock);
    javaTokenizer.addSpecialSequence("}", closeBlock);
    docTokenizer.addSpecialSequence("*/", javaTokenizer);
    docTokenizer.addSpecialSequence("@", atSign);
    docTokenizer.addKeyword("param");
    docTokenizer.addKeyword("return");
    docTokenizer.addKeyword("throws");
    docTokenizer.addKeyword("author");
    docTokenizer.addKeyword("version");
    docTokenizer.addKeyword("link");
    docTokenizer.addKeyword("see");
    docTokenizer.addKeyword("deprecated");
    javaTokenizer.addBlockComment(Tokenizer.DEFAULT_BLOCK_COMMENT_START, Tokenizer.DEFAULT_BLOCK_COMMENT_END);
    javaTokenizer.addLineComment(Tokenizer.DEFAULT_LINE_COMMENT);
    
    //    System.out.println("\nStart parsing \"" + m_path + "\"");
    while (currTokenizer.hasMoreToken()) {
      token = currTokenizer.nextToken();
      
      switch (token.getType()) {
        case Token.SPECIAL_SEQUENCE:
          if (token.getCompanion() instanceof AbstractTokenizer) {
            AbstractTokenizer tokenizer = (AbstractTokenizer)token.getCompanion();
            
            currTokenizer.switchTo(tokenizer);
            currTokenizer = tokenizer;
          } else if (token.getCompanion() == openBlock) {
            blockBalance++;
          } else if (token.getCompanion() == closeBlock) {
            blockBalance--;
          } else if (token.getCompanion() == atSign) {
            token = currTokenizer.nextToken();
            assertTrue("Expected keyword after @ sign in javadoc comment, but found \"" + currTokenizer.current(),
                      token.getType() == Token.KEYWORD);
          }
          break;
      }
    }
    
    // some checks
    assertTrue("Braces should be balanced in Java file \"" 
              + m_path + "\", but detected inbalance " + blockBalance,
              blockBalance == 0);

    // print elapsed time
    long diff = System.currentTimeMillis() - start;
    //    System.out.println("Finished after " + diff + " milliseconds");
  }
    

  
  /**
   * The method takes the HTML file given in the constructor, and parses with
   * the main HTML tokenizer and two embedded tokenizers for JavaScript and
   * CSS.
   *
   * @throws Throwable   for anything that might go wrong
   * @see   #testEmbeddedTokenizer
   */
  public void testEmbeddedTokenizer() throws Throwable {
    long                  start         = System.currentTimeMillis();
    InputStreamTokenizer  htmlTokenizer = new InputStreamTokenizer(m_reader);
    InputStreamTokenizer  jsTokenizer   = new InputStreamTokenizer(m_reader);
    InputStreamTokenizer  cssTokenizer  = new InputStreamTokenizer(m_reader);
    String                keywordLang   = new String("LANGUAGE");
    Object                endOfEmbedded = new Object();
    Object                startOfTag    = new Object();
    Object                endOfTag      = new Object();
    Object                endOfScript   = new Object();
    Token                 token;

    htmlTokenizer.setParseFlags(Tokenizer.F_NO_CASE | Tokenizer.F_TOKEN_POS_ONLY);
    jsTokenizer.setParseFlags(Tokenizer.F_TOKEN_POS_ONLY);
    cssTokenizer.setParseFlags(Tokenizer.F_TOKEN_POS_ONLY);
    
    htmlTokenizer.addKeyword("SCRIPT", jsTokenizer);
    htmlTokenizer.addKeyword("LANGUAGE", keywordLang);
    htmlTokenizer.addKeyword("STYLE", cssTokenizer);
    htmlTokenizer.addSpecialSequence("<", startOfTag);
    htmlTokenizer.addSpecialSequence(">", endOfTag);
    htmlTokenizer.addBlockComment("<!--", "-->");
    htmlTokenizer.addString(Tokenizer.DEFAULT_STRING_START, Tokenizer.DEFAULT_STRING_END, Tokenizer.DEFAULT_STRING_ESCAPE);
    htmlTokenizer.setSeparators(Tokenizer.DEFAULT_SEPARATORS);
    
    jsTokenizer.addBlockComment(Tokenizer.DEFAULT_BLOCK_COMMENT_START, Tokenizer.DEFAULT_BLOCK_COMMENT_END);
    jsTokenizer.addSpecialSequence("<!--");
    jsTokenizer.addSpecialSequence("-->", endOfEmbedded);
    jsTokenizer.setSeparators(Tokenizer.DEFAULT_SEPARATORS);
    
    cssTokenizer.addSpecialSequence("<!--");
    cssTokenizer.addSpecialSequence("-->", endOfEmbedded);
    
    htmlTokenizer.addTokenizer(jsTokenizer);
    htmlTokenizer.addTokenizer(cssTokenizer);

    //    System.out.println("\nStart parsing \"" + m_path + "\"");
    while (htmlTokenizer.hasMoreToken()) {
      token = htmlTokenizer.nextToken();
      
      switch (token.getType()) {
      case Token.SPECIAL_SEQUENCE:
        
        // dealing with JavaScript
        if (token.getCompanion() == startOfTag) {
          token = htmlTokenizer.nextToken();
          if (token.getType() == Token.KEYWORD && token.getCompanion() == jsTokenizer) {
            token = htmlTokenizer.nextToken();
            assertTrue("Found token \"" + htmlTokenizer.current() + "\". Expected \"" + keywordLang + "\".",
                      token.getCompanion() == keywordLang);   // see above; should be the LANGUAGE token
            token = htmlTokenizer.nextToken();
            assertTrue("Found token \"" + htmlTokenizer.current() + "\". Expected \"=\".",
                      htmlTokenizer.current().equals("="));           // see above; should be "="
            token = htmlTokenizer.nextToken();
            assertTrue("Found token \"" + htmlTokenizer.current() + "\". Expected string.",
                      token.getType() == Token.STRING);       // see above; should be "JavaScript"
            
            // exclude JavaScript-Includes
            token = htmlTokenizer.nextToken();
            if (token.getCompanion() == endOfTag) {
              htmlTokenizer.switchTo(jsTokenizer);

              // continuing with JavaScriptTokenizer
              while (jsTokenizer.hasMoreToken()) {
                token = jsTokenizer.nextToken();
                if (token.getType() == Token.SPECIAL_SEQUENCE && token.getCompanion() == endOfEmbedded) {
                  jsTokenizer.switchTo(htmlTokenizer);
                  break;
                }
              }
              
              // now we should find the end-of script tag
              token = htmlTokenizer.nextToken();
              assertTrue("Found token \"" + htmlTokenizer.current() + "\". Expected start of tag.",
                        token.getCompanion() == startOfTag);
              token = htmlTokenizer.nextToken();
              assertTrue("Found token \"" + htmlTokenizer.current() + "\". Expected \"/\".",
                        htmlTokenizer.current().equals("/"));
              token = htmlTokenizer.nextToken();
              assertTrue("Found token \"" + htmlTokenizer.current() + "\". Expected script.",
                        token.getCompanion() == jsTokenizer);
              token = htmlTokenizer.nextToken();
              assertTrue("Found token \"" + htmlTokenizer.current() + "\". Expected end of tag.",
                        token.getCompanion() == endOfTag);
            }
            
          // dealing with Cascading Style Sheets (CSS
          } else if (token.getType() == Token.KEYWORD && token.getCompanion() == jsTokenizer) {
            token = htmlTokenizer.nextToken();
            assertTrue("Found token \"" + htmlTokenizer.current() + "\". Expected end of tag.",
                      token.getCompanion() == endOfTag);   // should be the end of tag
            
            htmlTokenizer.switchTo(cssTokenizer);
            while (cssTokenizer.hasMoreToken()) {
              token = cssTokenizer.nextToken();
              if (token.getType() == Token.SPECIAL_SEQUENCE && token.getCompanion() == endOfEmbedded) {
                jsTokenizer.switchTo(htmlTokenizer);
                break;
              }
            }
            
            // now we should find the end-of-style tag
            token = htmlTokenizer.nextToken();
            assertTrue("Found token \"" + htmlTokenizer.current() + "\". Expected start of tag.",
                      token.getCompanion() == startOfTag);
            token = htmlTokenizer.nextToken();
            assertTrue("Found token \"" + htmlTokenizer.current() + "\". Expected \"/\".",
                      htmlTokenizer.current().equals("/"));
            token = htmlTokenizer.nextToken();
            assertTrue("Found token \"" + htmlTokenizer.current() + "\". Expected script.",
                      token.getCompanion() == cssTokenizer);
            token = htmlTokenizer.nextToken();
            assertTrue("Found token \"" + htmlTokenizer.current() + "\". Expected end of tag.",
                      token.getCompanion() == endOfTag);
          }
        }
        break;
      }
    }

    long diff = System.currentTimeMillis() - start;
    //    System.out.println("Finished after " + diff + " milliseconds");
  }
  
  
  //---------------------------------------------------------------------------
  // Members
  //
  protected InputStreamReader m_reader = null;
  protected String            m_path   = null;
}
