/*
 * TestPluginTokenizer.java: JUnit test for the InputStreamTokenizer
 *
 * Copyright (C) 2001 Heiko Blau
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
import java.util.ArrayList;
import java.util.Properties;
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
import de.susebox.java.util.TokenizerException;
import de.susebox.java.util.InputStreamTokenizer;


//-----------------------------------------------------------------------------
// Class TestTokenizerSpeed
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
 * @see     de.susebox.java.util.Tokenizer
 * @see     de.susebox.java.util.AbstractTokenizer
 * @see     de.susebox.java.util.InputStreamTokenizer
 * @see     PluginTokenizer
 * @see     java.io.InputStreamReader
 * @author  Heiko Blau
 */
public class TestTokenizerSpeed extends TestCase {
  
  //---------------------------------------------------------------------------
  // properties
  //

  /**
   * The name of the test configuration file. This file will be read by 
   * {@link java.lang.Class#getResourceAsStream}.
   */
  public static final String CONFIG_FILE = "TestTokenizerSpeed.conf";
  
  /**
   * Property for the tests {@link #compareSpeed1} and {@link #compareSpeed2}
   */
  public static final String PROP_PATH = "Path";
  
  
  //---------------------------------------------------------------------------
  // main method
  //
  
  /**
   * call this method to invoke the tests
   */
  public static void main(String[] args) {
    String[]   tests = { TestTokenizerSpeed.class.getName() };
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
      props.load(TestTokenizerSpeed.class.getResourceAsStream(CONFIG_FILE));
    } catch (Exception ex) {
      throw new ExtRuntimeException(ex);
    }
    
