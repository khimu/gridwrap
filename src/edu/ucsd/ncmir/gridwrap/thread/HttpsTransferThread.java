package edu.ucsd.ncmir.gridwrap.thread;

import edu.ucsd.ncmir.grid.*;
import edu.ucsd.ncmir.gridwrap.util.*;
import edu.ucsd.ncmir.gridwrap.local.*;
import edu.ucsd.ncmir.gridwrap.gui.transfer.*;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.io.*;
import java.util.Vector;
import java.util.Timer;
import java.lang.Thread;
import java.lang.Runnable;

import java.net.URL;

import javax.net.ssl.*;
import java.security.*;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;
import java.security.cert.*;


/* This class starts an Https secure transfer.  It is also responsible for
 * updating the bytes transfered so far to the ProgressBarThread.
 *
 * @author Khim Ung National Center for Microscopy and Imaging Research
 */
public class HttpsTransferThread implements Runnable, TransferByteSizeInterface
{
	public void addTransferByteSizeListener(TransferByteSizeListener listener){
		bytesizelisteners.addElement(listener);
	}

	public void removeTransferByteSizeListener(TransferByteSizeListener listener){
		bytesizelisteners.removeElement(listener);
	}

	public void fireTransferByteSizeEvent(TransferByteSizeEvent e, int bytes){
		Vector list = (Vector) bytesizelisteners.clone();
		for(int i = 0; i < list.size(); i ++){
			TransferByteSizeListener listener = (TransferByteSizeListener) list.elementAt(i);
			switch(e.getID()){
				case TransferByteSizeEvent.BYTE:
					listener.setBytesTransfered(bytes, getSuccess());
			}
		}
	}

	/* Performs parallel transfers.
	 * @param gpanel is used to update the associated progress bar gui
	 * @param httpsLocation is the url location to download the file
	 * @param tempLocation is the temporary location on the local machine
	 * @param urlTransferThread is the parent transfer thread
	 * @param title indicates an upload or downlaod transfer although we are only allowing download
	 * via this protocol
	 */
	public HttpsTransferThread(TransferProgressPanel gpanel,
		String httpsLocation, String tempLocation, Object urlTransferThread, String title)
	{
		this.httpsLocation = httpsLocation;
		this.tempLocation = tempLocation;
		this.gpanel = gpanel;
		this.urlTransferThread = urlTransferThread;
		this.title = title;
	}

	public void start(ThreadGroup urlThreadGroup){
		if(httpsTransferThread == null){
			httpsTransferThread = new Thread(urlThreadGroup, this);
			httpsTransferThread.start();
		}
	}

	public void run(){
		ProgressBarThread progressBarThread = new ProgressBarThread(gpanel, this);	
		if(title.toUpperCase().indexOf("DOWNLOAD") > 0)
			download();
		else
			upload();
	}

	private void download(){
		try{
			String fileName = LocalFileWrap.getName(httpsLocation);
			URL url = new URL(httpsLocation);
			HttpsSSLConnection hts = new HttpsSSLConnection();
			HttpsURLConnection urlcon =(HttpsURLConnection)hts.connecttowebsite(url);
			urlcon.setRequestMethod("GET");
			urlcon.setUseCaches(false);
			urlcon.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
			urlcon.setDoInput(true);
			urlcon.setDoOutput(false);
			BufferedReader inFile = new BufferedReader(new
				InputStreamReader(urlcon.getInputStream()));
			write2local(inFile, tempLocation+"/"+fileName);
			urlcon.disconnect();
		}catch(Exception e22){
			gpanel.setError(httpsLocation);
			JOptionPane.showMessageDialog(null, e22);
		}

    gpanel.setMax(-1);
    gpanel.setIndeterminate(false);
    gpanel.setStringValue("Transfer Complete");
		UrlTransferThread.decrementThreadCount();
		synchronized(urlTransferThread){
			urlTransferThread.notify();
		}
	}

	private void upload(){
	}

	/**
	 * Copy data from a buffer to a local path
	 *  @param inFile Data stored in a BufferedReader
	 *  @param localFilePath The local path on the client's machine
	 **/
	public void write2local(BufferedReader inFile, String localFilePath)
		throws Exception
	{
		File file = new File(localFilePath);
		if(file.exists())
			file.delete();
		file.createNewFile();

		PrintWriter out = new PrintWriter(new FileWriter(file, true));

		String a = null;
		while((a = inFile.readLine()) != null)
		{
			StringBuffer saveStr = new StringBuffer();
			saveStr.append(a);
			out.println(saveStr.toString());
      try{
        Thread.sleep(100);
      }catch(InterruptedException ie){
        JOptionPane.showMessageDialog(null, ""+ie);
      }
			fireTransferByteSizeEvent( new TransferByteSizeEvent(
				this, TransferByteSizeEvent.BYTE), saveStr.length());
		}
		inFile.close();
		out.flush();
		out.close();
	}

	private synchronized boolean getSuccess(){
		return SUCCESSFUL;
	}

	public synchronized void setSuccessful(boolean success){
		SUCCESSFUL = success;
	}

	private boolean SUCCESSFUL = true;
	private String title = null;
	private String tempLocation = null;
	private String httpsLocation = null;
	private Vector bytesizelisteners = new Vector();
	private Object urlTransferThread = null;
	private Thread httpsTransferThread = null;
	private TransferProgressPanel gpanel = null;
	private final static boolean DEBUG = false;
}
