import junit.framework.TestCase;


public class maxTest extends TestCase {
	public maxTest() {
	    super("maxTest");}
	


	muse m1 = new muse();
	
	public void testT0(){
		assertEquals(1, m1.Set_Min(3, 1));
	}
	public void testT1(){
		assertEquals(-4,m1.Set_Min(5, -4));
	}
	public void testT2(){
		assertEquals(-1, m1.Set_Min(0, -1));
	}
	public void testT3(){
		assertEquals(0, m1.Set_Min(0, 0));
	}
	public void testT4(){
		assertEquals(-1, m1.Set_Min(-1, 3));
	}
	/*	public void testT5(){
		assertEquals(3, m1.Set_Min(9, 3));
	}
	public void testT6(){
		assertEquals(1, m1.Set_Min(1, 3));
	}
	public void testT7(){
		assertEquals(3, m1.Set_Min(11, 3));
	}
	public void testT8(){
		assertEquals(-1, m1.Set_Min(-1, 1));
	}
	public void testT9(){
		assertEquals(-2, m1.Set_Min(8, -2));
	}
	public void testT10(){
		assertEquals(0, m1.Set_Min(0, 0));
	}
	public void testT11(){
		assertEquals(0, m1.Set_Min(5,0));
	}
	public void testT12(){
		assertEquals(-1, m1.Set_Min(-1, 0));
	}
	*/
	public void testT13(){
		assertEquals(-3, m1.Set_Min(0, -3));
	}
}