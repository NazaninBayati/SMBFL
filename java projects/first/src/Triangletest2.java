import junit.framework.TestCase;


public class Triangletest2 extends TestCase {

	triangle tv1=new triangle();
	public void testT0(){
		assertEquals(0, tv1.Type_Area(2, 2, 5));
	}
	public void testT1(){
		assertEquals(3,tv1.Type_Area(2, 2, 2));
	}
	public void testT2(){
		assertEquals(2,tv1.Type_Area(4, 4, 3));
	}
	public void testT3(){
		assertEquals(2,tv1.Type_Area(5, 4, 3));
	}
	public void testT4(){
		assertEquals(2,tv1.Type_Area(6, 5, 4));
	}
	public void testT5(){
		assertEquals(3,tv1.Type_Area(3, 3, 3));
	}

}
