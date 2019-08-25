/*
 * ExtIndexOutOfBoundsException.java: Extended standard exceptio for stacks
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
import java.lang.IndexOutOfBoundsException;

import de.susebox.java.lang.ExceptionList;
import de.susebox.java.lang.ExceptionMessageFormatter;


//------------------------------------------------------------------------------
// ExtIndexOutOfBoundsException - definition
//

/**
 * Implementation of the ExceptionList interface for the JDK IOException.
 *
 * @version	1.00, 2001/06/26
 * @author 	Heiko Blau
 */
public class ExtIndexOutOfBoundsException
  extends     IndexOutOfBoundsException 
  implements  ExceptionList 
{
  
  //---------------------------------------------------------------------------
  // methods of the ExceptionList interface
  //
  
  /**
   * Method to traverse the exception list. See {@link ExceptionList#nextException}
   * for details.
   *
   * @return the "earlier" exception
   */
  public Exception nextException() {
    return _next;
  }
  
  /**
   * Check if <CODE>this</CODE> is only a exception that wraps the real one. This
   * might be nessecary to pass an exception incompatible to a method declaration.
   *
   * @return <CODE>true</CODE> if this is a wrapper exception,
   *         <CODE>false</CODE> otherwise
   */
  public boolean isWrapperException() {
    return _isWrapper;
  }
  
  /**
   * Getting the format string of a exception message. This can also be the
   * message itself if there are no arguments.
   *
   * @return  the format string being used by {@link java.text.MessageFormat}
   * @see     #getArguments
   */
  public String getFormat() {
    return super.getMessage();
  }  
  
  /**
   * Retrieving the arguments for message formats. These arguments are used by
   * the {@link java.text.MessageFormat#format} call.
   *
   * @return  the arguments for a message format
   * @see     #getFormat
   */
  public Object[] getArguments() {
    return _args;
  }
  
  
  //---------------------------------------------------------------------------
  // constructors
  //
  
  /**
   * This constructor should be used for wrapping another exception. While reading
   * data an IOException may occur, but a certain interface requires a
   * {@link java.sql.SQLException}. Simply use:
   *<blockquote><pre>
   * try {
   *   ...
   * } catch (NullPointerException ex) {
   *   throw new ExtIndexOutOfBoundsException(ex);
   * }
   *</pre></blockquote>
   *
   * @param ex the exception to wrap
   */
  public ExtIndexOutOfBoundsException(Exception ex) {
    this(ex, null, null);
  }
  
  /**
   * If one likes to add ones own information to an exception, this constructor is
   * the easiest way to do so. By using such an approach a exception trace with useful
   * additional informations (which file could be found, what username is unknown)
   * can be realized:
   *<blockquote><pre>
   * try {
   *   ...
   * } catch (SQLException ex) {
   *   throw new IOException(ex, "while connecting to " + url);
   * }
   *</pre></blockquote>
   *
   * @param ex    the inner exception
   * @param msg   exception message
   */
  public ExtIndexOutOfBoundsException(Exception ex, String msg) {
    this(ex, msg, null);
  }
  
  /**
   * This constructor takes a format string and its arguments. The format string
   * must have a form that can be used by {@link java.text.MessageFormat} methods.
   * That means:
   *<blockquote><pre>
   *    java.text.Message.format(fmt, args)
   *</pre></blockquote>
   * is similar to
   *<blockquote><pre>
   *    new MyException(fmt, args).getMessage();
   *</pre></blockquote>
   *
   * @param fmt   exception message
   * @param args  arguments for the given format string
   */
  public ExtIndexOutOfBoundsException(String fmt, Object[] args) {
    this(null, fmt, args);
  }
  
  /**
   * This is the most complex way to construct an <CODE>ExceptionList</CODE>-
   * Exception.<br>
   * An inner exception is accompanied by a format string and its arguments.
   * Use this constructor in language-sensitive contexts or for formalized messages.
   * The meaning of the parameters is explained in the other constructors.
   *
   * @param ex    the inner exception
   * @param fmt   exception message
   * @param args  arguments for the given format string
   */
  public ExtIndexOutOfBoundsException(Exception ex, String fmt, Object[] args) {
    super(fmt);
   
    if (ex != null && fmt == null) {
      _isWrapper = true;
    } else {
      _isWrapper = false;
    }
    _next = ex;
    _args = args;
  }
  
  
  //---------------------------------------------------------------------------
  // overridden methods
  //
  
  /**
   * Implementation of the standard {@link java.Throwable#getMessage} method. It
   * delegates the call to the central {@link ExceptionMessageFormatter#getMessage}
   * method.
   *
   * @return  the formatted exception message
   * @see     ExceptionMessageFormatter
   */
  public String getMessage() {
    return ExceptionMessageFormatter.getMessage(this);
  }
  

  //---------------------------------------------------------------------------
  // members
  //

  /**
   * the parameters to be used when formatting the exception message
   */
  protected Object[]  _args       = null;

  /**
   * The wrapped, nested of next exception.
   */
  protected Exception _next       = null;
  
  /**
   * If <code>true</code> this is only a wrapper exception with the real one
   * being returned by {@link #nextException}, <code>false</code> for standalone, 
   * nested or subsequent exceptions
   */
  protected boolean   _isWrapper  = false;
}
