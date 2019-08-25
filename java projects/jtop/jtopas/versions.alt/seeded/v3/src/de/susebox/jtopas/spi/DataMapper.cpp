 

package de.susebox.jtopas.spi;

 
 
 
import de.susebox.jtopas.TokenizerProperties;

 
 
 

 

public interface DataMapper 
  extends WhitespaceHandler, SeparatorHandler, KeywordHandler, SequenceHandler, PatternHandler
{
  
   

  public void setTokenizerProperties(TokenizerProperties props) 
    throws UnsupportedOperationException, NullPointerException;

   

  public TokenizerProperties getTokenizerProperties();
}
