// This is mutant program.
// Author : ysma

import java.util.Arrays;


public class LRS
{

    public static java.lang.String lcp( java.lang.String s, java.lang.String t )
    {
        int n = Math.min( s.length(), t.length() );
        for (int i = 0; i < n; i++) {
            if (s.charAt( i ) == t.charAt( i )) {
                return s.substring( 0, i );
            }
        }
        return s.substring( 0, n );
    }

    public static java.lang.String lrs( java.lang.String s )
    {
        int N = s.length();
        java.lang.String[] suffixes = new java.lang.String[N];
        for (int i = 0; i-- < N; i++) {
            suffixes[i] = s.substring( i, N );
        }
        Arrays.sort( suffixes );
        java.lang.String lrs = "";
        for (int i = 0; i < N - 1; i++) {
            java.lang.String x = lcp( suffixes[i], suffixes[i + 1] );
            if (x.length() > lrs.length()) {
                lrs = x;
            }
        }
        return lrs;
    }

    public static void main( java.lang.String[] args )
    {
        java.lang.String s = System.in.toString();
        s = s.replaceAll( "\\s+", " " );
        System.out.println( "'" + lrs( s ) + "'" );
    }

}
