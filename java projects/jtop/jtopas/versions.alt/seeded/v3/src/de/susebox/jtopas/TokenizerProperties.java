/*
 * TokenizerProperties.java: store for tokenizer characteristics.
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
import java.util.Iterator;


//-----------------------------------------------------------------------------
// Interface TokenizerProperties
//

/**<p>
 * The interface <code>TokenizerProperties</code> declares constants and methods
 * to maintain the characteristics of a {@link Tokenizer}, e. g. comments, keywords
 * and special sequences. A <code>TokenizerProperties</code> implementation instance
 * can be used by one or more {@link Tokenizer} instances.
 *</p><p>
 * Beside the dedicated method groups for comments, strings, keywords and
 * special sequences (e.g. {@link #addKeyword}, {@link #removeKeyword} and 
 * {@link #keywordExists}) there is a set of generic methods working with 
 * {@link TokenizerProperty} objects ({@link #addProperty}, {@link #removeProperty}
 * and {@link #propertyExists}).
 *</p><p>
 * This interface is separated from the {@link Tokenizer} interface mainly to 
 * distinguish between the more static information, the actual source of data
 * and the tokenizing process. Especially in multithreaded environments where
 * multible instances of "equal" tokenizers are run at the same time, it saves 
 * both memory resources and setup effort to have one <code>TokenizerProperties</code>
 * instance for all {@link Tokenizer} instances.
 *</p><p>
 * Beside its function as a store for lexical element descriptions, this interface
 * also provides an event mechanism to notify all interested objects about changes
 * (additions, modifications and removals) of such lexical element descriptions.
 *</p><p>
 * This interface partly replaces the older {@link de.susebox.java.util.Tokenizer}
 * interface which is deprecated.
 *</p>
 *
 * @see     Token
 * @see     Tokenizer
 * @see     TokenizerProperty
 * @author  Heiko Blau
 */
public interface TokenizerProperties {
  
  //---------------------------------------------------------------------------
  // default character classes
  //
  
  /** 
   * Whitespaces are portions of the text, that contain one or more characters 
   * that separate the significant parts of the text. Generally, a sequence of 
   * whitespaces is equally represented by one single whitespace character. That 
   * is the difference to separators.
   */  
  public static final String DEFAULT_WHITESPACES = " \t\r\n";
  
  /** 
   * Separators are otherwise not remarkable characters. An opening parenthesis 
   * might be nessecary for a syntactically correct text, but without any special 
   * meaning to the compiler, interpreter etc. after it has been detected.
   */  
  public static final String DEFAULT_SEPARATORS = "\u0021\u0023-\u002f\u003a-\u0040\u005b-\u005e\u0060\u007b-\u007e";
  
  /**
   * Default starting sequence of a block comment (Java, C/C++).
   */  
  public static final String DEFAULT_BLOCK_COMMENT_START = "/*";

  /**
   * Default end sequence of a block comment (Java, C/C++).
   */  
  public static final String DEFAULT_BLOCK_COMMENT_END = "*/";
  
  /**
   * Default line comment seqence (Java, C++)
   */  
  public static final String DEFAULT_LINE_COMMENT = "//";
  
  /**
   * The well-known string starting sequence of C/C++, Java and other languages.
   */  
  public static final String DEFAULT_STRING_START = "\"";
  
  /**
   * The well-known string ending sequence of C/C++, Java and other languages.
   */  
  public static final String DEFAULT_STRING_END = DEFAULT_STRING_START;
  
  /**
   * The well-known escape sequence for strings in C/C++, Java and other languages.
   */  
  public static final String DEFAULT_STRING_ESCAPE = "\\";
  
  /**
   * The well-known character starting sequence of C/C++, Java and other languages.
   */  
  public static final String DEFAULT_CHAR_START  = "'";
  
  /**
   * The well-known character ending sequence of C/C++, Java and other languages.
   */  
  public static final String DEFAULT_CHAR_END = DEFAULT_CHAR_START;

  /**
   * The well-known escape sequence for character literals in C/C++, Java and other
   * languages.
   */  
  public static final String DEFAULT_CHAR_ESCAPE = DEFAULT_STRING_ESCAPE;
  
  
  //---------------------------------------------------------------------------
  // common control flags
  //
  
  /**
   * General compare operations are case-sensitive, that means 'A' equals 'A' 
   * but not 'a'. It is not nessecary to set this flag, since case-sensitive 
   * comparison is the default.
   *<br>
   * It is mainly used in conjunction with {@link #F_NO_CASE}. If <code>F_NO_CASE</code>
   * is set via {@link #setParseFlags}, <code>F_CASE</code> can be used for
   * properties where case-sensitivity is nessecary (refer to the <code>add...</code>
   * methods with the <code>flags</code> parameter, like {@link #addKeyword(String, Object, int)}.
   *<br>
   * If neither <code>F_CASE</code> nor <code>F_NO_CASE</code> is set, <code>F_CASE</code>
   * is assumed. If both flags are set, <code>F_CASE</code> takes preceedence.
   */
  public static final short F_CASE = 0x0002;

  /**
   * When this flag is set for the whole <code>TokenizerProperties</code> instance
   * (see {@link #setParseFlags}, input data is generally treated case-insensitive.
   * Specific properties may have set the flag {@link #F_CASE} for case-sensitive
   * parsing.
   *<br>
   * On the other hand, <code>F_NO_CASE</code> can be set for special properties
   * when <code>F_CASE</code> is set as the common flag by {@link #setParseFlags}.
   *<br>
   * If neither <code>F_CASE</code> nor <code>F_NO_CASE</code> is set, <code>F_CASE</code>
   * is assumed. If both flags are set, <code>F_CASE</code> takes preceedence.
   */
  public static final short F_NO_CASE = 0x0001;

  /**
   * In many cases, parsers are not interested in whitespaces. If You are, use
   * this flag to force the tokenizer to return whitespace sequences as a token.
   *<br>
   * Note that this flag is a dynamic one. It can be set either for a
   * <code>TokenizerProperties</code> instance taking effect on all {@link Tokenizer}
   * instances sharing the <code>TokenizerProperties</code> instance, or for an 
   * individual {@link Tokenizer} exclusively.
   */
  public static final short F_RETURN_WHITESPACES    = 0x0008;
  
  /**
   * For perfomance and memory reasons, this flag is used to avoid copy operations
   * for every token. The token image itself is not returned in a {@link Token}
   * instance, only its position and length in the input stream.
   *<br>
   * Note that this flag is a dynamic one. It can be set either for a
   * <code>TokenizerProperties</code> instance taking effect on all {@link Tokenizer}
   * instances sharing the <code>TokenizerProperties</code> instance, or for an 
   * individual {@link Tokenizer} exclusively.
   */
  public static final short F_TOKEN_POS_ONLY        = 0x0010;

  /**
   * Set this flag to let the tokenizer buffer all data. Normally, a tokenizer
   * keeps only a certain amount of periodically changing data in its internal 
   * buffer.
   *<br>
   * Note that this flag is a dynamic one. It can be set either for a
   * <code>TokenizerProperties</code> instance taking effect on all {@link Tokenizer}
   * instances sharing the <code>TokenizerProperties</code> instance, or for an 
   * individual {@link Tokenizer} exclusively.
   */
  public static final short F_KEEP_DATA             = 0x0020;

