package edu.ucsd.ncmir.gridwrap.gui.transfer;

import edu.ucsd.ncmir.gridwrap.gui.component.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.lang.Integer;

/* <code>TransferProgressPanel</code> displays a transfer's progress, active status,
 * and file name.
 * 
 * @author Khim Ung National Center for Microscopy and Imaging Research
 */
public class TransferProgressPanel extends JPanel implements Runnable
{
	private final static boolean DEBUG = false;

	private final static String SYMBOL_PERCENTAGE = "/";
	private final static String SYMBOL_DIVIDE = "\\";

	/* This is how many characters the filename label can hold */
	private final static int FILENAME_WIDTH = 30;

	private final static int WIDTH = 350;
	private final static int HEIGHT = 55;

	private final static Font gridFont = 
			new Font("courier", Font.BOLD, 10);

	private boolean toggleSymbol = true;
	
	private ProgressMonitor progressMonitor = null;
	private CustomProgressBar transferProgressBar = null;
	private JLabel fileNameLabel = null;
	private JLabel percentageTransferedLabel = null;
	private int VALUE = 0;

	private Thread drawSymbolThread = null;

	private Object MAX = null;

	public TransferProgressPanel(int max, String fileName){

		System.out.println("creating TransferProgressPanel.");

		if(max == 0){
			this.MAX = "?";
		}else
			this.MAX = new Integer(max);
		this.VALUE = 0;

		/* Set up the progress bar */
		transferProgressBar = new CustomProgressBar(new JTextField("Queued"));
		transferProgressBar.start(max);
		transferProgressBar.setPreferredSize(new Dimension(150, 20));

		/* Create the percentage transfered label */
		if(max > 0)
			percentageTransferedLabel = new JLabel("0 / "+((Integer)MAX).intValue(), JLabel.RIGHT);
		else{
			percentageTransferedLabel = new JLabel("? / ?", JLabel.RIGHT);
			MAX = "?";
		}

		percentageTransferedLabel.setPreferredSize(new Dimension(150, 10));
		percentageTransferedLabel.setFont(gridFont);
		percentageTransferedLabel.setForeground(Color.black);

		/* Before adding the name, check the size of the name so that
		 * all the characters will fit.
		 */
		if(fileName.length() > FILENAME_WIDTH){
			int removeChars = fileName.length() - FILENAME_WIDTH;
			// The 3 is to add in the ... at the front of the name 
			removeChars += 3;
			fileName = fileName.substring(removeChars, fileName.length());
			fileName = "..."+fileName;
		}

		/* Now that I have everything I need, lets create this JPanel */
		this.setPreferredSize(new Dimension(WIDTH,HEIGHT)); // Was working
		this.setLayout(new GridBagLayout());
		this.setBackground(new Color(255,255,255));
		Border etchedBdr = BorderFactory.createEtchedBorder();
		this.setBorder(CustomBorder.CustomTitledBorder("   File Name:  "+fileName + "   ", (new Color(00,34,102)), (new Color(255,255,255)), (new Color(0,0,0)) ));

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(0,5,0,5);
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 0;
		gbc.gridy = 0;
		this.add(transferProgressBar, gbc);

		gbc.gridx = 1;
		gbc.gridy = 0;

		this.add(percentageTransferedLabel, gbc);
		System.out.println("done creating gridprogressbar");
	}

	public String getName(){
		return fileNameLabel.getText();
	}	

	public void start(){
		if(drawSymbolThread == null){
			drawSymbolThread = new Thread(this, "Transfer Symbol");
			drawSymbolThread.setPriority(Thread.MIN_PRIORITY);
			drawSymbolThread.start();
		}
	}

	/* This is to help show that the application has not frozen */
	public void run(){
    if( !SwingUtilities.isEventDispatchThread() ){
			if(DEBUG) System.out.println("need SwingUtilities.invokeLater here.");
		}

		while(true){

			try{
				drawSymbolThread.sleep(100);
			}catch(InterruptedException e44){
				JOptionPane.showMessageDialog(null, ""+e44);
			}

			synchronized (this) {
				if(MAX == null)
					break;
				if(MAX instanceof Integer){
					if(toggleSymbol == true){
						percentageTransferedLabel.setText("     "+VALUE+" "+
								SYMBOL_DIVIDE+" "+((Integer)MAX).intValue());
						toggleSymbol = false;
					}else{
						percentageTransferedLabel.setText("     "+VALUE+" "+
								SYMBOL_PERCENTAGE+" "+((Integer)MAX).intValue());
						toggleSymbol = true;
					}

					if(VALUE == ((Integer)MAX).intValue())
						break;
				}else{
					if(toggleSymbol == true){
						percentageTransferedLabel.setText("     "+VALUE+" "+
								SYMBOL_DIVIDE+" "+MAX);
						toggleSymbol = false;
					}else{
						percentageTransferedLabel.setText("     "+VALUE+" "+
								SYMBOL_PERCENTAGE+" "+MAX);
						toggleSymbol = true;
					}
				}
			}
		}
	}

	/* Just in case we need to change the max */
	synchronized public void setMax(final int max){
		if(max == -1){
			this.MAX = null;
			return;
		}
		else if(max == 0){
			this.MAX = "?";
			setIndeterminate(true);
		}else{
			this.MAX = new Integer(max);
			setIndeterminate(false);
		}

  	SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				if(MAX instanceof Integer)
					transferProgressBar.setMaximum(((Integer)MAX).intValue());
			}
		});
	}

	synchronized public void setStringValue(final String val){
  	SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				transferProgressBar.setString(val);
			}
		});
	}

	synchronized public void setIndeterminate(final boolean b){
  	SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				transferProgressBar.setIndeterminate(b);
			}
		});
	}

	synchronized public void changeBackground(){
  	SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				//transferProgressBar.setBackground(new Color(65,105,225));
			}
		});
	}

	/* This is to draw the progress bar with the new transfer value */
	synchronized public void setProgressValue(int val, boolean success){
		if(DEBUG) System.out.println("Got called:"+getSuccess());
    if( SwingUtilities.isEventDispatchThread() ){
			if(DEBUG) System.out.println("start:I do not need SwingUtilities.invokeLater here.");
		}
		this.VALUE += val;
		//System.out.println(fileNameLabel.getText());
		//System.out.println("VALUE:"+VALUE);
  	SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				transferProgressBar.step(VALUE, getSuccess());
				transferProgressBar.paintImmediately(transferProgressBar.getVisibleRect());
			}
		});
	}

	public synchronized void setError(final String s){
  	SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				setSuccess(false);
				System.out.println("setError called:"+getSuccess());
				transferProgressBar.setString("Transfer Error");
				transferProgressBar.setIndeterminate(false);
				setMax(-1);
			}
		});
	}

	public synchronized void done(final String s){
  	SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				System.out.println("done called:"+getSuccess());
			}
		});
	}

	public synchronized void setSuccess(boolean success){
		SUCCESSFUL = success;
	}

	public synchronized boolean getSuccess(){
		return SUCCESSFUL;
	}

	private boolean SUCCESSFUL = true;
}
