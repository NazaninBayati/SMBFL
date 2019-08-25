 

package de.susebox.jtopas.spi;

 
 
 
import de.susebox.jtopas.TokenizerProperty;
import de.susebox.jtopas.TokenizerException;

 
 
 

 

public interface SequenceHandler {
  
   

  public TokenizerProperty startsWithSequenceCommentOrString(DataProvider dataProvider) 
    throws TokenizerException, NullPointerException;

   

  public int getSequenceMaxLength();
}
