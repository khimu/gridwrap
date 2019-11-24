package edu.ucsd.ncmir.gridwrap.thread;

import edu.ucsd.ncmir.grid.*;
import edu.ucsd.ncmir.gridwrap.gui.transfer.*;

import edu.sdsc.grid.io.srb.SRBFile;
import edu.sdsc.grid.io.local.LocalFile;

import javax.swing.JOptionPane;
import java.io.*;
import java.util.Vector;
import java.util.Timer;
import java.lang.Thread;
import java.lang.Runnable;


/*
 * @author Khim Ung National Center for Microscopy and Imaging Research
 */
public class UploadThread implements Runnable
{
	/* Performs an Upload from an SRB server.
	* @param aConnection is a GridService to SRB servers
	* @param srbFile is a string represent of an SRB path
	* @param localFile is a string representation of a local path
	* @param gridTransferThread is an Object representation of the parent Thread
	* @param freeConnection is an index to one of our created GridService instance
	* @param gpanel is a reference to out Progress Bar GUI
	*/
	public UploadThread(GridService aConnection, String srbFile, 
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
		if(uploadThread == null){
			uploadThread = new Thread(gridThreadGroup, this, threadName);
			uploadThread.start();
		}
	}

	public void run(){
		gtt = (GridTransferThread)gridTransferThread;
		SRBFile srbPath = aConnection.getSrbFile(srbFile);
		LocalFile localPath = new LocalFile(new File(localFile));
		if(DEBUG) System.out.println("LocalFile:"+localPath.getAbsolutePath());

		long len = localPath.length();
		if(localPath.isDirectory()){
			recurs(new File(localFile));
			len = global_length;
			global_length = 0;
			//len = 0;
		}
		gpanel.setMax((new Long(len)).intValue());

		GridProgressThread gridProgressThread = new GridProgressThread(
			gpanel, aConnection, srbPath, (new Long(len)).intValue(), uploadThread);

		gridProgressThread.start();

		upload();
	}

	int global_length = 0;

	private void recurs(File f){
		if(f.isDirectory()){
			File[] list = f.listFiles();
			for(int i = 0; i < list.length; i ++){
				recurs(list[i]);
			}
		}else{
			global_length += f.length();		
		}
	}

 	private void upload(){
		try{
			System.out.println("calling copy2remote:srbFile:"+srbFile+" localFile:"+localFile);
			aConnection.copy2remote(srbFile, localFile);
			gpanel.done(localFile);
		}catch(IOException io){
			gpanel.setError(localFile);
			JOptionPane.showMessageDialog(null, ""+io);
		}catch(Exception e){
			gpanel.setError(localFile);
			JOptionPane.showMessageDialog(null, ""+e);
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
		return uploadThread;
	}

	/* threadName is a String representation of a number we are using
	* to reference the GridService we are using to perform the transfer.
	* There are only a finite number of GridService that we create initially
	* for performing transfers.  This way, we do not open too many connections
	*/
	private String threadName = null;

	/* Reference to the parent Thread */
	private Object gridTransferThread = null;

	/* The upload thread for this class */
	private Thread uploadThread = null;

	/* Uploading to this SRB path */
	private String srbFile = null;

	/* Uploading this local file */
	private String localFile = null;

	/* Our SRB connection to use to perform the transfer */
	private GridService aConnection = null;

	/* Our associated Progress Bar GUI */
	private TransferProgressPanel gpanel = null;
	private GridTransferThread gtt = null;
	private final static boolean DEBUG = false;
}
