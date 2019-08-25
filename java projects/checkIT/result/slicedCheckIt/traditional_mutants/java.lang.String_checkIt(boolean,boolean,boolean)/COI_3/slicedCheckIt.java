// This is mutant program.
// Author : ysma

public class slicedCheckIt
{

    public static java.lang.String checkIt( boolean a, boolean b, boolean c )
    {
        java.lang.String str = "";
        if (a || (b || !c)) {
            str = "P is true";
        }
        return str;
    }

    public static void main( java.lang.String[] argv )
    {
        boolean[] inArr = new boolean[argv.length];
        for (int i = 0; i < argv.length; i++) {
            inArr[i] = Boolean.parseBoolean( argv[i] );
        }
        checkIt( inArr[0], inArr[1], inArr[2] );
    }

}
