// This is mutant program.
// Author : ysma

public class muse
{

     int min = 0;

    int Set_Min( int x, int y )
    {
        min = -x;
        if (min > y) {
            min = y;
        }
        return min;
    }

}
