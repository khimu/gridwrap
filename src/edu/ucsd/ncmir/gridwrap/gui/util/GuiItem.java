package edu.ucsd.ncmir.gridwrap.gui.util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.*;

public class GuiItem 
{
  public final static Font gridFont =
        new Font("helvetica", Font.BOLD, 12);

	public final static Border etchedBdr = BorderFactory.createEtchedBorder();
	
	private final static GuiItem guiItem = new GuiItem();

	public GuiItem(){}

	public static ImageIcon createIcon(String s){
			ClassLoader classLoader = guiItem.getClass().getClassLoader();
			return (new ImageIcon(classLoader.getResource(s)));
	}

	public static JButton createButton(String s, String s1, String s2)
	{
		try{
/*
			ClassLoader classLoader = guiItem.getClass().getClassLoader();
			JButton jbutton = new JButton(new ImageIcon(classLoader.getResource(s)));
*/
			JButton jbutton = new JButton(createIcon(s));
			jbutton.setPreferredSize(new Dimension(50, 20));
			jbutton.setFont(gridFont);
			jbutton.setActionCommand(s2);
			jbutton.setToolTipText(s1);
			jbutton.setRequestFocusEnabled(false);
			return jbutton;
		}catch(Exception e){
			JButton jbutton = new JButton(s);
			jbutton.setPreferredSize(new Dimension(50, 20));
			jbutton.setFont(gridFont);
			jbutton.setActionCommand(s2);
			jbutton.setToolTipText(s1);
			jbutton.setRequestFocusEnabled(false);
			return jbutton;
		}
	}

	public static JButton createButton(String s, String s1, String s2, int width)
	{
		try{
			ClassLoader classLoader = guiItem.getClass().getClassLoader();
			JButton jbutton = new JButton(new ImageIcon(classLoader.getResource(s)));
			jbutton.setPreferredSize(new Dimension(width, 20));
			jbutton.setFont(gridFont);
			jbutton.setActionCommand(s2);
			jbutton.setToolTipText(s1);
			jbutton.setRequestFocusEnabled(false);
			return jbutton;
		}catch(Exception e){
			JButton jbutton = new JButton(s);
			jbutton.setPreferredSize(new Dimension(width, 20));
			jbutton.setFont(gridFont);
			jbutton.setActionCommand(s2);
			jbutton.setToolTipText(s1);
			jbutton.setRequestFocusEnabled(false);
			return jbutton;
		}
	}

	public static JMenuItem createMenuItem(String name, String action, char mnemonic){
		JMenuItem item = new JMenuItem(name);
		item.setMnemonic(mnemonic);
		item.setActionCommand(action);
		return item;
	}

	public static JCheckBoxMenuItem createCheckBoxMenuItem(String name, 
		String action, char mnemonic, boolean checked)
	{
		JCheckBoxMenuItem item = new JCheckBoxMenuItem(name, checked);
		item.setMnemonic(mnemonic);
		item.setActionCommand(action);
		return item;
	}

	public static void centerWindow(Window guiComponent) {
		//Center the window
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = guiComponent.getSize();
		if (frameSize.height > screenSize.height) {
			frameSize.height = screenSize.height;
		}
		if (frameSize.width > screenSize.width) {
			frameSize.width = screenSize.width;
		}
		guiComponent.setLocation((screenSize.width - frameSize.width) / 2,
			(screenSize.height - frameSize.height) / 2);
	}

	public static JTextField createTextField(String s, int tw, int th){
		JTextField temp = new JTextField();
		temp.setPreferredSize(new Dimension(tw, th));
		temp.setFont(gridFont);
		temp.setText(s);

		return temp;
	}
}
