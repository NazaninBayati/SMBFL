 

package de.susebox.jtopas.spi;

 
 
 
import de.susebox.jtopas.TokenizerProperty;
import de.susebox.jtopas.TokenizerProperties;

 
 
 

 

public class StandardKeywordHandler implements KeywordHandler {
  
   

  public StandardKeywordHandler(TokenizerProperties props) {
    _properties = props;
  }
  
   

  public TokenizerProperty isKeyword(DataProvider dataProvider) throws NullPointerException {
    if (_properties != null) {
      String keyword = new String(dataProvider.getData(), dataProvider.getStartPosition(), dataProvider.getLength());
      return _properties.getKeyword(keyword);
    } else {
      return null;
    }
  }

   
   
   
  
   

  private TokenizerProperties _properties = null;
}
