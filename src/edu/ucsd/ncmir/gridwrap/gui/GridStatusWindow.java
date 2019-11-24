package edu.ucsd.ncmir.gridwrap.gui;

import edu.ucsd.ncmir.gridwrap.util.SwingWorker;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

import java.net.URL;


/* <code>GridStatusWindow</code> displays all the transfers that is being performed 
 * required by this application session.
 *
 * @author Khim Ung National Center for Microscopy and Imaging Research
 */
public class GridStatusWindow extends JFrame
{
	private final static int MAX_TRANSFER_DISPLAY = 5;
  private final static Font gridFont =
	      new Font("helvetica", Font.BOLD, 12);

	private final ClassLoader classLoader = getClass().getClassLoader();
	private final static String LOGO = "images/tplogosmall.gif";
	//private final static String LOGO = "images/telescience_logo.gif";

	Border etchedBdr = BorderFactory.createEtchedBorder();

	GridBagConstraints gbc = new GridBagConstraints();
	Container container = null;
	private JPanel mainPanel = null;

	private int x = 0;
	private int y = 0;

	static GridStatusWindow window = null;

	/* Use this to calculate the size of the ScrollPane */
	private int transferSize = 0;

  /* This is just to show how it is used */
	public static void main(String[] args){
		window = new GridStatusWindow("Download", 5);
		GridProgressPanel bar1 = new GridProgressPanel(120, "abcdfsedwerwufgeslfeigalcdA", window);
		GridProgressPanel bar2 = new GridProgressPanel(120, "12345678901234567890123", window);
		GridProgressPanel bar3 = new GridProgressPanel(120, "teakAKDFAKJFDKJFADKSAKFSFDK", window);
		GridProgressPanel bar4 = new GridProgressPanel(120, "khim", window);
		window.addPanel(bar1);
		window.addPanel(bar2);
		window.addPanel(bar3);
		window.addPanel(bar4);

		final GridProgressPanel bar5 = new GridProgressPanel(130, "khim", window);
		window.addPanel(bar5);

		window.pack();
		window.show();

		makePanel(120, bar1);
		makePanel(120, bar2);
		makePanel(120, bar3);
		makePanel(120, bar4);

		SwingWorker worker = new SwingWorker(){
			public void doNonUILogic(){
					try{
						Thread.sleep(10000);
					}catch(InterruptedException e44){
						JOptionPane.showMessageDialog(null, e44);
					}
					makePanel(130, bar5);
			}
			public void doUIUpdateLogic(){
				window.repaint();
				window.validate();
				window.pack();
			}
		};
		worker.start();
	}

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

	public GridStatusWindow(String title, int transferSize){
		super(title);
		window = this;

		this.transferSize = transferSize;
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(false);
		container = this.getContentPane();
		container.setLayout(new GridBagLayout());
		container.setBackground(new Color(255,255,255));

		JLabel fileName = new JLabel("File Name", JLabel.CENTER);
		fileName.setPreferredSize(new Dimension(160, 24));
		fileName.setFont(gridFont);
		fileName.setForeground(Color.yellow);
		JLabel transferBar = new JLabel("Transfer Progress", JLabel.CENTER);
		transferBar.setPreferredSize(new Dimension(150, 24));
		transferBar.setFont(gridFont);
		transferBar.setForeground(Color.white);
		JLabel bytesTransfered = new JLabel("Bytes Transfered", JLabel.CENTER);
		bytesTransfered.setPreferredSize(new Dimension(150, 24));
		bytesTransfered.setFont(gridFont);
		bytesTransfered.setForeground(Color.white);

		JLabel colName = new JLabel("total", JLabel.RIGHT);
		colName.setPreferredSize(new Dimension(150, 10));
		colName.setFont(new Font("Arial", Font.PLAIN, 8));

		JPanel panel = new JPanel(new GridBagLayout());
		panel.setLayout(new GridBagLayout());
		panel.setPreferredSize(new Dimension(370, 10));
		//panel.setBackground(new Color(00,34,102));
		//panel.setBackground(new Color(162,181,205));

		gbc.fill = GridBagConstraints.NONE;
	  //gbc.insets = new Insets(5,5,5,5);
		gbc.gridx = 0;
		gbc.gridy = 0;

		panel.add(colName);
/*
		gbc.gridx = 1;
		gbc.gridy = 0;
		
		//panel.add(transferBar, gbc);

		gbc.gridx = 2;
		gbc.gridy = 0;
*/
		//panel.add(bytesTransfered, gbc);

		mainPanel = new JPanel();
		mainPanel.setLayout(new GridBagLayout());
		mainPanel.setBackground(new Color(255,255,255));
		//mainPanel.setBackground(new Color(191,191,191));
		//mainPanel.setBorder(GridBorder.GridTitledBorder(title, (new Color(198,226,225)), (new Color(198,226,225)), (new Color(0,0,0)) ));

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
			container.setSize(new Dimension(370, (transferSize+1)*56 ));
		}else{
			scrollPane.setPreferredSize(new Dimension(370,(MAX_TRANSFER_DISPLAY)*56));
			container.setSize(new Dimension(370, (MAX_TRANSFER_DISPLAY+1)*56 ));
		}

		URL logoURL= classLoader.getResource(LOGO);
		setXY();
		container.add(new JLabel(new ImageIcon(logoURL), JLabel.LEFT), gbc);

		//setXY();
		//container.add(panel, gbc);

		setXY();
		container.add(scrollPane, gbc);
	}

	private void setXY(){
		gbc.gridx = x;
		gbc.gridy = y++;
	}

	public void destroyWindow(){
		SwingWorker worker = new SwingWorker(){
			public void doNonUILogic(){
			}
			public void doUIUpdateLogic(){
				window.hide();
				window.dispose();
			}
		};
		worker.start();
	}

	public void hideWindow(){
		SwingWorker worker = new SwingWorker(){
			public void doNonUILogic(){
			}
			public void doUIUpdateLogic(){
				window.hide();
			}
		};
		worker.start();
	}

	public void showWindow(){
		SwingWorker worker = new SwingWorker(){
			public void doNonUILogic(){
			}
			public void doUIUpdateLogic(){
				window.show();
				window.pack();
			}
		};
		worker.start();
	}

	public void addPanel(final GridProgressPanel panel){
		SwingWorker worker = new SwingWorker(){
			public void doNonUILogic(){
			}
			public void doUIUpdateLogic(){
				gbc.gridx = x;
				gbc.gridy = y++;
				mainPanel.add(panel, gbc);
				window.repaint();
				window.validate();
				window.pack();
			}
		};
		worker.start();
	}
}
