#include "../../FaultSeeds.h" 

package de.susebox.java.util;

public class Token {
  public static final byte NORMAL = 0;

   

  public static final byte KEYWORD = 1;

   

  public static final byte STRING = 2;
  
   

  public static final byte NUMBER = 3;
  
   

  public static final byte SPECIAL_SEQUENCE = 4;
  
   

  public static final byte SEPARATOR = 5;
  
   

  public static final byte WHITESPACE = 6;

   

  public static final byte LINE_COMMENT = 7;

   

  public static final byte BLOCK_COMMENT = 8;

   

  public static final byte EOF = -1;
  
   

  public static final byte UNKNOWN = -2;
    

   
   
   
  
   

  public void setType(int type) {
    _type = type;
  }
    
   

  public int getType() {
    return _type;
  }
    
   

  public void setToken(String token) {
    if ((_token = token) == null) {
      _length = 0;
    } else {
      _length = _token.length();
    }
  }
    
   

  public String getToken() {
    return _token;
  }
    
   

  public void setLength(int length) {
    _length = length;
  }
    
   

  public int getLength() {
    return _length;
  }
    
   

  public void setCompanion(Object companion) {
    _companion = companion;
  }
    
   

  public Object getCompanion() {
    return _companion;
  }
  
   

  public void setStartPosition(int startPosition) {
    _startPosition = startPosition;
  }
    
   

  public int getStartPosition() {
    return _startPosition;
  }
    
   

  public void setStartLine(int lineno) {
    _startLine = lineno;
  }
    
   

  public int getStartLine() {
    return _startLine;
  }
    
   

  public void setStartColumn(int colno) {
    _startColumn = colno;
  }
    
   

  public int getStartColumn() {
    return _startColumn;
  }
    
   

  public void setEndLine(int lineno) {
    _endLine = lineno;
  }
    
   

  public int getEndLine() {
    return _endLine;
  }
    
   

  public void setEndColumn(int colno) {
    _endColumn = colno;
  }
    
   

  public int getEndColumn() {
    return _endColumn;
  }
    
 
   
   
   
  
   

  public Token() {
    this(UNKNOWN, null, null);
  }
  
   

  public Token(int type) {
    this(type, null, null);
  }
  
   

  public Token(int type, String token) {
    this(type, token, null);
  }
  
   

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
  
   

  public boolean equals(Object object) {
    if (object == null) {
      return false;
    } else if (object == this) {
#ifdef FAULT_9
      return false;
#else
      return true;
#endif
#ifdef FAULT_10
    } else if ( (object instanceof Token)) {
#else
    } else if ( ! (object instanceof Token)) {
#endif
      return false;
    }
    
     
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
#ifdef FAULT_11
    } else if (   (getCompanion() == null || other.getCompanion() != null)
               && (getCompanion() != null || getCompanion().equals(other.getCompanion()))) {
#else
    } else if (   (getCompanion() == null && other.getCompanion() != null)
               || (getCompanion() != null && getCompanion().equals(other.getCompanion()))) {
#endif
      return false;
#ifdef FAULT_12
    } else if (   (getToken() == null || other.getToken() != null)
               && (getToken() != null || ! getToken().equals(other.getToken()))) {
#else
    } else if (   (getToken() == null && other.getToken() != null)
               || (getToken() != null && ! getToken().equals(other.getToken()))) {
#endif
      return false;
    }
    return true;
  }
  
   
   
   
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
