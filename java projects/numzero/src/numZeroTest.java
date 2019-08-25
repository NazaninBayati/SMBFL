import static org.junit.Assert.assertEquals;


import org.junit.Test;


public class numZeroTest {

	/*
	 *   Test case number: 1
	 *   Pre-condition: given integer array {1}
	 *   Test case values: call numZero to get the number of zeros
	 *   Expected output (Post-state): 0
	 */
		@Test
		public void test1() {
			int num = -10;
			int[] argv = new int[] { 1 };
			num = numZero.numZero(argv);
			assertEquals(0, num);
		}

	/*
	 *   Test case number: 2
	 *   Pre-condition: given integer array {0}
	 *   Test case values: call numZero to get the number of zeros
	 *   Expected output (Post-state): 1
	 */
		@Test
		public void test2() {
			int num = -10;
			int[] argv = new int[] { 0 };
			num = numZero.numZero(argv);
			assertEquals(1, num);
		}
		
	/*
	 *   Test case number: 3
	 *   Pre-condition: given integer array {1,0}
	 *   Test case values: call numZero to get the number of zeros
	 *   Expected output (Post-state): 1
	 */
		@Test
		public void test3() {
			int num = -10;
			int[] argv = new int[] { 1,0 };
			num = numZero.numZero(argv);
			assertEquals(1, num);
		}
	/*
	 *   Test case number: 4
	 *   Pre-condition: given integer array {1,0,0}
	 *   Test case values: call numZero to get the number of zeros
	 *   Expected output (Post-state): 2
	 */
		@Test
		public void test4() {
			int num = -10;
			int[] argv = new int[] { 1,0,0 };
			num = numZero.numZero(argv);
			assertEquals(2, num);
		}
	/*
	 *   Test case number: 5
	 *   Pre-condition: given integer array {-1, -2, 0, -4}
	 *   Test case values: call numZero to get the number of zeros
	 *   Expected output (Post-state): 1
	 */
		@Test
		public void test5() {
			int num = -10;
			int[] argv = new int[] { -1, -2, 0, -4 };
			num = numZero.numZero(argv);
			assertEquals(1, num);
		}
	}
