// This is mutant program.
// Author : ysma

public class slicedQuickSort
{

    public static void quickSort( int[] list )
    {
        quickSort( list, 0, list.length - 1 );
    }

    private static void quickSort( int[] list, int first, int last )
    {
        if (last > first) {
            int pivotIndex = partition( list, first, last );
            quickSort( list, first, pivotIndex - 1 );
            quickSort( list, pivotIndex + 1, last );
        }
    }

    private static int partition( int[] list, int first, int last )
    {
        int pivot = list[first];
        int low = first + 1;
        int high = last;
        if (pivot > list[--high]) {
            list[first] = list[high];
            list[high] = pivot;
            return high;
        } else {
            return first;
        }
    }

    public static void main( java.lang.String[] args )
    {
        int[] list = { 2, 3, 2, 5, 6, 1, -2, 3, 14, 12 };
        quickSort( list );
        for (int i = 0; i < list.length; i++) {
            System.out.print( list[i] + " " );
        }
    }

}
