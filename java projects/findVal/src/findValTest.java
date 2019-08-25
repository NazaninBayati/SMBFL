import static org.junit.Assert.assertEquals;

import org.junit.Test;




public class findValTest 
{

	/*
	 *   Test case number: 1
	 *   Test case values: { 1, 2, 3, 4, 5, 2, 1} find 2
	 *   Expected output (Post-state): index of 5
	 */
		@Test
		public void test1()
		{
			findVal fv = new findVal();
			int index = -10;
			
			int[] argv = new   int[]{ 1, 2, 3, 4, 5, 2, 1};
			int target = 2;
			index=fv.findVal(argv, target);
			assertEquals(5, index);;
			
		}
	/*
	 *   Test case number: 2
	 *   Test case values: { 1, 2, 3, 4, 2, 2, 1} find 2
	 *   Expected output (Post-state): index of 5
	 */	
		@Test
		public void test2()
		{
			findVal fv = new findVal();
			int index = -10;
			
			int[] argv = new   int[]{ 1, 2, 3, 4, 2, 2, 1};
			int target = 2;
			index=fv.findVal(argv, target);
			assertEquals(5, index);;
			
		}
	/*
	 *   Test case number: 3
	 *   Test case values: { 1, 2, 3, 4, 2, 2, 1} find 9
	 *   Expected output (Post-state): index of -1
	 */	
		@Test
		public void test3()
		{
			findVal fv = new findVal();
			int index = -10;
			
			int[] argv = new   int[]{ 1, 2, 3, 4, 2, 2, 1};
			int target = 9;
			index=fv.findVal(argv, target);
			assertEquals(-1, index);;
			
		}
	/*
	 *   Test case number: 4
	 *   Test case values: { 1, 2, 3, 4, 2, 2, 1} find 0
	 *   Expected output (Post-state): index of -1
	 */	
		@Test
	public void test4()
	{
		findVal fv = new findVal();
		int index = -10;
		
		int[] argv = new   int[]{ 1, 2, 3, 4, 2, 2, 1};
		int target = 0;
		index=fv.findVal(argv, target);
		assertEquals(-1, index);;
		
	}
	/*
	 *   Test case number: 5
	 *   Test case values: { 1, 2 } find 1
	 *   Expected output (Post-state): index of 0
	 */
		@Test
	public void test5()
	{
		findVal fv = new findVal();
		int index = -10;
		
		int[] argv = new   int[]{ 1, 2 };
		int target = 1;
		index=fv.findVal(argv, target);
		assertEquals(0, index);;
		
	}
		////////till test 10
		
	}

