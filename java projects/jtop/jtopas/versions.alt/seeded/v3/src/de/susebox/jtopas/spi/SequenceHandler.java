/*
 * SequenceHandler.java: string, comment and special sequence handling in tokenizers
 *
 * Copyright (C) 2002 Heiko Blau
 *
 * This file belongs to the JTopas Library.
 * JTopas is free software; you can redistribute it and/or modify it 
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
 * with JTopas. If not, write to the
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

package de.susebox.jtopas.spi;

//-----------------------------------------------------------------------------
// Imports
//
import de.susebox.jtopas.TokenizerProperty;
import de.susebox.jtopas.TokenizerException;


//-----------------------------------------------------------------------------
// Interface SequenceHandler
//

/**<p>
 * This interface must be implemented by classes that should be used as a 
 * special sequence, string and comment start sequence checker for {@link Tokenizer}
 * implementations.
 *</p>
 *
 * @see     de.susebox.jtopas.Tokenizer
 * @see     de.susebox.jtopas.TokenizerProperties
 * @see     de.susebox.jtopas.spi.DataMapper
 * @author  Heiko Blau
 */
public interface SequenceHandler {
  
  /**
   * This method checks if a given range of data starts with a special sequence,
   * a comment or a string. These three types of token are tested together since
   * both comment and string prefixes are ordinary special sequences. Only the 
   * actions preformed <strong>after</strong> a string or comment has been detected,
   * are different.
   *<br>
   * The method returns <code>null</code> if no special sequence, comment or string 
   * could matches the the leading part of the data range given through the
   * {@link DataProvider}.
   *<br>
   * In cases of strings or comments, the return value contains the description
   * for the introducing character sequence, <strong>NOT</strong> the whole
   * string or comment. The reading of the rest of the string or comment is done
   * by the calling {@link de.susebox.jtopas.Tokenizer}.
   *
   * @param   dataProvider  the source to get the data range from
   * @return  a {@link de.susebox.jtopas.TokenizerProperty} if a special sequence, 
   *          comment or string could be detected, <code>null</code> otherwise
   * @throws  TokenizerException    generic exception
   * @throws  NullPointerException  if no {@link DataProvider} is given
   */
  public TokenizerProperty startsWithSequenceCommentOrString(DataProvider dataProvider) 
    throws TokenizerException, NullPointerException;

  /**
   * This method returns the length of the longest special sequence, comment or
   * string prefix that is known to this <code>SequenceHandler</code>. When
   * calling {@link #startsWithSequenceCommentOrString}, the passed {@link DataProvider}
   * parameter will supply at least this number of characters (see {@link DataProvider#getLength}).
   * If less characters are provided, EOF is reached.
   *<br>
   * The method is an easy approach to the problem of how to provide more data
   * in case a test runs out of characters. The invoking {@link Tokenizer} (
   * represented by the given {@link DataProvider}) can supply enough data for
   * the {@link #startsWithSequenceCommentOrString} method.
   *
   * @return  the number of characters needed in the worst case to identify a 
   *          special sequence
   */
  public int getSequenceMaxLength();
}
