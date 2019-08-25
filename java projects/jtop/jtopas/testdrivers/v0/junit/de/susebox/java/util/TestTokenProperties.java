/*
 * TestTokenProperties.java: JUnit test for the InputStreamTokenizer
 *
 * Copyright (C) 2001 Heiko Blau
 *
 * This file belongs to the Susebox Java Core Library Test Suite 
 * (Susebox JCL-TS).
 * The Susebox JCL-TS is free software; you can redistribute it and/or modify it 
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
 * with the Susebox JCL-TS. If not, write to the
 *
 *   Free Software Foundation, Inc.
 *   59 Temple Place, Suite 330, 
 *   Boston, MA 02111-1307 
 *   USA
 *
 * or check the Internet: http://www.fsf.org
 *
 * The Susebox JCL-TS uses the test framework JUnit by Kent Beck and Erich Gamma.
 * You should have received a copy of their JUnit licence agreement along with 
 * the Susebox JCL-TS.
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
import java.util.Enumeration;
import java.util.Properties;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URL;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.framework.Assert;
import junit.swingui.TestRunner;

import de.susebox.java.lang.ExtRuntimeException;
import de.susebox.java.util.Token;
import de.susebox.java.util.Tokenizer;
import de.susebox.java.util.TokenizerProperty;
import de.susebox.java.util.InputStreamTokenizer;


//-----------------------------------------------------------------------------
// Class TestTokenProperties
//

/**<p>
 * This class tests the {@link Token} objects returned by a {@link Tokenizer}. 
 *</p><p>
 * It uses a test configuration file with a set of properties for one test run.
 * Each set is identified by a trailing number in the property names.
 *</p>
 *
 * @see     Token
 * @see     Tokenizer
 * @author  Heiko Blau
 */
public class TestTokenProperties extends TestCase {
  
  //---------------------------------------------------------------------------
  // properties
  //

  /**
   * The name of the test configuration file. This file will be read by 
   * {@link java.lang.Class#getResourceAsStream}.
   */
  public static final String CONFIG_FILE = "TestTokenProperties.conf";
  
  /**
   * Which file should be used for the tokenizer
   */
  public static final String PROP_PATH = "Path";
  
  
  
  //---------------------------------------------------------------------------
  // main method
  //
  
  /**
   * call this method to invoke the tests
   */
  public static void main(String[] args) {
    String[]   tests = { TestTokenProperties.class.getName() };
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
      props.load(TestTokenProperties.class.getResourceAsStream(CONFIG_FILE));
    } catch (Exception ex) {
      throw new ExtRuntimeException(ex);
    }
    
    while ((path = props.getProperty(PROP_PATH + count)) != null) {
      if ((url = TestTokenProperties.class.getResource(path)) != null) {
        path = url.getFile();
      }
      suite.addTest(new TestTokenProperties("testTokenPos", path));
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
  public TestTokenProperties(String test, String path) {
    super(test);
    _path = path;
  }

  
  //---------------------------------------------------------------------------
  // Fixture setup and release
  //
  
	/**
	 * Sets up the fixture, for example, open a network connection.
	 * This method is called before a test is executed.<br>
   * Here the tokenizer for the tests is initialized and the contents of the 
   * given path is read into a buffer for later comparison with the
   * tokenizer output.
	 */
  protected void setUp() throws Exception {
    InputStream       stream = new FileInputStream(_path);
    InputStreamReader reader = new InputStreamReader(stream);
    int               size   = stream.available();
    
    // readiong the file completely into the contents buffer
    _contents = new char[size];
    reader.read(_contents);  
    reader.close();
    
    // setting up the tokenizer
    _tokenizer = new InputStreamTokenizer(new InputStreamReader(new FileInputStream(_path)));
    _tokenizer.setParseFlags(Tokenizer.F_RETURN_WHITESPACES | Tokenizer.F_COUNT_LINES);
    _tokenizer.addBlockComment(Tokenizer.DEFAULT_BLOCK_COMMENT_START, Tokenizer.DEFAULT_BLOCK_COMMENT_END);
    _tokenizer.addLineComment(Tokenizer.DEFAULT_LINE_COMMENT);
  }

  
	/**
	 * Tears down the fixture, for example, close a network connection.
	 * This method is called after a test is executed.
	 */
	protected void tearDown() throws Exception {
	}
  
  //---------------------------------------------------------------------------
  // test cases
  //

  /**
   * This test compares the token positions returned by the tokenizer against
   * the position given by the input stream (simple reader).<br>
   * There are also several test of the start / end line / column values.
   */
  public void testTokenPos() throws Throwable {
    while (_tokenizer.hasMoreToken()) {
      Token   token    = _tokenizer.nextToken();
      String  image    = token.getToken();
      int     start    = token.getStartPosition();
      int     length   = token.getLength();
      String  contents = new String(_contents, start, length);
      
      assertTrue("Found token  does not match the text in contents buffer.\nToken: \"" 
             + image + "\"\nContents: \"" + contents + "\"",
             image.equals(contents));
      
      switch (token.getType()) {
        case Token.LINE_COMMENT:
          assertTrue("There should be exactly one newline in line comment.",
                 token.getStartLine() + 1 == token.getEndLine());
          assertTrue("End position " + token.getEndColumn() + " of line comment not 0",
                 token.getEndColumn() == 0);
          break;
        case Token.SEPARATOR:
          assertTrue("End column " + token.getEndColumn() + " is not the successor of start column " 
                 + token.getStartColumn(),
                 token.getEndColumn() == token.getStartColumn() + 1);
          break;
        case Token.NORMAL:
          assertTrue("Newline in normal token", token.getStartLine() == token.getEndLine());
          assertTrue("Start and end column does not match token length.",
                 token.getLength() == token.getEndColumn() - token.getStartColumn());
          break;
      }
    }
  }
  
  
  //---------------------------------------------------------------------------
  // Members
  //
  private Tokenizer _tokenizer = null;
  private char[]    _contents  = null;
  private String    _path      = null;
}
