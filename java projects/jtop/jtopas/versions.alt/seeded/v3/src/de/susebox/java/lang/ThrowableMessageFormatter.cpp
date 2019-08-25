 

package de.susebox.java.lang;

 
 
 
import java.text.MessageFormat;
import de.susebox.java.lang.ThrowableList;

 
 
 

 

public final class ThrowableMessageFormatter {
  
   

  public static final String MSG_IDENTATION = "    ";
  
   

  public static final String getMessage(ThrowableList ex) {
     
    if (ex.isWrapper()) {
      return ex.nextThrowable().getMessage();
    }
    
     
    StringBuffer  msg  = new StringBuffer();
    String        fmt  = ex.getFormat();
    Throwable     next = ex.nextThrowable();
    
     
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
        try {
          msg.append(MessageFormat.format(fmt, args));
        } catch (IllegalArgumentException argEx) {
          msg.append(argEx.getMessage());
          msg.append(_eolSequence);
          msg.append("While formatting this message:");
          msg.append(_eolSequence);
          msg.append(fmt);
        }
      }
    }
    return msg.toString();
  }

   
   
   
  private static final String _eolSequence = System.getProperty("line.separator");
}
