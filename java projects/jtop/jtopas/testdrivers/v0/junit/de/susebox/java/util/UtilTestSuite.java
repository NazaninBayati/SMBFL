/*
 * UtilTestSuite.java: JUnit test for the util package
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
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.swingui.TestRunner;


//-----------------------------------------------------------------------------
// Class UtilTestSuite
//

/**<p>
 * This is the test suite for the util package. It composes all test classes
 * in the util package to a singel test suite.
 *</p>
 *
 * @author  Heiko Blau
 */
public class UtilTestSuite extends TestCase {
  
  //---------------------------------------------------------------------------
  // main method
  //
  
  /**
   * call this method to invoke the tests
   */
  public static void main(String[] args) {
    String[]   tests = { UtilTestSuite.class.getName() };
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
    
    suite.addTest(TestTokenizerProperties.suite());
    suite.addTest(TestTokenProperties.suite());
    suite.addTest(TestInputStreamTokenizer.suite());
    suite.addTest(TestDifficultSituations.suite());
    suite.addTest(TestEmbeddedTokenizer.suite());
    return suite;
  }

  //---------------------------------------------------------------------------
  // constructor
  //
  
  /**
   * Constructor
   */
  public UtilTestSuite(String name) {
    super(name);
  }
}
