package edu.ucsd.ncmir.gridwrap.gui.component;

import javax.swing.*;
import java.net.URL;
import java.awt.Color;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import javax.swing.filechooser.FileFilter;
import java.text.DecimalFormat;
import javax.swing.border.BevelBorder;
import javax.swing.border.*; 
import javax.swing.text.*;
import java.util.Vector;
import java.util.StringTokenizer;
import java.util.regex.*;
import java.awt.dnd.*;



/**
 *Customized download browser for users to select files from their
 *local system. They will be able to select files or directories
 *to be uploaded to SRB.
 *
 *@author Tomas Molina
 *@version 2.2.0
 */
public class CustomizedChooser extends JFileChooser implements ActionListener
{
	
	/** My standard debug variable */
	private boolean DEBUG = false;
	
	/** A button for users to select files/directory and be uploaded to SRB*/
	private JButton addfile = new JButton("<== Add");

	private Object frame = null;
	
	
	/** Constructor to setup the browser for selection.*/
	public CustomizedChooser(Object frame)
	{
		this.frame = frame;
		this.addActionListener(this);
		this.setBackground(Color.white);
 	  this.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black, 2), "Your Files on Your Local Machine", TitledBorder.CENTER,TitledBorder.TOP));
 	   		
    this.setMultiSelectionEnabled(true);
    this.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
    this.setFileHidingEnabled(false);
    this.setDragEnabled(true);
    this.setControlButtonsAreShown(false);
     	
    GridBagConstraints gbc = new GridBagConstraints();
     
    addfile.addActionListener(this);
    addfile.setBorder(new BevelBorder(BevelBorder.RAISED));
		addfile.setPreferredSize(new Dimension(300,20));
 	   	
    JPanel temp = new JPanel();
    temp.setBackground(Color.white);
    temp.setLayout(new GridBagLayout());
    gbc.gridx=0;
    gbc.gridy=1;
    temp.add(addfile,gbc);
    this.add(temp,BorderLayout.SOUTH);  	
	}
	
	
	/**  This will basically pass the selected files back to the 
	 *	SRBBrowser class.
	 */
	public void actionPerformed(ActionEvent evt)
 	{
	}	
}
