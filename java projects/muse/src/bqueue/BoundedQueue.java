// Introduction to Software Testing, www.introsoftwaretesting.com
// Authors: Paul Ammann & Jeff Offutt
// Chapter 4, section 4.2, page 164
// Note:  BoundedQueue is a minor variation of Queue from Chapter 2
package bqueue;

public class BoundedQueue
{ // Overview:  a BoundedQueue is a mutable, bounded FIFO data structure
  // of fixed size, set in the constructor
  // A typical BoundedQueue is [], [o1], or [o1, o2], where neither o1 nor o2
  // are ever null.  Older elements are listed before newer ones.
  private Object[] elements;
  private int size, front, back;
  private static int capacity;

public BoundedQueue (int cap)
{  // Effects:   If cap < 0 throw Illegal ArgumentException
   // else instantiate BoundedQueue with capacity cap.
   if (cap < 0)
      throw new IllegalArgumentException ("BoundedQueue constructor");
   capacity = cap;
   elements = new Object [capacity];
   size  = 0; front = 0; back  = 0;
}

public void enqueue (Object o)
   throws NullPointerException, IllegalStateException
{  // Modifies: this
   // Effects:   If argument is null throw NullPointerException
   // else if this is full, throw IllegalStateException,
   // else make o the newest element of this
   if (o == null)
      throw new NullPointerException ("BoundedQueue.enqueue");
   else if (size == capacity)
      throw new IllegalStateException ("BoundedQueue.enqueue");
   else
   {
      size++;
      elements [back] = o;
      back = (back+1) % capacity; 
   }
}

public Object dequeue () throws IllegalStateException
{  // Modifies: this
   // Effects:   If queue is empty, throw IllegalStateException,
   // else remove and return oldest element of this

   if (size == 0)
      throw new IllegalStateException ("BoundedQueue.dequeue");
   else
   {
      size--;
      Object o = elements [ (front % capacity) ];
      elements [front] = null;
      front = (front+1) % capacity;
      return o;
   }
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
      if (i < size + 1) { // size-1
         result += ", ";
      }
   }
   result += "]";
   return result;
}

}
