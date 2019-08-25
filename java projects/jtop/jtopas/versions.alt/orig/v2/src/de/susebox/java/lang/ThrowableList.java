/*
 * ThrowableList.java: Interface for throwable stacks
 *
 * Copyright (C) 2002 Heiko Blau
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

/*-->
import de.susebox.java.lang.ThrowableList;
import de.susebox.java.lang.ThrowableMessageFormatter;
-->*/


//------------------------------------------------------------------------------
// Interface ThrowableList
//

/**<p>
 * This interface should be implemented by classes derived from {@link java.lang.Throwable}
 * that may contain a stacked, additional or wrapped throwable.
 *</p><p>
 * Such cases are common when
 *<ul><li>
 *   a method implements a certain interface method that allows for a specific
 *   throwable like IOException, but the method itself may encounter a different
 *   throwable type like SQLException (wrapped throwable)
 *</li><li>
 *   one application layer catches an throwable only to add its specific
 *   information in form of another throwable (throwable stack, nested throwable
 *   like in SQLException or MessagingException).
 *</li></ul>
 *</p><p>
 * We provide the expected code in block comments starting with --&gt;, terminated
 * by --&gt;. Note that the provided code also includes a new implementation of
 * the base class method {@link java.lang.Throwable#getMessage} using the text
 * formatting capabilities of {@link java.text.MessageFormat}.
 *<p></p>
 * <strong>This interface replaces the formerly used {@link ExceptionList} 
 * interface.</strong>
 *</p>
 *
 * @version	1.00, 2001/06/26
 * @author 	Heiko Blau
 */
public interface ThrowableList {
  
  /**
   * Method to traverse the throwable list. By convention, <code>nextThrowable</code>>
   * returns the "earlier" throwable. By walking down the throwable list one gets the
   * the following meaning:
   *<br>
   * this happened because nextThrowable happened because nextThrowable happened...
   *<br>
   * The next throwable has usually one of the following meaning:
   *<br><ul><li>
   *  It is the "real" throwable. An interface implementation might be allowed to 
   *  throw only <code>IOException</code>, but actually has to pass on a 
   *  <code>SQLException</code>. That ould be done by wrapping the <code>SQLException</code>
   *  into the <code>IOException</code>.
   *</li><li>
   *  The next throwable is "deeper" cause of this one (often called a nested
   *  throwable). A file couldn't be read in the first place and therefore not be 
   *  attached to a mail. Both this throwable and the one nested inside have their
   *  own message.
   *</li></li>
   *  There are more than one basic throwable to be propagated. A simple parser 
   *  might return all syntax errors in one throwable list.
   *</li><ul>
   *
   * @return the "earlier" throwable
   */
  public Throwable nextThrowable();
  /*-->
  {
    return _next;
  }
  -->*/
  
  
  /**
   * Check if <CODE>this</CODE> is only a throwable that wraps the real one. This
   * might be nessecary to pass an throwable incompatible to a method declaration.
   *
   * @return <CODE>true</CODE> if this is a wrapper throwable,
   *         <CODE>false</CODE> otherwise
   */
  public boolean isWrapper();
  /*-->
  {
    return _isWrapper;
  }
  -->*/
  
  /**
   * Getting the format string of a throwable message. This can also be the 
   * message itself if there are no arguments.
   *
   * @return  the format string being used by {@link java.text.MessageFormat}
   * @see     #getArguments
   */
  public String getFormat();
  /*-->
  {
    return super.getMessage();
  }
  -->*/
  
  /**
   * Retrieving the arguments for message formats. These arguments are used by
   * the {@link java.text.MessageFormat#format} call.
   *
   * @return  the arguments for a message format
   * @see     #getFormat
   */
  public Object[] getArguments();
  /*-->
  {
    return _args;
  }
  -->*/
  
  
  //---------------------------------------------------------------------------
  // implementation code templates
  //
  
  /**
   * This constructor takes a simple message string like ordinary Java exceptions.
   * This is the most convenient form to construct an <code>ThrowableList</code>
   * throwable.
   *
   * @param msg   throwable message
   */
  /*-->
  public <<WHICH>>Throwable(String msg) {
    this(null, msg, null);
  }
  -->*/
  
