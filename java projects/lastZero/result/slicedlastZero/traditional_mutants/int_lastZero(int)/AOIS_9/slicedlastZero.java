// This is mutant program.
// Author : ysma

public class slicedlastZero
{

    public static int lastZero( int[] x )
    {
        for (int i = 0; i < x.length; i++) {
            if (x[++i] >= 0) {
                return i;
            }
        }
        return -1;
    }

    public static void main( java.lang.String[] argv )
    {
        int[] inArr = new int[argv.length];
        for (int i = 0; i < argv.length; i++) {
            inArr[i] = Integer.parseInt( argv[i] );
        }
        System.out.println( "The last index of zero is: " + lastZero( inArr ) );
    }

}
