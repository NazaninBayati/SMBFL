 

package de.susebox.jtopas;

 
 
 
import java.util.Iterator;

 
 
 

 

public interface TokenizerProperties {
  
   
   
   
  
   

  public static final String DEFAULT_WHITESPACES = " \t\r\n";
  
   

  public static final String DEFAULT_SEPARATORS = "\u0021\u0023-\u002f\u003a-\u0040\u005b-\u005e\u0060\u007b-\u007e";
  
   

  public static final String DEFAULT_BLOCK_COMMENT_START = "/*";

   

  public static final String DEFAULT_BLOCK_COMMENT_END = "*/";
  
   

  public static final String DEFAULT_LINE_COMMENT = "//";
  
   

  public static final String DEFAULT_STRING_START = "\"";
  
   

  public static final String DEFAULT_STRING_END = DEFAULT_STRING_START;
  
   

  public static final String DEFAULT_STRING_ESCAPE = "\\";
  
   

  public static final String DEFAULT_CHAR_START  = "'";
  
   

  public static final String DEFAULT_CHAR_END = DEFAULT_CHAR_START;

   

  public static final String DEFAULT_CHAR_ESCAPE = DEFAULT_STRING_ESCAPE;
  
   
   
   
  
   

  public static final short F_CASE = 0x0002;

   

  public static final short F_NO_CASE = 0x0001;

   

  public static final short F_RETURN_WHITESPACES    = 0x0008;
  
   

  public static final short F_TOKEN_POS_ONLY        = 0x0010;

   

  public static final short F_KEEP_DATA             = 0x0020;

   

  public static final short F_COUNT_LINES           = 0x0040;

   

  public static final short F_ALLOW_NESTED_COMMENTS = 0x0080;
  
   
   
   
  
   

  public void setParseFlags(int flags);

    

  public int getParseFlags();
  
   
   
   
  
   

  public void setWhitespaces(String whitespaces) 
    throws IllegalArgumentException;
  
   

  public String getWhitespaces();
  
   

  public void setSeparators(String separators) 
    throws IllegalArgumentException;
  
   

  public String getSeparators();
  
   
   
   
  
   

  public void addString(String start, String end, String escape)
    throws IllegalArgumentException, UnsupportedOperationException;

   

  public void addString(String start, String end, String escape, Object companion)
    throws IllegalArgumentException;
  
   

  public void addString(String start, String end, String escape, Object companion, int flags)
    throws IllegalArgumentException, UnsupportedOperationException;
  
   

  public void removeString(String start)
    throws IllegalArgumentException;
  
   

  public Object getStringCompanion(String start)
    throws IllegalArgumentException, UnsupportedOperationException;
  
   

  public boolean stringExists(String start);
  
   

  public TokenizerProperty getString(String start) 
    throws IllegalArgumentException;

   

  public Iterator getStrings();
  
   
   
   
  
   

  public void addLineComment(String lineComment) 
    throws IllegalArgumentException, UnsupportedOperationException;

   

  public void addLineComment(String lineComment, Object companion) 
    throws IllegalArgumentException, UnsupportedOperationException;

   

  public void addLineComment(String lineComment, Object companion, int flags)
   throws IllegalArgumentException, UnsupportedOperationException;
  
   

  public void removeLineComment(String lineComment) 
    throws IllegalArgumentException;
  
   

  public Object getLineCommentCompanion(String lineComment) 
    throws IllegalArgumentException;

   

  public boolean lineCommentExists(String lineComment);
  
   

  public TokenizerProperty getLineComment(String lineComment) 
    throws IllegalArgumentException;

   

  public Iterator getLineComments();
  
   

  public void addBlockComment(String start, String end)
    throws IllegalArgumentException, UnsupportedOperationException;
  
   

  public void addBlockComment(String start, String end, Object companion)
    throws IllegalArgumentException, UnsupportedOperationException;
  
   

  public void addBlockComment(String start, String end, Object companion, int flags)
    throws IllegalArgumentException, UnsupportedOperationException;
  
   

  public void removeBlockComment(String start)
    throws IllegalArgumentException;
  
   

  public Object getBlockCommentCompanion(String start)
    throws IllegalArgumentException;
  
   

  public boolean blockCommentExists(String start);
  
   

  public TokenizerProperty getBlockComment(String start)
    throws IllegalArgumentException;

   

  public Iterator getBlockComments();
  
   
   
   
  
   

  public void addSpecialSequence(String specSeq)
    throws IllegalArgumentException, UnsupportedOperationException;
  
   

  public void addSpecialSequence(String specSeq, Object companion)
    throws IllegalArgumentException, UnsupportedOperationException;
  
   

  public void addSpecialSequence(String specSeq, Object companion, int flags)
    throws IllegalArgumentException, UnsupportedOperationException;
  
   

  public void removeSpecialSequence(String specSeq)
    throws IllegalArgumentException;
  
   

  public Object getSpecialSequenceCompanion(String specSeq)
    throws IllegalArgumentException;

   

  public Iterator getSpecialSequences();
  
   

  public boolean specialSequenceExists(String specSeq);
  
   

  public TokenizerProperty getSpecialSequence(String specSeq)
    throws IllegalArgumentException;

   
   
   
  
   

  public void addKeyword(String keyword)
    throws IllegalArgumentException, UnsupportedOperationException;
  
   

  public void addKeyword(String keyword, Object companion)
    throws IllegalArgumentException, UnsupportedOperationException;
  
   

  public void addKeyword(String keyword, Object companion, int flags)
    throws IllegalArgumentException, UnsupportedOperationException;
  
   

  public void removeKeyword(String keyword)
    throws IllegalArgumentException;
  
   

  public Object getKeywordCompanion(String keyword)
    throws IllegalArgumentException;

   

  public Iterator getKeywords();
  
   

  public boolean keywordExists(String keyword);

   

  public TokenizerProperty getKeyword(String keyword)
    throws IllegalArgumentException;

   
   
   
  
   

  public void addPattern(String pattern) 
    throws IllegalArgumentException, UnsupportedOperationException;

   

  public void addPattern(String pattern, Object companion)
    throws IllegalArgumentException, UnsupportedOperationException;
  
   

  public void addPattern(String pattern, Object companion, int flags)
    throws IllegalArgumentException, UnsupportedOperationException;
  
   

  public void removePattern(String pattern)
    throws IllegalArgumentException;
  
   

  public Object getPatternCompanion(String pattern)
    throws IllegalArgumentException;
  
   

  public boolean patternExists(String pattern);
  
   

  public TokenizerProperty getPattern(String pattern) 
    throws IllegalArgumentException;

   

  public Iterator getPatterns();
  
   
   
   
  
   

  public void addProperty(TokenizerProperty property)
    throws IllegalArgumentException, UnsupportedOperationException;
  
   

  public void removeProperty(TokenizerProperty property)
    throws IllegalArgumentException;
  
   

  public Iterator getProperties();
  
   

  public boolean propertyExists(TokenizerProperty property);

   
   
   
  
   

  public void addTokenizerPropertyListener(TokenizerPropertyListener listener);
  
   

  public void removeTokenizerPropertyListener(TokenizerPropertyListener listener);
}
