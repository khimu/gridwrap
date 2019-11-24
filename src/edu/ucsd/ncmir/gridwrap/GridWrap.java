package edu.ucsd.ncmir.gridwrap;

import edu.ucsd.ncmir.grid.*;
import edu.ucsd.ncmir.grid.impl.*;
import edu.ucsd.ncmir.gridwrap.gui.*;
import edu.ucsd.ncmir.gridwrap.util.*;
import edu.ucsd.ncmir.gridwrap.gui.transfer.*;
import edu.ucsd.ncmir.gridwrap.thread.*;

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

import java.awt.event.*;

/**
 * <code>GridWrap<code> allows legacy command line programs to operate on SRB objects without
 * worrying about downloading and uploading to SRB.  You can specify the cache
 * location you want to use for file access with the -c option.  If you have
 * a tmpfs partition or similar it is recommended to use that.
 *
 * @author    Khim Ung (NCMIR)
 * @designer	Tim Warnock (BIRN), Khim Ung (NCMIR)
 */

public class GridWrap //extends JFrame
{
	private final static String VERSION = "1.1";
	private final static String TITLE = "GridWrap";

	public static boolean DEBUG = false;

	public static boolean GSI_AUTH = false; // Not using GSI_AUTH

	private static String srb_host = null;
	private static int srb_port = -1;
	private static String srb_user = null;
	private static String srb_home = null;
	private static String srb_domain = null;
	private static String srb_storage = null;
	private static String ca_url = null;
	private static String cert_id = null;
	private static String proxy_file = null;

	private static String srbpassword = null;

  // Local system's file separator character.
  private static String fileSep = File.separator;
                                                                                
  // Performs all the SRB related functionalities
  private static GridService gridService = null;
                                                                                
  // Set in GridService.java with setCertificateAuthentication accessed from this.authenticate()
  private static String local_cert_id = null;
                                                                                
	private Vector uploadPaths = new Vector();

	// This is read in from the arguments and requires parsing
	private static Vector commandTokens = new Vector();

	//private static GridStatusWindow window = null;

	private static TransferWindow dlw = null;
	private static TransferWindow ulw = null;

	private static TransferManager dtm = null;
	private static TransferManager utm = null;

	private static ParseInput parser = new ParseInput();

	private Vector dataVector = null;

	private Vector[] guiArray = null;

	private int totalSize = 0;

	private GridWrap gridwrap = null;

	public GridWrap(){
		parser.parse(commandTokens);
		dataVector = parser.getData();
		totalSize = getTransferSize(dataVector);
		createWindow();
    try{
      Thread.sleep(100);
    }catch(InterruptedException ie){
      JOptionPane.showMessageDialog(null, ""+ie);
    }
		startDownloadTransfer();
		dlw.hide();
		//lw.hideWindow();
		execProgram();
	}

	public static void setPassword(String passwd){
		srbpassword = passwd;
		setSession();
	}

	private void createWindow(){
		dlw = new TransferWindow(TITLE+" "+VERSION+" Download Window", totalSize, LocalFileWrap.LOCAL_TEMP);
		dtm = new TransferManager(totalSize, "Download", gridSession, dlw);
		dlw.pack();
		dlw.setVisible(true);
    createDownloadGUI();

	}

	private static int getTransferSize(Vector main){
		Vector inner = null;
		int total = 0;
		for(int i = 0; i < main.size(); i ++){
			inner = (Vector)main.elementAt(i);
			total += ((Vector)inner.elementAt(ParseInput.LIST)).size();	
		}
		return total;
	}

