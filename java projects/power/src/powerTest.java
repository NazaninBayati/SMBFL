
import static org.junit.Assert.*;

import org.junit.Test;


public class powerTest
{
/*
*   Test case number: 1
*   Test case values: 0^0
*   Expected output (Post-state): 1
*/
	@Test
	public void test1()
	{ 
		power p = new power();

		assertEquals(1, p.power(0,0));
		
	}
/*
*   Test case number: 2
*   Test case values: 0^1
*   Expected output (Post-state): 0
*/
	@Test
	public void test2()
	{ 
		power p = new power();

		assertEquals(0, p.power(0,1));
		
	}
/*
*   Test case number: 3
*   Test case values: -1^0
*   Expected output (Post-state): 1
*/ 
	@Test
	public void test3()
	{ 
		power p = new power();

		assertEquals(1, p.power(-1,0));
		
	}
/*
*   Test case number: 4
*   Test case values: -1^1
*   Expected output (Post-state): -1
*/ 
	@Test
	public void test4()
	{ 
		power p = new power();

		assertEquals(-1, p.power(-1,1));
		
	}
/*
*   Test case number: 5
*   Test case values: 2^3
*   Expected output (Post-state): 8
*/ 
	@Test
	public void test5()
	{ 
		power p = new power();

		assertEquals(8, p.power(2,3));
		
	}
/*
*   Test case number: 6
*   Test case values: -2^3
*   Expected output (Post-state): -8
*/ 
	@Test
	public void test6()
	{ 
		power p = new power();

		assertEquals(-8, p.power(-2,3));
		
	}
/*
*   Test case number: 7
*   Test case values: -9^3
*   Expected output (Post-state): -729
*/
	@Test
	public void test7()
	{ 
		power p = new power();

		assertEquals(-729, p.power(-9,3));
		
	}
/*
*   Test case number: 8
*   Test case values: 2^4
*   Expected output (Post-state): 16
*/
	@Test
	public void test8()
	{ 
		power p = new power();

		assertEquals(16, p.power(2,4));
		
	}
/*
*   Test case number: 9
*   Test case values: 0^4
*   Expected output (Post-state): 0
*/ 	
	@Test
	public void test9()
	{ 
		power p = new power();

		assertEquals(0, p.power(0,4));
		
	}
/*
*   Test case number: 10
*   Test case values: 4^-4
*   Expected output (Post-state): 4
*/ 	
	@Test
	public void test10()
	{ 
		power p = new power();

		assertEquals(4, p.power(4,-4));
		
	}
}