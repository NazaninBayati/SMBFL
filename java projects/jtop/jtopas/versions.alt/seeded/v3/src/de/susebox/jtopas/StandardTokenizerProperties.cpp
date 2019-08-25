#include "../FaultSeeds.h" 

package de.susebox.jtopas;

 
 
 
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

 
 
 

 

public class StandardTokenizerProperties 
  implements TokenizerProperties, DataMapper
{
  public StandardTokenizerProperties() {
    this(0);
  }

  public StandardTokenizerProperties(int flags) {
#ifdef FAULT_13
#else
    this(flags, DEFAULT_WHITESPACES, DEFAULT_SEPARATORS);
#endif
  }
  
   

  public StandardTokenizerProperties(int flags, String whitespaces, String separators) {
    setParseFlags(flags);
    setWhitespaces(whitespaces);
    setSeparators(separators);
  }
  
   
   
   
  
   

  public void setParseFlags(int flags) {
     
    flags = normalizeFlags(flags);
    
#ifdef FAULT_15
#else     
    synchronized(this) {
#endif
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
#ifdef FAULT_15
#else
    }
#endif
  }

    

  public int getParseFlags() {
    return _flags;
  }
  
   

  public void addString(String start, String end, String escape) 
    throws IllegalArgumentException
  {
    addString(start, end, escape, null);
  }

   

  public void addString(String start, String end, String escape, Object companion)
    throws IllegalArgumentException
  {
    addString(start, end, escape, companion, getParseFlags());
  }
  
   

  public void addString(String start, String end, String escape, Object companion, int flags)
    throws IllegalArgumentException
  {
    addSpecialSequence(
      new TokenizerProperty(Token.STRING, new String[] { start, end, escape }, 
                            companion, flags)
    );
  }
  
   

  public void removeString(String start) throws IllegalArgumentException {
    removeSpecialSequence(start);
  }
  
    
   

  public Object getStringCompanion(String start) throws IllegalArgumentException {
    return getSpecialSequenceCompanion(start);
  }
  
   

  public boolean stringExists(String start) {
    return getString(start) != null;
  }

   

  public TokenizerProperty getString(String start) throws IllegalArgumentException {
    return getSpecialSequence(start);
  }
  
   

  public Iterator getStrings() {
    return new SpecialSequencesIterator(this, Token.STRING);
  }
  
   

  public void setWhitespaces(String whitespaces) {
#ifdef FAULT_14
    System.out.println("========FAULT_14========");
#else
    synchronized(_syncWhitespaces) {
#endif
      String ws;
      
       
      ws = (whitespaces != null) ? whitespaces : "";
      if (ws.indexOf('\n') >= 0 || ws.indexOf('\r') >= 0) {
        _newlineIsWhitespace = true;
      }

       
      if (   ws.equals(DEFAULT_WHITESPACES)
          || (   ws.length()      == 4
              && ws.indexOf('\n') >= 0 
              && ws.indexOf('\r') >= 0
              && ws.indexOf(' ')  >= 0
              && ws.indexOf('\t') >= 0)) {
        _defaultWhitespaces = true;
      }
      
       
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
#ifdef FAULT_14
#else
    }
#endif
  }

   

  public String getWhitespaces() {
    synchronized(_syncWhitespaces) {
      return _whitespacesCase + _whitespacesNoCase;
    }
  }
  
   

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
  
   

  public String getSeparators() {
    synchronized(_syncSeparators) {
      return _separatorsCase + _separatorsNoCase;
    }
  }
  
   

  public void addLineComment(String lineComment) throws IllegalArgumentException {
    addLineComment(lineComment, null);
  }

   

  public void addLineComment(String lineComment, Object companion) throws IllegalArgumentException {
    addLineComment(lineComment, companion, getParseFlags());
  }

   

  public void addLineComment(String lineComment, Object companion, int flags) throws IllegalArgumentException {
    addSpecialSequence(
      new TokenizerProperty(Token.LINE_COMMENT, new String[] { lineComment }, 
                            companion, flags)
    );
  }  

   

  public void removeLineComment(String lineComment) throws IllegalArgumentException {
    removeSpecialSequence(lineComment);
  }
  
   

  public Object getLineCommentCompanion(String lineComment) throws IllegalArgumentException {
    return getSpecialSequenceCompanion(lineComment);
  }

   

  public boolean lineCommentExists(String lineComment) {
    return getLineComment(lineComment) != null;
  }
  
   

  public TokenizerProperty getLineComment(String lineComment) throws IllegalArgumentException {
    return getSpecialSequence(lineComment);
  }

   

  public Iterator getLineComments() {
    return new SpecialSequencesIterator(this, Token.LINE_COMMENT);
  }
  
   

  public void addBlockComment(String start, String end) throws IllegalArgumentException {
    addBlockComment(start, end, null);
  }
  
   

  public void addBlockComment(String start, String end, Object companion) throws IllegalArgumentException {
    addBlockComment(start, end, companion, getParseFlags());
  }
  
   

  public void addBlockComment(String start, String end, Object companion, int flags) throws IllegalArgumentException {
    addSpecialSequence(
      new TokenizerProperty(Token.BLOCK_COMMENT, new String[] { start, end }, 
                            companion, flags)
    );
  }
  
   

  public void removeBlockComment(String start) throws IllegalArgumentException {
    removeSpecialSequence(start);
  }
  
   

  public Object getBlockCommentCompanion(String start) throws IllegalArgumentException {
    return getSpecialSequenceCompanion(start);
  }
  
   

  public Iterator getBlockComments() {
    return new SpecialSequencesIterator(this, Token.BLOCK_COMMENT);
  }
  
   

  public boolean blockCommentExists(String start) {
    return getBlockComment(start) != null;
  }
  
   

  public TokenizerProperty getBlockComment(String start) throws IllegalArgumentException {
    return getSpecialSequence(start);
  }

   

  public void addSpecialSequence(String specSeq) throws IllegalArgumentException {
    addSpecialSequence(specSeq, null);
  }
  
   

  public void addSpecialSequence(String specSeq, Object companion) throws IllegalArgumentException {
    addSpecialSequence(specSeq, companion, getParseFlags());
  }

   

  public void addSpecialSequence(String specSeq, Object companion, int flags) throws IllegalArgumentException {
    addSpecialSequence(
      new TokenizerProperty(Token.SPECIAL_SEQUENCE, new String[] { specSeq }, 
                            companion, flags)
    );
  }  
    

   

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
  
   

  public Object getSpecialSequenceCompanion(String specSeq) throws IllegalArgumentException {
     
    checkArgument(specSeq, "Special sequence");
    
     
    TokenizerProperty prop = searchBinary(specSeq);

    if (prop != null) {
      return prop.getCompanion();
    } else {
      return null;
    }
  }
  
   

  public Iterator getSpecialSequences() {
    return new SpecialSequencesIterator(this, Token.SPECIAL_SEQUENCE);
  }
  
   

  public boolean specialSequenceExists(String specSeq) {
    return getSpecialSequence(specSeq) != null;
  }
  
   

  public TokenizerProperty getSpecialSequence(String specSeq) throws IllegalArgumentException {
    if (specSeq != null) {
      return searchBinary(specSeq);
    } else {
      return null;
    }
  }

   

  public void addKeyword(String keyword) throws IllegalArgumentException {
    addKeyword(keyword, null);
  }
  
   

  public void addKeyword(String keyword, Object companion) throws IllegalArgumentException {
    addKeyword(keyword, companion, getParseFlags());
  }
  
   

  public void addKeyword(String keyword, Object companion, int flags) throws IllegalArgumentException {
     
    checkArgument(keyword, "Keyword");
    
     
    flags = normalizeFlags(flags);

     
     
     
     
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
  
   

  public void removeKeyword(String keyword) throws IllegalArgumentException {
     
    checkArgument(keyword, "Keyword");

     
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
  
   

  public Object getKeywordCompanion(String keyword) throws IllegalArgumentException {
    TokenizerProperty prop = getKeyword(keyword);
    
    if (prop != null) {
      return prop.getCompanion();
    } else {
      return null;
    }
  }

   

  public Iterator getKeywords() {
    synchronized(_keywords) {
      return new MapIterator(this, _keywords[0], _keywords[1]);
    }
  }
  
   

  public boolean keywordExists(String keyword) {
    try {
      return getKeyword(keyword) != null;
    } catch (IllegalArgumentException ex) {
      return false;
    }
  }

   

  public TokenizerProperty getKeyword(String keyword) throws IllegalArgumentException {
     
    checkArgument(keyword, "Keyword");

     
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
  
   
   
   
  
   

  public void addPattern(String pattern) throws IllegalArgumentException {
    addPattern(pattern, null);
  }

   

  public void addPattern(String pattern, Object companion) throws IllegalArgumentException {
    addPattern(pattern, companion, getParseFlags());
  }

   

  public void addPattern(String pattern, Object companion, int flags)
    throws IllegalArgumentException
  {
     
    checkArgument(pattern, "Pattern");
    
     
    flags = normalizeFlags(flags);

     
    PatternData       data = null;
    TokenizerProperty prop = new TokenizerProperty(Token.PATTERN, new String[] { pattern }, companion, flags);
    
    try {
      data = new PatternData(prop);
    } catch (Exception ex) {
      throw new ExtIllegalArgumentException(ex);
    }
                                                      
     
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
                                                      
       
      _patterns.add(data);
      notifyListeners(new TokenizerPropertyEvent(TokenizerPropertyEvent.PROPERTY_ADDED, data.getProperty()));
    }
  }
  
   

  public void removePattern(String pattern) throws IllegalArgumentException {
     
    checkArgument(pattern, "Pattern");

     
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
  
   

  public Object getPatternCompanion(String pattern) throws IllegalArgumentException {
    TokenizerProperty prop = getPattern(pattern);
    
    if (prop != null) {
      return prop.getCompanion();
    } else {
      return null;
    }
  }
  
   

  public boolean patternExists(String pattern) {
    try {
      return getPattern(pattern) != null;
    } catch (IllegalArgumentException ex) {
      return false;
    }
  }
  
   

  public TokenizerProperty getPattern(String pattern) throws IllegalArgumentException {
     
    checkArgument(pattern, "Pattern");

     
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

   

  public Iterator getPatterns() {
    return new PatternIterator(this);
  }
  
   

  public void addProperty(TokenizerProperty property) throws IllegalArgumentException {
     
    checkPropertyArgument(property);
    
     
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
  
   

  public void removeProperty(TokenizerProperty property) throws IllegalArgumentException {
     
    checkPropertyArgument(property);
    
     
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
  
   

  public Iterator getProperties() {
    return new FullIterator(this);
  }
  
   

  public boolean propertyExists(TokenizerProperty property) {
     
    if (property == null || property.getImages() == null || property.getImages()[0] == null) {
      return false;
    }
    
     
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

   

  public void addTokenizerPropertyListener(TokenizerPropertyListener listener) {
    if (listener != null) {
      synchronized(_listeners) {
        _listeners.add(listener);
      }
    }
  }
  
   

  public void removeTokenizerPropertyListener(TokenizerPropertyListener listener) {
    if (listener != null) {
      synchronized(_listeners) {
        _listeners.remove(listener);
      }
    }
  }
 
  
   
   
   
  
   

  public void setTokenizerProperties(TokenizerProperties props) 
    throws UnsupportedOperationException, NullPointerException
  {
    throw new ExtUnsupportedOperationException(
                  "Class {0} already defines the {1} interface.",
                  new Object[] { StandardTokenizerProperties.class.getName(), 
                                 DataMapper.class.getName() } );
  }

   

  public TokenizerProperties getTokenizerProperties() {
    return this;
  }

   

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
  
 
   

  public boolean newlineIsWhitespace() {
    synchronized(_syncWhitespaces) {
      return _newlineIsWhitespace;
    }
  }  
  
   

  public boolean isSeparator(char testChar) {
    synchronized(_syncSeparators) {
      return   indexInSet          (testChar, _separatorsCase)   >= 0 
            || indexInSetIgnoreCase(testChar, _separatorsNoCase) >= 0;
    }
  }

   

  public TokenizerProperty startsWithSequenceCommentOrString(DataProvider dataProvider) 
    throws TokenizerException, NullPointerException
  {
     
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

   

  public int getSequenceMaxLength() {
    return _sequenceMaxLength;
  }

   

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

   
   
   

   

  public int normalizeFlags(int flags) {
    if ((flags & (F_CASE | F_NO_CASE)) == 0) {
       
      flags |= F_CASE;
    } else if ((flags & F_CASE) != 0) {
       
      flags &= ~F_NO_CASE;
    }
    return flags;
  }
    
   

  private void checkArgument(String arg, String name) throws IllegalArgumentException {
    if (arg == null) {
      throw new ExtIllegalArgumentException("{0} is null.", new Object[] { name } );
    } else if (arg.length() <= 0) {
      throw new ExtIllegalArgumentException("{0} is empty.", new Object[] { name } );
    }
  }
  
   

  private void checkPropertyArgument(TokenizerProperty property) throws IllegalArgumentException {
     
    if (property == null) {
      throw new ExtIllegalArgumentException("Property is null.", null );
    } else if (property.getImages() == null) {
      throw new ExtIllegalArgumentException("No image(s) given in property.", null );
    } else if (property.getImages()[0] == null) {
      throw new ExtIllegalArgumentException("No (leading) image given in property.", null );
    }
  }

   

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
  
   

  protected void addSpecialSequence(TokenizerProperty property) throws IllegalArgumentException {
     
    checkPropertyArgument(property);
    
     
    String[] images = property.getImages();
      
    switch (property.getType()) {
    case Token.STRING:
    case Token.BLOCK_COMMENT:
      checkArgument((images.length < 2) ? null : images[1], "End sequence");
      break;
    }
    
     
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
      
       
      TokenizerProperty oldProp = _sequences[arrayIdx].addSpecialSequence(property);
      
       
      if (images[0].length() > _sequenceMaxLength) {
        _sequenceMaxLength = images[0].length();
      }
      
       
      if (oldProp == null) {
        notifyListeners(new TokenizerPropertyEvent(TokenizerPropertyEvent.PROPERTY_ADDED, property));
      } else if ( ! oldProp.equals(property)) {
        notifyListeners(new TokenizerPropertyEvent(TokenizerPropertyEvent.PROPERTY_MODIFIED, property, oldProp));
      }
    }
  }
  
   

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
  
   

  protected int indexInSetIgnoreCase(char ch, String set) {
    if (set != null && set.length() > 0) {
      return indexInSet(Character.toUpperCase(ch), set);
    } else {
      return -1;
    }
  }
  
   

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
         
        
      default:
        if (ch == setChar) {
          return ii;
        }
      }
    }
    
     
    return -1;
  }
  
   

  protected void notifyListeners(TokenizerPropertyEvent event) {
    synchronized(_listeners) {
      for (int index = 0; index < _listeners.size(); index++) {
        TokenizerPropertyListener listener = (TokenizerPropertyListener)_listeners.get(index);

        listener.propertyChanged(event);
      }
    }
  }
  
   
   
   
  
   

  protected int _flags = 0;
  
   

  protected String _whitespacesCase = DEFAULT_WHITESPACES;
  
   

  protected String _whitespacesNoCase = "";
  
   

  protected String _separatorsCase = DEFAULT_SEPARATORS;
  
   

  protected String _separatorsNoCase = "";
  
   

  protected SortedArray[] _sequences = new SortedArray[2];
  
   

  protected HashMap[] _keywords = new HashMap[2];
  
   

  protected ArrayList _patterns = new ArrayList();
  
   

  protected boolean _newlineIsWhitespace  = false;
  
   

  private boolean _defaultWhitespaces = false;
  
   

  private int _sequenceMaxLength = 0;
  
   

  private ArrayList _listeners = new ArrayList();
  
   

  private Class _patternClass = null;

   

  private Object _syncWhitespaces = new Object();
  
   

  private Object _syncSeparators = new Object();
}

 
 
 

 

final class SortedArray {

   

  public SortedArray(StandardTokenizerProperties parent, int flags) {
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
      String            exSeq    = existing.getImages()[0];

      if ((_flags & TokenizerProperties.F_NO_CASE) != 0) {
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

   

  protected TokenizerProperty searchBinary(String specSeq, DataProvider provider, boolean remove) {
     
    if (_array == null) {
      return null;
    }

     
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

     
    SpecialSequence   listElem   = (SpecialSequence)_array.get(nearestMatch[0]);
    TokenizerProperty existing   = listElem._property;
    String            exSeq      = existing.getImages()[0];

    if (fromStream) {
      int len = exSeq.length();
      specSeq = new String(data, startPos, (maxLength < len) ? maxLength : len);
    }

     
    SpecialSequence prevElem = null;

    while (listElem != null) {
      existing = listElem._property;
      exSeq    = existing.getImages()[0];

       
       
       
      if (specSeq.length() > exSeq.length()) {
        if (fromStream) {
          specSeq = specSeq.substring(0, exSeq.length());
        } else {
          return null;
        }
      }

       
      if (specSeq.length() == exSeq.length()) {
        int res;

        if ((_flags & TokenizerProperties.F_NO_CASE) != 0) {
          res = exSeq.compareToIgnoreCase(specSeq);
        } else {
          res = exSeq.compareTo(specSeq); 
        }
        
         
        if (res == 0) {
          TokenizerProperty prop = listElem._property;
          
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
          }
          return prop;
          
         
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

     
    return null;
  }

   

  public TokenizerProperty addSpecialSequence(TokenizerProperty prop) {
     
    if (_array == null) {
      _array = new ArrayList();
    }

     
    String  seq           = prop.getImages()[0];
    int[]   nearestMatch  = { -1 };
    int     res           = searchIndex(seq.charAt(0), nearestMatch);

     
     
    int idx = nearestMatch[0];

    if (res < 0) {
      idx++;
    }

     
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

   
  private StandardTokenizerProperties _parent = null;
  private ArrayList                   _array  = null;
  int                                 _flags  = 0;
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
  
 

final class FullIterator implements Iterator {
  
   

  public FullIterator(StandardTokenizerProperties parent) {
    _parent = parent;
    
     
    _iterators    = new Object[3];
    synchronized(_parent._keywords) {
      _iterators[0] = new MapIterator(_parent, _parent._keywords[0], _parent._keywords[1]);
    }
    _iterators[1] = new PatternIterator(_parent);
    _iterators[2] = new SpecialSequencesIterator(parent, 0);
    _currIndex    = 0;
  }

   

  public boolean hasNext() {
#ifdef FAULT_15
#else
    synchronized(this) {
#endif
      while (_currIndex < _iterators.length) {
        Iterator iter = (Iterator)_iterators[_currIndex];

        if (iter.hasNext()) {
          return true;
        }
        _currIndex++;
      }
      return false;
#ifdef FAULT_15
#else
    }
#endif
  }
  
   

  public Object next() {
    if (hasNext()) {
#ifdef FAULT_15
#else
      synchronized(this) {
#endif
        Iterator iter = (Iterator)_iterators[_currIndex];
        return iter.next();
#ifdef FAULT_15
#else
      }
#endif
    } else {
      return null;
    }
  }
  
   

  public void remove() {
    if (_currIndex < _iterators.length) {
      Iterator iter = (Iterator)_iterators[_currIndex];
      iter.remove();
    }
  }
  
   
  private StandardTokenizerProperties _parent     = null;
  private Object[]                    _iterators  = null;
  private int                         _currIndex  = -1;
}

 

final class MapIterator implements Iterator {

   

  public MapIterator(StandardTokenizerProperties parent, Map caseSensitiveMap, Map caseInsensitiveMap) {
#ifdef FAULT_15
    System.out.println("==========FAULT_15============");
#else
    synchronized(this) {
#endif
      _parent = parent;
      if (caseSensitiveMap != null) {
        _iterators[0] = caseSensitiveMap.values().iterator();
      }
      if (caseInsensitiveMap != null) {
        _iterators[1] = caseInsensitiveMap.values().iterator();
      }
#ifdef FAULT_15
#else
    }
#endif
  }

   

  public boolean hasNext() {
     
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

   

  public Object next() {
    if (hasNext()) {
#ifdef FAULT_15
#else
      synchronized(this) {
#endif
        if (_iterators[0] != null) {
          _currData = (TokenizerProperty)_iterators[0].next();
        } else {
          _currData = (TokenizerProperty)_iterators[1].next();
        }
        return _currData;
#ifdef FAULT_15
#else
      }
#endif
    }
    return null;
  }
  
   

  public void remove() {
#ifdef FAULT_15
#else
    synchronized(this) {
#endif
      if (_iterators[0] != null) {
        _iterators[0].remove();
      } else {
        _iterators[1].remove();
      }
      _parent.notifyListeners(new TokenizerPropertyEvent(TokenizerPropertyEvent.PROPERTY_REMOVED, _currData));
#ifdef FAULT_15
#else
    }
#endif
  }

   
  private StandardTokenizerProperties _parent     = null;
  private Iterator[]                  _iterators  = new Iterator[2];
  private TokenizerProperty           _currData   = null;
}

 

final class SpecialSequencesIterator implements Iterator {

   

  public SpecialSequencesIterator(StandardTokenizerProperties parent, int type) {
    _type      = type;
    _parent    = parent;
    synchronized(parent._sequences) {
      _arrays[0] = parent._sequences[0];
      _arrays[1] = parent._sequences[1];
    }
  }

   

  private boolean listHasNext() {
    while (_nextElem != null) {
      if (_type == 0 || _nextElem._property.getType() == _type) {
        return true;
      }
      _nextElem = _nextElem._next;
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
        _nextElem = array.get(_currentIndex);
        if (listHasNext()) {
          return true;
        }
      }

       
      if (array == _arrays[0]) {
        _arrays[0]    = null;
        _nextElem     = null;
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
      _currentArray = (_arrays[0] != null) ? _arrays[0] : _arrays[1];
      _currentElem  = _nextElem; 
      _nextElem     = _nextElem._next;
      hasNext();     
      return _currentElem._property;
    }
  }
  
   

  public void remove() throws IllegalStateException {
     
    if (_currentElem == null) {
      throw new IllegalStateException();
    }
    
     
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

   
  private StandardTokenizerProperties _parent       = null;
  private SortedArray[]               _arrays       = new SortedArray[2];
  private SortedArray                 _currentArray = null;
  private SpecialSequence             _currentElem  = null;
  private SpecialSequence             _nextElem     = null;
  private int                         _currentIndex = -1;
  private int                         _type         = Token.UNKNOWN;
}

 

final class PatternData implements PatternHandler {
  
   

 
  public PatternData(TokenizerProperty prop) 
    throws UnsupportedOperationException, InstantiationException, IllegalAccessException, InvocationTargetException
  {
     
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
    
     
    setProperty(prop);
  }
  
   

  public void setProperty(TokenizerProperty prop)   
    throws InstantiationException, IllegalAccessException, InvocationTargetException
  {
     
    String    regexp  = prop.getImages()[0];
    int       flags   = ((prop.getFlags() & TokenizerProperties.F_NO_CASE) != 0) ? _noCaseFlag : 0;
    Object[]  args    = new Object[] { regexp, new Integer(flags) };
    
    _pattern  = _compilerMethod.invoke(null, args);
    
     
    _property = prop;
  }
  
   

  public TokenizerProperty getProperty() {
    return _property;
  }
  
   

  public int matches(DataProvider dataProvider, TokenizerProperty property)
    throws TokenizerException, NullPointerException
  {
     
    if (_patternClass == null) {
      return NO_MATCH;
    }
    
     
    String input = new String(dataProvider.getData(), dataProvider.getStartPosition(), dataProvider.getLength());
    
     
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

   
  private static Object     _syncObject     = new Object();
  private static Class      _patternClass   = null;
  private static Class      _matcherClass   = null;
  private static Method     _compilerMethod = null;
  private static Method     _matcherFactory = null;
  private static Method     _matcherMethod  = null;
  private static Method     _endMethod      = null;
  private static int        _noCaseFlag     = 0;
#ifdef FAULT_16
  private static TokenizerProperty _property       = null;
#else
  private TokenizerProperty _property       = null;
#endif
  private Object            _pattern        = null;
}

 

final class PatternIterator implements Iterator {
   

  public PatternIterator(StandardTokenizerProperties parent) {
    _parent   = parent;
    synchronized(parent._patterns) {
      _iterator = parent._patterns.iterator();
    }
  }

   

  public boolean hasNext() {
    return _iterator.hasNext();
  }

   

  public Object next() {
#ifdef FAULT_15
#else
    synchronized(this) {
#endif
      _currData = (PatternData)_iterator.next();

      if (_currData != null) {
        return _currData.getProperty();
      } else {
        return null;
      }
#ifdef FAULT_15
#else
    }
#endif
  }
  
   

  public void remove() {
    synchronized(_iterator) {
      _iterator.remove();
    }
    synchronized(_parent) {
      _parent.notifyListeners(new TokenizerPropertyEvent(TokenizerPropertyEvent.PROPERTY_REMOVED, _currData.getProperty()));
    }
  }

   
  private StandardTokenizerProperties _parent = null;
  private Iterator                    _iterator = null;
  private PatternData                 _currData = null;
}
