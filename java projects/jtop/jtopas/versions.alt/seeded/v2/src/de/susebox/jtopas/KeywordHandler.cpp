 

package de.susebox.jtopas;

 
 
 
import de.susebox.java.util.TokenizerProperty;
import de.susebox.java.util.TokenizerException;

 
 
 

 

public interface KeywordHandler extends Plugin {
  
   

  public TokenizerProperty isKeyword(int startingAtPos, int length); 
}
