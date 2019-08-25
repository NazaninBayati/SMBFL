 

package de.susebox.jtopas.spi;

 
 
 
import de.susebox.jtopas.TokenizerProperty;
import de.susebox.jtopas.TokenizerException;

 
 
 

 

public interface PatternHandler {
  
   

  public static final byte NO_MATCH = -1;
  
   

  public static final byte COMPLETE_MATCH = 0;
  
   

  public static final byte MATCH = 1;
  
   

  public static final byte INCOMPLETE_MATCH = 2;
  
   

  public int matches(DataProvider dataProvider, TokenizerProperty property)
    throws TokenizerException, NullPointerException;
}
