/*
 * TestMultithreadTokenizer.java: JUnit test for Tokenizer implementations
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
import java.util.Random;
import java.util.ArrayList;

import java.io.Reader;
import java.io.StringReader;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.framework.Assert;
import junit.swingui.TestRunner;

import de.susebox.java.lang.ExtRuntimeException;

//-----------------------------------------------------------------------------
// Class TestMultithreadTokenizer
//

/**<p>
 * This class tests {@link Tokenizer} implementations in a multithreaded environment.
 *</p>
 *
 * @see     Tokenizer
 * @author  Heiko Blau
 */
public class TestMultithreadTokenizer extends TestCase {
  
  //---------------------------------------------------------------------------
  // main method
  //
  
  /**
   * call this method to invoke the tests.
   */
  public static void main(String[] args) {
    String[]   tests = { TestMultithreadTokenizer.class.getName() };
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
    
    suite.addTest(new TestMultithreadTokenizer("testParallelParsing"));
    return suite;
  }
  
  
  //---------------------------------------------------------------------------
  // Constructor
  //
  
  /**
   * Default constructor. Standard input {@link java.lang.System#in} is used
   * to construct the input stream reader.
   */  
  public TestMultithreadTokenizer(String test) {
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
    for (int index = 0; index < _testProperties.length; ++index) {
      _properties.addProperty(_testProperties[index]);
    }
	}

  
	/**
	 * Tears down the fixture, for example, close a network connection.
	 * This method is called after a test is executed.
	 */
	protected void tearDown() throws Exception {
    _properties = null;
  }

  
  //---------------------------------------------------------------------------
  // test cases
  //

  /**
   * Testing generic methods.
   */
  public void testParallelParsing() throws Throwable {
    Random          random  = new Random();
    StringBuffer[]  active  = new StringBuffer[_numberOfThreads];
    String[]        last    = new String[_numberOfThreads];
    Runner[]        runner  = new Runner[_numberOfThreads];
    Thread[]        thread  = new Thread[_numberOfThreads];
    long            start   = System.currentTimeMillis();

    // Create TokenizerProperties
    _properties = new StandardTokenizerProperties(TokenizerProperties.F_RETURN_WHITESPACES);
    
    for (int index = 0; index < _testProperties.length; ++index) {
      _properties.addProperty(_testProperties[index]);
    }
    
    // Create resources
    for (int index = 0; index < _numberOfThreads; ++index) {
      active[index] = new StringBuffer("0");
      runner[index] = new Runner(this, random.nextInt(_testTexts.length), active[index]);
      thread[index] = new Thread(runner[index]);
    }
      
    // start threads and check actions 
    try {
      for (int index = 0; index < _numberOfThreads; ++index) {
        thread[index].start();
      }

      while (System.currentTimeMillis() - start < _duration * 1000) {
        synchronized(this) {
          try {
	      //            wait(2000);
	      wait(200);
          } catch (InterruptedException ex) {}
        }

        // check activity
        for (int index = 0; index < _numberOfThreads; ++index) {
          assertTrue("No activity at runner " + index + ". Last value: " + active[index], 
                    ! active[index].toString().equals(last[index]));
          last[index] = active[index].toString();
        }
      }
      
      // stop the threads
      for (int index = 0; index < _numberOfThreads; ++index) {
        runner[index].stop();
      }
      //      Thread.sleep(5000);
      //      Thread.sleep(200);

    } finally {
      for (int index = 0; index < _numberOfThreads; ++index) {
        thread[index] = null;
      }
    }
  }
  
 
  //---------------------------------------------------------------------------
  // class members
  //
    private static int _numberOfThreads = 9 /*29*/;
    private static int _duration        = 2 /*60*/;

