package edu.ucsd.ncmir.gridwrap.gui.component;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import java.net.URL;

public class GeneralStatusBar extends JPanel
{
	ClassLoader classLoader = getClass().getClassLoader();
	
	private final static int maxSize = 10;
	//public JLabel[] status = new JLabel[maxSize];
	public JTextField[] status = new JTextField[maxSize];
	public int activeStatus = 0;
	
	private GridBagConstraints gbc = null;
	private GridBagLayout gbl = null;

	public GeneralStatusBar()
	{
		gbl = new GridBagLayout();
		gbc = new GridBagConstraints();
		setLayout(gbl);
	}

	protected void setIcon(String icon, JLabel status){
    //The status bar added to south
    ImageIcon imageicon = new ImageIcon(classLoader.getResource("vdg/images/Directory.gif"));
    status.setIcon(imageicon);		
	}
	
	protected int getActiveStatus(){
		return activeStatus;
	}
	
	protected void setColor(Color color){
		setBackground(color);
	}
	
	public void addStatusLabel(int width, String text, Insets inset, int fill, int anchor, int gridwidth, double weightx){
		if(activeStatus < maxSize){
			status[activeStatus] = new JTextField(text);
    	status[activeStatus].setPreferredSize(new Dimension(300,20));
    	status[activeStatus].setEditable(false);
    	status[activeStatus].setBorder(new BevelBorder(BevelBorder.RAISED));
    	status[activeStatus].setFont(new Font("Helvetica", Font.PLAIN, 12));

			//status[activeStatus] = new JLabel(text);
			
			status[activeStatus].setBorder(new SoftBevelBorder(BevelBorder.LOWERED));
			if(width > 0){
				Dimension size = new Dimension(width, status[activeStatus].getPreferredSize().height);
				status[activeStatus].setMinimumSize(size);
				status[activeStatus].setMaximumSize(size);
				status[activeStatus].setPreferredSize(size);
			}
			status[activeStatus].setHorizontalAlignment(JLabel.LEFT);

			gbc.insets = inset;
			gbc.fill = fill;
			gbc.anchor = anchor;
			gbc.gridwidth = gridwidth;
			gbc.weightx = weightx;

			gbl.setConstraints(status[activeStatus], gbc);
			add(status[activeStatus]);
			activeStatus ++;
		}
	}

	public void setBar(String text, int index)
	{
		status[index].setText((text == null || text.length() < 1) ? " " : text);
	}
}
