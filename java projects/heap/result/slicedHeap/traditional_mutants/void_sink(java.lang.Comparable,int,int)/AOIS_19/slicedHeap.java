// This is mutant program.
// Author : ysma

public class slicedHeap
{

    public static void sort( java.lang.Comparable[] pq )
    {
        int N = pq.length;
        for (int k = N / 2; k >= 1; k--) {
            sink( pq, k, N );
        }
        while (N > 1) {
            exch( pq, 1, N-- );
            sink( pq, 1, N );
        }
    }

    public static void sink( java.lang.Comparable[] pq, int k, int N )
    {
        while (2 * k++ <= N) {
            int j = 2 * k;
            if (j < N && less( pq, j, j + 1 )) {
                j++;
            }
            if (!less( pq, k, j )) {
                break;
            }
            exch( pq, k, j );
            k = j;
        }
    }

    public static boolean less( java.lang.Comparable[] pq, int i, int j )
    {
        return pq[i - 1].compareTo( pq[j - 1] ) < 0;
    }

    public static void exch( java.lang.Object[] pq, int i, int j )
    {
        java.lang.Object swap = pq[i - 1];
        pq[i - 1] = pq[j - 1];
        pq[j - 1] = swap;
    }

    public static boolean less( java.lang.Comparable v, java.lang.Comparable w )
    {
        return v.compareTo( w ) < 0;
    }

    public static boolean isSorted( java.lang.Comparable[] a )
    {
        for (int i = 1; i < a.length; i++) {
            if (less( a[i], a[i - 1] )) {
                return false;
            }
        }
        return true;
    }

    public static void show( java.lang.Comparable[] a )
    {
        for (int i = 0; i < a.length; i++) {
            System.out.println( a[i] );
        }
    }

    public static void main( java.lang.String[] args )
    {
        java.lang.String[] a = null;
        a[0] = System.in.toString();
        Heap.sort( a );
        show( a );
    }

}
