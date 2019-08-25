 

package de.susebox.jtopas;

 
 
 
import de.susebox.java.util.Tokenizer;
import de.susebox.java.util.TokenizerProperty;
import de.susebox.java.util.TokenizerException;
import de.susebox.java.util.AbstractTokenizer;

 
 
 

 

public class PluginTokenizer extends AbstractTokenizer {
  
   
   
   
  
   

  protected int read(char[] cbuf, int offset, int maxChars) throws Exception {
    if (_source != null) {
      return _source.read(cbuf, offset, maxChars);
    } else {
      return -1;
    }
  }
  
   
   
   
  
   

  public PluginTokenizer() {
    this(null, 0);
  }

   

  public PluginTokenizer(TokenizerSource dataSource) {
    this(dataSource, 0);
  }
  
   

  public PluginTokenizer(TokenizerSource dataSource, int flags) {
    super(flags);
    
    setSource((dataSource != null) ? dataSource: new InputStreamSource());
  }
  
   
   
   
  
   

  public void setSource(TokenizerSource dataSource) {
    if ((_source = dataSource) != null) {
      _source.setTokenizer(this);
    }
    reset();
  }
  
   

  public TokenizerSource getSource() {
    return _source;
  }
  
   

  public void setKeywordHandler(KeywordHandler handler) {
    if ((_keywordHandler = handler) != null) {
      _keywordHandler.setTokenizer(this);
    }
  }
  
   

  public KeywordHandler getKeywordHandler() {
    return _keywordHandler;
  }
  
   

  public void setWhitespaceHandler(WhitespaceHandler handler) {
    if ((_whitespaceHandler = handler) != null) {
      _whitespaceHandler.setTokenizer(this);
    }
  }
  
   

  public WhitespaceHandler getWhitespaceHandler() {
    return _whitespaceHandler;
  }
  
   

  public void setSeparatorHandler(SeparatorHandler handler) {
    if ((_separatorHandler = handler) != null) {
      _separatorHandler.setTokenizer(this);
    }
  }
  
   

  public SeparatorHandler getSeparatorHandler() {
    return _separatorHandler;
  }
  
   

  public void setSequenceHandler(SequenceHandler handler) {
    if ((_sequenceHandler = handler) != null) {
      _sequenceHandler.setTokenizer(this);
    }
  }
  
   

  public SequenceHandler getSequenceHandler() {
    return _sequenceHandler;
  }
  
   
   
   
  
   

  protected TokenizerProperty isKeyword(int startingAtPos, int length) {
    if (_keywordHandler != null) {
      return _keywordHandler.isKeyword(startingAtPos, length);
    } else {
      return super.isKeyword(startingAtPos, length);
    }
  }
    
  
   

  protected boolean isWhitespace(char testChar) {
    if (_whitespaceHandler != null) {
      return _whitespaceHandler.isWhitespace(testChar);
    } else {
      return super.isWhitespace(testChar);
    }
  }
  
   

  protected int readWhitespaces(int startingAtPos, int maxChars) 
    throws TokenizerException 
  {
    if (_whitespaceHandler != null) {
      return _whitespaceHandler.readWhitespaces(startingAtPos, maxChars);
    } else {
      return super.readWhitespaces(startingAtPos, maxChars);
    }
  }
  
   

  protected boolean isSeparator(char testChar) {
    if (_separatorHandler != null) {
      return _separatorHandler.isSeparator(testChar);
    } else {
      return super.isSeparator(testChar);
    }
  }

   

  protected TokenizerProperty isSequenceCommentOrString(int startingAtPos) throws TokenizerException {
    if (_sequenceHandler != null) {
      int available = currentlyAvailable() - (startingAtPos - getRangeStart());
      int maxLength = _sequenceHandler.getSequenceMaxLength();
      
      if (maxLength > 0 && available < maxLength) {
        readMore();
        available = currentlyAvailable() - (startingAtPos - getRangeStart());
      }
      return _sequenceHandler.isSequenceCommentOrString(startingAtPos, available);
    } else {
      return super.isSequenceCommentOrString(startingAtPos);
    }
  }

    
   
   
   
  private TokenizerSource   _source            = null;
  private WhitespaceHandler _whitespaceHandler = null;
  private SeparatorHandler  _separatorHandler  = null;
  private SequenceHandler   _sequenceHandler   = null;
  private KeywordHandler    _keywordHandler    = null;
}
