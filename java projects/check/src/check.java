
public class check {
	int func(int a, int b, int c)
	{
		
		int max = 0;
		
		if ( a> b)
		{ 
			max = a + b/c;
			}
		else
		{
			max = b + c/a; //////max = b + a/c
		}
	
		return max;	
	}

}

