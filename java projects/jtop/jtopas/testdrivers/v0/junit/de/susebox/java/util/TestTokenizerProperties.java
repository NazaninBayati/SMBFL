/*
 * TestTokenizerProperties.java: JUnit test for the InputStreamTokenizer
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
import java.util.Iterator;
import java.io.InputStreamReader;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.framework.Assert;
import junit.swingui.TestRunner;

import de.susebox.java.lang.ExtRuntimeException;


//-----------------------------------------------------------------------------
// Class TestTokenizerProperties
//

/**<p>
 * This class tests some basic getter and setter methods in a {@link Tokenizer}. 
 *</p>
 *
 * @see     Tokenizer
 * @see     AbstractTokenizer
 * @author  Heiko Blau
 */
public class TestTokenizerProperties extends TestCase {
  
  //---------------------------------------------------------------------------
  // main method
  //
  
  /**
   * call this method to invoke the tests
   */
  public static void main(String[] args) {
    String[]   tests = { TestTokenizerProperties.class.getName() };
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
    
    suite.addTest(new TestTokenizerProperties("testAddingSequences"));
    suite.addTest(new TestTokenizerProperties("testAddingKeywords"));
    return suite;
  }
  
  
  //---------------------------------------------------------------------------
  // Constructor
  //
  
  /**
   * Default constructor. Standard input {@link java.lang.System#in} is used
   * to construct the input stream reader.
   */  
  public TestTokenizerProperties(String test) {
    super(test);
  }

  
  //---------------------------------------------------------------------------
  // Fixture setup and release
  //
  
