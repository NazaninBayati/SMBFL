/*
 * StandardKeywordHandler.java: Implementation of KeywordHandler.
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
import de.susebox.jtopas.TokenizerProperties;


//-----------------------------------------------------------------------------
// class StandardKeywordHandler
//

/**<p>
 * Simple implementation of the {@link KeywordHandler} interface. This class
 * works only with the {@link TokenizerProperties} interface methods and is aware
 * of changes in these properties. It does not cache any information and is 
 * therefore a more or less slow way to handle keywords. 
 *</p><p>
 * This class is a bridge between arbitrary {@link de.susebox.jtopas.Tokenizer} 
 * implementations using the SPI interface {@link KeywordHandler} and any 
 * {@link de.susebox.jtopas.TokenizerProperties} implementation that does not 
 * implement the <code>KeywordHandler</code> interface itself.
 *</p>
 *
 * @see     KeywordHandler
 * @see     de.susebox.jtopas.Tokenizer
 * @see     de.susebox.jtopas.TokenizerProperties
 * @author  Heiko Blau
 */
public class StandardKeywordHandler implements KeywordHandler {
  
  /**
   * The constructor takes the {@link de.susebox.jtopas.TokenizerProperties}
   * that provide the keywords.
   *
   * @param props   the {@link de.susebox.jtopas.TokenizerProperties} to take the 
   *                keywords from 
   */
  public StandardKeywordHandler(TokenizerProperties props) {
    _properties = props;
  }
  
  /**
   * This method checks if the character range given through the 
   * {@link DataProvider} comprises a keyword.
   *
   * @param   dataProvider  the source to get the data from, that are checked
   * @return  a {@link de.susebox.jtopas.TokenizerProperty} if a keyword could be 
   *          found, <code>null</code> otherwise
   * @throws  TokenizerException failure while reading more data
   * @throws  NullPointerException  if no {@link DataProvider} is given
   */
  public TokenizerProperty isKeyword(DataProvider dataProvider) throws NullPointerException {
    if (_properties != null) {
      String keyword = new String(dataProvider.getData(), dataProvider.getStartPosition(), dataProvider.getLength());
      return _properties.getKeyword(keyword);
    } else {
      return null;
    }
  }

  
  //---------------------------------------------------------------------------
  // Members
  //
  
  /**
   * The {@link TokenizerProperties} that provide the keywords and the 
   * control flags.
   */
  private TokenizerProperties _properties = null;
}
