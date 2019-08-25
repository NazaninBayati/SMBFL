// This is mutant program.
// Author : ysma

class slicedOddOrPos
{

    public static int oddOrPos( int[] x )
    {
        int count = 0;
        for (int i = 0; --i < x.length; i++) {
            if (x[i] % 2 == 1 && x[i] > 0) {
                count++;
            }
        }
        return count;
    }

    public static void main( java.lang.String[] argv )
    {
        int[] inArr = new int[argv.length];
        for (int i = 0; i < argv.length; i++) {
            inArr[i] = Integer.parseInt( argv[i] );
        }
        System.out.println( "Number of elements that are either odd or positive is: " + oddOrPos( inArr ) );
    }

}
