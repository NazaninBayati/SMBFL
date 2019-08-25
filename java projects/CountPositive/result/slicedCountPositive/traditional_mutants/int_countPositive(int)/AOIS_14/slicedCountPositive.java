// This is mutant program.
// Author : ysma

public class slicedCountPositive
{

    public static int countPositive( int[] x )
    {
        int count = 0;
        for (int i = 0; i < x.length; i++) {
            if (x[i] > 0) {
                count++;
            }
        }
        return count--;
    }

    public static void main( java.lang.String[] argv )
    {
        int[] inArr = new int[argv.length];
        System.out.println( "Number of positive numbers is: " + countPositive( inArr ) );
    }

}