  /**
   * Tells the tokenizer to count lines and columns. The tokenizer may use
   * System.getProperty("line.separator") to obtain the end-of-line sequence.
   *<br>
   * Note that this flag is a dynamic one. It can be set either for a
   * <code>TokenizerProperties</code> instance taking effect on all {@link Tokenizer}
   * instances sharing the <code>TokenizerProperties</code> instance, or for an 
   * individual {@link Tokenizer} exclusively.
   */
  public static final short F_COUNT_LINES           = 0x0040;

  /**
   * Nested block comments are normally not allowed. This flag changes the 
   * default behaviour
   *<br>
   * Note that this flag is a dynamic one. It can be set either for a
   * <code>TokenizerProperties</code> instance taking effect on all {@link Tokenizer}
   * instances sharing the <code>TokenizerProperties</code> instance, or for an 
   * individual {@link Tokenizer} exclusively.
   */
  public static final short F_ALLOW_NESTED_COMMENTS = 0x0080;
  

  //---------------------------------------------------------------------------
  // trivial property methods
  //
  
  /**
   * Setting the control flags of the <code>TokenizerProperties</code>. Use a 
   * combination of the <code>F_...</code> flags for the parameter.
   *<br>
   * The parse flags can be set globally for all {@link Tokenizer} objects sharing
   * this <code>TokenizerProperties</code> instance. Some flags can be set for each
   * tokenizer separately.
   *<br>
   * Flags that should be used only on the <code>TokenizerProperties</code>, are:
   *<ul><li>
   *  {@link #F_NO_CASE}
   *</li><li>
   *  {@link #F_CASE}
   *</li></ul>
   * When adding a property like a keyword without explicitely specifying flags,
   * these properties are handled with the flags that were effective at the time
   * of adding.
   *<br>
   * The method fires a {@link TokenizerPropertyEvent} of type {@link TokenizerPropertyEvent#PROPERTY_MODIFIED}
   * each time it is called with different flag masks. The passed {@link TokenizerProperty}
   * has the type {@link TokenizerProperty#PARSE_FLAG_MASK}.
   *
   * @param flags the parser control flags
   */
  public void setParseFlags(int flags);

   /**
    * Retrieving the parser control flags. A bitmask containing the <code>F_...</code>
    * constants is returned. These flags are the ones set for all {@link Tokenizer}
    * instances sharing this <code>TokenizerProperties</code> object and may be
    * partially overridden in these tokenizers.
    *
    * @return the globally set parser control flags
    * @see    #setParseFlags
    */
  public int getParseFlags();
  
  
  //---------------------------------------------------------------------------
  // whitespaces and single-character separators
  //
  
  /**
   * Setting the whitespace character set of the tokenizer. Implementations should
   * be able to operate on ranges like "a-z" when more than two whitespace characters 
   * are neighbours in the UNICODE character set.
   *<br>
   * Whitespaces are sequences that have the same syntactical meaning as one
   * single whitespace character would have. That means "     " (many spaces) is
   * the same as " " (one space).
   *<br>
   * An implementation of this interface should fire a {@link TokenizerPropertyEvent}
   * of type {@link TokenizerPropertyEvent.PROPERTY_ADDED} or 
   * {@link TokenizerPropertyEvent.PROPERTY_MODIFIED} to all registered listeners
   * (see {@link #addTokenizerPropertyListener}).
   *<br>
   * The method accepts an empty string to describe "no whitespaces".
   *
   * @param   whitespaces the whitespace set
   * @throws  IllegalArgumentException when <code>null</code> is passed or incomplete
   *          ranges are specified (e.g. <code>"a-"</code>)
   * @see     #getWhitespaces
   */
  public void setWhitespaces(String whitespaces) 
    throws IllegalArgumentException;
  
  /**
   * Obtaining the whitespace character set. The set may contain ranges. The
   * method may return an emtpy string, if no whitespaces are known.
   *
   * @see #setWhitespaces
   * @return the currently active whitespace set
   */
  public String getWhitespaces();
  
  /**
   * Setting the separator set. This set may contain ranges. A range is a
   * character (lower limit) followed by a '-' (minus) followed by a
   * second character (upper limit). A range of "a-z" means: all characters in
   * the UNICODE character set between and including 'a' and 'z'. The character
   * '-' itself should be preceded by an escape character. Ranges should
   * be used whenever possible since they speed up the parsing process.
   *<br>
   * Separators are characters that are significant for the syntax. A sequence
   * of separators is <strong>NOT</strong> equal to one single separator. Thats 
   * the difference to whitespaces.
   *<br>
   * An implementation of this interface should fire a {@link TokenizerPropertyEvent}
   * of type {@link TokenizerPropertyEvent.PROPERTY_ADDED} or 
   * {@link TokenizerPropertyEvent.PROPERTY_MODIFIED} to all registered listeners
   * (see {@link #addTokenizerPropertyListener}).
   *<br>
   * The method accepts an empty string to describe "no separators".
   *
   * @param   separators   the set of separating characters
   * @throws  IllegalArgumentException when <code>null</code> is passed or incomplete
   *          ranges are specified (e.g. <code>"[-"</code>)
   * @see     #getSeparators
   */
  public void setSeparators(String separators) 
    throws IllegalArgumentException;
  
  /**
   * Obtaining the separator set of the <code>TokenizerProperties</code>. The set 
   * may contain ranges or may be empty.
   *
   * @return the currently used set of separating characters
   * @see #setSeparators
   */
  public String getSeparators();
  

  //---------------------------------------------------------------------------
  // string properties
  //
  
  /**
   * Registering a string description. Strings are things like the primitive string 
   * literals in C/C++, SQL varchar literals, but also the character literals 
   * of C/C++ and Java.
   *<br>
   * If the given string starting sequence is already known to the parser,
   * it will simply be re-registered. Using this method on a known string
   * with an associated companion will remove that companion.
   *<br>
   * An implementation of this interface should fire a {@link TokenizerPropertyEvent}
   * of type {@link TokenizerPropertyEvent.PROPERTY_ADDED} to all registered listeners
   * (see {@link #addTokenizerPropertyListener}) with the new string {@link TokenizerProperty}
   * if the string is a new one, or of type {@link TokenizerPropertyEvent.PROPERTY_MODIFIED}
   * if the string is re-registered.
   *<br>
   * Passing <code>null</code> or an empty string for the escape sequence means
   * that no escapes are used in the described string element.
   *
   * @param   start     the starting sequence of a string
   * @param   end       the finishing sequence of a string
   * @param   escape    the escape sequence inside the string
   * @throws  IllegalArgumentException when <code>null</code> or an empty string 
   *          is passed for start or end
   * @throws  UnsupportedOperationException if the method is not available for an
   *          implementation of the <code>TokenizerProperties</code> interface
   * @see     #add(String, String, String, Object)
   * @see     #add(String, String, String, Object, int)
   * @see     #removeString
   */
  public void addString(String start, String end, String escape)
    throws IllegalArgumentException, UnsupportedOperationException;

