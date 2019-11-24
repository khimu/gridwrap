package edu.ucsd.ncmir.gridwrap.thread;

import edu.ucsd.ncmir.grid.*;
import edu.ucsd.ncmir.grid.impl.BaseGridService;
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
//public class GridTransferThread 
public class GridTransferThread 
{
	private int TRANSFER_LIST_SIZE = 0;

	/* Performs parallel transfers.
	 * @param parent is the Gui window containing all the progress bar
	 * @param threadGroup groups all the transfer threads together
	 * @param gridSession is the user's GridService parameter
	 */
	public GridTransferThread(TransferWindow parent, ThreadGroup threadGroup,
		GridSession gridSession)
	{
		this.threadGroup = threadGroup;
		this.gridSession = gridSession;
		this.parentFrame = parent;
	}

	/*
	 * @param gridProgressBars is the list of gui progress bar for this transfer
	 * @param transferList is the list of paths to transfer
	 * @param tempLocation is the local temporary location for the transfer
	 * @param title indicates an upload or download
	 */
	public void startTransfer(Vector gridProgressBars,
		Vector transferList, String tempLocation, String title)
	{
		this.gridProgressBars = gridProgressBars;
		this.transferList = transferList;
		this.tempLocation = tempLocation;
		this.title = title;
		this.threadCounter = 0;
		this.activeTransfer = 0;

		TRANSFER_LIST_SIZE = transferList.size();
		if(DEBUG) System.out.println("TRANSFER_LIST_SIZE:"+TRANSFER_LIST_SIZE);

		initialThreads();

		while(true){
			if((threadCounter == 0) && (activeTransfer == TRANSFER_LIST_SIZE)){
				System.out.println("exiting transfer");
				if(DEBUG){
				System.out.println("startTransfer:exiting");
				}
				break; // No more thread to create and last thread completed
			}

			createNewThread();

			if(DEBUG){
			System.out.println("startTransfer:threadCounter:"+threadCounter);
			System.out.println("startTransfer:activeTransfer:"+activeTransfer);
			}

			if(!(threadCounter <= 0) && (threadCounter >= MAX_TRANSFER) || 
			    (activeTransfer == TRANSFER_LIST_SIZE)){
				try{
					synchronized(this){
						System.out.println("thread is waiting");
						this.wait();
						System.out.println("thread is awake");
					}
				if(DEBUG) System.out.println("Thread is alive:"+threadCounter);

				}catch(InterruptedException e){
					JOptionPane.showMessageDialog(null, "GridTransferThread:startTransfer:"+e);
					System.out.println(""+e);
				}
			}
		}	
	}

	// start the initial MAX_TRANSFER threads
	private void initialThreads(){
		try{
			// Not using thread count since parent thread only 
			// wakes up after one of the 4 threads is done.
			for(int i = 0; i < MAX_TRANSFER; i ++){ 

				if(activeTransfer == TRANSFER_LIST_SIZE){
					if(DEBUG) System.out.println("initialThreads:activeTransfer==MAX_SIZE");
					break;  // the list of items to transfer is less than the MAX_TRANSFER
				}

				if(connections[i] == null){
					connections[i] = new BaseGridService(gridSession);
					connections[i].authenticate();
				}

				// Set up a free connection to use
				freeConnection.addElement( (new Integer(i)).toString() );
				createNewThread();
			}
		}catch(IOException io){
			JOptionPane.showMessageDialog(null, "GridTransferThread:initialThreads:"+io);
		}
		catch(Exception e){
			JOptionPane.showMessageDialog(null, "GridTransferThread:initialThreads:"+e);
		}
	}

	// decide if we are downloading or uploading
	private synchronized void createNewThread(){
		if(activeTransfer == TRANSFER_LIST_SIZE){
			if(DEBUG) System.out.println("createNewThread:activeTransfer == MAX");
			return;  //  check if we still have more transfer to do
		}

		// Check if we have any free connections and that they are less than the 
		// MAX_TRANSFER
		if((freeConnection.size() > 0) && (freeConnection.size() <= MAX_TRANSFER))
		{
			int fc = (new Integer((String)freeConnection.firstElement())).intValue();
			if(title.toLowerCase().indexOf("download") != -1){
				System.out.println("calling upload");
				download(fc);
			}else{
				System.out.println("calling upload");
				upload(fc);
			}
			freeConnection.remove((new Integer(fc)).toString());
		}
		else{
			if(DEBUG) System.out.println("createNewThread:no free connection");	
			return;
		}
	}

