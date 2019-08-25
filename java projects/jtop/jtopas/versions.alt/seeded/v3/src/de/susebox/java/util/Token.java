/*
 * Token.java: Token for parsers etc.
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

package de.susebox.java.util;

//-----------------------------------------------------------------------------
// Class Token
//

/**<p>
 * Instances of this class are created by the classes implementing the 
 * {@link Tokenizer} interface.
 * <code>Token</code> describes a portion of text according to the settings given
 * to the producing {@link Tokenizer}. Beside the token type the token image 
 * itself, its position in the input stream, line and column position and 
 * associated informations can be obtained from the <code>Token</code>.
 *</p><p>
 * This class is deprecated. Use {@link de.susebox.jtopas.Token} instead.
 *</p>
 *
 * @author Heiko Blau
 * @see Tokenizer
 * @deprecated
 */
public class Token {
  
  //---------------------------------------------------------------------------
  // constants (token types)
  //

  /**
   * The token is nothing special (no keyword, no whitespace, etc.).
   */  
  public static final byte NORMAL = 0;

  /**
   * The token is a keyword registered with the used {@link Tokenizer}.
   */  
  public static final byte KEYWORD = 1;

  /**
   * The token is one of the quoted strings known to the {@link Tokenizer}. In Java
   * this would be for instance a "String" or a 'c' (haracter).
   */  
  public static final byte STRING = 2;
  
  /**
   * If a {@link Tokenizer} recognizes numbers, this token is one.
   */  
  public static final byte NUMBER = 3;
  
  /**
   * Special sequences are characters or character combinations that have a certain
   * meaning to the parsed language or dialect. In computer languages we have for
   * instance operators, end-of-statement characters etc.
   * A companion might have been associated with a special sequence. It probably
   * contains information important to the user of the <code>Token</code>.
   */  
  public static final byte SPECIAL_SEQUENCE = 4;
  
  /** 
   * Separators are otherwise not remarkable characters. An opening parenthesis 
   * might be nessecary for a syntactically correct text, but without any special 
   * meaning to the compiler, interpreter etc. after it has been detected.
   */  
  public static final byte SEPARATOR = 5;
  
  /** 
   * Whitespaces are portions of the text, that contain one or more characters 
   * that separate the significant parts of the text. Generally, a sequence of 
   * whitespaces is equally represented by one single whitespace character. That 
   * is the difference to separators.
   */  
  public static final byte WHITESPACE = 6;

  /**
   * Although a line comment is - in most cases - actually a whitespace sequence, it
   * is often nessecary to handle it separately. Syntax hilighting is a thing that
   * needs to know a line comment.
   */  
  public static final byte LINE_COMMENT = 7;

  /**
   * Block comments are also a special form of a whitespace sequence. See 
   * {@link #LINE_COMMENT} for details.
   */  
  public static final byte BLOCK_COMMENT = 8;

  /**
   * A token of the type <code>EOF</code> is used to indicate an end-of-line condition
   * on the input stream of the tokenizer.
   */  
  public static final byte EOF = -1;
  
  /**
   * This is for the leftovers of the lexical analysis of a text.
   */  
  public static final byte UNKNOWN = -2;
    

  //---------------------------------------------------------------------------
  // Getter- und Setter-Methoden
  //
  
  /**
   * Setting the type property of the <code>Token</code>. This is one of the constants
   * defined in this class.
   *
   * @param type the token type
   */  
  public void setType(int type) {
    _type = type;
  }
    
  /**
   * Obtaining the type of the <code>Token</code>. This is one of the constants
   * defined in the <code>Token</code> class.
   *
   * @return the token type
   */  
  public int getType() {
    return _type;
  }
    
  /**
   * Setting the token image. Note that some {@link Tokenizer} only fill position and
   * length information rather than setting the token image. This strategy might have
   * a tremendous influence on the parse performance and the memory allocation.
   *
   * @param token the token image
   */  
  public void setToken(String token) {
    if ((_token = token) == null) {
      _length = 0;
    } else {
      _length = _token.length();
    }
  }
    
