package edu.ucsd.ncmir.gridwrap.thread;

import edu.ucsd.ncmir.grid.*;
import edu.ucsd.ncmir.gridwrap.gui.transfer.*;

import edu.sdsc.grid.io.srb.SRBFile;
import edu.sdsc.grid.io.local.LocalFile;

import javax.swing.JOptionPane;
import javax.swing.JDialog;
import java.io.*;
import java.util.Vector;
import java.util.Timer;
import java.lang.Thread;
import java.lang.Runnable;

/* This class spawns download threads or upload threads and
 * their associated progressBar threads.
 *
 * @author Khim Ung National Center for Microscopy and Imaging Research
 */
public class UrlTransferThread //implements ThreadUpdateListener
{

	private int TRANSFER_LIST_SIZE = 0;

	/* Performs parallel transfers.
	 * @param title determines if we are downloading or uploading
	 * @param gridSession is used to make a connection to SRB
	 * @param tempLocation is the temporary location on the local machine
	 */
	public UrlTransferThread(Vector gridProgressBars, Vector transferList, String tempLocation, 
		String title, ThreadGroup threadGroup )
	{
		this.gridProgressBars = gridProgressBars;
		this.transferList = transferList;
		this.tempLocation = tempLocation;
		this.title = title;
		this.threadGroup = threadGroup;

		TRANSFER_LIST_SIZE = transferList.size();
		if(DEBUG) System.out.println("UrlTransferThread:initialThreads:TRANSFER_LIST_SIZE:"+TRANSFER_LIST_SIZE);
		startTransfer();
	}

	private void startTransfer()
	{
		initialThreads();
		while(true){
			createNewThread();

			if((threadCounter == 0) && (activeTransfer == TRANSFER_LIST_SIZE))
				break;

			try{
				synchronized(this){
					if(DEBUG) System.out.println("This thread is sleeping");
					this.wait();
				}
				if(DEBUG) System.out.println("sleeping no more");
			}catch(InterruptedException ie){
				JOptionPane.showMessageDialog(null, ""+ie);
			}
		}
		if(DEBUG) System.out.println("Done transfer");
		threadCounter = 0;
		activeTransfer = 0;
	}

	// start the initial MAX_TRANSFER threads
	private void initialThreads(){
		for(int i = 0; i < MAX_TRANSFER; i ++){
			if(DEBUG) System.out.println("initialThreads:activeTransfer:"+activeTransfer);
			if(activeTransfer == TRANSFER_LIST_SIZE)
				break; // break if item in the list is less than the required initial max transfer
			createNewThread();
		}
	}

	// decide if we are downloading or uploading
	private synchronized void createNewThread(){
		if(activeTransfer == TRANSFER_LIST_SIZE) return;
		if(threadCounter < MAX_TRANSFER) 
		{
			if(title.toUpperCase().startsWith("HTTPS"))
				https();
			else
				http();
		}
		else
			return;
	}

	private void http(){
		threadCounter ++;
		if(DEBUG) System.out.println("http:activeTransfer:"+activeTransfer);
		if(DEBUG) System.out.println("http:threadCounter:"+threadCounter);
		String transferFileName = (String)transferList.elementAt(activeTransfer);
		if(DEBUG) System.out.println("http:transferFileName:"+transferFileName);
		TransferProgressPanel bar = (TransferProgressPanel)gridProgressBars.elementAt(activeTransfer);
		bar.setMax(0);

		if(DEBUG)
			System.out.println("download:"+bar.getName());

		HttpTransferThread httpTransferThread = new HttpTransferThread(
			bar, transferFileName, tempLocation, this, title);
		//httpTransferThread.addThreadUpdateListener(this);
		httpTransferThread.start(threadGroup);
		
		activeTransfer++;
	}

	private void https(){
		threadCounter ++;
		String transferFileName = (String)transferList.elementAt(activeTransfer);
		TransferProgressPanel bar = (TransferProgressPanel)gridProgressBars.elementAt(activeTransfer);
		bar.setMax(0);

		if(DEBUG){
			System.out.println("download:"+bar.getName());
			System.out.println(activeTransfer + "activeTransfer" + threadCounter +
				"threadCounter");
		}

		HttpsTransferThread httpsTransferThread = new HttpsTransferThread(
			bar, transferFileName, tempLocation, this, title);
		//httpsTransferThread.addThreadUpdateListener(this);
		httpsTransferThread.start(threadGroup);
		
		activeTransfer++;
	}

	/* DownloadThread is notifying transferThread that it is done 
	 * and to start another thread in its place.
	 * This method has to be synchronized because it is accessed by
	 * multiple threads.  It needs to be locked and done one at a time.
	 */
	public static void decrementThreadCount(){
		threadCounter --;
	}

	public static void incrementThreadCount(){
		threadCounter ++;
	}

	private ThreadGroup threadGroup = null;

	private static int threadCounter = 0;

	/* Instead of a for loop, i just traverse transferList globally */
	private int activeTransfer = 0;

	/* This is the thread that spawns the children threads */
	private Thread transferThread = null;

	/* These are the list of files to transfer */
	private Vector transferList = null;

	/* Temporary location where all files get downloaded */
	private String tempLocation = null;

	/* Title tells me if I am uploading or downloading */
	private String title = null;

	/* This is the ProgressBar gui that needs to get updated */
	private Vector gridProgressBars = null;

	/* This is the maximum number of parallel transfer I can do at a time */
	private final static int MAX_TRANSFER = 4;
	private final static boolean DEBUG = false;
}
