import junit.framework.TestCase;


public class checktest2 extends TestCase {
	check ch = new check();
	
	public void testT0(){
		assertEquals(5, ch.func(4, 2, 2)); //a>b +//-///*
	}
	public void testT1(){
		assertEquals(0, ch.func(1, 2, 3));  /// a<b   3//2//3
	}
	public void testT2(){
		assertEquals(2, ch.func(2, 2, 3));  /// a=>b 3 //1//2
	}
	public void testT3(){
		assertEquals(-3, ch.func(-2,-4,4)); 
	}
	public void testT4(){
		assertEquals(4, ch.func(-4,-2, -4)); //-4//0//2
	}
	public void testT5(){
		assertEquals(-2, ch.func(-1, -2,2)); 
	}
	public void testT6(){
		assertEquals(0, ch.func(2, 2, 2));  /// 2 /// 2//8
	}

}
