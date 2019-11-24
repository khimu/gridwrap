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


/* This class starts the bytes transfer monitor and updates the
 * bytes transfered thus far.
 *
 * @author Khim Ung National Center for Microscopy and Imaging Research
 */
public class ProgressBarThread implements TransferByteSizeListener
{
	/* Performs parallel transfers.
	 * @param title determines if we are downloading or uploading
	 * @param gridSession is used to make a connection to SRB
	 * @param tempLocation is the temporary location on the local machine
	 */
	public ProgressBarThread(TransferProgressPanel gpanel,
		TransferByteSizeInterface transferInterface)
	{
		this.gpanel = gpanel;
		transferInterface.addTransferByteSizeListener(this);

		startProgressBar();

	}

	public void startProgressBar(){
		gpanel.setStringValue(null);
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				gpanel.start();
			}
		});
	}

	public void setBytesTransfered(final int bytes, final boolean successful){
		gpanel.setProgressValue(bytes, successful);
	}

	private TransferProgressPanel gpanel = null;
	private final static boolean DEBUG = false;
}
