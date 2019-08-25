/*
 * StandardTokenizer.java: core class for lexical parser.
 *
 * Copyright (C) 2001 Heiko Blau
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

import de.susebox.java.lang.ExtIndexOutOfBoundsException;

import de.susebox.jtopas.spi.WhitespaceHandler;
import de.susebox.jtopas.spi.KeywordHandler;
import de.susebox.jtopas.spi.PatternHandler;
import de.susebox.jtopas.spi.SeparatorHandler;
import de.susebox.jtopas.spi.SequenceHandler;

import de.susebox.jtopas.spi.StandardWhitespaceHandler;
import de.susebox.jtopas.spi.StandardKeywordHandler;
import de.susebox.jtopas.spi.StandardSeparatorHandler;
import de.susebox.jtopas.spi.StandardSequenceHandler;

import de.susebox.jtopas.spi.DataProvider;
import de.susebox.jtopas.spi.DataMapper;


//-----------------------------------------------------------------------------
// Class StandardTokenizer
//

/**<p>
 * This is the mainstream {@link Tokenizer}. It implements the {@link Tokenizer}
 * interface in a straightforward approach without too specialized parse
 * optimizations.
 * </p><p>
 * Beside the {@link Tokenizer} interface, the class <code>StandardTokenizer</code>
 * provides some basic features for cascading (nested) tokenizers. Consider the usual
 * HTML pages found today in the WWW. Most of them are a mixture of regular HTML,
 * cascading style sheets (CSS) and embedded JavaScript. These different languages
 * use different syntaxes, so one needs varous tokenizers on the same input stream.
 *</p><p>
 * This {@link Tokenizer} implementation is not synchronized. Take care when using
 * with multible threads.
 *</p>
 *
 * @see Tokenizer
 * @see TokenizerProperties
 * @author Heiko Blau
 */
public class StandardTokenizer implements Tokenizer, TokenizerPropertyListener {

  //---------------------------------------------------------------------------
  // Constructors
  //
  
  /**
   * Default constructor that sets the tokenizer control flags as it would be
   * approbriate for C/C++ and Java. Found token images are copied. No line nor
   * column informations are provided. Nested comments are not allowed.
   *<br>
   * The tokenizer will use the {@link Tokenizer#DEFAULT_WHITESPACES} and 
   * {@link Tokenizer#DEFAULT_SEPARATORS} for whitespace and separator handling.
   */  
  public StandardTokenizer() {
    if (_defaultProperties == null) {
      _defaultProperties = new StandardTokenizerProperties();
    }
    setTokenizerProperties(_defaultProperties);
  }
  
  /**
   * Contructing a <code>StandardTokenizer</code> with a backing {@link TokenizerProperties}
   * instance.
   *
   * @param properties  an {@link TokenizerProperties} object containing the 
   *                    settings for the tokenizing process
   */
  public StandardTokenizer(TokenizerProperties properties) {
    setTokenizerProperties(properties);
  }

  
  //---------------------------------------------------------------------------
  // data source
  //
  
  /**
   * Setting the source of data. This method is usually called during setup of
   * the <code>Tokenizer</code> but may also be invoked while the tokenizing
   * is in progress. It will reset the tokenizers input buffer, line and column 
   * counters etc.
   *
   * @param source  a {@link TokenizerSource} to read data from
   * @see #getSource
   */
  public void setSource(TokenizerSource source) {
    _source           = source;
    _currentReadPos   = 0;
    _currentWritePos  = 0;
    _rangeStart       = 0;
    _currentToken     = null;
    if ((_flags & TokenizerProperties.F_COUNT_LINES) != 0) {
      _lineNumber     = 0;
      _columnNumber   = 0;
    } else {
      _lineNumber     = -1;
      _columnNumber   = -1;
    }
    _lookAheadToken.setType(Token.UNKNOWN);
  }
  
  /**
   * Convenience method to avoid the construction of a {@link TokenizerSource}
   * from the most important data source {@link java.io.Reader}.
   *
   * @param reader  the {@link java.io.Reader} to get data from
   */
  public void setSource(Reader reader) {
    setSource(new ReaderSource(reader));
  }
  
  /**
   * Retrieving the {@link TokenizerSource} of this <code>Tokenizer</code>. The
   * method may return <code>null</code> if there is no <code>TokenizerSource</code>
   * associated with it.
   *
   * @param the {@link TokenizerSource} associated with this <code>Tokenizer</code>
   * @see #setSource
   */
  public TokenizerSource getSource() {
    return _source;
  }
  
  
  //---------------------------------------------------------------------------
  // Methods of the Tokenizer interface
  //
  
  /**
   * Setting the tokenizer characteristics. This operation is usually done before
   * the parse process. If the tokenizer characteristics change during the parse
   * process they take effect with the next call of {@link #nextToken} or {@link #next}.
   *<br>
   * If <code>null</code> is passed to the method it throws 
   * {@link java.lang.NullPointerException}.
   *
   * @param   props   the {@link TokenizerProperties} for this tokenizer
   * @throws  NullPointerException if the <code>null</code> is passed to the call
   * @see     #getProperties
   */
  public void setTokenizerProperties(TokenizerProperties props) throws NullPointerException {
    if (props == null) {
      throw new NullPointerException();
    }

    // who is going to handle the various token types ?
    if (props instanceof WhitespaceHandler) {
      setWhitespaceHandler((WhitespaceHandler)props);
    } else {
      setWhitespaceHandler(new StandardWhitespaceHandler(props));
    }
    if (props instanceof SeparatorHandler) {
      setSeparatorHandler((SeparatorHandler)props);
    } else {
      setSeparatorHandler(new StandardSeparatorHandler(props));
    }
    if (props instanceof SequenceHandler) {
      setSequenceHandler((SequenceHandler)props);
    } else {
      setSequenceHandler(new StandardSequenceHandler(props));
    }
    if (props instanceof KeywordHandler) {
      setKeywordHandler((KeywordHandler)props);
    } else {
      setKeywordHandler(new StandardKeywordHandler(props));
    }
      
    // set properties
    if (_properties != null) {
      _properties.removeTokenizerPropertyListener(this);
    }
    _properties = props;
    _properties.addTokenizerPropertyListener(this);

    // flag handling
    int newFlags = _properties.getParseFlags();

    if (newFlags != _flags) {
      propertyChanged(new TokenizerPropertyEvent(
                            TokenizerPropertyEvent.PROPERTY_MODIFIED,
                            new TokenizerProperty(TokenizerProperty.PARSE_FLAG_MASK, 
                                                  new String[] { Integer.toBinaryString(newFlags) } ),
                            new TokenizerProperty(TokenizerProperty.PARSE_FLAG_MASK, 
                                                  new String[] { Integer.toBinaryString(_flags) } )));
    }
  }
  

  /**
   * Retrieving the current tokenizer characteristics. 
   *
   * @return  the {@link TokenizerProperties} of this <code>Tokenizer</code>
   * @see     #setProperties
   */
  public TokenizerProperties getTokenizerProperties() {
    return _properties;
  }
  

  /**
   * Setting the control flags of the <code>Tokenizer</code>. Use a combination
   * of the <code>Tokenizer.F_...</code> flags for the parameter. The <code>mask</code> 
   * parameter contains a bit mask of the <code>F_...</code> flags to change.
   *<br>
   * This method throws a {@link TokenizerException} if a flag is passed that cannot
   * be handled by this <code>Tokenizer</code> itself. See the discussion of 
   * the tokenizer flags at {@link Tokenizer#changeParseFlags}.
   *
   * @param flags the parser control flags
   * @param mask  the mask for the flags to set or unset
   * @throws TokenizerException if one or more of the flags given cannot be honored
   * @see   #getParseFlags
   */
  public void changeParseFlags(int flags, int mask) throws TokenizerException {
    // test the given flags
    if ((mask | VALID_FLAGS_MASK) != VALID_FLAGS_MASK) {
      throw new TokenizerException(
                  "One or more flags cannot be set separately for a {0}. Violating flags in {1}: {2}.",
                  new Object[] { StandardTokenizer.class.getName(), 
                                 Integer.toHexString(flags),
                                 Integer.toHexString(mask & ~VALID_FLAGS_MASK) } );
    }
    
    // set the new flags for this tokenizer
    _flagMask = mask;
    _flags    = (flags & mask) | (getTokenizerProperties().getParseFlags() & ~mask);

    // when counting lines initialize the current line and column position
    if ((_flags & TokenizerProperties.F_COUNT_LINES) != 0) {
      _lineNumber   = 0;
      _columnNumber = 0;
    }
  }

