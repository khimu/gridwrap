package edu.ucsd.ncmir.gridwrap.gui;

import edu.ucsd.ncmir.gridwrap.util.SwingWorker;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

import java.net.URL;


/* <code>TransferPanel</code> displays all the transfers that is being performed 
 * required by this application session.
 *
 * @author Khim Ung National Center for Microscopy and Imaging Research
 */
public class TransferPanel extends JPanel
{
	private final static int MAX_TRANSFER_DISPLAY = 5;

  private final static Font gridFont =
	      new Font("helvetica", Font.BOLD, 12);

	private final ClassLoader classLoader = getClass().getClassLoader();
	private final static String LOGO = "images/tplogosmall.gif";

	Border etchedBdr = BorderFactory.createEtchedBorder();

	GridBagConstraints gbc = new GridBagConstraints();
	private JPanel mainPanel = null;

	private int x = 0;
	private int y = 0;

	//static TransferPanel window = null;

	/* Use this to calculate the size of the ScrollPane */
	private int transferSize = 0;

	/* Used for testing out this gui */
	public static void makePanel(final int max, final GridProgressPanel bar){
		bar.setStringValue("Queued");
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				bar.start();
			}
		});

		Thread t = new Thread(new Runnable(){
			public void run(){
				int i = 0;
				while(i <= max){
					bar.setProgressValue(i++, true);
					try{
						Thread.sleep(1000);
					}catch(InterruptedException e44){
						JOptionPane.showMessageDialog(null, e44);
					}
				}
				bar.setProgressValue(i++, true);
			}
		});
		t.start();

	}

	public TransferPanel(int transferSize){

		this.transferSize = transferSize;
		
		this.setLayout(new GridBagLayout());
		this.setBackground(new Color(255,255,255));

		gbc.fill = GridBagConstraints.NONE;

		mainPanel = new JPanel();
		mainPanel.setLayout(new GridBagLayout());
		mainPanel.setBackground(new Color(255,255,255));

	  gbc.insets = new Insets(0,0,0,0);
		gbc.anchor = GridBagConstraints.NORTH;

		JScrollPane scrollPane = new JScrollPane(mainPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
					JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		/* I add the one to account for the title bar 
		 * I am making the size dynamic so that the window does not look awkward 
		 * If I can figure out how to push my JPanel to the top leaving space 
		 * on the bottom, then I don't need to do this.
		 */
		if(transferSize < MAX_TRANSFER_DISPLAY){
			scrollPane.setPreferredSize(new Dimension(370,(transferSize)*56));
			this.setSize(new Dimension(370, (transferSize+1)*56 ));
		}else{
			scrollPane.setPreferredSize(new Dimension(370,(MAX_TRANSFER_DISPLAY)*56));
			this.setSize(new Dimension(370, (MAX_TRANSFER_DISPLAY+1)*56 ));
		}

		setXY();
		this.add(scrollPane, gbc);
	}

	private void setXY(){
		gbc.gridx = x;
		gbc.gridy = y++;
	}

	public void addPanel(final GridProgressPanel panel){
		SwingWorker worker = new SwingWorker(){
			public void doNonUILogic(){
			}
			public void doUIUpdateLogic(){
				gbc.gridx = x;
				gbc.gridy = y++;
				mainPanel.add(panel, gbc);
			}
		};
		worker.start();
	}
}
