

import org.junit.Test;
import static org.junit.Assert.*;

public class QueueTest
{

/*
*   Test case number: 1
*   Test case values: push 1 and 2 into the queue
*   Expected output (Post-state): [1, 2]
*/
	@Test
	public void test1()
	{
		Queue q = new Queue();
		q.enqueue(1);
		q.enqueue(2);
		//Object i = q.dequeue();
		assertEquals("[1, 2]",q.toString());
	}

/*
*   Test case number: 2
*   Test case values: push 1 and 2 into the queue, then pop the first item
*   Expected output (Post-state): [2]
*/
	@Test
		public void test2()
	{
		Queue q = new Queue();
		q.enqueue(1);
		q.enqueue(2);
		Object i = q.dequeue();
		assertEquals("[2]",q.toString());
		
	}
	
/*
*   Test case number: 3
*   Test case values: push 1, 2 and 3 into a queue with 2 spaces
*   Expected output (Post-state): throw IllegalStateException
*/
	@Test (expected=IllegalStateException.class)
			public void test3()
	{
		Queue q = new Queue();
		q.enqueue(1);
		q.enqueue(2);
		q.enqueue(3);
		Object i = q.dequeue();
		Object j = q.dequeue();
		Object k = q.dequeue();
		assertEquals("[2]",q.toString());
	}

/*
*   Test case number: 4
*   Test case values: push null value into the queue
*   Expected output (Post-state): throw NullPointerException
*/
	@Test (expected=NullPointerException.class)
	public void test4()
	{
		Queue q = new Queue();
		q.enqueue(null);

		assertEquals("[2]",q.toString());
	}
	
/*
*   Test case number: 5
*   Test case values: push 1 into a queue with 2 spaces, check if the queue is full
*   Expected output (Post-state): return false, the queue is not full
*/
	@Test
		public void test5()
	{
		Queue q = new Queue();
		q.enqueue(1);
		assertEquals(false, q.isFull());	
	}
	
/*
*   Test case number: 6
*   Test case values: push 1 into a queue with 2 spaces, check if the queue is empty
*   Expected output (Post-state): return false, the queue is not empty
*/	
	@Test
			public void test6()
	{
		Queue q = new Queue();
		q.enqueue(1);
		assertEquals(false, q.isEmpty());	
	}

	
/*
*   Test case number: 7
*   Test case values: initialize a new queue, check if the queue is empty
*   Expected output (Post-state): return true, the queue is empty
*/	
	@Test
				public void test7()
	{
		Queue q = new Queue();
		assertEquals(true, q.isEmpty());	
	}
	
/*
*   Test case number: 8
*   Test case values: push 1 and 2into a queue with 2 spaces, check if the queue is full
*   Expected output (Post-state): return true, the queue is full
*/		
	@Test
	public void test8() {
		Queue q = new Queue();
		q.enqueue(1);
		q.enqueue(2);
		assertEquals(true, q.isFull());
	}
	/*
	 *   Test case number: 9
	 *   Test case values: push 1 and 2into a queue with 2 spaces, check if the queue is full
	 *   Expected output (Post-state): return true, the queue is full
	 */		
	@Test
	public void test9() {
		Queue q = new Queue();
		q.enqueue(1);
		q.enqueue(2);
		q.dequeue();
		q.enqueue(3);
		assertEquals("[2, 3]", q.toString());
	}
/*
*   Test case number: 10
*   Test case values: push 1,2,3,4 into a queue, and pop the first item
*   Expected output (Post-state): throw IllegalStateException
*/	
		@Test(expected=IllegalStateException.class)
	public void test10()
	{
			Queue q = new Queue();
		q.enqueue(1);
		q.enqueue(2);
		q.enqueue(3);
		q.enqueue(4);
		q.dequeue();
		assertEquals(2,q.dequeue());
	}
			
/*
*   Test case number: 11
*   Test case values: initialize a new queue with 2 spaces, pop 3 times
*   Expected output (Post-state): throw IllegalStateException
*/	
	@Test(expected=IllegalStateException.class)
	public void test11()
	{
		Queue q = new Queue();
		q.enqueue(1);
		q.enqueue(2);
		q.dequeue();
		q.dequeue();q.dequeue();
		assertEquals(true, q.isEmpty());
	}
	
		
/*
*   Test case number: 12
*   Test case values: push 1,2,3,4 into a queue, and pop four times, then push 5 into the queue
*   Expected output (Post-state): throw IllegalStateException
*/		
	@Test(expected=IllegalStateException.class)
	public void test12() {
		Queue q = new Queue();
		q.enqueue(1);
		q.enqueue(2);
		q.enqueue(3);
		q.enqueue(4);
		q.dequeue();q.dequeue();q.dequeue();q.dequeue();
		q.enqueue(5);
		assertEquals(5, q.dequeue());
	}

/*
*   Test case number: 13
*   Test case values: push 1,2,3,4 into a queue, and pop two times, then push 5,6 into the queue
*   Expected output (Post-state): throw IllegalStateException
*/
		@Test(expected=IllegalStateException.class)
	public void test13() {
			Queue q = new Queue();
		q.enqueue(1);
		q.enqueue(2);
		q.enqueue(3);
		q.enqueue(4);
		q.dequeue();q.dequeue();
		q.enqueue(5);
		q.enqueue(6);
		assertEquals("[3, 4, 5, 6]", q.toString());
	}
	
		
/*
*   Test case number: 14
*   Test case values: push 1,2,3,4,5,6 into a queue with four spaces
*   Expected output (Post-state): throw IllegalStateException
*/	
	
