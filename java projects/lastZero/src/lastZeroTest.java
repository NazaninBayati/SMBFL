import static org.junit.Assert.assertEquals;


import org.junit.Test;


public class lastZeroTest {

	/*
	*   Test case number: 1
	*   Pre-condition: given array { -1, 2, -3, 0, -5, 2, 1}
	*   Test case values: get the last zero
	*   Expected output (Post-state): index of 3
	*/
		@Test
		public void test1()
		{ 
			lastZero l = new lastZero();
			int[] argv = new   int[]{ -1, 2, -3, 0, -5, 2, 1};
			int result=l.lastZero(argv);
			assertEquals(3, result);
			
		}
	/*
	*   Test case number: 2
	*   Pre-condition: given null array
	*   Test case values: get the last zero
	*   Expected output (Post-state): throw NPE
	*/	
		@Test(expected=NullPointerException.class)
			public void test2()
		{ 
			lastZero l = new lastZero();
			int[] argv = null;
			int result=l.lastZero(argv);
			assertEquals(0, result);
			
		}
	/*
	*   Test case number: 3
	*   Pre-condition: given array { -1, 2, -3,-5, 2, 1}
	*   Test case values: get the last zero
	*   Expected output (Post-state): index of -1
	*/		
		@Test
			public void test3()
		{ 
			lastZero l = new lastZero();
			int[] argv = new   int[]{ -1, 2, -3,-5, 2, 1};
			int result=l.lastZero(argv);
			assertEquals(-1, result);
			
		}
	/*
	*   Test case number: 4
	*   Pre-condition: given array { 0,0}
	*   Test case values: get the last zero
	*   Expected output (Post-state): index of 0
	*/		
			@Test
			public void test4()
		{ 
			lastZero l = new lastZero();
			int[] argv = new   int[]{ 0,0};
			int result=l.lastZero(argv);
			assertEquals(0, result);
			
		}
			////////////till test 9
	}
