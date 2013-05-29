package xlive.method.fs.google;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.logging.Level;

import javax.servlet.http.HttpServletResponse;

import xlive.xProbeServlet;
import xlive.xResourceManager;
import xlive.google.ds.xFile;
import xlive.method.*;
import xlive.method.logger.xLogger;

public class xDownloadMethod extends xDefaultMethod{
	public Object process() throws xMethodException{
		String static_resource=getProperties("static-resource");
		if("true".equalsIgnoreCase(static_resource)) return static_resource_process();
		return datastore_process();
	}
	public Object datastore_process() throws xMethodException{
		String root_directory=getProperties("root-directory");
		if(root_directory==null||root_directory.trim().length()==0)	{
			throw this.createMethodException("xFileMethod", "root-directory is empty");
		}
		if(root_directory.startsWith("./"))	root_directory=root_directory.replaceFirst("./", this.getObjectResourcePath());
		if(!root_directory.endsWith("/")) root_directory=root_directory+"/";
		//
		String work_directory=this.getArguments("work-directory");
		if(work_directory==null||work_directory.trim().length()==0) work_directory="";
		else {
			if(work_directory.startsWith("/")) work_directory=work_directory.substring(1);
			if(!work_directory.endsWith("/")) work_directory=work_directory+"/";
		}
		String directory = root_directory+work_directory;
		//
		String name=getArguments("name"); //name for return
		String file_name=getArguments("file-name");
		if(name==null || name.trim().length()==0) name=file_name;
		if(file_name==null || file_name.trim().length()==0)file_name=name;
		file_name=directory+file_name;
		//
        HttpServletResponse response = getServiceContext().customizeResponse();
		response.setHeader("Cache-Control", "no-cache");
		response.setHeader("Content-Type", "application/force-download");
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
        	xFile xfile = new xFile(file_name);
        	byte[] data=xfile.getBytes();
        	if(data != null){
        		ByteArrayInputStream bais = new ByteArrayInputStream(xfile.getBytes());
        		input_stream=new BufferedInputStream(bais);
        	}
          	output_stream= new BufferedOutputStream(response.getOutputStream());
          	if(input_stream != null){
          		byte[] bytes=new byte[10240];
          		int length=-1;
          		while((length=input_stream.read(bytes)) !=-1)
          			output_stream.write(bytes, 0, length);
          		input_stream.close();
          		input_stream=null;
        	}
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

	public Object static_resource_process() throws xMethodException{
		String root_directory=getProperties("root-directory");
		if(root_directory==null||root_directory.trim().length()==0)	{
			throw this.createMethodException("xFileMethod", "root-directory is empty");
		}
		if(root_directory.startsWith("./"))	root_directory=root_directory.replaceFirst("/", this.getObjectResourcePath());
		if(!root_directory.endsWith("/")) root_directory=root_directory+"/";
		//
		String work_directory=this.getArguments("work-directory");
		if(work_directory==null||work_directory.trim().length()==0) work_directory="";
		else {
			if(work_directory.startsWith("/")) work_directory=work_directory.substring(1);
			if(!work_directory.endsWith("/")) work_directory=work_directory+"/";
		}
		String directory = root_directory+work_directory;
		//
		String name=getArguments("name"); //name for return
		String file_name=getArguments("file-name");
		if(name==null || name.trim().length()==0) name=file_name;
		if(file_name==null || file_name.trim().length()==0)file_name=name;
		file_name=directory+file_name;
		//
        HttpServletResponse response = getServiceContext().customizeResponse();
		response.setHeader("Cache-Control", "no-cache");
		response.setHeader("Content-Type", "application/force-download");
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
		if(file_name.equals("/download/iMall委託開店授權書.doc")) file_name="/download/imall_authorized.doc";
		BufferedInputStream input_stream=null;
		BufferedOutputStream output_stream=null;
		boolean valid=false;
        try {
        	input_stream=new BufferedInputStream(xResourceManager.getResourceAsStream(file_name));
          	output_stream= new BufferedOutputStream(response.getOutputStream());
          	byte[] bytes=new byte[10240];
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
