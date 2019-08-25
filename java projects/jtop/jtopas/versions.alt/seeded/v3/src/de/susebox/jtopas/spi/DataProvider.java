/*
 * DataProvider.java: service provider interface for data sources.
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
import de.susebox.jtopas.Tokenizer;


//-----------------------------------------------------------------------------
// Interface DataProvider
//

/**<p>
 * This interface supplies the nessecary methods for a {@link DataMapper} to 
 * perform its operations like checking for whitespaces, special sequences etc.
 * Instances of the interface are created by {@link de.susebox.jtopas.Tokenizer}
 * implementations.
 *</p><p>
 * A {@link de.susebox.jtopas.Tokenizer} implementation will either implement the
 * <code>DataProvider</code> interface itself or has an associated - probably 
 * non-public - implementing class.
 *</p><p>
 * Ech implementation of this interface should also override the method
 * {@link java.lang.Object#toString}.
 *</p>
 *
 * @see de.susebox.jtopas.Tokenizer
 * @see de.susebox.jtopas.TokenizerProperties
 * @see DataMapper
 * @author Heiko Blau
 */
public interface DataProvider {
  
  /**
   * This method retrieves the data range the active {@link DataMapper} should
   * use for its operations. The calling method of the <code>DataMapper</code>
   * should be aware that the provided array is probably the input buffer of the
   * {@link de.susebox.jtopas.Tokenizer}. It should therefore treat it as a 
   * read-only object.
   *<br>
   * Use this method in combination with {@link #getStartPosition} and {@link getLength}.
   * Characters outside this range are invalid.
   *<br>
   * For a more secure but slower access, use the method {@link #getDataCopy}
   * to retrieve a copy of the valid character range.
   *
   * @return the character buffer to read data from
   */
  public char[] getData();
  
  /**
   * This method copies the current data range of the {@link de.susebox.jtopas.Tokenizer}
   * providing the <code>DataProvider</code> into a character buffer. Use this 
   * method if data should be modified.
   *<br>
   * The copy contains only valid data starting at position 0 with the length of
   * the array returned. <strong>Don't</strong> use {@link #getStartPosition} or
   * {@link #getLength} on this copy.
   *<br>
   * An alternative to this method is {@link #toString}.
   *
   * @return  a copy of the valid data of this {@link DataProvider}
   * @see #getData
   */
  public char[] getDataCopy();
     
  /**
   * Retrieving the position where the data to analyze start in the buffer provided
   * by {@link #getData}. The calling {@link DataMapper} must not access data
   * prior to this index in the character array.
   *
   * @return  index in the character array returned by {@link #getData}, where data starts
   */
  public int getStartPosition(); 

  /**
   * Retrieving the maximum number of characters in the array provided by {@link getData}
   * that can be analyzed by the calling {@link DataMapper}.
   *
   * @param testChar  check this character
   * @return <code>true</code> if the given character is a separator,
   *         <code>false</code> otherwise
   */
  public int getLength();
}
