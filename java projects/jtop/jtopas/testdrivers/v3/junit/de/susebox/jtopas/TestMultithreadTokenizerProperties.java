/*
 * TestMultithreadTokenizerProperties.java: JUnit test for TokenizerProperties implementations
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
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

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
// Class TestMultithreadTokenizerProperties
//

/**<p>
 * This class tests the implementations of {@link TokenizerProperties}. 
 *</p>
 *
 * @see     TokenizerProperties
 * @see     TokenizerPropertyListener 
 * @author  Heiko Blau
 */
public class TestMultithreadTokenizerProperties 
  extends TestCase 
  implements TokenizerPropertyListener, DataProvider {
  
  //---------------------------------------------------------------------------
  // main method
  //
  
  /**
   * call this method to invoke the tests.
   */
  public static void main(String[] args) {
    String[]   tests = { TestMultithreadTokenizerProperties.class.getName() };
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
    
    suite.addTest(new TestMultithreadTokenizerProperties("testParallelModification"));
    return suite;
  }
  
  
  //---------------------------------------------------------------------------
  // Constructor
  //
  
  /**
   * Default constructor. Standard input {@link java.lang.System#in} is used
   * to construct the input stream reader.
   */  
  public TestMultithreadTokenizerProperties(String test) {
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
    _eventRecord = new HashMap(101);
    // _eventRecord = new HashMap(101);
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
   * Testing generic methods.
   */
  public void testParallelModification() throws Throwable {
      //    System.out.println("Number of threads: " + _numberOfThreads);
      //    System.out.println("Test duration (s): " + _duration);

    Random          random  = new Random();
    StringBuffer[]  active  = new StringBuffer[_numberOfThreads];
    String[]        last    = new String[_numberOfThreads];
    Runner[]        runner  = new Runner[_numberOfThreads];
    Thread[]        thread  = new Thread[_numberOfThreads];
    long            start   = System.currentTimeMillis();

    // Create resources
    for (int index = 0; index < _numberOfThreads; ++index) {
      active[index] = new StringBuffer("0");
      runner[index] = new Runner(this, random.nextInt(_testProperties.length - 1), 2, active[index]);
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
	      //            wait(1000);
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
      //     Thread.sleep(5000);
      //      Thread.sleep(2000);

    } finally {
      for (int index = 0; index < _numberOfThreads; ++index) {
        thread[index] = null;
      }
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
    TokenizerProperty prop = event.getProperty();
    String            name = Thread.currentThread().getName();

    switch (event.getType()) {
    case TokenizerPropertyEvent.PROPERTY_MODIFIED:
      if ( ! _eventRecord.containsKey(prop)) { 
	  //        System.out.println(name + ": modified not existing: " + prop);
        if (_eventRecord.put(prop, prop) != null) {
	    //          System.out.println("   but existed unexpected a moment later!");
        }
      } else {
	  //        System.out.println(name + ": modified existing: " + prop);
      }
      // assertTrue("Modified non-existing property " + prop, _eventRecord.containsKey(prop));
      assertTrue("Property does not exist: " + prop, _properties.propertyExists(prop));
      break;

    case TokenizerPropertyEvent.PROPERTY_ADDED:
      if (_eventRecord.put(prop, prop) != null) {
	  //        System.out.println(name + ": added again: " + prop);
      }
      // assertTrue("Added existing property " + prop, _eventRecord.put(prop, prop) == null);
      assertTrue("Property does not exist: " + prop, _properties.propertyExists(prop));
      break;

    case TokenizerPropertyEvent.PROPERTY_REMOVED:
      if (_eventRecord.remove(prop) == null) {
	  //        System.out.println(name + ": never added: " + prop);
      }
      // assertTrue("Removed not added property " + prop, _eventRecord.remove(prop) != null);
      assertTrue("Property still exists: " + prop, ! _properties.propertyExists(prop));
      break;
    }
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
    protected static int _numberOfThreads = 9 /*29*/;
    protected static int _duration        = 2 /*60*/;
  
  protected static final TokenizerProperty[] _testProperties = {
    new TokenizerProperty(Token.KEYWORD, new String[] { "k1" } ),
    new TokenizerProperty(Token.STRING, new String[] { "\"", "\"", "\\" } ),
    new TokenizerProperty(Token.SPECIAL_SEQUENCE, new String[] { ">>>" }, new Object() ),
    new TokenizerProperty(Token.SPECIAL_SEQUENCE, new String[] { ">>" } ),
    new TokenizerProperty(Token.KEYWORD, new String[] { "k2" }, new Object(), TokenizerProperties.F_NO_CASE),
    new TokenizerProperty(Token.KEYWORD, new String[] { "k3" }, new Object(), TokenizerProperties.F_NO_CASE),
    new TokenizerProperty(Token.LINE_COMMENT, new String[] { "//" } ),
    new TokenizerProperty(Token.SPECIAL_SEQUENCE, new String[] { "<<" }, new Object() ),
    new TokenizerProperty(Token.BLOCK_COMMENT, new String[] { "/**", "*/" }, new Object()),
    new TokenizerProperty(Token.SPECIAL_SEQUENCE, new String[] { "+=" } ),
    new TokenizerProperty(Token.KEYWORD, new String[] { "H1" }, null, TokenizerProperties.F_NO_CASE),
    new TokenizerProperty(Token.KEYWORD, new String[] { "H2" }, null, TokenizerProperties.F_NO_CASE),
    new TokenizerProperty(Token.KEYWORD, new String[] { "H3" }, null, TokenizerProperties.F_NO_CASE),
    new TokenizerProperty(Token.SPECIAL_SEQUENCE, new String[] { "-=" }, new Object() ),
    new TokenizerProperty(Token.SPECIAL_SEQUENCE, new String[] { "*=" } ),
    new TokenizerProperty(Token.SPECIAL_SEQUENCE, new String[] { "**=" } ),
    new TokenizerProperty(Token.LINE_COMMENT, new String[] { "#" }, new Object() ),
    new TokenizerProperty(Token.LINE_COMMENT, new String[] { "rem" }, null, TokenizerProperties.F_NO_CASE),
    new TokenizerProperty(Token.BLOCK_COMMENT, new String[] { "/*", "*/" } ),
    new TokenizerProperty(Token.SPECIAL_SEQUENCE, new String[] { "++" } ),
    new TokenizerProperty(Token.SPECIAL_SEQUENCE, new String[] { "+++" }, new Object() ),
    new TokenizerProperty(Token.BLOCK_COMMENT, new String[] { "{", "}" }, new Object()),
    new TokenizerProperty(Token.BLOCK_COMMENT, new String[] { "[startBlockComment]", "[endBlockComment]" }, null, TokenizerProperties.F_NO_CASE),
    new TokenizerProperty(Token.SPECIAL_SEQUENCE, new String[] { "**" } ),
    new TokenizerProperty(Token.BLOCK_COMMENT, new String[] { "<!--", "-->" } ),
    new TokenizerProperty(Token.STRING, new String[] { "'", "'", "\\" } ),
    new TokenizerProperty(Token.SPECIAL_SEQUENCE, new String[] { ":=" } ),
    new TokenizerProperty(Token.PATTERN, new String[] { "[+\\-]?[0-9]+\\.?[0-9]*" }, new Object()),
    new TokenizerProperty(Token.KEYWORD, new String[] { "if" }, new Object() ),
    new TokenizerProperty(Token.KEYWORD, new String[] { "then" }, new Object() ),
    new TokenizerProperty(Token.PATTERN, new String[] { "[A-Z_][A-Z0-9_]*" }, new Object(), TokenizerProperties.F_NO_CASE),
    new TokenizerProperty(Token.KEYWORD, new String[] { "while" }, new Object() ),
    new TokenizerProperty(Token.SPECIAL_SEQUENCE, new String[] { "!=" }, new Object()),
    new TokenizerProperty(Token.KEYWORD, new String[] { "loop" }, new Object() ),
    new TokenizerProperty(Token.KEYWORD, new String[] { "class" }, new Object() ),
    new TokenizerProperty(Token.KEYWORD, new String[] { "interface" }, new Object() ),
    new TokenizerProperty(Token.SPECIAL_SEQUENCE, new String[] { "<>" }, new Object()),
    new TokenizerProperty(Token.LINE_COMMENT, new String[] { "--" }, new Object() ),
    new TokenizerProperty(Token.KEYWORD, new String[] { "synchronized" }, new Object() ),
    new TokenizerProperty(Token.KEYWORD, new String[] { "final" } ),
    new TokenizerProperty(Token.KEYWORD, new String[] { "implements" } ),
    new TokenizerProperty(Token.KEYWORD, new String[] { "int" } ),
    new TokenizerProperty(Token.STRING, new String[] { "''", "''", "''" }, new Object() ),
    new TokenizerProperty(Token.KEYWORD, new String[] { "boolean" } ),
    new TokenizerProperty(Token.KEYWORD, new String[] { "void" } ),
    new TokenizerProperty(Token.KEYWORD, new String[] { "do" } ),
    new TokenizerProperty(Token.KEYWORD, new String[] { "HREF" }, null, TokenizerProperties.F_NO_CASE),
    new TokenizerProperty(Token.KEYWORD, new String[] { "FONT" }, null, TokenizerProperties.F_NO_CASE),
    new TokenizerProperty(Token.SPECIAL_SEQUENCE, new String[] { "." } ),
    new TokenizerProperty(Token.SPECIAL_SEQUENCE, new String[] { "{" } ),
    new TokenizerProperty(Token.SPECIAL_SEQUENCE, new String[] { "}" } ),
    new TokenizerProperty(Token.KEYWORD, new String[] { "import" } ),
    new TokenizerProperty(Token.KEYWORD, new String[] { "package" } ),
    new TokenizerProperty(Token.KEYWORD, new String[] { "static" } ),
    new TokenizerProperty(Token.KEYWORD, new String[] { "try" }, new Object() ),
    new TokenizerProperty(Token.KEYWORD, new String[] { "catch" }, new Object() ),
    new TokenizerProperty(Token.SPECIAL_SEQUENCE, new String[] { "/" }  ),
    new TokenizerProperty(Token.SPECIAL_SEQUENCE, new String[] { "/=" } ),
    new TokenizerProperty(Token.KEYWORD, new String[] { "table" }, null, TokenizerProperties.F_NO_CASE),
    new TokenizerProperty(Token.KEYWORD, new String[] { "span" }, null, TokenizerProperties.F_NO_CASE),
    new TokenizerProperty(Token.SPECIAL_SEQUENCE, new String[] { "[" } ),
    new TokenizerProperty(Token.KEYWORD, new String[] { "layer" }, null, TokenizerProperties.F_NO_CASE),
    new TokenizerProperty(Token.SPECIAL_SEQUENCE, new String[] { "]" } ),
    new TokenizerProperty(Token.KEYWORD, new String[] { "STYLE" }, null, TokenizerProperties.F_NO_CASE),
    new TokenizerProperty(Token.KEYWORD, new String[] { "image" }, null, TokenizerProperties.F_NO_CASE),
    new TokenizerProperty(Token.SPECIAL_SEQUENCE, new String[] { "+" }  )
  };
  

  //---------------------------------------------------------------------------
  // Members
  //
  protected Map                     _eventRecord    = null;
  protected TokenizerProperties     _properties     = null;
  private   char[]                  _currData       = new char[8192];
  private   int                     _currStartPos   = 0;
  private   int                     _currDataLength = 0;


  //---------------------------------------------------------------------------
  // inner classes
  //

  /**
   * Thread for TokenizerProperties manipulation
   */
  class Runner implements Runnable {

    /**
     * Constructor taking the {@link TokenizerProperties} instance to work, an array
     * of {@link TokenizerProperty} objects to add and remove from the <code>TokenizerProperties</code>
     * object and a step interval for the entries in the property array.
     */
    public Runner(TestMultithreadTokenizerProperties parent, int start, int step, StringBuffer activity) {
      _parent   = parent;
      _start    = start;
      _activity = activity;
      if ((_step = step) < 1) {
        _step = 1;
      }
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
      Thread  thread  = Thread.currentThread();
      String  name    = thread.getName();
      int     counter = _start;

      try {
        while (Thread.currentThread() == thread && ! _stop) {
          synchronized(this) {
            long              start = System.currentTimeMillis();
            int               index = counter % _parent._testProperties.length;
            TokenizerProperty prop  = _parent._testProperties[index];

            // add or remove a property
            // System.out.println(name + ": checking " + prop);
            if (_parent._properties.propertyExists(prop)) {
              _parent._properties.removeProperty(prop);
            } else {
              _parent._properties.addProperty(prop);
            }

            // check duration
            long duration = System.currentTimeMillis() - start;
            if (duration > 5000) {
              _parent.assertTrue(name + ": waited too long: " + duration, false);
              break;
            }

            // increase counter
            counter += _step;

            // signal activity
            long value = Long.parseLong(_activity.toString());
            _activity.setLength(0);
            _activity.append(value + 1);

            // pause a little bit
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


    //---------------------------------------------------------------------------
    // Members
    //
    private TestMultithreadTokenizerProperties  _parent     = null;
    private int                                 _start      = 0;
    private int                                 _step       = 0;
    private boolean                             _stop       = false;
    private StringBuffer                        _activity   = null;
  }
}

