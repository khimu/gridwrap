package edu.ucsd.ncmir.gridwrap.file;

import edu.ucsd.ncmir.grid.*;
import edu.ucsd.ncmir.gridwrap.gui.*;
import edu.ucsd.ncmir.gridwrap.util.*;

import edu.ucsd.ncmir.gridwrap.file.*;
import edu.ucsd.ncmir.gridwrap.local.*;
import edu.ucsd.ncmir.gridwrap.util.Usage;
import edu.ucsd.ncmir.gridwrap.util.MessageWindow;
import edu.ucsd.ncmir.gridwrap.thread.*;

import javax.swing.JOptionPane;
import javax.swing.*;
import java.io.*;
import javax.net.ssl.HttpsURLConnection;

import java.util.Vector;
import java.net.URL;
import java.util.Random;

/**
 * <code>ParseInput<code> allows legacy command line programs to operate on SRB objects without
 * worrying about downloading and uploading to SRB.  You can specify the cache
 * location you want to use for file access with the -c option.  If you have
 * a tmpfs partition or similar it is recommended to use that.
 *
 * @author     	Khim Ung (NCMIR)
 * @designer	Tim Warnock (BIRN), Khim Ung (NCMIR)
 */

public class ParseInput 
{
	public static boolean DEBUG = true;
	public final static int PROTOCOL = 0;
	public final static int TEMP = 1;
	public final static int LIST = 2;

  // Local system's file separator character.
  private static String fileSep = File.separator;

	private Vector dataVector = new Vector();
                                                                                
	// This is in the form of how the program should be executed
	// from the command line
	private String commandLine = "";

	public ParseInput(){
	}

	public String toString(){
		return commandLine;
	}

	public Vector getData(){
		return dataVector;
	}

	/* 
	 * @param tokens is a GridWrap specific syntax for running a program from
	 * the command line.  Read the README for info on how GridWrap defines its
	 * data paths from SRB, Grid, Http, and Https.
	 */
	public void parse(Vector tokens){

		/********  FOR DATA FILES **********/
		String srbTemp = LocalFileWrap.LOCAL_TEMP + File.separator + "srb";
		String gridTemp = LocalFileWrap.LOCAL_TEMP + File.separator + "grid";
		String httpTemp = LocalFileWrap.LOCAL_TEMP + File.separator + "http";
		String httpsTemp = LocalFileWrap.LOCAL_TEMP + File.separator + "https";

		Vector srb = new Vector();
		Vector grid = new Vector();
		Vector http = new Vector();
		Vector https = new Vector();
		/*****************************/

		String element = "";
		for(int i = 0; i < tokens.size(); i ++){
			element = (String) tokens.elementAt(i);
			if(DEBUG) System.out.println("element:"+element);

			if(element.startsWith("-Dplugins.dir=")){
				String[] pluginspath = element.split("=");
				String tempLoc = LocalFileWrap.LOCAL_TEMP + File.separator + "plugins";
				LocalFileWrap.createPath(new File(tempLoc));
				System.out.println("pluginspath[1]:"+pluginspath[1]);
				if((pluginspath[1]).startsWith("http")){
					Vector pluginsVector = readHttpFile(pluginspath[1]);
					add("http", tempLoc, pluginsVector); // For plugins automatically use http protocol
				}else if((pluginspath[1]).startsWith("https")){
					Vector pluginsVector = readHttpsFile(pluginspath[1]);
					add("https", tempLoc, pluginsVector); // For plugins automatically use http protocol
				}else if((pluginspath[1]).startsWith("srb")){
					String srbpath = removeHeader("srb://", pluginspath[1]);
					srb.addElement(srbpath);
				}else{
					String gridpath = removeHeader("grid://", pluginspath[1]);
					grid.addElement(gridpath);
				}
				commandLine += pluginspath[0] + "=" + LocalFileWrap.LOCAL_TEMP + " ";
			}
			else if(element.startsWith("-classpath")){
				commandLine += element + " ";
				if(DEBUG) System.out.println("commandLine:"+commandLine);
				i++;
				// Assume the next token is the string of jar files
				element = (String) tokens.elementAt(i);
				readJars(element);
			}
			else 
			if(element.startsWith("srb://D://")){
				String srbpath = removeHeader("srb://D://", element);
				srb.addElement(srbpath);
			}
			else 
			if(element.startsWith("srb://F://")){
				String srbpath = removeHeader("srb://F://", element);
				commandLine += srbTemp+srbpath + " ";
			}
			else // This should come before http check
			if(element.startsWith("srb://")){
				String srbpath = removeHeader("srb://", element);
				srb.addElement(srbpath);
				commandLine += srbTemp+srbpath + " ";
			}
			else
			if(element.startsWith("http://")){
				http.addElement(element);
				String path = removeHeader("http://", element);
				commandLine += httpTemp+path + " ";
			}
			else
			if(element.startsWith("https://")){
				https.addElement(element);
				String path = removeHeader("https://", element);
				commandLine += httpsTemp+path + " ";
			}
			else
			if(element.startsWith("grid://")){
				String gridpath = removeHeader("grid://", element);
				grid.addElement(gridpath);
				commandLine += gridTemp+gridpath + " ";
			}
			else // plain element (ie java )
				commandLine += element + " ";
		}

		if(srb.size()>0)
			add("srb", srbTemp, srb); 
		if(grid.size()>0)
			add("grid", gridTemp, grid); 
		if(http.size()>0)
			add("http", httpTemp, http); 
		if(https.size()>0)
			add("https", httpsTemp, https); 
	}

