package edu.ucsd.ncmir.gridwrap.thread;

/*
 * @author Khim Ung National Center for Microscopy and Imaging Research
 */
public class TransferByteSizeEvent extends java.util.EventObject
{
	protected int id;
	public static final int BYTE = 0;

	public TransferByteSizeEvent(Object source, int id)
	{
		super(source);
		this.id = id;
	}

	public int getID(){
		return id;
	}
}
