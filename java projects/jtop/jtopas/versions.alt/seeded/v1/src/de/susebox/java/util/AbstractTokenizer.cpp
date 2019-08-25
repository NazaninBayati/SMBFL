#include "../../FaultSeeds.h" 

package de.susebox.java.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import de.susebox.java.lang.ExtRuntimeException;
import de.susebox.java.lang.ExtIndexOutOfBoundsException;

public abstract class AbstractTokenizer implements Tokenizer
{
  protected abstract int read(char[] cbuf, int offset, int maxChars) throws Exception;
  
  protected void reset() {
    synchronized(this) {
      _currentReadPos   = 0;
      _currentWritePos  = 0;
      _rangeStart       = 0;
      _lineNumber       = -1;
      _columnNumber     = -1;
      _currentToken     = null;
      _lookAheadToken.setType(Token.UNKNOWN);
    }
  }

  public AbstractTokenizer() {
    this(0);
  }

  public AbstractTokenizer(int flags) {
    this(flags, Tokenizer.DEFAULT_WHITESPACES, Tokenizer.DEFAULT_SEPARATORS);
  }
  
  public AbstractTokenizer(int flags, String whitespaces, String separators) {
    setParseFlags(flags);
    setWhitespaces(whitespaces);
    setSeparators(separators);

    if ((_flags & Tokenizer.F_KEEP_DATA) != 0) {
      _inputBuffer = new char[0x10000];    
    } else {
      _inputBuffer = new char[0x2000];     
    }
  }

  public void setParseFlags(int flags) {
    _flags = flags;
    
    if ((_flags & (Tokenizer.F_KEYWORDS_NO_CASE | Tokenizer.F_NO_CASE)) == 0) {
      _flags |= Tokenizer.F_KEYWORDS_CASE;
    } else if ((_flags & Tokenizer.F_KEYWORDS_CASE) != 0) {
      _flags &= ~Tokenizer.F_KEYWORDS_NO_CASE;
    }
    
    if ((_flags & Tokenizer.F_COUNT_LINES) != 0) {
      _lineNumber   = 0;
      _columnNumber = 0;
    }
  }

  public int getParseFlags() {
    return _flags;
  }

  public void addString(String start, String end, String escape) {
    addString(start, end, escape, null);
  }

  public void addString(
    String start, 
    String end, 
    String escape, 
    Object companion
  )
  {
    addString(start, end, escape, companion, getParseFlags());
  }

  public void addString(
    String start, 
    String end, 
    String escape, 
    Object companion, 
    int    flags
  )
  {
    addSpecialSequence(
      new TokenizerProperty(Token.STRING, new String[] { start, end, escape }, 
                            companion, flags)
    );
  }

  public void removeString(String start) {
    removeSpecialSequence(start);
  }
  
    
   

  public Object getStringCompanion(String start) {
    return getSpecialSequenceCompanion(start);
  }
  
   

  public boolean stringExists(String start) {
    return specialSequenceExists(start);
  }

   

  public Iterator getStrings() {
    return new SpecialSequencesIterator(this, Token.STRING);
  }
  
   

  public void setWhitespaces(String whitespaces) {
     
    _whitespaces = (whitespaces != null) ? whitespaces : "";
    if (_whitespaces.indexOf('\n') >= 0 || _whitespaces.indexOf('\r') >= 0) {
      _newlineIsWhitespace = true;
    }
    
     
    if (   _whitespaces.equals(Tokenizer.DEFAULT_WHITESPACES)
        || (   _whitespaces.length()      == 4
            && _whitespaces.indexOf('\n') >= 0 
            && _whitespaces.indexOf('\r') >= 0
            && _whitespaces.indexOf(' ')  >= 0
            && _whitespaces.indexOf('\t') >= 0)) {
      _defaultWhitespaces = true;
    }
  }
  
   

  public String getWhitespaces() {
    return _whitespaces;
  }
  
   

  public void setSeparators(String separators) {
    _separators = (separators != null) ? separators : "";
  }
  
   

  public String getSeparators() {
    return _separators;
  }
  
   

  public void addLineComment(String lineComment) {
    addLineComment(lineComment, null);
  }

   

  public void addLineComment(String lineComment, Object companion) {
    addLineComment(lineComment, companion, getParseFlags());
  }

   

  public void addLineComment(String lineComment, Object companion, int flags) {
    addSpecialSequence(
      new TokenizerProperty(Token.LINE_COMMENT, new String[] { lineComment }, 
                            companion, flags)
    );
  }  

   

