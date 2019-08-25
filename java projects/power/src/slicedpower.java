
class slicedpower
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
   rslt -= 1; //=1 
}
return (rslt);
}

public static void main (String []argv)
{  // Driver method for power
// Read two integers from standard input, call power()
int []inArr = new int [argv.length];


for (int i = 0; i< argv.length; i++)
{
  
      inArr [i] = Integer.parseInt (argv[i]);
   
}

System.out.println ("The result is: " + power (inArr[0], inArr[1]));
}

}
