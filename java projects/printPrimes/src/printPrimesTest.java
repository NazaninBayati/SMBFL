

import org.junit.Test;
import static org.junit.Assert.*;

public class printPrimesTest
{
	@Test
	public void test1()
	{
		assertEquals(true, printPrimes.isDivisible(1,2));
		
	}
	@Test
		public void test2()
	{
		assertEquals(false, printPrimes.isDivisible(2,3));
	}
	
//	-1
	@Test
	public void test3()
	{
		int[] result = printPrimes.printPrimes(-1);
		String results = new String();
		for(int i =0; i<result.length;i++)
			results = results + result[i];
			
		assertEquals("2000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000", results) ;
	}
 
//	0
	@Test
	public void test4()
	{
		int[] result = printPrimes.printPrimes(0);
		String results = new String();
		for(int i =0; i<result.length;i++)
			results = results + result[i];
			
		assertEquals("2000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000", results) ;
	}
//	1
	@Test
	public void test5()
	{
		int[] result = printPrimes.printPrimes(1);
		String results = new String();
		for(int i =0; i<result.length;i++)
			results = results + result[i];
			
		assertEquals("2000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000", results) ;
	}
//	2
	@Test
	public void test6()
	{
		int[] result = printPrimes.printPrimes(2);
		String results = new String();
		for(int i =0; i<result.length;i++)
			results = results + result[i];
			
		assertEquals("2300000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000", results) ;
	}
//	3
	@Test
	public void test7()
	{
		int[] result = printPrimes.printPrimes(3);
		String results = new String();
		for(int i =0; i<result.length;i++)
			results = results + result[i];
			
		assertEquals("2350000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000", results) ;
	}
//	5
	@Test
	public void test8()
	{
		int[] result = printPrimes.printPrimes(5);
		String results = new String();
		for(int i =0; i<result.length;i++)
			results = results + result[i];
			
		assertEquals("23571100000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000", results) ;
	}
//	10
	@Test
	public void test9()
	{
		int[] result = printPrimes.printPrimes(10);
		String results = new String();
		for(int i =0; i<result.length;i++)
			results = results + result[i];
			
		assertEquals("2357111317192329000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000", results) ;
	}
	@Test
	public void test10() // kill ror4
{
	assertEquals(false, printPrimes.isDivisible(-10,-3));
}
	
}