package edu.ucsd.ncmir.gridwrap.thread;

import edu.ucsd.ncmir.grid.*;
import edu.ucsd.ncmir.gridwrap.gui.transfer.*;

import edu.sdsc.grid.io.srb.SRBFile;

import javax.swing.JOptionPane;
import java.io.*;
import java.util.Vector;
import java.util.Timer;
import java.lang.Thread;
import java.lang.Runnable;


/* This thread allows for downloading a file from SRB 
 *
 * @author Khim Ung National Center for Microscopy and Imaging Research
 */
public class DownloadThread implements Runnable
{
	/* Performs a Download from an SRB server.
	 * @param aConnection is a GridService to SRB servers
	 * @param srbFile is a string represent of an SRB path
	 * @param localFile is a string representation of a local path
	 * @param gridTransferThread is an Object representation of the parent Thread
	 * @param freeConnection is an index to one of our created GridService instance
	 * @param gpanel is an reference to our TransferProgressPanel gui
	 *
	 */
	public DownloadThread(GridService aConnection, String srbFile, 
		String localFile, Object gridTransferThread, int freeConnection,
		TransferProgressPanel gpanel)
	{
		this.aConnection = aConnection;
		this.srbFile = srbFile;
		this.localFile = localFile;
		this.gridTransferThread = gridTransferThread;
		this.threadName = (new Integer(freeConnection)).toString();
		this.gpanel = gpanel;
	}

	public void start(ThreadGroup gridThreadGroup){
		if(downloadThread == null){
			downloadThread = new Thread(gridThreadGroup, this, threadName);
			downloadThread.start();
		}else{
			System.out.println("Saying my downloadThread is not null");
		}
	}

	public void run(){
		if(DEBUG) System.out.println("downlaod gtt incremented");
		gtt = (GridTransferThread)gridTransferThread;

		try{
			SRBFile srbPath = aConnection.getSrbFile(srbFile);
			long len = aConnection.length(srbFile);

			GridProgressThread gridProgressThread = new GridProgressThread(
				gpanel, aConnection, srbPath, (new Long(len)).intValue(), downloadThread);
			gridProgressThread.start();

			// SwingUtilities is used in setMax
			gpanel.setMax((new Long(len)).intValue());

			download();
		}catch(IOException e000){
			JOptionPane.showMessageDialog(null, "DownloadThread:run:"+e000);
		}
	}

 	private void download(){
		try{
			if(DEBUG) System.out.println("Download Starting");
			if(DEBUG) System.out.println("srbFile:"+srbFile);
			if(DEBUG) System.out.println("localFile:"+localFile);
			aConnection.copy2local(srbFile, localFile);
			gpanel.done(srbFile);
			if(DEBUG) System.out.println("Download Done");
		}catch(IOException io){
			gpanel.setError(srbFile);
			JOptionPane.showMessageDialog(null, "DownloadThread:downlaod:"+io);
		}catch(NullPointerException nn){
			gpanel.setError(srbFile);
			JOptionPane.showMessageDialog(null, "DownloadThread:downlaod:"+nn);
		}catch(RuntimeException rr){
			gpanel.setError(srbFile);
			JOptionPane.showMessageDialog(null, "DownloadThread:downlaod:"+rr);
		}
		gtt.decrementThreadCount();
		gtt.threadFinished(threadName);// Start another thread
		synchronized(gridTransferThread){
			gridTransferThread.notify();
		}
	}

	// This method is here so that we can passed this
	// created thread to the associated progressBarThread
	// so that the progress bar can use this thread to stop
	// itself.
	public Thread getThread(){
		return downloadThread;
	}

	/* threadName is a String representation of a number we are using
	 * to reference the GridService we are using to perform the transfer.
	 * There are only a finite number of GridService that we create initially 
	 * for performing transfers.  This way, we do not open too many connections 
	 * and cause some server overloading.
	 **/
	private String threadName = null;

	/* Parent Thread to this DownloadThread */
	private Object gridTransferThread = null;

	/* This is the DownloadThread */
	private Thread downloadThread = null;

	/* SRB path for downloading */
	private String srbFile = null;

	/* Downloading to this local path */
	private String localFile = null;

	/* An SRB connection */
	private GridService aConnection = null;

	/* The associated Progress Bar GUI */
	private TransferProgressPanel gpanel = null;
	private GridTransferThread gtt = null;
	private final static boolean DEBUG = false;
}
