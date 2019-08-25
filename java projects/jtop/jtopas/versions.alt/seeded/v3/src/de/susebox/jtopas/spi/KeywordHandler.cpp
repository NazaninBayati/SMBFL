 

package de.susebox.jtopas.spi;

 
 
 
import de.susebox.jtopas.TokenizerProperty;
import de.susebox.jtopas.TokenizerException;

 
 
 

 

public interface KeywordHandler {
  
   

  public TokenizerProperty isKeyword(DataProvider dataProvider)
    throws TokenizerException, NullPointerException;
}