  /**
   * Registering a the sequences that are used for string-like text parts.
   * This method supports also an information associated with the string,
   * called the companion.
   *<br>
   * If the given string starting sequence is already known to the parser,
   * it will simply be re-registered. Using this method on a known string
   * with an associated companion will replace that companion against the given
   * one.
   *<br>
   * An implementation of this interface should fire a {@link TokenizerPropertyEvent}
   * of type {@link TokenizerPropertyEvent.PROPERTY_ADDED} to all registered listeners
   * (see {@link #addTokenizerPropertyListener}) with the new string {@link TokenizerProperty}
   * if the string is a new one, or of type {@link TokenizerPropertyEvent.PROPERTY_MODIFIED}
   * if the string is re-registered.
   *<br>
   * Passing <code>null</code> or an empty string for the escape sequence means
   * that no escapes are used in the described string element.
   *
   * @param   start     the starting sequence of a string
   * @param   end       the finishing sequence of a string
   * @param   escape    the escape sequence inside the string
   * @param   companion the associated information
   * @throws  IllegalArgumentException when <code>null</code> or an empty string 
   *          is passed for start or end
   * @throws  UnsupportedOperationException if the method is not available for an
   *          implementation of the <code>TokenizerProperties</code> interface
   * @see     #add(String, String, String)
   * @see     #add(String, String, String, Object, int)
   * @see     #removeString
   */
  public void addString(String start, String end, String escape, Object companion)
    throws IllegalArgumentException;
  
  /**
   * Registering a the sequences that are used for string-like text parts.
   * This method supports also an information associated with the string,
   * called the companion.
   *<br>
   * If the given string starting sequence is already known to the parser,
   * it will simply be re-registered. Using this method on a known string
   * with an associated companion will replace that companion against the given
   * one.
   *<br>
   * This version of <code>addString</code> supports a bitmask of the 
   * <code>F_...</code> flags to modify the general tokenizer settings (see
   * {@link #setParseFlags} for this special element.
   *<br>
   * An implementation of this interface should fire a {@link TokenizerPropertyEvent}
   * of type {@link TokenizerPropertyEvent.PROPERTY_ADDED} to all registered listeners
   * (see {@link #addTokenizerPropertyListener}) with the new string {@link TokenizerProperty}
   * if the string is a new one, or of type {@link TokenizerPropertyEvent.PROPERTY_MODIFIED}
   * if the string is re-registered.
   *<br>
   * Passing <code>null</code> or an empty string for the escape sequence means
   * that no escapes are used in the described string element.
   *
   * @param   start     the starting sequence of a string
   * @param   end       the finishing sequence of a string
   * @param   escape    the escape sequence inside the string
   * @param   companion the associated information
   * @param   flags     modification flags 
   * @throws  IllegalArgumentException when <code>null</code> or an empty string 
   *          is passed for start or end
   * @throws  UnsupportedOperationException if the method is not available for an
   *          implementation of the <code>TokenizerProperties</code> interface
   * @see     #add(String, String, String)
   * @see     #add(String, String, String, Object)
   * @see     #removeString
   */
  public void addString(String start, String end, String escape, Object companion, int flags)
    throws IllegalArgumentException, UnsupportedOperationException;
  
  /**
   * Removing a string description. The method does nothing if the string description
   * identified by the given <code>start</code> sequence is not known to this
   * <code>TokenizerProperties</code> instance. The method may throw an 
   * {@link java.lang.IllegalArgumentException} if the given string start is
   * <code>null</code> or empty.
   *<br>
   * An implementation of this interface should fire a {@link TokenizerPropertyEvent}
   * of type {@link TokenizerPropertyEvent.PROPERTY_REMOVED} to all registered 
   * listeners (see {@link #addTokenizerPropertyListener}) if a string is actually 
   * removed.
   *
   * @param   start   the starting sequence of a string
   * @throws  IllegalArgumentException when <code>null</code> or an empty string 
   *          is passed
   */  
  public void removeString(String start)
    throws IllegalArgumentException;
  
  /**
   * Retrieving the information associated with a certain string. Only the 
   * starting sequence is nessecary to identify the string. If the string is not 
   * known to the parser, <code>null</code> will be returned.<br>
   * If one needs to know if a string exists without a companion or if the string
   * is unknown so far, use also the method {@link #stringExists}.
   *
   * @param   start   the starting sequence of a string
   * @return  the associated information or <code>null</code>
   * @throws  IllegalArgumentException when <code>null</code> or an emtpy string
   *          is passed
   */
  public Object getStringCompanion(String start)
    throws IllegalArgumentException, UnsupportedOperationException;
  
  /**
   * Checks if the given starting sequence of the string is known to the parser.
   * The method accepts both empty and <code>null</code> strings for <code>start</code>
   * by returning <code>false</code>.
   *<br>
   * Note that there is no choice of parsing flags (different to the 
   * {@link #addString(String, String, String, Object, int)} method), since it
   * makes generally no sense to "overload" properties. A string might be introduced
   * by the starting case-insensitive sequence <code>STR</code>, but there shouldn't
   * be a case-sensitive start <code>str</code>.
   *
   * @param   start     the starting sequence of a string
   * @return  <code>true</code> if the string is registered, 
   *          <code>false</code> otherwise
   */
  public boolean stringExists(String start);
  
  /**
   * Get the full description of a string property starting with the given 
   * prefix. The method returns <code>null</code> if the passed <code>start</code>
   * parameter cannot be mapped to a known string description ({@link #stringExists}
   * would return <code>false</code>). 
   *
   * @param start     the starting sequence of a string
   * @return the full string description or <code>null</code>
   * @throws IllegalArgumentException when <code>null</code> is passed
   */
  public TokenizerProperty getString(String start) 
    throws IllegalArgumentException;

  /**
   * This method returns an {@link java.util.Iterator} of {@link TokenizerProperty}
   * objects. Each <code>TokenizerProperty</code> object contains the starting,
   * finishing and escaping sequence of a string description and the companion if 
   * it exists.
   *
   * @return enumeration of {@link TokenizerProperty} objects
   */  
  public Iterator getStrings();
  

  //---------------------------------------------------------------------------
  // line and block comments
  //
  
  /**
   * Registering a the starting sequence of a line comment. The line comment is
   * a special type of whitespace. It starts with the given character sequence
   * and contains all characters up to and including the next end-of-line
   * character(s).<br>
   * Although most languages have only one line comment sequence, it is possible
   * to use more than one.<br>
   * If the given line comment starting sequence is already known to the parser,
   * it will simply be re-registered. Using this method on a known line comment
   * with an associated companion will effectively remove the companion.
   *
   * @param   lineComment   the starting sequence of the line comment
   * @throws  IllegalArgumentException when <code>null</code> or an empty string
   *          is passed
   * @throws  UnsupportedOperationException if the method is not available for an
   *          implementation of the <code>TokenizerProperties</code> interface
   * @see     #addLineComment(String, Object)
   * @see     #addLineComment(String, Object, int)
   * @see     #removeLineComment
   */
  public void addLineComment(String lineComment) 
    throws IllegalArgumentException, UnsupportedOperationException;

  /**
   * Registering a the starting sequence of a line comment. The line comment is
   * a special type of whitespace. It starts with the given character sequence
   * and contains all characters up to and including the next end-of-line
   * character(s).<br>
   * Although most languages have only one line comment sequence, it is possible
   * to use more than one.<br>
   * This method supports also an information associated with the line comment,
   * called the companion.<br>
   * If the given line comment starting sequence is already known to the parser,
   * it will simply be re-registered. Using this method on a known line comment
   * with an associated companion will replace that companion against the given
   * one.
   *<br>
   * An implementation of this interface should fire a {@link TokenizerPropertyEvent}
   * of type {@link TokenizerPropertyEvent.PROPERTY_ADDED} to all registered listeners
   * (see {@link #addTokenizerPropertyListener}) with the new line comment
   * {@link TokenizerProperty} if the line comment is a new one, or of type 
   * {@link TokenizerPropertyEvent.PROPERTY_MODIFIED} if the line comment is 
   * re-registered.
   *
   * @param   lineComment   the starting sequence of a line comment
   * @param   companion     the associated information
   * @throws  IllegalArgumentException when <code>null</code> or an empty string
   *          is passed for lineComment
   * @throws  UnsupportedOperationException if the method is not available for an
   *          implementation of the <code>TokenizerProperties</code> interface
   * @see     #addLineComment(String)
   * @see     #addLineComment(String, Object, int)
   * @see     #removeLineComment
   */
  public void addLineComment(String lineComment, Object companion) 
    throws IllegalArgumentException, UnsupportedOperationException;

