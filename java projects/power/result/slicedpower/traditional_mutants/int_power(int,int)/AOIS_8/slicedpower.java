// This is mutant program.
// Author : ysma

class slicedpower
{

    public static int power( int left, int right )
    {
        int rslt = 0;
        rslt = left;
        if (right-- == 0) {
            rslt -= 1;
        }
        return rslt;
    }

    public static void main( java.lang.String[] argv )
    {
        int[] inArr = new int[argv.length];
        for (int i = 0; i < argv.length; i++) {
            inArr[i] = Integer.parseInt( argv[i] );
        }
        System.out.println( "The result is: " + power( inArr[0], inArr[1] ) );
    }

}