	@Test(expected=IllegalStateException.class)
	public void test14() {
		Queue q = new Queue();
		q.enqueue(1);
		q.enqueue(2);
		q.enqueue(3);
		q.enqueue(4);
		q.enqueue(5);
		q.enqueue(6);
		assertEquals("[3, 4, 5, 6]", q.toString());
	}
		
/*
*   Test case number: 15
*   Test case values: push 1,2 into a queue with four spaces, check if empty twice
*   Expected output (Post-state): the state is [1, 2]
*/	
	@Test   
	public void test15() {
		Queue q = new Queue();
		q.enqueue(1);
		q.enqueue(2);
		q.isEmpty();
		q.isEmpty();
		assertEquals("[1, 2]", q.toString());
	}
	
/*
*   Test case number: 16
*   Test case values: push 1,2 into a queue with four spaces, check if full twice
*   Expected output (Post-state): the state is [1, 2]
*/		
	@Test  
	public void test16() {
		Queue q = new Queue();
		q.enqueue(1);
		q.enqueue(2);
		q.isFull();
		q.isFull();
		assertEquals("[1, 2]", q.toString());
	}
	
/*
*   Test case number: 17
*   Test case values: push 1,2 into a queue with four spaces, check if full twice, then add 3,4
*   Expected output (Post-state): throw IllegalStateException
*/		
	@Test  (expected=IllegalStateException.class)
	public void test17() {
		Queue q = new Queue();
		q.enqueue(1);
		q.enqueue(2);
		q.isFull();
		q.isFull();
		q.enqueue(3);
		q.enqueue(4);
		assertEquals("[1, 2, 3, 4]", q.toString());
	}
	
	
/*
*   Test case number: 18
*   Test case values: push 1,2,3,4 into a queue with four spaces, check if full three times
*   Expected output (Post-state): throw IllegalStateException
*/			
	@Test (expected=IllegalStateException.class) 
	public void test18() {
		Queue q = new Queue();
		q.enqueue(1);
		q.enqueue(2);
		q.enqueue(3);
		q.enqueue(4);
		assertEquals(true, q.isFull());
		assertEquals(true, q.isFull());
		assertEquals(true, q.isFull());
	}
	
/*
*   Test case number: 19
*   Test case values: push 1,2 then pop the first, then push 3, then pop all
*   Expected output (Post-state): empty queue
*/	
	@Test
	public void test19() {
		Queue q = new Queue();
		q.enqueue(1); 
		q.enqueue(2); 
		q.dequeue(); 
		q.enqueue(3); 
		q.dequeue(); 
		q.dequeue();assertEquals("[]", q.toString());
	}
	
}
