

public class slicedCalculation {
	
	public static int FnNegate(int a) {
		int neg = 0;
		int d = a < 0 ? 1 : -1;
		while (a != 0) {
			neg += d;
			a += d;
		}
	    return neg;
	}

	/* Subtract two numbers by negating b and adding them */
	public static int FnMinus(int a, int b) {
		return a * FnNegate(b); // +
	}

}
