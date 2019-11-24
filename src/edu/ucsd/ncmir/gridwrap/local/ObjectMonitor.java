package edu.ucsd.ncmir.gridwrap.local;

import java.io.*;
import java.util.Vector;
import java.util.Hashtable;

/* <code>ObjectMonitor</code> traverses a local file path for information
 * storing and information retrieval.  All this is required for figuring out
 * what files will need to be uploaded when the program is ready to terminate.
 * 
 * @author Khim Ung National Center for Microscopy and Imaging Research
 */
public class ObjectMonitor
{

	private static boolean DEBUG = false;

	public ObjectMonitor(){
	}

	/* The purpose of this method is to get the
	 * timestamp and size of each file downlaoded.
	 */
	public void scanDownload(File localFile){
		// This is here to not add the initial directory to
		// the hashtable.  (ie srb, grid, http, https)
		if(localFile.isDirectory()){
			File[] childFiles = localFile.listFiles();
			for(int i = 0; i < childFiles.length; i ++){
				saveFileState(childFiles[i]);
			}
		}
	}

	public void saveFileState(File parentFile){
		if(DEBUG) System.out.println("scanDownload:"+parentFile.getPath());
		// Gather the information
		FileObject fileObject = new FileObject(parentFile);
		table.put(parentFile, fileObject);
	
		if(parentFile.isDirectory()){
			File[] childFiles = parentFile.listFiles();
			String[] list = parentFile.list();
			
			for(int i = 0; i < childFiles.length; i ++){
				saveFileState(childFiles[i]);
			}
		}
	}

	/* The purpose of this method is to find
	 * which files downloaded has been modified.
	 */
	public void scanUpload(File localFile){
		if(DEBUG) System.out.println("localfile is:"+localFile.getPath());
		// This is here to not add the initial directory to
		// the hashtable.  (ie srb, grid, http, https)
		if(localFile.isDirectory()){
			// If a directory was modified that means that directory
			// was created and everything in it should be new to SRB.
			if(modifiedFiles.contains(localFile.getAbsolutePath()) == false){
				if(DEBUG) System.out.println(localFile.getPath()+" was not modified");
				File[] childFiles = localFile.listFiles();
				for(int i = 0; i < childFiles.length; i ++){
					if(DEBUG) System.out.println("childfiles:"+childFiles[i].getPath());
					findModifiedFiles(childFiles[i]);
				}
			}
		}
	}

	public void findModifiedFiles(File localFile){
		// The srb parent path must be stored somewhere previously 
		FileObject fileObject = (FileObject)table.get(localFile);

		if(fileObject == null){
			if(DEBUG) System.out.println("fileObj==null::Modified is:"+localFile.getPath());
			modifiedFiles.addElement(localFile.getAbsolutePath());	
		}
		else
		if((fileObject.getTimeStamp() < localFile.lastModified())
				&& localFile.isFile()){
			if(DEBUG) System.out.println("Modified is:"+localFile.getPath());
			/* This means the file has a new timestamp */
			modifiedFiles.addElement(localFile.getAbsolutePath());	
		}

		if(localFile.isDirectory()){
			// If a directory was modified that means that directory
			// was created and everything in it should be new to SRB.
			if(modifiedFiles.contains(localFile.getAbsolutePath()) == false){
				if(DEBUG) System.out.println(""+localFile.getPath()+" was not modified");
				File[] childFiles = localFile.listFiles();
				for(int i = 0; i < childFiles.length; i ++){
					if(DEBUG) System.out.println("childfiles:"+childFiles[i].getPath());
					findModifiedFiles(childFiles[i]);
				}
			}
		}
	}

	public static Hashtable getSaveFileState(){
		return table;
	}

	public static Vector getModifiedFiles(){
		return modifiedFiles;
	}

	private static Hashtable table = new Hashtable();

	// Only contains the srb path that was downloaded.  No local temp location
	// in front of the path
	private static Vector modifiedFiles = new Vector();
}
