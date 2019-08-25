 

package de.susebox.java.util;

 
 
 

 
 
 

 

public class TokenizerProperty {
  
   
   
   
  
   

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

   

  public void setValues(String[] values) {
    _values = values;
  }
    
  public String[] getValues() {
    return _values;
  }
    
   

  public void setCompanion(Object companion) {
    _companion = companion;
  }
    
   

  public Object getCompanion() {
    return _companion;
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
  
   

  public TokenizerProperty(int type, String[] values, Object companion, int flags) {
    setType(type);
    setValues(values);
    setCompanion(companion);
    setFlags(flags);
  }
  
   
   
   
  protected int       _type;
  protected int       _flags;
  protected String[]  _values;
  protected Object    _companion;
}