  public void removeLineComment(String lineComment) {
    removeSpecialSequence(lineComment);
  }
  
   

  public Object getLineCommentCompanion(String lineComment) {
    return getSpecialSequenceCompanion(lineComment);
  }

   

  public boolean lineCommentExists(String lineComment) {
    return specialSequenceExists(lineComment);
  }
  
   

  public Iterator getLineComments() {
    return new SpecialSequencesIterator(this, Token.LINE_COMMENT);
  }
  
   

  public void addBlockComment(String start, String end) {
    addBlockComment(start, end, null);
  }
  
   

  public void addBlockComment(String start, String end, Object companion) {
    addBlockComment(start, end, companion, getParseFlags());
  }
  
   

  public void addBlockComment(String start, String end, Object companion, int flags) {
    addSpecialSequence(
      new TokenizerProperty(Token.BLOCK_COMMENT, new String[] { start, end }, 
                            companion, flags)
    );
  }
  
   

  public void removeBlockComment(String start) {
    removeSpecialSequence(start);
  }
  
   

  public Object getBlockCommentCompanion(String start) {
    return getSpecialSequenceCompanion(start);
  }
  
   

  public boolean blockCommentExists(String start) {
    return specialSequenceExists(start);
  }
  
   

  public Iterator getBlockComments() {
    return new SpecialSequencesIterator(this, Token.BLOCK_COMMENT);
  }
  
   

  public int getCurrentLine() {
    return _lineNumber;
  }
  
   

  public int getCurrentColumn() {
    return _columnNumber;
  }
  
   

  public void addSpecialSequence(String specSeq) {
    addSpecialSequence(specSeq, null);
  }
  
   

  public void addSpecialSequence(String specSeq, Object companion) {
    addSpecialSequence(specSeq, companion, getParseFlags());
  }

   

  public void addSpecialSequence(String specSeq, Object companion, int flags) {
    addSpecialSequence(
      new TokenizerProperty(Token.SPECIAL_SEQUENCE, new String[] { specSeq }, 
                            companion, flags)
    );
  }  
    

   

  public void removeSpecialSequence(String specSeq) {
    if (specSeq != null) {
      try {
        for (int pos = 0; pos < _sequences.length; ++pos) {
          if (_sequences[pos] != null) {
            _sequences[pos].searchBinary(specSeq, 0, true);
          }
        }
      } catch (TokenizerException ex) {
         
        throw new ExtRuntimeException(
                    ex,
                    "While trying to remove special sequence \"{0}\".", 
                    new Object[] { specSeq }
                  );
      }
    }
  }
  
   

  public Object getSpecialSequenceCompanion(String specSeq) {
    if (specSeq != null) {
      TokenizerProperty prop = searchBinary(specSeq);

      if (prop != null) {
        return prop.getCompanion();
      }
    }
    
     
    return null;
  }
  
   

  public Iterator getSpecialSequences() {
    return new SpecialSequencesIterator(this, Token.SPECIAL_SEQUENCE);
  }
  
   

  public boolean specialSequenceExists(String specSeq) {
    if (specSeq != null && searchBinary(specSeq) != null) {
      return true;
    } else {
      return false;
    }
  }
  
   

  public void addKeyword(String keyword) {
    addKeyword(keyword, null);
  }
  
   

  public void addKeyword(String keyword, Object companion) {
    addKeyword(keyword, companion, getParseFlags());
  }
  
   