   /**
    * Retrieving the parser control flags. A bitmask containing the <code>F_...</code>
    * constants is returned.
    *
    * @return the current parser control flags
    * @see #changeParseFlags
    */
  public int getParseFlags() {
    return (getTokenizerProperties().getParseFlags() & ~_flagMask) + (_flags & _flagMask);
  }
  
  /**
   * Setting a new {@link KeywordHandler} or removing any previously installed
   * one. If <code>null</code> is passed (installed handler removed), no keyword
   * support is available.
   *
   * @param handler the (new) {@link KeywordHandler} to use or <code>null</code>
   *                to remove it
   */
  public void setKeywordHandler(de.susebox.jtopas.spi.KeywordHandler handler) {
    _keywordHandler = handler; 
  }
  
   /**
    * Retrieving the current {@link KeywordHandler}. The method may return
    * <code>null</code> if there isn't any handler installed.
    *
    * @return the currently active whitespace keyword or <code>null</code>, if 
    *         keyword support is switched off
   */
  public de.susebox.jtopas.spi.KeywordHandler getKeywordHandler() {
    return _keywordHandler;
  }
  
  /**
   * Setting a new {@link WhitespaceHandler} or removing any previously installed
   * one. If <code>null</code> is passed, the tokenizer will not recognize 
   * whitespaces.
   *
   * @param handler the (new) whitespace handler to use or <code>null</code> to 
   *                switch off whitespace handling
   * @see   #getWhitespaceHandler
   */
  public void setWhitespaceHandler(de.susebox.jtopas.spi.WhitespaceHandler handler) {
    _whitespaceHandler = handler;
  }
  
  /**
   * Retrieving the current {@link WhitespaceHandler}. The method may return
   * <code>null</code> if there whitespaces are not recognized.
   *
   * @return  the currently active whitespace handler or null, if the base
   *          implementation is working
   */
  public de.susebox.jtopas.spi.WhitespaceHandler getWhitespaceHandler() {
    return _whitespaceHandler;
  }
  
  
  /**
   * Setting a new {@link SeparatorHandler} or removing any previously installed
   * <code>SeparatorHandler</code>. If <code>null</code> is passed, the tokenizer
   * doesn't recognize separators.
   *
   * @param handler the (new) separator handler to use or <code>null</code> to
   *                remove it
   * @see   #getSeparatorHandler
   */
  public void setSeparatorHandler(de.susebox.jtopas.spi.SeparatorHandler handler) {
    _separatorHandler = handler;
  }
  
  /**
   * Retrieving the current {@link SeparatorHandler}. The method may return
   * <code>null</code> if there isn't any handler installed.
   *
   * @return  the currently active {@link SeparatorHandler} or <code>null</code>, 
   *          if separators aren't recognized by the tokenizer
   * @see     #setSequenceHandler
   */
  public de.susebox.jtopas.spi.SeparatorHandler getSeparatorHandler() {
    return _separatorHandler;
  }
  
  
  /**
   * Setting a new {@link SequenceHandler} or removing any previously installed
   * one. If <code>null</code> is passed, the tokenizer will fall back to the 
   * base implementation.
   *
   * @param handler   the (new) {@link SequenceHandler} to use or null to remove it
   */
  public void setSequenceHandler(de.susebox.jtopas.spi.SequenceHandler handler) {
    _sequenceHandler = handler;
  }
  
  
  /**
   * Retrieving the current {@link SequenceHandler}. The method may return
   * <code>null</code> if there isn't any handler installed. That does not
   * mean, that special sequences, comments and strings are not dealt with, but 
   * that parsing is done by the base method of {@link de.susebox.java.util.AbstractTokenizer}.
   *
   * @return  the currently active {@link SequenceHandler} or null, if the base
   *          implementation is working
   */
  public de.susebox.jtopas.spi.SequenceHandler getSequenceHandler() {
    return _sequenceHandler;
  }
  

  /**
   * Setting a new {@link de.susebox.jtopas.spi.PatternHandler} or removing any 
   * previously installed one. If <code>null</code> is passed, pattern are not 
   * supported by the tokenizer (any longer).
   *
   * @param handler the (new) {@link de.susebox.jtopas.spi.PatternHandler} to 
   *                use or <code>null</code> to remove it
   * @see #getPatternHandler
   */
  public void setPatternHandler(de.susebox.jtopas.spi.PatternHandler handler) {
    _patternHandler = handler;
  }
  
  /**
   * Retrieving the current {@link de.susebox.jtopas.spi.PatternHandler}. The method 
   * may return <code>null</code> if there isn't any handler installed.
   *
   * @return  the currently active {@link de.susebox.jtopas.spi.PatternHandler} 
   *          or <code>null</code>, if patterns are not recognized by the tokenizer
   * @see #setPatternHandler
   */
  public de.susebox.jtopas.spi.PatternHandler getPatternHandler() {
    return _patternHandler;
  }
  

  /**
   * Query the current row. The method can only be used if the flag <code>F_COUNT_LINES</code>
   * has been set.
   * Without this flag being set, the return value is undefined.
   * Note that row counting starts with 0, while editors often use 1 for the first
   * row.
   *
   * @return current row (starting with 0) 
   *         or -1 if the flag {@link Tokenizer#F_COUNT_LINES} is set
   */
  public int getCurrentLine() {
    return _lineNumber;
  }
  
  
  /**
   * Retrieve the current column. The method can only be used if the flag <code>F_COUNT_LINES</code>
   * has been set.
   * Without this flag being set, the return value is undefined.
   * Note that column counting starts with 0, while editors often use 1 for the first
   * column in one row.
   *
   * @return current column number (starting with 0)
   */
  public int getCurrentColumn() {
    return _columnNumber;
  }
  

