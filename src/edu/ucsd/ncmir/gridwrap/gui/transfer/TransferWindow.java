package edu.ucsd.ncmir.gridwrap.gui.transfer;

import edu.ucsd.ncmir.gridwrap.*;
import edu.ucsd.ncmir.gridwrap.local.*;
import edu.ucsd.ncmir.gridwrap.gui.*;
import edu.ucsd.ncmir.gridwrap.gui.util.*;
import edu.ucsd.ncmir.gridwrap.util.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

import java.net.URL;
import java.io.File;


/* <code>TransferWindow</code> displays all the transfers that is being performed 
 * required by this application session.
 *
 * @author Khim Ung National Center for Microscopy and Imaging Research
 */
public class TransferWindow extends JFrame//JPanel
{
	private final static int MAX_TRANSFER_DISPLAY = 5;
	private final static int WIDTH = 370;
	private final static int HEIGHT = 56;

  private final static Font gridFont =
	      new Font("helvetica", Font.BOLD, 12);

	private final ClassLoader classLoader = getClass().getClassLoader();
	private final static String LOGO = "images/tplogosmall.gif";

	Border etchedBdr = BorderFactory.createEtchedBorder();

	GridBagConstraints gbc = new GridBagConstraints();
	private TransferPanel transferPanel = null;

	private static TransferWindow instance = null;

	private int x = 0;
	private int y = 0;

	/* Use this to calculate the size of the ScrollPane */
	private int transferSize = -1;

	/* Used for testing out this gui */
	public static void makePanel(final int max, final TransferProgressPanel bar){
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

	public TransferWindow(String title, int transferSize, String tmpLocation){
		super(title);
		instance = this;
		    // changing the icon on the left-top corner of the titlebar
    ImageIcon icon = new ImageIcon(
      classLoader.getResource("images/xp-control-panel.png"));
    this.setIconImage(icon.getImage());
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setLocation(new Point(0,0));
    this.addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
				destroyWindow();
      }
    });

		this.transferSize = transferSize;

		JPanel mainpanel = new JPanel();
		mainpanel.setLayout(new GridBagLayout());
		mainpanel.setBackground(new Color(255,255,255));

		transferPanel = new TransferPanel(transferSize);

	  gbc.insets = new Insets(5,5,5,5);

		JPanel infoPanel = new JPanel(new GridBagLayout());
		infoPanel.setBackground(new Color(255,255,255));
		infoPanel.setPreferredSize(new Dimension(WIDTH, 30)); 
		JLabel cache = new JLabel("Cache:  "+tmpLocation);
		cache.setPreferredSize(new Dimension(290,20)); 
		cache.setFont(gridFont);
		setXY(0,0);
		infoPanel.add(cache, gbc);
		JButton close = GuiItem.createButton("images/door13.png","Click to close window","1");
		close.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				destroyWindow();
				GridWrap.delTree(new File(LocalFileWrap.LOCAL_TEMP));
			}
		});
		setXY(1,0);
		infoPanel.add(close, gbc);

		/* I add the one to account for the title bar 
		 * I am making the size dynamic so that the window does not look awkward 
		 * If I can figure out how to push my JPanel to the top leaving space 
		 * on the bottom, then I don't need to do this.
		 */
		System.out.println("transfersize:"+transferSize);
		if(transferSize < MAX_TRANSFER_DISPLAY){
			mainpanel.setPreferredSize(new Dimension(WIDTH, (transferSize+1)*HEIGHT + 30));
			System.out.println("transfer window size is : "+(transferSize+1)*56);
		}else{
			mainpanel.setPreferredSize(new Dimension(WIDTH, (MAX_TRANSFER_DISPLAY+1)*HEIGHT + 30));
			System.out.println("transfer window size is : "+(MAX_TRANSFER_DISPLAY+1)*56);
		}

	  gbc.insets = new Insets(0,0,0,0);
		gbc.anchor = GridBagConstraints.NORTH;
		gbc.fill = GridBagConstraints.NONE;

		URL logoURL= classLoader.getResource(LOGO);
		setXY(0,0);
		//this.add(new JLabel(new ImageIcon(logoURL), JLabel.LEFT), gbc);

		setXY(0,1);
		mainpanel.add(transferPanel, gbc);
		
		setXY(0,2);
		mainpanel.add(infoPanel, gbc);

    Container container = this.getContentPane();
    container.setLayout(new BorderLayout());
    container.add(mainpanel, BorderLayout.NORTH);
    //UITools.center(null, dlg);
	}

	private void setXY(int x, int y){
		gbc.gridx = x;
		gbc.gridy = y;
	}

	public void destroyWindow(){
		SwingWorker worker = new SwingWorker(){
			public void doNonUILogic(){
			}
			public void doUIUpdateLogic(){
				instance.hide();
				instance.dispose();
				instance.removeAll();
				System.gc();
				System.exit(0);
			}
		};
		worker.start();
	}

	public void hideWindow(){
		SwingWorker worker = new SwingWorker(){
			public void doNonUILogic(){
			}
			public void doUIUpdateLogic(){
				instance.hide();
			}
		};
		worker.start();
	}

	public void showWindow(){
		SwingWorker worker = new SwingWorker(){
			public void doNonUILogic(){
			}
			public void doUIUpdateLogic(){
        instance.pack();
        instance.setVisible(true);
			}
		};
		worker.start();
	}

	public void refresh(){
		SwingWorker worker = new SwingWorker(){
			public void doNonUILogic(){
			}
			public void doUIUpdateLogic(){
				instance.repaint();
				instance.validate();
				instance.pack();
				instance.show();
			}
		};
		worker.start();
	}

	public void addPanel(final TransferProgressPanel panel){
		SwingWorker worker = new SwingWorker(){
			public void doNonUILogic(){
			}
			public void doUIUpdateLogic(){
				transferPanel.addPanel(panel);
				transferPanel.revalidate();
				transferPanel.repaint();
				instance.repaint();
				instance.validate();
				instance.pack();
			}
		};
		worker.start();
	}
}
