 

package de.susebox.jtopas;

 
 
 
import java.io.Reader;
import java.io.InputStreamReader;

 
 
 

 

public class InputStreamSource implements TokenizerSource {
  
   
   
   
  
   

  public int read(char[] cbuf, int offset, int maxChars) throws Exception {
    if (_reader != null) {
      return _reader.read(cbuf, offset, maxChars);
    } else {
      return -1;
    }
  }

   

  public void setTokenizer(PluginTokenizer tokenizer) {}
  
   
   
   
  
   

  public InputStreamSource() {
    this(null, 0);
  }

   

  public InputStreamSource(Reader reader) {
    this(reader, 0);
  }
  
   

  public InputStreamSource(Reader reader, int flags) {
    setReader((reader != null) ? reader : new InputStreamReader(System.in));
  }
  
   
   
   
  
   

  public void setReader(Reader reader) {
    _reader = reader;
  }
  
   

  public Reader getReader() {
    return _reader;
  }
  
   
   
   
  
   

  protected Reader _reader = null;
}
