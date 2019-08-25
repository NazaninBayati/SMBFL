 

package de.susebox.jtopas;

 
 
 

 

public interface TokenizerSource {
  
   

  int read(char[] cbuf, int offset, int maxChars) throws Exception;
}
