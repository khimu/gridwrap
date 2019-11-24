package edu.ucsd.ncmir.gridwrap.gui.component;

import java.awt.*;
import javax.swing.*;

import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;


public class CustomBorder
{
	private final static Font gridFont =
      new Font("Arial", Font.PLAIN, 10);

  public static Border CustomTitledBorder(String title, Color ehbColor, Color esbColor, Color textColor) {
    Border empty5Border = BorderFactory.createEmptyBorder(5,5,5,5);
    Border etchedBorder  = BorderFactory.createEtchedBorder(ehbColor, esbColor);
    TitledBorder tb = BorderFactory.createTitledBorder(etchedBorder, title);
    tb.setTitleFont(gridFont);
    tb.setTitleColor(textColor);
    return BorderFactory.createCompoundBorder(tb, empty5Border);
    //BorderFactory.createTitledBorder(etchedBorder, title, TitledBorder.TOP, TitledBorder.LEFT, gridFont, Color.blue), empty5Border);
   //BorderFactory.createTitledBorder(etchedBorder, title, TitledBorder.BELOW_TOP, TitledBorder.LEFT, gridFont, Color.blue), empty5Border);
  }//end myTitledBorder
}
