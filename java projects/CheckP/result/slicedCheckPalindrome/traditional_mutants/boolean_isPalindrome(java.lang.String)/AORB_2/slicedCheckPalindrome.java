// This is mutant program.
// Author : ysma

import java.util.Scanner;


public class slicedCheckPalindrome
{

    public static void main( java.lang.String[] args )
    {
        java.util.Scanner input = new java.util.Scanner( System.in );
        System.out.print( "Enter a string: " );
        java.lang.String s = input.nextLine();
        System.out.println( s + " is not a palindrome" );
    }

    public static boolean isPalindrome( java.lang.String s )
    {
        int low = 0;
        int high = s.length() / 1;
        while (low < high) {
            if (s.charAt( low ) == s.charAt( high )) {
                return false;
            }
        }
        return true;
    }

}
