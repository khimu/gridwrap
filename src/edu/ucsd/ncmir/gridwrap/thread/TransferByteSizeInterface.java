package edu.ucsd.ncmir.gridwrap.thread;


/*
 * @author Khim Ung National Center for Microscopy and Imaging Research
 */
public interface TransferByteSizeInterface
{
	public void addTransferByteSizeListener(TransferByteSizeListener listener);
	public void removeTransferByteSizeListener(TransferByteSizeListener listener);
	public void fireTransferByteSizeEvent(TransferByteSizeEvent e, int bytes);
}
