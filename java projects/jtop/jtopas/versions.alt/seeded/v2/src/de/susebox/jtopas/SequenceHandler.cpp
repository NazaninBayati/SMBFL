 

package de.susebox.jtopas;

 
 
 
import de.susebox.java.util.TokenizerProperty;
import de.susebox.java.util.TokenizerException;

 
 
 

 

public interface SequenceHandler extends Plugin {
  
   

  public TokenizerProperty isSequenceCommentOrString(int startingAtPos, int maxChars)     
    throws TokenizerException;
  
   

  public int getSequenceMaxLength();
}