  /**
   * Obtaining the token image as a String.
   * @return the token image as a {@link java.lang.String}.
   */  
  public String getToken() {
    return _token;
  }
    
  /**
   * Setting the length of the token. Some {@link Tokenizer} may prefer or may be
   * configured not to return a token image, but only the position and length
   * informations. This may save a lot of time whereever only a subset of the found
   * tokens are actually needed by the user.
   *
   * @param length the length of the token
   */  
  public void setLength(int length) {
    _length = length;
  }
    
  /**
   * Obtaining the length of the token. Note that some token types have a zero length
   * (like EOF or UNKNOWN).
   *
   * @return the length of the token.
   */  
  public int getLength() {
    return _length;
  }
    
  /**
   * Some token may have associated informations for the user of the <code>Token</code>.
   * A popular thing would be the association of an integer constant to a special
   * sequence or keyword to be used in fast <code>switch</code> statetents.
   *
   * @param companion the associated information for this token
   */  
  public void setCompanion(Object companion) {
    _companion = companion;
  }
    
  /**
   * Obtaining the associated information of the token. Can be <code>null</code>. See
   * {@link #setCompanion} for details.
   *
   * @return the associated information of this token
   */  
  public Object getCompanion() {
    return _companion;
  }
  
  /**
   * Setting the start position of the token relative to the start of the input 
   * stream. For instance, the first character in a file has the start position 
   * 0.
   *
   * @param startPosition the position where the token starts in the input stream.
   */  
  public void setStartPosition(int startPosition) {
    _startPosition = startPosition;
  }
    
  /**
   * Obtaining the starting position of the token.
   *
   * @return  start position of the token.
   * @see     #setStartPosition
   */  
  public int getStartPosition() {
    return _startPosition;
  }
    
  /**
   * In {@link Tokenizer}'s counting lines and columns, this method is used to 
   * set the line number where the beginning of the <code>Token</code> was found.
   * Line numbers start with 0.
   *
   * @param lineno line number where the token begins
   */  
  public void setStartLine(int lineno) {
    _startLine = lineno;
  }
    
  /**
   * Obtaining the line number where the <code>Token</code> starts. See also
   * {@link #setStartLine} for details.<br>
   * If a tokenizer doesn't count lines and columns, the returned value is -1.
   *
   * @return  the line number where the token starts or -1, if no line counting is
   *          performed
   * @see     #setStartLine
   */  
  public int getStartLine() {
    return _startLine;
  }
    
  /**
   * In {@link Tokenizer}'s counting lines and columns, this method is used to 
   * set the column number where the beginning of the <code>Token</code> was 
   * found. Column numbers start with 0.
   *
   * @param colno number where the token begins
   */  
  public void setStartColumn(int colno) {
    _startColumn = colno;
  }
    
  /**
   * Obtaining the column number of the <code>Token</code> start. See {@link #setStartColumn}
   * for details.<br>
   * If a tokenizer doesn't count lines and columns, the returned value is -1.
   *
   * @return  the column number where the token starts or -1, if no line counting 
   *          is performed
   * @see     #setStartColumn
   */  
  public int getStartColumn() {
    return _startColumn;
  }
    
  /**
   * In {@link Tokenizer}'s counting lines and columns, this method is used to 
   * set the line number where the end of the <code>Token</code> was found. 
   * See {@link #setStartLine} for more.<br>
   * The end line number is the one there the first character was found that does
   * <b><i>NOT</i></b> belongs to the token. This approach is choosen in accordance
   * to the toIndex parameters in {@link java.lang.String#substring(int, int)}.
   *
   * @param lineno line number where the token ends
   */  
  public void setEndLine(int lineno) {
    _endLine = lineno;
  }
    
