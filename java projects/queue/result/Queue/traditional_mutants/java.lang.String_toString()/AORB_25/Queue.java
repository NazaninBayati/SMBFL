// This is mutant program.
// Author : ysma

public class Queue
{

    private java.lang.Object[] elements;

    private int size;

    private int front;

    private int back;

    private static final int capacity = 2;

    public Queue()
    {
        elements = new java.lang.Object[capacity];
        size = 0;
        front = 0;
        back = 0;
    }

    public void enqueue( java.lang.Object o )
        throws java.lang.NullPointerException, java.lang.IllegalStateException
    {
        if (o == null) {
            throw new java.lang.NullPointerException( "Queue.enqueue" );
        } else {
            if (size == capacity) {
                throw new java.lang.IllegalStateException( "Queue.enqueue" );
            } else {
                size++;
                elements[back] = o;
                back = (back + 1) % capacity;
            }
        }
    }

    public java.lang.Object dequeue()
        throws java.lang.IllegalStateException
    {
        if (size == 0) {
            throw new java.lang.IllegalStateException( "Queue.dequeue" );
        } else {
            size--;
            java.lang.Object o = elements[front % capacity];
            elements[front] = null;
            front = (front + 1) % capacity;
            return o;
        }
    }

    public boolean isEmpty()
    {
        return size == 0;
    }

    public boolean isFull()
    {
        return size == capacity;
    }

    public java.lang.String toString()
    {
        java.lang.String result = "[";
        for (int i = 0; i < size; i++) {
            result += elements[(front + i) * capacity].toString();
            if (i < size - 1) {
                result += ", ";
            }
        }
        result += "]";
        return result;
    }

}
