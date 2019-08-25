 

package de.susebox.jtopas;

 
 
 
import de.susebox.java.util.TokenizerProperty;
import de.susebox.java.util.TokenizerException;

 
 
 

 

public interface SequenceHandler extends Plugin {
  
   

  public de.susebox.java.util.TokenizerProperty isSequenceCommentOrString(int startingAtPos, int maxChars)     
    throws de.susebox.java.util.TokenizerException;
  
   

  public int getSequenceMaxLength();
}
