package edu.ucsd.ncmir.gridwrap.util;


/********************************************************************
* Filename: SwingWorker.java
* Author: Yexin Chen from JavaWorld.com
* File Description: This is a variant of the SwingWorker. It works in
*	conjunction with the GlassPane class to allow users to execute
*	time-consuming task on a separate thread. The GlassPane is used
*	to prevent additional SwingWorker task while one SwingWorker is
*	already executing.
*
* Copyright 2003 NCMIR, UCSD. All Rights Reserved.
********************************************************************/

import java.awt.*;
import java.lang.InterruptedException;
import javax.swing.SwingUtilities;

public abstract class SwingWorker
{
	private Thread thread;
	
	// Private inner class used to maintain reference to
	// current worker thread under separate synchronization
	// control.
	private static class ThreadVar
	{
		private Thread thread;

		ThreadVar(Thread t)
		{
			thread = t;
		}

		synchronized Thread get()
		{
			return thread;
		}

		synchronized void clear()
		{
			thread = null;
		}
	}

	private ThreadVar threadVar;
	private GlassPane glassPane;
	private Component component;

	// This method is added by khim ung to allow no glass pane.
	public SwingWorker()
	{
		final Runnable doFinished = new Runnable()
		{
			public void run()
			{
				finished();
			}
		};
		
		Runnable doConstruct = new Runnable()
		{
			public void run()
			{
				try
				{
					construct();
				}
				finally
				{
					threadVar.clear();
				}

				// Execute the doFinnished runnable on the Swing
				// dispatcher thread.
				SwingUtilities.invokeLater(doFinished);
			}
		};

		// Group the new worker thread in the same group as the
		// "spawner" thread.
		Thread t = new Thread(Thread.currentThread().getThreadGroup(),
			doConstruct);
		threadVar = new ThreadVar(t);	
	}

	public SwingWorker(Component com)
	{
		setComponent(com);

		final Runnable doFinished = new Runnable()
		{
			public void run()
			{
				finished();
			}
		};
		
		Runnable doConstruct = new Runnable()
		{
			public void run()
			{
				try
				{
					construct();
				}
				finally
				{
					threadVar.clear();
				}

				// Execute the doFinnished runnable on the Swing
				// dispatcher thread.
				SwingUtilities.invokeLater(doFinished);
			}
		};

		// Group the new worker thread in the same group as the
		// "spawner" thread.
		Thread t = new Thread(Thread.currentThread().getThreadGroup(),
			doConstruct);
		threadVar = new ThreadVar(t);
	}

	// activate the glasspane
	private void activateGlassPane()
	{
		// Mount the glasspane on the component window
		GlassPane gp = GlassPane.mount(getComponent(), true);
		// keep track of the glasspane as an instance variable
		setGlassPane(gp);

		if(getGlassPane() != null)
		{
			//start interception UI interactions
			getGlassPane().setVisible(true);
		}
	}

	// Disable unwanted UI manipulation using glasspane, then spawn the
	// non-UI logic on a separate thread.
	private void construct()
	{
		if(getComponent() != null)  // Added by Khim Ung so that we can disable glasspane where it is unnecessary.
			activateGlassPane();
		try
		{
			doNonUILogic();
		}
		catch(RuntimeException e) {}
	}

	// deactivation of the glasspane
	private void deactivateGlassPane()
	{
		if(getGlassPane() != null)
		{
			getGlassPane().setVisible(false);
		}
	}

	// Abstract method to be implemented by its child class, it
	// should only be responsible of the non-UI logics.
	//
	// @throws java.lang.RuntimeException thrown if there are any 
	// errors in the non-ui logic
	public abstract void doNonUILogic() throws RuntimeException;//protected
	
	// Abstract method to be implemented by its child class, it
	// should be responsible of UI updating when doNonUIlogic() is done.
	//
	// @throws java.lang.RuntimeException thrown if there are any 
	// problems executing the ui update logic
	public abstract void doUIUpdateLogic() throws RuntimeException;//protected

	// Called on the event dispatching thread (not on the worker thread)
	// after the constructor has returned.
	protected void finished()
	{
		try
		{
			deactivateGlassPane();
			doUIUpdateLogic();
		}
		catch(RuntimeException e)
		{
			System.out.println("SwingWorker error" + e);
		}
		finally
		{
			// request focus back to the original component
			if(getComponent() != null)
			{
				getComponent().requestFocus();
			}
		}
	}

/*
	protected void invokeWait(){
		Thread t = threadVar.get();
		if(t !=  null){
			t.wait();
		}
	}

	protected void invokeNotifyAll(){
		Thread t = threadVar.get();
		if(t !=  null){
			t.notifyAll();
		}
	}
*/
	protected Component getComponent()
	{
		return component;
	}

	protected GlassPane getGlassPane()
	{
		return glassPane;
	}

	// Method that interrupts the current worker thread, forcing
	// the worker to stop what it's doing.
	public void interrupt()
	{
		Thread t = threadVar.get();
		if(t != null)
		{
			t.interrupt();
		}
		threadVar.clear();
	}

	protected void setComponent(Component newComponent)
	{
		component = newComponent;
	}

	protected void setGlassPane(GlassPane newGlassPane)
	{
		glassPane = newGlassPane;
	}

	public void join(){
		try{
			Thread t = threadVar.get();
			if(t != null)
				t.join();
		}catch(java.lang.InterruptedException io){
			System.out.println("SwingWorker:join "+io);
		}
	}

	public void setDaemon(boolean daemon){
		Thread t = threadVar.get();
		if(t != null)
			t.setDaemon(daemon);
	}

	public void setPriority(int priority){
		Thread t = threadVar.get();
		if(t != null)
			t.setPriority(priority);
	}

	public void start()
	{
		Thread t = threadVar.get();
		if(t != null)
			t.start();
	}
}