	/* The localpath from transferList needs to be created by appending the tempLocation to
	 * the name.  The downloadPath should already be in transferList in the form that it is there.
	 */
	private synchronized void upload(int fc){
		try{
			incrementThreadCount();
			GridService aConnection = connections[fc];
			TransferProgressPanel bar = (TransferProgressPanel)gridProgressBars.elementAt(activeTransfer);
			/* This is the local path with the local temp */
			String localfile = (String)transferList.elementAt(activeTransfer);
			if(localfile.endsWith("/"))
      localfile = localfile.substring(0, localfile.length() -1);

			// This is for UploadPanel
			String transferFileName = tempLocation + "/" + (new File(localfile)).getName();

			if(!tempLocation.startsWith(gridSession.getHome())){
				String[] tmp = localfile.split(tempLocation);
				transferFileName = removeProtocol(tmp[1]);
				transferFileName = transferFileName.replace(File.separatorChar, '/');
			}

			// Creating our srb file
			if(DEBUG) System.out.println("upload:transferFileName:"+transferFileName);
			if(DEBUG) System.out.println("localfile:"+localfile);
			System.out.println("upload:transferFileName:"+transferFileName);
			System.out.println("localfile:"+localfile);

			// Need to create an UploadThread to use here
			UploadThread uploadThread = new UploadThread(aConnection, 
				transferFileName, localfile, this, fc, bar);
			uploadThread.start(threadGroup);
			
			activeTransfer++;
		}catch(Exception e888){
			JOptionPane.showMessageDialog(null, ""+e888);
			synchronized(this){
				this.notify();
			}
		}
	}

	// Start downloading
	private synchronized void download(int fc){
		try{
			incrementThreadCount();
			GridService aConnection = connections[fc];
			String transferFileName = (String)transferList.elementAt(activeTransfer);
			TransferProgressPanel bar = (TransferProgressPanel)gridProgressBars.elementAt(activeTransfer);

			String localdownloadpath = transferFileName.replace('/',File.separatorChar);
			File localfile = new File(tempLocation + File.separator + localdownloadpath);
			if(DEBUG) System.out.println("localfile:"+localfile.getPath());
			if(DEBUG) System.out.println("srbfile:"+transferFileName);

			if(!aConnection.exists(transferFileName)){
				System.out.println("download path does not exist");
				//bar.setProgressValue((new Long(aConnection.length(transferFileName))).intValue(), true);
				bar.setProgressValue(1, true);
				decrementThreadCount();
				activeTransfer++;
				threadFinished((new Integer(fc)).toString());
				synchronized(this){
					this.notify();
				}
				return;
			}
		
			File parentFile = new File(localfile.getParent());

			if(!parentFile.exists())
				// Creating a local directory
				if(!parentFile.mkdirs())
					JOptionPane.showMessageDialog(null, "Unable to create local directories for file downloading");

			// Start DownloadThread
			DownloadThread downloadThread = new DownloadThread(aConnection, 
				transferFileName, localfile.getAbsolutePath(), this, fc, bar);
			downloadThread.start(threadGroup);

			activeTransfer++;
		}
		catch(Exception e888){
			JOptionPane.showMessageDialog(null, ""+e888);
			synchronized(this){
				this.notify();
			}
		}
	}

	private String removeProtocol(String path){
		if(path.startsWith("/srb")){
			String[] tmp = path.split("/srb");
			return tmp[1];
		}
		else if(path.startsWith("/grid")){
			String[] tmp = path.split("/grid");
			return tmp[1];
		}
		else if(path.startsWith("/http")){
			String[] tmp = path.split("/http");
			path = gridSession.getHome() + "/" + tmp[1];
			return path;
		}
		else if(path.startsWith("/https")){
			String[] tmp = path.split("/https");
			path = gridSession.getHome() + "/" + tmp[1];
			return path;
		}else{
			return path;
		}
	}

	/* DownloadThread is notifying gridTransferThread that it is done 
	 * and to start another thread in its place.
	 */
	public synchronized void threadFinished(String threadName){
		int fc = (new Integer(threadName)).intValue();
		freeConnection.addElement( (new Integer(fc)).toString() );
	}

	// This is more accurate since we know for sure the thread has started or ended
	public synchronized void decrementThreadCount(){
		if(DEBUG) System.out.println("decrement:threadCounter"+threadCounter);
		threadCounter --;
		if(DEBUG) System.out.println("decrement:threadCounter"+threadCounter);
	}

	public synchronized void incrementThreadCount(){
		if(DEBUG) System.out.println("increment:threadCounter"+threadCounter);
		threadCounter ++;
		if(DEBUG) System.out.println("increment:threadCounter"+threadCounter);
	}

	/* This groups all the transfer threads into a group to keep as a counter and used
	 * globally.
	 */
	private ThreadGroup threadGroup = null;

	private int threadCounter = 0;

	/* Tells me which thread died and which GridService to use */
	private Vector freeConnection = new Vector();

	/* Instead of a for loop, i just traverse transferList globally */
	private int activeTransfer = 0;

	/* This is the thread that spawns the children threads */
	private Thread gridTransferThread = null;

	/* These are the list of files to transfer */
	private Vector transferList = null;

	private String tempLocation = null;

	/* Title tells me if I am uploading or downloading */
	private String title = null;

	/* This is the SRB connection class */
	private GridService[] connections = new GridService[MAX_TRANSFER];

	/* User's GridService parameter */
	private GridSession gridSession = null;

	/* This is the ProgressBar gui that needs to get updated */
	private Vector gridProgressBars = null;

	private TransferWindow parentFrame = null;

	/* This is the maximum number of parallel transfer I can do at a time */
	private final static int MAX_TRANSFER = 4;
	private final static boolean DEBUG = true;
}
