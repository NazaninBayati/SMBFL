

//Subject class: oddOrPos.class
//Tests: Test cases kill all non-equivalent mutants
//Experiment: Deletion Mutation Operators Experiment
//Author: Lin Deng
//Date: 05/23/2014
//Note: Please see the corresponding result file for detailed killing results
import org.junit.Test;
import static org.junit.Assert.*;

public class oddOrPosTest
{
/*
*   Test case number: 1
*   Pre-condition: given integer array { 1,2,3,4 }
*   Test case values: call oddOrPos() to return the number of elements in x that
*       are either odd or positive (or both)
*   Expected output (Post-state): 4
*/
	@Test
	public void test1()
	{ 
		oddOrPos c = new oddOrPos();
		int[] argv = new   int[]{ 1,2,3,4};
		int result=c.oddOrPos(argv);
		//return result;
		assertEquals(4, result);
		
	}
/*
*   Test case number: 2
*   Pre-condition: given integer array { -1,2,3,4 }
*   Test case values: call oddOrPos() to return the number of elements in x that
*       are either odd or positive (or both)
*   Expected output (Post-state): 3
*/
	@Test
	public void test2()
	{ 
		oddOrPos c = new oddOrPos();
		int[] argv = new   int[]{ -1,2,3,4};
		int result=c.oddOrPos(argv);
		//return result;
		assertEquals(3, result);
		
	}
/*
*   Test case number: 3
*   Pre-condition: given integer array { 1,-2,3,4 }
*   Test case values: call oddOrPos() to return the number of elements in x that
*       are either odd or positive (or both)
*   Expected output (Post-state): 3
*/
	@Test
	public void test3()
	{ 
		oddOrPos c = new oddOrPos();
		int[] argv = new   int[]{ 1,-2,3,4};
		int result=c.oddOrPos(argv);
		//return result;
		assertEquals(3, result);
		
	}
/*
*   Test case number: 4
*   Pre-condition: given integer array { 0,1,2,-3,4 }
*   Test case values: call oddOrPos() to return the number of elements in x that
*       are either odd or positive (or both)
*   Expected output (Post-state): 3
*/ 
	@Test
	public void test4()
	{ 
		oddOrPos c = new oddOrPos();
		int[] argv = new   int[]{ 0,1,2,-3,4};
		int result=c.oddOrPos(argv);
		//return result;
		assertEquals(3, result);
		
	}
/*
*   Test case number: 5
*   Pre-condition: given integer array { 0,1,2,3,-4 }
*   Test case values: call oddOrPos() to return the number of elements in x that
*       are either odd or positive (or both)
*   Expected output (Post-state): 3
*/
	@Test
	public void test5()
	{ 
		oddOrPos c = new oddOrPos();
		int[] argv = new   int[]{ 0,1,2,3,-4};
		int result=c.oddOrPos(argv);
		//return result;
		assertEquals(3, result);
		
	}
/*
*   Test case number: 6
*   Pre-condition: given integer array { -2, -4, -5, -6, -7, -8, -9, -1}
*   Test case values: call oddOrPos() to return the number of elements in x that
*       are either odd or positive (or both)
*   Expected output (Post-state): 0
*/
	@Test
	public void test6()
	{ 
		oddOrPos c = new oddOrPos();
		int[] argv = new   int[]{ -2, -4, -5, -6, -7, -8, -9, -1};
		int result=c.oddOrPos(argv);
		//return result;
		assertEquals(0, result);
		
	}
/*
*   Test case number: 7
*   Pre-condition: given integer array null
*   Test case values: call oddOrPos() to return the number of elements in x that
*       are either odd or positive (or both)
*   Expected output (Post-state): throw NPE
*/		
	@Test (expected = NullPointerException.class)
		public void test7()
	{
		// TODO Auto-generated method stub
		oddOrPos c = new oddOrPos();
		int[] argv = null;
		int result=c.oddOrPos(argv);
		//return result;
		assertEquals(0, result);
	}
	
}