#include "../FaultSeeds.h" 

package de.susebox.jtopas;
 
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

public class StandardTokenizer implements Tokenizer, TokenizerPropertyListener {
  public StandardTokenizer() {
    if (_defaultProperties == null) {
      _defaultProperties = new StandardTokenizerProperties();
    }
    setTokenizerProperties(_defaultProperties);
  }
  
  public StandardTokenizer(TokenizerProperties properties) {
    setTokenizerProperties(properties);
  }

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

  public void setSource(Reader reader) {
    setSource(new ReaderSource(reader));
  }
  
  public TokenizerSource getSource() {
    return _source;
  }

  public void setTokenizerProperties(TokenizerProperties props) throws NullPointerException {
    if (props == null) {
      throw new NullPointerException();
    }

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
     
    if (_properties != null) {
      _properties.removeTokenizerPropertyListener(this);
    }
    _properties = props;
    _properties.addTokenizerPropertyListener(this);
     
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
  
  public TokenizerProperties getTokenizerProperties() {
    return _properties;
  }
  
  public void changeParseFlags(int flags, int mask) throws TokenizerException {
     
    if ((mask | VALID_FLAGS_MASK) != VALID_FLAGS_MASK) {
      throw new TokenizerException(
                  "One or more flags cannot be set separately for a {0}. Violating flags in {1}: {2}.",
                  new Object[] { StandardTokenizer.class.getName(), 
                                 Integer.toHexString(flags),
                                 Integer.toHexString(mask & ~VALID_FLAGS_MASK) } );
    }
    
     
    _flagMask = mask;
    _flags    = (flags & mask) | (getTokenizerProperties().getParseFlags() & ~mask);

     
    if ((_flags & TokenizerProperties.F_COUNT_LINES) != 0) {
      _lineNumber   = 0;
      _columnNumber = 0;
    }
  }

    

  public int getParseFlags() {
    return (getTokenizerProperties().getParseFlags() & ~_flagMask) + (_flags & _flagMask);
  }
  
   

  public void setKeywordHandler(de.susebox.jtopas.spi.KeywordHandler handler) {
    _keywordHandler = handler; 
  }
  
    

  public de.susebox.jtopas.spi.KeywordHandler getKeywordHandler() {
    return _keywordHandler;
  }
  
   

  public void setWhitespaceHandler(de.susebox.jtopas.spi.WhitespaceHandler handler) {
    _whitespaceHandler = handler;
  }
  
   

  public de.susebox.jtopas.spi.WhitespaceHandler getWhitespaceHandler() {
    return _whitespaceHandler;
  }
  
   

  public void setSeparatorHandler(de.susebox.jtopas.spi.SeparatorHandler handler) {
    _separatorHandler = handler;
  }
  
   

  public de.susebox.jtopas.spi.SeparatorHandler getSeparatorHandler() {
    return _separatorHandler;
  }
  
   

  public void setSequenceHandler(de.susebox.jtopas.spi.SequenceHandler handler) {
    _sequenceHandler = handler;
  }
  
   

  public de.susebox.jtopas.spi.SequenceHandler getSequenceHandler() {
    return _sequenceHandler;
  }
  
   

  public void setPatternHandler(de.susebox.jtopas.spi.PatternHandler handler) {
    _patternHandler = handler;
  }
  
   

  public de.susebox.jtopas.spi.PatternHandler getPatternHandler() {
    return _patternHandler;
  }
  
   

  public int getCurrentLine() {
    return _lineNumber;
  }
  
   

  public int getCurrentColumn() {
    return _columnNumber;
  }
  
   

  public boolean hasMoreToken() {
    return _currentToken == null || _currentToken.getType() != Token.EOF;
  }
  
   

  public Token nextToken() throws TokenizerException {
    Token token = new Token();
    
     
__MAIN_LOOP__:
    do {
       
      token.setStartPosition(getReadPosition());
      token.setStartLine(_lineNumber);
      token.setStartColumn(_columnNumber);

       
      switch (_lookAheadToken.getType()) {
      case Token.UNKNOWN:
        token.setImage(null);

         
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
        if ( ! isFlagSet(TokenizerProperties.F_RETURN_WHITESPACES)) {
          token.setType(Token.UNKNOWN);    
          break;
        }
         
      default:
        if ( ! isFlagSet(TokenizerProperties.F_TOKEN_POS_ONLY)) {    
          token.setImage(new String(_inputBuffer, _currentReadPos, token.getLength()));
        }
      }

       
       
      adjustLineAndColumn(token.getType(), token.getLength());
      token.setEndLine(_lineNumber);
      token.setEndColumn(_columnNumber);

       
       
      _currentReadPos += token.getLength();

    } while (token.getType() == Token.UNKNOWN);

     
    _currentToken = token;
    return token;
  }
  
 
   

  public String nextImage() throws TokenizerException {
    nextToken();
    return currentImage();
  }
 
  
   

  public Token currentToken() {
    return _currentToken;
  }
  
 
   

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

   

  public void setReadPositionRelative(int offset) throws IndexOutOfBoundsException {
    setReadPositionAbsolute(getReadPosition() + offset);
  }
  
   
   
   
  
   

  public void addTokenizer(StandardTokenizer tokenizer) throws TokenizerException {
    StandardTokenizer curr = this;
    
    while (curr._nextTokenizer != null) {
      curr = curr._nextTokenizer;
    }
    
    if (tokenizer != null) {
      synchronized(tokenizer) {
        curr._nextTokenizer      = tokenizer;
        tokenizer._prevTokenizer = curr;

         
        StandardTokenizer base = getBaseTokenizer(this);
        
        tokenizer._inputBuffer = base._inputBuffer;
        
         
        tokenizer.changeParseFlags(base.getParseFlags(), TokenizerProperties.F_COUNT_LINES);
      }
    }
  }

   

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

   
   
   
  
   

  protected boolean isWhitespace(char testChar) {
    if (_whitespaceHandler != null) {
      return _whitespaceHandler.isWhitespace(testChar);
    } else {
      return false;
    }
  }
      
 
   

  protected int readWhitespaces(int startingAtPos, int maxChars) throws TokenizerException {
    if (_whitespaceHandler != null) {
      StandardDataProvider dataProvider = new StandardDataProvider(_inputBuffer, startingAtPos - _rangeStart, maxChars);
      return _whitespaceHandler.countLeadingWhitespaces(dataProvider);
    } else {
      return 0;
    }
  }
  
   

  protected boolean isSeparator(char testChar) {
    if (_separatorHandler != null) {
      return _separatorHandler.isSeparator(testChar);
    } else {
      return false;
    }
  }

   

  protected TokenizerProperty isSequenceCommentOrString(int startingAtPos) 
    throws TokenizerException 
  {
    if (_sequenceHandler != null) {
       
       
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
      
       
      StandardDataProvider dataProvider = new StandardDataProvider(_inputBuffer, start, maxLength);
      return _sequenceHandler.startsWithSequenceCommentOrString(dataProvider);
      
    } else {
       
      return null;
    }
  }

   

  protected TokenizerProperty isKeyword(int startingAtPos, int length) throws TokenizerException {
    if (_keywordHandler != null) {
      StandardDataProvider dataProvider = new StandardDataProvider(_inputBuffer, startingAtPos - _rangeStart, length);
      return _keywordHandler.isKeyword(dataProvider);
    } else {
      return null;
    }
  }
  
   
   
   
  
   

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
  
   
   
   

   

  protected StandardTokenizer getBaseTokenizer(StandardTokenizer t) {
    while (t._prevTokenizer != null) {
      t = t._prevTokenizer;
    }
    return t;
  }
  
   

  protected int readMoreData() throws TokenizerException  {
     
    int               bytes = 0;
    StandardTokenizer base  = getBaseTokenizer(this);
    
    if (base != this) {
      return base.readMoreData();
    } else if (_source == null) {
      return -1;
    }

     
    if (_inputBuffer == null) {
      if ((_flags & TokenizerProperties.F_KEEP_DATA) != 0) {
        _inputBuffer = new char[0x10000];    
      } else {
        _inputBuffer = new char[0x2000];     
      }
    }
    
     
     
     
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
    
     
     
    if (_currentWritePos >= _inputBuffer.length) {
      char[] newBuffer = new char[_inputBuffer.length * 2];
      
#ifdef FAULT_12
      if (!isFlagSet(TokenizerProperties.F_KEEP_DATA)) {
#else
      if (isFlagSet(TokenizerProperties.F_KEEP_DATA)) {
#endif
        System.arraycopy(_inputBuffer, 0, newBuffer, 0, _currentWritePos);
      } else {
        System.arraycopy(_inputBuffer, _currentReadPos, newBuffer, 0, _currentWritePos - _currentReadPos);
      }
      _inputBuffer = newBuffer;
    }
    
     
     
     
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
    
     
    base.synchronizeAll(readOffset);
    return bytes;
  }
  
   

  protected void synchronizeAll(int readPosOffset) {
    StandardTokenizer base     = getBaseTokenizer(this);
    StandardTokenizer embedded = base;

    while ((embedded = embedded._nextTokenizer) != null) {
      embedded._inputBuffer     = base._inputBuffer;
      embedded._currentWritePos = base._currentWritePos;
#ifdef FAULT_11
      embedded._currentReadPos -= readPosOffset;      
#else
      embedded._currentReadPos += readPosOffset;
#endif
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
      token.setType(Token.KEYWORD); 
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
#ifdef FAULT_10
    token.setLength(len);
#else
    token.setLength(len + 1);            
#endif
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
      if (isSeparator(_inputBuffer[_currentReadPos + offset])) {
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
    String            seq  = prop.getImages()[0];
      
     
    token.setType(prop.getType());
    token.setCompanion(prop.getCompanion());
      
     
    switch (prop.getType()) {
    case Token.STRING:
      token.setLength(completeString(seq.length(), prop));
      break;
    case Token.BLOCK_COMMENT:
#ifdef FAULT_9
      token.setLength(completeLineComment(seq.length()));
#else
      token.setLength(completeBlockComment(seq.length(), prop));
#endif
      break;
    case Token.LINE_COMMENT:
#ifdef FAULT_8
      token.setLength(completeBlockComment(seq.length(), prop));
#else
      token.setLength(completeLineComment(seq.length()));
#endif
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
    String[]  images = prop.getImages();
    String    start  = images[0];
    String    end    = images[1];
    boolean   noCase = (prop.getFlags() & TokenizerProperties.F_NO_CASE) != 0;
    int       len    = offset;
    int       level  = 0;
    boolean   nested = isFlagSet(TokenizerProperties.F_ALLOW_NESTED_COMMENTS);

  __LOOP__:
    do {
       
       
      if (nested) {
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
    String[]  images        = prop.getImages();
    String    end           = images[1];
    String    esc           = images[2];
    boolean   noCase        = (prop.getFlags() & TokenizerProperties.F_NO_CASE) != 0;
    boolean   escEqualsEnd  =    ( ! noCase && esc.compareTo(end)           == 0)
                              || (   noCase && esc.compareToIgnoreCase(end) == 0);
    int       len           = offset;

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
#ifdef FAULT_7
	return -1;
#else
        return 1;
#endif
      }
    }
    
     
    return 0;
  }
  
   

  protected void adjustLineAndColumn(int type, int length) {
     
    if ( ! isFlagSet(TokenizerProperties.F_COUNT_LINES)) {
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
      if (_whitespaceHandler.newlineIsWhitespace()) {  
#ifdef FAULT_6
        _columnNumber = length;
#else
        _columnNumber += length;                       
#endif
        return;                                        
      }
      break;
        
    case Token.WHITESPACE:
      if ( ! _whitespaceHandler.newlineIsWhitespace()) {   
#ifdef FAULT_5
        _columnNumber -= length;  
#else
        _columnNumber += length;                           
#endif
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
  
   

  protected boolean isFlagSet(int flag) {
    if ((_flagMask & flag) != 0) {
      return (_flags & flag) != 0;
    } else {
      return (getTokenizerProperties().getParseFlags() & flag) != 0;
    }
  }
  
   
   
   
  
   

  protected static final int VALID_FLAGS_MASK = 
      TokenizerProperties.F_RETURN_WHITESPACES 
    | TokenizerProperties.F_TOKEN_POS_ONLY
    | TokenizerProperties.F_ALLOW_NESTED_COMMENTS
    | TokenizerProperties.F_KEEP_DATA
    | TokenizerProperties.F_COUNT_LINES;
  
   

  protected StandardTokenizerProperties _defaultProperties = null;
  
   
   
   
  
   

  protected int _flags = 0;
  
   

  private int _flagMask = 0;
  
   

  protected char[] _inputBuffer = null;

   

  protected int _currentReadPos = 0;

   

  protected int _currentWritePos = 0;
  
   

  protected int _rangeStart = 0;
  
   

  protected int _lineNumber = -1;

   

  protected int _columnNumber = -1;
  
   

  protected Token _currentToken = null;
  
   

  protected Token _lookAheadToken = new Token();
  
   

  protected StandardTokenizer _nextTokenizer = null;

   

  protected StandardTokenizer _prevTokenizer = null;
  
   

  private de.susebox.jtopas.spi.WhitespaceHandler _whitespaceHandler = null;

   

  private de.susebox.jtopas.spi.SeparatorHandler _separatorHandler = null;

   

  private de.susebox.jtopas.spi.KeywordHandler _keywordHandler = null;

   

  private de.susebox.jtopas.spi.SequenceHandler _sequenceHandler = null;
  
   

  private de.susebox.jtopas.spi.PatternHandler _patternHandler = null;
  
   

  private TokenizerSource _source = null;
  
   

  private TokenizerProperties _properties = null;
}

 

class StandardDataProvider implements DataProvider {
  
   
   
   
  
   

  public StandardDataProvider(char[] data, int startPosition, int length) {
    _data           = data;
    _startPosition  = startPosition;
    _length         = length;
  }
  
   
   
   
  
   

  public char[] getData() {
    return _data;
  }
  
   

  public char[] getDataCopy() {
    char[] copy = new char[getLength()];
    
    System.arraycopy(_data, getStartPosition(), copy, 0, copy.length);
    return copy;
  }
  
     
   

  public int getStartPosition() {
    return _startPosition;
  }

   

  public int getLength() {
    return _length;
  }
  
   

  public String toString() {
    if (_data != null) {
      return new String(_data, _startPosition, _length);
    } else {
      return "";
    }
  }

  private char[]  _data           = null;
  private int     _startPosition  = 0;
  private int     _length         = 0;
}
