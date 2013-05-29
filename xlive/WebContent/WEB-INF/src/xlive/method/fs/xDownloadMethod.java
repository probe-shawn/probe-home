package xlive.method.fs;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletResponse;

import xlive.method.*;

public class xDownloadMethod extends xDefaultMethod{
	public Object process() throws xMethodException{
		String root_directory=getProperties("root-directory");
		if(root_directory==null||root_directory.trim().length()==0)	{
			throw this.createMethodException("xDownloadMethod", "root-directory is empty");
		}
		String work_directory=this.getArguments("work-directory");
		if(work_directory==null||work_directory.trim().length()==0) work_directory="";
		else work_directory=work_directory.startsWith(File.separator)?work_directory:File.separator+work_directory;
		File directory=directoryResolve(root_directory+work_directory);
		//
		String name=getArguments("name"); //name for return
		String file_name=getArguments("file-name");
		if(name==null || name.trim().length()==0) name=file_name;
		if(file_name==null || file_name.trim().length()==0)file_name=name;
		this.logMessage("name :"+name);
		File file= new File(directory, file_name);
		this.logMessage("file_name :"+file.getAbsolutePath());
		
        HttpServletResponse response = getServiceContext().customizeResponse();
		response.setHeader("Cache-Control", "no-cache");
		response.setHeader("Content-Type", "application/force-download");
		//response.setHeader("Content-Type", "application/x-www-form-urlencoded");
		//response.setHeader("Content-Type", "application/octet-stream");
		String user_agent=this.getServiceContext().getHttpServletRequest().getHeader("user-Agent").toUpperCase();
		boolean is_ie=user_agent.indexOf("MSIE")>=0;
		boolean is_safari=user_agent.indexOf("SAFARI")>=0;
		boolean is_chrome=user_agent.indexOf("CHROME")>=0;
		String why="";
		try {
			if(is_ie||is_safari||is_chrome)response.setHeader("Content-Disposition", "attachment;filename="+URLEncoder.encode(name,"UTF-8")+";");
			else response.setHeader("Content-Disposition", "attachment;filename*='utf-8'"+URLEncoder.encode(name,"UTF-8")+";");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		//
		BufferedInputStream input_stream=null;
		BufferedOutputStream output_stream=null;
		boolean valid=false;
        try {
        	input_stream=new BufferedInputStream(new FileInputStream(file));
          	output_stream= new BufferedOutputStream(response.getOutputStream());
          	byte[] bytes=new byte[1024];
          	int length=-1;
          	while((length=input_stream.read(bytes)) !=-1)
          		output_stream.write(bytes, 0, length);
          	input_stream.close();
          	input_stream=null;
          	output_stream.flush();
          	output_stream.close();
          	output_stream=null;
          	valid=true;
        }catch(FileNotFoundException fnf){
        	fnf.printStackTrace();
        	why="FileNotFoundException :"+fnf.getLocalizedMessage();
        }catch(IOException ioe){
        	 ioe.printStackTrace();
        	 why="IOException :"+ioe.getLocalizedMessage();
        }finally{
        	try{
        		if(input_stream != null) input_stream.close();
        		if(output_stream!=null) output_stream.close();
        	}catch(Exception e){}
        }
        if(!valid){
        	try{
        		response.sendError(HttpServletResponse.SC_NO_CONTENT);
        	}catch(Exception e){}
        }
		return getServiceContext().doNextProcess();
	}
}
