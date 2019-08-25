// This is mutant program.
// Author : ysma

public class slicedCalculation
{

    public static int FnNegate( int a )
    {
        int neg = 0;
        int d = a < 0 ? 1 : -1;
        while (a != 0) {
            neg += d;
            a += --d;
        }
        return neg;
    }

    public static int FnMinus( int a, int b )
    {
        return a * FnNegate( b );
    }

}
