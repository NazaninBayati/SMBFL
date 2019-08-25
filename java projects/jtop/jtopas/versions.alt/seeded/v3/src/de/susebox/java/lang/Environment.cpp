 

package de.susebox.java.lang;

 
 
 
import java.io.InputStream;
import java.io.PrintStream;

 
 
 

 

public interface Environment {
  
   

  public InputStream in() throws UnsupportedOperationException;
   

   

  public PrintStream out() throws UnsupportedOperationException;
   

   

  public PrintStream err() throws UnsupportedOperationException;
   

   

  public void setExitStatus(int status);
   

   

  public int getExitStatus();
   

   

  public void exit() throws UnsupportedOperationException;
   

   
   
   
   

}