	private void execProgram(){
		String commandLine = parser.toString();
		if(DEBUG) System.out.println(commandLine+" exists");
		downloadScan("https");
		downloadScan("http");
		downloadScan("srb");
		downloadScan("grid");

		if(DEBUG) System.out.println("commandLine:"+commandLine);
		ExecuteProgram exec = new ExecuteProgram(commandLine);
		exec.executeprogram();
		if(DEBUG) System.out.println("About to upload grid.");

		// Time to upload
		uploadScan("http");
		uploadScan("https");
		uploadScan("srb");
		uploadScan("grid");
		
		dlw.show();

		Vector uploadVector = objectMonitor.getModifiedFiles();
		for(int i = 0; i < uploadVector.size(); i ++){
			if(DEBUG) System.out.println("return:"+uploadVector.elementAt(i));
			uploadPaths.addElement(uploadVector.elementAt(i));
		}

		if(DEBUG)System.out.println("UploadPaths:"+uploadPaths.size());

		ulw = new TransferWindow(TITLE+" "+VERSION+" Upload Window", uploadPaths.size(), LocalFileWrap.LOCAL_TEMP);
		utm = new TransferManager(uploadPaths.size(), "Upload", gridSession, ulw);
		ulw.showWindow();
		Vector guiv = utm.buildGui(uploadPaths, LocalFileWrap.LOCAL_TEMP, "Upload");
		utm.gridTransfer(uploadPaths, LocalFileWrap.LOCAL_TEMP, "Upload Transfer", guiv);

		delTree(new File(LocalFileWrap.LOCAL_TEMP));
    try{
      Thread.sleep(500);
    }catch(InterruptedException ie){
      JOptionPane.showMessageDialog(null, ""+ie);
    }
	}

	public static void delTree(File deletePath){
		if(deletePath.isDirectory()){
			File[] childFiles = deletePath.listFiles();
			for(int i = 0; i < childFiles.length; i ++){
				delTree(childFiles[i]);
			}
			deletePath.delete();
		}
		else{
			deletePath.delete();
		}
	}

	private void uploadScan(String dir){
		File f = new File(LocalFileWrap.LOCAL_TEMP+ File.separator + dir);
		if(DEBUG) System.out.println("uploadScan for:"+f.getPath());
		objectMonitor.scanUpload(f);
	}

	private void downloadScan(String dir){
		File localFile = new File(LocalFileWrap.LOCAL_TEMP+ File.separator + dir);
		objectMonitor.scanDownload(localFile);
	}

	private void createDownloadGUI(){
		guiArray = new Vector[dataVector.size()];
		Vector inner = null;
		Vector gui = null;
		for(int i = 0; i < dataVector.size(); i ++){
			inner = (Vector)dataVector.elementAt(i);
			Vector data = (Vector) inner.elementAt(ParseInput.LIST);
			String temp = (String) inner.elementAt(ParseInput.TEMP);
			String protocol = (String) inner.elementAt(ParseInput.PROTOCOL);
			gui = dtm.buildGui(data, temp, protocol.toUpperCase()+" Download Transfer");
    	try{
      	Thread.sleep(100);
    	}catch(InterruptedException ie){
      	JOptionPane.showMessageDialog(null, ""+ie);
    	}
			guiArray[i] = gui;
		}
	}

	private void startDownloadTransfer(){

		Vector inner = null;
		Vector gui = null;
		for(int i = 0; i < dataVector.size(); i ++){
			inner = (Vector)dataVector.elementAt(i);
			Vector data = (Vector) inner.elementAt(ParseInput.LIST);
			String temp = (String) inner.elementAt(ParseInput.TEMP);
			String protocol = (String) inner.elementAt(ParseInput.PROTOCOL);
			gui = guiArray[i];
			if(protocol.equals("http"))
				dtm.httpTransfer(data, temp, "Http Download Transfer", gui);
			else if(protocol.equals("https"))
				dtm.httpTransfer(data, temp, "Https Download Transfer", gui);
			else if(protocol.equals("srb"))
				dtm.gridTransfer(data, temp, "Download Transfer", gui);
			else if(protocol.equals("grid"))
				dtm.gridTransfer(data, temp, "Download Transfer", gui);
      try{
        Thread.sleep(100);
      }catch(InterruptedException ie){
        JOptionPane.showMessageDialog(null, ""+ie);
      }
		}
	}

