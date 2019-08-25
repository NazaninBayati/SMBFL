import java.io.*; 

public class slicer {

	public static void main(String[] args) throws Exception 
	{ 
		 String str = ""; 
	        
	 // pass the path to the file as a parameter 
	 FileReader fr = 
	   new FileReader("C:\\Users\\nazanin\\Desktop\\text.txt"); 
	 
	 int i; 
	 while ((i=fr.read()) != -1) 
	 {
		 
	     str = str + (char) i ; 
	 }
	 String[] arrOfStr = str.split(";"); 
     String word = "number2";
     
    
     
     
     for (String a : arrOfStr) 
     {

        System.out.println(a); 
        System.out.println("******************");
  
        
     }
     String string = arrOfStr[2].toString();
     String[] wordofarr = string.split(" ");
     boolean flag = false;
     for(int j=0; j<wordofarr.length;j++)
     {
    	 if (wordofarr[j] == word){
    		 flag = true;
    	 }
     }
     
     for (String b : wordofarr)
     {
    	
    	 System.out.println(b);
    	 System.out.println("-----------------------------");
    	 
     }
     
     System.out.print(flag);
   
     
	} 
	

}