    while ((path = props.getProperty(PROP_PATH + count)) != null) {
      if ((url = TestPluginTokenizer.class.getResource(path)) != null) {
        path = url.getFile();
      }
      suite.addTest(new TestTokenizerSpeed("compareSpeed1", path));
      suite.addTest(new TestTokenizerSpeed("compareSpeed2", path));
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
  public TestTokenizerSpeed(String test, String path) {
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
   * Extracting the pure contents of a HTML stream.
   */
  public void compareSpeed1() throws Throwable {
    PluginTokenizer       pluginTokenizer = new PluginTokenizer();
    InputStreamTokenizer  streamTokenizer = new InputStreamTokenizer();
    XMLPlugin1            xmlPlugin       = new XMLPlugin1();
    FileInputStream       reader;
    long                  start;
    
    // configure tokenizers
    pluginTokenizer.setParseFlags(Tokenizer.F_TOKEN_POS_ONLY);
    pluginTokenizer.setWhitespaceHandler(xmlPlugin);
    pluginTokenizer.setSequenceHandler(xmlPlugin);
    pluginTokenizer.setSeparatorHandler(xmlPlugin);

    streamTokenizer.setParseFlags(Tokenizer.F_TOKEN_POS_ONLY);
    streamTokenizer.setSeparators("=");
    streamTokenizer.addString("\"", "\"", "\\");
    streamTokenizer.addBlockComment("<!--", "-->");
    streamTokenizer.addSpecialSequence("<!");
    streamTokenizer.addSpecialSequence("<",  XMLPlugin1.START_TAG_COMP);
    streamTokenizer.addSpecialSequence("</", XMLPlugin1.END_TAG_COMP);
    streamTokenizer.addSpecialSequence(">");

    // start basic tokenizer
    reader = new FileInputStream(_path);
    start  = System.currentTimeMillis();
    
    //    System.out.println("\nInputStreamTokenizer on \"" + _path + "\"");
    
    streamTokenizer.setSource(new InputStreamReader(reader));
    doTokenizing1(streamTokenizer, _streamTagList);
    
    long streamTime = System.currentTimeMillis() - start;
    //    System.out.println("Finished after " + streamTime + " milliseconds");
    reader.close();
    reader = null;
    
    // start plugin tokenizer 1
    reader = new FileInputStream(_path);
    start  = System.currentTimeMillis();

    //    System.out.println("\nPluginTokenizer on \"" + _path + "\"");
    
    pluginTokenizer.setSource(new InputStreamSource(new InputStreamReader(reader)));
    doTokenizing1(pluginTokenizer, _pluginTagList);

    long pluginTime = System.currentTimeMillis() - start;
    //    System.out.println("Finished after " + pluginTime + " milliseconds");
    reader.close();
    reader = null;

    // second run
    _pluginTagList.clear();
    _streamTagList.clear();

    // start plugin tokenizer, second time
    reader = new FileInputStream(_path);
    start  = System.currentTimeMillis();

    //    System.out.println("\nPluginTokenizer on \"" + _path + "\"");
    
    pluginTokenizer.setSource(new InputStreamSource(new InputStreamReader(reader)));
    doTokenizing1(pluginTokenizer, _pluginTagList);

    pluginTime = System.currentTimeMillis() - start;
    //    System.out.println("Finished after " + pluginTime + " milliseconds");
    reader.close();
    reader = null;
    
    // start basic tokenizer, second time
    reader = new FileInputStream(_path);
    start  = System.currentTimeMillis();
    
    //    System.out.println("\nInputStreamTokenizer on \"" + _path + "\"");
    
    streamTokenizer.setSource(new InputStreamReader(reader));
    doTokenizing1(streamTokenizer, _streamTagList);
    
    streamTime = System.currentTimeMillis() - start;
    //    System.out.println("Finished after " + streamTime + " milliseconds");
    reader.close();
    reader = null;
    
    // check tag list equivalence
    //    System.out.println("PluginTokenizer returned " + _pluginTagList.size() + " tags.");
    //    System.out.println("StreamTokenizer returned " + _streamTagList.size() + " tags.");
    assertTrue("No tags found with PluginTokenizer", _pluginTagList.size() > 0);
    assertTrue("No tags found with StreamTokenizer", _streamTagList.size() > 0);
    assertTrue("Plugin tag number (" + _pluginTagList.size() + ") differs from stream tag number (" + _streamTagList.size() + ").",
              _streamTagList.size() == _pluginTagList.size());
    
    for (int idx = 0; idx < _streamTagList.size(); ++idx) {
      TagInfo pluginTag = (TagInfo)_pluginTagList.get(idx);
      TagInfo streamTag = (TagInfo)_streamTagList.get(idx);

      // System.out.println("Plugin / stream tag: \"" + pluginTag.getTag() + "\" / \"" + streamTag.getTag() + "\"");
      assertTrue("Tag mismatch at position " + idx + ": plugin tag 1 \"" + pluginTag.getTag() + "\", stream tag \"" + streamTag.getTag() + "\".",
                pluginTag.getTag().equals(streamTag.getTag()));
    }

    // the plugin tokenizer should be faster than the InputStreamTokenizer
    // assertTrue("The PluginTokenizer lost with " + pluginTime + "ms against " + streamTime + "ms.", pluginTime < streamTime);
  }

  
  /**
   * Extracting the pure contents of a HTML stream.
   */
  public void compareSpeed2() throws Throwable {
    PluginTokenizer       pluginTokenizer = new PluginTokenizer();
    InputStreamTokenizer  streamTokenizer = new InputStreamTokenizer();
    XMLPlugin2            xmlPlugin       = new XMLPlugin2();
    FileInputStream       reader;
    long                  start;
    
    // configure tokenizers
    pluginTokenizer.setParseFlags(Tokenizer.F_TOKEN_POS_ONLY | Tokenizer.F_RETURN_WHITESPACES);
    pluginTokenizer.setWhitespaceHandler(xmlPlugin);
    pluginTokenizer.setSequenceHandler(xmlPlugin);
    pluginTokenizer.setSeparatorHandler(xmlPlugin);

    streamTokenizer.setParseFlags(Tokenizer.F_TOKEN_POS_ONLY | Tokenizer.F_RETURN_WHITESPACES);
    streamTokenizer.setSeparators("=");
    streamTokenizer.addString("\"", "\"", "\\");
    streamTokenizer.addBlockComment("!--", "--");
    streamTokenizer.addSpecialSequence("<!");
    streamTokenizer.addSpecialSequence("/", XMLPlugin2.END_TAG_COMP);
    streamTokenizer.addBlockComment(">", "<", XMLPlugin2.CONTENTS_COMP);
    
    _pluginTagList.clear();
    _streamTagList.clear();

    // start plugin tokenizer
    reader = new FileInputStream(_path);
    start  = System.currentTimeMillis();

    //    System.out.println("\nPluginTokenizer on \"" + _path + "\"");
    
    pluginTokenizer.setSource(new InputStreamSource(new InputStreamReader(reader)));
    doTokenizing2(pluginTokenizer, _pluginTagList);

    long pluginTime = System.currentTimeMillis() - start;
    //    System.out.println("Finished after " + pluginTime + " milliseconds");
    reader.close();
    reader = null;
    
    // start basic tokenizer
    reader = new FileInputStream(_path);
    start  = System.currentTimeMillis();
    
    //    System.out.println("\nInputStreamTokenizer on \"" + _path + "\"");
    
    streamTokenizer.setSource(new InputStreamReader(reader));
    doTokenizing2(streamTokenizer, _streamTagList);
    
    long streamTime = System.currentTimeMillis() - start;
    //    System.out.println("Finished after " + streamTime + " milliseconds");
    reader.close();
    reader = null;
    
    // check tag list equivalence
    //    System.out.println("PluginTokenizer returned " + _pluginTagList.size() + " tags.");
    //    System.out.println("StreamTokenizer returned " + _streamTagList.size() + " tags.");
    assertTrue("No tags found with PluginTokenizer", _pluginTagList.size() > 0);
    assertTrue("No tags found with StreamTokenizer", _streamTagList.size() > 0);
    assertTrue("Plugin tag number (" + _pluginTagList.size() + ") differs from stream tag number (" + _streamTagList.size() + ").",
              _streamTagList.size() == _pluginTagList.size());
    
    for (int idx = 0; idx < _streamTagList.size(); ++idx) {
      TagInfo pluginTag = (TagInfo)_pluginTagList.get(idx);
      TagInfo streamTag = (TagInfo)_streamTagList.get(idx);

      // System.out.println("Plugin / stream tag: \"" + pluginTag.getTag() + "\" / \"" + streamTag.getTag() + "\"");
      assertTrue("Tag mismatch at position " + idx + ": plugin tag \"" + pluginTag.getTag() + "\", stream tag \"" + streamTag.getTag() + "\".",
                pluginTag.getTag().equals(streamTag.getTag()));
    }

    // the plugin tokenizer should be faster than the InputStreamTokenizer
    // assertTrue("The PluginTokenizer lost with " + pluginTime + "ms against " + streamTime + "ms.", pluginTime < streamTime);
  }

  
  /**
   * Helper method
   */
  private void doTokenizing1(Tokenizer tokenizer, ArrayList tagList) throws Throwable {
    while (tokenizer.hasMoreToken()) {
      Token token = tokenizer.nextToken();
      
      if (token.getType() == Token.SPECIAL_SEQUENCE) {
        if (token.getCompanion() == XMLPlugin1.START_TAG_COMP) {
          assertTrue("Tag start without tag name", tokenizer.hasMoreToken());
          token = tokenizer.nextToken();
          assertTrue("Unexpected token type for tag name " + token.getType(), token.getType() == Token.NORMAL);
          tagList.add(new TagInfo(tokenizer.current(), false));
        } else if (token.getCompanion() == XMLPlugin1.END_TAG_COMP) {
          assertTrue("Tag end without tag name", tokenizer.hasMoreToken());
          token = tokenizer.nextToken();
          assertTrue("Unexpected token type for tag name " + token.getType(), token.getType() == Token.NORMAL);
          tagList.add(new TagInfo(tokenizer.current(), true));
        }
      }          
    }
  }

  
  /**
   * Helper method
   */
  private void doTokenizing2(Tokenizer tokenizer, ArrayList tagList) throws Throwable {
    while (tokenizer.hasMoreToken()) {
      Token token = tokenizer.nextToken();
      
      if (token.getType() == Token.BLOCK_COMMENT) {
        if (token.getCompanion() == XMLPlugin2.CONTENTS_COMP) {
          boolean isEndTag = false;
          
          __INNER_LOOP__:
          while (tokenizer.hasMoreToken()) {
            token = tokenizer.nextToken();
            switch (token.getType()) {
            case Token.WHITESPACE:
              break;
            case Token.SPECIAL_SEQUENCE:
              assertTrue("Tag start without tag name", token.getCompanion() == XMLPlugin2.END_TAG_COMP);
              isEndTag = true;
              break;
            case Token.BLOCK_COMMENT:
            case Token.EOF:
              break __INNER_LOOP__;
            default:
              assertTrue("Unexpected token type for tag name " + token.getType(), token.getType() == Token.NORMAL);
              tagList.add(new TagInfo(tokenizer.current(), isEndTag));
              break __INNER_LOOP__;
            }
          }
        }
      }          
    }
  }

  
  //---------------------------------------------------------------------------
  // inner class
  //
  
  /**
   * This class stores information about a tag
   */
  protected class TagInfo {
    public TagInfo(String tag, boolean isEndOfTag) {
      _tag        = tag;
      _isEndOfTag = isEndOfTag;
    }
    
    public String getTag() {
      return _tag;
    }
    
    public boolean isEndOfTag() {
      return _isEndOfTag;
    }
    
    private String  _tag = null;
    private boolean _isEndOfTag = false;
  }
  
  
  //---------------------------------------------------------------------------
  // Members
  //
  private String      _path           = null;
  private ArrayList   _pluginTagList  = new ArrayList(1024);
  private ArrayList   _streamTagList  = new ArrayList(1024);
}


/**
 * Handler class for the plugin tokenizer, first type. XML-Tags like "<", "</"
 * and ">" are basic special sequences.
 */
class XMLPlugin1 implements  WhitespaceHandler, SequenceHandler, SeparatorHandler 
{
  
  //---------------------------------------------------------------------------
  // constructor
  //
  
  /**
   * Simple constructor
   */
  public XMLPlugin1() {};
  

  //---------------------------------------------------------------------------
  // interface methods
  //

  /**
   * When registering an instance that implements this interface, the
   * {@link PluginTokenizer} will call this method to make itself known to
   * the <code>SeparatorHandler</code> instance in turn.
   *
   * @param tokenizer   the controlling {@link PluginTokenizer}
   */
  public void setTokenizer(PluginTokenizer tokenizer) {
    _tokenizer = tokenizer;
  }
  
  /**
   * This method detects the number of whitespace characters starting at the given
   * position. It should use {@link getChar} to retrieve a character to check.
   * <br>
   * The method should return the number of characters identified as whitespaces
   * starting from and including the given start position.
   * <br>
   * Do not attempt to actually read more data or do anything that leads to the
   * change of the data source or to tokenizer switching. This is done by the
   * tokenizer framework.
   *
   * @param   startingAtPos  start checking for whitespace from this position
   * @param   maxChars  if there is no non-whitespace character, read up to this number of characters
   * @return  number of whitespace characters starting from the given offset
   * @throws  TokenizerException failure while reading data from the input stream
   */
  public int readWhitespaces(int startingAtPos, int maxChars) {
    int pos    = startingAtPos;
    int endPos = startingAtPos + maxChars;
    
    while (pos < endPos) {
      if ( ! isWhitespace(_tokenizer.getCharUnchecked(pos))) {
        break;
      }
      pos++;
    }
    return pos - startingAtPos;
  }

  
  /**
   * This method checks if the character is a whitespace.
   *
   * @param testChar  check this character
   * @return <CODE>true</CODE> if the given character is a whitespace,
   *        <CODE>false</CODE> otherwise
   */
  public boolean isWhitespace(char testChar) {
    switch (testChar) {
      case ' ':
      case '\t':
      case '\r':
      case '\n':
        return true;
      default:
        return false;
    }
  }
  
  /**
   * This method checks if the character is a separator.
   *
   * @param testChar  check this character
   * @return <CODE>true</CODE> if the given character is a separator,
   *         <CODE>false</CODE> otherwise
   */
  public boolean isSeparator(char testChar) {
    return testChar == '=';
  }
  
  
  /**
   * Return a {@link de.susebox.java.util.TokenizerProperty} if the character
   * starting at the given position comprise a special sequence (like the ++ operator
   * in C and Java or the &amp;bsp; in HTML), a comment starting sequence or
   * a string sign.<br>
   * Return <code>null</code> if no special sequence is present at the given
   * position.<br>
   * Use the {@link de.susebox.java.util.AbstractTokenizer#getCharUnchecked} to
   * retrieve a character from the tokenizers input buffer.
   *
   * @param  startingAtPos check from this position in the tokenizers input buffer
   * @throws {@link de.susebox.java.util.TokenizerException} for any problems
   * @return a <code>TokenizerProperty</code> instance describing the special sequence,
   *        comment etc. or <code>null</code> if no such thing was found.
   */
  public TokenizerProperty isSequenceCommentOrString(int startingAtPos, int maxChars) 
    throws TokenizerException
  {
    TokenizerProperty prop = null;
    
    switch (_tokenizer.getCharUnchecked(startingAtPos)) {

    case '"':
      // strings are attribute values in XML
      prop = STRING_PROP;
      break;

    case '<':
      // tag opening
      switch (_tokenizer.getCharUnchecked(startingAtPos + 1)) {
      case '!':
        if (   _tokenizer.getCharUnchecked(startingAtPos + 2) == '-' 
            && _tokenizer.getCharUnchecked(startingAtPos + 3) == '-') {
          prop = COMMENT_PROP;
        } else {
          prop = SPEC_COMMENT_PROP;
        }
        break;
      case '/':
        prop = END_TAG;
        break;
      default:
        prop = START_TAG;
        break;
      }
      break;
      
    case '>':
      // tag closing
      prop = TAG_END;
      break;
    }
  
    // either we found one or the initial null is returned
    return prop;
  }
  
  
  /**
   * This method is called by the parent {@link PluginTokenizer} to learn how
   * many characters are needed by an instance of this interface to identify a
   * special sequence in the worst case. Usually that should be the length of
   * the longest possible special sequence, comment prefix etc.
   * The tokenizer will make sure that at least this number of characters is
   * available when {@link #isSequenceCommentOrString} is called. If less
   * characters are provided, EOF is reached.
   *
   * @return  the number of characters needed in the worst case to identify a
   *         special sequence
   */
  public int getSequenceMaxLength() {
    return 4;   // length of "<!--"
  }
  

  
  //---------------------------------------------------------------------------
  // Constants for use in this package
  //
  protected static final Object END_TAG_COMP   = new Object();
  protected static final Object START_TAG_COMP = new Object();
  
  
  //---------------------------------------------------------------------------
  // Constants
  //
  private static final TokenizerProperty  STRING_PROP
    = new TokenizerProperty(Token.STRING, new String[] { "\"", "\"", "\\" }, null );

  private static final TokenizerProperty  COMMENT_PROP
    = new TokenizerProperty(Token.BLOCK_COMMENT, new String[] { "<!--", "-->" }, null );

  private static final TokenizerProperty  SPEC_COMMENT_PROP
    = new TokenizerProperty(Token.SPECIAL_SEQUENCE, new String[] { "<!" }, null );

  private static final TokenizerProperty  END_TAG
    = new TokenizerProperty(Token.SPECIAL_SEQUENCE, new String[] { "</" }, END_TAG_COMP );

  private static final TokenizerProperty  START_TAG
    = new TokenizerProperty(Token.SPECIAL_SEQUENCE, new String[] { "<" }, START_TAG_COMP );

  private static final TokenizerProperty  TAG_END
    = new TokenizerProperty(Token.SPECIAL_SEQUENCE, new String[] { ">" }, null );

    
  //---------------------------------------------------------------------------
  // Members
  //
  private PluginTokenizer _tokenizer = null;
}


/**
 * Handler class for the plugin tokenizer, second type. Everything outside an
 * XML-Tags is treated as a block comment.
 */
class XMLPlugin2 implements  WhitespaceHandler, SequenceHandler, SeparatorHandler 
{
  
  //---------------------------------------------------------------------------
  // constructor
  //
  
  /**
   * Simple constructor
   */
  public XMLPlugin2() {};
  

  //---------------------------------------------------------------------------
  // interface methods
  //

  /**
   * When registering an instance that implements this interface, the
   * {@link PluginTokenizer} will call this method to make itself known to
   * the <code>SeparatorHandler</code> instance in turn.
   *
   * @param tokenizer   the controlling {@link PluginTokenizer}
   */
  public void setTokenizer(PluginTokenizer tokenizer) {
    _tokenizer = tokenizer;
  }
  
  /**
   * This method detects the number of whitespace characters starting at the given
   * position. It should use {@link getChar} to retrieve a character to check.
   * <br>
   * The method should return the number of characters identified as whitespaces
   * starting from and including the given start position.
   * <br>
   * Do not attempt to actually read more data or do anything that leads to the
   * change of the data source or to tokenizer switching. This is done by the
   * tokenizer framework.
   *
   * @param   startingAtPos  start checking for whitespace from this position
   * @param   maxChars  if there is no non-whitespace character, read up to this number of characters
   * @return  number of whitespace characters starting from the given offset
   * @throws  TokenizerException failure while reading data from the input stream
   */
  public int readWhitespaces(int startingAtPos, int maxChars) {
    int pos    = startingAtPos;
    int endPos = startingAtPos + maxChars;
    
    while (pos < endPos) {
      if ( ! isWhitespace(_tokenizer.getCharUnchecked(pos))) {
        break;
      }
      pos++;
    }
    return pos - startingAtPos;
  }

  
  /**
   * This method checks if the character is a whitespace.
   *
   * @param testChar  check this character
   * @return <CODE>true</CODE> if the given character is a whitespace,
   *        <CODE>false</CODE> otherwise
   */
  public boolean isWhitespace(char testChar) {
    switch (testChar) {
      case ' ':
      case '\t':
      case '\r':
      case '\n':
        return true;
      default:
        return false;
    }
  }
  
  /**
   * This method checks if the character is a separator.
   *
   * @param testChar  check this character
   * @return <CODE>true</CODE> if the given character is a separator,
   *         <CODE>false</CODE> otherwise
   */
  public boolean isSeparator(char testChar) {
    return testChar == '=';
  }
  
  
  /**
   * Return a {@link de.susebox.java.util.TokenizerProperty} if the character
   * starting at the given position comprise a special sequence (like the ++ operator
   * in C and Java or the &amp;bsp; in HTML), a comment starting sequence or
   * a string sign.<br>
   * Return <code>null</code> if no special sequence is present at the given
   * position.<br>
   * Use the {@link de.susebox.java.util.AbstractTokenizer#getCharUnchecked} to
   * retrieve a character from the tokenizers input buffer.
   *
   * @param  startingAtPos check from this position in the tokenizers input buffer
   * @throws {@link de.susebox.java.util.TokenizerException} for any problems
   * @return a <code>TokenizerProperty</code> instance describing the special sequence,
   *        comment etc. or <code>null</code> if no such thing was found.
   */
  public TokenizerProperty isSequenceCommentOrString(int startingAtPos, int maxChars) 
    throws TokenizerException
  {
    TokenizerProperty prop = null;
    
    switch (_tokenizer.getCharUnchecked(startingAtPos)) {

    case '"':
      // strings are attribute values in XML
      prop = STRING_PROP;
      break;

    case '>':
      // tag closing -> this is begin of block comment
      prop = CONTENTS_PROP;
      break;
      
    case '<':
      // tag opening for special tags (<!DOCTYPE ...) or real XML comments
      if (_tokenizer.getCharUnchecked(startingAtPos + 1) == '!') {
        // XML comment
        if (   _tokenizer.getCharUnchecked(startingAtPos + 2) == '-' 
            && _tokenizer.getCharUnchecked(startingAtPos + 3) == '-') {
          prop = COMMENT_PROP;
        } else {
          prop = SPEC_COMMENT_PROP;
        }
      }
      break;
    
    case '!':
      // XML comment
      if (   _tokenizer.getCharUnchecked(startingAtPos + 1) == '-' 
          && _tokenizer.getCharUnchecked(startingAtPos + 2) == '-') {
        prop = COMMENT_PROP;
      } else {
        prop = SPEC_COMMENT_PROP;
      }
      break;
    
    case '/':
      prop = END_TAG;
      break;
    }
  
    // either we found one or the initial null is returned
    return prop;
  }
  
  
  /**
   * This method is called by the parent {@link PluginTokenizer} to learn how
   * many characters are needed by an instance of this interface to identify a
   * special sequence in the worst case. Usually that should be the length of
   * the longest possible special sequence, comment prefix etc.
   * The tokenizer will make sure that at least this number of characters is
   * available when {@link #isSequenceCommentOrString} is called. If less
   * characters are provided, EOF is reached.
   *
   * @return  the number of characters needed in the worst case to identify a
   *         special sequence
   */
  public int getSequenceMaxLength() {
    return 4;   // length of "<!--"
  }
  

  
  //---------------------------------------------------------------------------
  // Constants for use in this package
  //
  protected static final Object END_TAG_COMP  = new Object();
  protected static final Object CONTENTS_COMP = new Object();
  
  
  //---------------------------------------------------------------------------
  // Constants
  //
  private static final TokenizerProperty  STRING_PROP
    = new TokenizerProperty(Token.STRING, new String[] { "\"", "\"", "\\" }, null );

  private static final TokenizerProperty  COMMENT_PROP
    = new TokenizerProperty(Token.BLOCK_COMMENT, new String[] { "!--", "--" }, null );
  
  private static final TokenizerProperty  CONTENTS_PROP
    = new TokenizerProperty(Token.BLOCK_COMMENT, new String[] { ">", "<" }, CONTENTS_COMP );

  private static final TokenizerProperty  SPEC_COMMENT_PROP
    = new TokenizerProperty(Token.SPECIAL_SEQUENCE, new String[] { "<!" }, null );

  private static final TokenizerProperty  END_TAG
    = new TokenizerProperty(Token.SPECIAL_SEQUENCE, new String[] { "/" }, END_TAG_COMP );

    
  //---------------------------------------------------------------------------
  // Members
  //
  private PluginTokenizer _tokenizer = null;
}
