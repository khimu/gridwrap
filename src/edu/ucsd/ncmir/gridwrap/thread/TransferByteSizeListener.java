package edu.ucsd.ncmir.gridwrap.thread;

/*
 * @author Khim Ung National Center for Microscopy and Imaging Research
 */
public interface TransferByteSizeListener
{
	public void setBytesTransfered(int bytes, boolean success);
}
