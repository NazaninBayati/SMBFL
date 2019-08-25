import static org.junit.Assert.assertEquals;


import org.junit.Test;


public class GaussianTest2 
{

	/*
	 *   Test case number: 1
	 *   Test case values: Phi2 (2, 3, 4)
	 *   Expected output (Post-state): 0.40129367
	 */
	@Test
	  public void test1 ()
	  {
			double z     = 2;
	        double mu    = 3;
	        double sigma = 4;
	        assertEquals(0.40129367,  Gaussian.Phi2(z, mu, sigma), 0.00001);
	        // double y = Phi(z);
	        // StdOut.println(PhiInverse(y));
	  }
	/*
	 *   Test case number: 2
	 *   Test case values: PhiInverse(Phi2(10) )
	 *   Expected output (Post-state): 7.99999999
	 */
		@Test
	    public void test2 ()
	  {
			double z     = 10;
	        double mu    = 3;
	        double sigma = 4;
	        
	        double y = Gaussian.Phi2(z);
	        assertEquals(7.99999999, Gaussian.PhiInverse(y), 0.00001);
	  }
	  
	/*
	 *   Test case number: 3
	 *   Test case values: phi (10, 3, 4)
	 *   Expected output (Post-state): 0.02156932
	 */
		@Test
	      public void test3 ()
	  {
			double z     = 10;
	        double mu    = 3;
	        double sigma = 4;
	        
	        double y = Gaussian.phi(z, mu, sigma);
	        assertEquals(0.02156932, y, 0.00001);
	  }
	 /*
	 *   Test case number: 4
	 *   Test case values: PhiInverse(Phi2(-10) )
	 *   Expected output (Post-state): -7.995849546
	 */ 
		@Test
	      public void test4 ()
	  {
			double z     = -10;
	        double mu    = 3;
	        double sigma = 4;
	        
	        double y = Gaussian.Phi2(z);
	        assertEquals(-7.995849546, Gaussian.PhiInverse(y),0.00001);
	  }
	 /*
	 *   Test case number: 5
	 *   Test case values: Phi2(1, 3, 4) , Phi2 (1)
	 *   Expected output (Post-state): 0.30853753872, 1
	 */ 
		@Test 
		public void test5()
		{
			assertEquals(0.30853753872, Gaussian.Phi2(1, 3, 4), 0.00001);
			assertEquals(1, Gaussian.PhiInverse(Gaussian.Phi2(1)),0.00001);
		}
	 /*
	 *   Test case number: 6
	 *   Test case values: Phi2(0, 3, 4) , Phi2 (0)
	 *   Expected output (Post-state): 0.2266273523768, 0
	 */ 
		@Test 
		public void test6()
		{
			assertEquals(0.2266273523768, Gaussian.Phi2(0, 3, 4), 0.00001);
			assertEquals(0, Gaussian.PhiInverse(Gaussian.Phi2(0)),0.00001);
		}	
	 /*
	 *   Test case number: 7
	 *   Test case values: Phi2(1, 0, 4) 
	 *   Expected output (Post-state): 0.59870632568
	 */ 
		@Test 
		public void test7()
		{
			assertEquals(0.59870632568, Gaussian.Phi2(1, 0, 4), 0.00001);
		}
	 /*
	 *   Test case number: 8
	 *   Test case values: Phi2(1, -3, 4) 
	 *   Expected output (Post-state): 0.84134474606
	 */ 
		@Test 
		public void test8()
		{
			assertEquals(0.84134474606, Gaussian.Phi2(1, -3, 4), 0.00001);
		}
	 /*
	 *   Test case number: 9
	 *   Test case values: Phi2(1, 3, 0) 
	 *   Expected output (Post-state): 0
	 */ 
		@Test 
		public void test9()
		{
			assertEquals(0, Gaussian.Phi2(1, 3, 0), 0.00001);
		}
	 /*
	 *   Test case number: 10
	 *   Test case values: Phi2(1, 3, -4) 
	 *   Expected output (Post-state): 0.69146246127
	 */ 
		@Test 
		public void test10()
		{
			assertEquals(0.69146246127, Gaussian.Phi2(1, 3, -4), 0.00001);
			
		}
	 /*
	 *   Test case number: 11
	 *   Test case values: PhiInverse (-2)
	 *   Expected output (Post-state): -8
	 */ 	
		@Test
		public void test11()
		{
			assertEquals(-8, Gaussian.PhiInverse(-2),0.00001);
		}
	  
	}