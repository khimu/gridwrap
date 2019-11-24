package edu.ucsd.ncmir.gridwrap.gui;

import edu.ucsd.ncmir.gridwrap.GridWrap;

import javax.swing.*;
import java.net.URL;
import java.awt.Container;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import javax.swing.filechooser.FileFilter;
import java.text.DecimalFormat;
import javax.swing.border.BevelBorder;
import java.awt.event.ComponentListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.util.Vector;
import java.util.StringTokenizer;
import java.net.*;
import java.awt.event.*;


public class PasswordDialog extends JDialog
{
  private static GridBagConstraints gbc = new GridBagConstraints();
	private JPasswordField password = null;
	private JDialog self = null;
	public static JButton ok = null;
	public static JButton cancel = null;

  public PasswordDialog (JFrame jframe)
  {
    super(jframe, "Enter Your Portal Password", true);
		self = this;

    Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
    JButton ok;
    JButton cancel;
    this.setSize(350, 100);
    int w = this.getSize().width;
    int h = this.getSize().height;
    int x = (dim.width-w)/2;
    int y = (dim.height-h)/2;

    this.setLocation(x, y);

    Container c = this.getContentPane();
    c.setLayout(new GridBagLayout());
    password = new JPasswordField(15);
    ok = new JButton("OK");
    ok.setBorder(new BevelBorder(BevelBorder.RAISED));
    cancel = new JButton("CANCEL");
    cancel.setBorder(new BevelBorder(BevelBorder.RAISED));

    JPanel p = new JPanel();
    p.setLayout(new GridBagLayout());
    gbc.gridx=0;
    gbc.gridy=0;
    p.add(new JLabel("Portal Password : "), gbc);
    gbc.gridx=1;
    p.add(password, gbc);

    JPanel p1 = new JPanel();
    p1.add(ok);
    p1.add(cancel);

    gbc.gridx=0;
    gbc.gridy=0;
    c.add(p, gbc);
    gbc.gridy=2;
    c.add(p1, gbc);

    ok.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae){
				checkpassword(password);
      }
    });

    cancel.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
				System.exit(0);
      }
    });

    addWindowListener(new WindowAdapter () {
      public void windowClosing(WindowEvent e) {
				System.exit(0);
      }
    });

    this.pack();
    this.setVisible(true);
  }

  private void checkpassword(JPasswordField password)
  {
    final String srbpassword = new String (password.getPassword());
		if(!srbpassword.equals("")){
			closeWindow();

			Thread t = new Thread(new Runnable(){
				public void run(){
      		try{
						Thread.sleep(500);
      		}catch(InterruptedException ie){
        		JOptionPane.showMessageDialog(null, ""+ie);
      		}

					GridWrap.setPassword(srbpassword);
    		  new GridWrap();
				}
			});
			t.start();
		}
  }

	public void closeWindow(){
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				self.setVisible(false);
				self.dispose();
 			}
		});
	}

	public String getPassword(){
		return new String(password.getPassword());
	}
}
