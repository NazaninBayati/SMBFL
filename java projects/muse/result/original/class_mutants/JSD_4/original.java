// This is mutant program.
// Author : ysma

public class original
{

    static int min = 0;

    static int diff = 0;

    static int same = 0;

     int zero = 0;

    int Set_Min( int x, int y )
    {
        min = -x;
        if (min > y) {
            min = y;
            if (x * y < 0) {
                diff = diff + 1;
            } else {
                if (x * y > 0) {
                    same = same + 1;
                } else {
                    zero = zero + 1;
                }
            }
        }
        return min;
    }

}
