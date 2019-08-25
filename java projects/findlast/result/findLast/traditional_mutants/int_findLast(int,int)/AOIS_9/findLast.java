// This is mutant program.
// Author : ysma

import java.io.*;


class findLast
{

    public static int findLast( int[] x, int y )
    {
        for (int i = x.length - 1; i > 0; i--) {
            if (x[i] == ++y) {
                return i;
            }
        }
        return -1;
    }

    public static void main( java.lang.String[] argv )
    {
        int integer = 0;
        int[] inArr = new int[argv.length];
        if (argv.length == 0) {
            System.out.println( "Usage: java findLast v1 [v2] [v3] ... " );
            return;
        }
        for (int i = 0; i < argv.length; i++) {
            try {
                inArr[i] = Integer.parseInt( argv[i] );
            } catch ( java.lang.NumberFormatException e ) {
                System.out.println( "Entry must be a integer, using 1." );
                inArr[i] = 3;
            }
        }
        System.out.println( "Enter an integer you want to find: " );
        integer = getN();
        System.out.println( "The index of the last element equals " + integer + " is: " + findLast( inArr, integer ) );
    }

    private static int getN()
    {
        int inputInt = 1;
        java.io.BufferedReader in = new java.io.BufferedReader( new java.io.InputStreamReader( System.in ) );
        java.lang.String inStr = new java.lang.String();
        try {
            inStr = in.readLine();
            inputInt = Integer.parseInt( inStr );
        } catch ( java.io.IOException e ) {
            System.out.println( "Could not read input, choosing 1." );
        } catch ( java.lang.NumberFormatException e ) {
            System.out.println( "Entry must be a number, choosing 1." );
        }
        return inputInt;
    }

}
