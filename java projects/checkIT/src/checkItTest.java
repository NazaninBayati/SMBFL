import static org.junit.Assert.assertEquals;

import org.junit.Test;


public class checkItTest {

	/*
	 *   Test case number: 1
	 *   Test case values: a=false, b=true, c=false
	 *   Expected output (Post-state): P isn't true
	 */
		@Test
		public void test1()
		{

			String result = checkIt.checkIt(false, true, false);
			assertEquals("P isn't true" ,result);
			
		}
	/*
	 *   Test case number: 2
	 *   Test case values: a=true, b=true, c=false
	 *   Expected output (Post-state): P is true
	 */
		@Test
		public void test2()
		{

			String result = checkIt.checkIt(true, true, false);
			assertEquals("P is true" ,result);
			
		}
	/*
	 *   Test case number: 3
	 *   Test case values: a=true, b=false, c=true
	 *   Expected output (Post-state): P is true
	 */
		@Test
		public void test3()
		{

			String result = checkIt.checkIt(true, false, true);
			assertEquals("P is true" ,result);
			
		}
	/*
	 *   Test case number: 4
	 *   Test case values: a=true, b=false, c=false
	 *   Expected output (Post-state): P isn't true
	 */
		@Test
		public void test4()
		{

			String result = checkIt.checkIt(true, false, false);
			assertEquals("P isn't true" ,result);
			
		}
	/*
	 *   Test case number: 5
	 *   Test case values: a=true, b=true, c=true
	 *   Expected output (Post-state): P is true
	 */
		@Test
		public void test5()
		{

			String result = checkIt.checkIt(true, true, true);
			assertEquals("P is true" ,result);
				
		}
	}