  public void addKeyword(String keyword, Object companion, int flags) {
     
    int commonFlags = getParseFlags();
    
    if (flags != commonFlags) {
      if ((commonFlags & F_KEYWORDS_CASE) == 0) {
        if ((flags & F_KEYWORDS_CASE) != 0) {
          flags |= F_KEYWORDS_CASE;
        }
      } else {
        if ((flags & (F_KEYWORDS_NO_CASE | F_NO_CASE)) != 0) {
          flags &= ~F_KEYWORDS_CASE;
        }
      }
    }
    
     
     
     
     
    HashMap table;

    if ((flags & Tokenizer.F_KEYWORDS_CASE) != 0) {
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
    
     
    table.put(keyword, new TokenizerProperty(Token.KEYWORD, 
                                             new String[] { keyword }, 
                                             companion, flags));
  }
  
   

  public void removeKeyword(String keyword) {
    if (keyword != null) {
      for (int pos = 0; pos < _keywords.length; ++pos) {
        if (_keywords[pos] != null) {
          _keywords[pos].remove(keyword);
        }
      }
    }
  }
  
   

  public Object getKeywordCompanion(String keyword) {
     
    if (keyword != null) {
      for (int pos = 0; pos < _keywords.length; ++pos) {
        if (_keywords[pos] != null) {
          TokenizerProperty prop = (TokenizerProperty)_keywords[pos].get(keyword);
          
          if (prop != null) {
            return prop.getCompanion();
          }
        }
      }
    }
    
     
    return null;
  }

   

  public Iterator getKeywords() {
    return new KeywordIterator(this);
  }
  
   

  public boolean keywordExists(String keyword) {
    if (keyword != null) {
      if (   (_keywords[0] != null && _keywords[0].containsKey(keyword)) 
          || (_keywords[1] != null && _keywords[1].containsKey(keyword.toUpperCase()))) {
        return true;
      }
    }
    return false;
  }
  
   

  public boolean hasMoreToken() {
    return _currentToken == null || _currentToken.getType() != Token.EOF;
  }
  
   

  public Token nextToken() throws TokenizerException {
    Token token = new Token();
    
     
     
    synchronized(this) {
      
__MAIN_LOOP__:
      do {
         
        token.setStartPosition(getReadPosition());
        token.setStartLine(_lineNumber);
        token.setStartColumn(_columnNumber);

         
        switch (_lookAheadToken.getType()) {
        case Token.UNKNOWN:
          token.setToken(null);
        
           
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

         
        switch (token.getType()) {
        case Token.WHITESPACE:
        case Token.LINE_COMMENT:
        case Token.BLOCK_COMMENT:
          if ((_flags & Tokenizer.F_RETURN_WHITESPACES) == 0) {
            token.setType(Token.UNKNOWN);    
            break;
          }
           
        default:
          if ((_flags & Tokenizer.F_TOKEN_POS_ONLY) == 0) {    
            token.setToken(new String(_inputBuffer, _currentReadPos, token.getLength()));
          }
        }
        
         
         
        adjustLineAndColumn(token.getType(), token.getLength());
        token.setEndLine(_lineNumber);
        token.setEndColumn(_columnNumber);

         
         
        _currentReadPos += token.getLength();
        
      } while (token.getType() == Token.UNKNOWN);
      
       
      _currentToken = token;
    }
    return token;
  }
  
 
   

  public String next() throws TokenizerException {
    nextToken();
    return current();
  }
 
  
   

  public Token currentToken() {
    return _currentToken;
  }
  
 
   

  public String current() {
    Token token = currentToken();
    
    if ((_flags & Tokenizer.F_TOKEN_POS_ONLY) == 0 || token.getToken() != null) {
      return token.getToken();
    } else {
      return getText(token.getStartPosition(), token.getLength());
    }
  }

   

  public int getLineNumber() {
    return _lineNumber;
  }
  
   

  public int getColumnNumber() {
    return _columnNumber;
  }
  
   

  public int getRangeStart() {
    return _rangeStart;
  }
  
   

  public int getReadPosition() {
    return _rangeStart + _currentReadPos;
  }
  
   

  public int currentlyAvailable() {
    return _currentWritePos;
  }
  
   

  public int readMore() throws TokenizerException {
    readMoreData();
    return currentlyAvailable();
  }
  
   

  public void skip(int numberOfChars) throws IndexOutOfBoundsException {
    int available = _currentWritePos - _currentReadPos;
    
    if (numberOfChars > available) {
      throw new ExtIndexOutOfBoundsException(
                  "Number of characters to skip ({0}) exceeds the available number ({1}).", 
                  new Object[] { new Integer(numberOfChars), new Integer(available) } 
                );
    } else if (numberOfChars < 0) {
      throw new ExtIndexOutOfBoundsException(
                  "Number of characters to skip ({0}) is negative.", 
                  new Object[] { new Integer(numberOfChars) } 
                );
    }
    
    _currentReadPos += numberOfChars;
    _lookAheadToken  = null;
  }
  
   

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
  
  public String getTextUnchecked(int start, int len) {
#ifdef FAULT_4
    return new String(_inputBuffer, start - _rangeStart + 1, len);
#else
    return new String(_inputBuffer, start - _rangeStart, len);
#endif
  }
  
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

   

  public char getCharUnchecked(int pos) {
    return _inputBuffer[pos - _rangeStart];
  }

   
   
   
  
   

  public void addTokenizer(AbstractTokenizer tokenizer) {
    AbstractTokenizer curr = this;
    
    while (curr._nextTokenizer != null) {
      curr = curr._nextTokenizer;
    }
    curr._nextTokenizer      = tokenizer;
    tokenizer._prevTokenizer = curr;
    
     
    tokenizer._inputBuffer = getBaseTokenizer(this)._inputBuffer;
  }

   

  public void switchTo(AbstractTokenizer tokenizer) 
    throws TokenizerException
  {
    if (tokenizer._inputBuffer != _inputBuffer) {
      throw new TokenizerException("Trying to switch to an alien tokenizer (not added with addTokenizer).", null);
    }
    tokenizer._currentReadPos  = this._currentReadPos;
    tokenizer._currentWritePos = this._currentWritePos;
    tokenizer._columnNumber    = this._columnNumber;
    tokenizer._lineNumber      = this._lineNumber;
    tokenizer._rangeStart      = this._rangeStart;
  }

   
   
   
  
   

  protected boolean isWhitespace(char testChar) {
    int idx = -1;
    
    if (_defaultWhitespaces) {
      switch (testChar) {
        case ' ':
        case '\n':
        case '\r':
        case '\t':
          idx = 0;
          break;
      }
          
    } else {
      idx = indexInSet(testChar, _whitespaces);
    }
    return (idx >= 0);
  }
      
 
   

  protected int readWhitespaces(int startingAtPos, int maxChars) throws TokenizerException {
    int len   = 0;
    
    while (len < maxChars) {
      if ( ! isWhitespace(getCharUnchecked(startingAtPos + len))) {
        break;
      }
      len++;
    }
    return len;
  }
  
   

  protected int readString(int startingAtPos, int maxChars) throws TokenizerException {
    return 0;
  }
  
   

  protected boolean isSeparator(char testChar) {
    return indexInSet(testChar, _separators) >= 0;
  }

   

  protected TokenizerProperty isSequenceCommentOrString(int startingAtPos) 
    throws TokenizerException 
  {
     
    TokenizerProperty prop   = null;
    int               offset = startingAtPos - (_currentReadPos + _rangeStart);
    
    for (int pos = 0; pos < _sequences.length; ++pos) {
      if (_sequences[pos] != null) {
        TokenizerProperty p = _sequences[pos].searchBinary(null, offset, false);
        
        if (   p != null 
            && (   prop == null
                || p.getValues()[0].length() > prop.getValues()[0].length())) {
          prop = p;
        }
      }
    }
    return prop;
  }

  protected TokenizerProperty isKeyword(int startingAtPos, int length) {
    TokenizerProperty prop = null;
     
    if (_keywords[0] != null || _keywords[1] != null) {
#ifdef FAULT_5
      String keyword = new String(_inputBuffer, startingAtPos - _rangeStart + 1, length - 1);
#else
      String keyword = new String(_inputBuffer, startingAtPos - _rangeStart, length);
#endif      
      if (_keywords[0] != null) {
        prop = (TokenizerProperty)_keywords[0].get(keyword);
      }
      if (prop == null && _keywords[1] != null) {
        keyword = keyword.toUpperCase();
#ifdef FAULT_5
        prop    = (TokenizerProperty)_keywords[0].get(keyword);
#else
        prop    = (TokenizerProperty)_keywords[1].get(keyword);
#endif
      }
    }
    return prop;
  }
  
  protected void addSpecialSequence(TokenizerProperty prop) {
    int arrayIdx;
    int flags = prop.getFlags();
    
    if ((flags & Tokenizer.F_NO_CASE) == 0) {
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
    _sequences[arrayIdx].addSpecialSequence(prop);
  }

  protected TokenizerProperty searchBinary(String specSeq) {
    try {
      for (int pos = 0; pos < _sequences.length; ++pos) {
        TokenizerProperty prop;
        
        if (   _sequences[pos] != null 
            && (prop = _sequences[pos].searchBinary(specSeq, 0, false)) != null) {
          return prop;
        }
      } 
    } catch (TokenizerException ex) {
       
      throw new ExtRuntimeException(
                  ex,
                  "While trying to retrieve the companion of special sequence \"{0}\".", 
                  new Object[] { specSeq }
                );
    }
    return null;
  }

  protected AbstractTokenizer getBaseTokenizer(AbstractTokenizer t) {
    while (t._prevTokenizer != null) {
      t = t._prevTokenizer;
    }
    return t;
  }
  
   

  protected int readMoreData() throws TokenizerException  {
     
    int               bytes = 0;
    AbstractTokenizer base  = getBaseTokenizer(this);
    
    if (base != this) {
      return base.readMoreData();
    }
    
    int readOffset = 0;
    
    if ((_flags & Tokenizer.F_KEEP_DATA) == 0) {
      if (   _currentReadPos  > _inputBuffer.length / 4
          && _currentWritePos > (3 * _inputBuffer.length) / 4) {
        System.arraycopy(_inputBuffer, _currentReadPos, _inputBuffer, 0, _currentWritePos - _currentReadPos);
        readOffset        = _currentReadPos;
        _rangeStart      += _currentReadPos;
        _currentWritePos -= _currentReadPos;
        _currentReadPos   = 0;
      }
    }

    if (_currentWritePos >= _inputBuffer.length) {
      char[] newBuffer = new char[_inputBuffer.length * 2];
      
      if ((_flags & Tokenizer.F_KEEP_DATA) != 0) {
        System.arraycopy(_inputBuffer, 0, newBuffer, 0, _currentWritePos);
      } else {
        System.arraycopy(_inputBuffer, _currentReadPos, newBuffer, 0, _currentWritePos - _currentReadPos);
      }
      _inputBuffer = newBuffer;
    }
     
    while (bytes == 0) {
      try {
        bytes = read(_inputBuffer, _currentWritePos, _inputBuffer.length - _currentWritePos);
      } catch (Exception ex) {
        throw new TokenizerException(ex);
      }
    }
    if (bytes > 0) {
      _currentWritePos += bytes;
    }
     
    base.synchronizeAll(readOffset);
    return bytes;
  }
  
  protected void synchronizeAll(int readPosOffset) {
    AbstractTokenizer base     = getBaseTokenizer(this);
    AbstractTokenizer embedded = base;

    while ((embedded = embedded._nextTokenizer) != null) {
      embedded._inputBuffer     = base._inputBuffer;
      embedded._currentWritePos = base._currentWritePos;
      embedded._currentReadPos += readPosOffset;
    }
  }

  protected boolean test4Normal(Token token) throws TokenizerException {

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
     
    if (len <= 0) {
      return false;
    }
     
    TokenizerProperty prop = isKeyword(_currentReadPos + _rangeStart, len);
    
    if (prop != null) {
#ifdef FAULT_6
      token.setLength(Token.KEYWORD); 
#else
      token.setType(Token.KEYWORD); 
#endif
      token.setCompanion(prop.getCompanion());
    } else {
      token.setType(Token.NORMAL);
    }
    token.setLength(len);
    return true;
  }

  protected boolean test4Whitespace(Token token) throws TokenizerException {
    if (_currentReadPos < _currentWritePos ||  readMoreData() > 0) {
      if (isWhitespace(_inputBuffer[_currentReadPos])) {
        completeWhitespace(token);
        return true;
      }
    }
    return false;
  }

  protected void completeWhitespace(Token token) throws TokenizerException {
    int start     = _currentReadPos + 1;   
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
    token.setLength(len + 1);            
  }
  
  protected boolean isWhitespace(int offset) throws TokenizerException {
     
    if (_currentReadPos + offset >= _currentWritePos && readMoreData() < 0) {
      return false;
    }
     
    if (isWhitespace(_inputBuffer[_currentReadPos + offset])) {
      _lookAheadToken.setType(Token.WHITESPACE);
      return true;
    } else {
      return false;
    }
  }

  protected boolean test4Separator(Token token) throws TokenizerException {
    if (isSeparator(0)) {
      _lookAheadToken.setType(Token.UNKNOWN);
      completeSeparator(token);
      return true;
    } else {
      return false;
    }
  }
  
  protected void completeSeparator(Token token) throws TokenizerException {
    token.setType(Token.SEPARATOR);
    token.setLength(1);
  }

  protected boolean isSeparator(int offset) throws TokenizerException {
    if (_currentReadPos + offset < _currentWritePos ||  readMoreData() > 0) {
      int idx = indexInSet(_inputBuffer[_currentReadPos + offset], _separators);
      if (idx >= 0) {
        _lookAheadToken.setType(Token.SEPARATOR);
        return true;
      }
    }
    return false;
  }

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
  
  protected void completeSpecialSequence(Token token) throws TokenizerException {
    TokenizerProperty prop = (TokenizerProperty)_lookAheadToken.getCompanion();
    String            seq  = prop.getValues()[0];
      
    token.setType(prop.getType());
    token.setCompanion(prop.getCompanion());
      
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
        return len;        
      case '\n':
        return len + 1;      
      default:
        len++;
      }
    }

    return len;
  }

  protected int completeBlockComment(int offset, TokenizerProperty prop) 
    throws TokenizerException 
  {
    String  start  = prop.getValues()[0];
    String  end    = prop.getValues()[1];
    boolean noCase = (prop.getFlags() & F_NO_CASE) != 0;
    int     len    = offset;
    int     level  = 0;

  __LOOP__:
    do {
       
       
      if (   (_flags & Tokenizer.F_ALLOW_NESTED_COMMENTS) != 0) {
        switch (comparePrefix(len, start, noCase)) {
        case 0:      
          level++;
          len += start.length();
          continue __LOOP__;
        case -1:     
          return _currentWritePos - _currentReadPos;   
        }
      }
      
       
      switch (comparePrefix(len, end, noCase)) {
      case 0:        
        level--;
        len += end.length();
        break;
      case -1:       
        return _currentWritePos - _currentReadPos;
      default:
        len++;
      }
    } while (level >= 0);
    
    return len;
  }

  protected int completeString(int offset, TokenizerProperty prop) 
    throws TokenizerException 
  {
    String  end           = prop.getValues()[1];
    String  esc           = prop.getValues()[2];
    boolean noCase        = (prop.getFlags() & F_NO_CASE) != 0;
    boolean escEqualsEnd  =    ( ! noCase && esc.compareTo(end)           == 0)
                            || (   noCase && esc.compareToIgnoreCase(end) == 0);
    int     len           = offset;

    while (true) {
       
      if (esc != null) {
        switch (comparePrefix(len, esc, noCase)) {
        case 0:        
          len += esc.length();
          if (escEqualsEnd) {
            switch (comparePrefix(len, end, noCase)) {
            case 0:
              len += end.length();
              break;
            case -1:       
              return _currentWritePos - _currentReadPos;   
            default:
              return len;  
            }
          } else {
            len++;         
          }
          continue;
        case -1:           
          return _currentWritePos - _currentReadPos;   
        }
      }

      switch (comparePrefix(len, end, noCase)) {
      case 0:              
        len += end.length();    
        return len;
      case -1:             
        return _currentWritePos - _currentReadPos;   
      }
     
      len++;
    }
  }

  protected int comparePrefix(int offset, String prefix, boolean noCase) 
    throws TokenizerException 
  {
     
    int len = prefix.length();
    
    for (int pos = offset; pos < offset + len; ++pos) {
       
      if (_currentReadPos + pos >= _currentWritePos && readMoreData() < 0) {
        return -1;
      }
      
       
      char c1 = prefix.charAt(pos - offset);
      char c2 = _inputBuffer[_currentReadPos + pos];
      
      if (   c1 != c2
          && (! noCase || Character.toUpperCase(c1) != Character.toUpperCase(c2))) {
        return 1;
      }
    }
     
    return 0;
  }

  protected int indexInSet(char ch, String set) {
    int  len = (set != null) ? set.length() : 0;
    char start, end, setChar;
    char chUp = 0;
    
    if ((_flags & Tokenizer.F_NO_CASE) != 0){
      chUp = Character.toUpperCase(ch);
    }
    
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
         
      default:
        if (   ch == setChar
            || ((_flags & Tokenizer.F_NO_CASE) != 0 && chUp == Character.toUpperCase(setChar))) {
          return ii;
        }
      }
    }
    return -1;
  }
  
  protected void adjustLineAndColumn(int type, int length) {
    if ((_flags & Tokenizer.F_COUNT_LINES) == 0) {
      return;
    }
     
    switch (type) {
    case Token.EOF:
      return;
        
    case Token.LINE_COMMENT:         
      _lineNumber++;
      _columnNumber = 0;
      return;
      
    case Token.SPECIAL_SEQUENCE:
    case Token.NORMAL:
    case Token.KEYWORD:
      if (_newlineIsWhitespace) {    
        _columnNumber += length;     
        return;                      
      }
      break;
        
    case Token.WHITESPACE:
      if (!_newlineIsWhitespace) {   
        _columnNumber += length;     
        return;                      
      }
      break;
    }
     
    for (int pos = _currentReadPos; pos < _currentReadPos + length; ++pos) {
      switch (_inputBuffer[pos]) {
      case '\r':
        if (pos + 1 >= _currentReadPos + length || _inputBuffer[pos + 1] != '\n') {
          _lineNumber++;
          _columnNumber = 0;
          break;
        }
        pos++;
         
      case '\n':
        _lineNumber++;
        _columnNumber = 0;
        break;
        
      default:
        _columnNumber++;
      }
    }
  }

  protected int _flags = 0;

  protected String _whitespaces = Tokenizer.DEFAULT_WHITESPACES;

  protected String _separators = Tokenizer.DEFAULT_SEPARATORS;

  protected SortedArray[] _sequences = new SortedArray[2];

  protected HashMap[] _keywords = new HashMap[2];

  protected boolean _newlineIsWhitespace  = false;

  protected char[] _inputBuffer = null;

  protected int _currentReadPos = 0;

  protected int _currentWritePos = 0;

  protected int _rangeStart = 0;

  protected int _lineNumber = -1;

  protected int _columnNumber = -1;

  protected Token _currentToken = null;

  protected Token _lookAheadToken = new Token();

  protected AbstractTokenizer _nextTokenizer = null;

  protected AbstractTokenizer _prevTokenizer = null;

  private boolean _defaultWhitespaces = false;
}

