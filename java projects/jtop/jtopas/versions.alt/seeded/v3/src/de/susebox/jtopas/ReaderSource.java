/*
 * ReaderSource.java: java.io.Reader-backed data source for the Tokenizer.
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

package de.susebox.jtopas;

//-----------------------------------------------------------------------------
// Imports
//
import java.io.Reader;
import java.io.InputStreamReader;

//-----------------------------------------------------------------------------
// Class ReaderSource
//

/**<p>
 * This implementation of the {@link TokenizerSource} interface uses the JDK 
 * {@link java.io.Reader} class to realize the requested functionality. Note that
 * the backing <code>Reader</code> can be changed during the parse operations of
 * the {@link Tokenizer} instance that accesses this <code>ReaderSource</code>.
 *</p>
 *
 * @see     de.susebox.java.util.Tokenizer
 * @see     de.susebox.java.util.AbstractTokenizer
 * @author  Heiko Blau
 * @see     java.io.Reader
 */
public class ReaderSource implements TokenizerSource {
  
  //---------------------------------------------------------------------------
  // Constructors
  //
  
  /**
   * The default constructor constructs a <code>ReaderSource</code> instance
   * that reads from nowhere. A call to the {@link #read} method will immediately
   * return the end-of-file condition.
   *
   * @see #read
   */
  public ReaderSource() {
    this(null);
  }
  
  /**
   * Constructing an <code>ReaderSource</code> object with the given {@link java.io.Reader} 
   * instance to get input data from. <code>null</code> is a valid value. The
   * {@link #read} method will return no data in that case.
   *
   * @param reader  the backing {@link java.io.Reader}
   */
  public ReaderSource(Reader reader) {
    setReader(reader);
  }
  
  
  //---------------------------------------------------------------------------
  // Methods of the TokenizerSource interface
  //
  
  /**
   * The method calls the {@link java.io.Reader#read(char[], int, int)} method of 
   * the currently backing {@link java.io.Reader}. If no <code>Reader is set so far,
   * -1 (end-of-file) is returned.
   *
   * @param cbuf      buffer to receive data
   * @param offset    position from where the data should be inserted in <CODE>cbuf</CODE>
   * @param maxChars  maximum number of characters to be read into <CODE>cbuf</CODE>
   * @return actually read characters or -1 on an end-of-file condition
   * @throws Exception anything that could happen during read, most likely {@link java.io.IOException}
   */
  public int read(char[] cbuf, int offset, int maxChars) throws Exception {
    if (_reader != null) {
      return _reader.read(cbuf, offset, maxChars);
    } else {
      return 0;
    }
  }
  
  //---------------------------------------------------------------------------
  // Implementation
  //
  
  /**
   * Setting the backing {@link java.io.Reader} instance. <code>null</code> is a 
   * valid value. The {@link #read} method will return no data (end-of-file) in 
   * that case.
   *
   * @param reader  the backing {@link java.io.Reader}
   * @see   #read
   * @see   #getReader
   */
  public void setReader(Reader reader) {
    _reader = reader;
  }
  
  /**
   * Retrieving the current {@link java.io.Reader}. The method may return <code>null</code>
   * if a {@link #setReader} with null has occured before.
   *
   * @return  the current {@link java.io.Reader} or <code>null</code>
   * @see     #setReader
   */
  public Reader getReader() {
    return _reader;
  }
  
  
  //---------------------------------------------------------------------------
  // Members
  //
  private Reader _reader = null;
}
