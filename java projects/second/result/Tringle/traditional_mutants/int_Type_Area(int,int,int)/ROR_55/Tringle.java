// This is mutant program.
// Author : ysma

public class Tringle
{

    static final int INVALID = 0;

    static final int SCALENE = 1;

    static final int ISO = 2;

    static final int EQUAL = 3;

    int Type_Area( int a, int b, int c )
    {
        int type;
        if (a <= 0 || b <= 0 || c <= 0 || a + b <= c || a + c <= b || b + c <= a) {
            type = INVALID;
        } else {
            type = SCALENE;
            if (a == b || b == c || a == c) {
                type = ISO;
            }
            if (a == b && b != c) {
                type = EQUAL;
            }
        }
        return type;
    }

}
