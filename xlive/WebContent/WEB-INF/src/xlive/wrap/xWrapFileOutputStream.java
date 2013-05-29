package xlive.wrap;

import java.io.*;
/*
 *  wrap fileOutputStream for exclude from GAE
 */
public class xWrapFileOutputStream {
	
	public static OutputStream fileOutputStream(String file_name) throws FileNotFoundException{
		return new FileOutputStream(file_name);
	}
	public static OutputStream fileOutputStream(File file) throws FileNotFoundException{
		return new FileOutputStream(file);
	}
}