  /**
   * Table with properties
   */
  private static final TokenizerProperty[] _testProperties = {
    new TokenizerProperty(Token.STRING, new String[] { "\"", "\"", "\\" }, null, TokenizerProperties.F_CASE ),
    new TokenizerProperty(Token.STRING, new String[] { "'", "'", "\\" }, null, TokenizerProperties.F_CASE ),
    new TokenizerProperty(Token.LINE_COMMENT, new String[] { "//" }, null, TokenizerProperties.F_CASE ),
    new TokenizerProperty(Token.BLOCK_COMMENT, new String[] { "/*", "*/" }, null, TokenizerProperties.F_CASE ),
    new TokenizerProperty(Token.BLOCK_COMMENT, new String[] { "/**", "*/" }, null, TokenizerProperties.F_CASE ),
    new TokenizerProperty(Token.KEYWORD, new String[] { "if" }, null, TokenizerProperties.F_CASE ),
    new TokenizerProperty(Token.KEYWORD, new String[] { "else" }, null, TokenizerProperties.F_CASE ),
    new TokenizerProperty(Token.KEYWORD, new String[] { "return" }, null, TokenizerProperties.F_CASE ),
    new TokenizerProperty(Token.KEYWORD, new String[] { "native" }, null, TokenizerProperties.F_CASE ),
    new TokenizerProperty(Token.KEYWORD, new String[] { "for" }, null, TokenizerProperties.F_CASE ),
    new TokenizerProperty(Token.KEYWORD, new String[] { "while" }, null, TokenizerProperties.F_CASE ),
    new TokenizerProperty(Token.KEYWORD, new String[] { "do" }, null, TokenizerProperties.F_CASE ),
    new TokenizerProperty(Token.KEYWORD, new String[] { "switch" }, null, TokenizerProperties.F_CASE ),
    new TokenizerProperty(Token.KEYWORD, new String[] { "case" }, null, TokenizerProperties.F_CASE ),
    new TokenizerProperty(Token.KEYWORD, new String[] { "default" }, null, TokenizerProperties.F_CASE ),
    new TokenizerProperty(Token.KEYWORD, new String[] { "break" }, null, TokenizerProperties.F_CASE ),
    new TokenizerProperty(Token.KEYWORD, new String[] { "class" }, null, TokenizerProperties.F_CASE ),
    new TokenizerProperty(Token.KEYWORD, new String[] { "interface" }, null, TokenizerProperties.F_CASE ),
    new TokenizerProperty(Token.KEYWORD, new String[] { "synchronized" }, null, TokenizerProperties.F_CASE),
    new TokenizerProperty(Token.KEYWORD, new String[] { "public" }, null, TokenizerProperties.F_CASE ),
    new TokenizerProperty(Token.KEYWORD, new String[] { "protected" }, null, TokenizerProperties.F_CASE ),
    new TokenizerProperty(Token.KEYWORD, new String[] { "private" }, null, TokenizerProperties.F_CASE ),
    new TokenizerProperty(Token.KEYWORD, new String[] { "final" }, null, TokenizerProperties.F_CASE ),
    new TokenizerProperty(Token.KEYWORD, new String[] { "static" }, null, TokenizerProperties.F_CASE ),
    new TokenizerProperty(Token.KEYWORD, new String[] { "implements" }, null, TokenizerProperties.F_CASE ),
    new TokenizerProperty(Token.KEYWORD, new String[] { "extends" }, null, TokenizerProperties.F_CASE ),
    new TokenizerProperty(Token.KEYWORD, new String[] { "byte" }, null, TokenizerProperties.F_CASE ),
    new TokenizerProperty(Token.KEYWORD, new String[] { "char" }, null, TokenizerProperties.F_CASE ),
    new TokenizerProperty(Token.KEYWORD, new String[] { "int" }, null, TokenizerProperties.F_CASE ),
    new TokenizerProperty(Token.KEYWORD, new String[] { "long" }, null, TokenizerProperties.F_CASE ),
    new TokenizerProperty(Token.KEYWORD, new String[] { "double" }, null, TokenizerProperties.F_CASE ),
    new TokenizerProperty(Token.KEYWORD, new String[] { "String" }, null, TokenizerProperties.F_CASE ),
    new TokenizerProperty(Token.KEYWORD, new String[] { "boolean" }, null, TokenizerProperties.F_CASE ),
    new TokenizerProperty(Token.KEYWORD, new String[] { "void" }, null, TokenizerProperties.F_CASE ),
    new TokenizerProperty(Token.KEYWORD, new String[] { "throw" }, null, TokenizerProperties.F_CASE ),
    new TokenizerProperty(Token.KEYWORD, new String[] { "throws" }, null, TokenizerProperties.F_CASE ),
    new TokenizerProperty(Token.KEYWORD, new String[] { "new" }, null, TokenizerProperties.F_CASE ),
    new TokenizerProperty(Token.KEYWORD, new String[] { "assert" }, null, TokenizerProperties.F_CASE ),
    new TokenizerProperty(Token.KEYWORD, new String[] { "try" }, null, TokenizerProperties.F_CASE ),
    new TokenizerProperty(Token.KEYWORD, new String[] { "catch" }, null, TokenizerProperties.F_CASE ),
    new TokenizerProperty(Token.KEYWORD, new String[] { "finally" }, null, TokenizerProperties.F_CASE ),
    new TokenizerProperty(Token.KEYWORD, new String[] { "import" }, null, TokenizerProperties.F_CASE ),
    new TokenizerProperty(Token.KEYWORD, new String[] { "package" }, null, TokenizerProperties.F_CASE ),
    new TokenizerProperty(Token.KEYWORD, new String[] { "this" }, null, TokenizerProperties.F_CASE ),
    new TokenizerProperty(Token.KEYWORD, new String[] { "super" }, null, TokenizerProperties.F_CASE ),
    new TokenizerProperty(Token.KEYWORD, new String[] { "null" }, null, TokenizerProperties.F_CASE ),
    new TokenizerProperty(Token.SPECIAL_SEQUENCE, new String[] { "," }, null, TokenizerProperties.F_CASE ),
    new TokenizerProperty(Token.SPECIAL_SEQUENCE, new String[] { ";" }, null, TokenizerProperties.F_CASE ),
    new TokenizerProperty(Token.SPECIAL_SEQUENCE, new String[] { "=" }, null, TokenizerProperties.F_CASE ),
    new TokenizerProperty(Token.SPECIAL_SEQUENCE, new String[] { "==" }, null, TokenizerProperties.F_CASE ),
    new TokenizerProperty(Token.SPECIAL_SEQUENCE, new String[] { "!=" }, null, TokenizerProperties.F_CASE ),
    new TokenizerProperty(Token.SPECIAL_SEQUENCE, new String[] { ">=" }, null, TokenizerProperties.F_CASE ),
    new TokenizerProperty(Token.SPECIAL_SEQUENCE, new String[] { ">>=" }, null, TokenizerProperties.F_CASE ),
    new TokenizerProperty(Token.SPECIAL_SEQUENCE, new String[] { "<=" }, null, TokenizerProperties.F_CASE ),
    new TokenizerProperty(Token.SPECIAL_SEQUENCE, new String[] { "<<=" }, null, TokenizerProperties.F_CASE ),
    new TokenizerProperty(Token.SPECIAL_SEQUENCE, new String[] { ">" }, null, TokenizerProperties.F_CASE ),
    new TokenizerProperty(Token.SPECIAL_SEQUENCE, new String[] { "<" }, null, TokenizerProperties.F_CASE ),
    new TokenizerProperty(Token.SPECIAL_SEQUENCE, new String[] { "+=" }, null, TokenizerProperties.F_CASE ),
    new TokenizerProperty(Token.SPECIAL_SEQUENCE, new String[] { "-=" }, null, TokenizerProperties.F_CASE ),
    new TokenizerProperty(Token.SPECIAL_SEQUENCE, new String[] { "*=" }, null, TokenizerProperties.F_CASE ),
    new TokenizerProperty(Token.SPECIAL_SEQUENCE, new String[] { "/=" }, null, TokenizerProperties.F_CASE ),
    new TokenizerProperty(Token.SPECIAL_SEQUENCE, new String[] { "&=" }, null, TokenizerProperties.F_CASE ),
    new TokenizerProperty(Token.SPECIAL_SEQUENCE, new String[] { "|=" }, null, TokenizerProperties.F_CASE ),
    new TokenizerProperty(Token.SPECIAL_SEQUENCE, new String[] { "<<" }, null, TokenizerProperties.F_CASE ),
    new TokenizerProperty(Token.SPECIAL_SEQUENCE, new String[] { ">>" }, null, TokenizerProperties.F_CASE ),
    new TokenizerProperty(Token.SPECIAL_SEQUENCE, new String[] { ">>>" }, null, TokenizerProperties.F_CASE ),
    new TokenizerProperty(Token.SPECIAL_SEQUENCE, new String[] { "++" }, null, TokenizerProperties.F_CASE ),
    new TokenizerProperty(Token.SPECIAL_SEQUENCE, new String[] { "--" }, null, TokenizerProperties.F_CASE ),
    new TokenizerProperty(Token.SPECIAL_SEQUENCE, new String[] { "~" }, null, TokenizerProperties.F_CASE ),
    new TokenizerProperty(Token.SPECIAL_SEQUENCE, new String[] { "*" }, null, TokenizerProperties.F_CASE ),
    new TokenizerProperty(Token.SPECIAL_SEQUENCE, new String[] { "/" }, null, TokenizerProperties.F_CASE ),
    new TokenizerProperty(Token.SPECIAL_SEQUENCE, new String[] { "%" }, null, TokenizerProperties.F_CASE ),
    new TokenizerProperty(Token.SPECIAL_SEQUENCE, new String[] { "^" }, null, TokenizerProperties.F_CASE ),
    new TokenizerProperty(Token.SPECIAL_SEQUENCE, new String[] { "+" }, null, TokenizerProperties.F_CASE ),
    new TokenizerProperty(Token.SPECIAL_SEQUENCE, new String[] { "-" }, null, TokenizerProperties.F_CASE ),
    new TokenizerProperty(Token.SPECIAL_SEQUENCE, new String[] { "." }, null, TokenizerProperties.F_CASE ),
    new TokenizerProperty(Token.SPECIAL_SEQUENCE, new String[] { "(" }, null, TokenizerProperties.F_CASE ),
    new TokenizerProperty(Token.SPECIAL_SEQUENCE, new String[] { ")" }, null, TokenizerProperties.F_CASE ),
    new TokenizerProperty(Token.SPECIAL_SEQUENCE, new String[] { "{" }, null, TokenizerProperties.F_CASE ),
    new TokenizerProperty(Token.SPECIAL_SEQUENCE, new String[] { "}" }, null, TokenizerProperties.F_CASE ),
    new TokenizerProperty(Token.SPECIAL_SEQUENCE, new String[] { "[" }, null, TokenizerProperties.F_CASE ),
    new TokenizerProperty(Token.SPECIAL_SEQUENCE, new String[] { "]" }, null, TokenizerProperties.F_CASE ),
    new TokenizerProperty(Token.SPECIAL_SEQUENCE, new String[] { "?" }, null, TokenizerProperties.F_CASE ),
    new TokenizerProperty(Token.SPECIAL_SEQUENCE, new String[] { ":" }, null, TokenizerProperties.F_CASE )
  };
  
