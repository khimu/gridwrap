package edu.ucsd.ncmir.gridwrap.gui;

import edu.ucsd.ncmir.gridwrap.util.SwingWorker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

import java.net.URL;


/* <code>GridWindow</code> displays all the transfers that is being performed 
 * required by this application session.
 *
 * @author Khim Ung National Center for Microscopy and Imaging Research
 */
public class GridWindow extends JFrame
{
	private final static int MAX_TRANSFER_DISPLAY = 5;

  private final static Font gridFont =
	      new Font("helvetica", Font.BOLD, 12);

	private final ClassLoader classLoader = getClass().getClassLoader();
	private final static String LOGO = "images/tplogosmall.gif";

	Border etchedBdr = BorderFactory.createEtchedBorder();

	GridBagConstraints gbc = new GridBagConstraints();
	Container container = null;
	private TransferPanel transferPanel = null;

	private int x = 0;
	private int y = 0;

	static GridWindow window = null;

	private JButton cancelButton = null;

	/* Use this to calculate the size of the ScrollPane */
	private int transferSize = 0;

  /* This is just to show how it is used */
	public static void main(String[] args){
		window = new GridWindow("Download", 5, "/tmp");
		TransferPanel tp = new TransferPanel(5);
		GridProgressPanel bar1 = new GridProgressPanel(120, "abcdfsedwerwufgeslfeigalcdA", window);
		GridProgressPanel bar2 = new GridProgressPanel(120, "12345678901234567890123", window);
		GridProgressPanel bar3 = new GridProgressPanel(120, "teakAKDFAKJFDKJFADKSAKFSFDK", window);
		GridProgressPanel bar4 = new GridProgressPanel(120, "khim", window);
		tp.addPanel(bar1);
		tp.addPanel(bar2);
		tp.addPanel(bar3);
		tp.addPanel(bar4);

		final GridProgressPanel bar5 = new GridProgressPanel(130, "khim", window);
		tp.addPanel(bar5);

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

	public GridWindow(String title, int transferSize, String tmpLocation){
		super(title);
		window = this;

		this.transferSize = transferSize;
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(false);
		container = this.getContentPane();
		container.setLayout(new GridBagLayout());
		container.setBackground(new Color(255,255,255));

		transferPanel = new TransferPanel(5);

	  gbc.insets = new Insets(5,5,5,5);

		JPanel infoPanel = new JPanel(new GridBagLayout());
		infoPanel.setBackground(new Color(255,255,255));
		infoPanel.setBorder(etchedBdr);
		infoPanel.setPreferredSize(new Dimension(370,40)); 
		JLabel cache = new JLabel("Cache:  "+tmpLocation, JLabel.LEFT);
		cache.setFont(gridFont);
		setXY();
		infoPanel.add(cache, gbc);

		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				System.exit(0);
			}
		});
		
		setXY();
		infoPanel.add(cancelButton, gbc);

		/* I add the one to account for the title bar 
		 * I am making the size dynamic so that the window does not look awkward 
		 * If I can figure out how to push my JPanel to the top leaving space 
		 * on the bottom, then I don't need to do this.
		 */
		if(transferSize < MAX_TRANSFER_DISPLAY){
			container.setSize(new Dimension(370, (transferSize+2)*56));
		}else{
			container.setSize(new Dimension(370, (MAX_TRANSFER_DISPLAY+2)*56 ));
		}

	  gbc.insets = new Insets(0,0,0,0);
		gbc.anchor = GridBagConstraints.NORTH;
		gbc.fill = GridBagConstraints.NONE;

		URL logoURL= classLoader.getResource(LOGO);
		setXY();
		container.add(new JLabel(new ImageIcon(logoURL), JLabel.LEFT), gbc);

		setXY();
		container.add(transferPanel, gbc);
		
		setXY();
		container.add(infoPanel, gbc);
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
				transferPanel.addPanel(panel);
				transferPanel.revalidate();
				transferPanel.repaint();
				window.repaint();
				window.validate();
				window.pack();
			}
		};
		worker.start();
	}
}
