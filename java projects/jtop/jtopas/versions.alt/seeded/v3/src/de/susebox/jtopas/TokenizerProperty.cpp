 

package de.susebox.jtopas;

 
 
 

 
 
 

 

public class TokenizerProperty {
  
   
   
   
  
   

  public static final byte PARSE_FLAG_MASK = 127;
  
   
   
   
  
   

  public void setType(int type) {
    _type = type;
  }
    
   

  public int getType() {
    return _type;
  }

   

  public void setFlags(int flags) {
    _flags = flags;
  }
    
   

  public int getFlags() {
    return _flags;
  }

   

  public void setImages(String[] images) throws IllegalArgumentException {
    _images = images;
  }
    
   

  public String[] getImages() {
    return _images;
  }
    
   

  public void setCompanion(Object companion) {
    _companion = companion;
  }
    
   

  public Object getCompanion() {
    return _companion;
  }
  
 
   

  public boolean isCaseSensitive() {
    return (getFlags() & TokenizerProperties.F_CASE) != 0;
  }
   
   
   
   
   
  
   

  public TokenizerProperty() {
    this(Token.UNKNOWN, null, null);
  }
  
   

  public TokenizerProperty(int type) {
    this(type, null, null);
  }
  
   

  public TokenizerProperty(int type, String[] values) {
    this(type, values, null);
  }
  
   

  public TokenizerProperty(int type, String[] values, Object companion) {
    this(type, values, companion, 0);
  }
  
   

  public TokenizerProperty(int type, String[] images, Object companion, int flags) {
    setType(type);
    setImages(images);
    setCompanion(companion);
    setFlags(flags);
  }
  
   
   
   
  
   

  public boolean equals(Object that) {
     
    if (that == null) {
      return false;
    } else if (that == this) {
      return true;
    } else if ( ! (that.getClass() == getClass())) {
      return false;
    }
    
     
    TokenizerProperty thatProp = (TokenizerProperty)that;

    if (   getType()      == thatProp.getType()
        && getCompanion() == thatProp.getCompanion()
        && getFlags()     == thatProp.getFlags()) {
       
      String[] thisImg = getImages();
      String[] thatImg = thatProp.getImages();
      
      if (thisImg != thatImg) {
        if (thisImg == null || thatImg == null || thisImg.length != thatImg.length) {
          return false;
        }
        for (int index = 0; index < thisImg.length; ++index) {
          if (   (   isCaseSensitive() && ! thisImg[index].equals(thatImg[index]))
              || ( ! isCaseSensitive() && ! thisImg[index].equalsIgnoreCase(thatImg[index]))) {
            return false;
          }
        }
      }
      return true;
    } else {
      return false;
    }
  }
  
   

  public String toString() {
    StringBuffer  buffer = new StringBuffer();
    
    buffer.append(getClass().getName());
    buffer.append(':');

    switch (getType()) {
      case Token.NORMAL:
        buffer.append(" NORMAL, ");
        break;
      case Token.BLOCK_COMMENT:
        buffer.append(" BLOCK_COMMENT, ");
        break;
      case Token.LINE_COMMENT:
        buffer.append(" LINE_COMMENT, ");
        break;
      case Token.STRING:
        buffer.append(" STRING, ");
        break;
      case Token.PATTERN:
        buffer.append(" PATTERN, ");
        break;
      case Token.KEYWORD:
        buffer.append(" KEYWORD, ");
        break;
      case Token.WHITESPACE:
        buffer.append(" WHITESPACE, ");
        break;
      case Token.SEPARATOR:
        buffer.append(" SEPARATOR, ");
        break;
      case Token.SPECIAL_SEQUENCE:
        buffer.append(" SPECIAL_SEQUENCE, ");
        break;
      case Token.EOF:
        buffer.append(" EOF, ");
        break;
      case TokenizerProperty.PARSE_FLAG_MASK:
        buffer.append(" PARSE FLAG MASK, ");
        break;
      default:
        buffer.append(" UNKNOWN, ");
    }
    
     
    if (isCaseSensitive()) {
      buffer.append("case-sensitive:");
    } else {
      buffer.append("case-insensitive:");
    }
    
     
    if (_images != null) {
      for (int index = 0; index < _images.length; ++index) {
        if (_images[index] != null) {
          buffer.append(' ');
          buffer.append(_images[index]);
        } else {
          break;
        }
      }
    }
    
     
    return buffer.toString();
  }
  
   
   
   
  
   

  private boolean isCaseSensitive(int flags, int caseFlag, int noCaseFlag) {
    boolean isCase = (flags & caseFlag) != 0;
    
    if ( ! isCase) {
      isCase = (flags & (TokenizerProperties.F_NO_CASE | noCaseFlag)) == 0;
    }
    return isCase;
  }
   
   
   
   
   
  protected int       _type;
  protected int       _flags;
  protected String[]  _images;
  protected Object    _companion;
}
