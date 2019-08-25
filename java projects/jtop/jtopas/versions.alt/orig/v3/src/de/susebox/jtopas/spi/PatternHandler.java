/*
 * PatternHandler.java: Interface for pattern-aware tokenizers.
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
// Interface PatternHandler
//

/**<p>
 * This interface must be implemented by classes that should be used as a 
 * pattern handler for a {@link de.susebox.jtopas.Tokenizer}. Pattern are usually
 * regular expressions that are applied on token images to check if that image 
 * matches the pattern.
 *</p>
 *
 * @see     de.susebox.jtopas.Tokenizer
 * @see     de.susebox.jtopas.TokenizerProperties
 * @see     de.susebox.jtopas.spi.DataMapper
 * @author  Heiko Blau
 */
public interface PatternHandler {
  
  /**
   * Return code for no match.
   */
  public static final byte NO_MATCH = -1;
  
  /**
   * Return code for a complete match.
   */
  public static final byte COMPLETE_MATCH = 0;
  
  /**
   * Return code for a complete match.
   */
  public static final byte MATCH = 1;
  
  /**
   * Return code for an incomplete match.
   */
  public static final byte INCOMPLETE_MATCH = 2;
  
  /**
   * This method checks if the start of a character range given through the 
   * {@link DataProvider} matches a pattern. An implementation should use
   * a {@link de.susebox.jtopas.TokenizerException} to report problems.
   *<br>
   * The pattern check is repeated if the method returns {@link #INCOMPLETE_MATCH}
   * or {@link #MATCH}. Since it is probably a rare case, that where are not enough
   * data to find a complete or no match, the overhead of a repeated check on 
   * part of the data is neglected.
   *
   * @param   dataProvider  the source to get the data from
   * @param   property      a filled {@link de.susebox.jtopas.TokenizerProperty} 
   *                        if a matching pattern could be found, otherwise the
   *                        state remains unchanged
   * @return  one of the constants defined in this interface
   * @throws  TokenizerException    generic exception
   * @throws  NullPointerException  if no {@link DataProvider} is given
   */
  public int matches(DataProvider dataProvider, TokenizerProperty property)
    throws TokenizerException, NullPointerException;
}
