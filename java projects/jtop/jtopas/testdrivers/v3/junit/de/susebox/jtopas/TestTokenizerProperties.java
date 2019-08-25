/*
 * TestTokenizerProperties.java: JUnit test for TokenizerProperties implementations
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
import java.util.Iterator;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.framework.Assert;
import junit.swingui.TestRunner;

import de.susebox.java.lang.ExtRuntimeException;
import de.susebox.jtopas.spi.DataProvider;
import de.susebox.jtopas.spi.KeywordHandler;
import de.susebox.jtopas.spi.SequenceHandler;
import de.susebox.jtopas.spi.PatternHandler;
import de.susebox.jtopas.spi.WhitespaceHandler;
import de.susebox.jtopas.spi.SeparatorHandler;

//-----------------------------------------------------------------------------
// Class TestTokenizerProperties
//

/**<p>
 * This class tests the implementations of {@link TokenizerProperties}. 
 *</p>
 *
 * @see     TokenizerProperties
 * @see     TokenizerPropertyListener 
 * @author  Heiko Blau
 */
public class TestTokenizerProperties 
  extends TestCase 
  implements TokenizerPropertyListener, DataProvider {
  
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
    suite.addTest(new TestTokenizerProperties("testGenericProperties"));
    suite.addTest(new TestTokenizerProperties("testDataHandlers"));
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
    _properties = new StandardTokenizerProperties();
    _properties.addTokenizerPropertyListener(this);
	}

  
	/**
	 * Tears down the fixture, for example, close a network connection.
	 * This method is called after a test is executed.
	 */
	protected void tearDown() throws Exception {}

  
  //---------------------------------------------------------------------------
  // test cases
  //

  /**
   * Checking the property handler faculties like {@link de.susebox.jtopas.spi.KeywordHandler} 
   * and {@link de.susebox.jtopas.spi.SequenceHandler}.
   */
  public void testDataHandlers() throws Throwable {
    // add all the properties
    for (int index = 0; index < _testProperties.length; ++index) {
      TokenizerProperty prop = _testProperties[index];
      
      _properties.addProperty(prop);
      switch(prop.getType()) {
      case Token.KEYWORD:
        assertTrue("Keyword not found: " + prop, _properties.keywordExists(prop.getImages()[0]));
        break;
      case Token.STRING:
        assertTrue("String not found: " + prop, _properties.stringExists(prop.getImages()[0]));
        break;
      case Token.BLOCK_COMMENT:
        assertTrue("Block comment not found: " + prop, _properties.blockCommentExists(prop.getImages()[0]));
        break;
      case Token.LINE_COMMENT:
        assertTrue("Line comment not found: " + prop, _properties.lineCommentExists(prop.getImages()[0]));
        break;
      case Token.SPECIAL_SEQUENCE:
        assertTrue("Special sequence not found: " + prop, _properties.specialSequenceExists(prop.getImages()[0]));
        break;
      case Token.PATTERN:
        assertTrue("Pattern not found: " + prop, _properties.patternExists(prop.getImages()[0]));
        break;
      }
    }

    // check all non-pattern properties
    de.susebox.jtopas.spi.KeywordHandler  kh = (de.susebox.jtopas.spi.KeywordHandler)_properties;
    de.susebox.jtopas.spi.SequenceHandler sh = (de.susebox.jtopas.spi.SequenceHandler)_properties;
    
    for (int index = 0; index < _testProperties.length; ++index) {
      TokenizerProperty prop  = _testProperties[index];
      
      if (prop.getType() == Token.PATTERN) {
        continue;
      }

      // all properties except pattern
      String            image = prop.getImages()[0];
      TokenizerProperty isKeyword;
      TokenizerProperty isSequence;
      TokenizerProperty isPattern;
      
      _currDataLength = image.length();
      System.arraycopy(image.toCharArray(), 0, _currData, _currStartPos, _currDataLength);
      
      isKeyword = kh.isKeyword(this);
      isSequence = sh.startsWithSequenceCommentOrString(this);
      
      switch(prop.getType()) {
      case Token.KEYWORD:
        assertTrue("Keyword not found: " + prop, isKeyword != null);
        assertTrue("Unexpectedly found sequence: " + prop, isSequence == null);
        assertTrue("Expected keyword: " + prop + ", found: " + isKeyword, prop.equals(isKeyword));
        break;
      case Token.STRING:
      case Token.BLOCK_COMMENT:
      case Token.LINE_COMMENT:
      case Token.SPECIAL_SEQUENCE:
        assertTrue("Sequence not found: " + prop, isSequence != null);
        assertTrue("Unexpectedly found keyword: " + prop, isKeyword == null);
        assertTrue("Expected sequence: " + prop + ", found: " + isSequence, prop.equals(isSequence));
        break;
      }
      
      _currStartPos += _currDataLength;
    }
    
    // check all pattern properties
    de.susebox.jtopas.spi.PatternHandler  ph   = (de.susebox.jtopas.spi.PatternHandler)_properties;
    TokenizerProperty                     prop = new TokenizerProperty();
    
    _currStartPos = 0;
    for (int index = 0; index < _patternMatchingStrings.length; ++index) {
      String image    = _patternMatchingStrings[index];
      char[] complete = { ';' };
      
      _currDataLength = image.length();
      System.arraycopy(image.toCharArray(), 0, _currData, _currStartPos, _currDataLength);
      assertTrue("Pattern matching failed for: " + image, ph.matches(this, prop) == PatternHandler.MATCH);
      System.arraycopy(complete, 0, _currData, _currStartPos + _currDataLength, complete.length);
      _currDataLength += complete.length;
      assertTrue("Pattern matching failed for: " + image, ph.matches(this, prop) == PatternHandler.COMPLETE_MATCH);
      _currStartPos   += _currDataLength;
    }
  }
  
  
  /**
   * Adding and removing of line and block comments and special sequences.
   */
  public void testAddingSequences() throws Throwable {
    // adding sequences
    _properties.addString("'", "'", "\\");
    _properties.addString("\"", "\"", "\\");
    _properties.addLineComment("rem", null, TokenizerProperties.F_NO_CASE);
    _properties.addBlockComment("/*", "*/");
    _properties.addBlockComment("<!--", "-->");
    _properties.addSpecialSequence("<<");
    _properties.addSpecialSequence(">>");
    _properties.addSpecialSequence(">>>");
    _properties.addSpecialSequence("<H1>", null, TokenizerProperties.F_NO_CASE);
    _properties.addSpecialSequence("<H2>", null, TokenizerProperties.F_NO_CASE);
    
    assertTrue("Couldn't find \"'\".",    _properties.stringExists("'"));
    assertTrue("Couldn't find \"\"\".",   _properties.stringExists("\""));
    assertTrue("Couldn't find \"rem\".",  _properties.lineCommentExists("rem"));
    assertTrue("Couldn't find \"REM\".",  _properties.lineCommentExists("REM"));
    assertTrue("Couldn't find \"Rem\".",  _properties.lineCommentExists("Rem"));
    assertTrue("Couldn't find \"/*\".",   _properties.blockCommentExists("/*"));
    assertTrue("Couldn't find \"<!--\".", _properties.blockCommentExists("<!--"));
    assertTrue("Couldn't find \"<<\".",   _properties.specialSequenceExists("<<"));
    assertTrue("Couldn't find \">>\".",   _properties.specialSequenceExists(">>"));
    assertTrue("Couldn't find \">>>\".",  _properties.specialSequenceExists(">>>"));
    assertTrue("Couldn't find \"<H1>\".", _properties.specialSequenceExists("<H1>"));
    assertTrue("Couldn't find \"<h1>\".", _properties.specialSequenceExists("<h1>"));
    assertTrue("Couldn't find \"<H2>\".", _properties.specialSequenceExists("<H2>"));
    assertTrue("Couldn't find \"<h2>\".", _properties.specialSequenceExists("<h2>"));

    // check for almost the same sequences
    assertTrue("Unexpectedly found  \"''\".",   ! _properties.stringExists("''"));
    assertTrue("Unexpectedly found  \"\"\"\".", ! _properties.stringExists("\"\""));
    assertTrue("Unexpectedly found  \"re\".",   ! _properties.lineCommentExists("re"));
    assertTrue("Unexpectedly found  \"RE\".",   ! _properties.lineCommentExists("RE"));
    assertTrue("Unexpectedly found  \"Re\".",   ! _properties.lineCommentExists("Re"));
    assertTrue("Unexpectedly found  \"*\".",    ! _properties.blockCommentExists("*"));
    assertTrue("Unexpectedly found  \"<!-\".",  ! _properties.blockCommentExists("<!-"));
    assertTrue("Unexpectedly found  \"<\".",    ! _properties.specialSequenceExists("<"));
    assertTrue("Unexpectedly found  \">\".",    ! _properties.specialSequenceExists(">"));
    assertTrue("Unexpectedly found  \">>>>\".", ! _properties.specialSequenceExists(">>>>"));
    assertTrue("Unexpectedly found  \"<H1\".",  ! _properties.specialSequenceExists("<H1"));
    assertTrue("Unexpectedly found  \"h1>\".",  ! _properties.specialSequenceExists("h1>"));
    assertTrue("Unexpectedly found  \"<H>\".",  ! _properties.specialSequenceExists("<H>"));
    assertTrue("Unexpectedly found  \"<h2\".",  ! _properties.specialSequenceExists("<h2"));
    assertTrue("Unexpectedly found  \"<<H>\".", ! _properties.specialSequenceExists("<<H>"));
    assertTrue("Unexpectedly found  \"<h2>>\".",! _properties.specialSequenceExists("<h2>>"));
    
    // check enumeration
    Iterator iter;
    int      count;

    iter = _properties.getStrings();
    count = 0;
    while (iter.hasNext()) {
      iter.next();
      count++;
    }
    assertTrue("More / less strings than expected: " + count, count == 2);

    iter = _properties.getLineComments();
    count = 0;
    while (iter.hasNext()) {
      iter.next();
      count++;
    }
    assertTrue("More / less line comments than expected: " + count, count == 1);

    iter = _properties.getBlockComments();
    count = 0;
    while (iter.hasNext()) {
      iter.next();
      count++;
    }
    assertTrue("More / less block comments than expected: " + count, count == 2);

    iter = _properties.getSpecialSequences();
    count = 0;
    while (iter.hasNext()) {
      iter.next();
      count++;
    }
    assertTrue("More / less special sequences than expected: " + count, count == 5);

    // removing sequences
    _properties.removeString("'");
    assertTrue("Still found \"'\".",  ! _properties.stringExists("'"));
    
    _properties.removeString("\"");
    assertTrue("Still found \"\"\".", ! _properties.stringExists("\""));

    _properties.removeLineComment("rem");
    assertTrue("Still found \"rem\".",  ! _properties.lineCommentExists("rem"));

    _properties.removeBlockComment("/*");
    assertTrue("Still found \"/*\".",   ! _properties.blockCommentExists("/*"));

    _properties.removeBlockComment("<!--");
    assertTrue("Still found \"<!--\".", ! _properties.blockCommentExists("<!--"));

    _properties.removeSpecialSequence("<<");
    assertTrue("Still found \"<<\".",   ! _properties.specialSequenceExists("<<"));

    _properties.removeSpecialSequence(">>");
    assertTrue("Still found \">>\".",   ! _properties.specialSequenceExists(">>"));
    
    _properties.removeSpecialSequence(">>>");
    assertTrue("Still found \">>>\".",   ! _properties.specialSequenceExists(">>>"));
    
    _properties.removeSpecialSequence("<H1>");
    assertTrue("Still found \"<H1>\".", ! _properties.specialSequenceExists("<H1>"));

    _properties.removeSpecialSequence("<H2>");
    assertTrue("Still found \"<H2>\".", ! _properties.specialSequenceExists("<H2>"));
    
    // check enumeration
    iter = _properties.getStrings();
    assertTrue("Still contains strings.",           ! iter.hasNext());

    iter = _properties.getLineComments();
    assertTrue("Still contains line comments.",     ! iter.hasNext());

    iter = _properties.getBlockComments();
    assertTrue("Still contains block comments.",    ! iter.hasNext());

    iter = _properties.getSpecialSequences();
    assertTrue("Still contains special sequences.", ! iter.hasNext());
  }
  
  
  /**
   * This test adds a set of keywords, both case-sensitive and not, then tries 
   * to retrieve them, then removes them
   */
  public void testAddingKeywords() throws Throwable {
    final String[] keywordsCase = {
      "if",
      "else",
      "elsif",
      "end",
      "integ",
      "while",
      "loop",
      "case",
      "switch",
      "return",
      "break"
    };
    final String[] keywordsNoCase = {
      "char",
      "int",
      "class",
      "interface",
      "integer"
    };

    // insert keywords
    for (int index = 0; index < keywordsCase.length; ++index) {
      _currEvent = null;
      _properties.addKeyword(keywordsCase[index]);
      assertTrue("No TokenizerPropertyEvent happened.", _currEvent != null);
      assertTrue("Wrong type of event: " + _currEvent.getType(), _currEvent.getType() == TokenizerPropertyEvent.PROPERTY_ADDED);
      assertTrue("Wrong property type: " + _currEvent.getProperty().getType(),
                _currEvent.getProperty().getType() == Token.KEYWORD);
      assertTrue("Wrong property image: " + _currEvent.getProperty().getImages()[0],
                keywordsCase[index].equals(_currEvent.getProperty().getImages()[0]));
    }
    for (int index = 0; index < keywordsNoCase.length; ++index) {
      _currEvent = null;
      _properties.addKeyword(keywordsNoCase[index], null, TokenizerProperties.F_NO_CASE);
      assertTrue("No TokenizerPropertyEvent happened.", _currEvent != null);
      assertTrue("Wrong type of event: " + _currEvent.getType(), _currEvent.getType() == TokenizerPropertyEvent.PROPERTY_ADDED);
      assertTrue("Wrong property type: " + _currEvent.getProperty().getType(),
                _currEvent.getProperty().getType() == Token.KEYWORD);
      assertTrue("Wrong property image: " + _currEvent.getProperty().getImages()[0],
                keywordsNoCase[index].equalsIgnoreCase(_currEvent.getProperty().getImages()[0]));
    }
    
    // search keywords
    for (int index = 0; index < keywordsCase.length; ++index) {
      String keyword = keywordsCase[index];
      assertTrue("Couldn't find \"" + keyword + "\".", _properties.keywordExists(keyword));
      assertTrue("Unexpectedly found \"" + keyword.toUpperCase() + "\".", ! _properties.keywordExists(keyword.toUpperCase()));
    }
    for (int index = 0; index < keywordsNoCase.length; ++index) {
      String keyword = keywordsNoCase[index];
      assertTrue("Couldn't find \"" + keyword + "\".", _properties.keywordExists(keyword));
      assertTrue("Couldn't find \"" + keyword.toUpperCase() + "\".", _properties.keywordExists(keyword.toUpperCase()));
      assertTrue("Couldn't find \"" + keyword.toLowerCase() + "\".", _properties.keywordExists(keyword.toLowerCase()));
    }

    // check enumeration
    Iterator iter  = _properties.getKeywords();
    int      count = 0;

    while (iter.hasNext()) {
      iter.next();
      count++;
    }
    assertTrue("More / less keywords than expected: " + count, count == keywordsCase.length + keywordsNoCase.length);
    
    // check removal
    for (int index = 0; index < keywordsCase.length; ++index) {
      _currEvent = null;
      _properties.removeKeyword(keywordsCase[index]);
      assertTrue("No TokenizerPropertyEvent happened.", _currEvent != null);
      assertTrue("Wrong type of event: " + _currEvent.getType(), _currEvent.getType() == TokenizerPropertyEvent.PROPERTY_REMOVED);
      assertTrue("Wrong property type: " + _currEvent.getProperty().getType(),
                _currEvent.getProperty().getType() == Token.KEYWORD);
      assertTrue("Wrong property image: " + _currEvent.getProperty().getImages()[0],
                keywordsCase[index].equals(_currEvent.getProperty().getImages()[0]));
    }
    for (int index = 0; index < keywordsNoCase.length; ++index) {
      _currEvent = null;
      _properties.removeKeyword(keywordsNoCase[index]);
      assertTrue("No TokenizerPropertyEvent happened.", _currEvent != null);
      assertTrue("Wrong type of event: " + _currEvent.getType(), _currEvent.getType() == TokenizerPropertyEvent.PROPERTY_REMOVED);
      assertTrue("Wrong property type: " + _currEvent.getProperty().getType(),
                _currEvent.getProperty().getType() == Token.KEYWORD);
      assertTrue("Wrong property image: " + _currEvent.getProperty().getImages()[0],
                keywordsNoCase[index].equalsIgnoreCase(_currEvent.getProperty().getImages()[0]));
    }
  }
  
  /**
   * Testing generic methods.
   */
  public void testGenericProperties() throws Throwable {
    // insert properties
    _properties.setWhitespaces(" ");
    assertTrue("Wrong type of event.", _currEvent.getType() == TokenizerPropertyEvent.PROPERTY_MODIFIED);
    assertTrue("Wrong property type in event.", _currEvent.getProperty().getType() == Token.WHITESPACE);
    assertTrue("Wrong property in event.", _currEvent.getProperty().getImages()[0].equals(" "));

    _currEvent = null;
    _properties.setSeparators(TokenizerProperties.DEFAULT_SEPARATORS);
    assertTrue("Unexpected event happened.", _currEvent == null);
    
    for (int index = 0; index < _testProperties.length; ++index) {
      TokenizerProperty prop = _testProperties[index];
      
      _currEvent = null;
      _properties.addProperty(prop);
      assertTrue("No event.", _currEvent != null);
      assertTrue("Wrong type of event.", _currEvent.getType() == TokenizerPropertyEvent.PROPERTY_ADDED);
      assertTrue("Wrong property type in event.", _currEvent.getProperty().getType() == prop.getType());
      if ((prop.getFlags() & TokenizerProperties.F_NO_CASE) != 0) {
        assertTrue("Wrong property in event.", _currEvent.getProperty().getImages()[0].equalsIgnoreCase(prop.getImages()[0]));
      } else {
        assertTrue("Wrong property in event.", _currEvent.getProperty().getImages()[0].equals(prop.getImages()[0]));
      }
      assertTrue("Wrong companion in event.", _currEvent.getProperty().getCompanion() == prop.getCompanion());
    }

    // re-register properties
    for (int index = 0; index < _testProperties.length; ++index) {
      TokenizerProperty prop = _testProperties[index];
      
      _currEvent = null;
      _properties.addProperty(prop);
      assertTrue("Unexpected event while re-registering property.", _currEvent == null);
    }
    
    // test iterator
    Iterator iter  = _properties.getProperties();
    int      count = 0;
    
    while (iter.hasNext()) {
      TokenizerProperty prop  = (TokenizerProperty)iter.next();
      int               index = 0;
      
      count++;
      //      System.out.println(prop);
      while (index < _testProperties.length) {
        // Note that the TokenizerProperties might modify the flags of a property !
        // Thats why we cannot use equals here.
        if (   prop.getType() == _testProperties[index].getType()
            && prop.getCompanion() == _testProperties[index].getCompanion()
            && prop.getImages().length == _testProperties[index].getImages().length) {
          int imageIndex = 0;
          int flags      = prop.getFlags() & (TokenizerProperties.F_CASE | TokenizerProperties.F_NO_CASE);
          
          while (imageIndex < prop.getImages().length) {
            if ((flags & TokenizerProperties.F_CASE) != 0) {
              if (! (prop.getImages()[imageIndex].equals(_testProperties[index].getImages()[imageIndex]))) {
                break;
              }
            } else {
              if (! (prop.getImages()[imageIndex].equalsIgnoreCase(_testProperties[index].getImages()[imageIndex]))) {
                break;
              }
            }
            imageIndex++;
          }
          if (imageIndex >= prop.getImages().length) {
            break;
          }
        }
        index++;
      }
      assertTrue("Unexpected property " + prop.toString() + ".", index < _testProperties.length);
    }
    assertTrue("Too many / few properties: " + count + ". Expected were " + _testProperties.length, count == _testProperties.length);
    
    // check if all properties are present
    for (int index = 0; index < _testProperties.length; ++index) {
      TokenizerProperty prop = _testProperties[index];
    
      assertTrue("Could't find property: " + prop, _properties.propertyExists(prop));
    }
    
    // test removal of properties
    iter  = _properties.getProperties();
    count = 0;
    while (iter.hasNext()) {
      TokenizerProperty prop  = (TokenizerProperty)iter.next();
      
      count++;
      //      System.out.println("Removing: " + prop);
      _currEvent = null;
      iter.remove();
      assertTrue("No event.", _currEvent != null);
      assertTrue("Wrong type of event.", _currEvent.getType() == TokenizerPropertyEvent.PROPERTY_REMOVED);
      assertTrue("Wrong property type in event.", _currEvent.getProperty().getType() == prop.getType());
      if ((prop.getFlags() & TokenizerProperties.F_NO_CASE) != 0) {
        assertTrue("Wrong property in event.", _currEvent.getProperty().getImages()[0].equalsIgnoreCase(prop.getImages()[0]));
      } else {
        assertTrue("Wrong property in event.", _currEvent.getProperty().getImages()[0].equals(prop.getImages()[0]));
      }
      assertTrue("Wrong companion in event.", _currEvent.getProperty().getCompanion() == prop.getCompanion());
      assertTrue("Property still exists: " + prop, ! _properties.propertyExists(prop));
    }
    assertTrue("Too many / few properties: " + count + ". Expected were " + _testProperties.length, count == _testProperties.length);

    // check if all properties have been removed
    for (int index = 0; index < _testProperties.length; ++index) {
      TokenizerProperty prop = _testProperties[index];
    
      assertTrue("Property still present: " + prop, ! _properties.propertyExists(prop));
    }
  }
  
  
  //---------------------------------------------------------------------------
  // TokenizerPropertyListener implementation
  //
  
  /**
   * Event handler method. The given {@link TokenizerPropertyEvent} parameter
   * contains the nessecary information about the property change.
   *
   * @param event the {@link TokenizerPropertyEvent} that describes the change
   */
  public void propertyChanged(TokenizerPropertyEvent event) {
    _currEvent = event;
  }
  
  /**
   * See {@link de.susebox.jtopas.spi.DataProvider} for details.
   *
   * @return the character buffer to read data from
   */
  public char[] getData() {
    return _currData;
  }  

  /**
   * See {@link de.susebox.jtopas.spi.DataProvider} for details.
   *
   * @return  a copy of the valid data of this {@link DataProvider}
   * @see #getData
   */
  public char[] getDataCopy() {
    char[]  dst = new char[_currDataLength];
    
    System.arraycopy(_currData, _currStartPos, dst, 0, _currDataLength);
    return dst;
  }
  
  /**
   * See {@link de.susebox.jtopas.spi.DataProvider} for details.
   *
   * @param testChar  check this character
   * @return <code>true</code> if the given character is a separator,
   *        <code>false</code> otherwise
   */
  public int getLength() {
    return _currDataLength;
  }
  
  /**
   * See {@link de.susebox.jtopas.spi.DataProvider} for details.
   *
   * @return  index in the character array returned by {@link #getData}, where data starts
   */
  public int getStartPosition() {
    return _currStartPos;
  }
  
  //---------------------------------------------------------------------------
  // class members
  //
  static final Object              _companion1 = new Object();
  static final Object              _companion2 = new Object();
  
  static final TokenizerProperty[] _testProperties = {
    new TokenizerProperty(Token.KEYWORD, new String[] { "k1" }, null,        TokenizerProperties.F_NO_CASE),
    new TokenizerProperty(Token.KEYWORD, new String[] { "k2" }, _companion1, TokenizerProperties.F_NO_CASE),
    new TokenizerProperty(Token.KEYWORD, new String[] { "k3" }, _companion2, TokenizerProperties.F_NO_CASE),
    new TokenizerProperty(Token.LINE_COMMENT, new String[] { "//" }, null, TokenizerProperties.F_CASE),
    new TokenizerProperty(Token.LINE_COMMENT, new String[] { "--" }, _companion1, TokenizerProperties.F_CASE ),
    new TokenizerProperty(Token.LINE_COMMENT, new String[] { "#" }, _companion2, TokenizerProperties.F_CASE ),
    new TokenizerProperty(Token.LINE_COMMENT, new String[] { "rem" }, null, TokenizerProperties.F_NO_CASE),
    new TokenizerProperty(Token.BLOCK_COMMENT, new String[] { "/*", "*/" }, null, TokenizerProperties.F_CASE ),
    new TokenizerProperty(Token.BLOCK_COMMENT, new String[] { "/**", "*/" }, _companion1, TokenizerProperties.F_CASE),
    new TokenizerProperty(Token.BLOCK_COMMENT, new String[] { "{", "}" }, _companion2, TokenizerProperties.F_CASE),
    new TokenizerProperty(Token.BLOCK_COMMENT, new String[] { "[startBlockComment]", "[endBlockComment]" }, null, TokenizerProperties.F_NO_CASE),
    new TokenizerProperty(Token.SPECIAL_SEQUENCE, new String[] { ":=" }, null, TokenizerProperties.F_CASE),
    new TokenizerProperty(Token.SPECIAL_SEQUENCE, new String[] { "<>" }, _companion1, TokenizerProperties.F_CASE),
    new TokenizerProperty(Token.SPECIAL_SEQUENCE, new String[] { "!=" }, _companion2, TokenizerProperties.F_CASE),
    new TokenizerProperty(Token.STRING, new String[] { "\"", "\"", "\\" }, null, TokenizerProperties.F_CASE),
    new TokenizerProperty(Token.STRING, new String[] { "'", "'", "\\" }, null, TokenizerProperties.F_CASE),
    new TokenizerProperty(Token.PATTERN, new String[] { "[+\\-]?[0-9]+\\.?[0-9]*" }, _companion1, TokenizerProperties.F_CASE),
    new TokenizerProperty(Token.PATTERN, new String[] { "[A-Z_][A-Z0-9_]*" }, _companion2, TokenizerProperties.F_NO_CASE),
    new TokenizerProperty(Token.KEYWORD, new String[] { "if" }, new Object(), TokenizerProperties.F_CASE),
    new TokenizerProperty(Token.KEYWORD, new String[] { "then" }, new Object(), TokenizerProperties.F_CASE),
    new TokenizerProperty(Token.KEYWORD, new String[] { "while" }, new Object(), TokenizerProperties.F_CASE ),
    new TokenizerProperty(Token.KEYWORD, new String[] { "loop" }, new Object(), TokenizerProperties.F_CASE ),
    new TokenizerProperty(Token.KEYWORD, new String[] { "class" }, new Object(), TokenizerProperties.F_CASE ),
    new TokenizerProperty(Token.KEYWORD, new String[] { "interface" }, new Object(), TokenizerProperties.F_CASE ),
    new TokenizerProperty(Token.KEYWORD, new String[] { "final" }, null, TokenizerProperties.F_CASE ),
    new TokenizerProperty(Token.KEYWORD, new String[] { "implements" }, null, TokenizerProperties.F_CASE ),
    new TokenizerProperty(Token.KEYWORD, new String[] { "int" }, null, TokenizerProperties.F_CASE ),
    new TokenizerProperty(Token.KEYWORD, new String[] { "boolean" }, null, TokenizerProperties.F_CASE ),
    new TokenizerProperty(Token.KEYWORD, new String[] { "void" }, null, TokenizerProperties.F_CASE ),
    new TokenizerProperty(Token.KEYWORD, new String[] { "do" }, null, TokenizerProperties.F_CASE ),
    new TokenizerProperty(Token.KEYWORD, new String[] { "import" }, null, TokenizerProperties.F_CASE ),
    new TokenizerProperty(Token.KEYWORD, new String[] { "package" }, null, TokenizerProperties.F_CASE ),
    new TokenizerProperty(Token.KEYWORD, new String[] { "static" }, null, TokenizerProperties.F_CASE ),
    new TokenizerProperty(Token.KEYWORD, new String[] { "H1" }, null, TokenizerProperties.F_NO_CASE),
    new TokenizerProperty(Token.KEYWORD, new String[] { "H2" }, null, TokenizerProperties.F_NO_CASE),
    new TokenizerProperty(Token.KEYWORD, new String[] { "H3" }, null, TokenizerProperties.F_NO_CASE),
    new TokenizerProperty(Token.KEYWORD, new String[] { "table" }, null, TokenizerProperties.F_NO_CASE),
    new TokenizerProperty(Token.KEYWORD, new String[] { "span" }, null, TokenizerProperties.F_NO_CASE),
    new TokenizerProperty(Token.KEYWORD, new String[] { "layer" }, null, TokenizerProperties.F_NO_CASE),
    new TokenizerProperty(Token.SPECIAL_SEQUENCE, new String[] { ">>>" }, new Object(), TokenizerProperties.F_CASE ),
    new TokenizerProperty(Token.SPECIAL_SEQUENCE, new String[] { ">>" }, null, TokenizerProperties.F_CASE ),
    new TokenizerProperty(Token.SPECIAL_SEQUENCE, new String[] { "<<" }, new Object(), TokenizerProperties.F_CASE ),
    new TokenizerProperty(Token.SPECIAL_SEQUENCE, new String[] { "+=" }, null, TokenizerProperties.F_CASE ),
    new TokenizerProperty(Token.SPECIAL_SEQUENCE, new String[] { "-=" }, new Object(), TokenizerProperties.F_CASE ),
    new TokenizerProperty(Token.SPECIAL_SEQUENCE, new String[] { "++" }, null, TokenizerProperties.F_CASE ),
    new TokenizerProperty(Token.SPECIAL_SEQUENCE, new String[] { "+++" }, new Object(), TokenizerProperties.F_CASE ),
    new TokenizerProperty(Token.SPECIAL_SEQUENCE, new String[] { "*=" }, null, TokenizerProperties.F_CASE ),
    new TokenizerProperty(Token.SPECIAL_SEQUENCE, new String[] { "**" }, null, TokenizerProperties.F_CASE ),
    new TokenizerProperty(Token.SPECIAL_SEQUENCE, new String[] { "**=" }, null, TokenizerProperties.F_CASE ),
    new TokenizerProperty(Token.SPECIAL_SEQUENCE, new String[] { "/=" }, null, TokenizerProperties.F_CASE ),
    new TokenizerProperty(Token.SPECIAL_SEQUENCE, new String[] { "/" }, null, TokenizerProperties.F_CASE ),
    new TokenizerProperty(Token.SPECIAL_SEQUENCE, new String[] { "+" }, null, TokenizerProperties.F_CASE )
  };
  
  static final String _patternMatchingStrings[] = {
    "1",
    "23.95",
    "-10435.3394",
    "+2",
    "+34543",
    "+445.435345830",
    "003",
    "0234.000023",
    "-0.0",
    "+0.0",
    "Gonzo",
    "kermit",
    "Kermit2",
    "_myMuppet",
    "_9934abc",
    "_a_b_c_1_",
    "a__1",
    "aA_123B_C",
  };
  

  //---------------------------------------------------------------------------
  // Members
  //
  private TokenizerProperties     _properties     = null;
  private TokenizerPropertyEvent  _currEvent      = null;
  private char[]                  _currData       = new char[8192];
  private int                     _currStartPos   = 0;
  private int                     _currDataLength = 0;
}
