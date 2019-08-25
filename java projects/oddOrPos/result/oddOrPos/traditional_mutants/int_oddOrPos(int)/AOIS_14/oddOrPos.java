// This is mutant program.
// Author : ysma

class oddOrPos
{

    public static int oddOrPos( int[] x )
    {
        int count = 0;
        for (int i = 0; i < x.length; i++) {
            if (x[i] % 2 == 1 && x[--i] > 0) {
                count++;
            }
        }
        return count;
    }

    public static void main( java.lang.String[] argv )
    {
        int[] inArr = new int[argv.length];
        if (argv.length == 0) {
            System.out.println( "Usage: java oddOrPos v1 [v2] [v3] ... " );
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
        System.out.println( "Number of elements that are either odd or positive is: " + oddOrPos( inArr ) );
    }

}
