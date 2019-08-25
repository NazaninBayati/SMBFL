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
import java.util.Vector;
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


//-----------------------------------------------------------------------------
// Class TestPluginTokenizer
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
 * @see     PluginTokenizer
 * @see     java.io.InputStreamReader
 * @author  Heiko Blau
 */
public class TestPluginTokenizer 
  extends     TestCase
  implements  WhitespaceHandler, SequenceHandler
{
  
  //---------------------------------------------------------------------------
  // properties
  //

  /**
   * The name of the test configuration file. This file will be read by 
   * {@link java.lang.Class#getResourceAsStream}.
   */
  public static final String CONFIG_FILE = "TestPluginTokenizer.conf";
  
  /**
   * Property for the tests. A path to a file to use as test data source
   */
  public static final String PROP_PATH = "Path";
  
  
  //---------------------------------------------------------------------------
  // main method
  //
  
  /**
   * call this method to invoke the tests
   */
  public static void main(String[] args) {
    String[]   tests = { TestPluginTokenizer.class.getName() };
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
      props.load(TestPluginTokenizer.class.getResourceAsStream(CONFIG_FILE));
    } catch (Exception ex) {
      throw new ExtRuntimeException(ex);
    }
    
    while ((path = props.getProperty(PROP_PATH + count)) != null) {
      if ((url = TestPluginTokenizer.class.getResource(path)) != null) {
        path = url.getFile();
      }
      suite.addTest(new TestPluginTokenizer("testContentsParsing",    path));
      suite.addTest(new TestPluginTokenizer("testContentsFormatting", path));
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
  public TestPluginTokenizer(String test, String path) {
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
  
  
  /**
   * Extracting the pure contents of a HTML stream.
   */
  public void testContentsParsing() throws Throwable {
    long            start     = System.currentTimeMillis();
    PluginTokenizer tokenizer = new PluginTokenizer();

    //    System.out.println("\nStart extracting contents in \"" + _path + "\"");
    
    tokenizer.setSource(new InputStreamSource(_reader));
    tokenizer.setParseFlags(Tokenizer.F_NO_CASE | Tokenizer.F_TOKEN_POS_ONLY);
    tokenizer.setWhitespaceHandler(this);
    tokenizer.setSequenceHandler(this);

    while (tokenizer.hasMoreToken()) {
      tokenizer.nextToken();
      //      System.out.println(tokenizer.current());
      assertTrue("Method current() returned null.", tokenizer.current() != null);
    }
    
    long diff = System.currentTimeMillis() - start;
    //    System.out.println("Finished after " + diff + " milliseconds");
  }
  
  
  /**
   * Advanced contents extracting. Lines will be around 80 characters, a basic
   * paragraph recognition takes place.
   */
  public void testContentsFormatting() throws Throwable{
    long            start     = System.currentTimeMillis();
    PluginTokenizer tokenizer = new PluginTokenizer();
    Token           token;
    int             len;
    int             inPRE     = 0;

    //    System.out.println("\nStart formatting contents in \"" + _path + "\"");
    
    tokenizer.setSource(new InputStreamSource(_reader));
    tokenizer.setParseFlags( Tokenizer.F_NO_CASE 
                           | Tokenizer.F_TOKEN_POS_ONLY 
                           | Tokenizer.F_RETURN_WHITESPACES);
    tokenizer.setSeparators(null);
    tokenizer.setWhitespaceHandler(this);
    tokenizer.setSequenceHandler(this);

    len = 0;
    while (tokenizer.hasMoreToken()) {
      token = tokenizer.nextToken();
      switch (token.getType()) {
      case Token.NORMAL:
	  //        System.out.print(tokenizer.current());
        if (inPRE <= 0) {
          len += token.getLength();
        }
        break;
        
      case Token.SPECIAL_SEQUENCE:
        if (token.getCompanion() == PRE_START_COMPANION) {
	    //          System.out.println();
          len   = 0;
          inPRE++;
        } else if (token.getCompanion() == PRE_END_COMPANION) {
          System.out.println();
          len   = 0;
          inPRE--;
        } else {
	    //          System.out.print((String)token.getCompanion());
        }
        break;
        
      case Token.BLOCK_COMMENT:
        if (len > 0) {
	    //          System.out.println();
          len = 0;
        }
        break;
        
      case Token.WHITESPACE:
        if (inPRE > 0) {
	    //          System.out.print(tokenizer.current());
        } else if (len > 75) {
	    //          System.out.println();
          len = 0;
        } else if (len > 0) {
	    //          System.out.print(' ');
          len++;
        }
        break;
      }
    }

    long diff = System.currentTimeMillis() - start;
    //    System.out.println("Finished after " + diff + " milliseconds");
  }
  

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
   * position. It should use {@link de.susebox.java.util.Tokenizer#getChar} or 
   * {@link de.susebox.java.util.AbstractTokenizer#getCharUnchecked} to retrieve a 
   * character to check.
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
   * @return a <code>TokenizerProperty</code> instance describing the special sequence,
   *        comment etc. or <code>null</code> if no such thing was found.
   */
  public TokenizerProperty isSequenceCommentOrString(int startingAtPos, int maxChars) {
    TokenizerProperty prop = null;
    String            text;
    char              nextChar;
    
    switch (_tokenizer.getCharUnchecked(startingAtPos)) {
    // handling a variety of HTML tags
    case '<':
      if (maxChars >= 2) {
        nextChar = _tokenizer.getCharUnchecked(startingAtPos + 1);
        switch (nextChar) {
        case '!':
          if (   maxChars >= 4 
              && _tokenizer.getCharUnchecked(startingAtPos + 2) == '-'
              && _tokenizer.getCharUnchecked(startingAtPos + 3) == '-') {
            prop = HTML_COMMENT;
          }
          break;
        case 'H':
        case 'h':
          if (   maxChars >= 6 
              && _tokenizer.getText(startingAtPos + 2, 4).compareToIgnoreCase("EAD>") == 0) {
            prop = HEAD_COMMENT;
          }
          break;
        case 'c':
        case 'C':
          if (   maxChars >= 6 
              && _tokenizer.getText(startingAtPos + 2, 4).compareToIgnoreCase("ODE>") == 0) {
            prop = CODE_TAG;
          }
          break;
        case 'p':
        case 'P':
          if (   maxChars >= 5
              && _tokenizer.getText(startingAtPos + 2, 3).compareToIgnoreCase("RE>") == 0) {
            prop = PRE_TAG;
          }
          break;
        case '/':
          if (maxChars >= 7) {
            text = _tokenizer.getText(startingAtPos + 2, 5);
          } else if (  maxChars >= 6) {
            text = _tokenizer.getText(startingAtPos + 2, 4);
          } else if (maxChars >= 4) {
            text = _tokenizer.getText(startingAtPos + 2, 2);
          } else {
            break;    // this is a not expected situation (EOF in tag)
          }
          if (text.compareToIgnoreCase("B>") == 0) {
            prop = BOLD_END_TAG;
          } else if (text.compareToIgnoreCase("I>") == 0) {
            prop = ITALIC_END_TAG;
          } else if (text.compareToIgnoreCase("CODE>") == 0) {
            prop = CODE_END_TAG;
          } else if (text.compareToIgnoreCase("PRE>") == 0) {
            prop = PRE_END_TAG;
          }
          break;
        case 'b':
        case 'B':
          if (   maxChars >= 3
              && _tokenizer.getCharUnchecked(startingAtPos + 2) == '>') {
            prop = BOLD_TAG;
          }
          break;
        case 'i':
        case 'I':
          if (   maxChars >= 3
              && _tokenizer.getCharUnchecked(startingAtPos + 2) == '>') {
            prop = ITALIC_TAG;
          }
          break;
        }
      }

      // no special tag found - its simply a tag regarded as a comment
      if (prop == null) {
        prop = TAG_COMMENT;
      }
      break;

    // handling special character encodings
    case '&':
      if (maxChars >= 8) {
        text = _tokenizer.getText(startingAtPos + 1, 7);
      } else if (maxChars >= 7) {
        text = _tokenizer.getText(startingAtPos + 1, 6);
      } else if (maxChars >= 6) {
        text = _tokenizer.getText(startingAtPos + 1, 5);
      } else {
        break;    // this is a not expected situation (EOF in special character)
      }
      if (text.compareToIgnoreCase("auml;") == 0) {
        prop = AUML_TAG;
      } else if (text.compareToIgnoreCase("ouml;") == 0) {
        prop = OUML_TAG;
      } else if (text.compareToIgnoreCase("uuml;") == 0) {
        prop = UUML_TAG;
      } else if (text.compareToIgnoreCase("szlig;") == 0) {
        prop = SZLIG_TAG;
      } else if (text.compareToIgnoreCase("amp;lt;") == 0) {
        prop = LT_TAG;
      } else if (text.compareToIgnoreCase("amp;gt;") == 0) {
        prop = GT_TAG;
      }
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
   * available when {@link SequenceHandler#isSequenceCommentOrString} is called. 
   * If less characters are provided, EOF is reached.
   *
   * @return  the number of characters needed in the worst case to identify a
   *          special sequence
   */
  public int getSequenceMaxLength() {
    return 8;   // length of "&amp;lt;"
  }
  
  
  //---------------------------------------------------------------------------
  // Members
  //
  private InputStreamReader _reader     = null;
  private String            _path       = null;
  private PluginTokenizer   _tokenizer  = null;
  
  
  //---------------------------------------------------------------------------
  // Constants
  //
  private static final Object PRE_START_COMPANION = new Object();
  private static final Object PRE_END_COMPANION   = new Object();
  
  private static final TokenizerProperty  TAG_COMMENT 
    = new TokenizerProperty(Token.BLOCK_COMMENT, new String[] { "<", ">" }, null );

  private static final TokenizerProperty  HEAD_COMMENT 
    = new TokenizerProperty(Token.BLOCK_COMMENT, new String[] { "<HEAD>", "</HEAD>" }, null, Tokenizer.F_NO_CASE );
    
  private static final TokenizerProperty  HTML_COMMENT 
    = new TokenizerProperty(Token.BLOCK_COMMENT, new String[] { "<!--", "-->" }, null );
    
  private static final TokenizerProperty  BOLD_TAG 
    = new TokenizerProperty(Token.SPECIAL_SEQUENCE, new String[] { "<b>" }, "", Tokenizer.F_NO_CASE );

  private static final TokenizerProperty  BOLD_END_TAG 
    = new TokenizerProperty(Token.SPECIAL_SEQUENCE, new String[] { "</b>" }, "", Tokenizer.F_NO_CASE );
    
  private static final TokenizerProperty  ITALIC_TAG 
    = new TokenizerProperty(Token.SPECIAL_SEQUENCE, new String[] { "<i>" }, "", Tokenizer.F_NO_CASE );

  private static final TokenizerProperty  ITALIC_END_TAG 
    = new TokenizerProperty(Token.SPECIAL_SEQUENCE, new String[] { "</i>" }, "", Tokenizer.F_NO_CASE );

  private static final TokenizerProperty  CODE_TAG 
    = new TokenizerProperty(Token.SPECIAL_SEQUENCE, new String[] { "<code>" }, "", Tokenizer.F_NO_CASE );

  private static final TokenizerProperty  CODE_END_TAG 
    = new TokenizerProperty(Token.SPECIAL_SEQUENCE, new String[] { "</code>" }, "", Tokenizer.F_NO_CASE );

  private static final TokenizerProperty  PRE_TAG 
    = new TokenizerProperty(Token.SPECIAL_SEQUENCE, new String[] { "<pre>" }, PRE_START_COMPANION, Tokenizer.F_NO_CASE );

  private static final TokenizerProperty  PRE_END_TAG 
    = new TokenizerProperty(Token.SPECIAL_SEQUENCE, new String[] { "</pre>" }, PRE_END_COMPANION, Tokenizer.F_NO_CASE );

  private static final TokenizerProperty  LT_TAG 
    = new TokenizerProperty(Token.SPECIAL_SEQUENCE, new String[] { "&amp;lt;" }, "<", Tokenizer.F_NO_CASE );

  private static final TokenizerProperty  GT_TAG 
    = new TokenizerProperty(Token.SPECIAL_SEQUENCE, new String[] { "&amp;gt;" }, ">", Tokenizer.F_NO_CASE );

  private static final TokenizerProperty  AUML_TAG 
    = new TokenizerProperty(Token.SPECIAL_SEQUENCE, new String[] { "&auml;" }, "ä", Tokenizer.F_NO_CASE );

  private static final TokenizerProperty  OUML_TAG 
    = new TokenizerProperty(Token.SPECIAL_SEQUENCE, new String[] { "&ouml;" }, "ö", Tokenizer.F_NO_CASE );

  private static final TokenizerProperty  UUML_TAG 
    = new TokenizerProperty(Token.SPECIAL_SEQUENCE, new String[] { "&uuml;" }, "ü", Tokenizer.F_NO_CASE );

  private static final TokenizerProperty  SZLIG_TAG 
    = new TokenizerProperty(Token.SPECIAL_SEQUENCE, new String[] { "&szlig;" }, "ß", Tokenizer.F_NO_CASE );
}
