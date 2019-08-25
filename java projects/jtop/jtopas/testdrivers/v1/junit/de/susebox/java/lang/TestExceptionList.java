/*
 * TestExceptionList.java: JUnit test for ExceptionList implementations
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

package de.susebox.java.lang;

//-----------------------------------------------------------------------------
// Imports
//
import java.text.MessageFormat;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.framework.Assert;
import junit.swingui.TestRunner;


//-----------------------------------------------------------------------------
// Class TestExceptionList
//

/**<p>
 * This class tests the functionality of the {@link ExceptionList} interface
 * through various implementing classes.
 *</p>
 *
 * @see     ExceptionList
 * @see     ExtRuntimeException
 * @author  Heiko Blau
 */
public class TestExceptionList extends TestCase {
  
  //---------------------------------------------------------------------------
  // properties
  //

  
  //---------------------------------------------------------------------------
  // main method
  //
  
  /**
   * call this method to invoke the tests
   */
  public static void main(String[] args) {
    String[]   tests = { TestExceptionList.class.getName() };
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
    
    suite.addTest(new TestExceptionList("testWrappedException"));
    suite.addTest(new TestExceptionList("testExceptionList"));
    suite.addTest(new TestExceptionList("testMessageFormatting"));
    return suite;
  }
  
  
  //---------------------------------------------------------------------------
  // Constructor
  //
  
  /**
   * Default constructor. Standard input {@link java.lang.System#in} is used
   * to construct the input stream reader.
   */  
  public TestExceptionList(String test) {
    super(test);
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
   * Test wrapped exceptions. The wrapping exception is only a thin layer around
   * the real one.
   */
  public void testWrappedException() throws Throwable {
    String                        msg     = "This is an illegal argument.";
    IllegalArgumentException      argEx   = new IllegalArgumentException(msg);
    ExtRuntimeException           rtEx    = new ExtRuntimeException(argEx);
    ExtIndexOutOfBoundsException  indexEx = new ExtIndexOutOfBoundsException(argEx);
    ExtIndexOutOfBoundsException  bigEx   = new ExtIndexOutOfBoundsException(rtEx); 
    
    assertTrue("rtEx: Wrapper exception not recognized.", rtEx.isWrapperException());
    assertTrue("rtEx: Didn't retrieve the wrapped exception.", rtEx.nextException() == argEx);
    assertTrue("rtEx: Messages not equal.", rtEx.getMessage().equals(argEx.getMessage()));
    
    assertTrue("indexEx: Wrapper exception not recognized.", indexEx.isWrapperException());
    assertTrue("indexEx: Didn't retrieve the wrapped exception.", indexEx.nextException() == argEx);
    assertTrue("indexEx: Messages not equal.", indexEx.getMessage().equals(argEx.getMessage()));

    assertTrue("bigEx: Wrapper exception not recognized.", bigEx.isWrapperException());
    assertTrue("bigEx: Didn't retrieve the first wrapped exception.", bigEx.nextException() == rtEx);
    assertTrue("bigEx: Didn't retrieve the second wrapped exception.", ((ExceptionList)bigEx.nextException()).nextException() == argEx);
    assertTrue("bigEx: Messages not equal.", bigEx.getMessage().equals(argEx.getMessage()));
  }

  /**
   * Test nested exceptions.
   */
  public void testExceptionList() throws Throwable {
    String              format = "This is exception no {0} of class {1}.";
    String              msg    = "Message without format parameters.";
    ExtRuntimeException rtEx1  = new ExtRuntimeException( format, 
                                                          new Object[] { new Integer(1), ExtRuntimeException.class } );
    ExtRuntimeException rtEx2  = new ExtRuntimeException( rtEx1,
                                                          format, 
                                                          new Object[] { new Integer(2), ExtRuntimeException.class } );
    ExtIndexOutOfBoundsException  indexEx = new ExtIndexOutOfBoundsException(rtEx2, msg);
    
    assertTrue("rtEx1: False wrapper exception.", ! rtEx1.isWrapperException());
    assertTrue("rtEx2: False wrapper exception.", ! rtEx2.isWrapperException());
    assertTrue("indexEx: False wrapper exception.", ! indexEx.isWrapperException());
    assertTrue("rtEx2: Didn't retrieve the nested exception.", rtEx2.nextException() == rtEx1);
    assertTrue("indexEx: Didn't retrieve the first nested exception.", indexEx.nextException() == rtEx2);
    assertTrue("indexEx: Didn't retrieve the second nested exception.", ((ExceptionList)indexEx.nextException()).nextException() == rtEx1);
    assertTrue("rtEx1: Format not found.", rtEx1.getFormat() == format);
    assertTrue("rtEx2: Format not found.", rtEx2.getFormat() == format);
    assertTrue("indexEx: Format not found.", indexEx.getFormat() == msg);
  }
  
  /**
   * Test the message formatting
   */
  public void testMessageFormatting() throws Throwable {
    String              format = "Class {0}, reason \"{1}\", user {2}.";
    Object[]            paras  = new Object[] { ExtRuntimeException.class, "bad weather", "myself" };
    ExtRuntimeException rtEx   = new ExtRuntimeException(format, paras);
                                        
    assertTrue("Format not found.", rtEx.getFormat() == format);
    
    String str1 = MessageFormat.format(format, paras);
    String str2 = rtEx.getMessage();
    assertTrue("Formating failed. Expected \"" + str1 + "\", got \"" + str2 + "\".",
              str1.equals(str2));
  }
}  