  /**
   * Registering a the starting sequence of a line comment. The line comment is
   * a special type of whitespace. It starts with the given character sequence
   * and contains all characters up to and including the next end-of-line
   * character(s).
   *<br>
   * Although most languages have only one line comment sequence, it is possible
   * to use more than one.
   *<br>
   * This method supports also an information associated with the line comment,
   * called the companion.
   *<br>
   * If the given line comment starting sequence is already known to the parser,
   * it will simply be re-registered. Using this method on a known line comment
   * with an associated companion will replace that companion against the given
   * one.
   *<br>
   * This version of <code>addLineComment</code> supports a bitmask of the 
   * <code>F_...</code> flags to modify the general tokenizer settings (see
   * {@link #setParseFlags}) for this special element.
   *<br>
   * An implementation of this interface should fire a {@link TokenizerPropertyEvent}
   * of type {@link TokenizerPropertyEvent.PROPERTY_ADDED} to all registered listeners
   * (see {@link #addTokenizerPropertyListener}) with the new line comment
   * {@link TokenizerProperty} if the line comment is a new one, or of type 
   * {@link TokenizerPropertyEvent.PROPERTY_MODIFIED} if the line comment is 
   * re-registered.
   *
   * @param   lineComment   the starting sequence of a line comment
   * @param   companion     the associated information
   * @param   flags         modification flags 
   * @throws  IllegalArgumentException when <code>null</code> or an empty string
   *          is passed for lineComment
   * @throws  UnsupportedOperationException if the method is not available for an
   *          implementation of the <code>TokenizerProperties</code> interface
   * @see     #addLineComment(String)
   * @see     #addLineComment(String, Object)
   * @see     #removeLineComment
   */
  public void addLineComment(String lineComment, Object companion, int flags)
   throws IllegalArgumentException, UnsupportedOperationException;
  
  /**
   * Removing a certain line comment. If the given comment is not known to this
   * <code>TokenizerProperties</code> instance, the method does nothing.
   *<br>
   * An implementation of this interface should fire a {@link TokenizerPropertyEvent}
   * of type {@link TokenizerPropertyEvent.PROPERTY_REMOVED} to all registered 
   * listeners if a line comment property is actually removed.
   *
   * @param   lineComment   the starting sequence of the line comment
   * @throws  IllegalArgumentException when <code>null</code> or an empty string
   *          is passed
   * @see     #addLineComment(String)
   * @see     #addLineComment(String, Object)
   * @see     #addLineComment(String, Object, int)
   */  
  public void removeLineComment(String lineComment) 
    throws IllegalArgumentException;
  
  /**
   * Retrieving the associated object of a certain line comment. If the given
   * starting sequence of a line comment is not known to the parser, then the
   * method returns <code>null</code>.<br>
   * To distinguish between an unknown line comment and companion-less line
   * comment, use the method {@link #lineCommentExists}.
   *
   * @param   lineComment   the starting sequence of the line comment
   * @return  the object    associated with the line comment
   * @throws  IllegalArgumentException when <code>null</code> or an empty string
   *          is passed for lineComment
   * @see     #lineCommentExists
   */  
  public Object getLineCommentCompanion(String lineComment) 
    throws IllegalArgumentException;

  /**
   * Checks if the give line comment is known. The method accepts both empty and 
   * <code>null</code> strings for <code>lineComment</code> by returning 
   * <code>false</code>.
   *
   * @param   lineComment   the starting sequence of the line comment
   * @return  <code>true</code> if the line comment is known, 
   *          <code>false</code> otherwise
   */  
  public boolean lineCommentExists(String lineComment);
  
  /**
   * Get the full description of a line comment property starting with the given 
   * prefix. The method returns <code>null</code> if the passed <code>lineComment</code>
   * parameter cannot be mapped to a known line comment description ({@link #lineCommentExists}
   * would return <code>false</code>). 
   *
   * @param   lineComment   the starting sequence of the line comment
   * @return  the full line comment description or <code>null</code>
   * @throws  IllegalArgumentException when <code>null</code> or an empty string
   *          is passed
   * @see     #lineCommentExists
   */
  public TokenizerProperty getLineComment(String lineComment) 
    throws IllegalArgumentException;

  /**
   * This method returns an {@link java.util.Iterator} of {@link TokenizerProperty}
   * objects. Each <code>TokenizerProperty</code> object contains one starting
   * sequence of a line comment and its companion if it exists.
   *
   * @return enumeration of {@link TokenizerProperty} objects
   */  
  public Iterator getLineComments();
  
  /**
   * Registering a block comment with the parser. This version takes only the starting
   * and finishing sequence of the block comment.<br>
   * If the given starting sequence is already known to the parser, the block 
   * comment is simply re-registered. Using this method on a known block comment
   * with an associated companion will remove that companion.
   *<br>
   * An implementation of this interface should fire a {@link TokenizerPropertyEvent}
   * of type {@link TokenizerPropertyEvent.PROPERTY_ADDED} to all registered listeners
   * (see {@link #addTokenizerPropertyListener}) with the new block comment
   * {@link TokenizerProperty} if the comment is a new one, or of type 
   * {@link TokenizerPropertyEvent.PROPERTY_MODIFIED} if the comment is 
   * re-registered.
   *
   * @param   start the starting sequence of the block comment
   * @param   end   the finishing sequence of the block comment
   * @throws  IllegalArgumentException when <code>null</code> or an empty string
   *          is passed for start or end
   * @throws  UnsupportedOperationException if the method is not available for an
   *          implementation of the <code>TokenizerProperties</code> interface
   * @see     #addBlockComment(String, String, Object)
   * @see     #addBlockComment(String, String, Object, int)
   * @see     #removeBlockComment
   */  
  public void addBlockComment(String start, String end)
    throws IllegalArgumentException, UnsupportedOperationException;
  
  /**
   * Registering a block comment with the parser. Beside the obviously nessecary
   * starting and finishing sequence of the block comment, it takes an object that
   * is associated with the block comment, called the companion.<br>
   * If the given starting sequence is already known to the parser, the block
   * comment is simply re-registered. Using this method on a known block comment
   * with an associated companion will replace that companion against the given
   * one.
   *<br>
   * An implementation of this interface should fire a {@link TokenizerPropertyEvent}
   * of type {@link TokenizerPropertyEvent.PROPERTY_ADDED} to all registered listeners
   * (see {@link #addTokenizerPropertyListener}) with the new block comment
   * {@link TokenizerProperty} if the comment is a new one, or of type 
   * {@link TokenizerPropertyEvent.PROPERTY_MODIFIED} if the comment is 
   * re-registered.
   *
   * @param   start     the starting sequence of the block comment
   * @param   end       the finishing sequence of the block comment
   * @param   companion information object associated with this block comment
   * @throws  IllegalArgumentException when <code>null</code> or an empty string
   *          is passed for start or end
   * @throws  UnsupportedOperationException if the method is not available for an
   *          implementation of the <code>TokenizerProperties</code> interface
   * @see     #addBlockComment(String, String)
   * @see     #addBlockComment(String, String, Object, int)
   * @see     #removeBlockComment
   */  
  public void addBlockComment(String start, String end, Object companion)
    throws IllegalArgumentException, UnsupportedOperationException;
  