  /**
   * Table with texts to parse
   */
  private static final String[] _testTexts = {
      "import junit.framework.TestCase;\n"
    + "\n"
    + "public class MyTest extends TestCase implements TestListener {\n"
    +   "/** default constructor */\n"
    +   "public MyTest() {\n"
    +     "this(null);\n"
    +   "}\n"
    +   "\n"
    +   "/**\n"
    +   " * constructor taking name\n"
    +   " *\n"
    +   " * @param name the name\n"
    +   " */\n"
    +   "public MyTest(String name) {\n"
    +     "setName(name);\n"
    +   "}\n"
    +   "\n"
    +   "/**\n"
    +   " * Getting the name\n"
    +   " *\n"
    +   " * @return the name\n"
    +   " */\n"
    +   "public String getName() {\n"
    +     "return _myName;\n"
    +   "}\n"
    +   "\n"
    +   "/**\n"
    +   " * Setting a new name\n"
    +   " *\n"
    +   " * @param name the new name\n"
    +   " * @return the old name or <code>null</code>\n"
    +   " */\n"
    +   "public String setName(String name) {\n"
    +     "// setting _myName safely to a non-null value\n"
    +     "_myName = (name != null) ? name : \"\";\n"
    +   "}\n"
    +   "\n"
    +   "// Members\n"
    +   "private String _myName = null;\n"
    + "}\n",
    
      // second text
      "// package declaration\r\n"
    + "package my.domain.toppackage.subpackage;\r\n"
    + "\r\n"
    + "// imports\r\n"
    + "import java.applet.Applet;\r\n"
    + "import java.util.ArrayList;\r\n"
    + "import java.io.InputStream;\r\n"
    + "import java.io.InputStreamReader;\r\n"
    + "import java.io.FileInputStream;\r\n"
    + "import java.io.StringReader;\r\n"
    + "import java.io.IOException;\r\n"
    + "\n"
    + "/**\r\n"
    + " * A class for parsing only :-)\r\n"
    + " */\r\n"
    + "public class MyRunner extends Applet implements Runnable {\r\n"
    +   "/** default constructor */\r\n"
    +   "public MyRunner() {\r\n"
    +     "super();\r\n"
    +   "}\r\n"
    +   "\r\n"
    +   "/**\r\n"
    +   " * constructor taking name\r\n"
    +   " *\r\n"
    +   " * @param name the name\r\n"
    +   " */\r\n"
    +   "public MyRunner(String name) {\r\n"
    +     "super(name);\r\n"
    +   "}\r\n"
    +   "\r\n"
    +   "/**\r\n"
    +   " * Getting the name\r\n"
    +   " *\r\n"
    +   " * @return the name\r\n"
    +   " */\r\n"
    +   "public String getName() {\r\n"
    +     "return super.getName();\r\n"
    +   "}\r\n"
    +   "\r\n"
    +   "/**\n"
    +   " * Run method a defined in {@link java.lang.Runnable}.\r\n"
    +   " */\r\n"
    +   "public void run() throws Throwable {\r\n"
    +     "Thread thread = Thread.currentThread();\r\n"
    +     "long   count = 0;\r\n"
    +     "\r\n"
    +     "while (Thread.currentThread() == this) {\r\n"
    +       "count++;\r\n"
    +       "_shifter >>= 1;\r\n"
    +       "synchronized(this){\r\n"
    +         "try {\r\n"
    +           "wait((count % 100) + 10);\r\n"
    +         "} catch (Exception ex) {\r\n"
    +           "break;\r\n"
    +         "} finally {\r\n"
    +           "_shifter = 0;\r\n"
    +         "}\r\n"
    +       "}\r\n"
    +     "}\r\n"
    +   "}\r\n"
    +   "\r\n"
    +   "// Members\r\n"
    +   "private long _shifter = 0;\r\n"
    + "}"
  };
  
