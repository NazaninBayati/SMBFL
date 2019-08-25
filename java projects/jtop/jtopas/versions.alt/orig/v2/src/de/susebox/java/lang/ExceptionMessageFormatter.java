/*
 * ExceptionMessageFormatter.java: formatting an exception message
 *
 * Copyright (C) 2001 Heiko Blau
 *
 * This file belongs to the Susebox Java Core Library (Susebox JCL).
 * The Susebox JCL is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation; either version 2.1 of the License, or (at your
 * option) any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along
 * with the Susebox JCL. If not, write to the
 *
 *   Free Software Foundation, Inc.
 *   59 Temple Place, Suite 330,
 *   Boston, MA 02111-1307
 *   USA
 *
 * or check the Internet: http://www.fsf.org
 *
 * Contact:
 *   email: heiko@susebox.de
 */

package de.susebox.java.lang;

//------------------------------------------------------------------------------
// Imports
//
import java.text.MessageFormat;
import de.susebox.java.lang.ExceptionList;


//------------------------------------------------------------------------------
// Class ExceptionMessageFormatter
//

/**<p>
 * Usage of this class is deprecated in favour of the new {@link ThrowableMessageFormatter}
 *</p>
 *
 * @author 	Heiko Blau
 * @deprecated
 */
public final class ExceptionMessageFormatter {
  
  /**
   * Message indentation for nested exceptions.
   */
  public static final String MSG_IDENTATION = "    ";
  
  /**
   * This method should be called by all implementations of the {@link ExceptionList}
   * interface in their {@link java.lang.Throwable#getMessage} implementation. It
   * ensures that the formatting of exception lists, nested or wrapped exceptions 
   * is done in a consistent way.
   * <br>
   * The method returns an exception message assembled in this way:
   * <br><ol><li>
   *  If the calling exception is a wrapper exception (see {@link ExceptionList#isWrapperException}),
   *  it delegates the call to the wrapped exception (<code>wrappedEx.getMessage()</code>).
   * </li><li>
   *  If there is a nested exception (the calling exception has a message of its own),
   *  the returned message starts with the string returned by the {@link java.lang.Object#toString}
   *  method, followed and separated by a end-of-line sequence by the formatted message
   *  of the calling exception.
   * </li><li>
   *  If the calling exception has only a message without parameters, this message
   *  is either appended to the string produced by processing a nested exception or
   *  is taken as the entire return value if there is no nested or subsequent exception
   *  present.
   * </li><li>
   *  If the calling exception provides parameters along with a format string (the
   *  return value of <code>super.getMessage</code> is interpreted as a format string),
   *  a formatted message produced by {@link java.text.MessageFormat#format} is either
   *  appended to the string produced by processing a nested exception or is taken as
   *  the entire return value if there is no nested or subsequent exception present.
   * </li><ol>
   *
   * @param   ex  the calling exception
   * @return  the formatted exception message
   * @see     java.text.MessageFormat
   * @see     ExceptionList
   */
  public static final String getMessage(ExceptionList ex) {
    // wrapped exceptions return theri own message
    if (ex.isWrapperException()) {
      return ex.nextException().getMessage();
    }
    
    // prepare the formatting
    StringBuffer  msg  = new StringBuffer();
    String        fmt  = ex.getFormat();
    String        nl   = System.getProperty("line.separator");
    Exception     next = ex.nextException();
    
    // message of the nested or next exception first
    if (next != null) {
      msg.append(_eolSequence);
      msg.append(MSG_IDENTATION);
      msg.append(next.toString());
      if (fmt != null) {
        msg.append(_eolSequence);
        msg.append(MSG_IDENTATION);
      }
    }

    // and now our own message
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

  
  //---------------------------------------------------------------------------
  // members
  //
  private static final String _eolSequence = System.getProperty("line.separator");
}