final class SortedArray {
  public SortedArray(AbstractTokenizer parent, int flags) {
    _parent = parent;
    _flags  = flags;
  }

  private int searchIndex(char startChar, int[] nearestMatch) {
     
    char upChar = Character.toUpperCase(startChar);
    int  left   = 0;
    int  right  = _array.size() - 1;
    int  res    = -1;

    nearestMatch[0] = -1;

     
  __MAIN_LOOP__:
    while (left <= right) {
      int idx;

       
      if ((idx = (right + left) / 2) == nearestMatch[0]) {
        break;
      }
      nearestMatch[0] = idx;

       
      SpecialSequence   listElem = (SpecialSequence)_array.get(idx);
      TokenizerProperty existing = listElem._property;
      String            exSeq    = existing.getValues()[0];

      if ((_flags & Tokenizer.F_NO_CASE) != 0) {
        res = Character.toUpperCase(exSeq.charAt(0)) - upChar;
      } else {
        res = exSeq.charAt(0) - startChar; 
      }

      if (res == 0) { 
        break;                 
      } else if (res < 0) {   
        left = idx + 1;        
      } else {                
        right = idx - 1;       
      }
    }
     
    return res;
  }

  public SpecialSequence get(int index) {
    return (SpecialSequence)_array.get(index);
  }

