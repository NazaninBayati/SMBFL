/*
 * TestExceptions.java: JUnit test for ExceptionList implementations
 *
 * Copyright (C) 2002 Heiko Blau
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

package de.susebox;

//-----------------------------------------------------------------------------
// Imports
//
import java.lang.reflect.Constructor;
import java.text.MessageFormat;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.framework.Assert;
import junit.swingui.TestRunner;

import de.susebox.java.lang.ExceptionList;


//-----------------------------------------------------------------------------
// Class TestExceptions
//

/**<p>
 * This class is a generic test for implementations of the {@link ExceptionList}
 * interface. Instead of testing each implementation by its own, the common
 * pattern of all these exceptions is used to define tests on all of them.
 *</p>
 *
 * @see     ExceptionList
 * @author  Heiko Blau
 */
public class TestExceptions extends TestCase {
  
  //---------------------------------------------------------------------------
  // properties
  //
  
  /**
   * The class paths of the exceptions that are tested with this unit test.
   */
  public static final String[] EXCEPTIONS_TO_TEST = {
    "de.susebox.java.lang.ExtRuntimeException",
    "de.susebox.java.lang.ExtIndexOutOfBoundsException",
    "de.susebox.java.io.ExtIOException",
    "de.susebox.java.util.TokenizerException"
  };

  
  //---------------------------------------------------------------------------
  // main method
  //
  
  /**
   * Call this method to run this test class.
   */
  public static void main(String[] args) {
    String[]   tests = { TestExceptions.class.getName() };
    TestRunner runner = new TestRunner();
    
    runner.main(tests);
  }
  

  //---------------------------------------------------------------------------
  // suite method
  //
  
  /**
   * Implementation of the JUnit method <code>suite</code>. For each of the 
   * {@link de.susebox.java.lang.ExceptionList} implementatios one set of test
   * cases is added to the suite.
   *
   * @return a test suite
   */
  public static Test suite() {
    TestSuite   suite = new TestSuite();
    
    for (int index = 0; index < EXCEPTIONS_TO_TEST.length; ++index) {
      suite.addTest(new TestExceptions("testWrappedException",  EXCEPTIONS_TO_TEST[index]));
      suite.addTest(new TestExceptions("testNestedExceptions",  EXCEPTIONS_TO_TEST[index]));
      suite.addTest(new TestExceptions("testMessageFormatting", EXCEPTIONS_TO_TEST[index]));
    }
    return suite;
  }
  
  
  //---------------------------------------------------------------------------
  // Constructor
  //
  
  /**
   * Constructs a test case instance that has a name and is responsible to test
   * one implementation of the {@link de.susebox.java.lang.ExceptionList}
   * interface.
   *
   * @param test      name of the test
   * @param exToTest  class path of the {@link de.susebox.java.lang.ExceptionList} implementation to test
   */  
  public TestExceptions(String test, String exToTest) {
    super(test);
    _classToTest = exToTest;
  }

  
  //---------------------------------------------------------------------------
  // Fixture setup and release
  //
  
  /**
   * Sets up the fixture, for example, open a network connection.
   * This method is called before a test is executed.
   *
   * @throws  Exception   for anything that might go wrong
   */
  protected void setUp() throws Exception {}

  
  /**
   * Tears down the fixture, for example, close a network connection.
   * This method is called after a test is executed.
   *
   * @throws  Exception   for anything that might go wrong
   */
  protected void tearDown() throws Exception {}
  
  
  //---------------------------------------------------------------------------
  // test cases
  //
 
  /**
   * Test wrapped exceptions. The wrapping exception is only a thin layer around
   * the real one.
   *
   * @throws  Throwable   for anything that might go wrong
   */
  public void testWrappedException() throws Throwable {
    // prerequisitories
      //    Class[]                   paraTypes = new Class[] { new Exception().getClass() };
    Class[]                   paraTypes = new Class[] { new Throwable().getClass() };

    String                    msg     = "This is an illegal argument.";
    IllegalArgumentException  argEx   = new IllegalArgumentException(msg);
    
    // construct the exception to test
    Constructor   constr  = Class.forName(_classToTest).getConstructor(paraTypes);
    ExceptionList ex      = (ExceptionList)constr.newInstance(new Object[] { argEx } );

    // do the checks
    assertTrue("rtEx: Wrapper exception not recognized.", ex.isWrapperException());
    assertTrue("rtEx: Didn't retrieve the wrapped exception.", ex.nextException() == argEx);
    assertTrue("rtEx: Messages not equal.", ((Exception)ex).getMessage().equals(argEx.getMessage()));
  }

  /**
   * Test nested exceptions.
   */
  public void testNestedExceptions() throws Throwable {
    // prerequisitories
    Object[]                  objArray  = new Object[1];
    String                    format    = "This is exception no {0} of class {1}.";
    String                    msg       = "Message without format parameters.";
    IllegalArgumentException  argEx     = new IllegalArgumentException(msg);
    //    Class[]                   paraTypes = new Class[] { new Exception().getClass(), msg.getClass(), objArray.getClass() };    
    Class[]                   paraTypes = new Class[] { new Throwable().getClass(), msg.getClass(), objArray.getClass() };
    
    // construct the exception to test
    Constructor   constr  = Class.forName(_classToTest).getConstructor(paraTypes);
    ExceptionList ex1     = (ExceptionList)constr.newInstance(new Object[] { argEx, format, new Object[] { new Integer(1), _classToTest } } );
    ExceptionList ex2     = (ExceptionList)constr.newInstance(new Object[] { ex1,   format, new Object[] { new Integer(2), _classToTest } } );

    // do the checks
    assertTrue("ex1: False wrapper exception.", ! ex1.isWrapperException());
    assertTrue("ex2: False wrapper exception.", ! ex2.isWrapperException());
    assertTrue("ex1: Didn't retrieve the nested exception.", ex1.nextException() == argEx);
    assertTrue("ex2: Didn't retrieve the first nested exception.", ex2.nextException() == ex1);
    assertTrue("ex2: Didn't retrieve the second nested exception.", ((ExceptionList)ex2.nextException()).nextException() == argEx);
    assertTrue("ex1: Format not found.", ex1.getFormat() == format);
    assertTrue("ex2: Format not found.", ex2.getFormat() == format);
  }
  
  /**
   * Test the message formatting
   */
  public void testMessageFormatting() throws Throwable {
    // prerequisitories
    String    format    = "Class {0}, reason \"{1}\", user {2}.";
    Object[]  paras     = new Object[] { _classToTest, "bad weather", "myself" };
    Class[]   paraTypes = new Class[] { format.getClass(), paras.getClass() };
    
    // construct the exception to test
    Constructor   constr  = Class.forName(_classToTest).getConstructor(paraTypes);
    ExceptionList ex      = (ExceptionList)constr.newInstance(new Object[] { format, paras } );

    // do the checks
    assertTrue("Format not found.", ex.getFormat() == format);
    
    String str1 = MessageFormat.format(format, paras);
    String str2 = ((Exception)ex).getMessage();
    assertTrue("Formating failed. Expected \"" + str1 + "\", got \"" + str2 + "\".",
              str1.equals(str2));

  }
  
  //---------------------------------------------------------------------------
  // members
  //
  private String  _classToTest = null;
}  