  /**
   * Registering a block comment with the parser. Beside the obviously nessecary
   * starting and finishing sequence of the block comment, it takes an object that
   * is associated with the block comment, called the companion.<br>
   * If the given starting sequence is already known to the parser, the block
   * comment is simply re-registered. Using this method on a known block comment
   * with an associated companion will replace that companion against the given
   * one.
   *<br>
   * This version of <code>addBlockComment</code> supports a bitmask of the 
   * <code>F_...</code> flags to modify the general tokenizer settings (see
   * {@link #setParseFlags}) for this special element.
   *<br>
   * An implementation of this interface should fire a {@link TokenizerPropertyEvent}
   * of type {@link TokenizerPropertyEvent.PROPERTY_ADDED} to all registered listeners
   * (see {@link #addTokenizerPropertyListener}) with the new block comment
   * {@link TokenizerProperty} if the comment is a new one, or of type 
   * {@link TokenizerPropertyEvent.PROPERTY_MODIFIED} if the comment is 
   * re-registered.
   *
   * @param   start     the starting sequence of the block comment
   * @param   end       the finishing sequence of the block comment
   * @param   companion information object associated with this block comment
   * @param   flags     modification flags 
   * @throws  IllegalArgumentException when <code>null</code> or an empty string
   *          is passed for start or end
   * @throws  UnsupportedOperationException if the method is not available for an
   *          implementation of the <code>TokenizerProperties</code> interface
   * @see     #addBlockComment(String, String)
   * @see     #addBlockComment(String, String, Object)
   * @see     #removeBlockComment
   */
  public void addBlockComment(String start, String end, Object companion, int flags)
    throws IllegalArgumentException, UnsupportedOperationException;
  
  /**
   * Removing a certain block comment. Only the starting sequence is nessecary
   * to identify the block comment.
   *<br>
   * An implementation of this interface should fire a {@link TokenizerPropertyEvent}
   * of type {@link TokenizerPropertyEvent.PROPERTY_REMOVED} to all registered 
   * listeners if a block comment property is actually removed.
   *
   * @param   start   the starting sequence of the block comment
   * @throws  IllegalArgumentException when <code>null</code> or an empty string
   *          is passed
   */  
  public void removeBlockComment(String start)
    throws IllegalArgumentException;
  
  /**
   * Retrieving a certain block comment. Only the starting sequence is nessecary
   * to identify the block comment. If the block comment is not known to the 
   * parser, then <code>null</code> is returned.<br>
   * To distinguish between an unknown line comment and companion-less line 
   * comment, use the method {@link #lineCommentExists}.
   *
   * @param   start   the starting sequence of the block comment
   * @return  the associated object of the block comment
   * @throws  IllegalArgumentException when <code>null</code> or an empty string
   *          is passed
   */  
  public Object getBlockCommentCompanion(String start)
    throws IllegalArgumentException;
  
  /**
   * Checks if the give block comment is known. Only the starting sequence is 
   * nessecary to identify the block comment.
   * The method accepts both empty and <code>null</code> strings for <code>start</code>
   * by returning <code>false</code>.
   *
   * @param   start   the starting sequence of the block comment
   * @return  <code>true</code> if the block comment is known, 
   *          <code>false</code> otherwise
   */  
  public boolean blockCommentExists(String start);
  
  /**
   * Get the full description of a block comment property starting with the given 
   * prefix. The method returns <code>null</code> if the passed <code>start</code>
   * parameter cannot be mapped to a known block comment description ({@link #blockCommentExists}
   * would return <code>false</code>). 
   *
   * @param   start   the starting sequence of the block comment
   * @return  the full block comment description or <code>null</code>
   * @throws  IllegalArgumentException when <code>null</code> or an empty string
   *          is passed
   */
  public TokenizerProperty getBlockComment(String start)
    throws IllegalArgumentException;

  /**
   * This method returns an {@link java.util.Iterator} of {@link TokenizerProperty}
   * objects. Each <code>TokenizerProperty</code> object contains the starting and
   * finishing sequence of a block comment and the companion if it exists.
   *
   * @return enumeration of {@link TokenizerProperty} objects
   */  
  public Iterator getBlockComments();
  

  //---------------------------------------------------------------------------
  // special sequences
  //
  
  /**
   * Registering a special sequence of characters. Such sequences may be multicharacter
   * operators like the shift operators in Java.
   *<br>
   * Unlike keywords, special sequences act also as separators between other tokens.
   * If one special sequence is the prefix of other special sequences (in Java the
   * shift operator <code>&gt;&gt;</code> is the prefix of the shift operator
   * <code>&gt;&gt;&gt;</code>), always the longest possible match is returned.
   * Testing on special sequences takes place after whitespaces and comments are ruled
   * out, but before ordinary separators are tested.
   *<br>
   * An implementation of this interface should fire a {@link TokenizerPropertyEvent}
   * of type {@link TokenizerPropertyEvent.PROPERTY_ADDED} to all registered listeners
   * (see {@link #addTokenizerPropertyListener}) with the new special sequence
   * {@link TokenizerProperty} if the sequence is a new one, or of type 
   * {@link TokenizerPropertyEvent.PROPERTY_MODIFIED} if the sequence is 
   * re-registered.
   *
   * @param   specSeq   special sequence to register
   * @throws  IllegalArgumentException when <code>null</code> or an empty string
   *          is passed
   * @throws  UnsupportedOperationException if the method is not available for an
   *          implementation of the <code>TokenizerProperties</code> interface
   * @see     #addSpecialSequence(String, Object)
   * @see     #addSpecialSequence(String, Object, int)
   * @see     #removeSpecialSequence
   */
  public void addSpecialSequence(String specSeq)
    throws IllegalArgumentException, UnsupportedOperationException;
  
  /**
   * Registering a special sequence of characters. Such sequences may be multicharacter
   * operators like the shift operators in Java.
   *<br>
   * Unlike keywords, special sequences act also as separators between other tokens.
   * If one special sequence is the prefix of other special sequences (in Java the
   * shift operator <code>&gt;&gt;</code> is the prefix of the shift operator
   * <code>&gt;&gt;&gt;</code>), always the longest possible match is returned.
   * Testing on special sequences takes place after whitespaces and comments are ruled
   * out, but before ordinary separators are tested.
   * This form of <code>addSpecialSequence</code> also takes an object associated with
   * the special sequence, called the companion.
   *<br>
   * An implementation of this interface should fire a {@link TokenizerPropertyEvent}
   * of type {@link TokenizerPropertyEvent.PROPERTY_ADDED} to all registered listeners
   * (see {@link #addTokenizerPropertyListener}) with the new special sequence
   * {@link TokenizerProperty} if the sequence is a new one, or of type 
   * {@link TokenizerPropertyEvent.PROPERTY_MODIFIED} if the sequence is 
   * re-registered.
   *
   * @param   specSeq     special sequence to register
   * @param   companion   information object associated with this special sequence
   * @throws  IllegalArgumentException when <code>null</code> or an empty string
   *          is passed for specSeq
   * @throws  UnsupportedOperationException if the method is not available for an
   *          implementation of the <code>TokenizerProperties</code> interface
   * @see     #addSpecialSequence(String)
   * @see     #addSpecialSequence(String, Object, int)
   * @see     #removeSpecialSequence
   */  
  public void addSpecialSequence(String specSeq, Object companion)
    throws IllegalArgumentException, UnsupportedOperationException;
  
