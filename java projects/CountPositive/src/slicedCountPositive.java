
public class slicedCountPositive {


	public static int countPositive (int[] x)
	{  //Effects: If x==null throw NullPointerException
	   //   else return the number of
	   //        positive elements in x.
	   int count = 0;

	   for (int i=0; i < x.length; i++)
	   {
	      if (x[i] > 0) //>=
	      {
	         count++;
	      }
	   }
	   return count;
	}
	   // test:  x=[-4, 2, 0, 2]             
	   //        Expected = 2

	public static void main (String []argv)
	{  // Driver method for countPositive
	   // Read an array from standard input, call countPositive()
	   int []inArr = new int [argv.length];
	   

	   System.out.println ("Number of positive numbers is: " + countPositive (inArr));
	}



}
