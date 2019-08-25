#include "../../FaultSeeds.h"

package de.susebox.java.lang;

import java.lang.IndexOutOfBoundsException;
import de.susebox.java.lang.ExceptionList;
import de.susebox.java.lang.ExceptionMessageFormatter;

public class ExtIndexOutOfBoundsException
  extends     IndexOutOfBoundsException 
  implements  ExceptionList 
{
  public Exception nextException() {
    return _next;
  }
  
  public boolean isWrapperException() {
    return _isWrapper;
  }
  
  public String getFormat() {
    return super.getMessage();
  }  
  
  public Object[] getArguments() {
    return _args;
  }

  public ExtIndexOutOfBoundsException(Exception ex) {
    this(ex, null, null);
  }
  
  public ExtIndexOutOfBoundsException(Exception ex, String msg) {
    this(ex, msg, null);
  }
  
  public ExtIndexOutOfBoundsException(String fmt, Object[] args) {
    this(null, fmt, args);
  }
  
  public ExtIndexOutOfBoundsException(Exception ex, String fmt, Object[] args) {
#ifdef FAULT_10
#else
    super(fmt);
#endif
   
    if (ex != null && fmt == null) {
      _isWrapper = true;
    } else {
      _isWrapper = false;
    }
    _next = ex;
    _args = args;
  }

#ifdef FAULT_9
#else
  public String getMessage() {
    return ExceptionMessageFormatter.getMessage(this);
  }
#endif

  protected Object[]  _args       = null;
  protected Exception _next       = null;
  protected boolean   _isWrapper  = false;
}