  public int size() {
    return _array.size();
  }

  protected TokenizerProperty searchBinary(String specSeq, int offset, boolean remove) 
    throws TokenizerException
  {
     
    if (  _array == null 
       || (specSeq == null
           && _parent._currentReadPos + offset >= _parent._currentWritePos 
           && _parent.readMoreData() <= 0)) {
      return null;
    }

    boolean fromStream   = (specSeq == null);
    int[]   nearestMatch = { -1 };
    char    startChar;

    if (specSeq == null) {
      startChar = _parent._inputBuffer[_parent._currentReadPos + offset];
    } else {
      startChar = specSeq.charAt(0);
    }
    if (searchIndex(startChar, nearestMatch) != 0) {
      return null;
    }
     
    SpecialSequence   listElem   = (SpecialSequence)_array.get(nearestMatch[0]);
    TokenizerProperty existing   = listElem._property;
    String            exSeq      = existing.getValues()[0];

    if (specSeq == null) {
      int len = exSeq.length();

      if (_parent._currentReadPos + offset + len >= _parent._currentWritePos) {
        _parent.readMoreData();

        if ((_parent._currentWritePos - (_parent._currentReadPos + offset)) < len) {
          len = _parent._currentWritePos - (_parent._currentReadPos + offset);
        }
        if (len <= 0) {
          return null;
        }
      }
      specSeq = new String(_parent._inputBuffer, _parent._currentReadPos + offset, len);
    }
     
    SpecialSequence prevElem = null;

    do {
      existing = listElem._property;
      exSeq    = existing.getValues()[0];

      if (specSeq.length() > exSeq.length()) {
        if (fromStream) {
          specSeq = specSeq.substring(0, exSeq.length());
        } else {
          return null;
        }
      }

      if (specSeq.length() == exSeq.length()) {
        int res;

        if ((_flags & Tokenizer.F_NO_CASE) != 0) {
          res = exSeq.compareToIgnoreCase(specSeq);
        } else {
          res = exSeq.compareTo(specSeq); 
        }
        if (res == 0) {
          if (remove) {
            if (prevElem == null) {
              if (listElem._next == null) {
                _array.remove(nearestMatch[0]);
              } else {
                listElem = listElem._next;
                _array.set(nearestMatch[0], listElem);
              }
            } else {
              prevElem._next = listElem._next;
            }
            return null;
          } else {
            return listElem._property;
          }
        } else if (res < 0) {   
          specSeq = specSeq.substring(0, specSeq.length() - 1);
        }
      }
      prevElem = listElem;
      listElem = listElem._next;
    } while (listElem != null);
    return null;
  }