  /**
   * Checking if there are more tokens available. This method will return
   * <code>true</code> until and enf-of-file condition is encountered during a 
   * call to {@link #nextToken} or {@link #next}.<br>
   * That means, that the EOF is returned one time, afterwards <code>hasMoreToken</code>
   * will return <code>false</code>. Furthermore, that implies, that the method
   * will return <code>true</code> at least once, even if the input data stream
   * is empty.<br>
   * The method can be conveniently used in a while loop.
   *
   * @return  <code>true</code> if a call to {@link #nextToken} or {@link #next}
   *          will succed, <code>false</code> otherwise
   */
  public boolean hasMoreToken() {
    return _currentToken == null || _currentToken.getType() != Token.EOF;
  }
  
  
  /**
   * Retrieving the next {@link Token}. The method works in this order:<br>
   *<ol><li>
   *   Check for an end-of-file condition. If there is such a condition then
   *   return it.
   *</li><li>
   *   Try to collect a sequence of whitespaces. If such a sequence can be found
   *   return if the flag <code>F_RETURN_WHITESPACES</code> is set, or skip these
   *   whitespaces.
   *</li><li>
   *   Check the next characters against all known line and block comments. If
   *   a line or block comment starting sequence matches, return if the flag
   *   <code>F_RETURN_WHITESPACES</code> is set, or skip the comment.
   *   If comments are returned they include their starting and ending sequences
   *   (newline in the case of a line comment)
   *</li><li>
   *   Check the next characters against all known string starting sequences. If
   *   a string begin could be identified return the string until and including
   *   the closing sequence
   *</li><li>
   *   Check the next characters against all known pattern. A pattern is usually 
   *   a regular expression that is used by java.util.regex.Pattern. But implementations 
   *   of PatternHandler may use other pattern syntaxes.
   *</li><li>
   *   Check the next characters against all known special sequences. Especially,
   *   find the longest possible match. If a special sequence could be identified
   *   then return it.
   *</li><li>
   *   Check for ordinary separators. If one could be found return it.
   *</li><li>
   *   Check the next characters against all known keywords. If a keyword could
   *   be identified then return it.
   *</li><li>
   *   Return the text portion until the next whitespace, comment, special
   *   sequence or separator.
   *</li></ol>
   *
   * @return found {@link Token} including the EOF token
   * @throws TokenizerException generic exception (list) for all problems that may occur while parsing
   * (IOExceptions for instance)
   */
  public Token nextToken() throws TokenizerException {
    Token token = new Token();
    
    // Get the next token
__MAIN_LOOP__:
    do {
      // we know the starting positions
      token.setStartPosition(getReadPosition());
      token.setStartLine(_lineNumber);
      token.setStartColumn(_columnNumber);

      // no lookahead available
      switch (_lookAheadToken.getType()) {
      case Token.UNKNOWN:
        token.setImage(null);

        // take care to use the right order to test for different token types
        if ( ! test4Whitespace(token)) {
          if ( ! test4SpecialSequence(token)) {
            if ( ! test4Separator(token)) {
              if ( ! test4Normal(token)) {
                token.setType(Token.EOF);
              }
            }
          }
        }
        break;

      case Token.LINE_COMMENT:
      case Token.BLOCK_COMMENT:
      case Token.STRING:
      case Token.SPECIAL_SEQUENCE:
        completeSpecialSequence(token);
        _lookAheadToken.setType(Token.UNKNOWN);
        break;
      case Token.SEPARATOR:
        completeSeparator(token);
        _lookAheadToken.setType(Token.UNKNOWN);
        break;
      default:
        completeWhitespace(token);
        _lookAheadToken.setType(Token.UNKNOWN);
      }

      // more actions depending on the token
      switch (token.getType()) {
      case Token.WHITESPACE:
      case Token.LINE_COMMENT:
      case Token.BLOCK_COMMENT:
        if ( ! isFlagSet(TokenizerProperties.F_RETURN_WHITESPACES)) {
          token.setType(Token.UNKNOWN);   // see loop control (while)
          break;
        }
        /* no break; */
      default:
        if ( ! isFlagSet(TokenizerProperties.F_TOKEN_POS_ONLY)) {    
          token.setImage(new String(_inputBuffer, _currentReadPos, token.getLength()));
        }
      }

      // compute new line and column positions (if flag is set) and complete
      // the token
      adjustLineAndColumn(token.getType(), token.getLength());
      token.setEndLine(_lineNumber);
      token.setEndColumn(_columnNumber);

      // this is the one and only point where the current read position is
      // adjusted (except for the data shifting in readMoreData).
      _currentReadPos += token.getLength();

    } while (token.getType() == Token.UNKNOWN);

    // store the retrieved token
    _currentToken = token;
    return token;
  }
  
 
  /**
   * This method is a convenience method. It returns only the next token image
   * without any informations about its type or associated information.
   *
   * @return the token image of the next token
   * @throws TokenizerException generic exception (list) for all problems that may occur while parsing
   * (IOExceptions for instance)
   * @see #currentImage
   */
  public String nextImage() throws TokenizerException {
    nextToken();
    return currentImage();
  }
 
  
  /**
   * Retrieve the {@link Token} that was found by the last call to {@link #nextToken}.
   * @return the token that was found by the last call to <code>nextToken</code>
   * or <code>next</code>
   */
  public Token currentToken() {
    return _currentToken;
  }
  
 
  /**
   * Convenience method to retrieve only the token image of the {@link Token} that
   * would be returned by {@link #currentToken}.
   *
   * @return the token image of the current token
   * @see #currentToken
   */
  public String currentImage() {
    Token token = currentToken();
    
    if (token == null || token.getType() == Token.EOF) {
      return null;
    } else if ( ! isFlagSet(TokenizerProperties.F_TOKEN_POS_ONLY) || token.getImage() != null) {
      return token.getImage();
    } else {
      return getText(token.getStartPosition(), token.getLength());
    }
  }

  
  /**
   * If the flag {@link Tokenizer#F_COUNT_LINES} is set, this method will return the
   * line number starting with 0 in the input stream. Lines are terminated by one of
   * the following end-of-line sequences:
   * <br><ul><li>
   * Carriage Return (ASCII 13, '\r'). This EOL is used on Apple Macintosh
   * </li><li>
   * Linefeed (ASCII 10, '\n'). This is the UNIX EOL character.
   * </li><li>
   * Carriage Return + Linefeed ("\r\n"). This is used on MS Windows systems.
   * </li></ul>
   * Note that we didn't choose to use the system property "line.separator" since
   * today windows files are transfered to UNIX and Macs and vice versa. However,
   * a combination of '\n' with a subsequent '\r' is considered to be two lines.
   *
   * @return the current line number starting with 0 or -1 if no line numbers are supplied.
   * @see #getColumnNumber
   */  
  public int getLineNumber() {
    return _lineNumber;
  }
  
  /**
   * If the flag {@link Tokenizer#F_COUNT_LINES} is set, this method will return the
   * current column positionstarting with 0 in the input stream.
   *
   * @return the current column position
   * @see #getLineNumber
   */  
  public int getColumnNumber() {
    return _columnNumber;
  }
  
  /**
   * This method returns the absolute offset in characters to the start of the
   * parsed stream. Together with {@link StandardTokenizer#currentlyAvailable} 
   * it describes the currently available text "window".
   *
   * @return the absolute offset of the current text window in characters from 
   *         the start of the data source of the Tokenizer
   */
  public int getRangeStart() {
    return _rangeStart;
  }
  
  /**
   * Getting the current read offset. This is the absolute position where the
   * next call to <code>nextToken</code> or <code>next</code> will start.
   *
   * @return the absolute offset in characters from the start of the data source 
   *         of the Tokenizer where reading will be continued
   */
  public int getReadPosition() {
    return _rangeStart + _currentReadPos;
  }
  
  /**
   * Retrieving the number of the currently available characters. This includes
   * both characters already parsed by the <code>Tokenizer</code> and characters
   * still to be analyzed.<br>
   *
   * @return number of currently available characters
   */
  public int currentlyAvailable() {
    return _currentWritePos;
  }
  
  
  /**
   * Try to read more data into the text buffer of the tokenizer. This can be
   * useful when a method needs to look ahead of the available data or a skip
   * operation should be performed.<br>
   * The method returns the same value than an immediately following call to 
   * {@link currentlyAvailable} would return.<br>
   *
   * @return  the number of character now available
   * @throws  TokenizerException generic exception (list) for all problems that 
   *          may occur while reading (IOExceptions for instance)
   */
  public int readMore() throws TokenizerException {
    readMoreData();
    return currentlyAvailable();
  }
  
  
  /**
   * Retrieve text from the currently available range. The start and length
   * parameters must be inside {@link #getRangeStart} and
   * <code>getRangeStart + {@link #currentlyAvailable}</code>.
   *
   * @param   start   position where the text begins
   * @param   len     length of the text
   * @return  the text beginning at the given position ith the given length
   * @throws  IndexOutOfBoundsException if the starting position or the length 
   *          is out of the current text window
   */
  public String getText(int start, int len) throws IndexOutOfBoundsException {
    if (start < _rangeStart) {
      throw new ExtIndexOutOfBoundsException(
                  "Start position {0} lower than the current text window start {1}.", 
                  new Object[] { new Integer(start), new Integer(_rangeStart) } 
                );
    } else if (start + len > _rangeStart + _currentWritePos) {
      throw new ExtIndexOutOfBoundsException(
                  "required text starting at position {0} with length {1} exceeds current text window starting at {2} with length {3}.", 
                  new Object[] { 
                    new Integer(start), new Integer(len), 
                    new Integer(_rangeStart),  new Integer(currentlyAvailable()) 
                  }
                );
    }
    return new String(_inputBuffer, start - _rangeStart, len);
  }
  

