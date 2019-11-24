package edu.ucsd.ncmir.gridwrap.util;
 
import java.io.PrintWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Date;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.JOptionPane;
 
/**
 * The <code>Usage</code> will read the usage.ini file
 * and display the usage information to the user.
 */
 
public class Usage {
	
	private final static boolean DEBUG = false;

	/* The usage text */
	private String usageText = null;
 
	private BufferedReader in = null;

	private final File usageFileLocation =
			new File("doc/ini/Usage.ini");

	public static void main(String[] args){
		new Usage();
	}

	public Usage(){
		if(DEBUG) getUsageText();
	}
	
	public String getUsageText()
	{
		openUsageFile();
		try
		{
			usageText = "";
			String line = null;

			while((line = in.readLine()) != null){
				line.trim();
				usageText += line + "\n";
			}

		} catch (IOException e55) {

			/* Error Reporting */
			StackTraceElement[] trace_element = e55.getStackTrace();

			System.out.println("Usage:getUsageText\n");
			JOptionPane.showMessageDialog(null,"Usage:getUsageText\n"); 

			for(int i = 0; i < trace_element.length; i ++){
				JOptionPane.showMessageDialog(null, trace_element[i].toString());
				System.out.println(trace_element[i].toString());
			}

			e55.printStackTrace();

		}

		closeUsageFile();

		if(DEBUG) JOptionPane.showMessageDialog(null, usageText);
		if(DEBUG) System.out.println(usageText);

		return usageText;
	}

	public void openUsageFile()
	{
		try
		{
		
			in = new BufferedReader(new FileReader(usageFileLocation));
		} catch (IOException e55) {

			StackTraceElement[] trace_element = e55.getStackTrace();

			System.out.println("Usage:openUsageFile\n");
			JOptionPane.showMessageDialog(null,"Usage:openUsageFile\n"); 

			for(int i = 0; i < trace_element.length; i ++){
				JOptionPane.showMessageDialog(null, trace_element[i].toString());
				System.out.println(trace_element[i].toString());
			}

			e55.printStackTrace();

		}
	}

	public void closeUsageFile()
	{
		if(in != null){

			try{

				in.close();

			}catch(IOException e55){

				StackTraceElement[] trace_element = e55.getStackTrace();

				System.out.println("Usage:openUsageFile\n");
				JOptionPane.showMessageDialog(null,"Usage:openUsageFile\n"); 

				for(int i = 0; i < trace_element.length; i ++){
					JOptionPane.showMessageDialog(null, trace_element[i].toString());
					System.out.println(trace_element[i].toString());
				}

				e55.printStackTrace();

			}
			in = null;

		}
	}
}