  /**
   * The expected tokenizing results
   */
  protected static final int _expectedResults[][] = {
    {
      Token.KEYWORD, Token.WHITESPACE, Token.NORMAL, Token.SPECIAL_SEQUENCE, Token.NORMAL, Token.SPECIAL_SEQUENCE, Token.NORMAL, Token.SPECIAL_SEQUENCE, Token.WHITESPACE,
      Token.KEYWORD, Token.WHITESPACE, Token.KEYWORD, Token.WHITESPACE, Token.NORMAL, Token.WHITESPACE, Token.KEYWORD, Token.WHITESPACE, Token.NORMAL, Token.WHITESPACE, Token.KEYWORD, Token.WHITESPACE, Token.NORMAL, Token.WHITESPACE, Token.SPECIAL_SEQUENCE, Token.WHITESPACE,
      Token.BLOCK_COMMENT, Token.WHITESPACE,
      Token.KEYWORD, Token.WHITESPACE, Token.NORMAL, Token.SPECIAL_SEQUENCE, Token.SPECIAL_SEQUENCE, Token.WHITESPACE, Token.SPECIAL_SEQUENCE, Token.WHITESPACE,
      Token.KEYWORD, Token.SPECIAL_SEQUENCE, Token.KEYWORD, Token.SPECIAL_SEQUENCE, Token.SPECIAL_SEQUENCE, Token.WHITESPACE,
      Token.SPECIAL_SEQUENCE, Token.WHITESPACE,
      Token.BLOCK_COMMENT, Token.WHITESPACE,
      Token.KEYWORD, Token.WHITESPACE, Token.NORMAL, Token.SPECIAL_SEQUENCE, Token.KEYWORD, Token.WHITESPACE, Token.NORMAL, Token.SPECIAL_SEQUENCE, Token.WHITESPACE, Token.SPECIAL_SEQUENCE, Token.WHITESPACE,
      Token.NORMAL, Token.SPECIAL_SEQUENCE, Token.NORMAL, Token.SPECIAL_SEQUENCE, Token.SPECIAL_SEQUENCE, Token.WHITESPACE,
      Token.SPECIAL_SEQUENCE, Token.WHITESPACE,
      Token.BLOCK_COMMENT, Token.WHITESPACE,
      Token.KEYWORD, Token.WHITESPACE, Token.KEYWORD, Token.WHITESPACE, Token.NORMAL, Token.SPECIAL_SEQUENCE, Token.SPECIAL_SEQUENCE, Token.WHITESPACE, Token.SPECIAL_SEQUENCE, Token.WHITESPACE,
      Token.KEYWORD, Token.WHITESPACE, Token.NORMAL, Token.SPECIAL_SEQUENCE, Token.WHITESPACE,
      Token.SPECIAL_SEQUENCE, Token.WHITESPACE,
      Token.BLOCK_COMMENT, Token.WHITESPACE,
      Token.KEYWORD, Token.WHITESPACE, Token.KEYWORD, Token.WHITESPACE, Token.NORMAL, Token.SPECIAL_SEQUENCE, Token.KEYWORD, Token.WHITESPACE, Token.NORMAL, Token.SPECIAL_SEQUENCE, Token.WHITESPACE, Token.SPECIAL_SEQUENCE, Token.WHITESPACE,
      Token.LINE_COMMENT,
      Token.NORMAL, Token.WHITESPACE, Token.SPECIAL_SEQUENCE, Token.WHITESPACE, Token.SPECIAL_SEQUENCE, Token.NORMAL, Token.WHITESPACE, Token.SPECIAL_SEQUENCE, Token.WHITESPACE, Token.KEYWORD, Token.SPECIAL_SEQUENCE, Token.WHITESPACE, Token.SPECIAL_SEQUENCE, Token.WHITESPACE, Token.NORMAL, Token.WHITESPACE, Token.SPECIAL_SEQUENCE, Token.WHITESPACE, Token.STRING, Token.SPECIAL_SEQUENCE, Token.WHITESPACE,
      Token.SPECIAL_SEQUENCE, Token.WHITESPACE,
      Token.LINE_COMMENT,
      Token.KEYWORD, Token.WHITESPACE, Token.KEYWORD, Token.WHITESPACE, Token.NORMAL, Token.WHITESPACE, Token.SPECIAL_SEQUENCE, Token.WHITESPACE, Token.KEYWORD, Token.SPECIAL_SEQUENCE, Token.WHITESPACE,
      Token.SPECIAL_SEQUENCE, Token.WHITESPACE,
      Token.EOF
    },
    {
      Token.LINE_COMMENT,  // 0
      Token.KEYWORD, Token.WHITESPACE, Token.NORMAL, Token.SPECIAL_SEQUENCE, Token.NORMAL, Token.SPECIAL_SEQUENCE, Token.NORMAL, Token.SPECIAL_SEQUENCE, Token.NORMAL, Token.SPECIAL_SEQUENCE, Token.WHITESPACE,
      Token.LINE_COMMENT,  // 12
      Token.KEYWORD, Token.WHITESPACE, Token.NORMAL, Token.SPECIAL_SEQUENCE, Token.NORMAL, Token.SPECIAL_SEQUENCE, Token.NORMAL, Token.SPECIAL_SEQUENCE, Token.WHITESPACE,  // 13
      Token.KEYWORD, Token.WHITESPACE, Token.NORMAL, Token.SPECIAL_SEQUENCE, Token.NORMAL, Token.SPECIAL_SEQUENCE, Token.NORMAL, Token.SPECIAL_SEQUENCE, Token.WHITESPACE,  // 22
      Token.KEYWORD, Token.WHITESPACE, Token.NORMAL, Token.SPECIAL_SEQUENCE, Token.NORMAL, Token.SPECIAL_SEQUENCE, Token.NORMAL, Token.SPECIAL_SEQUENCE, Token.WHITESPACE,  // 31
      Token.KEYWORD, Token.WHITESPACE, Token.NORMAL, Token.SPECIAL_SEQUENCE, Token.NORMAL, Token.SPECIAL_SEQUENCE, Token.NORMAL, Token.SPECIAL_SEQUENCE, Token.WHITESPACE,  // 40
      Token.KEYWORD, Token.WHITESPACE, Token.NORMAL, Token.SPECIAL_SEQUENCE, Token.NORMAL, Token.SPECIAL_SEQUENCE, Token.NORMAL, Token.SPECIAL_SEQUENCE, Token.WHITESPACE,  // 49
      Token.KEYWORD, Token.WHITESPACE, Token.NORMAL, Token.SPECIAL_SEQUENCE, Token.NORMAL, Token.SPECIAL_SEQUENCE, Token.NORMAL, Token.SPECIAL_SEQUENCE, Token.WHITESPACE,  // 58
      Token.KEYWORD, Token.WHITESPACE, Token.NORMAL, Token.SPECIAL_SEQUENCE, Token.NORMAL, Token.SPECIAL_SEQUENCE, Token.NORMAL, Token.SPECIAL_SEQUENCE, Token.WHITESPACE,  // 67
      Token.BLOCK_COMMENT, Token.WHITESPACE, // 76
      Token.KEYWORD, Token.WHITESPACE, Token.KEYWORD, Token.WHITESPACE, Token.NORMAL, Token.WHITESPACE, Token.KEYWORD, Token.WHITESPACE, Token.NORMAL, Token.WHITESPACE, Token.KEYWORD, Token.WHITESPACE, Token.NORMAL, Token.WHITESPACE, Token.SPECIAL_SEQUENCE, Token.WHITESPACE, // 78
      Token.BLOCK_COMMENT, Token.WHITESPACE, // 94
      // "public MyRunner() {\r\n"
      Token.KEYWORD, Token.WHITESPACE, Token.NORMAL, Token.SPECIAL_SEQUENCE, Token.SPECIAL_SEQUENCE, Token.WHITESPACE, Token.SPECIAL_SEQUENCE, Token.WHITESPACE, // 87
      Token.KEYWORD, Token.SPECIAL_SEQUENCE, Token.SPECIAL_SEQUENCE, Token.SPECIAL_SEQUENCE, Token.WHITESPACE,
      Token.SPECIAL_SEQUENCE, Token.WHITESPACE,
      Token.BLOCK_COMMENT, Token.WHITESPACE,
      Token.KEYWORD, Token.WHITESPACE, Token.NORMAL, Token.SPECIAL_SEQUENCE, Token.KEYWORD, Token.WHITESPACE, Token.NORMAL, Token.SPECIAL_SEQUENCE, Token.WHITESPACE, Token.SPECIAL_SEQUENCE, Token.WHITESPACE,
      Token.KEYWORD, Token.SPECIAL_SEQUENCE, Token.NORMAL, Token.SPECIAL_SEQUENCE, Token.SPECIAL_SEQUENCE, Token.WHITESPACE,
      Token.SPECIAL_SEQUENCE, Token.WHITESPACE,
      Token.BLOCK_COMMENT, Token.WHITESPACE,
      Token.KEYWORD, Token.WHITESPACE, Token.KEYWORD, Token.WHITESPACE, Token.NORMAL, Token.SPECIAL_SEQUENCE, Token.SPECIAL_SEQUENCE, Token.WHITESPACE, Token.SPECIAL_SEQUENCE, Token.WHITESPACE,
      Token.KEYWORD, Token.WHITESPACE, Token.KEYWORD, Token.SPECIAL_SEQUENCE, Token.NORMAL, Token.SPECIAL_SEQUENCE, Token.SPECIAL_SEQUENCE, Token.SPECIAL_SEQUENCE, Token.WHITESPACE,
      Token.SPECIAL_SEQUENCE, Token.WHITESPACE,
      Token.BLOCK_COMMENT, Token.WHITESPACE,
      Token.KEYWORD, Token.WHITESPACE, Token.KEYWORD, Token.WHITESPACE, Token.NORMAL, Token.SPECIAL_SEQUENCE, Token.SPECIAL_SEQUENCE, Token.WHITESPACE, Token.KEYWORD, Token.WHITESPACE, Token.NORMAL, Token.WHITESPACE, Token.SPECIAL_SEQUENCE, Token.WHITESPACE,
      Token.NORMAL, Token.WHITESPACE, Token.NORMAL, Token.WHITESPACE, Token.SPECIAL_SEQUENCE, Token.WHITESPACE, Token.NORMAL, Token.SPECIAL_SEQUENCE, Token.NORMAL, Token.SPECIAL_SEQUENCE, Token.SPECIAL_SEQUENCE, Token.SPECIAL_SEQUENCE, Token.WHITESPACE,
      Token.KEYWORD, Token.WHITESPACE, Token.NORMAL, Token.WHITESPACE, Token.SPECIAL_SEQUENCE, Token.WHITESPACE, Token.NORMAL, Token.SPECIAL_SEQUENCE, Token.WHITESPACE,
      Token.KEYWORD, Token.WHITESPACE, Token.SPECIAL_SEQUENCE, Token.NORMAL, Token.SPECIAL_SEQUENCE, Token.NORMAL, Token.SPECIAL_SEQUENCE, Token.SPECIAL_SEQUENCE, Token.WHITESPACE, Token.SPECIAL_SEQUENCE, Token.WHITESPACE, Token.KEYWORD, Token.SPECIAL_SEQUENCE, Token.WHITESPACE, Token.SPECIAL_SEQUENCE, Token.WHITESPACE,
      Token.NORMAL, Token.SPECIAL_SEQUENCE, Token.SPECIAL_SEQUENCE, Token.WHITESPACE,
      Token.NORMAL, Token.WHITESPACE, Token.SPECIAL_SEQUENCE, Token.WHITESPACE, Token.NORMAL, Token.SPECIAL_SEQUENCE, Token.WHITESPACE,
      Token.KEYWORD, Token.SPECIAL_SEQUENCE, Token.KEYWORD, Token.SPECIAL_SEQUENCE, Token.SPECIAL_SEQUENCE, Token.WHITESPACE,
      Token.KEYWORD, Token.WHITESPACE, Token.SPECIAL_SEQUENCE, Token.WHITESPACE,
      Token.NORMAL, Token.SPECIAL_SEQUENCE, Token.SPECIAL_SEQUENCE, Token.NORMAL, Token.WHITESPACE, Token.SPECIAL_SEQUENCE, Token.WHITESPACE, Token.NORMAL, Token.SPECIAL_SEQUENCE, Token.WHITESPACE, Token.SPECIAL_SEQUENCE, Token.WHITESPACE, Token.NORMAL, Token.SPECIAL_SEQUENCE, Token.SPECIAL_SEQUENCE, Token.WHITESPACE,
      Token.SPECIAL_SEQUENCE, Token.WHITESPACE, Token.KEYWORD, Token.WHITESPACE, Token.SPECIAL_SEQUENCE, Token.NORMAL, Token.WHITESPACE, Token.NORMAL, Token.SPECIAL_SEQUENCE, Token.WHITESPACE, Token.SPECIAL_SEQUENCE, Token.WHITESPACE,
      Token.KEYWORD, Token.SPECIAL_SEQUENCE, Token.WHITESPACE,
      Token.SPECIAL_SEQUENCE, Token.WHITESPACE, Token.KEYWORD, Token.WHITESPACE, Token.SPECIAL_SEQUENCE, Token.WHITESPACE,
      Token.NORMAL, Token.WHITESPACE, Token.SPECIAL_SEQUENCE, Token.WHITESPACE, Token.NORMAL, Token.SPECIAL_SEQUENCE, Token.WHITESPACE,
      Token.SPECIAL_SEQUENCE, Token.WHITESPACE,
      Token.SPECIAL_SEQUENCE, Token.WHITESPACE,
      Token.SPECIAL_SEQUENCE, Token.WHITESPACE,
      Token.SPECIAL_SEQUENCE, Token.WHITESPACE,
      Token.LINE_COMMENT,
      Token.KEYWORD, Token.WHITESPACE, Token.KEYWORD, Token.WHITESPACE, Token.NORMAL, Token.WHITESPACE, Token.SPECIAL_SEQUENCE, Token.WHITESPACE, Token.NORMAL, Token.SPECIAL_SEQUENCE, Token.WHITESPACE,
      Token.SPECIAL_SEQUENCE,
      Token.EOF
    }
  };
      

