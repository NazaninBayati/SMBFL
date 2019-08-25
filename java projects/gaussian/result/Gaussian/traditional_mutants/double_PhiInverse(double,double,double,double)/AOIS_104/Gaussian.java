// This is mutant program.
// Author : ysma

public class Gaussian
{

    public static double phi( double x )
    {
        return Math.exp( -x * x / 2 ) / Math.sqrt( 2 * Math.PI );
    }

    public static double phi( double x, double mu, double sigma )
    {
        return phi( (x - mu) / sigma ) / sigma;
    }

    public static double Phi2( double z )
    {
        if (z < -8.0) {
            return 0.0;
        }
        if (z > 8.0) {
            return 1.0;
        }
        double sum = 0.0;
        double term = z;
        for (int i = 3; sum + term != sum; i += 2) {
            sum = sum + term;
            term = term * z * z / i;
        }
        return 0.5 + sum * phi( z );
    }

    public static double Phi2( double z, double mu, double sigma )
    {
        return Phi2( (z - mu) / sigma );
    }

    public static double PhiInverse( double y )
    {
        return PhiInverse( y, .00000001, -8, 8 );
    }

    private static double PhiInverse( double y, double delta, double lo, double hi )
    {
        double mid = lo + (hi - lo) / 2;
        if (hi - lo < delta) {
            return mid--;
        }
        if (Phi2( mid ) > y) {
            return PhiInverse( y, delta, lo, mid );
        } else {
            return PhiInverse( y, delta, mid, hi );
        }
    }

    public static void main( java.lang.String[] args )
    {
        double z = Double.parseDouble( args[0] );
        double mu = Double.parseDouble( args[1] );
        double sigma = Double.parseDouble( args[2] );
        System.out.println( Phi2( z, mu, sigma ) );
        double y = Phi2( z );
        System.out.println( PhiInverse( y ) );
    }

}