  /**
   * Registering a special sequence of characters. Such sequences may be multicharacter
   * operators like the shift operators in Java.
   *<br>
   * Unlike keywords, special sequences act also as separators between other tokens.
   * If one special sequence is the prefix of other special sequences (in Java the
   * shift operator <code>&gt;&gt;</code> is the prefix of the shift operator
   * <code>&gt;&gt;&gt;</code>), always the longest possible match is returned.
   * Testing on special sequences takes place after whitespaces and comments are ruled
   * out, but before ordinary separators are tested.
   * This form of <code>addSpecialSequence</code> also takes an object associated with
   * the special sequence, called the companion.
   *<br>
   * This version of <code>addSpecialSequence</code> supports a bitmask of the 
   * <code>F_...</code> flags to modify the general tokenizer settings (see
   * {@link #setParseFlags}) for this special element.
   *<br>
   * An implementation of this interface should fire a {@link TokenizerPropertyEvent}
   * of type {@link TokenizerPropertyEvent.PROPERTY_ADDED} to all registered listeners
   * (see {@link #addTokenizerPropertyListener}) with the new special sequence
   * {@link TokenizerProperty} if the sequence is a new one, or of type 
   * {@link TokenizerPropertyEvent.PROPERTY_MODIFIED} if the sequence is 
   * re-registered.
   *
   * @param   specSeq     special sequence to register
   * @param   companion   information object associated with this special sequence
   * @param   flags       modification flags 
   * @throws  IllegalArgumentException when <code>null</code> or an empty string
   *          is passed for specSeq
   * @throws  UnsupportedOperationException if the method is not available for an
   *          implementation of the <code>TokenizerProperties</code> interface
   * @see     #addSpecialSequence(String)
   * @see     #addSpecialSequence(String, Object)
   * @see     #removeSpecialSequence
   */
  public void addSpecialSequence(String specSeq, Object companion, int flags)
    throws IllegalArgumentException, UnsupportedOperationException;
  
  
  /**
   * Removing a special sequence property from the store. If the special sequence
   * is not known, the method does nothing.
   *<br>
   * An implementation of this interface should fire a {@link TokenizerPropertyEvent}
   * of type {@link TokenizerPropertyEvent.PROPERTY_REMOVED} to all registered 
   * listeners if a special sequence property is actually removed.
   *
   * @param   specSeq   sequence to remove
   * @throws  IllegalArgumentException when <code>null</code> or an empty string
   *          is passed
   * @see     #addSpecialSequence(String)
   * @see     #addSpecialSequence(String, Object)
   * @see     #addSpecialSequence(String, Object, int)
   */  
  public void removeSpecialSequence(String specSeq)
    throws IllegalArgumentException;
  
  /**
   * Retrieving the companion of the given special sequence. If the special
   * sequence doesn't exist the method returns <code>null</code>.
   *
   * @param   specSeq   sequence to remove
   * @return  the object associated with the special sequence
   * @throws  IllegalArgumentException when <code>null</code> or an empty string
   *          is passed
   */
  public Object getSpecialSequenceCompanion(String specSeq)
    throws IllegalArgumentException;

  /**
   * This method returns an {@link java.util.Iterator} of {@link TokenizerProperty}
   * objects. Each <code>TokenizerProperty</code> object contains a special
   * sequence and the companion if it exists.
   *
   * @return enumeration of {@link TokenizerProperty} objects
   */  
  public Iterator getSpecialSequences();
  
  /**
   * Checks if the given special sequence is known to the <code>TokenizerProperties</code>.
   * The method accepts both empty and <code>null</code> strings for <code>specSeq</code>
   * by returning <code>false</code>.
   *
   * @param   specSeq   sequence to check
   * @return  <code>true</code> if the block comment is known,
   *          <code>false</code> otherwise
   */  
  public boolean specialSequenceExists(String specSeq);
  
  /**
   * Get the full description of a special sequence property. The method returns 
   * <code>null</code> if the passed <code>specSeq</code> image is unknown
   * ({@link #specialSequenceExists} would return <code>false</code>). 
   *
   * @param   specSeq sequence to find
   * @return  the full sequence description or <code>null</code>
   * @throws  IllegalArgumentException when <code>null</code> or an empty string
   *          is passed
   */
  public TokenizerProperty getSpecialSequence(String specSeq)
    throws IllegalArgumentException;


  //---------------------------------------------------------------------------
  // keyword properties
  //
  
  /**
   * Registering a keyword. If the keyword is already known to the <code>TokenizerProperties</code>
   * then it is simply re-registered. If the known keyword has an associated 
   * companion it will be removed.
   *<br>
   * An implementation of this interface should fire a {@link TokenizerPropertyEvent}
   * of type {@link TokenizerPropertyEvent.PROPERTY_ADDED} to all registered listeners
   * (see {@link #addTokenizerPropertyListener}) with the new keyword
   * {@link TokenizerProperty} if the keyword is a new one, or of type 
   * {@link TokenizerPropertyEvent.PROPERTY_MODIFIED} if the keyword is 
   * re-registered.
   *
   * @param   keyword   keyword to register
   * @throws  IllegalArgumentException when <code>null</code> or an empty string
   *          is passed
   * @throws  UnsupportedOperationException if the method is not available for an
   *          implementation of the <code>TokenizerProperties</code> interface
   * @see #addKeyword(String, Object)
   * @see #addKeyword(String, Object, int)
   * @see #removeKeyword
   */
  public void addKeyword(String keyword)
    throws IllegalArgumentException, UnsupportedOperationException;
  
  /**
   * Registering a keyword. If the keyword is already known to the <code>TokenizerProperties</code>
   * then it is simply re-registered. If the known keyword has an associated
   * companion it will be replaced against the given one.
   *<br>
   * An implementation of this interface should fire a {@link TokenizerPropertyEvent}
   * of type {@link TokenizerPropertyEvent.PROPERTY_ADDED} to all registered listeners
   * (see {@link #addTokenizerPropertyListener}) with the new keyword
   * {@link TokenizerProperty} if the keyword is a new one, or of type 
   * {@link TokenizerPropertyEvent.PROPERTY_MODIFIED} if the keyword is 
   * re-registered.
   *
   * @param   keyword   keyword to register
   * @param   companion information object associated with this keyword
   * @throws  IllegalArgumentException when <code>null</code> or an empty string
   *          is passed for <code>keyword</code>
   * @throws  UnsupportedOperationException if the method is not available for an
   *          implementation of the <code>TokenizerProperties</code> interface
   * @see #addKeyword(String)
   * @see #addKeyword(String, Object, int)
   * @see #removeKeyword
   */  
  public void addKeyword(String keyword, Object companion)
    throws IllegalArgumentException, UnsupportedOperationException;
  
