 

package de.susebox.java.util;

 
 
 
import java.io.Reader;
import java.io.InputStreamReader;

 
 
 

 

public class InputStreamTokenizer extends AbstractTokenizer {
  
   
   
   
  
   

  protected int read(char[] cbuf, int offset, int maxChars) throws Exception {
    if (_reader != null) {
      return _reader.read(cbuf, offset, maxChars);
    } else {
      return -1;
    }
  }
  
   
   
   
  
   

  public InputStreamTokenizer() {
    this(null, 0);
  }

   

  public InputStreamTokenizer(Reader reader) {
    this(reader, 0);
  }
  
   

  public InputStreamTokenizer(Reader reader, int flags) {
    super(flags);
    
    setSource((reader != null) ? reader : new InputStreamReader(System.in));
  }
  
   

  public InputStreamTokenizer(Reader reader, int flags, String whitespaces, String separators) {
    super(flags);
    
    setSource((reader != null) ? reader : new InputStreamReader(System.in));
  }
  
   
   
   
  
   

  public void setSource(Reader reader) {
    _reader = reader;
    reset();
  }
  
   

  public Reader getSource() {
    return _reader;
  }
  
   
   
   
  protected Reader _reader = null;
}
