package de.susebox.java.lang;

public interface ThrowableList {
  public Throwable nextThrowable();
   
  public boolean isWrapper();

  public String getFormat();

  public Object[] getArguments();

}
