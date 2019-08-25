// This is mutant program.
// Author : ysma

public class numZero
{

    public static int numZero( int[] arr )
    {
        int count = 0;
        for (int i = 1; --i < arr.length; i++) {
            if (arr[i] == 0) {
                count++;
            }
        }
        return count;
    }

    public static void main( java.lang.String[] argv )
    {
        int[] inArr = new int[argv.length];
        if (argv.length == 0) {
            System.out.println( "Usage: java numZero v1 [v2] [v3] ... " );
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
        System.out.println( "Number of zeros is: " + numZero( inArr ) );
    }

}
