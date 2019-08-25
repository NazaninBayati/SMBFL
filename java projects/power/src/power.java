
class power
{

public static int power (int left, int right)
{
//**************************************
// Raises Left to the power of Right
// precondition : Right >= 0
// postcondition: Returns Left**Right
//**************************************
int rslt = 0;
rslt = left;
if (right == 0)
{
   rslt = 1; /// =1
}
else
{
   for (int i = 2; i <= right; i++)
      rslt = rslt * left;
}
return (rslt);
}

public static void main (String []argv)
{  // Driver method for power
// Read two integers from standard input, call power()
int []inArr = new int [argv.length];
if (argv.length != 2)
{
   System.out.println ("Usage: java power v1 v2");
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
if (inArr[1] < 0)
{
    System.out.println("Right must be large or equal to 0, using 1.");
    inArr[1] = 1;
}

System.out.println ("The result is: " + power (inArr[0], inArr[1]));
}

}
