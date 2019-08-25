 

package de.susebox.jtopas.spi;

 
 
 
import de.susebox.jtopas.TokenizerException;

 
 
 

 

public interface WhitespaceHandler {
  
   

  public boolean isWhitespace(char testChar);
     
   

  public int countLeadingWhitespaces(DataProvider dataProvider) 
    throws TokenizerException, NullPointerException;
  
   

  public boolean newlineIsWhitespace();
}
