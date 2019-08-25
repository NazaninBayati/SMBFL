

import org.junit.Test;
import static org.junit.Assert.*;


public class sliceTest {
	@Test
	public void test1()
	{
		assertEquals(-1, slicedCalculation.FnMinus(1, 2));

	}
	@Test
	public void test2()
	{
		assertEquals(0, slicedCalculation.FnMinus(2, 2));

	}
	@Test
	public void test3()
	{
		assertEquals(-4, slicedCalculation.FnMinus(1, 5));

	}
	@Test
	public void test4()
	{
		assertEquals(3, slicedCalculation.FnMinus(11, 7));

	}@Test
	public void test5()
	{
		assertEquals(0, slicedCalculation.FnMinus(0, 0));

	}
	

}
