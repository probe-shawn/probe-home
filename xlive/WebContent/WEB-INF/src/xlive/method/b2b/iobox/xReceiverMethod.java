package xlive.method.b2b.iobox;

import java.io.File;
import javax.servlet.http.HttpServletRequest;
import org.w3c.dom.Element;
import xlive.method.*;
import com.oreilly.servlet.multipart.*;

public class xReceiverMethod extends xDefaultMethod{
	public Object process() throws xMethodException{
	    String inbox_directory=getProperties("inbox-directory");
	    File inbox_file=directoryResolve(inbox_directory);
		int max_size=6*1000*1000;
		try{
			max_size=Integer.parseInt(getProperties("inbox-maximum-mb"))*1000*1000;
		}catch(Exception eint){}
		//
		boolean buffer=true; 
		boolean limit_length=false;
		boolean valid=true;
		String why="";
		try{
			Element return_data=setReturnArguments("data", "");
			HttpServletRequest request=(HttpServletRequest)getServiceContext().getHttpServletRequest();
			MultipartParser multi = new MultipartParser(request,max_size,buffer,limit_length);
			multi.setEncoding("UTF-8");
			com.oreilly.servlet.multipart.Part part;
			while((part=multi.readNextPart()) != null){
				if(part.isParam()){
					String param_name=((ParamPart)part).getName();
					String param_value=((ParamPart)part).getStringValue();
					if("_charset_".equalsIgnoreCase(param_name) && (param_value !=null && param_value.trim().length()>0)) 
							multi.setEncoding(param_value);
				}
				if(part.isFile() && ((FilePart)part).getFileName()!=null){
					String file_path=((FilePart)part).getFilePath();
					File file= new File(inbox_file,file_path);
					File parent_file=file.getParentFile();
					if(parent_file != null && !parent_file.exists()) parent_file.mkdirs();
					((FilePart)part).writeTo(file);
					long length=file.length();
					Element return_file=createElement("file",((FilePart)part).getFilePath());
					return_file.setAttribute("length", String.valueOf(length));
					return_data.appendChild(return_file);
				}
			}
		}catch(Exception e){
			valid = false;
			why=e.getLocalizedMessage();
			e.printStackTrace();
		}
		//
		if(valid){
			try{
				boolean do_dispatcher=false;
				do_dispatcher="true".equals(this.getArguments("do-dispatcher"));
				if(do_dispatcher)	this.processMethod("dispatcher");
			}catch(Exception e){}
			try{
				boolean do_executer=false;
				do_executer="true".equals(this.getArguments("do-executer"));
				if(do_executer)	this.processMethod("executer");
			}catch(Exception e){}
		}
		//
		setReturnArguments("valid", valid ? "true":"false");
		setReturnArguments("why", why);
		return getServiceContext().doNextProcess();
	}

}
