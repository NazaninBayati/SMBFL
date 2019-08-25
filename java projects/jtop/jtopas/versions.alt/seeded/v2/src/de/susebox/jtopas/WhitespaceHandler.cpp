 

package de.susebox.jtopas;

 
 
 
import de.susebox.java.util.TokenizerException;

 
 
 

 

public interface WhitespaceHandler extends Plugin {
  
   

  public boolean isWhitespace(char testChar);

   

  public int readWhitespaces(int startingAtPos, int maxChars) throws TokenizerException;
}