  /**
   * Get a single character from the current text range.
   *
   * @param pos position of the required character
   * @return the character at the specified position
   * @throws IndexOutOfBoundsException if the parameter <code>pos</code> is not 
   *         in the available text range (text window)
   */
  public char getChar(int pos) throws IndexOutOfBoundsException {
    if (pos < _rangeStart || pos >= _currentWritePos) {
      throw new ExtIndexOutOfBoundsException(
                  "Given position {0} is out of current text window starting at {2} with length {3}.", 
                  new Object[] { 
                    new Integer(pos), new Integer(_rangeStart), new Integer(currentlyAvailable())
                  } 
                );
    }
    return _inputBuffer[pos - _rangeStart];
  }

  
  /**
   * This is a method designed to be used by those who can guarantee to be in a 
   * valid position range. It is equivalent to the {@link #getChar}
   * method above except that it does not check for a invalid position.
   *<br>
   * When passing an invalid position value, two situations may occure: The
   * invalid position is really out of bounds of the input buffer (an unchecked 
   * exception will be thrown) or it points into an area where old or even 
   * initial values lay.
   *
   * @param pos position of the required character
   * @return the character at the specified position
   */
  public char getCharUnchecked(int pos) {
    return _inputBuffer[pos - _rangeStart];
  }

  
  /**
   * This method sets the tokenizers current read position to the given absolute
   * read position. It realizes one type of rewind / forward operations. The
   * given position must be inside the intervall {@link #getRangeStart} and
   * {@link #getRangeStart} + {@link #currentlyAvailable} - 1.
   * <br>
   * The operation does not affect the return values of {@link #current} and
   * {@link #currentToken}.
   *<br>
   * When using this method with embedded tokenizers, the user is responsible to
   * set the read position in the currently used tokenizer. It will be propagated
   * by the next call to {@link #switchTo}. Until that point, a call to this
   * method has no effect on the other tokenizers sharing the same data source.
   *
   * @param   position  absolute position for the next parse operation
   * @throws  IndexOutOfBoundsException if the parameter <code>position</code> is
   *          not in the available text range (text window)
   * @see     setReadPositionRelative
   */
  public void setReadPositionAbsolute(int position) throws IndexOutOfBoundsException {
    if (position < _rangeStart) {
      throw new ExtIndexOutOfBoundsException(
                  "Invalid read position {0} below the current text window start {1}.", 
                  new Object[] { new Integer(position), new Integer(_rangeStart) } 
                );
    } else if (position >= _rangeStart + _currentWritePos) {
      throw new ExtIndexOutOfBoundsException(
                  "Invalid read position {0} above the current text window end {1}.", 
                  new Object[] { new Integer(position), new Integer(currentlyAvailable() - _rangeStart - 1) }
                );
    }
    _currentReadPos = position;
    _lookAheadToken.setType(Token.UNKNOWN);
  }  

  /**
   * This method sets the tokenizers new read position the given number of characters
   * forward (positive value) or backward (negative value) starting from the current
   * read position. It realizes one type of rewind / forward operations. The
   * given offset must be greater or equal than {@link #getRangeStart} - {@link #getReadPosition}
   * and lower than {@link #currentlyAvailable} - {@link #getReadPosition}.
   * <br>
   * The operation does not affect the return values of {@link #current} and
   * {@link #currentToken}.
   *<br>
   * When using this method with embedded tokenizers, the user is responsible to
   * set the read position in the currently used tokenizer. It will be propagated
   * by the next call to {@link #switchTo}. Until that point, a call to this
   * method has no effect on the other tokenizers sharing the same data source.
   *
   * @param   offset  number of characters to move forward (positive offset) or
   *                 backward (negative offset)
   * @throws  IndexOutOfBoundsException if the parameter <code>offset</code> would
   *          move the read position out of the available text range (text window)
   * @see     setReadPositionAbsolute
   */
  public void setReadPositionRelative(int offset) throws IndexOutOfBoundsException {
    setReadPositionAbsolute(getReadPosition() + offset);
  }
  

  //---------------------------------------------------------------------------
  // embedded tokenizer support
  //
  
  /**
   * Adding an embedded tokenizer. Embedded tokenizer work on the same input 
   * buffer as their base tokenizer. A situation where embedded tokenizer could
   * be applied, is a HTML stream with cascading style sheet (CSS) and JavaScript
   * parts.
   *<br>
   * There are no internal means of switching from one tokenizer to another. 
   * This should be done by the caller using the method {@link #switchTo}.
   *<br>
   * The {@link TokenizerProperties#F_KEEP_DATA} and {@link TokenizerProperties#F_COUNT_LINES}
   * flags of the base tokenizer take effect also in the embedded tokenizers.
   *<br>
   * Since is might be possible that the given <code>tokenizer</code> is a
   * derivation of the <code>StandardTokenizer</code> class, this method is
   * synchronized on <code>tokenizer</code>.
   *
   * @param  tokenizer   an embedded tokenizer
   * @throws TokenizerException if something goes wrong (not likely :-)
   */
  public void addTokenizer(StandardTokenizer tokenizer) throws TokenizerException {
    StandardTokenizer curr = this;
    
    while (curr._nextTokenizer != null) {
      curr = curr._nextTokenizer;
    }
    
    if (tokenizer != null) {
      synchronized(tokenizer) {
        curr._nextTokenizer      = tokenizer;
        tokenizer._prevTokenizer = curr;

        // share the input buffer of the base tokenizer
        StandardTokenizer base = getBaseTokenizer(this);
        
        tokenizer._inputBuffer = base._inputBuffer;
        
        // inherited flags
        tokenizer.changeParseFlags(base.getParseFlags(), TokenizerProperties.F_COUNT_LINES);
      }
    }
  }

  
  /**
   * Changing fron one tokenizer to another. If the given tokenizer has not been
   * added with {@link #addTokenizer}, an exception is thrown.<br>
   * The <code>switchTo</code> method does the nessecary synchronisation between
   * <code>this</code> and the given tokenizer. The user is therefore responsible
   * to use <code>switchTo</code> whenever a tokenizer change is nessecary. It
   * must be done this way:
   *<blockquote><pre>
   *   Tokenizer base     = new MyTokenizer(...)
   *   Tokenizer embedded = new MyTokenizer(...)
   *
   *   // setting properties (comments, keywords etc.)
   *   ...
   *
   *   // embedding a tokenizer
   *   base.addTokenizer(embedded);
   *   
   *   // tokenizing with base
   *   ...
   *   if (<i>switch_condition</i>) {
   *     base.switchTo(embedded);
   *   }
   *
   *   // tokenizing with embedded
   *   ...
   *   if (<i>switch_condition</i>) {
   *     embedded.switchTo(base);
   *   }
   *</pre></blockquote>
   * That way we avoid a more complex synchronisation between tokenizers whenever
   * one of them parses the next data in the input stream. However, the danger
   * of not synchronized tokenizers remains, so take care.
   *<br>
   * Since is might be possible that the given <code>tokenizer</code> is a
   * derivation of the <code>StandardTokenizer</code> class, this method is
   * synchronized on <code>tokenizer</code>.
   *
   * @param tokenizer   the tokenizer that should be used from now on
   */
  public void switchTo(StandardTokenizer tokenizer) 
    throws TokenizerException
  {
    if (tokenizer != null) {
      synchronized(tokenizer) {
        if (tokenizer._inputBuffer != _inputBuffer) {
          throw new TokenizerException("Trying to switch to an alien tokenizer (not added with addTokenizer).", null);
        }
        tokenizer._currentReadPos  = this._currentReadPos;
        tokenizer._currentWritePos = this._currentWritePos;
        tokenizer._columnNumber    = this._columnNumber;
        tokenizer._lineNumber      = this._lineNumber;
        tokenizer._rangeStart      = this._rangeStart;
      }
    } else {
      throw new TokenizerException(new NullPointerException());
    }
  }