	/**
	 * Sets up the fixture, for example, open a network connection.
	 * This method is called before a test is executed.
	 */
  protected void setUp() throws Exception {
    _tokenizer = new InputStreamTokenizer(new InputStreamReader(System.in));
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
  
  public void testAddingSequences() throws Throwable {
    // adding sequences
    _tokenizer.addString("'", "'", "\\");
    _tokenizer.addString("\"", "\"", "\\");
    _tokenizer.addLineComment("rem", null, Tokenizer.F_NO_CASE);
    _tokenizer.addBlockComment("/*", "*/");
    _tokenizer.addBlockComment("<!--", "-->");
    _tokenizer.addSpecialSequence("<<");
    _tokenizer.addSpecialSequence(">>");
    _tokenizer.addSpecialSequence(">>>");
    _tokenizer.addSpecialSequence("<H1>", null, Tokenizer.F_NO_CASE);
    _tokenizer.addSpecialSequence("<H2>", null, Tokenizer.F_NO_CASE);
    
    assertTrue("Couldn't find \"'\".",    _tokenizer.stringExists("'"));
    assertTrue("Couldn't find \"\"\".",   _tokenizer.stringExists("\""));
    assertTrue("Couldn't find \"rem\".",  _tokenizer.lineCommentExists("rem"));
    assertTrue("Couldn't find \"REM\".",  _tokenizer.lineCommentExists("REM"));
    assertTrue("Couldn't find \"Rem\".",  _tokenizer.lineCommentExists("Rem"));
    assertTrue("Couldn't find \"/*\".",   _tokenizer.blockCommentExists("/*"));
    assertTrue("Couldn't find \"<!--\".", _tokenizer.blockCommentExists("<!--"));
    assertTrue("Couldn't find \"<<\".",   _tokenizer.specialSequenceExists("<<"));
    assertTrue("Couldn't find \">>\".",   _tokenizer.specialSequenceExists(">>"));
    assertTrue("Couldn't find \">>>\".",  _tokenizer.specialSequenceExists(">>>"));
    assertTrue("Couldn't find \"<H1>\".", _tokenizer.specialSequenceExists("<H1>"));
    assertTrue("Couldn't find \"<h1>\".", _tokenizer.specialSequenceExists("<h1>"));
    assertTrue("Couldn't find \"<H2>\".", _tokenizer.specialSequenceExists("<H2>"));
    assertTrue("Couldn't find \"<h2>\".", _tokenizer.specialSequenceExists("<h2>"));

    // check for almost the same sequences
    assertTrue("Unexpectedly found  \"''\".",   ! _tokenizer.stringExists("''"));
    assertTrue("Unexpectedly found  \"\"\"\".", ! _tokenizer.stringExists("\"\""));
    assertTrue("Unexpectedly found  \"re\".",   ! _tokenizer.lineCommentExists("re"));
    assertTrue("Unexpectedly found  \"RE\".",   ! _tokenizer.lineCommentExists("RE"));
    assertTrue("Unexpectedly found  \"Re\".",   ! _tokenizer.lineCommentExists("Re"));
    assertTrue("Unexpectedly found  \"*\".",    ! _tokenizer.blockCommentExists("*"));
    assertTrue("Unexpectedly found  \"<!-\".",  ! _tokenizer.blockCommentExists("<!-"));
    assertTrue("Unexpectedly found  \"<\".",    ! _tokenizer.specialSequenceExists("<"));
    assertTrue("Unexpectedly found  \">\".",    ! _tokenizer.specialSequenceExists(">"));
    assertTrue("Unexpectedly found  \">>>>\".", ! _tokenizer.specialSequenceExists(">>>>"));
    assertTrue("Unexpectedly found  \"<H1\".",  ! _tokenizer.specialSequenceExists("<H1"));
    assertTrue("Unexpectedly found  \"h1>\".",  ! _tokenizer.specialSequenceExists("h1>"));
    assertTrue("Unexpectedly found  \"<H>\".",  ! _tokenizer.specialSequenceExists("<H>"));
    assertTrue("Unexpectedly found  \"<h2\".",  ! _tokenizer.specialSequenceExists("<h2"));
    assertTrue("Unexpectedly found  \"<<H>\".", ! _tokenizer.specialSequenceExists("<<H>"));
    assertTrue("Unexpectedly found  \"<h2>>\".",! _tokenizer.specialSequenceExists("<h2>>"));
    
    // check enumeration
    Iterator iter;
    int      count;

    iter = _tokenizer.getStrings();
    count = 0;
    while (iter.hasNext()) {
      iter.next();
      count++;
    }
    assertTrue("More / less strings than expected: " + count, count == 2);

    iter = _tokenizer.getLineComments();
    count = 0;
    while (iter.hasNext()) {
      iter.next();
      count++;
    }
    assertTrue("More / less line comments than expected: " + count, count == 1);

    iter = _tokenizer.getBlockComments();
    count = 0;
    while (iter.hasNext()) {
      iter.next();
      count++;
    }
    assertTrue("More / less block comments than expected: " + count, count == 2);

    iter = _tokenizer.getSpecialSequences();
    count = 0;
    while (iter.hasNext()) {
      iter.next();
      count++;
    }
    assertTrue("More / less special sequences than expected: " + count, count == 5);

    // removing sequences
    _tokenizer.removeString("'");
    assertTrue("Still found \"'\".",  ! _tokenizer.stringExists("'"));
    
    _tokenizer.removeString("\"");
    assertTrue("Still found \"\"\".", ! _tokenizer.stringExists("\""));

    _tokenizer.removeLineComment("rem");
    assertTrue("Still found \"rem\".",  ! _tokenizer.lineCommentExists("rem"));

    _tokenizer.removeBlockComment("/*");
    assertTrue("Still found \"/*\".",   ! _tokenizer.blockCommentExists("/*"));

    _tokenizer.removeBlockComment("<!--");
    assertTrue("Still found \"<!--\".", ! _tokenizer.blockCommentExists("<!--"));

    _tokenizer.removeSpecialSequence("<<");
    assertTrue("Still found \"<<\".",   ! _tokenizer.specialSequenceExists("<<"));

    _tokenizer.removeSpecialSequence(">>");
    assertTrue("Still found \">>\".",   ! _tokenizer.specialSequenceExists(">>"));
    
    _tokenizer.removeSpecialSequence(">>>");
    assertTrue("Still found \">>>\".",   ! _tokenizer.specialSequenceExists(">>>"));
    
    _tokenizer.removeSpecialSequence("<H1>");
    assertTrue("Still found \"<H1>\".", ! _tokenizer.specialSequenceExists("<H1>"));

    _tokenizer.removeSpecialSequence("<H2>");
    assertTrue("Still found \"<H2>\".", ! _tokenizer.specialSequenceExists("<H2>"));
    
    // check enumeration
    iter = _tokenizer.getStrings();
    assertTrue("Still contains strings.",           ! iter.hasNext());

    iter = _tokenizer.getLineComments();
    assertTrue("Still contains line comments.",     ! iter.hasNext());

    iter = _tokenizer.getBlockComments();
    assertTrue("Still contains block comments.",    ! iter.hasNext());

    iter = _tokenizer.getSpecialSequences();
    assertTrue("Still contains special sequences.", ! iter.hasNext());
  }
  
  
  /**
   * This test adds a set of keywords, both case-sensitive and not, then tries 
   * to retrieve them, then removes them
   */
  public void testAddingKeywords() throws Throwable {
    int keywordCount = 0;
    
    _tokenizer.addKeyword("if");
    keywordCount++;
    _tokenizer.addKeyword("else");
    keywordCount++;
    _tokenizer.addKeyword("elsif");
    keywordCount++;
    _tokenizer.addKeyword("end");
    keywordCount++;
    _tokenizer.addKeyword("char",    null, Tokenizer.F_KEYWORDS_NO_CASE);
    keywordCount++;
    _tokenizer.addKeyword("int",     null, Tokenizer.F_KEYWORDS_NO_CASE);
    keywordCount++;
    _tokenizer.addKeyword("integer", null, Tokenizer.F_KEYWORDS_NO_CASE);
    keywordCount++;
    _tokenizer.addKeyword("integ");
    keywordCount++;
    _tokenizer.addKeyword("class");
    keywordCount++;
    _tokenizer.addKeyword("Class");
    keywordCount++;
    _tokenizer.addKeyword("CLASS");
    keywordCount++;
    _tokenizer.addKeyword("interface");
    keywordCount++;
    _tokenizer.addKeyword("Interface");
    keywordCount++;
    _tokenizer.addKeyword("INTERFACE");
    keywordCount++;
    
    assertTrue("Couldn't find \"if\".",       _tokenizer.keywordExists("if"));
    assertTrue("Couldn't find \"else\".",     _tokenizer.keywordExists("else"));
    assertTrue("Couldn't find \"elsif\".",    _tokenizer.keywordExists("elsif"));
    assertTrue("Couldn't find \"end\".",      _tokenizer.keywordExists("end"));
    assertTrue("Couldn't find \"char\".",     _tokenizer.keywordExists("char"));
    assertTrue("Couldn't find \"Char\".",     _tokenizer.keywordExists("Char"));
    assertTrue("Couldn't find \"CHAR\".",     _tokenizer.keywordExists("CHAR"));
    assertTrue("Couldn't find \"int\".",      _tokenizer.keywordExists("if"));
    assertTrue("Couldn't find \"Int\".",      _tokenizer.keywordExists("else"));
    assertTrue("Couldn't find \"INT\".",      _tokenizer.keywordExists("else"));
    assertTrue("Couldn't find \"integer\".",  _tokenizer.keywordExists("integer"));
    assertTrue("Couldn't find \"Integer\".",  _tokenizer.keywordExists("Integer"));
    assertTrue("Couldn't find \"INTEGER\".",  _tokenizer.keywordExists("INTEGER"));
    assertTrue("Couldn't find \"integ\".",    _tokenizer.keywordExists("integ"));
    assertTrue("Couldn't find \"class\".",    _tokenizer.keywordExists("class"));
    assertTrue("Couldn't find \"Class\".",    _tokenizer.keywordExists("Class"));
    assertTrue("Couldn't find \"CLASS\".",    _tokenizer.keywordExists("CLASS"));
    assertTrue("Couldn't find \"interface\".",   _tokenizer.keywordExists("interface"));
    assertTrue("Couldn't find \"Interface\".",   _tokenizer.keywordExists("Interface"));
    assertTrue("Couldn't find \"INTERFACE\".",   _tokenizer.keywordExists("INTERFACE"));

    assertTrue("Unexpectedly found  \"gonzo\".",      ! _tokenizer.keywordExists("gonzo"));
    assertTrue("Unexpectedly found  \"IF\".",         ! _tokenizer.keywordExists("IF"));
    assertTrue("Unexpectedly found  \"InTeRfAcE\".",  ! _tokenizer.keywordExists("InTeRfAcE"));
    assertTrue("Unexpectedly found  \"function\".",   ! _tokenizer.keywordExists("function"));
    assertTrue("Unexpectedly found  \"INTEG\".",      ! _tokenizer.keywordExists("INTEG"));

    // check enumeration
    Iterator iter  = _tokenizer.getKeywords();
    int         count = 0;

    while (iter.hasNext()) {
      iter.next();
      count++;
    }
    assertTrue("More / less keywords than expected: " + count, count == keywordCount);
  }
  
  
  //---------------------------------------------------------------------------
  // Members
  //
  private Tokenizer   _tokenizer = null;
}
