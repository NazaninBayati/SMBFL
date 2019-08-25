 

package de.susebox.jtopas;

 
 
 

 

public interface TokenizerSource extends Plugin {
  
   

  int read(char[] cbuf, int offset, int maxChars) throws Exception;
}