  //---------------------------------------------------------------------------
  // Methods that may be overwritten in derived classes
  //
  
  /**
   * This method checks if the character is a whitespace. Implement Your own
   * code for situations where this default implementation is not fast enough
   * or otherwise not really good.
   *
   * @param testChar  check this character
   * @return <code>true</code> if the given character is a whitespace,
   *         <code>false</code> otherwise
   */
  protected boolean isWhitespace(char testChar) {
    if (_whitespaceHandler != null) {
      return _whitespaceHandler.isWhitespace(testChar);
    } else {
      return false;
    }
  }
      
 
  /**
   * This method detects the number of whitespace characters starting at the given
   * position. It should use {@link #getChar} or {@link #getCharUnchecked} to 
   * retrieve a character to check.
   *<br>
   * The method should return the number of characters identified as whitespaces
   * starting from and including the given start position.
   *<br>
   * Do not attempt to actually read more data or do anything that leads to the
   * change of the data source or to tokenizer switching. This is done by the 
   * tokenizer framework.
   *
   * @param   startingAtPos  start checking for whitespace from this position
   * @param   maxChars  if there is no non-whitespace character, read up to this number of characters 
   * @return  number of whitespace characters starting from the given offset
   * @throws  TokenizerException failure while reading data from the input stream
   */
  protected int readWhitespaces(int startingAtPos, int maxChars) throws TokenizerException {
    if (_whitespaceHandler != null) {
      StandardDataProvider dataProvider = new StandardDataProvider(_inputBuffer, startingAtPos - _rangeStart, maxChars);
      return _whitespaceHandler.countLeadingWhitespaces(dataProvider);
    } else {
      return 0;
    }
  }
  
  
  /**
   * This method checks the given character if it is a separator.
   * Implement Your own code for situations where this default implementation 
   * is not fast enough or otherwise not really good.
   *
   * @param testChar  check this character
   * @return <code>true</code> if the given character is a separator,
   *         <code>false</code> otherwise
   */
  protected boolean isSeparator(char testChar) {
    if (_separatorHandler != null) {
      return _separatorHandler.isSeparator(testChar);
    } else {
      return false;
    }
  }

  
  /**
   * This method checks at the given position if it contains a a special sequence. 
   * Unlike the method {@link #test4SpecialSequence} it does nothing more.
   * It should use {@link #getChar} or {@link #getCharUnchecked} to retrieve 
   * characters at need.
   *
   * @param  startingAtPos  check at this position
   * @throws TokenizerException failure while reading data from the input stream
   *         or routed exception from the active {@link de.susebox.jtopas.spi.SequenceHandler}
   * @return <code>true</code> if a special sequence was found at the given offset,
   *         <code>false</code> otherwise
   */
  protected TokenizerProperty isSequenceCommentOrString(int startingAtPos) 
    throws TokenizerException 
  {
    if (_sequenceHandler != null) {
      // do we need more data to ensure enough characters for even the longest
      // possible sequence match 
      int start     = startingAtPos - _rangeStart;
      int maxLength = _currentWritePos - start;
      
      while (_sequenceHandler.getSequenceMaxLength() > maxLength) {
        readMore();
        start = startingAtPos - _rangeStart;
        if (_currentWritePos - start <= maxLength) {
          break;
        }
        maxLength = _currentWritePos - start;
      }
      
      // invoke the sequence handler
      StandardDataProvider dataProvider = new StandardDataProvider(_inputBuffer, start, maxLength);
      return _sequenceHandler.startsWithSequenceCommentOrString(dataProvider);
      
    } else {
      // no handler given
      return null;
    }
  }


  /**
   * This method checks if the character sequence starting at a given position
   * with a given lenghth is a keyword. If so, it returns the keyword description
   * as {@link TokenizerProperty} object.
   *
   * @param   startingAtPos   check at this position
   * @param   length          the candidate has this number of characters
   * @throws  TokenizerException routed exception from the active {@link de.susebox.jtopas.spi.KeywordHandler}
   * @return  {@link TokenizerProperty} describing the keyword or <code>null</code>
   */
  protected TokenizerProperty isKeyword(int startingAtPos, int length) throws TokenizerException {
    if (_keywordHandler != null) {
      StandardDataProvider dataProvider = new StandardDataProvider(_inputBuffer, startingAtPos - _rangeStart, length);
      return _keywordHandler.isKeyword(dataProvider);
    } else {
      return null;
    }
  }
  
  
  //---------------------------------------------------------------------------
  // TokenizerPropertyListener methods
  //
  
  /**
   * Event handler method. The given {@link TokenizerPropertyEvent} parameter
   * contains the nessecary information about the property change. We choose
   * one single method in favour of various more specialized methods since the
   * reactions on adding, removing and modifying tokenizer properties are often
   * the same (flushing cash, rereading information etc.) are probably not very
   * different.
   *<br>
   * Note that a modification of the parse flags in the backing {@link TokenizerProperties}
   * object removes all flags previously modified through {@link #changeParseFlags}.
   *
   * @param event the {@link TokenizerPropertyEvent} that describes the change
   */
  public void propertyChanged(TokenizerPropertyEvent event) {
    TokenizerProperty prop   = event.getProperty();
    String[]          images = prop.getImages();
    
    synchronized(this) {
      switch (event.getType()) {
      case TokenizerPropertyEvent.PROPERTY_ADDED:
      case TokenizerPropertyEvent.PROPERTY_MODIFIED:
        switch (prop.getType()) {
        case TokenizerProperty.PARSE_FLAG_MASK:
          _flags    = getTokenizerProperties().getParseFlags();
          _flagMask = 0;
          if ((_flags & TokenizerProperties.F_COUNT_LINES) != 0) {
            if (_lineNumber < 0) {
              _lineNumber = 0;
            }
            if (_columnNumber < 0) {
              _columnNumber = 0;
            }
          } else {
            _lineNumber   = -1;
            _columnNumber = -1;
          }
          break;
        }
        break;
        
      case TokenizerPropertyEvent.PROPERTY_REMOVED:
        break;
      }
    }
  }
  

  //---------------------------------------------------------------------------
  // Implementation
  //

  /**
   * Embedded tokenizers have their base tokenizer they share the input stream
   * with.
   *
   * @param t   the tokenizer thats base tokenizer should be found
   * @return the base tokenizer (the one owning the input stream and text buffer)
   */
  protected StandardTokenizer getBaseTokenizer(StandardTokenizer t) {
    while (t._prevTokenizer != null) {
      t = t._prevTokenizer;
    }
    return t;
  }
  
