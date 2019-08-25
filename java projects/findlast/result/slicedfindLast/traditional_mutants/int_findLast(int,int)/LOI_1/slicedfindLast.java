// This is mutant program.
// Author : ysma

import java.io.*;


class slicedfindLast
{

    public static int findLast( int[] x, int y )
    {
        for (int i = x.length - 1; ~i > 0; i--) {
            if (x[i] == y) {
                return i;
            }
        }
        return -1;
    }

    public static void main( java.lang.String[] argv )
    {
        int integer = 0;
        int[] inArr = new int[argv.length];
        for (int i = 0; i < argv.length; i++) {
            inArr[i] = 3;
        }
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
