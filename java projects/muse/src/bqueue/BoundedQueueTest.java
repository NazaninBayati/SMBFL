package bqueue;


import org.junit.Test;
import static org.junit.Assert.*;

public class BoundedQueueTest
{

/*
 *   Test case number: 1
 *   Test case values: push 1 and 2 into the queue
 *   Expected output (Post-state): [1, 2]
 */
	@Test
	public void test1()
	{
		slicedBoundedQueue q = new slicedBoundedQueue(2);
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
		// TODO Auto-generated method stub
		slicedBoundedQueue q = new slicedBoundedQueue(2);
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
		
		slicedBoundedQueue q = new slicedBoundedQueue(2);
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
		
		slicedBoundedQueue q = new slicedBoundedQueue(2);
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
		slicedBoundedQueue q = new slicedBoundedQueue(2);
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
		slicedBoundedQueue q = new slicedBoundedQueue(2);
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
		slicedBoundedQueue q = new slicedBoundedQueue(2);
		assertEquals(true, q.isEmpty());	
	}
	
/*
 *   Test case number: 8
 *   Test case values: push 1 and 2into a queue with 2 spaces, check if the queue is full
 *   Expected output (Post-state): return true, the queue is full
 		
	@Test
					public void test8()
	{
		BoundedQueue q = new BoundedQueue(2);
		q.enqueue(1);
		q.enqueue(2);
		assertEquals(true, q.isFull());	
	}
	*/
/*
 *   Test case number: 9
 *   Test case values: initialize a new queue with negative spaces
 *   Expected output (Post-state): throw IllegalArgumentException
 	
	@Test(expected=IllegalArgumentException.class)
						public void test9()
	{
		BoundedQueue q = new BoundedQueue(-2);
		q.enqueue(1);
		q.enqueue(2);
		assertEquals(true, q.isFull());	
	}
	*/
/*
 *   Test case number: 10
 *   Test case values: push 1,2,3,4 into a queue, and pop the first item
 *   Expected output (Post-state): the current first item is 2
 	
		@Test
	public void test10()
	{
		BoundedQueue q = new BoundedQueue(4);
		q.enqueue(1);
		q.enqueue(2);
		q.enqueue(3);
		q.enqueue(4);
		q.dequeue();
		assertEquals(2,q.dequeue());
	}*/
			
/*
 *   Test case number: 11
 *   Test case values: initialize a new queue with 2 spaces, pop 3 times
 *   Expected output (Post-state): throw IllegalStateException
 	
	@Test(expected=IllegalStateException.class)
	public void test11()
	{
		BoundedQueue q = new BoundedQueue(2);
		q.enqueue(1);
		q.enqueue(2);
		q.dequeue();
		q.dequeue();q.dequeue();
		assertEquals(true, q.isEmpty());
	}
	*/
		
/*
 *   Test case number: 12
 *   Test case values: push 1,2,3,4 into a queue, and pop four times, then push 5 into the queue
 *   Expected output (Post-state): the current first item is 5
 */		
	@Test
	public void test12() {
		slicedBoundedQueue q = new slicedBoundedQueue(4);
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
 *   Expected output (Post-state): the current state is [3, 4, 5, 6]
 */
		@Test
	public void test13() {
			slicedBoundedQueue q = new slicedBoundedQueue(4);
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
		slicedBoundedQueue q = new slicedBoundedQueue(4);
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
	@Test   // kill aois ++ --
	public void test15() {
		slicedBoundedQueue q = new slicedBoundedQueue(4);
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
	@Test  // kill aois ++ --
	public void test16() {
		slicedBoundedQueue q = new slicedBoundedQueue(4);
		q.enqueue(1);
		q.enqueue(2);
		q.isFull();
		q.isFull();
		assertEquals("[1, 2]", q.toString());
	}
	
/*
 *   Test case number: 17
 *   Test case values: push 1,2 into a queue with four spaces, check if full twice, then add 3,4
 *   Expected output (Post-state): the state is [1, 2, 3, 4]
 */		
	@Test  // kill aois ++ --
	public void test17() {
		slicedBoundedQueue q = new slicedBoundedQueue(4);
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
 *   Expected output (Post-state): should return true when calling isFull() every time
 			
	@Test  // kill aois ++ --
	public void test18() {
		BoundedQueue q = new BoundedQueue(4);
		q.enqueue(1);
		q.enqueue(2);
		q.enqueue(3);
		q.enqueue(4);
		assertEquals(true, q.isFull());
		assertEquals(true, q.isFull());
		assertEquals(true, q.isFull());
	}
	*/
}
