

//Introduction to Software Testing
//Authors: Paul Ammann & Jeff Offutt
//Chapter 1, section 1.2, page 16

class slicedOddOrPos
{

public static int oddOrPos (int[] x)
{  // Effects:  if x is null throw NullPointerException
// else return the number of elements in x that
//      are either odd or positive (or both)
int count = 0;

for (int i = 0; i < x.length; i++)
{
   if (x[i]%2 == 1 && x[i] > 0) //// ||
   {
      count++;
   }
}
return count;
}
// test:  x=[-3, -2, 0, 1, 4]           
//        Expected = 3

public static void main (String []argv)
{  // Driver method for oddOrPos
// Read an array from standard input, call oddOrPos()
int []inArr = new int [argv.length];

for (int i = 0; i< argv.length; i++)
{
 
      inArr [i] = Integer.parseInt (argv[i]);
  
}

System.out.println ("Number of elements that are either odd or positive is: " + oddOrPos (inArr));
}

}