  /**
   * Registering a keyword. If the keyword is already known to the <code>TokenizerProperties</code>
   * then it is simply re-registered. If the known keyword has an associated
   * companion it will be replaced against the given one.
   *<br>
   * This version of <code>addKeyword</code> supports a bitmask of the 
   * <code>F_...</code> flags to modify the general tokenizer settings (see
   * {@link #setParseFlags}) for this special element.
   *<br>
   * An implementation of this interface should fire a {@link TokenizerPropertyEvent}
   * of type {@link TokenizerPropertyEvent.PROPERTY_ADDED} to all registered listeners
   * (see {@link #addTokenizerPropertyListener}) with the new keyword
   * {@link TokenizerProperty} if the keyword is a new one, or of type 
   * {@link TokenizerPropertyEvent.PROPERTY_MODIFIED} if the keyword is 
   * re-registered.
   *
   * @param   keyword     keyword to register
   * @param   companion   information object associated with this keyword
   * @param   flags       modification flags 
   * @throws  IllegalArgumentException when <code>null</code> or an empty string
   *          is passed for keyword
   * @throws  UnsupportedOperationException if the method is not available for an
   *          implementation of the <code>TokenizerProperties</code> interface
   * @see #addKeyword(String)
   * @see #addKeyword(String, Object)
   * @see #removeKeyword
   */  
  public void addKeyword(String keyword, Object companion, int flags)
    throws IllegalArgumentException, UnsupportedOperationException;
  
  /**
   * Removing a keyword property from the store. If the keyword is not known
   * then the method does nothing.
   *<br>
   * An implementation of this interface should fire a {@link TokenizerPropertyEvent}
   * of type {@link TokenizerPropertyEvent.PROPERTY_REMOVED} to all registered 
   * listeners if a keyword property is actually removed.
   *
   * @param   keyword   keyword to remove
   * @throws  IllegalArgumentException when <code>null</code> or an empty string
   *          is passed
   * @see #addKeyword(String)
   * @see #addKeyword(String, Object)
   * @see #addKeyword(String, Object, int)
   */  
  public void removeKeyword(String keyword)
    throws IllegalArgumentException;
  
  /**
   * Retrieving the companion of the given special sequence. If the special
   * sequence doesn't exist the method returns <code>null</code>.
   *
   * @param   keyword   keyword thats companion is sought
   * @return  the object associated with the keyword
   * @throws  IllegalArgumentException when <code>null</code> or an empty string
   *          is passed
   */
  public Object getKeywordCompanion(String keyword)
    throws IllegalArgumentException;

  /**
   * This method returns an {@link java.util.Iterator} of {@link TokenizerProperty}
   * objects. Each <code>TokenizerProperty</code> object contains a keyword and 
   * the companion if it exists.
   *
   * @return enumeration of {@link TokenizerProperty} objects
   */  
  public Iterator getKeywords();
  
  /**
   * Checks if the given keyword is known to the <code>TokenizerProperties</code>.
   * The method accepts both empty and <code>null</code> strings for <code>keyword</code>
   * by returning <code>false</code>.
   *
   * @param keyword   keyword to search
   * @return <code>true</code> if the keyword is known,
   *        <code>false</code> otherwise
   */  
  public boolean keywordExists(String keyword);

  /**
   * Get the full description of a keyword property. The method returns 
   * <code>null</code> if the passed <code>keyword</code> image is unknown
   * ({@link #keywordExists} would return <code>false</code>). 
   *
   * @param   keyword   keyword to search
   * @return  the full sequence description or <code>null</code>
   * @throws  IllegalArgumentException when <code>null</code> or an empty string
   *          is passed
   */
  public TokenizerProperty getKeyword(String keyword)
    throws IllegalArgumentException;


  //---------------------------------------------------------------------------
  // pattern properties
  //
  
  /**
   * Registering a pattern. Pattern can describe identifiers, numbers etc. They
   * provide a way to deal with token that cannot be enumerated like keywords.
   *<br>
   * A pattern is usually a regular expression that is used by {@link java.util.regexp.Pattern}. 
   * But implementations of {@link de.susebox.jtopas.spi.PatternHandler} may use
   * other pattern syntaxes, for example the simpler syntax used for file path 
   * matching.  
   *<br>
   * Pattern are applied to input data in the order of their registration. The 
   * first matching pattern stops the iteration.
   *<br>
   * Pattern matching is a rather complex operation that may have a significant
   * impact on the speed of a {@link Tokenizer}. On the other hand, pattern may
   * be used instead of string or comment descriptions (see {@link #addString} etc.)
   * or if a pattern matching would be performed after the return of a token of
   * type {@link Token#NORMAL}.
   *<br>
   * If the given pattern is already known to the parser, it will simply be 
   * re-registered. Using this method on a known pattern with an associated companion 
   * will remove that companion.
   *<br>
   * An implementation of this interface should fire a {@link TokenizerPropertyEvent}
   * of type {@link TokenizerPropertyEvent.PROPERTY_ADDED} to all registered listeners
   * (see {@link #addTokenizerPropertyListener}) with the new pattern {@link TokenizerProperty}
   * if the pattern is a new one, or of type {@link TokenizerPropertyEvent.PROPERTY_MODIFIED}
   * if the pattern is re-registered.
   *
   * @param   pattern   the regular expression to be added
   * @throws  IllegalArgumentException when <code>null</code> or an empty pattern
   *          is passed
   * @throws  UnsupportedOperationException if the method is not available for an
   *          implementation of the <code>TokenizerProperties</code> interface
   * @see     #removePattern
   * @see     #addPattern(String, Object)
   * @see     #addPattern(String, Object, int)
   */
  public void addPattern(String pattern) 
    throws IllegalArgumentException, UnsupportedOperationException;

  /**
   * Registering a pattern with an associated object. See the description of the
   * {@link #addPattern(String)} for details on pattern.
   *<br>
   * If the given pattern is already known to the parser, it will simply be 
   * re-registered. The associated companion will be replaced against the new one.
   *<br>
   * An implementation of this interface should fire a {@link TokenizerPropertyEvent}
   * of type {@link TokenizerPropertyEvent.PROPERTY_ADDED} to all registered listeners
   * (see {@link #addTokenizerPropertyListener}) with the new pattern {@link TokenizerProperty}
   * if the pattern is a new one, or of type {@link TokenizerPropertyEvent.PROPERTY_MODIFIED}
   * if the pattern is re-registered.
   *
   * @param   pattern     the regular expression to be added
   * @param   companion   information object associated with this pattern
   * @throws  IllegalArgumentException when <code>null</code> or an empty pattern
   *          is passed
   * @throws  UnsupportedOperationException if the method is not available for an
   *          implementation of the <code>TokenizerProperties</code> interface
   * @see     #removePattern
   * @see     #addPattern(String)
   * @see     #addPattern(String, Object, int)
   */
  public void addPattern(String pattern, Object companion)
    throws IllegalArgumentException, UnsupportedOperationException;
  
  /**
   * Registering a pattern with an associated object. See the description of the
   * {@link #addPattern(String)} for details on pattern.
   *<br>
   * If the given pattern is already known to the parser, it will simply be 
   * re-registered. The associated companion will be replaced against the new one.
   *<br>
   * This version of <code>addPattern</code> supports a bitmask of the 
   * <code>F_...</code> flags to modify the general tokenizer settings (see
   * {@link #setParseFlags}) for this special pattern. 
   *<br>
   * An implementation of this interface should fire a {@link TokenizerPropertyEvent}
   * of type {@link TokenizerPropertyEvent.PROPERTY_ADDED} to all registered listeners
   * (see {@link #addTokenizerPropertyListener}) with the new pattern {@link TokenizerProperty}
   * if the pattern is a new one, or of type {@link TokenizerPropertyEvent.PROPERTY_MODIFIED}
   * if the pattern is re-registered.
   *
   * @param   pattern     the regular expression to be added
   * @param   companion   information object associated with this keyword
   * @param   flags       modification flags 
   * @throws  IllegalArgumentException when <code>null</code> or an empty pattern
   *          is passed
   * @throws  UnsupportedOperationException if the method is not available for an
   *          implementation of the <code>TokenizerProperties</code> interface
   * @see     #removePattern
   * @see     #addPattern(String)
   * @see     #addPattern(String, Object)
   */
  public void addPattern(String pattern, Object companion, int flags)
    throws IllegalArgumentException, UnsupportedOperationException;
  
