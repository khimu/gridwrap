package edu.ucsd.ncmir.gridwrap.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.lang.Integer;

/* <code>GridProgressPanel</code> displays a transfer's progress, active status,
 * and file name.
 * 
 * @author Khim Ung National Center for Microscopy and Imaging Research
 */
public class GridProgressPanel extends JPanel implements Runnable
{
	private final static boolean DEBUG = false;

	private final static String SYMBOL_PERCENTAGE = "/";
	private final static String SYMBOL_DIVIDE = "\\";

	/* This is how many characters the filename label can hold */
	private final static int FILENAME_WIDTH = 30;

	private final static Font gridFont = 
			new Font("courier", Font.BOLD, 10);

	private boolean toggleSymbol = true;
	
	private ProgressMonitor progressMonitor = null;
	private GridProgressBar gridProgressBar = null;
	private JLabel fileNameLabel = null;
	private JLabel percentageTransferedLabel = null;
	private int VALUE = 0;

	private Thread drawSymbolThread = null;

	private JFrame parent = null;

	private Object MAX = null;

	public static void main(String[] args){
		JFrame frame = new JFrame("Test GridProgressPanel");
		final GridProgressPanel self = new GridProgressPanel(19, "khim", frame);
		frame.getContentPane().add("Center", self);
		frame.setResizable(false);
		frame.pack();
		frame.show();

		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				self.start();
			}
		});

    if( SwingUtilities.isEventDispatchThread() ){
			if(DEBUG) System.out.println("main:I do not need SwingUtilities.invokeLater here.");
		}

		int i = 0;
		while(i < 20){
			self.setProgressValue(i++, true);
			try{
				Thread.sleep(1000);
			}catch(InterruptedException e44){
				JOptionPane.showMessageDialog(null, e44);
			}
		}

		self.setProgressValue(i++, true);
	}

	public GridProgressPanel(int max, String fileName, JFrame parent){

		if(max == 0){
			this.MAX = "?";
		}else
			this.MAX = new Integer(max);
		this.VALUE = 0;
		this.parent = parent;

		/* Set up the progress bar */
		gridProgressBar = new GridProgressBar(new JTextField("Queued"));
		gridProgressBar.start(max);
		gridProgressBar.setPreferredSize(new Dimension(150, 20));

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
		this.setPreferredSize(new Dimension(350,55)); // Was working
		this.setLayout(new GridBagLayout());
		this.setBackground(new Color(255,255,255));
		Border etchedBdr = BorderFactory.createEtchedBorder();
		this.setBorder(GridBorder.GridTitledBorder("   File Name:  "+fileName + "   ", (new Color(00,34,102)), (new Color(255,255,255)), (new Color(0,0,0)) ));

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(0,5,0,5);
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 0;
		gbc.gridy = 0;
		this.add(gridProgressBar, gbc);

		gbc.gridx = 1;
		gbc.gridy = 0;

		this.add(percentageTransferedLabel, gbc);
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
					gridProgressBar.setMaximum(((Integer)MAX).intValue());
			}
		});
	}

	synchronized public void setStringValue(final String val){
  	SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				gridProgressBar.setString(val);
			}
		});
	}

	synchronized public void setIndeterminate(final boolean b){
  	SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				gridProgressBar.setIndeterminate(b);
			}
		});
	}

	synchronized public void changeBackground(){
  	SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				//gridProgressBar.setBackground(new Color(65,105,225));
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
				gridProgressBar.step(VALUE, getSuccess());
				gridProgressBar.paintImmediately(gridProgressBar.getVisibleRect());
			}
		});
	}

	public synchronized void setError(){
  	SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				setSuccess(false);
				gridProgressBar.setString("Transfer Error");
				gridProgressBar.setIndeterminate(false);
				setMax(-1);
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
