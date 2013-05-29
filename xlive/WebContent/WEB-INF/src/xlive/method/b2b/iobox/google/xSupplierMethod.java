package xlive.method.b2b.iobox.google;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import xlive.google.ds.xFile;
import xlive.method.*;

public class xSupplierMethod extends xDefaultMethod{
	public Object process() throws xMethodException{
	    String outbox_directory=getProperties("outbox-directory");
	    outbox_directory = this.resourceDirectoryConvert(outbox_directory);
	    xFile outbox_file_directory = new xFile(outbox_directory);
	    if(!outbox_file_directory.exists()) outbox_file_directory.makeDirs();
	    int outbox_directory_length=outbox_directory.length();
		int max_size=6*1000*1000;
		try{
			max_size=Integer.parseInt(getProperties("outbox-maximum-mb"))*1000*1000;
		}catch(Exception eint){}
		//
	    Vector<xFile> file_vector = new Vector<xFile>();
	    Vector<xFile> directory_vector = new Vector<xFile>();
	    
	    getFiles(outbox_file_directory, file_vector, max_size, directory_vector);
	    
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
				xFile xfile=file_vector.get(i);
				String file_name_path= xfile.getFileName();
				file_name_path=file_name_path.substring(outbox_directory_length);
				String name=xfile.getName();
				String spec="";
		    	try {
		    		spec=URLEncoder.encode(file_name_path+";"+name+";"+xfile.getLength(), "utf-8");
		    	} catch (UnsupportedEncodingException e) {
		    		e.printStackTrace();
		    	}
				response.setHeader("file-"+i,spec);
			}
			for(int i=0; i< file_vector.size(); ++i){
				xFile xfile=file_vector.get(i);
				byte[] bytedata = xfile.getBytes();
				if(bytedata != null){
					output_stream.write(bytedata);
				}
			}
			output_stream.flush();
			output_stream.close();
			output_stream = null;
			//
			for(int i=0; i< file_vector.size(); ++i){
				xFile xfile=file_vector.get(i);
				if(xfile.isFile()) xfile.delete();
			}
    		for(int i=0;i<directory_vector.size();++i){
    			xFile xfile=directory_vector.get(i);
    			if(xfile.isDirectory()) removeDirectory(xfile);
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
	private void removeDirectory(xFile directory){
		List<xFile> files = directory.list();
		Iterator<xFile> it = files.iterator();
		while(it.hasNext()){
			xFile file = it.next();
			if(file.isDirectory()) removeDirectory(file);
		}
		files=directory.list();
		if(files.isEmpty()) directory.rd();
	}
	private void getFiles(xFile directory, Vector<xFile> vector, int max_size, Vector<xFile> directory_vector){
		List<xFile> files = directory.list();
		Iterator<xFile> it = files.iterator();
		while(it.hasNext()){
			xFile file = it.next();
			if(file.isDirectory()) {
				getFiles(file,vector,max_size,null);
				if(directory_vector != null)directory_vector.add(file);
			}
			else {
				if(file.getLength() < max_size)vector.add(file);
			}
		}
		
	}

}
