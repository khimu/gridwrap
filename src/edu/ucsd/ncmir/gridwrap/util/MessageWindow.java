package edu.ucsd.ncmir.gridwrap.util;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.io.*;
import java.net.URL;

/**
 *  Displays an html file in a window
 */
public class MessageWindow extends JFrame implements ActionListener {
	private static boolean DEBUG = true;
	private JButton Close;
	private boolean quit = false;
	private JTextArea textArea = null;
	private JScrollPane scrollpane = null;

	private final static Font gridFont =
			new Font("helvetica", Font.PLAIN, 12);

	public MessageWindow(String message, String title){
		if(DEBUG)System.out.println("MessageWindow");
		getContentPane().setLayout(new BorderLayout());	
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle(title);
		setSize(550,600);
		UITools.center(null, this);
		
		textArea = new JTextArea(message);
		textArea.setEditable(false);
		textArea.setMargin(new Insets(5,5,5,5));
		textArea.setFont(gridFont);
		scrollpane = new JScrollPane(textArea);

		Close = new JButton("Close");
		Close.addActionListener(this);

		getContentPane().add(scrollpane, BorderLayout.CENTER);
		getContentPane().add(Close, BorderLayout.SOUTH);

		setVisible(true);
	}
	
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == Close) {
			setVisible(false);
			dispose();
			System.exit(0);
		}
	}



}