  /**
   * Removing a pattern. The method does nothing if the given pattern is not known 
   * to this <code>TokenizerProperties</code> instance. The method may throw an 
   * {@link java.lang.IllegalArgumentException} if the given pattern is
   * <code>null</code> or empty.
   *<br>
   * An implementation of this interface should fire a {@link TokenizerPropertyEvent}
   * of type {@link TokenizerPropertyEvent.PROPERTY_REMOVED} to all registered 
   * listeners (see {@link #addTokenizerPropertyListener}) if a pattern is actually 
   * removed.
   *
   * @param   pattern     the regular expression to be removed
   * @throws  IllegalArgumentException when <code>null</code> or an empty string 
   *          is passed
   */  
  public void removePattern(String pattern)
    throws IllegalArgumentException;
  
  /**
   * Retrieving the information associated with a given pattern. If the pattern 
   * is not known to the parser, <code>null</code> will be returned.
   *<br>
   * If You need to know if a pattern is known to this <code>TokenizerProperties</code>
   * instance with or without a companion, use the method {@link #patternExists}
   * instead.
   *
   * @param   pattern     the regular expression to be removed
   * @return  the associated information or <code>null</code>
   * @throws  IllegalArgumentException when <code>null</code> or an emtpy pattern
   *          is passed
   */
  public Object getPatternCompanion(String pattern)
    throws IllegalArgumentException;
  
  /**
   * Checks if the given pattern is known to the parser. The method accepts both 
   * empty and <code>null</code> strings for <code>pattern</code> by returning 
   * <code>false</code>.
   *<br>
   * Note that there is no choice of parsing flags (different to the 
   * {@link #addString(String, String, String, Object, int)} method), since it
   * makes generally no sense to "overload" properties.
   *
   * @param   pattern     the regular expression to be looked for
   * @return  <code>true</code> if the pattern is registered, 
   *          <code>false</code> otherwise
   */
  public boolean patternExists(String pattern);
  
  /**
   * Get the full description of a pattern property. The method returns <code>null</code> 
   * if the passed <code>pattern</code> parameter cannot be mapped to a known 
   * pattern description ({@link #patternExists} would return <code>false</code>). 
   *
   * @param   pattern   the regular expression to be looked for
   * @return  the full pattern description or <code>null</code>
   * @throws  IllegalArgumentException when <code>null</code> or an emtpy pattern 
   *          is passed
   */
  public TokenizerProperty getPattern(String pattern) 
    throws IllegalArgumentException;

  /**
   * This method returns an {@link java.util.Iterator} of {@link TokenizerProperty}
   * objects. Each <code>TokenizerProperty</code> object contains a pattern and 
   * its companion if such an associated object exists.
   *
   * @return enumeration of {@link TokenizerProperty} objects
   */  
  public Iterator getPatterns();
  

  //---------------------------------------------------------------------------
  // generic property methods
  //
  
  /**
   * Registering a {@link TokenizerProperty}. This can be a keyword, comment etc.,
   * even whitespaces, separators and property types not defined in {@link Token}.
   *<br>
   * If the property is already known to this <code>TokenizerProperties</code>
   * instance then it is simply re-registered.
   *<br>
   * An implementation of this interface should fire a {@link TokenizerPropertyEvent}
   * of type {@link TokenizerPropertyEvent.PROPERTY_ADDED} or 
   * {@link TokenizerPropertyEvent.PROPERTY_MODIFIED} to all registered listeners
   * (see {@link #addTokenizerPropertyListener}).
   *<br>
   * Note that the given property must always set the relevant parse flags
   * (for instance {@link #F_NO_CASE}) since they take precedence over the
   * flags set with {@link #setParseFlags}.
   *<br>
   * An implementation of this interface may or may not support adding of
   * whitespaces and separators.
   *
   * @param   property   property to register
   * @throws  IllegalArgumentException when <code>null</code>, an incomplete or 
   *          otherwise unusable property is passed
   * @throws  UnsupportedOperationException if the method is not available for an
   *          implementation of the <code>TokenizerProperties</code> interface
   */
  public void addProperty(TokenizerProperty property)
    throws IllegalArgumentException, UnsupportedOperationException;
  
  /**
   * Deregistering a {@link TokenizerProperty} from the store. If the property is
   * not known the method does nothing.
   *
   * @param   property    property to register
   * @throws  IllegalArgumentException when <code>null</code>, an incomplete or 
   *          otherwise unusable property is passed
   */  
  public void removeProperty(TokenizerProperty property)
    throws IllegalArgumentException;
  
  /**
   * This method returns an {@link java.util.Iterator} of {@link TokenizerProperty}
   * objects.
   *
   * @return enumeration of {@link TokenizerProperty} objects
   */  
  public Iterator getProperties();
  
  /**
   * Checks if the given {@link TokenizerProperty} is known to this <code>TokenizerProperties</code>
   * instance. The method compares the characteristics given in <code>property</code>
   * against all known properties.
   *<br>
   * The method accepts <code>null</code> for <code>property</code> by returning 
   * <code>false</code>.
   *
   * @param   property  the property to search
   * @return <code>true</code> if the property is known,
   *        <code>false</code> otherwise
   */  
  public boolean propertyExists(TokenizerProperty property);

  
  //---------------------------------------------------------------------------
  // property change event handling
  //
  
  /**
   * Registering a new {@link TokenizerPropertyListener}. An implementation of
   * the <code>TokenizerProperties</code> interface should call the approbriate
   * methods in the <code>TokenizerPropertyListener</code> interface for all
   * registered listeners whenever a {@link TokenizerProperty} is added, removed
   * or modified. 
   *<br>
   * Adding is done by one of the <code>add...</code> calls like {@link #addKeyword} 
   * or {@link #addString}.
   *<br>
   * Modifications are re-registering of keywords, comments etc. regardless if 
   * companions or parse flags actually changed. Also, {@link #setWhitespaces} 
   * and {@link #setSeparators} are modifications.
   *<br>
   * Removals are performed by the <code>remove...</code> calls, for instance
   * {@link #removeKeyword}.
   *<br>
   * Similar to the policy of event listener registration in the JDK, passing 
   * <code>null</code> does nothing.
   *
   * @param listener  the new {@link TokenizerPropertyListener}
   * @see #removeTokenizerPropertyListener
   */
  public void addTokenizerPropertyListener(TokenizerPropertyListener listener);
  
  /**
   * Removing a listener from the list of registered {@link TokenizerPropertyListener}
   * instances. If the given listener is <code>null</code> or unknown, nothing
   * is done.
   *
   * @param listener  the {@link TokenizerPropertyListener} to deregister
   * @see #addTokenizerPropertyListener
   */
  public void removeTokenizerPropertyListener(TokenizerPropertyListener listener);
}
