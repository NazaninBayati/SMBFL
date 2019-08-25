
//Subject class: QuickSort.class
//Tests: Test cases kill all non-equivalent mutants
//Experiment: Deletion Mutation Operators Experiment
//Author: Lin Deng
//Date: 05/23/2014
//Note: Please see the corresponding result file for detailed killing results

import static org.junit.Assert.*;

import org.junit.Test;

public class QuickSortTest {
/*
*   Test case number: 1
*   Pre-condition: given integer list { }
*   Test case values: call quickSort to sort it
*   Expected output (Post-state): ""
*/
	@Test
	public void test1() {
		int[] list = {};
		String result = new String();
		QuickSort.quickSort(list);
		for (int i = 0; i < list.length; i++)
			result = result + " " + list[i];
		assertEquals("", result);
	}
/*
*   Test case number: 2
*   Pre-condition: given integer list { 1 }
*   Test case values: call quickSort to sort it
*   Expected output (Post-state): "1"
*/
	@Test
	public void test2() {
		int[] list = { 1 };
		String result = new String();
		QuickSort.quickSort(list);
		for (int i = 0; i < list.length; i++)
			result = result + " " + list[i];
		assertEquals(" 1", result);
	}
/*
*   Test case number: 3
*   Pre-condition: given integer list { 1,2 }
*   Test case values: call quickSort to sort it
*   Expected output (Post-state): "1 2"
*/
	@Test
	public void test3() {
		int[] list = { 1, 2 };
		String result = new String();
		QuickSort.quickSort(list);
		for (int i = 0; i < list.length; i++)
			result = result + " " + list[i];
		assertEquals(" 1 2", result);
	}
/*
*   Test case number: 4
*   Pre-condition: given integer list {2,1 }
*   Test case values: call quickSort to sort it
*   Expected output (Post-state): "1 2"
*/
	@Test
	public void test4() {
		int[] list = { 2, 1 };
		String result = new String();
		QuickSort.quickSort(list);
		for (int i = 0; i < list.length; i++)
			result = result + " " + list[i];
		assertEquals(" 1 2", result);
	}
/*
*   Test case number: 5
*   Pre-condition: given integer list {1,2,3 }
*   Test case values: call quickSort to sort it
*   Expected output (Post-state): "1 2 3"
*/
	@Test
	public void test5() {
		int[] list = { 1, 2, 3 };
		String result = new String();
		QuickSort.quickSort(list);
		for (int i = 0; i < list.length; i++)
			result = result + " " + list[i];
		assertEquals(" 1 2 3", result);
	}
/*
*   Test case number: 6
*   Pre-condition: given integer list { 3,2,1}
*   Test case values: call quickSort to sort it
*   Expected output (Post-state): "1 2 3"
*/
	@Test
	public void test6() {
		int[] list = { 3, 2, 1 };
		String result = new String();
		QuickSort.quickSort(list);
		for (int i = 0; i < list.length; i++)
			result = result + " " + list[i];
		assertEquals(" 1 2 3", result);
	}
/*
*   Test case number: 7
*   Pre-condition: given integer list { 2,3,1}
*   Test case values: call quickSort to sort it
*   Expected output (Post-state): "1 2 3"
*/
	@Test
	public void test7() {
		int[] list = { 2, 3, 1 };
		String result = new String();
		QuickSort.quickSort(list);
		for (int i = 0; i < list.length; i++)
			result = result + " " + list[i];
		assertEquals(" 1 2 3", result);
	}
/*
*   Test case number: 8
*   Pre-condition: given integer list {8, 3, 7, 9, 6, 1, 9, 10 }
*   Test case values: call quickSort to sort it
*   Expected output (Post-state): "1 3 6 7 8 9 9 10"
*/
	@Test
	public void test8() {// 8 3 7 9 6 1 9 10
		int[] list = { 8, 3, 7, 9, 6, 1, 9, 10 };
		String result = new String();
		QuickSort.quickSort(list);
		for (int i = 0; i < list.length; i++)
			result = result + " " + list[i];
		assertEquals(" 1 3 6 7 8 9 9 10", result);
	}
/*
*   Test case number: 9
*   Pre-condition: given integer list {8, 2, 78, 892, 11, 0, 34 }
*   Test case values: call quickSort to sort it
*   Expected output (Post-state): "0 2 8 11 34 78 892"
*/
	@Test
	public void test9() {// 8 2 78 892 11 0 34
		int[] list = { 8, 2, 78, 892, 11, 0, 34 };
		String result = new String();
		QuickSort.quickSort(list);
		for (int i = 0; i < list.length; i++)
			result = result + " " + list[i];
		assertEquals(" 0 2 8 11 34 78 892", result);
	}
/*
*   Test case number: 10
*   Pre-condition: given integer list {9, 03, 83, 9, 2, 0, 1, 65, 2, 822, 9, 11, 22, 3, 3, 3, 47 }
*   Test case values: call quickSort to sort it
*   Expected output (Post-state): "0 1 2 2 3 3 3 3 9 9 9 11 22 47 65 83 822"
*/
	@Test
	public void test10() {// 9 03 83 9 2 0 1 65 2 822 9 11 22 3 3 3 47
		int[] list = { 9, 03, 83, 9, 2, 0, 1, 65, 2, 822, 9, 11, 22, 3, 3, 3, 47 };
		String result = new String();
		QuickSort.quickSort(list);
		for (int i = 0; i < list.length; i++)
			result = result + " " + list[i];
		assertEquals(" 0 1 2 2 3 3 3 3 9 9 9 11 22 47 65 83 822", result);
	}
/*
*   Test case number: 11
*   Pre-condition: given integer list {-6, 9, 0, 1, 17, 91, 0, 178 }
*   Test case values: call quickSort to sort it
*   Expected output (Post-state): "-6 0 0 1 9 17 91 178"
*/
	@Test
	public void test11() {// -6 9 0 1 17 91 0 178
		int[] list = { -6, 9, 0, 1, 17, 91, 0, 178 };
		String result = new String();
		QuickSort.quickSort(list);
		for (int i = 0; i < list.length; i++)
			result = result + " " + list[i];
		assertEquals(" -6 0 0 1 9 17 91 178", result);
	}
/*
*   Test case number: 12
*   Pre-condition: given integer list {-3, -2, -1, -9, -5, -1, -19, -33 }
*   Test case values: call quickSort to sort it
*   Expected output (Post-state): "-33 -19 -9 -5 -3 -2 -1 -1"
*/
	@Test
	public void test12() {// -3 -2 -1 -9 -5 -1 -19 -33
		int[] list = { -3, -2, -1, -9, -5, -1, -19, -33 };
		String result = new String();
		QuickSort.quickSort(list);
		for (int i = 0; i < list.length; i++)
			result = result + " " + list[i];
		assertEquals(" -33 -19 -9 -5 -3 -2 -1 -1", result);
	}
/*
*   Test case number: 13
*   Pre-condition: given integer list {-5, -6, -7, 0, 0, 0, 0, -8, 1, 2, 3 }
*   Test case values: call quickSort to sort it
*   Expected output (Post-state): "-8 -7 -6 -5 0 0 0 0 1 2 3"
*/
	@Test
	public void test13() {
		int[] list = { -5, -6, -7, 0, 0, 0, 0, -8, 1, 2, 3 };
		String result = new String();
		QuickSort.quickSort(list);
		for (int i = 0; i < list.length; i++)
			result = result + " " + list[i];
		assertEquals(" -8 -7 -6 -5 0 0 0 0 1 2 3", result);
	}
}