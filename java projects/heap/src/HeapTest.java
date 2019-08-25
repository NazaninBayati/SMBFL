

//Subject class: Heap.class
//Tests: Test cases kill all non-equivalent mutants
//Experiment: Deletion Mutation Operators Experiment
//Author: Lin Deng
//Date: 05/23/2014
//Note: Please see the corresponding result file for detailed killing results

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.Test;

import static org.junit.Assert.*;

public class HeapTest {
/*
*   Test case number: 1
*   Test case values: sort array {"aaa","bbb","ccc","ddd"}
*   Expected output (Post-state): aaa bbb ccc ddd 
*/
	@Test
	public void test1 ()
{
		String[] a = {"aaa","bbb","ccc","ddd"};
		Heap.sort(a);
		String result = new String();
		for(int i = 0; i < a.length; i++)
			result = result+ a[i] +" ";
		assertEquals("aaa bbb ccc ddd ", result);
}
/*
*   Test case number: 2
*   Test case values: sort array {"fffffff","bbb","eeeeeeeeeee","dddddd"}
*   Expected output (Post-state): bbb dddddd eeeeeeeeeee fffffff 
*/
	@Test
 public void test2 ()
{
		String[] a = {"fffffff","bbb","eeeeeeeeeee","dddddd"};
		Heap.sort(a);
		String result = new String();
		for(int i = 0; i < a.length; i++)
			result = result+ a[i] +" ";
		assertEquals("bbb dddddd eeeeeeeeeee fffffff ", result);
}
/*
*   Test case number: 3
*   Test case values: sort array {"5","3","4","2","1"}
*   Expected output (Post-state): 1 2 3 4 5 
*/
	@Test
   public void test3 ()
{
		String[] a = {"5","3","4","2","1"};
		Heap.sort(a);
		String result = new String();
		for(int i = 0; i < a.length; i++)
			result = result+ a[i] +" ";
		assertEquals("1 2 3 4 5 ", result);
}
/*
*   Test case number: 4
*   Test case values: sort array { }
*   Expected output (Post-state):   
*/
	@Test
     public void test4()
{
		String[] a = {};
		Heap.sort(a);
		String result = new String();
		for(int i = 0; i < a.length; i++)
			result = result+ a[i] +" ";
		assertEquals("", result);
}
/*
*   Test case number: 5
*   Test case values: sort array {"bed","bug","dad","yes","zoo","all","bad","yet"}
*   Expected output (Post-state): all bad bed bug dad yes yet zoo 
*/
	@Test
       public void test5()
{
		String[] a = {"bed","bug","dad","yes","zoo","all","bad","yet"};
		Heap.sort(a);
		String result = new String();
		for(int i = 0; i < a.length; i++)
			result = result+ a[i] +" ";
		assertEquals("all bad bed bug dad yes yet zoo ", result);
}
/*
*   Test case number: 6
*   Test case values: check if "abc" is less than "abc"
*   Expected output (Post-state): false
*/
	@Test
 public void test6()
{
		assertEquals(false, Heap.less("abc","abc"));
}
/*
*   Test case number: 7
*   Test case values: check if "abc" is less than "def"
*   Expected output (Post-state): true
*/
	@Test
 public void test7()
{
		assertEquals(true,Heap.less("abc","def"));
}
/*
*   Test case number: 8
*   Pre-condition: given array {"bed","bug","dad","yes","zoo","all","bad","yet"}
*   Test case values: string 2 is less than 5
*   Expected output (Post-state): true
*/
	@Test
 public void test8()
{
			String[] a = {"bed","bug","dad","yes","zoo","all","bad","yet"};
			assertEquals(true, Heap.less(a, 2, 5));
}
/*
*   Test case number: 9
*   Pre-condition: given array {"bed","bug","dad","yes","dad","all","bad","yet"}
*   Test case values: string 3 is less than 5
*   Expected output (Post-state): false
*/
	@Test
 public void test9()
{
			String[] a = {"bed","bug","dad","yes","dad","all","bad","yet"};
			assertEquals(false, Heap.less(a, 3, 5));
}
/*
*   Test case number: 10
*   Pre-condition: given array {"bed","bug","dad","yes","dad","all","bad","yet"}
*   Test case values: check if the array is sorted
*   Expected output (Post-state): false
*/  
	@Test
 public void test10()
{
			String[] a = {"bed","bug","dad","yes","dad","all","bad","yet"};
			assertEquals(false, Heap.isSorted(a));
}
/*
*   Test case number: 11
*   Pre-condition: given array {"bed","yet"}
*   Test case values: check if the array is sorted
*   Expected output (Post-state): true
*/ 
	@Test
 public void test11()
{
			String[] a = {"bed","yet"};
			assertEquals(true, Heap.isSorted(a));
}
/*
*   Test case number: 12
*   Pre-condition: given array {"abc","def","ghi","jkl","mno"}
*   Test case values: sort the array
*   Expected output (Post-state): abc def ghi jkl mno 
*/ 
	@Test
	public void test12 ()
{
		String[] a = {"abc","def","ghi","jkl","mno"};
		Heap.sort(a);
		String result = new String();
		for(int i = 0; i < a.length; i++)
			result = result+ a[i] +" ";
		assertEquals("abc def ghi jkl mno ", result);
}
/*
*   Test case number: 13
*   Pre-condition: given array {"3","1","0"}
*   Test case values: check if the array is sorted
*   Expected output (Post-state): false
*/ 
	@Test
 public void test13()
{
String[] a = {"3","1","0"};
assertEquals(false, Heap.isSorted(a));
}
/*
*   Test case number: 14
*   Pre-condition: given array {"3","2"}
*   Test case values: check if the array is sorted
*   Expected output (Post-state): false
*/
	@Test
 public void test14()
{
String[] a = {"3","2"};
assertEquals(false, Heap.isSorted(a));
}
/*
*   Test case number: 15
*   Pre-condition: given array { "0", "2", "0" }
*   Test case values: check if the array is sorted
*   Expected output (Post-state): false
*/
	@Test
	public void test15() {
		String[] a = { "0", "2", "0" };
		assertEquals(false, Heap.isSorted(a));
	}
	
//	@Test
//	public void test15() {
//		String[] a = { "4","3", "2","1","0" };
//		ByteArrayOutputStream outContent = new ByteArrayOutputStream();
//		System.setOut(new PrintStream(outContent));
//		Heap.show(a);
//		assertEquals("4\n3\n2\n1\n0\n", outContent.toString());
//		System.setOut(null);
//	}

}