  public void addSpecialSequence(TokenizerProperty prop) {
     
    if (_array == null) {
      _array = new ArrayList();
    }
     
    String  seq           = prop.getValues()[0];
    int[]   nearestMatch  = { -1 };
    int     res           = searchIndex(seq.charAt(0), nearestMatch);

    int idx = nearestMatch[0];

    if (res < 0) {
      idx++;
    }

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
        exSeq    = existing.getValues()[0];

        if (seq.length() > exSeq.length()) {
          listElem._next     = new SpecialSequence(existing, listElem._next);
          listElem._property = prop;
          break;
        } else if (seq.length() == exSeq.length()) {
          if ((_flags & Tokenizer.F_NO_CASE) != 0) {
            res = exSeq.compareToIgnoreCase(seq);
          } else {
            res = exSeq.compareTo(seq); 
          }
          if (res == 0) {
            listElem._property = prop;
          } else if (res < 0) {
            listElem._next     = new SpecialSequence(existing, listElem._next);
            listElem._property = prop;
          } else if (listElem._next == null) {
            listElem._next     = new SpecialSequence(prop);
          }
          break;
        } else if (listElem._next == null) {
          listElem._next = new SpecialSequence(prop);
          break;
        }
        listElem = listElem._next;
      }
    }
  }
   
  private AbstractTokenizer _parent = null;
  private ArrayList         _array  = null;
  int                       _flags  = 0;
}

