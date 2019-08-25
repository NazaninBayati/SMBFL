 

package de.susebox.java.lang;

 
 
 
import java.lang.RuntimeException;

 
 
 

 

public class ExtRuntimeException
  extends     RuntimeException
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
  
   
   
   
  
   

  public ExtRuntimeException(String msg) {
    this(null, msg, null);
  }
  
   

  public ExtRuntimeException(Throwable throwable) {
    this(throwable, null, null);
  }
  
   

  public ExtRuntimeException(Throwable throwable, String msg) {
    this(throwable, msg, null);
  }
  
   

  public ExtRuntimeException(String fmt, Object[] args) {
    this(null, fmt, args);
  }
  
   

  public ExtRuntimeException(Throwable throwable, String fmt, Object[] args) {
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
