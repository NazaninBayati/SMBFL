import static org.junit.Assert.assertEquals;


import org.junit.Test;


public class LRSTest {

	/*
	*   Test case number: 1
	*   Pre-condition: given string aaaaaaaaa
	*   Test case values: call lrs() to find longest repeated substring
	*   Expected output (Post-state): aaaaaaaa
	*/
		@Test
	public void test1 ()
	{
			String s = new String("aaaaaaaaa");
	     s = s.replaceAll("\\s+", " ");
			assertEquals("aaaaaaaa", LRS.lrs(s));  
	}
	/*
	*   Test case number: 2
	*   Pre-condition: given string abcdefg
	*   Test case values: call lrs() to find longest repeated substring
	*   Expected output (Post-state): 
	*/
		@Test
	 public void test2()
	{
			String s = new String("abcdefg");
	     s = s.replaceAll("\\s+", " ");
	     assertEquals("", LRS.lrs(s));  
	}
	/*
	*   Test case number: 3
	*   Pre-condition: given string " "
	*   Test case values: call lrs() to find longest repeated substring
	*   Expected output (Post-state): ""
	*/
		@Test
	   public void test3()
	{
			String s = new String();
	     s = s.replaceAll("\\s+", " ");
	     assertEquals("", LRS.lrs(s));  
	}
	/*
	*   Test case number: 4
	*   Pre-condition: 
	*   Test case values: call lcp() to return the longest common prefix of vcd and cd
	*   Expected output (Post-state): ""
	*/
		@Test
	     public void test4()
	{
			// String s = new String();
	     // s = s.replaceAll("\\s+", " ");
			assertEquals("", LRS.lcp("vcd","cd"));
	}
	/*
	*   Test case number: 5
	*   Pre-condition: given string aaabaaaaabaabaaaabaaaaaaaaaaaaaabaaaaaaaaaaaaaaabaaaaaaaaaaaaaaba
	*   Test case values: call lrs() to find longest repeated substring
	*   Expected output (Post-state): aaaaaaaaaaaaaabaaaaaaaaaaaaaa
	*/
		@Test
	 public void test5 ()
	{
			String s = new String("aaabaaaaabaabaaaabaaaaaaaaaaaaaabaaaaaaaaaaaaaaabaaaaaaaaaaaaaaba");
	     s = s.replaceAll("\\s+", " ");
	     assertEquals("aaaaaaaaaaaaaabaaaaaaaaaaaaaa", LRS.lrs(s));  
	}
////////////////till test12
	}