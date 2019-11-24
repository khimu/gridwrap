package edu.ucsd.ncmir.gridwrap.local;

import java.lang.NullPointerException;
import java.io.IOException;
import java.io.File;
import javax.swing.JOptionPane;
import javax.swing.JDialog;
import java.util.StringTokenizer;
import java.util.Hashtable;
import java.util.HashSet;

import java.util.Vector;
import java.net.URL;
import java.util.Random;
                                                                                                 


/* <code>LocalFileWrap</code> is used to parse the SRB file paths without depending
 * on the <code>GridConnection</code>
 *
 * @author Khim Ung National Center for Microscopy and Imaging Research
 */
public class LocalFileWrap
{

 	private boolean DEBUG = false;

	public static String LOCAL_TEMP = System.getProperty("java.io.tmpdir");;
	public static String JAR_TEMP;
	public static String PLUGINS_TEMP;

 	/** Simple method for preparing the parameters 
 	 **/
 	public LocalFileWrap()
 	{		
 	}

  public static String getName(String s){
    if(s.endsWith("/"))
      s = s.substring(0, s.length() -1);
    String[] tmp = s.split("/");
    int i;
    for(i = 0; i < tmp.length - 1; i ++);
    return tmp[i];
  }
                                                                                                                     
  public static String getRoot(String s){
    int slash = s.indexOf("/");
    int slash2 = s.indexOf("/");
    return s.substring(slash, slash2);
  }
                                                                                                                     
  public static String getParent(String s){
    if(s.endsWith("/"))
      s = s.substring(0, s.length() -1);
    String[] tmp = s.split("/");
    String s2 = "";
    for(int i = 0; i < tmp.length - 1; i ++){
      s2 = s2 + tmp[i] + "/";
    }
		s2 = s2.substring(0, s2.length() - 1);
    return s2;
  }

	static {
		/********** CREATE LOCAL ************/
		if(LOCAL_TEMP.endsWith(File.separator))
			LOCAL_TEMP = LOCAL_TEMP.substring(0, LOCAL_TEMP.length() - 1);
		
		Random generator = new Random((new Long(System.currentTimeMillis())).intValue());
		int randomNumber = generator.nextInt();
		randomNumber = ((randomNumber/2) * (randomNumber/2)) / 2 ;
		System.out.println("randomNumber:"+randomNumber);
		LOCAL_TEMP += File.separator + "TP_GW" + String.valueOf(randomNumber);

		File filePath = new File(LOCAL_TEMP);
		createPath(filePath);
		
		/********** CREATE JAR ************/
		filePath = new File(LOCAL_TEMP + File.separator + "lib");
		createPath(filePath);
		JAR_TEMP = filePath.getAbsolutePath();

		/********** CREATE PLUGINS ************/
		filePath = new File(LOCAL_TEMP + File.separator + "plugins");
		createPath(filePath);
		PLUGINS_TEMP = filePath.getAbsolutePath();
	}

	public static void createPath(File file){
		if(file.exists())
			file.delete();
		file.mkdir();
	}
}
