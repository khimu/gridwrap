package edu.ucsd.ncmir.gridwrap.thread;

import edu.ucsd.ncmir.grid.*;
import edu.ucsd.ncmir.gridwrap.gui.transfer.*;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.io.*;
import java.util.Vector;
import java.util.Timer;
import java.lang.Thread;
import java.lang.Runnable;


/* This class handles the gui building for upload and download via http, https, grid, and srb.
 * It also starts the thread to perform the transfer and the progressmonitor to monitor
 * the progress of the transfer.  Only one instance of this class needs to be created and
 * used to do transfers for http, https, gird, and srb.  This class will create new thread 
 * instances to handle the transfers and destroys them when they are done.
 *
 * @author Khim Ung National Center for Microscopy and Imaging Research
 */
public class TransferManager 
{
	private TransferManager transferManager = null;

	private TransferWindow window = null;

	private GridTransferThread gridTransferThread = null;

	/* Performs parallel transfers.
	 * Pass in all the transfer list here
	 */
	public TransferManager(int totalSize, String title, GridSession gridSession, TransferWindow window) 
	{
		this.gridSession = gridSession;
		this.window = window;

		threadGroup = new ThreadGroup("Transfer Manager Thread Group");
		transferManager = this;
		gridTransferThread = new GridTransferThread(window, threadGroup, gridSession);
	}

	public void gridTransfer(Vector v, String tempLocation, String title, Vector bars){
		if(v.size() > 0){
			remoteTransfer(v, title, tempLocation, bars);
		}
	}

	public void httpTransfer(Vector v, String tempLocation, String title, Vector bars){
		if(v.size() > 0){
			urlTransfer(v, title, tempLocation, bars);
		}
	}

	public Vector buildGui(Vector transferList, String title, String tempLocation){
		Vector gridProgressBars = new Vector();
		try{
			System.out.println("transferlist size: "+ transferList.size());
			for(int i = 0; i < transferList.size(); i ++){
				String transferFileName = (String)transferList.elementAt(i);
				String fileSep = "/";
				if(transferFileName.indexOf(File.separator) != -1)
					fileSep = File.separator;	

				if(DEBUG)System.out.println("buildGUI:transferFileName:"+transferFileName);
				TransferProgressPanel bar = new TransferProgressPanel(0, transferFileName);
				window.addPanel(bar);
				gridProgressBars.addElement(bar);
				System.out.println("build gui successful");
      	try{
        	Thread.sleep(100);
      	}catch(InterruptedException ie){
					System.out.println("problem building gui.  cant sleep");
        	JOptionPane.showMessageDialog(null, ""+ie);
      	}
				if(DEBUG) System.out.println("TransferManager:transferFileName:"+transferFileName);
			}
		}catch(Exception e){
			System.out.println("problem building gui.  cant sleep");
			JOptionPane.showMessageDialog(null, "Creating gui:"+e);
		}
		return gridProgressBars;
	}

	/* We are purposely not going to handle uploading files that are downloaded via http and https.
	 * Thus, this method will only control the downloading of files via http and https.
	 */
	public void urlTransfer(Vector transferList, 
		String title, String tempLocation, Vector gbars)
	{
		if(DEBUG) System.out.println("urlTransfer:"+transferList.size() + " "+gbars.size());
		UrlTransferThread urlTransfer = new UrlTransferThread(
			gbars, transferList, tempLocation, title, threadGroup);
	}	

	/* This is going to use the SRB protocol to download and upload.
	 * @param	gridSession	The connection parameter for GridService
	 * @param transferList	The list of items to download or upload
	 * @param title	Indicates whether we are downloading or uploading
	 * @param tempLocation will be unique for each transfer since it needs either
	 * srb for srb transfer or grid for grid transfer.
	 */
	public void remoteTransfer(Vector transferList, String title, 
		String tempLocation, Vector gbars)
	{
		if(DEBUG) System.out.println("remoteTransfe:"+transferList.size() + " "+gbars.size());
		gridTransferThread.startTransfer(gbars, transferList, tempLocation, title);
	}

	private Vector threadVector = new Vector();
	private ThreadGroup threadGroup = null;
	private GridSession gridSession = null;
	private final static boolean DEBUG = true;
}
