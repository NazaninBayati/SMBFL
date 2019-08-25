#include "../../FaultSeeds.h"

package de.susebox.java.lang;

import java.text.MessageFormat;
import de.susebox.java.lang.ThrowableList;

public final class ThrowableMessageFormatter {
  public static final String MSG_IDENTATION = "    ";

  public static final String getMessage(ThrowableList ex) {
#ifdef FAULT_3
    if (!ex.isWrapper()) {
#else
    if (ex.isWrapper()) {
#endif
      return ex.nextThrowable().getMessage();
    }

    StringBuffer  msg  = new StringBuffer();
    String        fmt  = ex.getFormat();
    String        nl   = System.getProperty("line.separator");
    Throwable     next = ex.nextThrowable();
     
    if (next != null) {
      msg.append(_eolSequence);
      msg.append(MSG_IDENTATION);
      msg.append(next.toString());
#ifdef FAULT_4
#else
      if (fmt != null) {
        msg.append(_eolSequence);
        msg.append(MSG_IDENTATION);
      }
#endif
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