	public static void main(String[] args){
		// Leave out for now for debuging purposes

    try{
      if(args.length > 9){
        srb_host= args[0];
        srb_port = Integer.parseInt(args[1]);
        srb_user = args[2];
        srb_home = args[3];
        srb_domain = args[4];
        srb_storage = args[5];
        ca_url = args[6];
        cert_id = args[7];
        proxy_file = args[8];
				if(DEBUG) System.out.println("got arguments 9");
                                                                                
        if(!proxy_file.equals("null")){
					GSI_AUTH = true;  // Using GSI_AUTH
          srbpassword = "";
          write2localProxy(proxy_file, srbpassword);
          proxy_file = LocalFileWrap.LOCAL_TEMP+File.separator+srb_user+"_proxy";
          local_cert_id = LocalFileWrap.LOCAL_TEMP+ File.separator + cert_id + ".0";
          httpSslDownload(ca_url+cert_id+".signing_policy", LocalFileWrap.LOCAL_TEMP + File.separator + cert_id+".signing_policy");
          httpSslDownload(ca_url+cert_id+".0", local_cert_id);
					setSession();
				}

				int arglen = args.length - 9;
				if(DEBUG) System.out.println("arglen:"+arglen);
				if(arglen > 0){
					for(int  i = 9; i < args.length; i ++){
						if(DEBUG) System.out.println("args["+i+"]="+args[i]);
						commandTokens.addElement(args[i]);
					}
				}

				if(GSI_AUTH == false){
  		  	SwingUtilities.invokeLater(new Runnable(){
   					public void run(){
							final PasswordDialog pd = new PasswordDialog(null);
						}
    			});
				}else{
    			SwingUtilities.invokeLater(new Runnable(){
    	  		public void run(){
							new GridWrap();
    	  		}
    			});
				}
      }else{
        if(DEBUG)JOptionPane.showMessageDialog(null,
          "Bad Argument\nCalling khimAccount()");

				usage();
      }
    }catch(Exception e8){
      JOptionPane.showMessageDialog(null,
        "There is a problem with the user interface.  "+
        "Please contact the Web admin with this error.");
    }
	}

	private static void setSession(){
		gridSession.setHost(srb_host);
		gridSession.setPort(srb_port);
		gridSession.setHome(srb_home);
		gridSession.setUserName(srb_user);	
		gridSession.setDomain(srb_domain);
		gridSession.setStorage(srb_storage);
		gridSession.setPassword(srbpassword);
		gridSession.setProxy(proxy_file);
		gridSession.setCert(local_cert_id);
	}

	/* 
	 * Print usage information and exit gracefully.
	 */
	private static void usage(){
		//HtmlFileViewer usage = new HtmlFileViewer(null, "docs/ini/Usage.html");
		Usage usage = new Usage();
		new MessageWindow(usage.getUsageText(), "Usage");
	}

  /* Writing the proxy content from jnlp to a local file */
  public static void write2localProxy(String proxy_content, String localFilePath)
  {
    try
    {
      File file = new File(localFilePath);
      if(file.exists())
      {
        file.delete();
      }
      file.createNewFile();
      PrintWriter out = new PrintWriter(new FileWriter(file, true));
                                                                                
      out.println(proxy_content);
      out.flush();
      out.close();
    }catch(Exception e){
      if(DEBUG)System.out.println(
        "StartApplication proxy_file Download Exception " + e);
      JOptionPane.showMessageDialog(null,
        "StartApplication proxy_file Download Exception " + e);
    }
  }

  /* Writing the ca cert and signing policy content to a local file */
  public static void write2local(BufferedReader inFile, String localFilePath)
  {
    try
    {
      File file = new File(localFilePath);
      if(file.exists())
      {
        file.delete();
      }
      file.createNewFile();
      PrintWriter out = new PrintWriter(new FileWriter(file, true));
      if(DEBUG)System.out.println("successfully got printwriter");
                                                                                
      String a = null;
      while((a = inFile.readLine()) != null)
      {
        StringBuffer saveStr = new StringBuffer();
        saveStr.append(a);
        out.println(saveStr.toString());
      }
      inFile.close();
      out.flush();
      out.close();
    }catch(Exception e){
      if(DEBUG)System.out.println(
        "StartApplication CA_Policy Download Exception " + e);
      JOptionPane.showMessageDialog(null,
        "StartApplication CA_Policy write Exception " + e);
    }
  }

  /* Downloading ca cert and signing policy from a remote server location */
  public static void httpSslDownload(String localFile, String localFilePath)
  {
    try{
      URL u = new URL(localFile);
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
                                                                                
      write2local(inFile, localFilePath);
                                                                                
      urlcon.disconnect();
                                                                                
    }catch(Exception e){
      if(DEBUG)System.out.println(
        "StartApplication CA_Policy Download Exception " + e);
      JOptionPane.showMessageDialog(null,
        "StartApplication CA_Policy Download Exception " + e);
    }
  }

	private ObjectMonitor objectMonitor = new ObjectMonitor();
	private static TransferManager transfer = null;
	private static GridSession gridSession = new GridSessionImpl();
}
