 

package de.susebox.jtopas.spi;

 
 
 
import de.susebox.jtopas.Tokenizer;

 
 
 

 

public interface DataProvider {
  
   

  public char[] getData();
  
   

  public char[] getDataCopy();
     
   

  public int getStartPosition(); 

   

  public int getLength();
}
