package edu.ucsd.ncmir.gridwrap.thread;

import edu.ucsd.ncmir.grid.*;
import edu.ucsd.ncmir.gridwrap.gui.transfer.*;

import edu.sdsc.grid.io.GeneralFile;
import edu.sdsc.grid.io.srb.SRBFile;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.io.*;
import java.util.Vector;
import java.util.Timer;
import java.lang.Thread;
import java.lang.Runnable;


/* This class updates the progress bar gui at every half second 
 * for the associated transfer thread.  It uses the SRBFile instance
 * to obtain the filesize transfered thus far.
 *
 * @author Khim Ung National Center for Microscopy and Imaging Research
 **/
public class GridProgressThread implements Runnable, TransferByteSizeInterface
{
	private Vector bytesizelisteners = new Vector();

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
					listener.setBytesTransfered(bytes, getSuccess() );
			}
		}
	}


	/* Progress Bar GUI updater thread for Grid Transfers
	 * @param parent is the parent window that is displaying all the transfers
	 * @param gpanel is a reference to the GUI progress bar.
	 * @param aConnection is an SRB Connection
	 * @param transferFile is a general file instance of an SRBFile
	 * @param totalBytes is the total byte size to download.
	 */
	public GridProgressThread(TransferProgressPanel gpanel,
		GridService aConnection, GeneralFile transferFile, 
		int totalBytes, Thread parentThread)
	{
		this.gpanel = gpanel;
		this.aConnection = aConnection;
		this.transferFile = transferFile;
		this.totalBytes = totalBytes;
		this.parentThread = parentThread;
	}

	public void start(){
		if(gridProgressThread == null){
			gridProgressThread = new Thread(this);
			gridProgressThread.start();
		}
	}

	public void setPriority(int priority){
		gridProgressThread.setPriority(priority);
	}

	public void run(){
		ProgressBarThread progressBarThread = new ProgressBarThread(gpanel, this);	
		showProgress();
	}

	private void showProgress(){
		try{

			// Make sure we have an instance of an SRBFile
			if(transferFile instanceof SRBFile){
				SRBFile srbFile = (SRBFile) transferFile;
				int bytes_transfered = 0;
				// This is here to let the user know that SRB is still downloading dispite the 
				// delay in returning the bytes transfered thus far.
				int my_hack_bytes_transfered = 1;
				while(true){
					synchronized (this) {

						// Getting the bytes transfered from SRBFile
						bytes_transfered = (new Long(srbFile.fileCopyStatus())).intValue();

						if(bytes_transfered > 0)
							// Notify the progress bar gui
							fireTransferByteSizeEvent(new TransferByteSizeEvent(this, TransferByteSizeEvent.BYTE), bytes_transfered);
						else{
							// Our hack so the user does not see a none moving progress bar
							my_hack_bytes_transfered += 1024*10;
							fireTransferByteSizeEvent(new TransferByteSizeEvent(this, TransferByteSizeEvent.BYTE), my_hack_bytes_transfered);

						}
					
						// Update progress bar gui and exit
						if(bytes_transfered == totalBytes){
							fireTransferByteSizeEvent(new TransferByteSizeEvent(this, TransferByteSizeEvent.BYTE), totalBytes);
							gridProgressThread.sleep(100);
							gpanel.setMax(-1);
							break;
						}

						// Our transfer thread has ended.  Update progress bar gui and exit
						if(parentThread != null)
							if(parentThread.isAlive() == false){
								fireTransferByteSizeEvent(new TransferByteSizeEvent(this, TransferByteSizeEvent.BYTE), totalBytes);
								gridProgressThread.sleep(100);
								gpanel.setMax(-1);
								break;
							}
					}
					// Put this thread to sleep before querying for the transfered byte size again
					gridProgressThread.sleep(500);
				}
			}// end transferFile instanceof SRBFile
		}catch(InterruptedException e22){
			JOptionPane.showMessageDialog(null, e22);
		}
	}

	/* Should get rid of */
	private synchronized boolean getSuccess(){
		return SUCCESSFUL;
	}

	/* Should get rid of */
	private synchronized void setSuccess(boolean success){
		SUCCESSFUL = success;
	}

	private boolean SUCCESSFUL = true;

	/* This is not really a thread.  Should change the name. 
	 * It starts the progress Monitor 
	 */
	private ProgressBarThread progressBarThread = null;

	/* Associate transfer thread */
	private Thread parentThread = null;

	/* The thread for this class */
	private Thread gridProgressThread = null;

	/* total bytes to be transfered */
	private int totalBytes = 0;

	/* File being transfered */
	private GeneralFile transferFile = null;

	/* Our SRB Connection instance */
	private GridService aConnection= null;

	/* A reference to this progress bar gui */
	private TransferProgressPanel gpanel = null;
	private final static boolean DEBUG = false;
}
