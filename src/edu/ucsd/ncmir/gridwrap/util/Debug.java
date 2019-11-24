package edu.ucsd.ncmir.gridwrap.util;

import java.io.PrintWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Date;
import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import javax.swing.JOptionPane;

public class Debug 
{
	private boolean DEBUG = false;
  private PrintWriter out = null;
  private String filename;
	private String str;

  public Debug()
  {
  }

  public Debug(String filename)
  {
    this.filename = filename;
    if(DEBUG) { JOptionPane.showMessageDialog(null, "SaveQuery"); }
  }
    
  public void printThrowable(Throwable e){
    if(DEBUG){JOptionPane.showMessageDialog(null, "	writeln");}
		openFile();
		String line;

    e.printStackTrace(out);

    closeFile();
        
    if(DEBUG) {JOptionPane.showMessageDialog(null, "	done writeln");}	
  }
    
  public void printstack_IO(IOException io){
    if(DEBUG){JOptionPane.showMessageDialog(null, "	writeln");}
		openFile();
		String line;

    io.printStackTrace(out);

    closeFile();
        
    if(DEBUG) {JOptionPane.showMessageDialog(null, "	done writeln");}
  }
    
  public void printstack(Exception ie){
    try
    {
	    if(DEBUG){JOptionPane.showMessageDialog(null, "	writeln");}
			openFile();
			String line;

	    ie.printStackTrace(out);
	        
      Throwable chain = ie.getCause(); 
      while (chain != null) { 
        chain.printStackTrace(out); 
        chain = chain.getCause(); 
      }
	        
	    closeFile();

	 		if(DEBUG) System.out.println("In debug..done writing to ape.ucsd.edu");
			URL url = new URL("http://ape.ucsd.edu/Apps/SRBInterface/debug.txt");
			
			URLConnection connection = url.openConnection();
			connection.setDoOutput(true);

			PrintWriter out = new PrintWriter(connection.getOutputStream());		

			BufferedReader debugFile = new BufferedReader( new FileReader(new File("c:/vdggsiauth/debug.txt")));
			if(DEBUG) System.out.println("In debug..done writing to ape.ucsd.edu");
			String debugLine = "";
			while((debugLine = debugFile.readLine()) != null)
				out.println("string=" + debugLine);	

			if(DEBUG) System.out.println("In debug..done writing to ape.ucsd.edu");
			out.close();
			if(DEBUG)System.out.println("In debug..done writing to ape.ucsd.edu");
			debugFile.close();

			BufferedReader in = new BufferedReader(
				new InputStreamReader(
					connection.getInputStream()));
			String inputLine;
		
			while ((inputLine = in.readLine()) != null)
				JOptionPane.showMessageDialog(null, inputLine);
			    //System.out.println(inputLine);
		
			in.close();
	        
	    if(DEBUG) {JOptionPane.showMessageDialog(null, "	done writeln");}
      }catch(MalformedURLException mf){
      }catch(FileNotFoundException fe){
      }catch(IOException io){
		}
  }

    public synchronized void writeln(String message)
    {
    	if(DEBUG){JOptionPane.showMessageDialog(null, "	writeln");}
		openFile();

        StringBuffer saveStr = new StringBuffer();
        saveStr.append(message);
        out.println(saveStr.toString());
        
        closeFile();
        
        if(DEBUG) {JOptionPane.showMessageDialog(null, "	done writeln");}
    }
	
    private void openFile()
    {
    	if(DEBUG){
    		JOptionPane.showMessageDialog(null, "	getFileName");
    		System.out.println(getFileName());
    	}
            try
            {
            	File file = new File(getFileName());
            	file.createNewFile();
            	out = new PrintWriter(new FileWriter(file, true));
            }catch (IOException ioe) {
                    ioe.printStackTrace();
            }
            if(DEBUG){JOptionPane.showMessageDialog(null, "	done getFileName");}
    }

    private void closeFile()
    {
		if (out != null) {
		        out.flush();
		        out.close();
		        out = null;
		}
    }

    private String getFileName()
    {
    	return (filename);
    }
}