  //---------------------------------------------------------------------------
  // Members
  //
  protected TokenizerProperties _properties = null;

  
  //---------------------------------------------------------------------------
  // inner classes
  //

  /**
   * Thread for TokenizerProperties manipulation
   */
  class Runner implements Runnable {

    /**
     * Constructor
     */
    public Runner(TestMultithreadTokenizer parent, int startIndex, StringBuffer activity) {
      _parent     = parent;
      _tokenizer  = new StandardTokenizer(_parent._properties);
      _start      = startIndex;
      _activity   = activity;
    }

    /** 
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see     java.lang.Thread#run()
     */
    public void run() {
      Thread    thread  = Thread.currentThread();
      String    name    = thread.getName();
      int       counter = _start;
      ArrayList tokens  = new ArrayList(); 

      try {
        while (Thread.currentThread() == thread && ! _stop) {
          long  start     = System.currentTimeMillis();
          int   index     = counter % _parent._testTexts.length;
          int[] expected  = _parent._expectedResults[index];
          
          // Text parsen
          _tokenizer.setSource(new ReaderSource(new StringReader(_parent._testTexts[index])));
          tokens.clear();
          while (_tokenizer.hasMoreToken()) {
            tokens.add(_tokenizer.nextToken());
          }
          
          // Ergebnisse �berpr�fen
          int typeIndex = 0;
          
          while (typeIndex < tokens.size() && typeIndex < expected.length) {
            Token token = (Token)tokens.get(typeIndex);
            int   type  = token.getType();

            _parent.assertTrue("Index " + typeIndex + ": Expected type " + Token.getTypeName(expected[typeIndex]) + ", found " + token, type == expected[typeIndex]);
						typeIndex++;
          }
          _parent.assertTrue("Expected " + expected.length + " token, found " + tokens.size() + ".",
                            expected.length == tokens.size()); 

          // increase counter
          counter++;

          // signal activity
          long value = Long.parseLong(_activity.toString());
          _activity.setLength(0);
          _activity.append(value + 1);

          // pause a little bit
          synchronized(this) {
            try {
              wait(1);
            } catch (InterruptedException ex) {}
          }
        }
      } catch (Throwable t) {
        t.printStackTrace();
      }
      //      System.out.println(name + ": exiting. Activity: " + _activity);
    }

    /**
     * Signal the thread to stop
     */
    public void stop() {
      synchronized(this) {
        _stop = true;
      }
    }


    //-------------------------------------------------------------------------
    // Members
    //
    private TestMultithreadTokenizer  _parent     = null;
    private Tokenizer                 _tokenizer  = null;
    private int                       _start      = 0;
    private boolean                   _stop       = false;
    private StringBuffer              _activity   = null;
  }
}

