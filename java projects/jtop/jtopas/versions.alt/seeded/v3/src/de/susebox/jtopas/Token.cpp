 

package de.susebox.jtopas;

 
 
 

 

public class Token {
  
   
   
   

   

  public static final byte NORMAL = 0;

   

  public static final byte KEYWORD = 1;

   

  public static final byte STRING = 2;
  
   

  public static final byte PATTERN = 3;

   

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
    
   

  public void setImage(String image) {
    if ((_image = image) == null) {
      _length = 0;
    } else {
      _length = _image.length();
    }
  }
    
   

  public String getImage() {
    return _image;
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
    
   

  public void setEndPosition(int endPosition) {
    setLength(endPosition - _startPosition);
  }
    
   

  public int getEndPosition() {
    return getLength() - getStartPosition();
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
  
   

  public Token(int type, String image) {
    this(type, image, null);
  }
  
   

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
  
   

  public boolean equals(Object object) {
     
    if (object == null) {
      return false;
    } else if (object == this) {
      return true;
    } else if (object.getClass() != getClass()) {
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
    } else if (   (getCompanion() == null && other.getCompanion() != null)
               || (getCompanion() != null && getCompanion().equals(other.getCompanion()))) {
      return false;
    } else if (   (getImage() == null && other.getImage() != null)
               || (getImage() != null && ! getImage().equals(other.getImage()))) {
      return false;
    }
    return true;
  }
  
   

  public String toString() {
    StringBuffer buffer = new StringBuffer();
    
     
    buffer.append("Type ");
    buffer.append(Token.getTypeName(getType()));
    
     
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
  
   
   
   
  
   

  protected int _type;

   

  protected String _image;

   

  protected int _length;

   

  protected Object _companion;

   

  protected int _startPosition;

   

  protected int _startLine;

   

  protected int _startColumn;

   

  protected int _endLine;

   

  protected int _endColumn;
}
