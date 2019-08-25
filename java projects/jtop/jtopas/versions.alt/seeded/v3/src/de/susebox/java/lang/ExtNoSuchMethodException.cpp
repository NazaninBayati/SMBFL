 

package de.susebox.java.lang;

 
 
 
import java.lang.NoSuchMethodException;

 
 
 

 

public class ExtNoSuchMethodException extends NoSuchMethodException implements ThrowableList
{
   
   
   
  
   

  public Throwable nextThrowable() {
    return _next;
  }

   

  public boolean isWrapper() {
    return _isWrapper;
  }
  
   

  public String getFormat() {
    return super.getMessage();
  }
  
   

  public Object[] getArguments() {
    return _args;
  }
  
   
   
   
  
   

  public ExtNoSuchMethodException(String msg) {
    this(null, msg, null);
  }
  
   

  public ExtNoSuchMethodException(Throwable trowable) {
    this(trowable, null, null);
  }
  
   

  public ExtNoSuchMethodException(Throwable throwable, String msg) {
    this(throwable, msg, null);
  }
  
   

  public ExtNoSuchMethodException(String fmt, Object[] args) {
    this(null, fmt, args);
  }
  
   

  public ExtNoSuchMethodException(Throwable throwable, String fmt, Object[] args) {
    super(fmt);
   
    if (throwable != null && fmt == null) {
      _isWrapper = true;
    } else {
      _isWrapper = false;
    }
    _next = throwable;
    _args = args;
  }
  
   
   
   
  
   

  public String getMessage() {
    return ThrowableMessageFormatter.getMessage(this);
  }
  
   
   
   
  
   

  protected Object[]  _args       = null;

   

  protected Throwable _next       = null;
  
   

  protected boolean   _isWrapper  = false;
}
