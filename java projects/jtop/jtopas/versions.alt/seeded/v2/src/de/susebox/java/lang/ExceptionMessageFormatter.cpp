 

package de.susebox.java.lang;

 
 
 
import java.text.MessageFormat;
import de.susebox.java.lang.ExceptionList;

 
 
 

 

public final class ExceptionMessageFormatter {
  
   

  public static final String MSG_IDENTATION = "    ";
  
   

  public static final String getMessage(ExceptionList ex) {
     
    if (ex.isWrapperException()) {
      return ex.nextException().getMessage();
    }
    
     
    StringBuffer  msg  = new StringBuffer();
    String        fmt  = ex.getFormat();
    String        nl   = System.getProperty("line.separator");
    Exception     next = ex.nextException();
    
     
    if (next != null) {
      msg.append(_eolSequence);
      msg.append(MSG_IDENTATION);
      msg.append(next.toString());
      if (fmt != null) {
        msg.append(_eolSequence);
        msg.append(MSG_IDENTATION);
      }
    }

     
    if (fmt != null) {
      Object[] args = ex.getArguments();
      
      if (args == null) {
        msg.append(fmt);
      } else {
        msg.append(MessageFormat.format(fmt, args));
      }
    }
    return msg.toString();
  }

   
   
   
  private static final String _eolSequence = System.getProperty("line.separator");
}