  /**
   * This method organizes the input buffer. It moves the current text window if
   * nessecary or allocates more space, if data should be kept completely (see the
   * {@link Tokenizer#F_KEEP_DATA} flag).
   * Its main purpose is to call the {@link #read} method of the implementing class.
   *
   * @return number of read bytes or -1 if an end-of-file condition occured
   * @throws TokenizerException wrapped exceptions from the {@link #read} method
   */
  protected int readMoreData() throws TokenizerException  {
    // its always the base tokenizer doing the reading
    int               bytes = 0;
    StandardTokenizer base  = getBaseTokenizer(this);
    
    if (base != this) {
      return base.readMoreData();
    } else if (_source == null) {
      return -1;
    }

    // no input buffer so far
    if (_inputBuffer == null) {
      if ((_flags & TokenizerProperties.F_KEEP_DATA) != 0) {
        _inputBuffer = new char[0x10000];   // 64k
      } else {
        _inputBuffer = new char[0x2000];    // 8k
      }
    }
    
    // this is a good moment to move already read data if the write position is
    // near the end of the buffer and there is a certain space before the current
    // read position
    int readOffset = 0;
    
    if ( ! isFlagSet(TokenizerProperties.F_KEEP_DATA)) {
      if (   _currentReadPos  > _inputBuffer.length / 4
          && _currentWritePos > (3 * _inputBuffer.length) / 4) {
        System.arraycopy(_inputBuffer, _currentReadPos, _inputBuffer, 0, _currentWritePos - _currentReadPos);
        readOffset        = _currentReadPos;
        _rangeStart      += _currentReadPos;
        _currentWritePos -= _currentReadPos;
        _currentReadPos   = 0;
      }
    }
    
    // if there is no space any more and data couldn't be moved (see above)
    // we need a new input buffer
    if (_currentWritePos >= _inputBuffer.length) {
      char[] newBuffer = new char[_inputBuffer.length * 2];
      
      if (isFlagSet(TokenizerProperties.F_KEEP_DATA)) {
        System.arraycopy(_inputBuffer, 0, newBuffer, 0, _currentWritePos);
      } else {
        System.arraycopy(_inputBuffer, _currentReadPos, newBuffer, 0, _currentWritePos - _currentReadPos);
      }
      _inputBuffer = newBuffer;
    }
    
    // now read more data. We need to block the potentially non-blocking read 
    // method of the implementing class
    // Note that a bytes can only be != 0 with <0 for EOF
    while (bytes == 0) {
      try {
        bytes = _source.read(_inputBuffer, _currentWritePos, _inputBuffer.length - _currentWritePos);
      } catch (Exception ex) {
        throw new TokenizerException(ex);
      }
    }
    if (bytes > 0) {
      _currentWritePos += bytes;
    }
    
    // Inform all embedded tokenizers about input buffer changes
    base.synchronizeAll(readOffset);
    return bytes;
  }
  

  /**
   * When the method {@link readMoreData} changes the contents of the input buffer 
   * or the input buffer itself, all embedded tokenizers must be synchronized.
   * That means their member variables are adjusted to the base tokenizer.
   *
   * @param readPosOffset   add this (negative) offset to all current read positions
   */
  protected void synchronizeAll(int readPosOffset) {
    StandardTokenizer base     = getBaseTokenizer(this);
    StandardTokenizer embedded = base;

    while ((embedded = embedded._nextTokenizer) != null) {
      embedded._inputBuffer     = base._inputBuffer;
      embedded._currentWritePos = base._currentWritePos;
      embedded._currentReadPos += readPosOffset;
    }
  }

  /**
   * The number of characters until the next comment, whitespace, string, special
   * sequence or separator are determined.
   *
   * @param token buffer to receive information about the keyword or normal token
   * @return <code>true</code> if a keyword or normal text (e.g. an identifier) 
   *         has been found, <code>false</code> otherwise
   * @throws TokenizerException failure while reading data from the input stream
   */
  protected boolean test4Normal(Token token) throws TokenizerException {
    // find out the return value (length of normal token)
    int len = 0;
    int pos;
    
    while (_currentReadPos + len < _currentWritePos || readMoreData() > 0) {
      if (   isWhitespace(len) 
          || isSpecialSequence(len) 
          || isSeparator(len)) {
        break;
      }
      len++;
    }
    
    // something else found (whitespace, separator, EOF, ...)
    if (len <= 0) {
      return false;
    }
    
    // test on keyword
    TokenizerProperty prop = isKeyword(_currentReadPos + _rangeStart, len);
    
    if (prop != null) {
      token.setType(Token.KEYWORD); 
      token.setCompanion(prop.getCompanion());
    } else {
      token.setType(Token.NORMAL);
    }
    token.setLength(len);
    return true;
  }
  
  
  /**
   * Check if the current read position contains a whitespace. If so retrieve
   * the whole sequence of whitespaces.
   *
   * @param token   add information to this buffer
   * @throws TokenizerException failure while reading data from the input stream
   * @return <code>true</code> if a whitespace sequence was found
   *         <code>false</code> otherwise
   */
  protected boolean test4Whitespace(Token token) throws TokenizerException {
    if (_currentReadPos < _currentWritePos ||  readMoreData() > 0) {
      if (isWhitespace(_inputBuffer[_currentReadPos])) {
        completeWhitespace(token);
        return true;
      }
    }
    return false;
  }
  
  /**
   * After having identified a whitespace, this method continues to read data
   * until it detects a non-whitespace.<br>
   * The method is expected to fill in the type and the length of the whitespace
   * token using the methods {@link Token#setType} and {@link Token#setLength}.
   *
   * @param token   add information to this buffer
   * @throws TokenizerException failure while reading data from the input stream
   */
  protected void completeWhitespace(Token token) throws TokenizerException {
    int start     = _currentReadPos + 1;  // the first whitespace we have already
    int available = _currentWritePos - start;
    int len       = readWhitespaces(_rangeStart + start, available);
    
    while (len == available) {
      if (readMoreData() <= 0) {
        break;
      }
      start    += len;
      available = _currentWritePos - start;
      len      += readWhitespaces(_rangeStart + start, available);
    }

    token.setType(Token.WHITESPACE);
    token.setLength(len + 1);           // the first whitespace we had already
  }
  
  /**
   * This method checks at the given offset if it is a whitespace. Unlike the
   * method {@link #test4Whitespace} it does nothing more.
   *
   * @param offset  check at this position relative to the current read position
   * @throws TokenizerException failure while reading data from the input stream
   * @return <code>true</code> if a whitespace sequence was found at the given offset,
   *         <code>false</code> otherwise
   */
  protected boolean isWhitespace(int offset) throws TokenizerException {
    // enough data ?
    if (_currentReadPos + offset >= _currentWritePos && readMoreData() < 0) {
      return false;
    }
    
    // did we find a whitespace?
    if (isWhitespace(_inputBuffer[_currentReadPos + offset])) {
      _lookAheadToken.setType(Token.WHITESPACE);
      return true;
    } else {
      return false;
    }
  }
      
 
  /**
   * Check if the current read position contains a separator. If so retrieve
   * the separator.
   *
   * @param token   add information about the separator to this buffer
   * @throws TokenizerException failure while reading data from the input stream
   * @return <code>true</code> if a separator was found,
   *         <code>false</code> otherwise
   */
  protected boolean test4Separator(Token token) throws TokenizerException {
    if (isSeparator(0)) {
      _lookAheadToken.setType(Token.UNKNOWN);
      completeSeparator(token);
      return true;
    } else {
      return false;
    }
  }
  
  /**
   * After having identified a separator, this method stores information about
   * it in the given <code>Token</code>. Separators are always single characters.
   *
   * @param token   add information about the separator to this buffer
   * @throws TokenizerException failure while reading data from the input stream
   */
  protected void completeSeparator(Token token) throws TokenizerException {
    token.setType(Token.SEPARATOR);
    token.setLength(1);
  }
  
  
  /**
   * This method checks at the given offset if it contains a separator. Unlike the
   * method {@link #test4Separator} it does nothing more.
   *
   * @param offset  check at this position relative to the current read position
   * @throws TokenizerException failure while reading data from the input stream
   * @return <code>true</code> if a separator was found at the given offset,
   *         <code>false</code> otherwise
   */
  protected boolean isSeparator(int offset) throws TokenizerException {
    if (_currentReadPos + offset < _currentWritePos ||  readMoreData() > 0) {
      if (isSeparator(_inputBuffer[_currentReadPos + offset])) {
        _lookAheadToken.setType(Token.SEPARATOR);
        return true;
      }
    }
    return false;
  }
  
  
  /**
   * Check if the current read position is the start of a special sequence. If 
   * so retrieve the special sequence.
   *
   * @param token   add information about the special sequence to this buffer
   * @throws TokenizerException failure while reading data from the input stream
   * @return <code>true</code> if a special sequence was found,
   *         <code>false</code> otherwise
   */
  protected boolean test4SpecialSequence(Token token) throws TokenizerException {
    TokenizerProperty prop = isSequenceCommentOrString(_rangeStart + _currentReadPos);
    
    if (prop != null) {
      _lookAheadToken.setCompanion(prop);
      completeSpecialSequence(token);
      return true;
    } else {
      return false;
    }
  }
  
  
  /**
   * After having identified a special sequence, this method completes this sequence
   * and stores information about it in the given <code>Token</code>. Note that 
   * a special sequence is not nessecarily identical to the token image that comprises 
   * the special sequence. For instance, a line comment start with the character
   * sequence that identifies the line comment but contains also the data up to 
   * and including the end-of-line combination.
   *
   * @param token   add information about the special sequence to this buffer
   */
  protected void completeSpecialSequence(Token token) throws TokenizerException {
    TokenizerProperty prop = (TokenizerProperty)_lookAheadToken.getCompanion();
    String            seq  = prop.getImages()[0];
      
    // set type and associated info
    token.setType(prop.getType());
    token.setCompanion(prop.getCompanion());
      
    // complete token
    switch (prop.getType()) {
    case Token.STRING:
      token.setLength(completeString(seq.length(), prop));
      break;
    case Token.BLOCK_COMMENT:
      token.setLength(completeBlockComment(seq.length(), prop));
      break;
    case Token.LINE_COMMENT:
      token.setLength(completeLineComment(seq.length()));
      break;
    default:
      token.setLength(seq.length());
    }          
  }
  
