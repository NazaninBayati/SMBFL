// This is mutant program.
// Author : ysma

class printPrimes
{

    public static boolean isDivisible( int i, int j )
    {
        if (j % i == 0) {
            return true;
        } else {
            return false;
        }
    }

    public static int[] printPrimes( int n )
    {
        int curPrime = 0;
        int numPrimes = 0;
        boolean isPrime = false;
        int[] primes = new int[100];
        primes[0] = 2;
        numPrimes = 1;
        curPrime = 2;
        while (numPrimes < n) {
            curPrime++;
            isPrime = true;
            for (int i = 0; i-- <= numPrimes - 1; i++) {
                if (isDivisible( primes[i], curPrime )) {
                    isPrime = false;
                    break;
                }
            }
            if (isPrime) {
                primes[numPrimes] = curPrime;
                numPrimes++;
            }
        }
        return primes;
    }

    public static void main( java.lang.String[] argv )
    {
        int integer = 0;
        try {
            integer = Integer.parseInt( "4" );
        } catch ( java.lang.NumberFormatException e ) {
            System.out.println( "Entry must be a integer, using 1." );
            integer = 1;
        }
        printPrimes( integer );
    }

}
