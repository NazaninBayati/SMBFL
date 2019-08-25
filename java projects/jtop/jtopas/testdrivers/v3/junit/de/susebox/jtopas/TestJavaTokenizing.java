/*
 * TestJavaTokenizing.java: JUnit test for the PluginTokenizer
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
import java.util.HashMap;
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

import de.susebox.jtopas.PluginTokenizer;
import de.susebox.jtopas.KeywordHandler;
import de.susebox.jtopas.SequenceHandler;


//-----------------------------------------------------------------------------
// Class TestJavaTokenizing
//

/**<p>
 * This test suite works with a test configuration file. This file contains some
 * sets of properties, each set for one or more different test runs.
 *</p><p>
 * The properties are defined as class constants. In the configuration file, a 
 * property consists of the property name and a number identifying the property
 * set. 
 *</p><p>
 * The class is deprecated due to the deprecation of the {@link PluginTokenizer}.
 *</p>
 *
 * @see     de.susebox.java.util.Tokenizer
 * @see     de.susebox.java.util.AbstractTokenizer
 * @see     de.susebox.java.util.InputStreamTokenizer
 * @see     PluginTokenizer
 * @see     java.io.InputStreamReader
 * @author  Heiko Blau
 * @deprecated
 */
public class TestJavaTokenizing 
  extends     TestCase 
  implements  KeywordHandler, SequenceHandler
{
  
  //---------------------------------------------------------------------------
  // properties
  //

  /**
   * The name of the test configuration file. This file will be read by 
   * {@link java.lang.Class#getResourceAsStream}.
   */
  public static final String CONFIG_FILE = "TestJavaTokenizing.conf";
  
  /**
   * Property for the tests {@link #checkBraces}
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
      props.load(TestJavaTokenizing.class.getResourceAsStream(CONFIG_FILE));
    } catch (Exception ex) {
      throw new ExtRuntimeException(ex);
    }
    
    while ((path = props.getProperty(PROP_PATH + count)) != null) {
      if ((url = TestPluginTokenizer.class.getResource(path)) != null) {
        path = url.getFile();
      }
      suite.addTest(new TestJavaTokenizing("checkBraces", path));
      suite.addTest(new TestJavaTokenizing("checkImportStatement", path));
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
  public TestJavaTokenizing(String test, String path) {
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
    // initializing the keyword map
    if (_keywords.size() == 0) {
      _keywords.put("public",     new TokenizerProperty(Token.KEYWORD, new String[] { "public"    } ));
      _keywords.put("protected",  new TokenizerProperty(Token.KEYWORD, new String[] { "protected" } ));
      _keywords.put("private",    new TokenizerProperty(Token.KEYWORD, new String[] { "private"   } ));
      _keywords.put("class",      new TokenizerProperty(Token.KEYWORD, new String[] { "class"     } ));
      _keywords.put("final",      new TokenizerProperty(Token.KEYWORD, new String[] { "final"     } ));
      _keywords.put("interface",  new TokenizerProperty(Token.KEYWORD, new String[] { "interface" } ));
      _keywords.put("void",       new TokenizerProperty(Token.KEYWORD, new String[] { "void"      } ));
      _keywords.put("byte",       new TokenizerProperty(Token.KEYWORD, new String[] { "byte"      } ));
      _keywords.put("char",       new TokenizerProperty(Token.KEYWORD, new String[] { "char"      } ));
      _keywords.put("short",      new TokenizerProperty(Token.KEYWORD, new String[] { "short"     } ));
      _keywords.put("int",        new TokenizerProperty(Token.KEYWORD, new String[] { "int"       } ));
      _keywords.put("long",       new TokenizerProperty(Token.KEYWORD, new String[] { "long"      } ));
      _keywords.put("double",     new TokenizerProperty(Token.KEYWORD, new String[] { "double"    } ));
      _keywords.put("float",      new TokenizerProperty(Token.KEYWORD, new String[] { "float"     } ));
      _keywords.put("String",     new TokenizerProperty(Token.KEYWORD, new String[] { "String"    } ));
      _keywords.put("throws",     new TokenizerProperty(Token.KEYWORD, new String[] { "throws"    } ));
      _keywords.put("static",     new TokenizerProperty(Token.KEYWORD, new String[] { "static"    } ));
      _keywords.put("import",     new TokenizerProperty(Token.KEYWORD, new String[] { "import"    } ));
    }

    // opening the data source
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
   * Check the balance of braces. This test assumes a syntactically valid Java,
   * C or C++ file. 
   */
  public void checkBraces() throws Throwable {
    PluginTokenizer       tokenizer = new PluginTokenizer();
    FileInputStream       reader;
    long                  start;
    int                   braceLevel = 0;
    int                   openCount  = 0;
    
    // configure tokenizers
    tokenizer.setParseFlags(Tokenizer.F_TOKEN_POS_ONLY);
    tokenizer.setSequenceHandler(this);
    tokenizer.setKeywordHandler(this);
    tokenizer.setSource(new InputStreamSource(_reader));

    // start plugin tokenizer 1
    start  = System.currentTimeMillis();

    //    System.out.println("\nChecking brace balance on \"" + _path + "\"");
    
    while (tokenizer.hasMoreToken()) {
      Token token = tokenizer.nextToken();
      
      switch (token.getType()) {
      case Token.SPECIAL_SEQUENCE:
        if (token.getCompanion() == BRACE_OPEN_COMPANION) {
          braceLevel++;
          openCount++;
        } else if (token.getCompanion() == BRACE_CLOSE_COMPANION) {
          braceLevel--;
        }
        break;
      }
    }

    long diffTime = System.currentTimeMillis() - start;
    //    System.out.println("Finished after " + diffTime + " milliseconds");
    //    System.out.println("Found " + openCount + " opening braces.");
    
    assertTrue("Opening braces dont match closing ones (open - close = " + braceLevel + ").", braceLevel == 0);
    assertTrue("There should be at least one brace pair in a Java file.", openCount > 0);
  }


  /**
   * Check the syntax of an import statement. This test requieres a valid Java
   * source.
   */
  public void checkImportStatement() throws Throwable {
    PluginTokenizer tokenizer = new PluginTokenizer();
    FileInputStream reader;
    long            start;
    int             stmtCount = 0;
    
    // configure tokenizers
    tokenizer.setSequenceHandler(this);
    tokenizer.setKeywordHandler(this);
    tokenizer.setSource(new InputStreamSource(_reader));

    // start plugin tokenizer 1
    start  = System.currentTimeMillis();

    //    System.out.println("\nChecking import statements on \"" + _path + "\"");
    
    while (tokenizer.hasMoreToken()) {
      Token token = tokenizer.nextToken();
      
      if (token.getType() == Token.KEYWORD && token.getToken().equals("import")) {
        boolean expectColon     = false;
        boolean expectSemicolon = false;
        
        stmtCount++;
        do {
          assertTrue("Expected more token.", tokenizer.hasMoreToken());
          token = tokenizer.nextToken();
          
          if (expectSemicolon) {
            assertTrue("Expected end-of-statement character. Found " + token.getToken() + " of type " + token.getType(),
                      token.getType() == Token.SPECIAL_SEQUENCE && token.getCompanion() == SEMICOLON_COMPANION);
            assertTrue(token.getToken().equals(";"));
          } else if (expectColon) {
            assertTrue("Expected colon or end-of-statement character. Found " + token.getToken() + " of type " + token.getType(),
                      token.getCompanion() == COLON_COMPANION || token.getCompanion() == SEMICOLON_COMPANION);
            assertTrue(token.getToken().equals(".") || token.getToken().equals(";"));
            expectColon = false;
          } else {
            assertTrue("Expected package name or wildcard. Found " + token.getToken() + " of type " + token.getType(),
                      token.getCompanion() == STAR_COMPANION || token.getType() == Token.NORMAL);
            if (token.getCompanion() == STAR_COMPANION) {
              expectSemicolon = true;
            } else {
              expectColon = true;
            }
          }
        } while (   token.getType() == Token.SPECIAL_SEQUENCE
                 && token.getCompanion() == SEMICOLON_COMPANION);
      }
    }

    long diffTime = System.currentTimeMillis() - start;
    //    System.out.println("Finished after " + diffTime + " milliseconds");
    //    System.out.println("Found " + stmtCount + " import statements.");
  }

  
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
  public TokenizerProperty isSequenceCommentOrString(int startingAtPos, int maxChars) throws TokenizerException {
    switch (_tokenizer.getCharUnchecked(startingAtPos)) {
    case '{':
      return BRACE_OPEN;
    case '}':
      return BRACE_CLOSE;
    case '"':
      return STRING_STRING;
    case '\'':
      return CHAR_STRING;
    case '.':
      return COLON;
    case ';':
      return SEMICOLON;
    case '*':
      return STAR;
    case '/':
      if (maxChars > 1) {
        switch (_tokenizer.getCharUnchecked(startingAtPos + 1)) {
        case '/':
          return LINE_COMMENT;
        case '*':
          return BLOCK_COMMENT;
        default:
          return null;
        }
      } else {
        return null;
      }
    default:
      return null;
    }
  }  
  
  
  /**
   * This method checks if the character sequence starting at a given position
   * with a given length is a keyword. If so, it returns the keyword description
   * as {@link TokenizerProperty} object.
   * If the method needs to build a string from the character sequence it may
   * use {@link #getText} or {@link #getTextUnchecked} to retrieve it.
   *
   * @param   startingAtPos   check at this position
   * @param   length          number of characters in the token to be tested
   * @return  {@link TokenizerProperty} describing the keyword or <code>null</code>
   */
  public TokenizerProperty isKeyword(int startingAtPos, int length) {
    String  token = _tokenizer.getText(startingAtPos, length);

    return (TokenizerProperty)TestJavaTokenizing._keywords.get(token);
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
    return 2;   // being the length of /* and //
  }
  
  
  //---------------------------------------------------------------------------
  // class constants
  //
  private static final Object BRACE_OPEN_COMPANION  = new Object();
  private static final Object BRACE_CLOSE_COMPANION = new Object();
  private static final Object COLON_COMPANION       = new Object();
  private static final Object SEMICOLON_COMPANION   = new Object();
  private static final Object STAR_COMPANION        = new Object();
  
  private static final TokenizerProperty BRACE_OPEN 
    = new TokenizerProperty(Token.SPECIAL_SEQUENCE, new String[] { "{" }, BRACE_OPEN_COMPANION);
  
  private static final TokenizerProperty BRACE_CLOSE 
    = new TokenizerProperty(Token.SPECIAL_SEQUENCE, new String[] { "}" }, BRACE_CLOSE_COMPANION);
    
  private static final TokenizerProperty COLON 
    = new TokenizerProperty(Token.SPECIAL_SEQUENCE, new String[] { "." }, COLON_COMPANION);
    
  private static final TokenizerProperty SEMICOLON 
    = new TokenizerProperty(Token.SPECIAL_SEQUENCE, new String[] { ";" }, SEMICOLON_COMPANION);
    
  private static final TokenizerProperty STAR
    = new TokenizerProperty(Token.SPECIAL_SEQUENCE, new String[] { "*" }, STAR_COMPANION);
    
  private static final TokenizerProperty STRING_STRING 
    = new TokenizerProperty(Token.STRING, new String[] { "\"", "\"", "\\" } );
    
  private static final TokenizerProperty CHAR_STRING 
    = new TokenizerProperty(Token.STRING, new String[] { "'", "'", "\\" } );

  private static final TokenizerProperty LINE_COMMENT 
    = new TokenizerProperty(Token.LINE_COMMENT, new String[] { "//" } );

  private static final TokenizerProperty BLOCK_COMMENT 
    = new TokenizerProperty(Token.BLOCK_COMMENT, new String[] { "/*", "*/" } );

    
  //---------------------------------------------------------------------------
  // Members
  //
  private InputStreamReader _reader    = null;
  private String            _path      = null;
  private PluginTokenizer   _tokenizer = null;

  //---------------------------------------------------------------------------
  // class members
  //
  private static HashMap    _keywords  = new HashMap();
}

