#include "../../FaultSeeds.h" 

package de.susebox.jtopas.spi;

 
 
 
import de.susebox.jtopas.TokenizerProperties;
import de.susebox.jtopas.TokenizerException;

 
 
 

 

public class StandardWhitespaceHandler implements WhitespaceHandler {
  
   

  public StandardWhitespaceHandler(TokenizerProperties props) {
    _properties = props;
  }
  
   

  public boolean isWhitespace(char testChar) {
    String whitespaces;
    
    if (_properties != null && (whitespaces = _properties.getWhitespaces()) != null) {
      if ((_properties.getParseFlags() & TokenizerProperties.F_NO_CASE) != 0) {
        return whitespaces.indexOf(testChar) >= 0;
      } else {
        return whitespaces.toLowerCase().indexOf(Character.toLowerCase(testChar)) >= 0;
      }
    } else {
      return false;
    }
  }
     
   

  public int countLeadingWhitespaces(DataProvider dataProvider) throws NullPointerException {
    int     len      = 0;
    char[]  data     = dataProvider.getData();
    int     start    = dataProvider.getStartPosition();
    int     maxChars = dataProvider.getLength();
    
#ifdef FAULT_17
    while (len <= maxChars) {
#else
    while (len < maxChars) {
#endif
      if ( ! isWhitespace(data[start + len])) {
        break;
      }
      len++;
    }
    return len;
  }

   

  public boolean newlineIsWhitespace() {
    String  whitespaces;
    boolean isWhitespace;
    
    if (_properties != null && (whitespaces = _properties.getWhitespaces()) != null) {
      return newlineIsWhitespace(whitespaces);
    } else {
      return false;
    }
  }  
  
   
   
   
  
   

  private boolean newlineIsWhitespace(String set) {
    int  len = (set != null) ? set.length() : 0;
    char start, end, setChar;
    
    for (int ii = 0; ii < len; ++ii)  {
      switch (setChar = set.charAt(ii)) {
      case '-':
        start = (ii > 0) ? set.charAt(ii - 1) : 0;
        end   = (ii < len - 1) ? set.charAt(ii + 1) : 0xFFFF;
        if (   ('\n' >= start && '\n' <= end)
            || ('\r' >= start && '\r' <= end)) {
          return true;
        }
        ii += 2; 
        break;
        
      case '\r':
      case '\n':
        return true;
        
      case '\\':
        ii++;
        break;
      }
    }
    
     
    return false;
  }
  
   
   
   
  
   

  private TokenizerProperties _properties = null;
}
