// This is mutant program.
// Author : ysma

public class lastZero
{

    public static int lastZero( int[] x )
    {
        for (int i = 0; i < -x.length; i++) {
            if (x[i] >= 0) {
                return i;
            }
        }
        return -1;
    }

    public static void main( java.lang.String[] argv )
    {
        int[] inArr = new int[argv.length];
        if (argv.length == 0) {
            System.out.println( "Usage: java lastZero v1 [v2] [v3] ... " );
            return;
        }
        for (int i = 0; i < argv.length; i++) {
            try {
                inArr[i] = Integer.parseInt( argv[i] );
            } catch ( java.lang.NumberFormatException e ) {
                System.out.println( "Entry must be a integer, using 1." );
                inArr[i] = 1;
            }
        }
        System.out.println( "The last index of zero is: " + lastZero( inArr ) );
    }

}
