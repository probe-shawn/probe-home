package xlive.method.fs.google;

import java.io.ByteArrayOutputStream;
import java.util.logging.Level;

import javax.servlet.http.HttpServletRequest;

import org.w3c.dom.Element;
import xlive.google.ds.xFile;
import xlive.method.*;
import xlive.method.logger.xLogger;

import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.oreilly.servlet.multipart.*;

public class xUploadMethod extends xDefaultMethod{
	public Object process() throws xMethodException{
		HttpServletRequest request = this.getServiceContext().getHttpServletRequest();
		String gae_upload = request.getParameter("gaeblobstore");
		if("true".equals(gae_upload)) return process_blobstore();
		else  return process_datastore();
	}
	public Object process_blobstore() throws xMethodException{
		boolean valid=true;
		String why="";
		HttpServletRequest request = this.getServiceContext().getHttpServletRequest();
		int blob_count = 0;
		try{
			blob_count= Integer.parseInt(request.getParameter("blobKeyCount"));
		}catch(Exception e){}
		//
		String root_directory=getProperties("root-directory");
		if(root_directory==null||root_directory.trim().length()==0)	{
			throw this.createMethodException("xFileMethod", "root-directory is empty");
		}
		root_directory = this.resourceDirectoryConvert(root_directory);
		String work_directory=this.getArguments("work-directory");
		if(work_directory==null||work_directory.trim().length()==0) work_directory="";
		else {
			if(work_directory.startsWith("/")) work_directory=work_directory.substring(1);
			if(!work_directory.endsWith("/")) work_directory=work_directory+"/";
		}
		String directory = root_directory+work_directory;
		xFile xfile = new xFile(directory);
		if(!xfile.exists()) xfile.makeDirs();
		String prefix_file_name=this.getArguments("prefix-file-name");
		if(prefix_file_name==null||prefix_file_name.trim().length()==0) prefix_file_name="";
		//
		BlobInfoFactory blobInfoFactory = new BlobInfoFactory();
		try{
			Element return_data=this.setReturnArguments("data", "");
			//
			for(int i = 0; i < blob_count;++i){
				String key_string = request.getParameter("blobKey"+i);
				BlobKey blob_key = new BlobKey(key_string);
				BlobInfo blob_info = blobInfoFactory.loadBlobInfo(blob_key);
				String file_name = blob_info.getFilename();
				// localhost must encoding
				//file_name=new String(file_name.getBytes("iso-8859-1"),"utf-8");
				//System.out.println("file_name iso-8859-1 :"+file_name);
				long length= blob_info.getSize();
				xFile xfile2 = new xFile(directory+prefix_file_name+file_name);
				//xfile2.setBlobKey(blob_key);???????????????????????????????????????????????????????????
				Element return_file=createElement("file",file_name);
				return_file.setAttribute("inputName", file_name);
				return_file.setAttribute("length", String.valueOf(length));
				return_data.appendChild(return_file);
			}
			valid=true;
		}catch(Exception e){
			valid=false;
			why=e.getLocalizedMessage();
			e.printStackTrace();
		}
		setReturnArguments("valid", valid ? "true":"false");
		setReturnArguments("why", why);
		return getServiceContext().doNextProcess();
	}
	
	public Object process_datastore() throws xMethodException{
		boolean valid=false;
		String why="";

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
		xFile xfile = new xFile(directory);
		if(!xfile.exists()) xfile.makeDirs();
		String prefix_file_name=this.getArguments("prefix-file-name");
		if(prefix_file_name==null||prefix_file_name.trim().length()==0) prefix_file_name="";
		//
		int max_size=60*1000*1000;
		try{
			max_size=Integer.parseInt(getProperties("upload.maximum-mb"))*1000*1000;
		}catch(Exception eint){}
		//
		boolean buffer=true; 
		boolean limit_length=false;
		try{
			Element return_data=this.setReturnArguments("data", "");
			//
			HttpServletRequest request=(HttpServletRequest)getServiceContext().getHttpServletRequest();
			//
			MultipartParser multi = new MultipartParser(request,max_size,buffer,limit_length);
			multi.setEncoding("UTF-8");
			com.oreilly.servlet.multipart.Part part;
			while((part=multi.readNextPart()) != null){
				if(part.isParam()){
					String param_name=((ParamPart)part).getName();
					String param_value=((ParamPart)part).getStringValue();
					if("_charset_".equalsIgnoreCase(param_name) && (param_value !=null && param_value.trim().length()>0)) 
							multi.setEncoding(param_value);
					this.logMessage("Param Name :"+param_name+"  Value :"+param_value);
				}
				String file_name=null;
				if(part.isFile() && (file_name=((FilePart)part).getFileName())!=null){
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					((FilePart)part).writeTo(baos);
					xFile xfile2 = new xFile(directory+prefix_file_name+file_name);
					xfile2.setBytes(baos.toByteArray());
					long length=xfile2.getLength();
					String input_name=((FilePart)part).getName();
					Element return_file=createElement("file",((FilePart)part).getFileName());
					return_file.setAttribute("inputName", input_name);
					return_file.setAttribute("length", String.valueOf(length));
					return_data.appendChild(return_file);
				}
			}
			valid=true;
		}catch(Exception e){
			valid=false;
			why=e.getLocalizedMessage();
			e.printStackTrace();
		}
		setReturnArguments("valid", valid ? "true":"false");
		setReturnArguments("why", why);
		return getServiceContext().doNextProcess();
	}

}
