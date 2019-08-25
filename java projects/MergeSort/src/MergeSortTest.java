import static org.junit.Assert.assertEquals;
import org.junit.Test;


public class MergeSortTest {

	/*
	 *   Test case number: 1
	 *   Pre-condition: given integer array {1,4,3,2,5,6} 
	 *   Test case values: sort it using mergeSort
	 *   Expected output (Post-state): 1 2 3 4 5 6
	 */	
	
		@Test
	  public void test1 ()
	  {
			int [] list = {1,4,3,2,5,6};
			String result = new String();
			MergeSort.mergeSort(list);
			for(int i = 0; i<list.length; i ++)
				result = result + " " + list[i];
			assertEquals(" 1 2 3 4 5 6", result);
	  }
	/*
	 *   Test case number: 2
	 *   Pre-condition: given integer array { } 
	 *   Test case values: sort it using mergeSort
	 *   Expected output (Post-state): “”
	 */	
	  	@Test
	  public void test2 ()
	  {
			int [] list = { };
			String result = new String();
			MergeSort.mergeSort(list);
			for(int i = 0; i<list.length; i ++)
				result = result + " " + list[i];
			assertEquals("", result);
	  }
	/*
	 *   Test case number: 3
	 *   Pre-condition: given integer array {8,7,6,5,4,3,2,1} 
	 *   Test case values: sort it using mergeSort
	 *   Expected output (Post-state): 1 2 3 4 5 6 7 8
	 */	  
	  	@Test
		  public void test3 ()
		  {
				int [] list = {8,7,6,5,4,3,2,1};
				String result = new String();
				MergeSort.mergeSort(list);
				for(int i = 0; i<list.length; i ++)
					result = result + " " + list[i];
				assertEquals(" 1 2 3 4 5 6 7 8", result);
		  }
		  
//		@Test
	//   public void test2 ()
	//  {
//			int [] list = {1,4,3,2,5,6};
//			int [] list2 = {5,4,3,2,1,6};
//			int [] temp = new int[12];
//			String result = new String();
//			MergeSort.merge(list, list2, temp);
//			for(int i = 0; i<temp.length; i ++)
//				result = result + " " + temp[i];
//			assertEquals("", result);
	//  }
	}////////////////////////////////////////till test 10 