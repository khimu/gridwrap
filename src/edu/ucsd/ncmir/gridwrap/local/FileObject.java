package edu.ucsd.ncmir.gridwrap.local;

import java.io.*;


/* This class contains information for every file
 * downloaded from SRB.  Information gets stored here
 * after a download has been performed.
 *
 * @author Khim Ung National Center for Microscopy and Imaging Research
 */
public class FileObject
{
	private long filesize;
	private String fileName = null;
	private long timeStamp = -1; 

	public FileObject(File localFile){
		filesize = localFile.length();
		fileName = localFile.getAbsolutePath();
		timeStamp = localFile.lastModified();		
	}

	public long getTimeStamp(){
		return timeStamp;
	}

	public long getFileSize(){
		return filesize;
	}

	public String getAbsolutePath(){
		return fileName;
	}

	public String getName(){
		return (new File(fileName)).getName();
	}

	public String getParent(){
		return (new File(fileName)).getParent();
	}

	public String getPath(){
		return fileName;
	}
}