  /**
   * This method checks at the given offset if it contains a a special sequence. 
   * Unlike the method {@link #test4SpecialSequence} it does nothing more.
   *
   * @param offset  check at this position relative to the current read position
   * @throws TokenizerException failure while reading data from the input stream
   * @return <code>true</code> if a special sequence was found at the given offset,
   *         <code>false</code> otherwise
   */
  protected boolean isSpecialSequence(int offset) throws TokenizerException {
    TokenizerProperty prop = isSequenceCommentOrString(_rangeStart + _currentReadPos + offset);
    
    if (prop != null) {
      _lookAheadToken.setType(Token.SPECIAL_SEQUENCE);
      _lookAheadToken.setCompanion(prop);
      return true;
    } else {
      return false;
    }
  }
  
  
  /**
   * Completing a line comment. After a line comment sequence has been found, all
   * characters up to and including the end-of-line combination belong to the 
   * line comment. Note that on reaching end-of-file a line comment does not 
   * nessecarily ends with an end-of-line sequence (linefeed for example).
   *
   * @param  offset  start completing at this position relative to the current read position
   * @throws TokenizerException failure while reading data from the input stream
   * @return length of the line comment including start and terminating newline
   */
  protected int completeLineComment(int offset) 
    throws TokenizerException 
  {
    int len = offset;

    while (_currentReadPos + len < _currentWritePos || readMoreData() > 0) {
      switch (_inputBuffer[_currentReadPos + len]) {
      case '\r':
        len++;
        if (_currentReadPos + len < _currentWritePos || readMoreData() > 0) {
          if (_inputBuffer[_currentReadPos + len] == '\n') {
            len++;
          }
        }
        return len;       // this should be one of the usual return points
      case '\n':
        return len + 1;     // this should be the other of the usual return points
      default:
        len++;
      }
    }
    
    // this is reached on EOF
    return len;
  }
  
  
  /**
   * Completing a block comment. After a block comment sequence has been found, all
   * characters up to and including the end sequence of the block comment belong 
   * to the block comment. Note that on reaching end-of-file a block comment does 
   * not nessecarily ends with an end-of-block-comment sequence.
   *
   * @param offset  start completing at this position relative to the current read position
   * @throws TokenizerException failure while reading data from the input stream
   * @return length of the block comment including start and end sequence
   */
  protected int completeBlockComment(int offset, TokenizerProperty prop) 
    throws TokenizerException 
  {
    String[]  images = prop.getImages();
    String    start  = images[0];
    String    end    = images[1];
    boolean   noCase = (prop.getFlags() & TokenizerProperties.F_NO_CASE) != 0;
    int       len    = offset;
    int       level  = 0;
    boolean   nested = isFlagSet(TokenizerProperties.F_ALLOW_NESTED_COMMENTS);

  __LOOP__:
    do {
      // test on nested comments: we take only care for nesting the same
      // block comment
      if (nested) {
        switch (comparePrefix(len, start, noCase)) {
        case 0:     // comment start identified
          level++;
          len += start.length();
          continue __LOOP__;
        case -1:    // EOF reached
          return _currentWritePos - _currentReadPos;   
        }
      }
      
      // is it the end ?
      switch (comparePrefix(len, end, noCase)) {
      case 0:       // comment end identified
        level--;
        len += end.length();
        break;
      case -1:      // EOF reached
        return _currentWritePos - _currentReadPos;
      default:
        len++;
      }
    } while (level >= 0);
    
    // this is the regular return point
    return len;
  }
  
  
  /**
   * Completing a string. After a string start sequence has been found, all
   * characters up to and including the end-of-string sequence belong to the
   * string. Note that on reaching end-of-file a string does not nessecarily ends 
   * with an end-of-string sequence.
   *
   * @param offset  start completing at this position relative to the current read position
   * @throws TokenizerException failure while reading data from the input stream
   * @return length of the string including start and end sequence
   */
  protected int completeString(int offset, TokenizerProperty prop) 
    throws TokenizerException 
  {
    String[]  images        = prop.getImages();
    String    end           = images[1];
    String    esc           = images[2];
    boolean   noCase        = (prop.getFlags() & TokenizerProperties.F_NO_CASE) != 0;
    boolean   escEqualsEnd  =    ( ! noCase && esc.compareTo(end)           == 0)
                              || (   noCase && esc.compareToIgnoreCase(end) == 0);
    int       len           = offset;

    while (true) {
      // test on escape
      if (esc != null) {
        switch (comparePrefix(len, esc, noCase)) {
        case 0:       // escape found
          len += esc.length();
          if (escEqualsEnd) {
            switch (comparePrefix(len, end, noCase)) {
            case 0:
              len += end.length();
              break;
            case -1:      // EOF reached
              return _currentWritePos - _currentReadPos;   
            default:
              return len; // this is the regular return point if the esc is the string end
            }
          } else {
            len++;        // esc != string end: skip the next character
          }
          continue;
        case -1:          // EOF reached
          return _currentWritePos - _currentReadPos;   
        }
      }

      // test on end sequence
      switch (comparePrefix(len, end, noCase)) {
      case 0:             // this is the regular return point if esc != string end
        len += end.length();    
        return len;
      case -1:            // EOF reached    
        return _currentWritePos - _currentReadPos;   
      }
     
      len++;
    }
  }

  
  /**
   * This method compares the characters at the given offset (from the current
   * read position) with the given prefix.
   *
   * @param   offset  start comparing at this offset from the current read position
   * @param   prefic  compare read data with this prefix
   * @param   noCase  case- or not case-sensitive comparison
   * @throws  TokenizerException failure while reading data from the input stream
   * @return  0 if the the given prefix matches the input stream, -1 on EOF and
   *          1 if not matching
   */
  protected int comparePrefix(int offset, String prefix, boolean noCase) 
    throws TokenizerException 
  {
    // compare
    int len = prefix.length();
    
    for (int pos = offset; pos < offset + len; ++pos) {
      // do we have enough data
      if (_currentReadPos + pos >= _currentWritePos && readMoreData() < 0) {
        return -1;
      }
      
      // compare single character
      char c1 = prefix.charAt(pos - offset);
      char c2 = _inputBuffer[_currentReadPos + pos];
      
      if (   c1 != c2
          && (! noCase || Character.toUpperCase(c1) != Character.toUpperCase(c2))) {
        return 1;
      }
    }
    
    // found
    return 0;
  }
  

