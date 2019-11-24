package edu.ucsd.ncmir.gridwrap.util;

/********************************************************************
* Filename: GlassPane.java
* Author: Yexin Chen from JavaWorld.com
* File Description: It intercepts screen interations during system
*	busy states using the glass pane.
*
* Copyright 2003 NCMIR, UCSD. All Rights Reserved.
********************************************************************/

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.MouseInputAdapter;

public class GlassPane extends JComponent implements AWTEventListener
{
	private Window theWindow;
	private Component activeComponent;

	// the constructor
	protected GlassPane(Component activeComponent)
	{
		// add adaptors that generate no response to keyboard
		// and mouse actions
		addMouseListener(new MouseAdapter()
		{});

		addKeyListener(new KeyAdapter()
		{});

		setActiveComponent(activeComponent);
	}


	// Receives all key events in the AWT and processes the ones that
	// originated from the current window with the glass pane.
	public void eventDispatched(AWTEvent event)
	{
		Object source = event.getSource();

		// discard the event if its source is not from the 
		// correct type
		boolean sourceIsComponent = (event.getSource() instanceof Component);

		if((event instanceof KeyEvent) && sourceIsComponent)
		{
			// if the event is originated from the window with
			// glass pane, then consume the event
			if((SwingUtilities.windowForComponent((Component)source) ==
				theWindow))
			{
				((KeyEvent)event).consume();
			}
		}
	}

	// finds the glass pane that is related to the given component
	public synchronized static GlassPane mount(Component cp, boolean create)
	{
		RootPaneContainer container = null;
		Component component = cp;

		// trace back the component hierarchy until a RootPaneContainer is
		// found or until the very top
		while((component.getParent() != null) && !(component instanceof
			RootPaneContainer))
		{
			component = (Component)component.getParent();
		}

		// making sure that the tracing was successful
		if(component instanceof RootPaneContainer)
		{
			container = (RootPaneContainer)component;
		}

		if(container != null)
		{
			// return an existing glasspane, otherwise if specified create a
			// new glasspane
			if((container.getGlassPane() != null) && container.getGlassPane()
				instanceof GlassPane)
			{
				return (GlassPane)container.getGlassPane();
			}
			else if(create)
			{
				GlassPane gp = new GlassPane(cp);
				container.setGlassPane(gp);

				//Debug.spitln("GlassPane mounted on " + container.getClass());
				return gp;
			}
			else
			{
				return null;
			}
		}
		else
		{
			return null;
		}
	}

	private void setActiveComponent(Component component)
	{
		activeComponent = component;
	}

	// sets the glass pane visible or invisile, and then sets the mouse
	// cursor accordingly
	public void setVisible(boolean value)
	{
		if(value)
		{
			if(theWindow == null)
			{
				theWindow = SwingUtilities.windowForComponent(activeComponent);
				if(theWindow == null)
				{
					if(activeComponent instanceof Window)
					{
						theWindow = (Window)activeComponent;
					}
				}
			}

			// sets the mouse cursor to hourglass mode
			getTopLevelAncestor().setCursor(Cursor.getPredefinedCursor(
				Cursor.WAIT_CURSOR));
			activeComponent = theWindow.getFocusOwner();

			// receiving all events and consume them if necessary
			Toolkit.getDefaultToolkit().addAWTEventListener(this, 
				AWTEvent.KEY_EVENT_MASK);
			this.requestFocus();
			// activate the glass pane capability
			super.setVisible(value);
		}
		else
		{
						
			// stop receiving all events
			Toolkit.getDefaultToolkit().removeAWTEventListener(this);
			// deactivate the glass pane capability
			super.setVisible(value);

			// restore the regular mouse cursor
			if(getTopLevelAncestor() != null)
			{
				getTopLevelAncestor().setCursor(null);
			}
		}
	}
}
