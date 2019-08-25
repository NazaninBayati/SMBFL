/*
 * Token.java: Token for parsers etc.
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

package de.susebox.jtopas;

//-----------------------------------------------------------------------------
// Class Token
//

/**<p>
 * Instances of this class are created by the classes implementing the 
 * {@link Tokenizer} interface. <code>Token</code> describes a portion of text 
 * according to the settings given to the producing {@link Tokenizer} in form of 
 * a {@link TokenizerProperties} object. Beside the token type the token image 
 * itself, its position in the input stream, line and column position and associated 
 * informations can be obtained from the <code>Token</code> (provided, the nessecary
 * parse flags are set in the tokenizer).
 *</p><p>
 * This class replaces the older {@link de.susebox.java.util.Token} which is
 * deprecated.
 *</p>
 *
 * @author  Heiko Blau
 * @see     Tokenizer
 * @see     TokenizerProperties
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
   * The token matches a pattern. This can be a number od identifier pattern for 
   * instance.
   */  
  public static final byte PATTERN = 3;

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
   * @see   #getType
   */  
  public void setType(int type) {
    _type = type;
  }
    
  /**
   * Obtaining the type of the <code>Token</code>. This is one of the constants
   * defined in the <code>Token</code> class.
   *
   * @return the token type
   * @see   #setType
   */  
  public int getType() {
    return _type;
  }
    
  /**
   * Setting the token image. Note that some {@link Tokenizer} only fill position 
   * and length information rather than setting the token image. This strategy 
   * might have a tremendous influence on the parse performance and the memory 
   * allocation.
   *
   * @param image   the token image
   * @see   #getImage
   */  
  public void setImage(String image) {
    if ((_image = image) == null) {
      _length = 0;
    } else {
      _length = _image.length();
    }
  }
    
  /**
   * Obtaining the token image as a {@link java.lang.String}. Th method returns
   * <code>null</code> when called on an end-of-file token or if the {@link Tokenizer} 
   * producing this <code>Token</code> object, is configured to return only 
   * position informations (see {@link TokenizerProperties#F_TOKEN_POS_ONLY}).
   *
   * @return the token image as a {@link java.lang.String} (<code>null</code> is possible).
   * @see   #setImage
   */  
  public String getImage() {
    return _image;
  }
    
  /**
   * Setting the length of the token. Some {@link Tokenizer} may prefer or may be
   * configured not to return a token image, but only the position and length
   * informations. This may save a lot of time whereever only a subset of the found
   * tokens are actually needed by the user.
   *<br>
   * This method is an alternative to {@link #setEndPosition} depending on which
   * information is at hand or easier to obtain for the {@link Tokenizer} producing
   * this <code>Token</code>.
   *<br>
   * Note that this method is implicitely called by {@link #setToken} and 
   * {@link #setEndPosition}.
   *
   * @param length the length of the token
   * @see   #getLength
   * @see   #setEndPosition
   */  
  public void setLength(int length) {
    _length = length;
  }
    
  /**
   * Obtaining the length of the token. Note that some token types have a zero length
   * (like EOF or UNKNOWN).
   *
   * @return the length of the token.
   * @see   #setLength
   * @see   #getEndPosition
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
   * @see   #getStartPosition
   * @see   #setEndPosition
   */  
  public void setStartPosition(int startPosition) {
    _startPosition = startPosition;
  }
    
  /**
   * Obtaining the starting position of the token. If not set or not of interest, 
   * -1 is returned.
   *
   * @return  start position of the token.
   * @see     #setStartPosition
   * @see     #getEndPosition
   */  
  public int getStartPosition() {
    return _startPosition;
  }
    
  /**
   * Setting the end position of the token relative to the start of the input 
   * stream. For instance, the first character in a file has the start position 
   * 0. The character at the given end position is <strong>NOT</code> part of
   * this <code>Token</code>. This is the same principle as in the 
   * {@link java.lang.String#substring(int, int)} method.
   *<br>
   * This method is an alternative to {@link #setLength} depending on which
   * information is at hand or easier to obtain for the {@link Tokenizer} producing
   * this <code>Token</code>.
   *<br>
   * Note that this method <strong>MUST</strong> be called after {@link #setStartPosition}
   * since it affects the length of the token. Its effect is in turn eliminated
   * by calls to {@link #setLength} and {@link #setToken}
   *
   * @param endPosition   the position where the token ends in the input stream.
   */  
  public void setEndPosition(int endPosition) {
    setLength(endPosition - _startPosition);
  }
    
  /**
   * Obtaining the end position of this token. Note that the return value of this
   * method is only valid, if {@link setStartPosition} has been called and one
   * of the methods {@link #setToken}, {@link #setLength} or {@link #setEndPosition}.
   *
   * @return  end position of the token.
   * @see     #setEndPosition
   * @see     #setStartPosition
   * @see     #getStartPosition
   */  
  public int getEndPosition() {
    return getLength() - getStartPosition();
  }
    
  /**
   * In {@link Tokenizer}'s counting lines and columns, this method is used to 
   * set the line number where the beginning of the <code>Token</code> was found.
   * Line numbers start with 0.
   *
   * @param lineno line number where the token begins
   * @see   #getStartLine
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
   * @see   #getStartColumn
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
   * @param type  token type, one of the class constants.
   * @param image the token image itself
   */  
  public Token(int type, String image) {
    this(type, image, null);
  }
  
  /**
   * Construct a token of a given type with the given image and a companion. This
   * constructor is most useful for keywords or special sequences.
   *
   * @param type      token type, one of the class constants.
   * @param image     the token image itself
   * @param companion an associated information of the token type
   */  
  public Token(int type, String image, Object companion) {
    setType(type);
    setImage(image);
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
   * @param   object  the {@link java.lang.Object} to compare
   * @return <code>true</code> if two token are equal, <code>false</code>
   *          otherwise
   */
  public boolean equals(Object object) {
    // Test on intentical objects and incompatible classes
    if (object == null) {
      return false;
    } else if (object == this) {
      return true;
    } else if (object.getClass() != getClass()) {
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
    } else if (   (getImage() == null && other.getImage() != null)
               || (getImage() != null && ! getImage().equals(other.getImage()))) {
      return false;
    }
    return true;
  }
  
  /** 
   * Implementation of the well known method {@link java.lang.Object#toString}.
   *
   * @return string representation of this object
   */
  public String toString() {
    StringBuffer buffer = new StringBuffer();
    
    // Type
    buffer.append("Type ");
    buffer.append(Token.getTypeName(getType()));
    
    // Image
    if (getType() != EOF) {
      buffer.append(":  ");
      if (getImage() != null) {
        buffer.append(getImage());
      } else {
        buffer.append("no image, length ");
        buffer.append(getLength());
      }
    }
    return buffer.toString();
  }

  /**
   * Getting a type name for displaying. The methode never fails even if the
   * given type is unknown.
   *
   * @param type  one of the Token type constants
   * @return a string representation of the given type constant
   */
  public static String getTypeName(int type) {
    switch (type) {
    case NORMAL:
      return "NORMAL";
    case KEYWORD:
      return "KEYWORD";
    case STRING:
      return "STRING";
    case PATTERN:
      return "PATTERN";
    case SPECIAL_SEQUENCE:
      return "SPECIAL_SEQUENCE";
    case SEPARATOR:
      return "SEPARATOR";
    case WHITESPACE:
      return "WHITESPACE";
    case LINE_COMMENT:
      return "LINE_COMMENT";
    case BLOCK_COMMENT:
      return "BLOCK_COMMENT";
    case EOF:
      return "EOF";
    default:
      return "UNKNOWN";
    }
  }
  
  
  //---------------------------------------------------------------------------
  // members
  //
  
  /**
   * The token type. Usually one of the constants {@link #NORMAL}, {@link #EOF} etc.
   *
   * @see #getType
   * @see #setType
   */
  protected int _type;

  /**
   * The string representing the token. This member might not be present if a
   * {@link Tokenizer} is configured not to return token images.
   *
   * @see #getImage
   * @see #setImage
   */
  protected String _image;

  /**
   * The length of the string representing the token..
   *
   * @see #getLength
   * @see #setLength
   */
  protected int _length;

  /**
   * An information associated with the token. For instance, keywords can be
   * distinguished using different companions for each keyword
   *
   * @see #getCompanion
   * @see #setCompanion
   * @see TokenizerProperties#addKeyword
   */
  protected Object _companion;

  /**
   * The absolute position where the token starts in the source of data.
   *
   * @see #getStartPosition
   * @see #setStartPosition
   */
  protected int _startPosition;

  /**
   * The line where the token starts in the source of data. This member may not 
   * be set if a {@link Tokenizer} is configured not to return token line and 
   * column (see {@link TokenizerProperties#F_COUNT_LINES}).
   *
   * @see #getStartLine
   * @see #setStartLine
   */
  protected int _startLine;

  /**
   * The column where the token starts in the source of data. This member may not 
   * be set if a {@link Tokenizer} is configured not to return token line and 
   * column (see {@link TokenizerProperties#F_COUNT_LINES}).
   *
   * @see #getStartColumn
   * @see #setStartColumn
   */
  protected int _startColumn;

  /**
   * The line where the token ends in the source of data. This member may not 
   * be set if a {@link Tokenizer} is configured not to return token line and 
   * column (see {@link TokenizerProperties#F_COUNT_LINES}).
   *
   * @see #getEndLine
   * @see #setEndLine
   */
  protected int _endLine;

  /**
   * The column where the token ends in the source of data. This member may not 
   * be set if a {@link Tokenizer} is configured not to return token line and 
   * column (see {@link TokenizerProperties#F_COUNT_LINES}).
   *
   * @see #getEndColumn
   * @see #setEndColumn
   */
  protected int _endColumn;
}
