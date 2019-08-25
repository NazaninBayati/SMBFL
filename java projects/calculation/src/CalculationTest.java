

import org.junit.Test;
import static org.junit.Assert.*;

public class CalculationTest
{
	
	public void test1()
	{
		assertEquals(-1, Calculation.FnMinus(1, 2));

	}
	
	public void test2()
	{
		assertEquals(6, Calculation.FnTimes(2, 3));
	}
	
	public void test3()
	{
		assertEquals(2, Calculation.FnDivide(4, 2));
	}
	@Test (expected=ArithmeticException.class)
	public void test4()
	{
		assertEquals(0, Calculation.FnDivide(1, 0));
	}
	
	public void test5()
	{
		assertEquals(-20, Calculation.FnTimes(4, -5));
	}
	
	public void test6()
	{
		assertEquals(1, Calculation.DefineAndRoundFraction(0, 0));
	}
	
	public void test7()
	{
		assertEquals(0, Calculation.DefineAndRoundFraction(1, 8));
	}
	
	public void test8()
	{
		assertEquals(0, Calculation.FnDivide(-1, -10));
	}
	
	public void test9()
	{
		assertEquals(-5, Calculation.FnDivide(10, -2));
	}
}

