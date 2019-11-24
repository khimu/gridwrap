package edu.ucsd.ncmir.gridwrap.util;

/*
This file is licensed under the terms of the Globus Toolkit Public
License, found at http://www.globus.org/toolkit/download/license.html.
*/

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.io.*;
import java.net.URL;


/**
 *  Displays an html file in a window
 */
public class HtmlFileViewer extends JFrame implements ActionListener {
	JButton Close;

	public HtmlFileViewer(JFrame parent, String filePath, boolean resource, String title) {
		this.getContentPane().setLayout(new BorderLayout());	
		this.setTitle(title);
		this.setSize(800,600);
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setResizable(false);
		UITools.center(null, this);
		
		JTextPane jTextPane =  new JTextPane();	
		jTextPane.setEditable(false);
		
		try {
  		ClassLoader classLoader = getClass().getClassLoader();
		  URL fileURL = classLoader.getResource(filePath);
		  jTextPane.setPage(fileURL);
		}
		catch (Exception e) {
		    JOptionPane.showMessageDialog(null, "Cannot display Help file "+filePath+" "+e.getMessage(), "Error!", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}
		JScrollPane Scroller = new JScrollPane(jTextPane);

		Close = new JButton("Close");
		Close.addActionListener(this);

		this.getContentPane().add(Scroller, BorderLayout.CENTER);
		this.getContentPane().add(Close, BorderLayout.SOUTH);

		this.setVisible(true);
		this.pack();
	}
	
	public HtmlFileViewer(JFrame parent, String FileName) {
		this(parent, FileName, false, "");
	}


	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == Close) {
			System.exit(0);
		}
	}



}

