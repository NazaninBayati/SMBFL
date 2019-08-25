 

package de.susebox.jtopas;

 
 
 

 
 
 

 

public class TokenizerPropertyEvent {
  
   
   
   
  
   

  public static final byte PROPERTY_ADDED = 1;
  
   

  public static final byte PROPERTY_REMOVED = 2;
  
   

  public static final byte PROPERTY_MODIFIED = 3;
  
   
   
   
  
   

  public TokenizerPropertyEvent() {
    this(0, null);
  }
  
   

  public TokenizerPropertyEvent(int type, TokenizerProperty property) {
    setType(type);
    setProperty(property);
    setOldProperty(null);
  }
  
   

  public TokenizerPropertyEvent(int type, TokenizerProperty property, TokenizerProperty oldProperty) {
    setType(type);
    setProperty(property);
    setOldProperty(null);
  }
  
   
   
   
  
   

  public void setType(int type) {
    _type = type;
  }
  
   

  public int getType() {
    return _type;
  }
  
   

  public void setProperty(TokenizerProperty property) {
    _property = property;
  }
  
   

  public TokenizerProperty getProperty() {
    return _property;
  }

   

  public void setOldProperty(TokenizerProperty property) {
    _oldProperty = property;
  }
  
   

  public TokenizerProperty getOldProperty() {
    return _oldProperty;
  }

   
   
   
  
   

  public boolean equals(Object that) {
     
    if (that == null) {
      return false;
    } else if (that == this) {
      return true;
    } else if ( ! (that instanceof TokenizerPropertyEvent)) {
      return false;
    }
    
     
    TokenizerPropertyEvent thatEvent   = (TokenizerPropertyEvent)that;
    TokenizerProperty      thatProp    = thatEvent.getProperty();
    TokenizerProperty      thatOldProp = thatEvent.getOldProperty();
    
    if (   getType() == thatEvent.getType()
        && (_property == thatProp || (_property != null && _property.equals(thatProp)))
        && (_oldProperty == thatOldProp || (_oldProperty != null && _oldProperty.equals(thatOldProp)))) {
      return true;
    } else {
      return false;
    }
  }       
    
   

  public String toString() {
    StringBuffer  buffer = new StringBuffer();
    
    buffer.append(getClass().getName());
    buffer.append(": ");

    switch (_type) {
    case PROPERTY_ADDED:
      buffer.append("added ");
      break;
    case PROPERTY_REMOVED:
      buffer.append("removed ");
      break;
    case PROPERTY_MODIFIED:
      buffer.append("modified ");
      break;
    default:
      buffer.append("<unknown type> ");
    }
    
    if (getProperty() != null) {
      buffer.append(getProperty().toString());
    } else {
      buffer.append("<no property>");
    }
    return buffer.toString();
  }
  
   
   
   
  private int               _type         = 0;
  private TokenizerProperty _property     = null;
  private TokenizerProperty _oldProperty  = null;
}
