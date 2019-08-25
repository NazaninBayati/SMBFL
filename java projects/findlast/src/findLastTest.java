


import org.junit.Test;
import static org.junit.Assert.*;


public class findLastTest
{
/*
 *   Test case number: 1
 *   Test case values: {1, 2, 3, 4, 5, 2, 1} find 2
 *   Expected output (Post-state): index of 5
 */
	@Test 
	public void test1()
	{ 
		findLast fLast = new findLast();
		int index = -10;
		
		int[] argv = new   int[]{ 1, 2, 3, 4, 5, 2, 1};
		int target = 2;
		index=fLast.findLast(argv, target);
		assertEquals(5, index);
		
	}
	
/*
 *   Test case number: 2
 *   Test case values: {1, 2, 3, 4, 5, 2, 1} find 9
 *   Expected output (Post-state): index of -1
 */	
	@Test 
	public void test2()
	{
 
		findLast fLast = new findLast();
		int index = -10;
		
		int[] argv = new   int[]{ 1, 2, 3, 4, 5, 2, 1};
		int target = 9;
		index=fLast.findLast(argv, target);
		assertEquals(-1, index);
		
	}
/*
 *   Test case number: 3
 *   Test case values: {1, 2 } find 1
 *   Expected output (Post-state): index of 0
 */	
		@Test 
	public void test3()
	{
 
		findLast fLast = new findLast();
		int index = -10;
		
		int[] argv = new   int[]{ 1, 2};
		int target = 1;
		index=fLast.findLast(argv, target);
		assertEquals(0, index);
		
	}
/*
 *   Test case number: 4
 *   Test case values: {1, 2 } find 1
 *   Expected output (Post-state): index of 1
 */		
		@Test
	public void test4() {
 
		findLast fLast = new findLast();
		int index = -10;

		int[] argv = new int[] { 1, 2 };
		int target = 2;
		index = fLast.findLast(argv, target);
		assertEquals(1, index);

	}
		/////till test 9
	
}
