#include "../../FaultSeeds.h"

package de.susebox.java.io;
 
import java.io.IOException;
import de.susebox.java.lang.ExceptionList;
import de.susebox.java.lang.ExceptionMessageFormatter;

public class ExtIOException 
  extends    IOException 
  implements ExceptionList {

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
  
	public ExtIOException(Exception ex) {
		this(ex, null, null);
	}

	public ExtIOException(Exception ex, String msg) {
		this(ex, msg, null);
	}

	public ExtIOException(String fmt, Object[] args) {
    this(null, fmt, args);
	}

	public ExtIOException(Exception ex, String fmt, Object[] args) {
#ifdef FAULT_1
#else
    super(fmt);
#endif

   
    if (ex != null && fmt == null) {
#ifdef FAULT_2
      _isWrapper = false;
#else
      _isWrapper = true;
#endif
    } else {
#ifdef FAULT_2
      _isWrapper = true;
#else
      _isWrapper = false;
#endif
    }
    _next = ex;
    _args = args;
	}

#ifdef FAULT_3
#else
	public String getMessage() {
    return ExceptionMessageFormatter.getMessage(this);
	}
#endif

  protected Object[]  _args       = null;

  protected Exception _next       = null;

  protected boolean   _isWrapper  = false;
}


