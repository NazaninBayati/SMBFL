import static org.junit.Assert.assertEquals;

import org.junit.Test;




public class CountPositiveTest {

	/*
	 *   Test case number: 1
	 *   Test case values: -1, 2, -3, 4, -5, 2, 1
	 *   Expected output (Post-state): 4 positive
	 */
		@Test
		public void test1()
		{
			CountPositive c = new CountPositive();
			int[] argv = new   int[]{ -1, 2, -3, 4, -5, 2, 1};
			int result=c.countPositive(argv);
			assertEquals(4, result);
			
		}
	/*
	 *   Test case number: 2
	 *   Test case values: null
	 *   Expected output (Post-state): throw NPE
	 */	
		@Test (expected=NullPointerException.class)
			public void test2()
		{
			CountPositive c = new CountPositive();
			int[] argv = null;
			int result=c.countPositive(argv);
			assertEquals(0, result);
			
		}
	/*
	 *   Test case number: 3
	 *   Test case values: -1, 2, 0, 4, -5, 2, 1
	 *   Expected output (Post-state): 4 positive
	 */	
		@Test
		public void test3()
		{
			CountPositive c = new CountPositive();
			int[] argv = new   int[]{ -1, 2, 0, 4, -5, 2, 1};
			int result=c.countPositive(argv);
			assertEquals(5, result);
			
		}
		///////////till test10
	}

