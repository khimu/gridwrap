package edu.ucsd.ncmir.gridwrap.gui.component;

import java.awt.Color;
import java.awt.Font;
import javax.swing.*;

/* <code>ProgressBar</code> has specific features that is required by
 * this application, which includes error indicators.
 *
 * @author Khim Ung National Center for Microscopy and Imaging Research
 */
public class ProgressBar extends JProgressBar
{
	private boolean DEBUG = false;

	private final static Font gridFont = 
			new Font("Arial", Font.PLAIN, 10);

	private boolean fError = false;

	public ProgressBar(){
		super();
		updateBarColor();		
		setBackground(new Color(00,34,102));
		setStringPainted(true);
		setIndeterminate(false);
		setFont(gridFont);
	}

	protected Color getStatusColor(){
		if(fError == true){
			if(DEBUG) System.out.println("bar color will now be red.");
			return (new Color(205,55,0));
		}else
			return (new Color(65,105,225));
	}

	public void reset() {
		fError = false;
		updateBarColor();
		setValue(0);
	}

	public void start(int total){
		setMaximum(total);
		reset();
	}

	public void step(int value, boolean successful){
		setValue(value);
		if(!fError && !successful){
			if(DEBUG) System.out.println("I have a not true for both fError and successful");
			fError = true;
			updateBarColor();
		}
	}

	protected void updateBarColor(){
		setForeground(getStatusColor());
	}
}
