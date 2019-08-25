/*
 * StandardTokenizerProperties.java: general-use TokenizerProperties implementation
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
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

import de.susebox.java.lang.ExtRuntimeException;
import de.susebox.java.lang.ExtUnsupportedOperationException;
import de.susebox.java.lang.ExtIllegalArgumentException;

import de.susebox.jtopas.spi.DataMapper;
import de.susebox.jtopas.spi.DataProvider;
import de.susebox.jtopas.spi.PatternHandler;


//-----------------------------------------------------------------------------
// Class StandardTokenizerProperties
//

/**<p>
 * The class <code>StandardTokenizerProperties</code> provides a simple implementation
 * of the {@link TokenizerProperties} interface for use is most situations.
 *</p>
 *
 * @see TokenizerProperties
 * @see Tokenizer
 * @author Heiko Blau
 */
public class StandardTokenizerProperties 
  implements TokenizerProperties, DataMapper
{
  
  //---------------------------------------------------------------------------
  // Constructors
  //
  
  /**
   * Default constructor that intitializes an instance with teh default whitespaces
   * and separator sets.
   */  
  public StandardTokenizerProperties() {
    this(0);
  }

  /**
   * This constructor takes the control flags to be used. It is a shortcut to:
   * <pre>
   *   TokenizerProperties props = new StandardTokenizerProperties();
   *
   *   props.setParseFlags(flags);
   * </pre>
   * See the {@link TokenizerProperties} interface for the supported flags.
   *<br>
   * The {@link Tokenizer#DEFAULT_WHITESPACES} and {@link Tokenizer#DEFAULT_SEPARATORS} 
   * are used for whitespace and separator handling if no explicit calls to 
   * {@link #setWhitespaces} and {@link #setSeparators} will follow subsequently.
   *
   * @param flags     tokenizer control flags
   * @see   #setParseFlags
   */  
  public StandardTokenizerProperties(int flags) {
    this(flags, DEFAULT_WHITESPACES, DEFAULT_SEPARATORS);
  }
  
  
  /**
   * This constructor takes the whitespace and separator sets to be used. It is 
   * a shortcut to:
   * <pre>
   *   TokenizerProperties props = new StandardTokenizerProperties();
   *
   *   props.setWhitespaces(ws);
   *   props.setSeparators(sep);
   * </pre>
   *
   * @param flags       tokenizer control flags
   * @param whitespaces the whitespace set
   * @param separators  the set of separating characters
   * @see   #setParseFlags
   * @see   #setWhitespaces
   * @see   #setSeparators
   */  
  public StandardTokenizerProperties(int flags, String whitespaces, String separators) {
    setParseFlags(flags);
    setWhitespaces(whitespaces);
    setSeparators(separators);
  }
  
  
  //---------------------------------------------------------------------------
  // Methods of the TokenizerProperties interface
  //
  
  /**
   * See the method description in {@link TokenizerProperties}.
   *
   * @param flags the parser control flags
   * @see   #getParseFlags
   */
  public void setParseFlags(int flags) {
    // normalize flags
    flags = normalizeFlags(flags);
    
    // set flags
    synchronized(this) {
      int oldFlags = _flags;
      
      _flags = flags;
      if (oldFlags != _flags) {
        notifyListeners(new TokenizerPropertyEvent(
                              TokenizerPropertyEvent.PROPERTY_MODIFIED,
                              new TokenizerProperty(TokenizerProperty.PARSE_FLAG_MASK, 
                                                    new String[] { Integer.toBinaryString(_flags) } ),
                              new TokenizerProperty(TokenizerProperty.PARSE_FLAG_MASK, 
                                                    new String[] { Integer.toBinaryString(oldFlags) } )));
      }
    }
  }

   /**
    * See the method description in {@link TokenizerProperties}.
    *
    * @return the current parser control flags
    * @see    #setParseFlags
    */
  public int getParseFlags() {
    return _flags;
  }
  
  /**
   * Registering a string description. See the method description in the interface
   * {@link TokenizerProperties}.
   *
   * @param   start     the starting sequence of a string
   * @param   end       the finishing sequence of a string
   * @param   escape    the escape sequence inside the string
   * @throws  IllegalArgumentException when <code>null</code> or an empty string 
   *          is passed for start or end
   * @see     #removeString
   * @see     #add(String, String, String, Object)
   */
  public void addString(String start, String end, String escape) 
    throws IllegalArgumentException
  {
    addString(start, end, escape, null);
  }

  /** Registering a the sequences that are used for string-like text parts.
   * See the method description in {@link TokenizerProperties}.
   * @param start the starting sequence of a string
   * @param end the finishing sequence of a string
   * @param escape the escape sequence inside the string
   * @param companion the associated information
   * @throws IllegalArgumentException when <code>null</code> or an empty string is passed for start or end
   *
   */
  public void addString(String start, String end, String escape, Object companion)
    throws IllegalArgumentException
  {
    addString(start, end, escape, companion, getParseFlags());
  }
  
  
  /** Registering a the sequences that are used for string-like text parts.
   * See the method description in {@link TokenizerProperties}.
   * @param start the starting sequence of a string
   * @param end the finishing sequence of a string
   * @param escape the escape sequence inside the string
   * @param companion the associated information
   * @param flags modification flags
   * @throws IllegalArgumentException when <code>null</code> or an empty string is passed for start or end
   */
  public void addString(String start, String end, String escape, Object companion, int flags)
    throws IllegalArgumentException
  {
    addSpecialSequence(
      new TokenizerProperty(Token.STRING, new String[] { start, end, escape }, 
                            companion, flags)
    );
  }
  
  /** Removing a string description.
   * See the method description in {@link TokenizerProperties}.
   * @param start the starting sequence of a string
   * @throws IllegalArgumentException when <code>null</code> or an empty string is passed for start
   */  
  public void removeString(String start) throws IllegalArgumentException {
    removeSpecialSequence(start);
  }
  
    
  /** Retrieving the information associated with a certain string.
   * See the method description in {@link TokenizerProperties}.
   * @param start the starting sequence of a string
   * @return the associated information or <code>null</code>
   * @throws IllegalArgumentException when <code>null</code> or an empty string is passed for start
   */
  public Object getStringCompanion(String start) throws IllegalArgumentException {
    return getSpecialSequenceCompanion(start);
  }
  

  /**
   * Checks if the given starting sequence of the string is known to the parser.
   * See the method description in {@link TokenizerProperties}.
   *
   * @param start     the starting sequence of a string
   * @return <code>true</code> if the string is registered, 
   *         <code>false</code> otherwise
   */
  public boolean stringExists(String start) {
    return getString(start) != null;
  }

  
  /** Get the full description of a string property starting with the given
   * prefix. See the method description in {@link TokenizerProperties}.
   * @param start the starting sequence of a string
   * @return the full string description or <code>null</code>
   * @throws IllegalArgumentException when <code>null</code> or an empty string is passed for start
   */
  public TokenizerProperty getString(String start) throws IllegalArgumentException {
    return getSpecialSequence(start);
  }
  

  /**
   * This method returns an {@link java.util.Iterator} of {@link TokenizerProperty}
   * objects. See the method description in {@link TokenizerProperties}.
   *
   * @return enumeration of {@link TokenizerProperty} objects
   */  
  public Iterator getStrings() {
    return new SpecialSequencesIterator(this, Token.STRING);
  }
  
  /**
   * Setting the whitespace character set of the tokenizer. 
   * See the method description in {@link TokenizerProperties}.
   *
   * @param whitespaces the whitespace set
   */
  public void setWhitespaces(String whitespaces) {
    synchronized(_syncWhitespaces) {
      String ws;
      
      // set whitespaces and detect if end-of-line characters are part of them
      ws = (whitespaces != null) ? whitespaces : "";
      if (ws.indexOf('\n') >= 0 || ws.indexOf('\r') >= 0) {
        _newlineIsWhitespace = true;
      }

      // for fast whitespace scanning check for the most common ones
      if (   ws.equals(DEFAULT_WHITESPACES)
          || (   ws.length()      == 4
              && ws.indexOf('\n') >= 0 
              && ws.indexOf('\r') >= 0
              && ws.indexOf(' ')  >= 0
              && ws.indexOf('\t') >= 0)) {
        _defaultWhitespaces = true;
      }
      
      // set the right whitespaces
      String oldValue;
      String removed;
      
      if ((_flags & F_NO_CASE) == 0) {
        oldValue            = _whitespacesCase;
        removed             = _whitespacesNoCase;
        _whitespacesCase    = ws;
        _whitespacesNoCase  = "";
      } else {
        ws                  = ws.toUpperCase();
        oldValue            = _whitespacesNoCase;
        removed             = _whitespacesCase;
        _whitespacesCase    = "";
        _whitespacesNoCase  = ws;
      }
      handleEvent(Token.WHITESPACE, ws, oldValue, removed);
    }
  }

  /**
   * Obtaining the whitespace character set.
   * See the method description in {@link TokenizerProperties}.
   *
   * @see #setWhitespaces
   * @return the currently active whitespace set
   */
  public String getWhitespaces() {
    synchronized(_syncWhitespaces) {
      return _whitespacesCase + _whitespacesNoCase;
    }
  }
  
  /**
   * Setting the separator set. 
   * See the method description in {@link TokenizerProperties}.
   *
   * @param separators the set of separating characters
   */
  public void setSeparators(String separators) {
    synchronized(_syncSeparators) {
      String sep = (separators != null) ? separators : "";
      String oldValue;
      String removed;
      
      if ((_flags & F_NO_CASE) == 0) {
        oldValue          = _separatorsCase;
        removed           = _separatorsNoCase;
        _separatorsCase   = sep;
        _separatorsNoCase = "";
      } else {
        sep               = sep.toUpperCase();
        oldValue          = _separatorsNoCase;
        removed           = _separatorsCase;
        _separatorsCase   = "";
        _separatorsNoCase = sep;
      }
      handleEvent(Token.SEPARATOR, sep, oldValue, removed);
    }
  }
  
  /**
   * Obtaining the separator set of the <code>Tokenizer</code>.
   * See the method description in {@link TokenizerProperties}.
   *
   * @see #setSeparators
   * @return the currently used set of separating characters
   */
  public String getSeparators() {
    synchronized(_syncSeparators) {
      return _separatorsCase + _separatorsNoCase;
    }
  }
  
  /** 
   * Registering a the starting sequence of a line comment.
   * See the method description in {@link TokenizerProperties}.
   *
   * @param lineComment the starting sequence of the line comment
   * @throws IllegalArgumentException when <code>null</code> or an empty string is passed for start sequence of the line comment
   */
  public void addLineComment(String lineComment) throws IllegalArgumentException {
    addLineComment(lineComment, null);
  }

  /** 
   * Registering a the starting sequence of a line comment.
   *
   * See the method description in {@link TokenizerProperties}.
   * @param lineComment the starting sequence of a line comment
   * @param companion the associated information
   * @throws IllegalArgumentException when <code>null</code> or an empty string is passed for start sequence of the line comment
   */
  public void addLineComment(String lineComment, Object companion) throws IllegalArgumentException {
    addLineComment(lineComment, companion, getParseFlags());
  }

  
  /** 
   * Registering a the starting sequence of a line comment.
   * See the method description in {@link TokenizerProperties}.
   *
   * @param lineComment the starting sequence of a line comment
   * @param companion the associated information
   * @param flags modification flags
   * @throws IllegalArgumentException when <code>null</code> or an empty string 
   *         is passed for start sequence of the line comment
   */
  public void addLineComment(String lineComment, Object companion, int flags) throws IllegalArgumentException {
    addSpecialSequence(
      new TokenizerProperty(Token.LINE_COMMENT, new String[] { lineComment }, 
                            companion, flags)
    );
  }  

  /** Removing a certain line comment.
   * See the method description in {@link TokenizerProperties}.
   * @param lineComment the starting sequence of the line comment
   * @throws IllegalArgumentException when <code>null</code> or an empty string is passed for start sequence of the line comment
   */  
  public void removeLineComment(String lineComment) throws IllegalArgumentException {
    removeSpecialSequence(lineComment);
  }
  
  
  /** Retrieving the associated object of a certain line comment.
   * See the method description in {@link TokenizerProperties}.
   * @param lineComment the starting sequence of the line comment
   * @return the object associated with the line comment
   * @throws IllegalArgumentException when <code>null</code> or an empty string is passed for start sequence of the line comment
   */  
  public Object getLineCommentCompanion(String lineComment) throws IllegalArgumentException {
    return getSpecialSequenceCompanion(lineComment);
  }

  /**
   * Checks if the give line comment is known.
   * See the method description in {@link TokenizerProperties}.
   *
   * @param lineComment the starting sequence of the line comment
   * @return <code>true</code> if the line comment is known, 
   *         <code>false</code> otherwise
   */  
  public boolean lineCommentExists(String lineComment) {
    return getLineComment(lineComment) != null;
  }
  
  /** Get the full description of a line comment property starting with the given
   * prefix.
   * See the method description in {@link TokenizerProperties}.
   * @param lineComment the starting sequence of the line comment
   * @return the full line comment description or <code>null</code>
   * @throws IllegalArgumentException when <code>null</code> or an empty string is passed for start sequence of the line comment
   */
  public TokenizerProperty getLineComment(String lineComment) throws IllegalArgumentException {
    return getSpecialSequence(lineComment);
  }

  /**
   * This method returns an {@link java.util.Iterator} of {@link TokenizerProperty}
   * objects.
   * See the method description in {@link TokenizerProperties}.
   *
   * @return enumeration of {@link TokenizerProperty} objects
   */  
  public Iterator getLineComments() {
    return new SpecialSequencesIterator(this, Token.LINE_COMMENT);
  }
  
  /** Registering a block comment. See the method description in
   * {@link TokenizerProperties}.
   * @param start the starting sequence of the block comment
   * @param end the finishing sequence of the block comment
   * @throws IllegalArgumentException when <code>null</code> or an empty string 
   *        is passed for start / end sequence of the block comment
   */  
  public void addBlockComment(String start, String end) throws IllegalArgumentException {
    addBlockComment(start, end, null);
  }
  
  /** 
   * Registering a block comment.
   * See the method description in {@link TokenizerProperties}.
   *
   * @param start the starting sequence of the block comment
   * @param end the finishing sequence of the block comment
   * @param companion information object associated with this block comment
   * @throws IllegalArgumentException when <code>null</code> or an empty string 
   *        is passed for start / end sequence of the block comment
   */  
  public void addBlockComment(String start, String end, Object companion) throws IllegalArgumentException {
    addBlockComment(start, end, companion, getParseFlags());
  }
  
  /** 
   * Registering a block comment.
   * See the method description in {@link TokenizerProperties}.
   *
   * @param start the starting sequence of the block comment
   * @param end the finishing sequence of the block comment
   * @param companion information object associated with this block comment
   * @param flags modification flags
   * @throws IllegalArgumentException when <code>null</code> or an empty string 
   *        is passed for start / end sequence of the block comment
   */
  public void addBlockComment(String start, String end, Object companion, int flags) throws IllegalArgumentException {
    addSpecialSequence(
      new TokenizerProperty(Token.BLOCK_COMMENT, new String[] { start, end }, 
                            companion, flags)
    );
  }
  
  /** Removing a certain block comment.
   * See the method description in {@link TokenizerProperties}.
   * @param start the starting sequence of the block comment
   * @throws IllegalArgumentException when <code>null</code> or an empty string is passed for start sequence of the block comment
   */  
  public void removeBlockComment(String start) throws IllegalArgumentException {
    removeSpecialSequence(start);
  }
  
  /** 
   * Retrieving a certain block comment.
   * See the method description in {@link TokenizerProperties}.
   *
   * @param start the starting sequence of the block comment
   * @return the associated object of the block comment
   * @throws IllegalArgumentException when <code>null</code> or an empty string 
   * is passed for start sequence of the block comment
   */  
  public Object getBlockCommentCompanion(String start) throws IllegalArgumentException {
    return getSpecialSequenceCompanion(start);
  }
  
  
  /**
   * This method returns an {@link java.util.Iterator} of {@link TokenizerProperty}
   * objects.
   * See the method description in {@link TokenizerProperties}.
   *
   * @return enumeration of {@link TokenizerProperty} objects
   */  
  public Iterator getBlockComments() {
    return new SpecialSequencesIterator(this, Token.BLOCK_COMMENT);
  }
  
  
  /**
   * Checks if the given block comment is known.
   * See the method description in {@link TokenizerProperties}.
   *
   * @param start the starting sequence of the block comment
   * @return <code>true</code> if the block comment is known, 
   *         <code>false</code> otherwise
   */  
  public boolean blockCommentExists(String start) {
    return getBlockComment(start) != null;
  }
  
  
  /** 
   * Get the full description of a block comment property starting with the given
   * prefix.
   * See the method description in {@link TokenizerProperties}.
   *
   * @param start the starting sequence of the block comment
   * @return the full block comment description or <code>null</code>
   * @throws IllegalArgumentException when <code>null</code> or an empty string 
   *        is passed for start sequence of the block comment
   */
  public TokenizerProperty getBlockComment(String start) throws IllegalArgumentException {
    return getSpecialSequence(start);
  }

  /**
   * Registering a special sequence of characters.
   * See the method description in {@link TokenizerProperties}.
   *
   * @param specSeq   special sequence to register
   * @throws IllegalArgumentException if the given sequence is empty or null
   * @see   #addKeyword
   * @see   #setSeparators
   */
  public void addSpecialSequence(String specSeq) throws IllegalArgumentException {
    addSpecialSequence(specSeq, null);
  }
  
  
  /**
   * Registering a special sequence of characters.
   * See the method description in {@link TokenizerProperties}.
   *
   * @param specSeq     special sequence to register
   * @param companion information object associated with this special sequence
   * @throws IllegalArgumentException if the given sequence is empty or null
   * @see #addKeyword
   * @see #setSeparators
   */  
  public void addSpecialSequence(String specSeq, Object companion) throws IllegalArgumentException {
    addSpecialSequence(specSeq, companion, getParseFlags());
  }

  
  /**
   * Registering a special sequence of characters.
   * See the method description in {@link TokenizerProperties}.
   *
   * @param specSeq     special sequence to register
   * @param companion   information object associated with this special sequence
   * @param flags       modification flags
   * @throws IllegalArgumentException if the given sequence is empty or null
   * @see #addKeyword
   * @see #setSeparators
   */
  public void addSpecialSequence(String specSeq, Object companion, int flags) throws IllegalArgumentException {
    addSpecialSequence(
      new TokenizerProperty(Token.SPECIAL_SEQUENCE, new String[] { specSeq }, 
                            companion, flags)
    );
  }  
    

  /**
   * Deregistering a special sequence from the parser.
   * See the method description in {@link TokenizerProperties}.
   *
   * @param specSeq   sequence to remove
   * @throws IllegalArgumentException if the given sequence is empty or null
   */  
  public void removeSpecialSequence(String specSeq) throws IllegalArgumentException {
    checkArgument(specSeq, "Special sequence");
    
    synchronized(_sequences) {
      for (int pos = 0; pos < _sequences.length; ++pos) {
        if (_sequences[pos] != null) {
          TokenizerProperty prop = _sequences[pos].searchBinary(specSeq, null, true);
          
          if (prop != null) {
            notifyListeners(new TokenizerPropertyEvent(TokenizerPropertyEvent.PROPERTY_REMOVED, prop));
          }
        }
      }
    }
  }
  
  
  /**
   * Retrieving the companion of the given special sequence.
   * See the method description in {@link TokenizerProperties}.
   *
   * @param specSeq   sequence to remove
   * @return the object associated with the special sequence
   * @throws IllegalArgumentException if the given sequence is empty or null
   */
  public Object getSpecialSequenceCompanion(String specSeq) throws IllegalArgumentException {
    // check parameter
    checkArgument(specSeq, "Special sequence");
    
    // fetch companion
    TokenizerProperty prop = searchBinary(specSeq);

    if (prop != null) {
      return prop.getCompanion();
    } else {
      return null;
    }
  }
  

  /**
   * This method returns an {@link java.util.Iterator} of {@link TokenizerProperty}
   * objects.
   * See the method description in {@link TokenizerProperties}.
   *
   * @return enumeration of {@link TokenizerProperty} objects
   */  
  public Iterator getSpecialSequences() {
    return new SpecialSequencesIterator(this, Token.SPECIAL_SEQUENCE);
  }
  
  
  /**
   * Checks if the given special sequence is known to the <code>Tokenizer</code>.
   * See the method description in {@link TokenizerProperties}.
   *
   * @param specSeq sequence to check
   * @return <code>true</code> if the block comment is known,
   *       <code>false</code> otherwise
   */  
  public boolean specialSequenceExists(String specSeq) {
    return getSpecialSequence(specSeq) != null;
  }
  
  
  /**
   * Get the full description of a special sequence property.
   * See the method description in {@link TokenizerProperties}.
   *
   * @param specSeq sequence to find
   * @return the full sequence description or <code>null</code>
   * @throws IllegalArgumentException if the given sequence is empty or null
   */
  public TokenizerProperty getSpecialSequence(String specSeq) throws IllegalArgumentException {
    if (specSeq != null) {
      return searchBinary(specSeq);
    } else {
      return null;
    }
  }

  /**
   * Registering a keyword. 
   * See the method description in {@link TokenizerProperties}.
   *
   * @param keyword   keyword to register
   * @throws IllegalArgumentException if the given keyword is empty or null
   */
  public void addKeyword(String keyword) throws IllegalArgumentException {
    addKeyword(keyword, null);
  }
  
  
  /**
   * Registering a keyword.
   * See the method description in {@link TokenizerProperties}.
   *
   * @param keyword   keyword to register
   * @param companion information object associated with this keyword
   * @throws IllegalArgumentException if the given keyword is empty or null
   */  
  public void addKeyword(String keyword, Object companion) throws IllegalArgumentException {
    addKeyword(keyword, companion, getParseFlags());
  }
  
  
  /**
   * Registering a keyword.
   * See the method description in {@link TokenizerProperties}.
   *
   * @param keyword   keyword to register
   * @param companion information object associated with this keyword
   * @throws IllegalArgumentException if the given keyword is empty or null
   */  
  public void addKeyword(String keyword, Object companion, int flags) throws IllegalArgumentException {
    // check parameter
    checkArgument(keyword, "Keyword");
    
    // normalize flags
    flags = normalizeFlags(flags);

    // there is a HashMap for case-sensitive and one for non case-sensitive
    // keywords
    // case-insensitive comparison must be done by comparing normalized strings
    // we choose the upper case (lower case would be fine as well)
    synchronized(_keywords) {
      HashMap table;

      if ((flags & F_CASE) != 0) {
        if (_keywords[0] == null) {
          _keywords[0] = new HashMap();
        }
        table = _keywords[0];
      } else {
        if (_keywords[1] == null) {
          _keywords[1] = new HashMap();
        }
        table   = _keywords[1];
        keyword = keyword.toUpperCase();
      }

      // put keyword and its property
      TokenizerProperty prop    = new TokenizerProperty(Token.KEYWORD, 
                                                        new String[] { keyword }, 
                                                        companion, flags);
      TokenizerProperty oldProp = (TokenizerProperty)table.put(keyword, prop);

      if (oldProp == null) {
        notifyListeners(new TokenizerPropertyEvent(TokenizerPropertyEvent.PROPERTY_ADDED, prop));
      } else if ( ! oldProp.equals(prop)) {
        notifyListeners(new TokenizerPropertyEvent(TokenizerPropertyEvent.PROPERTY_MODIFIED, prop, oldProp));
      }
    }
  }
  
  
  /**
   * Deregistering a keyword.
   * See the method description in {@link TokenizerProperties}.
   *
   * @param keyword   keyword to remove
   * @throws IllegalArgumentException if the given keyword is empty or null
   */  
  public void removeKeyword(String keyword) throws IllegalArgumentException {
    // check parameter
    checkArgument(keyword, "Keyword");

    // remove it
    synchronized(_keywords) {
      TokenizerProperty prop;
      
      if (_keywords[0] != null) {
        if ((prop = (TokenizerProperty)_keywords[0].remove(keyword)) != null) {
          notifyListeners(new TokenizerPropertyEvent(TokenizerPropertyEvent.PROPERTY_REMOVED, prop));
        }
      }
      if (_keywords[1] != null) {
        if ((prop = (TokenizerProperty)_keywords[1].remove(keyword.toUpperCase())) != null) {
          notifyListeners(new TokenizerPropertyEvent(TokenizerPropertyEvent.PROPERTY_REMOVED, prop));
        }
      }
    }
  }
  
  
  /**
   * Retrieving the companion of the given keyword.
   * See the method description in {@link TokenizerProperties}.
   *
   * @param keyword   keyword thats companion is sought
   * @return the object associated with the keyword
   * @throws IllegalArgumentException if the given keyword is empty or null
   */
  public Object getKeywordCompanion(String keyword) throws IllegalArgumentException {
    TokenizerProperty prop = getKeyword(keyword);
    
    if (prop != null) {
      return prop.getCompanion();
    } else {
      return null;
    }
  }

  
  /**
   * This method returns an {@link java.util.Iterator} of {@link TokenizerProperty}
   * objects.
   * See the method description in {@link TokenizerProperties}.
   *
   * @return iteration of {@link TokenizerProperty} objects
   */  
  public Iterator getKeywords() {
    synchronized(_keywords) {
      return new MapIterator(this, _keywords[0], _keywords[1]);
    }
  }
  
  
  /**
   * Checks if the given keyword is known to the <code>Tokenizer</code>.
   * See the method description in {@link TokenizerProperties}.
   *
   * @param keyword   keyword to search
   * @return <code>true</code> if the keyword is known,
   *        <code>false</code> otherwise
   */  
  public boolean keywordExists(String keyword) {
    try {
      return getKeyword(keyword) != null;
    } catch (IllegalArgumentException ex) {
      return false;
    }
  }

  
  /**
   * Get the full description of a keyword property.
   * See the method description in {@link TokenizerProperties}.
   *
   * @param keyword   keyword to search
   * @return the full sequence description or <code>null</code>
   * @throws IllegalArgumentException if the given keyword is empty or null
   */
  public TokenizerProperty getKeyword(String keyword) throws IllegalArgumentException {
    // check parameter
    checkArgument(keyword, "Keyword");

    // fetch the keyword property
    TokenizerProperty prop;
    
    synchronized(_keywords) {
      if (_keywords[0] != null) {
        if ((prop = (TokenizerProperty)_keywords[0].get(keyword)) != null) {
          return prop;
        }
      }
      if (_keywords[1] != null) {
        if ((prop = (TokenizerProperty)_keywords[1].get(keyword.toUpperCase())) != null) {
          return prop;
        }
      }
      return null;
    }
  }
  
  
  //---------------------------------------------------------------------------
  // pattern properties
  //
  
  /**
   * Registering a pattern. See the method description in {@link TokenizerProperties}.
   *
   * @param   pattern   the regular expression to be added
   * @throws  IllegalArgumentException when <code>null</code> or an empty pattern
   *          is passed
   * @see     #removePattern
   * @see     #addPattern(String, Object)
   * @see     #addPattern(String, Object, int)
   */
  public void addPattern(String pattern) throws IllegalArgumentException {
    addPattern(pattern, null);
  }

  /**
   * Registering a pattern with an associated object. See the method description 
   * in {@link TokenizerProperties}.
   *
   * @param   pattern     the regular expression to be added
   * @param   companion   information object associated with this pattern
   * @throws  IllegalArgumentException when <code>null</code> or an empty pattern
   *          is passed
   * @see     #removePattern
   * @see     #addPattern(String)
   * @see     #addPattern(String, Object, int)
   */
  public void addPattern(String pattern, Object companion) throws IllegalArgumentException {
    addPattern(pattern, companion, getParseFlags());
  }

  /**
   * Registering a pattern with an associated object. See the method description 
   * in {@link TokenizerProperties}.
   *
   * @param   pattern     the regular expression to be added
   * @param   companion   information object associated with this keyword
   * @param   flags       modification flags 
   * @throws  IllegalArgumentException when <code>null</code> or an empty pattern
   *          is passed
   * @see     #removePattern
   * @see     #addPattern(String)
   * @see     #addPattern(String, Object)
   */
  public void addPattern(String pattern, Object companion, int flags)
    throws IllegalArgumentException
  {
    // check parameter
    checkArgument(pattern, "Pattern");
    
    // normalize flags
    flags = normalizeFlags(flags);

    // construct the pattern
    PatternData       data = null;
    TokenizerProperty prop = new TokenizerProperty(Token.PATTERN, new String[] { pattern }, companion, flags);
    
    try {
      data = new PatternData(prop);
    } catch (Exception ex) {
      throw new ExtIllegalArgumentException(ex);
    }
                                                      
    // Register pattern. First search for existing one
    synchronized(_patterns) {
      for (int index = 0; index < _patterns.size(); ++index) {
        PatternData       oldData = (PatternData)_patterns.get(index);
        TokenizerProperty oldProp = oldData.getProperty();
       
        if (oldProp.getImages()[0].equals(pattern)) {
          _patterns.set(index, data);
          if ( ! oldProp.equals(prop)) {
            notifyListeners(new TokenizerPropertyEvent(TokenizerPropertyEvent.PROPERTY_MODIFIED, data.getProperty(), oldProp));
          }
          return;
        }
      }
                                                      
      // not found -> register new pattern
      _patterns.add(data);
      notifyListeners(new TokenizerPropertyEvent(TokenizerPropertyEvent.PROPERTY_ADDED, data.getProperty()));
    }
  }
  
  /**
   * Removing a pattern. See the method description in {@link TokenizerProperties}.
   *
   * @param   pattern     the regular expression to be removed
   * @throws  IllegalArgumentException when <code>null</code> or an empty string 
   *          is passed
   */  
  public void removePattern(String pattern) throws IllegalArgumentException {
    // check parameter
    checkArgument(pattern, "Pattern");

    // remove it
    synchronized(_patterns) {
      for (int index = 0; index < _patterns.size(); ++index) {
        PatternData       data = (PatternData)_patterns.get(index);
        TokenizerProperty prop = data.getProperty();

        if (prop.getImages()[0].equals(pattern)) {
          _patterns.remove(index);
          notifyListeners(new TokenizerPropertyEvent(TokenizerPropertyEvent.PROPERTY_REMOVED, prop));
          break;
        }
      }
    }
  }
  
  /**
   * Retrieving the information associated with a given pattern. See the method 
   * description in {@link TokenizerProperties}.
   *
   * @param   pattern     the regular expression to be removed
   * @return  the associated information or <code>null</code>
   * @throws  IllegalArgumentException when <code>null</code> or an emtpy pattern
   *          is passed
   */
  public Object getPatternCompanion(String pattern) throws IllegalArgumentException {
    TokenizerProperty prop = getPattern(pattern);
    
    if (prop != null) {
      return prop.getCompanion();
    } else {
      return null;
    }
  }
  
  /**
   * Checks if the given pattern is known to the parser.
   * See the method description in {@link TokenizerProperties}.
   *
   * @param   pattern     the regular expression to be looked for
   * @return  <code>true</code> if the pattern is registered, 
   *          <code>false</code> otherwise
   */
  public boolean patternExists(String pattern) {
    try {
      return getPattern(pattern) != null;
    } catch (IllegalArgumentException ex) {
      return false;
    }
  }
  
  /**
   * Get the full description of a string property starting with the given 
   * prefix. The method returns <code>null</code> if the passed <code>start</code>
   * parameter cannot be mapped to a known string description ({@link #stringExists}
   * would return <code>false</code>). 
   *
   * @param   pattern   the regular expression to be looked for
   * @return  the full pattern description or <code>null</code>
   * @throws  IllegalArgumentException when <code>null</code> or an emtpy pattern 
   *          is passed
   */
  public TokenizerProperty getPattern(String pattern) throws IllegalArgumentException {
    // check parameter
    checkArgument(pattern, "Pattern");

    // fetch the pattern property
    synchronized(_patterns) {
      for (int index = 0; index < _patterns.size(); ++index) {
        PatternData       data = (PatternData)_patterns.get(index);
        TokenizerProperty prop = data.getProperty();

        if (prop.getImages()[0].equals(pattern)) {
          return prop;
        }
      }
      return null;
    }
  }

  /**
   * This method returns an {@link java.util.Iterator} of {@link TokenizerProperty}
   * objects. Each <code>TokenizerProperty</code> object contains a pattern and 
   * its companion if such an associated object exists.
   *
   * @return enumeration of {@link TokenizerProperty} objects
   */  
  public Iterator getPatterns() {
    return new PatternIterator(this);
  }
  

  /**
   * Registering a {@link TokenizerProperty}.
   * See the method description in {@link TokenizerProperties}.
   *
   * @param   property   property to register
   * @throws  IllegalArgumentException when <code>null</code>, an incomplete or 
   *          otherwise unusable property is passed
   */
  public void addProperty(TokenizerProperty property) throws IllegalArgumentException {
    // check the parameter
    checkPropertyArgument(property);
    
    // add property according to type
    String[] images = property.getImages();
    
    switch (property.getType()) {
    case Token.STRING:
    case Token.LINE_COMMENT:
    case Token.BLOCK_COMMENT:
    case Token.SPECIAL_SEQUENCE:
      addSpecialSequence(property);
      break;

    case Token.KEYWORD:
      addKeyword(images[0], property.getCompanion(), property.getFlags());
      break;

    case Token.PATTERN:
      addPattern(images[0], property.getCompanion(), property.getFlags());
      break;

    case Token.WHITESPACE:
    case Token.SEPARATOR:
    default:
      throw new ExtIllegalArgumentException("Unsupported property type {0}. (Leading) image \"{1}\".", 
                                            new Object[] { new Integer(property.getType()), images[0] } );
    }
  }
  
  /**
   * Deregistering a {@link TokenizerProperty} from the store.
   * See the method description in {@link TokenizerProperties}.
   *
   * @param   property    property to register
   * @throws  IllegalArgumentException when <code>null</code>, an incomplete or 
   *          otherwise unusable property is passed
   */  
  public void removeProperty(TokenizerProperty property) throws IllegalArgumentException {
    // check the parameter
    checkPropertyArgument(property);
    
    // removing property according to type
    String[] images = property.getImages();
    
    switch (property.getType()) {
    case Token.LINE_COMMENT:
      removeLineComment(images[0]);
      break;

    case Token.BLOCK_COMMENT:
      removeBlockComment(images[0]);
      break;

    case Token.STRING:
      removeString(images[0]);
      break;

    case Token.KEYWORD:
      removeKeyword(images[0]);
      break;

    case Token.SPECIAL_SEQUENCE:
      removeSpecialSequence(images[0]);
      break;

    case Token.PATTERN:
      removePattern(images[0]);
      break;

    case Token.WHITESPACE:
    case Token.SEPARATOR:
    default:
      throw new ExtIllegalArgumentException("Unsupported property type {0}. (Leading) image \"{1}\".", 
                                            new Object[] { new Integer(property.getType()), images[0] } );
    }
  }
  
  /**
   * This method returns an {@link java.util.Iterator} of {@link TokenizerProperty}
   * objects.
   * See the method description in {@link TokenizerProperties}.
   *
   * @return enumeration of {@link TokenizerProperty} objects
   */  
  public Iterator getProperties() {
    return new FullIterator(this);
  }
  
  /**
   * Checks if the given {@link TokenizerProperty} is known to this <code>TokenizerProperties</code>
   * instance. 
   * See the method description in {@link TokenizerProperties}.
   *
   * @param   property  the property to search
   * @return <code>true</code> if the property is known,
   *        <code>false</code> otherwise
   */  
  public boolean propertyExists(TokenizerProperty property) {
    // trivial case of null
    if (property == null || property.getImages() == null || property.getImages()[0] == null) {
      return false;
    }
    
    // which type ?
    String image = property.getImages()[0];
    
    switch (property.getType()) {
    case Token.LINE_COMMENT:
      return lineCommentExists(property.getImages()[0]);
    case Token.BLOCK_COMMENT:
      return blockCommentExists(property.getImages()[0]);
    case Token.KEYWORD:
      return keywordExists(property.getImages()[0]);
    case Token.PATTERN:
      return patternExists(property.getImages()[0]);
    default:
      return specialSequenceExists(property.getImages()[0]);
    }
  }

  
  /**
   * Registering a new {@link TokenizerPropertyListener}.
   * See the method description in {@link TokenizerProperties}.
   *
   * @param listener  the new {@link TokenizerPropertyListener}
   * @see #removeTokenizerPropertyListener
   */
  public void addTokenizerPropertyListener(TokenizerPropertyListener listener) {
    if (listener != null) {
      synchronized(_listeners) {
        _listeners.add(listener);
      }
    }
  }
  
  
  /**
   * Removing a listener from the list of registered {@link TokenizerPropertyListener}
   * instances.
   * See the method description in {@link TokenizerProperties}.
   *
   * @param listener  the {@link TokenizerPropertyListener} to deregister
   * @see #addTokenizerPropertyListener
   */
  public void removeTokenizerPropertyListener(TokenizerPropertyListener listener) {
    if (listener != null) {
      synchronized(_listeners) {
        _listeners.remove(listener);
      }
    }
  }
 
  
  //---------------------------------------------------------------------------
  // Methods of the DataMapper interface
  //
  
  /**
   * Setting the backing {@link de.susebox.jtopas.TokenizerProperties} instance
   * this <code>DataMapper</code> is working with. Usually, the <code>DataMapper</code>
   * interface is implemented by <code>TokenizerProperties</code> implementations,
   * too. Otherwise the {@link de.susebox.jtopas.Tokenizer} using the 
   * <code>TokenizerProperties</code>, will construct a default <code>DataMapper</code>
   * an propagate the <code>TokenizerProperties</code> instance by calling this 
   * method.
   *<br>
   * The method should throw an {@link java.lang.UnsupportedOperationException}
   * if this <code>DataMapper</code> is an extension to an <code>TokenizerProperties</code>
   * implementation.
   *
   * @param   props   the {@link de.susebox.jtopas.TokenizerProperties}
   * @throws  UnsupportedOperationException is this is a <code>DataMapper</code>
   *          implemented by a {@link de.susebox.jtopas.TokenizerProperties}
   *          implementation
   * @throws  NullPointerException  if no {@link TokenizerProperties} are given
   */
  public void setTokenizerProperties(TokenizerProperties props) 
    throws UnsupportedOperationException, NullPointerException
  {
    throw new ExtUnsupportedOperationException(
                  "Class {0} already defines the {1} interface.",
                  new Object[] { StandardTokenizerProperties.class.getName(), 
                                 DataMapper.class.getName() } );
  }

  /**
   * The method retrieves the backing {@link de.susebox.jtopas.TokenizerProperties}
   * instance, this <code>DataMapper</code> is working on. For implementations
   * of the <code>TokenizerProperties</code> interface that also implement the
   * <code>DataMapper</code> interface, this method returns the instance itself
   * it is called on.
   *<br>
   * Otherwise the method returns the <code>TokenizerProperties</code> instance 
   * passed through the last call to {@link #setTokenizerProperties} or <code>null</code>
   * if no such call has taken place so far.
   *
   * @return the backing {@link de.susebox.jtopas.TokenizerProperties} or <code>null</code>
   */
  public TokenizerProperties getTokenizerProperties() {
    return this;
  }

  /**
   * This method checks if the character is a whitespace. Implement Your own
   * code for situations where this default implementation is not fast enough
   * or otherwise not really good.
   *
   * @param testChar  check this character
   * @return <code>true</code> if the given character is a whitespace,
   *         <code>false</code> otherwise
   */
  public boolean isWhitespace(char testChar) {
    synchronized(_syncWhitespaces) {
      if (_defaultWhitespaces) {
        switch (testChar) {
        case ' ':
        case '\n':
        case '\r':
        case '\t':
          return true;
        default:
          return false;
        }
      } else {
        return (   indexInSet          (testChar, _whitespacesCase)   >= 0 
                || indexInSetIgnoreCase(testChar, _whitespacesNoCase) >= 0);
      }
    }
  }
      
 
  /**
   * This method detects the number of whitespace characters the data range given
   * through the {@link DataProvider} parameter starts with.
   *
   * @param   dataProvider  the source to get the data range from
   * @return  number of whitespace characters starting from the given offset
   * @throws  TokenizerException failure while reading data from the input stream
   * @throws  NullPointerException  if no {@link DataProvider} is given
   * @see     de.susebox.jtopas.spi.DataProvider
   */
  public int countLeadingWhitespaces(DataProvider dataProvider) throws NullPointerException {
    char[]  data     = dataProvider.getData();
    int     startPos = dataProvider.getStartPosition();
    int     maxChars = dataProvider.getLength();
    int     len      = 0;
    
    while (len < maxChars) {
      if ( ! isWhitespace(data[startPos + len])) {
        break;
      }
      len++;
    }
    return len;
  }
  
 
  /** 
   * If a {@link Tokenizer} performs line counting, it is often nessecary to
   * know if newline characters is considered to be a whitespace. See {@link WhitespaceHandler}
   * for details.
   *
   * @return  <code>true</code> if newline characters are in the current whitespace set,
   *          <code>false</code> otherwise
   *
   */
  public boolean newlineIsWhitespace() {
    synchronized(_syncWhitespaces) {
      return _newlineIsWhitespace;
    }
  }  
  

  /**
   * This method checks the given character if it is a separator.
   *
   * @param testChar  check this character
   * @return <code>true</code> if the given character is a separator,
   *         <code>false</code> otherwise
   */
  public boolean isSeparator(char testChar) {
    synchronized(_syncSeparators) {
      return   indexInSet          (testChar, _separatorsCase)   >= 0 
            || indexInSetIgnoreCase(testChar, _separatorsNoCase) >= 0;
    }
  }

  
  /**
   * This method checks if a given range of data starts with a special sequence,
   * a comment or a string. These three types of token are testet together since
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
   * @throws  TokenizerException failure while reading more data
   * @throws  NullPointerException  if no {@link DataProvider} is given
   */
  public TokenizerProperty startsWithSequenceCommentOrString(DataProvider dataProvider) 
    throws TokenizerException, NullPointerException
  {
    // we need the longest possible match
    TokenizerProperty prop = null;

    synchronized(_sequences) {
      for (int pos = 0; pos < _sequences.length; ++pos) {
        if (_sequences[pos] != null) {
          TokenizerProperty p = _sequences[pos].searchBinary(null, dataProvider, false);

          if (   p != null 
              && (   prop == null
                  || p.getImages()[0].length() > prop.getImages()[0].length())) {
            prop = p;
          }
        }
      }
    }
    return prop;
  }

  /**
   * This method returns the length of the longest special sequence, comment or
   * string prefix that is known to this <code>SequenceHandler</code>. When
   * calling {@link #startsWithSequenceCommentOrString}, the passed {@link DataProvider}
   * parameter will supply at least this number of characters (see {@link DataProvider#getLength}).
   * If less characters are provided, EOF is reached.
   *
   * @return  the number of characters needed in the worst case to identify a 
   *          special sequence
   */
  public int getSequenceMaxLength() {
    return _sequenceMaxLength;
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
  public TokenizerProperty isKeyword(DataProvider dataProvider)
    throws TokenizerException, NullPointerException
  {
    TokenizerProperty prop = null;
    
    synchronized(_keywords) {
      if (_keywords[0] != null || _keywords[1] != null) {
        String  keyword  = new String(dataProvider.getData(), dataProvider.getStartPosition(), dataProvider.getLength());

        if (_keywords[0] != null) {
          prop = (TokenizerProperty)_keywords[0].get(keyword);
        }
        if (prop == null && _keywords[1] != null) {
          keyword = keyword.toUpperCase();
          prop    = (TokenizerProperty)_keywords[1].get(keyword);
        }
      }
    }
    return prop;
  }
  
  
  /**
   * This method checks if the start of a character range given through the 
   * {@link DataProvider} matches a pattern.
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
    throws TokenizerException, NullPointerException
  {
    for (int index = 0; index < _patterns.size(); ++index) {
      PatternData data = (PatternData)_patterns.get(index);
      int         res  = data.matches(dataProvider, property);
      
      switch (res) {
      case MATCH: 
      case INCOMPLETE_MATCH:
      case COMPLETE_MATCH:
        return res;
      }
    } 
    return NO_MATCH;
  }

  
  //---------------------------------------------------------------------------
  // Implementation
  //

  /**
   * Normalize flags. This is nessecary for the case-sensitivity flags
   * {@link TokenizerProperties#F_CASE} and {@link TokenizerProperties#F_NO_CASE}.
   * If neither <code>F_CASE</code> nor <code>F_NO_CASE</code> is set, <code>F_CASE</code>
   * is assumed. If both flags are set, <code>F_CASE</code> takes preceedence.
   *
   * @param   flags   not yet normalized flags
   * @return  the normalized flags
   */
  public int normalizeFlags(int flags) {
    if ((flags & (F_CASE | F_NO_CASE)) == 0) {
      // none set: F_CASE is the default
      flags |= F_CASE;
    } else if ((flags & F_CASE) != 0) {
      // perhaps both set: F_CASE weights more
      flags &= ~F_NO_CASE;
    }
    return flags;
  }
    
  /**
   * Checking a string parameter on null or emptiness. The method encapsulates
   * commonly used code (see {@link #addKeyword} or {@link #addSpecialSequence}
   * for example).
   *
   * @param   arg   the parameter to check
   * @param   name  a name for the <code>arg</code> parameter
   * @throws  IllegalArgumentException if the given <code>arg</code> is null or empty
   */
  private void checkArgument(String arg, String name) throws IllegalArgumentException {
    if (arg == null) {
      throw new ExtIllegalArgumentException("{0} is null.", new Object[] { name } );
    } else if (arg.length() <= 0) {
      throw new ExtIllegalArgumentException("{0} is empty.", new Object[] { name } );
    }
  }
  
  /**
   * Checking a {@link TokenizerProperty} parameter on null or missing nessecary
   * values. The method encapsulates commonly used code (see {@link #addProperty} 
   * and {@link #removeProperty}).
   *
   * @param   property    the parameter to check
   * @throws  IllegalArgumentException if the given <code>arg</code> is null or empty
   */
  private void checkPropertyArgument(TokenizerProperty property) throws IllegalArgumentException {
    // check the parameter
    if (property == null) {
      throw new ExtIllegalArgumentException("Property is null.", null );
    } else if (property.getImages() == null) {
      throw new ExtIllegalArgumentException("No image(s) given in property.", null );
    } else if (property.getImages()[0] == null) {
      throw new ExtIllegalArgumentException("No (leading) image given in property.", null );
    }
  }

  /**
   * The method fires the nessecary events when whitespace or separator sets
   * change.
   *
   * @param type          token type
   * @param newValue      the newly set value 
   * @param oldValue      the old value with case-sensitive handling
   * @param removedValue  the removed property
   */
  private void handleEvent(
    int     type, 
    String  newValue, 
    String  oldValue, 
    String  removedValue 
  ) 
  {
    if (removedValue != null && removedValue.length() > 0) {
      notifyListeners(
        new TokenizerPropertyEvent(
              TokenizerPropertyEvent.PROPERTY_REMOVED,
              new TokenizerProperty(type, new String[] { removedValue } )));
    }
    if (newValue != null && newValue.length() > 0) {
      if (oldValue == null) {
        notifyListeners(
          new TokenizerPropertyEvent(
                TokenizerPropertyEvent.PROPERTY_ADDED,
                new TokenizerProperty(type, new String[] { newValue } )));
      } else if ( ! oldValue.equals(newValue)) {
        notifyListeners(
          new TokenizerPropertyEvent(
                TokenizerPropertyEvent.PROPERTY_MODIFIED,
                new TokenizerProperty(type, new String[] { newValue } ),
                new TokenizerProperty(type, new String[] { oldValue } )));
      }
    } else if (oldValue != null && oldValue.length() > 0) {
      notifyListeners(
        new TokenizerPropertyEvent(
              TokenizerPropertyEvent.PROPERTY_REMOVED,
              new TokenizerProperty(type, new String[] { oldValue } )));
    }
  }
  
  /**
   * This method creates the sorted arrays to store the case-sensitive and
   * -insensitive special sequences, comments, strings etc.
   *
   * @param   property  the description of the new sequence
   * @throws  IllegalArgumentException if <code>null</code> or an incomplete property
   *          is passed
   */
  protected void addSpecialSequence(TokenizerProperty property) throws IllegalArgumentException {
    // check the given property
    checkPropertyArgument(property);
    
    // check various special cases
    String[] images = property.getImages();
      
    switch (property.getType()) {
    case Token.STRING:
    case Token.BLOCK_COMMENT:
      checkArgument((images.length < 2) ? null : images[1], "End sequence");
      break;
    }
    
    // add new sequence
    synchronized(_sequences) {
      int arrayIdx;
      int flags = property.getFlags();

      if ((flags & F_NO_CASE) == 0) {
        if (_sequences[0] == null) {
          _sequences[0] = new SortedArray(this, flags);
        }
        arrayIdx = 0;
      } else {
        if (_sequences[1] == null) {
          _sequences[1] = new SortedArray(this, flags);
        }
        arrayIdx = 1;
      }
      
      // add / replace property
      TokenizerProperty oldProp = _sequences[arrayIdx].addSpecialSequence(property);
      
      // adjust longest sequence length
      if (images[0].length() > _sequenceMaxLength) {
        _sequenceMaxLength = images[0].length();
      }
      
      // notify listeners
      if (oldProp == null) {
        notifyListeners(new TokenizerPropertyEvent(TokenizerPropertyEvent.PROPERTY_ADDED, property));
      } else if ( ! oldProp.equals(property)) {
        notifyListeners(new TokenizerPropertyEvent(TokenizerPropertyEvent.PROPERTY_MODIFIED, property, oldProp));
      }
    }
  }
  
  
  /**
   * Search a special sequence for the getter methods.
   *
   * @param   specSeq   (starting) sequence to be found
   * @return  the {@link TokenizerProperty} of the sequence or <code>null</code>
   */
  protected TokenizerProperty searchBinary(String specSeq) {
    synchronized(_sequences) {
      for (int pos = 0; pos < _sequences.length; ++pos) {
        TokenizerProperty prop;

        if (   _sequences[pos] != null 
            && (prop = _sequences[pos].searchBinary(specSeq, null, false)) != null) {
          return prop;
        }
      } 
      return null;
    }
  }
  
  
  /**
   * A given character is searched in the given character set ignoring the case.
   *
   * @return  index in the given character set where the given character
   *          was found or -1 when not ound
   * @see #indexInSet
   */
  protected int indexInSetIgnoreCase(char ch, String set) {
    if (set != null && set.length() > 0) {
      return indexInSet(Character.toUpperCase(ch), set);
    } else {
      return -1;
    }
  }
  
  
  /**
   * A given character is searched in the given character set. This set may
   * contain ranges, for example "a-z" for all lowercase alpha characters. To use
   * the minus sign itself, escape it by "\-".
   *
   * @return  index in the given character set where the given character
   *          was found or -1 when not ound
   */
  protected int indexInSet(char ch, String set) {
    int  len = (set != null) ? set.length() : 0;
    char start, end, setChar;
    
    for (int ii = 0; ii < len; ++ii)  {
      switch (setChar = set.charAt(ii)) {
      case '-':
        start = (ii > 0) ? set.charAt(ii - 1) : 0;
        end   = (ii < len - 1) ? set.charAt(ii + 1) : 0xFFFF;
        if (ch >= start && ch <= end) {
          return ii;
        }
        ii += 2; 
        break;
        
      case '\\':
        setChar = (ii + 1 >= len) ? 0 : set.charAt(ii + 1);
        ii++;
        /* no break */
        
      default:
        if (ch == setChar) {
          return ii;
        }
      }
    }
    
    // not found
    return -1;
  }
  
  
  /**
   * Notifying the registered listeners about a change in the properties. Listeners
   * are called in the order of their registration (see {@link addTokenizerPropertyListener}).
   *
   * @param event   the {@link TokenizerPropertyEvent} to communicate to the listeners
   */
  protected void notifyListeners(TokenizerPropertyEvent event) {
    synchronized(_listeners) {
      for (int index = 0; index < _listeners.size(); index++) {
        TokenizerPropertyListener listener = (TokenizerPropertyListener)_listeners.get(index);

        listener.propertyChanged(event);
      }
    }
  }
  
  
  //---------------------------------------------------------------------------
  // Members
  //
  
  /**
   * overall tokenizer flags.
   */
  protected int _flags = 0;
  
  /**
   * current whitespace characters including character ranges.
   */
  protected String _whitespacesCase = DEFAULT_WHITESPACES;
  
  /**
   * current whitespace characters including character ranges. Case is ignored.
   */
  protected String _whitespacesNoCase = "";
  
  /**
   * current separator characters including character ranges.
   */
  protected String _separatorsCase = DEFAULT_SEPARATORS;
  
  /**
   * current separator characters including character ranges. Case is ignored.
   */
  protected String _separatorsNoCase = "";
  
  /**
   * The first element is the <code>SortedArray</code> for the case-sensitive 
   * sequences, the second is for the case-insensitive ones.
   */
  protected SortedArray[] _sequences = new SortedArray[2];
  
  /**
   * Like the array {@link #_sequences} this two-element Array contains two
   * {@link java.util.HashMap}, the first for the case-sensitive keywords, the
   * second for the case-insensitive ones.
   */
  protected HashMap[] _keywords = new HashMap[2];
  
  /**
   * This array contains the patterns
   */
  protected ArrayList _patterns = new ArrayList();
  
  /**
   * this flag speeds up the line and column counting
   */
  protected boolean _newlineIsWhitespace  = false;
  
  /**
   * For fast whitespace scans, check the common whitespaces first
   */
  private boolean _defaultWhitespaces = false;
  
  /**
   * maximum length of special sequences
   */
  private int _sequenceMaxLength = 0;
  
  /**
   * List of {@link TokenizerPropertyListener} instances.
   */
  private ArrayList _listeners = new ArrayList();
  
  /**
   * Which regular expression parser to use
   */
  private Class _patternClass = null;

  /**
   * Synchronization object for whitespaces
   */
  private Object _syncWhitespaces = new Object();
  
  /**
   * Synchronization object for separators
   */
  private Object _syncSeparators = new Object();
}



//---------------------------------------------------------------------------
// inner classes
//

/**
 * This hidden class implements a binary tree for the special sequences. It is
 * designed as a sorted array.
 */
final class SortedArray {

  /**
   * Constructor needs the Tokenizer and the flags (compare case-sensitive or
   * not).
   *
   * @param parent    the Tokenizer constructing me
   * @param flags     <code>F_...</code> flags as defined in {@link Tokenizer}
   */
  public SortedArray(StandardTokenizerProperties parent, int flags) {
    _parent = parent;
    _flags  = flags;
  }

  /**
   * Binary search in the special sequence tree. There are two results. First,
   * the method returns a value 0, >0 or <0 for the binary search, and in the
   * <code>nearestMatch</code> the index in the tree, where a new entry should be
   * placed.<br>
   * If the returned value is 0, a new entry goes to the list in nearestMatch[0].
   *<br>
   * If the returned value is <0, a new list at nearestMatch[0] + 1 will be
   * created.<br>
   * If the returned value is >0, a new list at nearestMatch[0] will be created.
   *
   * @param startChar search this character
   * @param nearestMatch  in this one-element array the method returns the 
   *                      index next to which new entries should be inserted
   * @return 
   */
  private int searchIndex(char startChar, int[] nearestMatch) {
    // do the binary search
    char upChar = Character.toUpperCase(startChar);
    int  left   = 0;
    int  right  = _array.size() - 1;
    int  res    = -1;

    nearestMatch[0] = -1;

    // typical binary search for the matching start character
  __MAIN_LOOP__:
    while (left <= right) {
      int idx;

      // do we really have a new one
      if ((idx = (right + left) / 2) == nearestMatch[0]) {
        break;
      }
      nearestMatch[0] = idx;

      // compare the starting characters
      SpecialSequence   listElem = (SpecialSequence)_array.get(idx);
      TokenizerProperty existing = listElem._property;
      String            exSeq    = existing.getImages()[0];

      if ((_flags & TokenizerProperties.F_NO_CASE) != 0) {
        res = Character.toUpperCase(exSeq.charAt(0)) - upChar;
      } else {
        res = exSeq.charAt(0) - startChar; 
      }

      // binary branching
      if (res == 0) { 
        break;                // found the list
      } else if (res < 0) {   
        left = idx + 1;       // take the upper half 
      } else {                
        right = idx - 1;      // take the lower half
      }
    }

    // res == 0 means found
    return res;
  }

  /**
   * Retrieving a special sequence at the given position. The method does not
   * check the given positionen for being inside the array boundaries.
   *
   * @param index   the position
   * @return the special sequence 
   */
  public SpecialSequence get(int index) {
    return (SpecialSequence)_array.get(index);
  }


  /**
   * Retrieving the size of this sorted array.
   *
   * @return the size of this array
   */
  public int size() {
    return _array.size();
  }


  /**
   * Search a given sequence in the special sequences vector. This is a combined
   * method. It can search for a given sequence or take the current reading
   * position to read a potential special sequence.
   * After having found a match in the list of special sequences, the method 
   * can be advised to remove the matching element.
   * Searching is done in a hopefully very effective way by binary search and 
   * length-ordered sequences with the same first character.
   *
   * @param specSeq   candidate for special sequence or <code>null</code> if the
   *                  <code>provider</code> should be read
   * @param provider  provider for data
   * @param remove    <code>true</code> if a found sequence should be removed from 
   *                  the array, <code>false</code> otherwise
   * @return the {@link TokenizerProperty} describing the special sequence or <code>null</code>
   */
  protected TokenizerProperty searchBinary(String specSeq, DataProvider provider, boolean remove) {
    // no special sequences known or no more data
    if (_array == null) {
      return null;
    }

    // do the binary search
    boolean fromStream   = (specSeq == null);
    int[]   nearestMatch = { -1 };
    char    startChar;
    char[]  data         = null;
    int     startPos     = 0;
    int     maxLength    = 0;

    if (fromStream) {
      data      = provider.getData();
      startPos  = provider.getStartPosition();
      maxLength = provider.getLength();
      if (maxLength > 0) {
        startChar = data[startPos];
      } else {
        return null;
      }
    } else {
      startChar = specSeq.charAt(0);
    }
    if (searchIndex(startChar, nearestMatch) != 0) {
      return null;
    }

    // does the sequence come from the input stream ?
    SpecialSequence   listElem   = (SpecialSequence)_array.get(nearestMatch[0]);
    TokenizerProperty existing   = listElem._property;
    String            exSeq      = existing.getImages()[0];

    if (fromStream) {
      int len = exSeq.length();
      specSeq = new String(data, startPos, (maxLength < len) ? maxLength : len);
    }

    // try to find the longest match in the list of equaly starting sequences
    SpecialSequence prevElem = null;

    while (listElem != null) {
      existing = listElem._property;
      exSeq    = existing.getImages()[0];

      // given sequences should be found as they are. Potential sequences from
      // the input stream are shortened approbriately as the list elements 
      // become shorter.
      if (specSeq.length() > exSeq.length()) {
        if (fromStream) {
          specSeq = specSeq.substring(0, exSeq.length());
        } else {
          return null;
        }
      }

      // only need to compare on equal lengths
      if (specSeq.length() == exSeq.length()) {
        int res;

        if ((_flags & TokenizerProperties.F_NO_CASE) != 0) {
          res = exSeq.compareToIgnoreCase(specSeq);
        } else {
          res = exSeq.compareTo(specSeq); 
        }
        
        // found ...
        if (res == 0) {
          TokenizerProperty prop = listElem._property;
          
          if (remove) {
            if (prevElem == null) {
              if (listElem._next == null) {
                _array.remove(nearestMatch[0]);   // only element in list
              } else {
                listElem = listElem._next;        // leading element in list
                _array.set(nearestMatch[0], listElem);
              }
            } else {
              prevElem._next = listElem._next;    // non-leading element in list
            }
          }
          return prop;
          
        // ... (current part of) given sequence is "greater" than the current list element
        } else if (res < 0) {   
          if (fromStream) {
            specSeq = specSeq.substring(0, specSeq.length() - 1);
          } else {
            return null;
          }
        }
      }
      prevElem = listElem;
      listElem = listElem._next;
    }

    // still not found
    return null;
  }


  /**
   * Inner method that controls the special sequence store. This store is 
   * implemented as a binary tree for fast search operations.
   *
   * @param prop  the special sequence to add
   * @return  the old property if an existing is replaced, <code>null</code> otherwise
   */
  public TokenizerProperty addSpecialSequence(TokenizerProperty prop) {
    // first call
    if (_array == null) {
      _array = new ArrayList();
    }

    // binary search for the right position of the token
    String  seq           = prop.getImages()[0];
    int[]   nearestMatch  = { -1 };
    int     res           = searchIndex(seq.charAt(0), nearestMatch);

    // where to insert: < 0 means new sequence is greater than neighbouring 
    // existing one
    int idx = nearestMatch[0];

    if (res < 0) {
      idx++;
    }

    // new entry or replace existing one
    TokenizerProperty oldProp = null;
    
    if (res != 0) {
      if (_array.size() > idx) {
        _array.add(idx, new SpecialSequence(prop));
      } else {        
        _array.add(new SpecialSequence(prop));
      }
    } else {
      SpecialSequence   listElem = (SpecialSequence)_array.get(idx);
      TokenizerProperty existing;
      String            exSeq;

      while (listElem != null) {
        existing = listElem._property;
        exSeq    = existing.getImages()[0];

        if (seq.length() > exSeq.length()) {
          listElem._next     = new SpecialSequence(existing, listElem._next);
          listElem._property = prop;
          break;
        } else if (seq.length() == exSeq.length()) {
          if ((_flags & TokenizerProperties.F_NO_CASE) != 0) {
            res = exSeq.compareToIgnoreCase(seq);
          } else {
            res = exSeq.compareTo(seq); 
          }
          if (res == 0) {
            oldProp            = listElem._property;
            listElem._property = prop;
            break;
          } else if (res < 0) {
            listElem._next     = new SpecialSequence(existing, listElem._next);
            listElem._property = prop;
            break;
          } else if (listElem._next == null) {
            listElem._next     = new SpecialSequence(prop);
            break;
          }
        } else if (listElem._next == null) {
          listElem._next = new SpecialSequence(prop);
          break;
        }
        listElem = listElem._next;
      }
    }
    return oldProp;
  }

  // Members
  private StandardTokenizerProperties _parent = null;
  private ArrayList                   _array  = null;
  int                                 _flags  = 0;
}


/**
 * List element for equaly starting special sequences.
 */
final class SpecialSequence {
  
  /**
   * Constructor taking the parent {@link TokenizerProperty} as its single
   * argument.
   *
   * @param prop  the calling {@link TokenizerProperty} instance
   */
  SpecialSequence(TokenizerProperty prop) {
    this(prop, null);
  }

  /**
   * Constructor taking the parent {@link TokenizerProperty} and the next list
   * element. For the next element, <code>null</code> is a valid value.
   *
   * @param prop  the calling {@link TokenizerProperty} instance
   * @param next  the following {@link SpecialSequence} element 
   */
  SpecialSequence(TokenizerProperty prop, SpecialSequence next) {
    _property = prop;
    _next     = next;
  }

  // members
  public SpecialSequence    _next;
  public TokenizerProperty  _property;
}
  
  
/**
 * Instances of this inner class are returned when a call to 
 * {@link TokenizerProperties#getProperties}.
 * Each element of the enumeration contains a {@link TokenizerProperty} element.
 */
final class FullIterator implements Iterator {
  
  /**
   * constructor taking the calling {@link TokenizerProperties} object to retrieve
   * the members holding {@link TokenizerProperty} elements which are iterated by 
   * this <code>FullIterator</code> instance.
   *
   * @param caseSensitiveMap  map with properties where case matters
   * @param caseSensitiveMap  map with properties where case doesn't matter
   */
  public FullIterator(StandardTokenizerProperties parent) {
    _parent = parent;
    
    // create list of iterators
    _iterators    = new Object[3];
    synchronized(_parent._keywords) {
      _iterators[0] = new MapIterator(_parent, _parent._keywords[0], _parent._keywords[1]);
    }
    _iterators[1] = new PatternIterator(_parent);
    _iterators[2] = new SpecialSequencesIterator(parent, 0);
    _currIndex    = 0;
  }

  /**
   * Test wether there is another element in the iterated set or not. See
   * {@link java.util.Iterator} for details.
   *
   * @return <code>true</code>if another call to {@link #next} will return an object,
   *        <code>false</code> otherwise
   */
  public boolean hasNext() {
    synchronized(this) {
      while (_currIndex < _iterators.length) {
        Iterator iter = (Iterator)_iterators[_currIndex];

        if (iter.hasNext()) {
          return true;
        }
        _currIndex++;
      }
      return false;
    }
  }
  
  /**
   * Retrieve the next element in the iterated set. See {@link java.util.Iterator} 
   * for details.
   *
   * @return the next element or <code>null</code> if there is none
   */
  public Object next() {
    if (hasNext()) {
      synchronized(this) {
        Iterator iter = (Iterator)_iterators[_currIndex];
        return iter.next();
      }
    } else {
      return null;
    }
  }
  
  /**
   * Retrieve the next element in the iterated set. See {@link java.util.Iterator} 
   * for details.
   *
   * @return the next element or <code>null</code> if there is none
   */
  public void remove() {
    if (_currIndex < _iterators.length) {
      Iterator iter = (Iterator)_iterators[_currIndex];
      iter.remove();
    }
  }
  
  
  // members
  private StandardTokenizerProperties _parent     = null;
  private Object[]                    _iterators  = null;
  private int                         _currIndex  = -1;
}

/**
 * Instances of this inner class are returned when a call to {@link TokenizerProperties#getKeywords}
 * or {@link TokenizerProperties#getPatterns}.
 * Each element of the enumeration contains a {@link TokenizerProperty} element,
 * that in turn has the keyword or a pattern with its companion
 */
final class MapIterator implements Iterator {

  /**
   * constructor taking the a case-sensitive and a case-insensitive {@link java.util.Map}
   * which are iterated by this <code>MapIterator</code> instance.
   *
   * @param caseSensitiveMap  map with properties where case matters
   * @param caseSensitiveMap  map with properties where case doesn't matter
   */
  public MapIterator(StandardTokenizerProperties parent, Map caseSensitiveMap, Map caseInsensitiveMap) {
    synchronized(this) {
      _parent = parent;
      if (caseSensitiveMap != null) {
        _iterators[0] = caseSensitiveMap.values().iterator();
      }
      if (caseInsensitiveMap != null) {
        _iterators[1] = caseInsensitiveMap.values().iterator();
      }
    }
  }

  /**
   * the well known method from the {@link java.util.Iterator} interface.
   *
   * @return <code>true</code> if there are more {@link TokenizerProperty}
   *         elements, <code>false</code> otherwise
   */
  public boolean hasNext() {
    // check the current array
    synchronized(_iterators) {
      if (_iterators[0] != null) {
        if (_iterators[0].hasNext()) {
          return true;
        } else {
          _iterators[0] = null;
        }
      }
      if (_iterators[1] != null) {
        if (_iterators[1].hasNext()) {
          return true;
        } else {
          _iterators[1] = null;
        }
      }
      return false;
    }
  }

  /**
   * Retrieve the next {@link TokenizerProperty} in this enumeration. 
   *
   * @return  the next keyword as a <code>TokenizerProperty</code>
   */
  public Object next() {
    if (hasNext()) {
      synchronized(this) {
        if (_iterators[0] != null) {
          _currData = (TokenizerProperty)_iterators[0].next();
        } else {
          _currData = (TokenizerProperty)_iterators[1].next();
        }
        return _currData;
      }
    }
    return null;
  }
  
  /**
   * This method is similar to {@link Tokenizer#removeKeyword}
   */
  public void remove() {
    synchronized(this) {
      if (_iterators[0] != null) {
        _iterators[0].remove();
      } else {
        _iterators[1].remove();
      }
      _parent.notifyListeners(new TokenizerPropertyEvent(TokenizerPropertyEvent.PROPERTY_REMOVED, _currData));
    }
  }

  // members
  private StandardTokenizerProperties _parent     = null;
  private Iterator[]                  _iterators  = new Iterator[2];
  private TokenizerProperty           _currData   = null;
}



/**
 * Iterator for comments.
 * Instances of this inner class are returned when a call to one of the methods
 *<ul><li>
 *    {@link #getBlockComments}
 *</li><li>
 *    {@link #getLineComments}
 *</li><li>
 *    {@link #getStrings}
 *</li><li>
 *    {@link #getSpecialSequences}
 *</li></ul>
 * is done. Each element of the enumeration contains a {@link TokenizerProperty}
 * element, that in turn has the comment, special sequence etc. together with
 * its companion
 */
final class SpecialSequencesIterator implements Iterator {

  /**
   * constructor taking the calling <code>Tokenizer</code> and the type of the
   * {@link TokenizerProperty}. If the type is 0 then special sequences, line and 
   * block comments are returned in one iterator
   *
   * @param parent  the calling tokenizer
   * @param type    type of the <code>TokenizerProperty</code> 
   */
  public SpecialSequencesIterator(StandardTokenizerProperties parent, int type) {
    _type      = type;
    _parent    = parent;
    synchronized(parent._sequences) {
      _arrays[0] = parent._sequences[0];
      _arrays[1] = parent._sequences[1];
    }
  }

  /**
   * Checking for the next element in a special sequence list, that has the
   * required type. This method is the one that ultimately decides if there are
   * more elements or not.
   *
   * @return <code>true</code> if there is a matching {@link TokenizerProperty}
   *         element, <code>false</code> otherwise
   */
  private boolean listHasNext() {
    while (_nextElem != null) {
      if (_type == 0 || _nextElem._property.getType() == _type) {
        return true;
      }
      _nextElem = _nextElem._next;
    }
    return false;
  }

  /**
   * the well known method from the {@link java.util.Iterator} interface.
   *
   * @return <code>true</code> if there are more {@link TokenizerProperty}
   *         elements, <code>false</code> otherwise
   */
  public boolean hasNext() {
    // simple: check the current list for a successor
    if (listHasNext()) {
      return true;
    }

    // which is the current array ?
    SortedArray array = null;

    if (_arrays[0] != null) {
      array = _arrays[0];
    } else {
      array = _arrays[1];
    }

    // check the current array 
    if (array != null) {
      int size = array.size();        

      while (++_currentIndex < size) {
        _nextElem = array.get(_currentIndex);
        if (listHasNext()) {
          return true;
        }
      }

      // possible to switch to the no-case array ?
      if (array == _arrays[0]) {
        _arrays[0]    = null;
        _nextElem     = null;
        _currentIndex = -1;
        return hasNext();
      }
    }

    // no (more) sequences
    return false;
  }

  /**
   * Retrieve the next {@link TokenizerProperty} in this enumeration.
   *
   * @return a {@link TokenizerProperty} of the desired type or <code>null</code>
   */
  public Object next() {
    if (! hasNext()) {
      return null;
    } else {
      _currentArray = (_arrays[0] != null) ? _arrays[0] : _arrays[1];
      _currentElem  = _nextElem; 
      _nextElem     = _nextElem._next;
      hasNext();    // loop to find the next element
      return _currentElem._property;
    }
  }
  
  /**
   * Remove the current special sequence entry from the collection. This is an
   * alternative to {@link Tokenizer#removeSpecialSequence}.
   *
   * @throws  IllegalStateExcpetion if {@link #next} has not been called before or
   *          <code>remove</code> has been called already after the last <code>next</code>.
   */
  public void remove() throws IllegalStateException {
    // if current element is not set
    if (_currentElem == null) {
      throw new IllegalStateException();
    }
    
    // remove current element
    try {
      TokenizerProperty prop  = _currentElem._property;
      int               size  = _currentArray.size();
      
      _currentElem = null;
      _currentArray.searchBinary(prop.getImages()[0], null, true);
      if (size > 1 && _currentArray.size() < size && _currentIndex >= 0) {
        _currentIndex--;
      }
      _parent.notifyListeners(new TokenizerPropertyEvent(TokenizerPropertyEvent.PROPERTY_REMOVED, prop));
    } catch (Exception ex) {
      throw new ExtRuntimeException(ex, "While trying to remove current element of a SpecialSequencesIterator.");
    }
  }


  // members
  private StandardTokenizerProperties _parent       = null;
  private SortedArray[]               _arrays       = new SortedArray[2];
  private SortedArray                 _currentArray = null;
  private SpecialSequence             _currentElem  = null;
  private SpecialSequence             _nextElem     = null;
  private int                         _currentIndex = -1;
  private int                         _type         = Token.UNKNOWN;
}


/**
 * This class bundles a compiled pattern and the {@link TokenizerProperty} object
 * associated with the pattern.
 */
final class PatternData implements PatternHandler {
  
  /**
   * The constructor takes a pattern and the {@link TokenizerProperty} object
   * associated with this instance of <code>PatternData</code>.
   *
   * @param prop  the {@link TokenizerProperty} associated with this object
   * @throws UnsupportedOperationException if no backing regular expression classes are available
   * @throws InstantiationException if invoking a regular expression method fails
   * @throws IllegalAccessException if invoking a regular expression method fails
   * @throws InvocationTargetException if a regular expression method throws an exception
   */ 
  public PatternData(TokenizerProperty prop) 
    throws UnsupportedOperationException, InstantiationException, IllegalAccessException, InvocationTargetException
  {
    // is a regular expression parser present ?
    synchronized(_syncObject) {
      if (_patternClass == null) {
        try {
          try {
            Class charSeq   = Class.forName("java.lang.CharSequence");

            _patternClass   = Class.forName("java.util.regex.Pattern");
            _matcherClass   = Class.forName("java.util.regex.Matcher");
            _compilerMethod = _patternClass.getMethod("compile", new Class[] { String.class, int.class } );
            _matcherFactory = _patternClass.getMethod("matcher", new Class[] { charSeq } );
            _matcherMethod  = _matcherClass.getMethod("lookingAt", null );
            _endMethod      = _matcherClass.getMethod("end", null);
            _noCaseFlag     = _patternClass.getField("CASE_INSENSITIVE").getInt(null); 
          } catch (ClassNotFoundException ex1) {
            _patternClass = null;
            throw new ExtUnsupportedOperationException(
                        "java.util.regexp is not available. Pattern matching cannot be performed.", 
                        null);
          }
        } catch (NoSuchMethodException mthEx) {
          throw new ExtUnsupportedOperationException(mthEx,
                      "Expected regular expression compiler method not available in class {0}.", 
                      new Object[] { _patternClass } );
        } catch (NoSuchFieldException fieldEx) {
          throw new ExtUnsupportedOperationException(fieldEx,
                      "Expected case-insensitive flag not available in class {0}.", 
                      new Object[] { _patternClass } );
        } catch (IllegalAccessException accessEx) {
          throw new ExtUnsupportedOperationException(accessEx,
                      "Illegal access to case-insensitive flag in class {0}.", 
                      new Object[] { _patternClass } );
        }
      }
    }
    
    // set members
    setProperty(prop);
  }
  
  /**
   * Setting the {@link TokenizerProperty} for this <code>PatternData</code>.
   * This method will recompile the regular expression pattern. 
   *
   * @param prop  the {@link TokenizerProperty} associated with this object
   * @throws InstantiationException if invoking a regular expression method fails
   * @throws IllegalAccessException if invoking a regular expression method fails
   * @throws InvocationTargetException if a regular expression method throws an exception
   */
  public void setProperty(TokenizerProperty prop)   
    throws InstantiationException, IllegalAccessException, InvocationTargetException
  {
    // compile the pattern
    String    regexp  = prop.getImages()[0];
    int       flags   = ((prop.getFlags() & TokenizerProperties.F_NO_CASE) != 0) ? _noCaseFlag : 0;
    Object[]  args    = new Object[] { regexp, new Integer(flags) };
    
    _pattern  = _compilerMethod.invoke(null, args);
    
    // set property
    _property = prop;
  }
  
  /**
   * Retrieving the {@link TokenizerProperty} of this <code>PatternData</code>.
   *
   * @return  the {@link TokenizerProperty} associated with this object
   */
  public TokenizerProperty getProperty() {
    return _property;
  }
  
  /**
   * This method checks if the start of a character range given through the 
   * {@link DataProvider} matches a pattern. Currently, there is no way to detect
   * an incomplete match. Therefore, the {@link DataProvider} should contain a
   * large amount of data to detect matches.
   *
   * @param   dataProvider  the source to get the data from
   * @param   property      a filled {@link de.susebox.jtopas.TokenizerProperty} 
   *                        if a matching pattern could be found, otherwise the
   *                        state remains unchanged
   * @return  one of the constants defined in the {@link de.susebox.jtopas.spi.PatternHandler} interface
   * @throws  TokenizerException    generic exception
   * @throws  NullPointerException  if no {@link DataProvider} is given
   */
  public int matches(DataProvider dataProvider, TokenizerProperty property)
    throws TokenizerException, NullPointerException
  {
    // pattern matching is not active (see also addPattern)
    if (_patternClass == null) {
      return NO_MATCH;
    }
    
    // get the String
    String input = new String(dataProvider.getData(), dataProvider.getStartPosition(), dataProvider.getLength());
    
    // invoke JDK 1.4 or jakarta regexp API
    try {
      Object  matcher = _matcherFactory.invoke(_pattern, new Object[] { input } );
      boolean result  = ((Boolean)_matcherMethod.invoke(matcher, null)).booleanValue();
      int     end     = result ? ((Integer)_endMethod.invoke(matcher, null)).intValue() : 0;
      
      if (result || end > 0) {
        property.setCompanion(_property.getCompanion());
        property.setFlags(_property.getFlags());
        property.setImages(new String[] { input.substring(0, end) });
        if (result) {
          return (end < input.length()) ? PatternHandler.COMPLETE_MATCH : PatternHandler.MATCH;
        } else {
          return PatternHandler.INCOMPLETE_MATCH;
        }
      } else {
        return NO_MATCH;
      }
    } catch (Exception ex) {
      throw new TokenizerException(ex);
    }
  }

  // members
  private static Object     _syncObject     = new Object();
  private static Class      _patternClass   = null;
  private static Class      _matcherClass   = null;
  private static Method     _compilerMethod = null;
  private static Method     _matcherFactory = null;
  private static Method     _matcherMethod  = null;
  private static Method     _endMethod      = null;
  private static int        _noCaseFlag     = 0;
  private TokenizerProperty _property       = null;
  private Object            _pattern        = null;
}


/**
 * An {@link java.util.Iterator} for pattern.
 */
final class PatternIterator implements Iterator {
  /**
   * constructor taking the calling {@link TokenizerProperties} object.
   *
   * @param parent  the caller
   */
  public PatternIterator(StandardTokenizerProperties parent) {
    _parent   = parent;
    synchronized(parent._patterns) {
      _iterator = parent._patterns.iterator();
    }
  }

  /**
   * the well known method from the {@link java.util.Iterator} interface.
   *
   * @return <code>true</code> if there are more {@link TokenizerProperty}
   *         elements, <code>false</code> otherwise
   */
  public boolean hasNext() {
    return _iterator.hasNext();
  }

  /**
   * Retrieve the next {@link TokenizerProperty} in this enumeration. 
   *
   * @return  the next keyword as a <code>TokenizerProperty</code>
   */
  public Object next() {
    synchronized(this) {
      _currData = (PatternData)_iterator.next();

      if (_currData != null) {
        return _currData.getProperty();
      } else {
        return null;
      }
    }
  }
  
  /**
   * This method is similar to {@link Tokenizer#removeKeyword}
   */
  public void remove() {
    synchronized(_iterator) {
      _iterator.remove();
    }
    synchronized(_parent) {
      _parent.notifyListeners(new TokenizerPropertyEvent(TokenizerPropertyEvent.PROPERTY_REMOVED, _currData.getProperty()));
    }
  }

  // members
  private StandardTokenizerProperties _parent = null;
  private Iterator                    _iterator = null;
  private PatternData                 _currData = null;
}
