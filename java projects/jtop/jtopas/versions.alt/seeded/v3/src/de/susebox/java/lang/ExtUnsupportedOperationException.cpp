 

package de.susebox.java.lang;

 
 
 
import java.lang.UnsupportedOperationException;

 
 
 

 

public class ExtUnsupportedOperationException 
  extends     UnsupportedOperationException 
  implements  ThrowableList
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
  
   
   
   
  
   

  public ExtUnsupportedOperationException(String msg) {
    this(null, msg, null);
  }
  
   

  public ExtUnsupportedOperationException(Throwable ex) {
    this(ex, null, null);
  }
  
   

  public ExtUnsupportedOperationException(Throwable ex, String msg) {
    this(ex, msg, null);
  }
  
   

  public ExtUnsupportedOperationException(String fmt, Object[] args) {
    this(null, fmt, args);
  }
  
   

  public ExtUnsupportedOperationException(Throwable ex, String fmt, Object[] args) {
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
