package xlive.method.b2b.iobox;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Vector;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import xlive.xUtility;
import xlive.method.*;

public class xSupplierMethod extends xDefaultMethod{
	public Object process() throws xMethodException{
	    String outbox_directory=getProperties("outbox-directory");
	    File outbox_file=directoryResolve(outbox_directory);
	    int outbox_directory_length=outbox_file.getAbsolutePath().length();
		int max_size=6*1000*1000;
		try{
			max_size=Integer.parseInt(getProperties("outbox-maximum-mb"))*1000*1000;
		}catch(Exception eint){}
		//
	    Vector<File> file_vector = new Vector<File>();
	    Vector<File> directory_vector = new Vector<File>();
	    getFiles(outbox_file, file_vector, max_size, directory_vector);
	    HttpServletResponse response=(HttpServletResponse)getServiceContext().customizeResponse();
		response.setContentType("application/octet-stream");
		response.setHeader("Cache-Control", "no-cache");
	    ServletOutputStream output_stream=null;
		FileInputStream file_input=null;
		String message=null;
		try{
			output_stream=response.getOutputStream();
			response.setHeader("valid", "true");
			response.setHeader("file-count", String.valueOf(file_vector.size()));
			for(int i=0; i< file_vector.size(); ++i){
				File file=file_vector.get(i);
				String file_name_path= file.getAbsolutePath();
				file_name_path=file_name_path.substring(outbox_directory_length);
				String name=file.getName();
				String spec="";
		    	try {
		    		spec=URLEncoder.encode(file_name_path+";"+name+";"+file.length(), "utf-8");
		    	} catch (UnsupportedEncodingException e) {
		    		e.printStackTrace();
		    	}
				response.setHeader("file-"+i,spec);
			}
			for(int i=0; i< file_vector.size(); ++i){
				File file=file_vector.get(i);
				String file_name= file.getAbsolutePath();
				file_name=file_name.substring(outbox_directory_length);
				file_input=new FileInputStream(file_vector.get(i));
				xUtility.copyStream(file_input, output_stream);
				file_input.close();
				file_input=null;
			}
			output_stream.flush();
			output_stream.close();
			output_stream = null;
			//
			for(int i=0; i< file_vector.size(); ++i){
				File file=file_vector.get(i);
				if(file.isFile()) file.delete();
			}
    		for(int i=0;i<directory_vector.size();++i){
    			File file=directory_vector.get(i);
    			if(file.isDirectory()) removeDirectory(file);
    		}
			return getServiceContext().doNextProcess();
        }catch(IOException ioe) {
        	ioe.printStackTrace();
        	message="IOException :"+ioe.getLocalizedMessage();
        }catch(Exception ex) {
        	ex.printStackTrace();
        	message="Exception :"+ex.getLocalizedMessage();
        }finally {
           	try {if(file_input != null) file_input.close();}catch(Exception e){}
        }
        try{
        	response.setHeader("valid", "false");
        	if(message != null){
        		try {
        			message=URLEncoder.encode(message, "utf-8");
        		} catch (UnsupportedEncodingException e) {
        			e.printStackTrace();
        		}
        		response.setHeader("message", message);
        	}
        	if(output_stream == null)output_stream=response.getOutputStream();
        	output_stream.close();
        }catch(IOException ioe){}
		return getServiceContext().doNextProcess();
	}
	private void removeDirectory(File directory){
		 File[] files=directory.listFiles();
		 for(int i=0;i<files.length; ++i){
			 if(files[i].isDirectory()) removeDirectory(files[i]);
		 }
		 files=directory.listFiles();
		 if(files.length == 0) directory.delete();
	}
	private void getFiles(File directory, Vector<File> vector, int max_size, Vector<File> directory_vector){
		File[] files=directory.listFiles();
		for(int i=0;i<files.length;++i){
			if(files[i].isDirectory()) {
				getFiles(files[i],vector,max_size,null);
				if(directory_vector != null)directory_vector.add(files[i]);
			}
			else {
				if(files[i].length() < max_size)vector.add(files[i]);
			}
		}
	}

}
