package edu.ucsd.ncmir.gridwrap.gui;
import edu.ucsd.ncmir.gridwrap.gui.component.*;

import java.awt.Color;
import javax.swing.*;
import java.awt.*;

/* <code>GridProgressBar</code> is a progress bar with features that meets the specific needs
 * of the GridWrap application.
 * 
 * @author Khim Ung National Center for Microscopy and Imaging Research
 */
public class GridProgressBar extends ProgressBar
{

	private JTextField component;
  private final static Font gridFont =
      new Font("Arial", Font.BOLD, 10);

	public GridProgressBar(JTextField component) {
		super();
		this.component = component;
    this.component.setFont(gridFont);
    this.component.setForeground(Color.black);
    //this.component.setBackground(Color.black);

	}

/*
	protected void updateBarColor(){
		component.setBackground(getStatusColor());
	}
*/
}
