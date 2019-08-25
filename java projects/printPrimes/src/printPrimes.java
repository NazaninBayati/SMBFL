


/** *****************************************************
* Finds and prints n prime integers
* Jeff Offutt, Spring 2003
********************************************************* */
class printPrimes
{

public static boolean isDivisible (int i, int j)
{
if (j%i == 0)
   return true;
else
   return false;
}
public static int[] printPrimes (int n)
{
int curPrime =0;           // Value currently considered for primeness
int numPrimes=0;          // Number of primes found so far.
boolean isPrime = false;        // Is curPrime prime?
int [] primes = new int [100]; // The list of prime numbers.

// Initialize 2 into the list of primes.
primes [0] = 2;
numPrimes = 1;
curPrime  = 2;
while (numPrimes < n)
{
   curPrime--;  // next number to consider ... /////////// ++
   isPrime = true;
   for (int i = 0; i <= numPrimes-1; i++)
   {   // for each previous prime.
      if (isDivisible (primes[i], curPrime))
      {  // Found a divisor, curPrime is not prime.
         isPrime = false;
         break; // out of loop through primes.
      }
   }
   if (isPrime)
   {   // save it!
      primes[numPrimes] = curPrime;
      numPrimes++;
   }
}  // End while

// Print all the primes out.
//for (int i = 0; i <= numPrimes-1; i++)
//{
//   System.out.println ("Prime: " + primes[i]);
//}

return primes;
}  // end printPrimes

public static void main (String []argv)
{  // Driver method for printPrimes
// Read an integer from standard input, call printPrimes()
int integer = 0;
//if (argv.length != 1)
//{
//   System.out.println ("Usage: java printPrimes v1 ");
//   return;
//}

try
{
   integer = Integer.parseInt ("4");
}
catch (NumberFormatException e)
{
   System.out.println ("Entry must be a integer, using 1.");
   integer = 1;
}

printPrimes (integer);
}

}
