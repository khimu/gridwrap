package edu.ucsd.ncmir.gridwrap.gui;

import java.awt.*;
import javax.swing.*;


public class ImagePanel extends JPanel
{
	Image image = null;

	public ImagePanel(Image image){
		this.image = image;
	}

	public void paintComponent(Graphics g) {
		g.drawImage(image,0,0,this);
		super.paintComponent(g);
	}

}
