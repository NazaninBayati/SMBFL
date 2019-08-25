import static org.junit.Assert.assertEquals;

import org.junit.Test;



public class CheckPalindromeTest {

	/*
	 *   Test case number: 1
	 *   Test case values: rotator
	 *   Expected output (Post-state): true
	 */
	 @Test
		public void test1 ()
	  {
			boolean result = CheckPalindrome.isPalindrome("rotator");
			assertEquals(true, result) ;
	  }

	/*
	 *   Test case number: 2
	 *   Test case values: abc123
	 *   Expected output (Post-state): false
	 */
	 @Test
	    public void test2 ()
	  {
			boolean result = CheckPalindrome.isPalindrome("abc123");
			assertEquals(false, result) ;
	  }
	  
	  /*
	 *   Test case number: 3
	 *   Test case values: ab
	 *   Expected output (Post-state): false
	 */
	 @Test
	 public void test3 ()
	{
			boolean result = CheckPalindrome.isPalindrome("ab");
			assertEquals(false, result) ;
	}
	 @Test
	 public void test4 ()
	{
			boolean result = CheckPalindrome.isPalindrome("aa");
			assertEquals(false, result) ;
	}
	 //////////add till test 14
	

}