final class SpecialSequence {
  SpecialSequence(TokenizerProperty prop) {
    this(prop, null);
  }

  SpecialSequence(TokenizerProperty prop, SpecialSequence next) {
    _property = prop;
    _next     = next;
  }

  public SpecialSequence    _next;
  public TokenizerProperty  _property;
}
  
 

final class KeywordIterator implements Iterator {

  public KeywordIterator(AbstractTokenizer parent) {
    if (parent._keywords[0] != null) {
      _iterators[0] = parent._keywords[0].values().iterator();
    }
    if (parent._keywords[1] != null) {
      _iterators[1] = parent._keywords[1].values().iterator();
    }
  }

  public boolean hasNext() {
     
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

  public Object next() {
    if (hasNext()) {
      if (_iterators[0] != null) {
        return _iterators[0].next();
      } else {
        return _iterators[1].next();
      }
    }
    return null;
  }

  public void remove() {
    if (_iterators[0] != null) {
      _iterators[0].remove();
    } else {
      _iterators[1].remove();
    }
  }

  private Iterator[] _iterators = new Iterator[2];
}

final class SpecialSequencesIterator implements Iterator {

  public SpecialSequencesIterator(AbstractTokenizer parent, int type) {
    _type      = type;
    _arrays[0] = parent._sequences[0];
    _arrays[1] = parent._sequences[1];
  }