  /**
   * Obtaining the line number where the token ends. See {@link #setEndLine} for 
   * more. If a tokenizer doesn't count lines and columns, the returned value is 
   * -1.
   *
   * @return  line number where the token ends or -1, if no line counting is
   *          performed
   * @see     #setEndLine
   */  
  public int getEndLine() {
    return _endLine;
  }
    
  /**
   * In {@link Tokenizer}'s counting lines and columns, this method is used to set the
   * column number where the end of the <code>Token</code> was found.<br>
   * The end column number is the one of the first character that does
   * <b><i>NOT</i></b> belongs to the token. This approach is choosen in accordance
   * to the toIndex parameters in {@link java.lang.String#substring(int, int)}.
   *
   * @param colno column number where the token ends
   */  
  public void setEndColumn(int colno) {
    _endColumn = colno;
  }
    
  /**
   * Obtaining the column number where the <code>Token</code> ends. See {@link #setEndColumn}
   * for more.<br>
   * If a tokenizer doesn't count lines and columns, the returned value is -1.
   *
   * @return  column number where the token ends or -1, if no line counting is
   *          performed
   * @see     #setEndColumn
   */  
  public int getEndColumn() {
    return _endColumn;
  }
    
 
  //---------------------------------------------------------------------------
  // construction
  //
  
  /**
   * Default constructor.
   */  
  public Token() {
    this(UNKNOWN, null, null);
  }
  
  /**
   * Constructs a token of a given type. Only the type of the token is known but not
   * its image or positions.
   *
   * @param type token type, one of the class constants.
   */  
  public Token(int type) {
    this(type, null, null);
  }
  
  /**
   * Construct a token of a given type with the given image. No position information
   * is given.
   *
   * @param type token type, one of the class constants.
   * @param token the token image itself
   */  
  public Token(int type, String token) {
    this(type, token, null);
  }
  
  /**
   * Construct a token of a given type with the given image and a companion. This
   * constructor is most useful for keywords or special sequences.
   *
   * @param type token type, one of the class constants.
   * @param token the token image itself
   * @param companion an associated information of the token type
   */  
  public Token(int type, String token, Object companion) {
    setType(type);
    setToken(token);
    setCompanion(companion);
    setStartPosition(-1);
    setStartLine(-1);
    setStartColumn(-1);
    setEndLine(-1);
    setEndColumn(-1);
  }
  
  /**
   * Implementation of the well known method {@link java.lang.Object#equals}.
   * Note that two token are equal if every member of it is equal. That means
   * that token retrieved by two different {@link Tokenizer} instances can be
   * equal.
   *
   * @return  <code>true</code> if two token are equal, <code>false</code>
   *          otherwise
   */
  public boolean equals(Object object) {
    // Test on intentical objects and incompatible classes
    if (object == null) {
      return false;
    } else if (object == this) {
      return true;
    } else if ( ! (object instanceof Token)) {
      return false;
    }
    
    // real check
    Token other = (Token)object;
      
    if (getType() != other.getType()) {
      return false;
    } else if (getStartPosition() != other.getStartPosition()) {
      return false;
    } else if (getLength() != other.getLength()) {
      return false;
    } else if (getStartLine() != other.getStartLine()) {
      return false;
    } else if (getStartColumn() != other.getStartColumn()) {
      return false;
    } else if (getEndLine() != other.getEndLine()) {
      return false;
    } else if (getEndColumn() != other.getEndColumn()) {
      return false;
    } else if (   (getCompanion() == null && other.getCompanion() != null)
               || (getCompanion() != null && getCompanion().equals(other.getCompanion()))) {
      return false;
    } else if (   (getToken() == null && other.getToken() != null)
               || (getToken() != null && ! getToken().equals(other.getToken()))) {
      return false;
    }
    return true;
  }
  
  
  //---------------------------------------------------------------------------
  // members
  //
  protected int     _type;
  protected String  _token;
  protected int     _length;
  protected Object  _companion;
  protected int     _startPosition;
  protected int     _startLine;
  protected int     _startColumn;
  protected int     _endLine;
  protected int     _endColumn;
}