	private String removeHeader(String remove, String element){
		String[] tmp = element.split(remove);
		String path = "/"+tmp[tmp.length - 1];
		return path;
	}

	private void readJars(String jarsPath){
		String tempLoc = LocalFileWrap.LOCAL_TEMP+File.separator+"lib";
		LocalFileWrap.createPath(new File(tempLoc));
		Vector jarsVector = new Vector();
		String separator = ";";
		if(File.separator.equals("/"))
			separator = ":";
		String[] jars = null;
		if(jarsPath.indexOf("=") != -1){
			jars = jarsPath.split("=");
			commandLine += tempLoc+File.separator+LocalFileWrap.getName(jars[0]);
			jarsVector.addElement(jars[0]);
			for(int j = 1; j < jars.length; j ++){
				jarsVector.addElement(jars[j]);
				if(DEBUG) System.out.println("jars[j]="+jars[j]);
				commandLine += separator+tempLoc+File.separator+LocalFileWrap.getName(jars[j]);
			}
			commandLine += " ";
		}else{
			jarsVector.addElement(jarsPath);
			if(DEBUG) System.out.println(jarsPath);
			commandLine += tempLoc+File.separator+LocalFileWrap.getName(jarsPath) + " ";
		}
		add(jarsPath, tempLoc, jarsVector);
	}

	// Assumes we are reading a file from http
	private Vector readHttpFile(String path){
		Vector pathVector = new Vector();
		try{
			String rootURL = LocalFileWrap.getParent(path);
			System.out.println("rootURL:readHttpFile:"+rootURL);
			URL url = new URL(path);
			BufferedReader is = new BufferedReader(new
				InputStreamReader(url.openConnection().getInputStream()));
			String fileURL;
			while((fileURL = is.readLine()) != null){
				pathVector.addElement(rootURL+"/"+fileURL);
			}
		}catch(IOException ie){
			JOptionPane.showMessageDialog(null, ""+ie);
		}
		
		return pathVector;
	}

	/*
	 * @param protocol tells how to transfer (ie http, srb, grid, https)
	 * @param tempLoc is where to transfer 
	 * @param v is what to transfer
	 */
	private void add(String protocol, String tempLoc, Vector v){
		Vector innerVector = new Vector();

		if(protocol.startsWith("http")){
			innerVector.addElement("http");
		}
		else 
		if(protocol.startsWith("https")){
			innerVector.addElement("https");
		}
		else 
		if(protocol.startsWith("srb")){
			innerVector.addElement("srb");
		}
		else 
		if(protocol.startsWith("grid")){
			innerVector.addElement("grid");
		}

		innerVector.addElement(tempLoc);
		innerVector.addElement(v);

		dataVector.addElement(innerVector);
	}

  /* Downloading ca cert and signing policy from a remote server location */
  private Vector readHttpsFile(String path)
  {
		Vector pathVector = new Vector();
    try{
			String rootURL = LocalFileWrap.getParent(path);
      URL u = new URL(path);
      HttpsSSLConnection hts = new HttpsSSLConnection();
      HttpsURLConnection urlcon =(HttpsURLConnection)hts.connecttowebsite(u);
      urlcon.setRequestMethod("GET");
      urlcon.setUseCaches(false);
      urlcon.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
      urlcon.setDoInput(true);
      urlcon.setDoOutput(false);
      BufferedReader inFile = new BufferedReader(new
      	InputStreamReader(urlcon.getInputStream()));
      if(DEBUG)System.out.println("successfully got bufferedreader.");

			String fileURL;
			while((fileURL = inFile.readLine()) != null){
				pathVector.addElement(rootURL+"/"+fileURL);
			}
      inFile.close();
      urlcon.disconnect();
    }catch(Exception e){
      if(DEBUG)System.out.println(
        "StartApplication CA_Policy Download Exception " + e);
      JOptionPane.showMessageDialog(null,
        "StartApplication CA_Policy Download Exception " + e);
    }
		return pathVector;
  }
}