  /**
   * The method recomputes the line and column position of the tokenizer, if the 
   * flag {@link Tokenizer#F_COUNT_LINES} is set. It gets the token type of the
   * {@link Token} that has been retrieved by the calling {@link #nextToken}.
   * Using the tokenizer control flags and certain other information it tries to
   * to find end-of-line sequences as fast as possible. For example, a line 
   * comment should always contain a end-of-line sequence, so we can simply 
   * increase the line count and set the column count to 0.
   *
   * @param type    the type of the current token
   * @param length  the length of the current token
   */
  protected void adjustLineAndColumn(int type, int length) {
    // line and column counting not required
    if ( ! isFlagSet(TokenizerProperties.F_COUNT_LINES)) {
      return;
    }
    
    // there might be a simple way to determine the current line and column position
    switch (type) {
    case Token.EOF:
      return;
        
    case Token.LINE_COMMENT:        // a line comment always ends with a newline
      _lineNumber++;
      _columnNumber = 0;
      return;
      
    case Token.SPECIAL_SEQUENCE:
    case Token.NORMAL:
    case Token.KEYWORD:
      if (_whitespaceHandler.newlineIsWhitespace()) { // newline is a whitespace character
        _columnNumber += length;                      // it does therefore not occure in other
        return;                                       // tokens
      }
      break;
        
    case Token.WHITESPACE:
      if ( ! _whitespaceHandler.newlineIsWhitespace()) {  // newline is not a whitespace; we do not
        _columnNumber += length;                          // have to test for it in the current 
        return;                                           // token
      }
      break;
    }
    
    // count it
    for (int pos = _currentReadPos; pos < _currentReadPos + length; ++pos) {
      switch (_inputBuffer[pos]) {
      case '\r':
        if (pos + 1 >= _currentReadPos + length || _inputBuffer[pos + 1] != '\n') {
          _lineNumber++;
          _columnNumber = 0;
          break;
        }
        pos++;
        /* no break; */
      case '\n':
        _lineNumber++;
        _columnNumber = 0;
        break;
        
      default:
        _columnNumber++;
      }
    }
  }
  
  /**
   * Checking a given flag. The method considers both the globally set flags
   * in the associated {@link TokenizerProperties} instance and the locally set
   * by {@link #changeParseFlags}.
   *
   * @param flag one of the <code>F_...</code> flags defined in {@link TokenizerProperties}
   */
  protected boolean isFlagSet(int flag) {
    if ((_flagMask & flag) != 0) {
      return (_flags & flag) != 0;
    } else {
      return (getTokenizerProperties().getParseFlags() & flag) != 0;
    }
  }
  
  
  //---------------------------------------------------------------------------
  // Class members
  //
  
  /**
   * mask of flags that can be set separately for a <code>StandardTokenizer</code>.
   */
  protected static final int VALID_FLAGS_MASK = 
      TokenizerProperties.F_RETURN_WHITESPACES 
    | TokenizerProperties.F_TOKEN_POS_ONLY
    | TokenizerProperties.F_ALLOW_NESTED_COMMENTS
    | TokenizerProperties.F_KEEP_DATA
    | TokenizerProperties.F_COUNT_LINES;
  
  /**
   * {@link TokenizerProperties} tha tare used if no others have been 
   * specified by calling {@link #setProperties}.
   */
  protected StandardTokenizerProperties _defaultProperties = null;
  
  
  //---------------------------------------------------------------------------
  // Members
  //
  
  /**
   * overall tokenizer flags.
   */
  protected int _flags = 0;
  
  /**
   * a combination of <code>F_...</code> constants defined in {@link TokenizerProperties}
   * indicating which bits in the {@link #_flags} member are valid. All other 
   * flags are taken from the associated {@link TokenizerProperties} object.
   *
   * @see #changeParseFlags
   */
  private int _flagMask = 0;
  
  /**
   * This buffer holds the currently read data. Dont use a buffered reader, since
   * we do buffering here.
   */
  protected char[] _inputBuffer = null;

  /**
   * index in {@link #_inputBuffer} there {@link #nextToken} will start parsing.
   */
  protected int _currentReadPos = 0;

  /**
   * index in {@link #_inputBuffer} there {@link #readMoreData} will fill in new data.
   */
  protected int _currentWritePos = 0;
  
  /**
   * Mapping of index 0 of {@link #_inputBuffer} to the absolute start of the 
   * input stream.
   */
  protected int _rangeStart = 0;
  
  /**
   * if line counting is enabled, this contains the current line number starting
   * with 0.
   */
  protected int _lineNumber = -1;

  /**
   * if line counting is enabled, this contains the current column number starting
   * with 0.
   */
  protected int _columnNumber = -1;
  
  /**
   * Token found by the last call to {@link #nextToken}
   */
  protected Token _currentToken = null;
  
  /**
   * If a mthod could detect the tokewn after the currently assembled one, infomation
   * regarding that token are stored here.
   */
  protected Token _lookAheadToken = new Token();
  
  /**
   * For embedded tokenizers: this is the list of the succeding tokenizers
   */
  protected StandardTokenizer _nextTokenizer = null;

  /**
   * For embedded tokenizers: this is the list of the previous tokenizers
   */
  protected StandardTokenizer _prevTokenizer = null;
  
  /**
   * Whitespace handler
   */
  private de.susebox.jtopas.spi.WhitespaceHandler _whitespaceHandler = null;

  /**
   * Separator handler
   */
  private de.susebox.jtopas.spi.SeparatorHandler _separatorHandler = null;

  /**
   * Keyword handler
   */
  private de.susebox.jtopas.spi.KeywordHandler _keywordHandler = null;

  /**
   * Sequence handler
   */
  private de.susebox.jtopas.spi.SequenceHandler _sequenceHandler = null;
  
  /**
   * Sequence handler
   */
  private de.susebox.jtopas.spi.PatternHandler _patternHandler = null;
  
  /**
   * The source of input data
   */
  private TokenizerSource _source = null;
  
  /**
   * The characteristics of this tokenizer.
   */
  private TokenizerProperties _properties = null;
}


/**
 * Implementation of the {@link de.susebox.jtopas.spi.DataProvider} interface 
 * for the {@link StandardTokenizer}.
 */
class StandardDataProvider implements DataProvider {
  
  //---------------------------------------------------------------------------
  // constructors
  //
  
  /**
   * The constructor takes the nessecary parameters for the methods defined
   * below
   */
  public StandardDataProvider(char[] data, int startPosition, int length) {
    _data           = data;
    _startPosition  = startPosition;
    _length         = length;
  }
  
  
  //---------------------------------------------------------------------------
  // methods of the DataProvider interface
  //
  
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
  public char[] getData() {
    return _data;
  }
  
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
   * @see #toString
   */
  public char[] getDataCopy() {
    char[] copy = new char[getLength()];
    
    System.arraycopy(_data, getStartPosition(), copy, 0, copy.length);
    return copy;
  }
  
     
  /**
   * Retrieving the position where the data to analyze start in the buffer provided
   * by {@link #getData}. The calling {@link DataMapper} must not access data
   * prior to this index in the character array.
   *
   * @return  index in the character array returned by {@link #getData}, where data starts
   */
  public int getStartPosition() {
    return _startPosition;
  }

  /**
   * Retrieving the maximum number of characters in the array provided by {@link getData}
   * that can be analyzed by the calling {@link DataMapper}.
   *
   * @param testChar  check this character
   * @return <code>true</code> if the given character is a separator,
   *         <code>false</code> otherwise
   */
  public int getLength() {
    return _length;
  }
  
  /**
   * Returning the valid data range of this <code>DataProvider</code> as a string.
   * This method is an alternative to {@link #getDataCopy}.
   *
   * @return the string representation of the valid data range
   */
  public String toString() {
    if (_data != null) {
      return new String(_data, _startPosition, _length);
    } else {
      return "";
    }
  }
  
  
  //---------------------------------------------------------------------------
  // Members
  //
  private char[]  _data           = null;
  private int     _startPosition  = 0;
  private int     _length         = 0;
}
