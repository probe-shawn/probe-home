package xlive.method.fs;

import java.io.File;
import javax.servlet.http.HttpServletRequest;
import org.w3c.dom.Element;
import xlive.method.*;
import com.oreilly.servlet.multipart.*;

public class xUploadMethod extends xDefaultMethod{
	public Object process() throws xMethodException{
		String root_directory=getProperties("root-directory");
		if(root_directory==null||root_directory.trim().length()==0)	{
			throw this.createMethodException("xUploadMethod", "root-directory is empty");
		}
		String work_directory=this.getArguments("work-directory");
		if(work_directory==null||work_directory.trim().length()==0) work_directory="";
		else work_directory=work_directory.startsWith(File.separator)?work_directory:File.separator+work_directory;
		File directory = directoryResolve(root_directory+work_directory);
		//
		if(!directory.exists()) directory.mkdirs();
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
		boolean valid=false;
		String why="";
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
					File file= new File(directory,prefix_file_name+file_name);
					((FilePart)part).writeTo(file);
					long length=file.length();
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
