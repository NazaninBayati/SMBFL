 

package de.susebox.jtopas.spi;

 
 
 
import java.util.Iterator;

import de.susebox.jtopas.TokenizerProperty;
import de.susebox.jtopas.TokenizerProperties;
import de.susebox.jtopas.TokenizerException;

 
 
 

 

public class StandardSequenceHandler implements SequenceHandler {
  
   

  public StandardSequenceHandler(TokenizerProperties props) {
    _properties = props;
  }
  
   

  public TokenizerProperty startsWithSequenceCommentOrString(DataProvider dataProvider) 
    throws NullPointerException
  {
    TokenizerProperty prop = null;
    
    if (_properties != null) {
      String data = dataProvider.toString();

      prop = getLongestMatch(data, _properties.getSpecialSequences(), prop);
      prop = getLongestMatch(data, _properties.getLineComments(),     prop);
      prop = getLongestMatch(data, _properties.getBlockComments(),    prop);
      prop = getLongestMatch(data, _properties.getStrings(),          prop);
    }
    return prop;
  }

   

  public int getSequenceMaxLength() {
    int maxLength = 0;
    
    if (_properties != null) {
      maxLength = getSequenceMaxLength(_properties.getSpecialSequences(), maxLength);
      maxLength = getSequenceMaxLength(_properties.getLineComments(),     maxLength);
      maxLength = getSequenceMaxLength(_properties.getBlockComments(),    maxLength);
      maxLength = getSequenceMaxLength(_properties.getStrings(),          maxLength);
    }
    return maxLength;
  }
    
   

  private int getSequenceMaxLength(Iterator iter, int currentMax) {
    while (iter.hasNext()) {
      TokenizerProperty prop = (TokenizerProperty)iter.next();
      int               len  = prop.getImages()[0].length();

      if (len > currentMax) {
        currentMax = len;
      }
    }
    return currentMax;
  }

   

  private TokenizerProperty getLongestMatch(
    String            data,
    Iterator          iter, 
    TokenizerProperty currentMatch
  ) 
  {
    int               currentMax = (currentMatch != null) ? currentMatch.getImages()[0].length() : 0;
    TokenizerProperty retProp    = currentMatch;
    
    while (iter.hasNext()) {
      TokenizerProperty prop = (TokenizerProperty)iter.next();
      int               len  = prop.getImages()[0].length();

      if (len > currentMax) {
        currentMax = len;
        retProp    = prop;
      }
    }
    return retProp;
  }

   
   
   
  
   

  private TokenizerProperties _properties = null;
}
