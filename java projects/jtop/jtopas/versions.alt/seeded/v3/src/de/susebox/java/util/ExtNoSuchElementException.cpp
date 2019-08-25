 

package de.susebox.java.util;

 
 
 
import java.util.NoSuchElementException;

import de.susebox.java.lang.ExceptionList;
import de.susebox.java.lang.ThrowableList;
import de.susebox.java.lang.ThrowableMessageFormatter;

 
 
 

 

public class ExtNoSuchElementException 
  extends     NoSuchElementException 
  implements  ThrowableList, ExceptionList
{
   
   
   
  
   

  public Throwable nextThrowable() {
    return _next;
  }

   

  public Exception nextException() {
    if (_next == null) {
      return null;
    } else if (_next instanceof Exception) {
      return (Exception)_next;
    } else {
      return new RuntimeException(_next.toString());
    }
  }
  
   

  public boolean isWrapper() {
    return _isWrapper;
  }
  
   

  public boolean isWrapperException() {
    return isWrapper();
  }
  
   

  public String getFormat() {
    return super.getMessage();
  }
  
   

  public Object[] getArguments() {
    return _args;
  }
  
   
   
   
  
   

  public ExtNoSuchElementException(String msg) {
    this(null, msg, null);
  }
  
   

  public ExtNoSuchElementException(Throwable ex) {
    this(ex, null, null);
  }
  
   

  public ExtNoSuchElementException(Throwable ex, String msg) {
    this(ex, msg, null);
  }
  
   

  public ExtNoSuchElementException(String fmt, Object[] args) {
    this(null, fmt, args);
  }
  
   

  public ExtNoSuchElementException(Throwable ex, String fmt, Object[] args) {
    super(fmt);
   
    if (ex != null && fmt == null) {
      _isWrapper = true;
    } else {
      _isWrapper = false;
    }
    _next = ex;
    _args = args;
  }
  
   
   
   
  
   

  public String getMessage() {
    return ThrowableMessageFormatter.getMessage(this);
  }
  
   
   
   
  
   

  protected Object[]  _args       = null;

   

  protected Throwable _next       = null;
  
   

  protected boolean   _isWrapper  = false;
}
