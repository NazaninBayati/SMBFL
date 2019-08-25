 

package de.susebox.java.util;

 
 
 
import java.util.Iterator;

 
 
 

 

public interface Tokenizer
{
   
   
   
  
   

  public static final String DEFAULT_WHITESPACES = " \t\r\n";
  
   

  public static final String DEFAULT_SEPARATORS = "\u0021\u0023-\u002f\u003a-\u0040\u005b-\u005e\u0060\u007b-\u007e";
  
   

  public static final String DEFAULT_BLOCK_COMMENT_START = "/*";

   

  public static final String DEFAULT_BLOCK_COMMENT_END = "*/";
  
   

  public static final String DEFAULT_LINE_COMMENT = "//";
  
   

  public static final String DEFAULT_STRING_START  = "\"";
  
   

  public static final String DEFAULT_STRING_END    = DEFAULT_STRING_START;
  
   

  public static final String DEFAULT_STRING_ESCAPE = "\\";
  
   

  public static final String DEFAULT_CHAR_START  = "'";
  
   

  public static final String DEFAULT_CHAR_END    = DEFAULT_CHAR_START;

   

  public static final String DEFAULT_CHAR_ESCAPE = DEFAULT_STRING_ESCAPE;
  
   
   
   
  
   

  public static final short F_NO_CASE               = 0x0001;

   

  public static final short F_KEYWORDS_CASE         = 0x0002;
  
   

  public static final short F_KEYWORDS_NO_CASE      = 0x0004;
  
   

  public static final short F_RETURN_WHITESPACES    = 0x0008;
  
   

  public static final short F_TOKEN_POS_ONLY        = 0x0010;

   

  public static final short F_KEEP_DATA             = 0x0020;

   

  public static final short F_COUNT_LINES           = 0x0040;

   

  public static final short F_ALLOW_NESTED_COMMENTS = 0x0080;
  
   

  public static final short F_PARSE_NUMBERS         = 0x0100;

   
   
   
  
   

  public void setParseFlags(int flags);

    

  public int getParseFlags();
  
   

  public void addString(String start, String end, String escape);

   

  public void addString(String start, String end, String escape, Object companion);
  
   

  public void addString(
    String start, 
    String end, 
    String escape, 
    Object companion, 
    int    flags
  );
  
   

  public void removeString(String start);
  
   

  public Object getStringCompanion(String start);
  
   

  public boolean stringExists(String start);

   

  public Iterator getStrings();
  
   

  public void setWhitespaces(String whitespaces);
  
   

  public String getWhitespaces();
  
   

  public void setSeparators(String separators);
  
   

  public String getSeparators();
  
   

  public void addLineComment(String lineComment);

   

  public void addLineComment(String lineComment, Object companion);

   

  public void addLineComment(String lineComment, Object companion, int flags);
  
   

  public void removeLineComment(String lineComment);
  
   

  public Object getLineCommentCompanion(String lineComment);

   

  public boolean lineCommentExists(String lineComment);
  
   

  public Iterator getLineComments();
  
   

  public void addBlockComment(String start, String end);
  
   

  public void addBlockComment(String start, String end, Object companion);
  
   

  public void addBlockComment(String start, String end, Object companion, int flags);
  
   

  public void removeBlockComment(String start);
  
   

  public Object getBlockCommentCompanion(String start);
  
   

  public boolean blockCommentExists(String start);
  
   

  public Iterator getBlockComments();
  
   

  public int getCurrentLine();
  
   

  public int getCurrentColumn();
  
   
   
   
  
   

  public void addSpecialSequence(String specSeq);
  
   

  public void addSpecialSequence(String specSeq, Object companion);
  
   

  public void addSpecialSequence(String specSeq, Object companion, int flags);
  
   

  public void removeSpecialSequence(String specSeq);
  
   

  public Object getSpecialSequenceCompanion(String specSeq);

   

  public Iterator getSpecialSequences();
  
   

  public boolean specialSequenceExists(String specSeq);
  
   

  public void addKeyword(String keyword);
  
   

  public void addKeyword(String keyword, Object companion);
  
   

  public void addKeyword(String keyword, Object companion, int flags);
  
   

  public void removeKeyword(String keyword);
  
   

  public Object getKeywordCompanion(String keyword);

   

  public Iterator getKeywords();
  
   

  public boolean keywordExists(String keyword);
  
   
   
   

   

  public boolean hasMoreToken();
  
   

  public Token nextToken() throws TokenizerException;
 
   

  public String next() throws TokenizerException;
 
   

  public Token currentToken();
 
   

  public String current();

   
   
   
  
   

  public int getLineNumber();
  
   

  public int getColumnNumber();
  
   
   
   
  
   

  public int getRangeStart();
  
   

  public int getReadPosition();
  
   

  public int currentlyAvailable();
  
   

  public String getText(int start, int length) throws IndexOutOfBoundsException;
  
   

  public char getChar(int pos) throws IndexOutOfBoundsException;
  
   

  public int readMore() throws TokenizerException;
  
   

  public void skip(int numberOfChars) throws IndexOutOfBoundsException;

   

  public void setReadPositionAbsolute(int position) throws IndexOutOfBoundsException;
  
   

  public void setReadPositionRelative(int offset) throws IndexOutOfBoundsException;
}
