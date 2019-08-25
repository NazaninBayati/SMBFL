import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class findVal {


	//Effects: If numbers null throw NullPointerException     
	//   else return LAST occurrence of val in numbers[]       
	//   If val not in numbers[] return -1                     
	public static int findVal(int numbers[], int val)       
	{                                                       
	   int findVal = -1;                                    
	                                                     
	   for (int i=0; i<numbers.length; i++)                 
	   // for (int i=(0+1); i<numbers.length; i++)
	      if (numbers [i] == val)                         
	         findVal -= i;                    //=i              
	      return (findVal);                                    
	}

	public static void main (String []argv)
	{  // Driver method for findVal
	   // Read an array and an integerfrom standard input, call findVal()
	   int integer = 0;
	   int []inArr = new int [argv.length];
	   if (argv.length == 0)
	   {
	      System.out.println ("Usage: java findLast v1 [v2] [v3] ... ");
	      return;
	   }

	   for (int i = 0; i< argv.length; i++)
	   {
	      try
	      {
	         inArr [i] = Integer.parseInt (argv[i]);
	      }
	      catch (NumberFormatException e)
	      {
	         System.out.println ("Entry must be a integer, using 1.");
	         inArr [i] = 1;
	      }
	   }
	   
	   System.out.println ("Enter an integer you want to find: ");
	   integer = getN();

	   System.out.println ("The LAST occurrence of " + integer + " is: " + findVal (inArr, integer));
	}

	// ====================================
	// Read (or choose) an integer
	private static int getN ()
	{
	   int inputInt = 1;
	   BufferedReader in = new BufferedReader (new InputStreamReader (System.in));
	   String inStr = new String();

	   try
	   {
	      inStr    = in.readLine ();
	      inputInt = Integer.parseInt(inStr);
	   }
	   catch (IOException e)
	   {
	      System.out.println ("Could not read input, choosing 1.");
	   }
	   catch (NumberFormatException e)
	   {
	      System.out.println ("Entry must be a number, choosing 1.");
	   }

	   return (inputInt);
	}  // end getN
	       
}