// This is mutant program.
// Author : ysma

public class Calculation
{

    public static int FnNegate( int a )
    {
        int neg = 0;
        int d = a < 0 ? 1 : -1;
        while (a != 0) {
            neg += d;
            a *= d;
        }
        return neg;
    }

    public static int FnMinus( int a, int b )
    {
        return a * FnNegate( b );
    }

    public static boolean DifferentSigns( int a, int b )
    {
        return a < 0 && b > 0 || a > 0 && b < 0 ? true : false;
    }

    public static int abs( int a )
    {
        if (a < 0) {
            return FnNegate( a );
        } else {
            return a;
        }
    }

    public static int FnTimes( int a, int b )
    {
        if (a < b) {
            return FnTimes( b, a );
        }
        int sum = 0;
        for (int iter = abs( b ); iter > 0; --iter) {
            sum += a;
        }
        if (b < 0) {
            sum = FnNegate( sum );
        }
        return sum;
    }

    public static int DefineAndRoundFraction( int a, int b )
    {
        if (FnTimes( abs( a ), 2 ) >= abs( b )) {
            return 1;
        } else {
            return 0;
        }
    }

    public static int FnDivide( int a, int b )
        throws java.lang.ArithmeticException
    {
        if (b == 0) {
            throw new java.lang.ArithmeticException( "ERROR: Divide by zero." );
        }
        int quotient = 0;
        int divisor = FnNegate( abs( b ) );
        int divend;
        for (divend = abs( a ); divend >= abs( divisor ); divend += divisor) {
            ++quotient;
        }
        if (DifferentSigns( a, b )) {
            quotient = FnNegate( quotient );
        }
        return quotient;
    }

}
