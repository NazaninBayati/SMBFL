
import java.io.*;

class findLast
{

public static int findLast (int[] x, int y)
{  //Effects: If x==null throw NullPointerException
   //   else return the index of the last element
   //   in x that equals y.
   //   If no such element exists, return -1

   // As the example in the book points out, this loop should end at 0.
   for (int i=x.length-1; i > 0; i--)
   {
      if (x[i] == y) 
      {
         return i;
      }
   }
   return -1;
}
   // test:  x=[2, 3, 5]; y = 2              
   //        Expected = 0

public static void main (String []argv)
{  // Driver method for findLast
   // Read an array and an integer from standard input, call findLast()
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
         inArr [i] = 3;//= 1
      }
   }
   System.out.println ("Enter an integer you want to find: ");
   integer = getN();

   System.out.println ("The index of the last element equals " + integer + " is: " + findLast (inArr, integer));
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