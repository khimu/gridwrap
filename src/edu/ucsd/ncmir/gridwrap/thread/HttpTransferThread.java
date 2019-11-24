package edu.ucsd.ncmir.gridwrap.thread;

import edu.ucsd.ncmir.gridwrap.local.*;
import edu.ucsd.ncmir.grid.*;
import edu.ucsd.ncmir.gridwrap.util.*;
import edu.ucsd.ncmir.gridwrap.gui.transfer.*;

import java.net.*;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.io.*;
import java.util.Vector;
import java.util.Timer;
import java.lang.Thread;
import java.lang.Runnable;


/* Performs an unsecure download via http protocol
 * @author Khim Ung National Center for Microscopy and Imaging Research
 */
public class HttpTransferThread implements Runnable, TransferByteSizeInterface
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
	 * @param title determines if we are downloading or uploading
	 * @param gridSession is used to make a connection to SRB
	 * @param tempLocation is the temporary location on the local machine
	 */
	public HttpTransferThread(TransferProgressPanel gpanel,
		String httpLocation, String tempLocation, 
		Object urlTransferThread, String title)
	{
		this.httpLocation = httpLocation;
		this.tempLocation = tempLocation;
		this.gpanel = gpanel;
		this.urlTransferThread = urlTransferThread;
		this.title = title;
	}

	public void start(ThreadGroup urlThreadGroup){
		if(httpTransferThread == null){
			httpTransferThread = new Thread(urlThreadGroup, this);
			httpTransferThread.start();
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
			String pluginName = LocalFileWrap.getName(httpLocation);
			URL url = new URL(httpLocation);
			URLConnection connection = url.openConnection();
			InputStream stream = connection.getInputStream();
			BufferedInputStream in = new BufferedInputStream(stream);
			FileOutputStream file = new FileOutputStream(
				tempLocation + File.separator + pluginName);

			BufferedOutputStream out = new BufferedOutputStream(file);
			int i=0;

  		byte buffer[] = new byte[BUF_SIZE];;
			int bytesRead;
    	// Simple read/write loop.
    	while(-1 != (bytesRead = in.read(buffer, 0, buffer.length))) {
        out.write(buffer, 0, bytesRead);
    		try{
      		Thread.sleep(100);
    		}catch(InterruptedException ie){
      		JOptionPane.showMessageDialog(null, ""+ie);
    		}
				fireTransferByteSizeEvent(new TransferByteSizeEvent(this, TransferByteSizeEvent.BYTE), bytesRead);
    	}
			out.flush();
			gpanel.setMax(-1);
			gpanel.setIndeterminate(false);
			gpanel.setStringValue("Transfer Complete");
			gpanel.changeBackground();

		}catch(java.net.MalformedURLException e22){
			gpanel.setError(httpLocation);
			JOptionPane.showMessageDialog(null, e22);
		}catch(java.io.IOException e43){
			gpanel.setError(httpLocation);
			JOptionPane.showMessageDialog(null, e43);
		}
    try{
      Thread.sleep(100);
    }catch(InterruptedException ie){
      JOptionPane.showMessageDialog(null, ""+ie);
    }
		UrlTransferThread.decrementThreadCount();
		synchronized(urlTransferThread){
			urlTransferThread.notify();
		}
	}

	/* Not used */
	private void upload(){
		try{	
			String urlPath = "http://"+httpLocation;

			String localHttpPath = tempLocation+File.separator+httpLocation;

			URL url = new URL(urlPath);
			URLConnection connection = url.openConnection();
			connection.setDoOutput(true);

			BufferedOutputStream out = new BufferedOutputStream(connection.getOutputStream());
			
			File localFile = new File(localHttpPath);
			BufferedInputStream in = new BufferedInputStream(
				new FileInputStream(localFile)); 
			int i = 0;
			while((i = in.read()) != -1){
				out.write(i);
				fireTransferByteSizeEvent(new TransferByteSizeEvent(this, TransferByteSizeEvent.BYTE), i);
			}
			in.close();
			out.flush();
			out.close();

		}catch(java.net.MalformedURLException e22){
			gpanel.setStringValue("Transfer Error");
			JOptionPane.showMessageDialog(null, e22);
		}catch(java.io.IOException e43){
			gpanel.setStringValue("Transfer Error");
			JOptionPane.showMessageDialog(null, e43);
		}
		gpanel.setMax(-1);
		synchronized(urlTransferThread){
			urlTransferThread.notify();
		}
	}

	private boolean getSuccess(){
		return SUCCESSFUL;
	}

	public void setSuccess(boolean success){
		SUCCESSFUL = success;
	}

	private boolean SUCCESSFUL = true;
	private String title = null;
	private String tempLocation = null;
	private String httpLocation = null;
	private Vector bytesizelisteners = new Vector();
	private Object urlTransferThread = null;
	private Thread httpTransferThread = null;
	private TransferProgressPanel gpanel = null;
	private final static int BUF_SIZE = 4096;
	private final static boolean DEBUG = false;
}