  /**
   * This constructor should be used for wrapping another throwable. While reading
   * data an IOException may occur, but a certain interface requires a
   * <CODE>SQLException</CODE>. Simply use:
   *<blockquote><pre>
   *   try {
   *     ...
   *   } catch (IOException ex) {
   *     throw new MyException(ex);
   *   }
   *</pre></blockquote>
   *
   * @param ex the throwable to wrap
   */
  /*-->
  public <<WHICH>>Throwable(Throwable ex) {
    this(ex, null, null);
  }
  -->*/
  
  /**
   * If one likes to add ones own information to an throwable, this constructor is
   * the easiest way to do so. By using such an approach a throwable trace with useful
   * additional informations (which file could be found, what username is unknown)
   * can be realized:
   *<br><br><CODE>
   * try {                                                                      <br>&nbsp;
   *   ...                                                                      <br>
   * } catch (SQLException ex) {                                                <br>&nbsp;
   *   throw new MyException(ex, "while connecting to " + url);                 <br>
   * }
   *<br></CODE>
   *
   * @param ex    the inner throwable
   * @param msg   throwable message
   */
  /*-->
  public <<WHICH>>Throwable(Throwable ex, String msg) {
    this(ex, msg, null);
  }
  -->*/
  
  /**
   * This constructor takes a format string and its arguments. The format string
   * must have a form that can be used by {@link java.text.MessageFormat} methods.
   * That means:
   *<br><CODE>
   *    java.text.Message.format(fmt, args)
   *<br><CODE>
   * is similar to
   *<br><CODE>
   *    new MyException(fmt, args).getMessage();
   *<CODE>
   *
   * @param fmt   throwable message format
   * @param args  arguments for the given format string
   */
  /*-->
  public <<WHICH>>Throwable(String fmt, Object[] args) {
    this(null, msg, args);
  }
  -->*/
  
  /**
   * This is the most complex way to construct an <CODE>ThrowableList</CODE>-
   * Throwable.<br>
   * An inner throwable is accompanied by a format string and its arguments.
   * Use this constructor in language-sensitive contexts or for formalized messages.
   * The meaning of the parameters is explained in the other constructors.
   *
   * @param ex    the inner throwable
   * @param fmt   throwable message
   * @param args  arguments for the given format string
   */
  /*-->
  public <<WHICH>>Throwable(Throwable ex, String fmt, Object[] args) {
    super(fmt);
   
    if (ex != null && fmt == null) {
      _isWrapper = true;
    } else {
      _isWrapper = false;
    }
    _next = ex;
    _args = args;
  }
  -->
   
  /**
   * Implementation of the standard {@link java.lang.Throwable#getMessage} method to
   * meet the requirements of formats and format arguments as well as wrapper
   * exceptions.<br>
   * If this is a wrapper throwable then the <code>getMessage</code> of the wrapped
   * throwable is returned.<br>
   * If this is not a wrapper throwable: if no arguments were given in the 
   * constructor then the format parameter is taken as the formatted message itself. 
   * Otherwise it is treated like the patter for the {@link java.text.MessageFormat#format}
   * method.
   *
   * @return  the formatted throwable message
   * @see     java.text.MessageFormat
   */
  /*-->
  public String getMessage() {
    return ThrowableMessageFormatter.getMessage(this);
  }
  -->*/
  
  
  //---------------------------------------------------------------------------
  // members
  //
  /**
   * the parameters to be used when formatting the throwable message
   */
  /*-->
  protected Object[]  _args       = null;
  -->*/
  
  /**
   * The wrapped, nested of next throwable.
   */
  /*-->
  protected Throwable _next       = null;
  -->*/

  /**
   * If <code>true</code> this is only a wrapper throwable with the real one
   * being returned by {@link #nextThrowable}, <code>false</code> for standalone, 
   * nested or subsequent exceptions
   */
  /*-->
  protected boolean   _isWrapper  = false;
  -->*/
}
