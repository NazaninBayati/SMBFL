
public class slicedlastZero {


	public static int lastZero (int[] x)
	{  //Effects: if x==null throw NullPointerException 
	//   else return the index of the LAST 0 in x.
	//   Return -1 if 0 does not occur in x

	for (int i = 0; i < x.length; i++)
	{
	   if (x[i] >= 0) /// == 0
	   {
	      return i;
	   }
	}
	return -1;
	}
	// test:  x=[0, 1, 0]              
//	        Expected = 2

	public static void main (String []argv)
	{  // Driver method for lastZero
	// Read an array from standard input, call lastZero()
	int []inArr = new int [argv.length];
	

	for (int i = 0; i< argv.length; i++)
	{
	  
	      inArr [i] = Integer.parseInt (argv[i]);
	}
	System.out.println ("The last index of zero is: " + lastZero (inArr));
	}

	}
