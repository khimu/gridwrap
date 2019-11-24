package edu.ucsd.ncmir.gridwrap;


import javax.swing.*;
import java.net.URL;
import java.awt.Container;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import javax.swing.filechooser.FileFilter;
import java.text.DecimalFormat;
import javax.swing.border.BevelBorder;
import java.awt.event.ComponentListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.util.Vector;
import java.util.StringTokenizer;


/**
 *Because each application is launched slightly different, i had to separate 
 *each application to have its own launching mechanism.  This sets it up to be 
 *more of a framework for adding apps.  This is also a prototype for what 
 *is envision for gridservices.  Why not use Java Web start again.  It would
 *be very annoying to be launching jnlp programs all over the place.  This
 *Execution scheme has proven to work on Windows, Mac OSX, Linux, Solaris.
 *
 *
 *@author Tomas Molina BIRN
 *@version 2.2.0
 */
public class ExecuteProgram
{
	
	/** the Debug var */
	private static boolean DEBUG = false;

	private String EXECUTE_CMD = null;
	
	/** Exit Code returned by the process running **/
	private int exitcode = -1;
	
	/** Simple constructor for accepting arguments from ApplicationLauncher */
	public ExecuteProgram(String exec) //
	{
		EXECUTE_CMD = exec;
	}

	/**
 	 * This will execute the program by creating a process and executing the 
 	 * program with the necessary arguments
 	 */
 	public void executeprogram() 
 	{
 		try{
 			
 			if(DEBUG)
				JOptionPane.showMessageDialog(null, EXECUTE_CMD);

 			String[] cmd = {EXECUTE_CMD};
 			runProcess(cmd, new StringBuffer(), new StringBuffer());
 		}
 		catch (IOException e1){
   		JOptionPane.showMessageDialog(null, "Could not launch the program. Double check the parameters"+e1);
   		System.out.println("InterruptedException raised: "+e1.getMessage()); 
 		}
 		catch (Exception e3) { 
   		JOptionPane.showMessageDialog(null, "Could not launch the program. Double check the parameters"+e3);
   		System.out.println("InterruptedException raised: "+e3.getMessage()); 
 		}
	}
 			
	/**
	 * Execute an OS native process
	 * @param cmd Full path to an executable as an array of strings
	 * @param stdout Standard out
	 * @param stderr Standard err
	 * @throws IOException if exec not found
	 */

	public static void runProcess(String[] cmd, StringBuffer stdout, StringBuffer stderr)
	        throws IOException
	{
	        Process p  = null;
	        String s = "";
	        for (int i = 0; i < cmd.length; i++) {
	                s += cmd[i];
	        }
	        try {
	        		System.out.println(""+s);
	                p = Runtime.getRuntime().exec(s);
	                int exitcode = p.waitFor();

       	 			if(DEBUG)
       	 				JOptionPane.showMessageDialog(null, "This is the exitcode: "+exitcode); 
       	 	} catch (InterruptedException ie){
       	 		System.out.println("runProcess interrupted.");
       	 	                
	        } catch (IOException e) {
	                throw new IOException("Command " + s + " not found.");
	        }

	        System.out.println("running application.");

	        BufferedReader in0  = new BufferedReader(new InputStreamReader(p.getInputStream()));
	        BufferedReader in1  = new BufferedReader(new InputStreamReader(p.getErrorStream()));

	        String line = null;

	        while ( (line = in0.readLine()) != null) {
	        	System.out.println("line:"+line);
	                stdout.append(line);
	        }

	        while ( (line = in1.readLine()) != null) {
	        	System.out.println("line:");
	                stderr.append(line);
	        }
	}
}
