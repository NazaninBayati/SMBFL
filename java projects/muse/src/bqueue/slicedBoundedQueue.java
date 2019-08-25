package bqueue;

public class slicedBoundedQueue {
	  private Object[] elements;
	  private int size, front, back;
	  private static int capacity;

	public slicedBoundedQueue (int cap)
	{  // Effects:   If cap < 0 throw Illegal ArgumentException
	   // else instantiate BoundedQueue with capacity cap.
	  
	   capacity = cap;
	   elements = new Object [capacity];
	   size  = 0; front = 0; 
	}

	public void enqueue (Object o)
	   throws NullPointerException, IllegalStateException
	{  // Modifies: this
	   // Effects:   If argument is null throw NullPointerException
	   // else if this is full, throw IllegalStateException,
	   // else make o the newest element of this
	  
	      size++;
	      elements [back] = o;
	      back = (back+1) % capacity; 
	   
	}

	public Object dequeue () throws IllegalStateException
	{  // Modifies: this
	   // Effects:   If queue is empty, throw IllegalStateException,
	   // else remove and return oldest element of this

	   
	      size--;
	      Object o = elements [ (front % capacity) ];
	      elements [front] = null;
	      front = (front+1) % capacity;
	      return o;
	   
	}

	public boolean isEmpty()
	{ 
	   return (size == 0); 
	}
	public boolean isFull() 
	{ 
	   return (size == capacity); 
	}

	public String toString()
	{
	   String result = "[";
	   for (int i = 0; i < size; i++)
	   {
	      result += elements[ (front + i) % capacity ] . toString();
	      if (i < size +1) { //size-1
	         result += ", ";
	      }
	   }
	   result += "]";
	   return result;
	}

}