  private boolean listHasNext() {
    while (_currentElem != null) {
      if (_currentElem._property.getType() == _type) {
        return true;
      }
      _currentElem = _currentElem._next;
    }
    return false;
  }

  public boolean hasNext() {
     
    if (listHasNext()) {
      return true;
    }

    SortedArray array = null;

    if (_arrays[0] != null) {
      array = _arrays[0];
    } else {
      array = _arrays[1];
    }
     
    if (array != null) {
      int size = array.size();        

      while (++_currentIndex < size) {
        _currentElem = array.get(_currentIndex);
        if (listHasNext()) {
          return true;
        }
      }
       
      if (array == _arrays[0]) {
        _arrays[0]    = null;
        _currentElem  = null;
        _currentIndex = -1;
        return hasNext();
      }
    }

    return false;
  }

  public Object next() {
    if (! hasNext()) {
      return null;
    } else {
      TokenizerProperty prop = _currentElem._property;
      _currentElem = _currentElem._next;
      return prop;
    }
  }

  public void remove() {
     
    SortedArray array = null;

    if (_arrays[0] != null) {
      array = _arrays[0];
    } else {
      array = _arrays[1];
    }

    try {
      TokenizerProperty prop = _currentElem._property;
      
      _currentElem = _currentElem._next;
      array.searchBinary(prop.getValues()[0], 0, true);
    } catch (Exception ex) {
      throw new ExtRuntimeException(ex, "While trying to remove current element of a SpecialSequencesIterator.");
    }
  }

  private SortedArray[]   _arrays       = new SortedArray[2];
  private SpecialSequence _currentElem  = null;
  private int             _currentIndex = -1;
  private int             _type         = Token.UNKNOWN;
}
