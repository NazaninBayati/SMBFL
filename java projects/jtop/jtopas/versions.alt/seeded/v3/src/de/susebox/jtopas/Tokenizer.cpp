 

package de.susebox.jtopas;

 
 
 
import de.susebox.jtopas.spi.WhitespaceHandler;
import de.susebox.jtopas.spi.SeparatorHandler;
import de.susebox.jtopas.spi.KeywordHandler;
import de.susebox.jtopas.spi.SequenceHandler;
import de.susebox.jtopas.spi.PatternHandler;

 
 
 

 

public interface Tokenizer {

   
   
   
  
   

  public void setSource(TokenizerSource source);
  
   

  public TokenizerSource getSource();
  
   
   
   
  
   

  public void setTokenizerProperties(TokenizerProperties props) throws NullPointerException, IllegalArgumentException;
  
   

  public TokenizerProperties getTokenizerProperties();
  
   

  public void changeParseFlags(int flags, int mask) throws TokenizerException;

    

  public int getParseFlags();
  
   

  public void setKeywordHandler(de.susebox.jtopas.spi.KeywordHandler handler);
  
    

  public de.susebox.jtopas.spi.KeywordHandler getKeywordHandler();
  
   

  public void setWhitespaceHandler(de.susebox.jtopas.spi.WhitespaceHandler handler);
  
   

  public de.susebox.jtopas.spi.WhitespaceHandler getWhitespaceHandler();
  
   

  public void setSeparatorHandler(de.susebox.jtopas.spi.SeparatorHandler handler);
  
   

  public de.susebox.jtopas.spi.SeparatorHandler getSeparatorHandler();
  
   

  public void setSequenceHandler(de.susebox.jtopas.spi.SequenceHandler handler);
  
   

  public de.susebox.jtopas.spi.SequenceHandler getSequenceHandler();
  
   

  public void setPatternHandler(de.susebox.jtopas.spi.PatternHandler handler);
  
   

  public de.susebox.jtopas.spi.PatternHandler getPatternHandler();
  
   
   
   

   

  public boolean hasMoreToken();
  
   

  public Token nextToken() throws TokenizerException;
 
   

  public String nextImage() throws TokenizerException;
 
   

  public Token currentToken();
 
   

  public String currentImage();

   
   
   
  
   

  public int getLineNumber();
  
   

  public int getColumnNumber();
  
   
   
   
  
   

  public int getRangeStart();
  
   

  public int getReadPosition();
  
   

  public int currentlyAvailable();
  
   

  public String getText(int start, int length) throws IndexOutOfBoundsException;
  
   

  public char getChar(int pos) throws IndexOutOfBoundsException;
  
   

  public int readMore() throws TokenizerException;
  
   

  public void setReadPositionAbsolute(int position) throws IndexOutOfBoundsException;
  
   

  public void setReadPositionRelative(int offset) throws IndexOutOfBoundsException;
}
