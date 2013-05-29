package xlive.method.b2b.iobox.google;

import java.io.ByteArrayOutputStream;
import javax.servlet.http.HttpServletRequest;
import org.w3c.dom.Element;

import xlive.google.ds.xFile;
import xlive.method.*;

import com.oreilly.servlet.multipart.*;

public class xReceiverMethod extends xDefaultMethod{
	public Object process() throws xMethodException{
	    String inbox_directory=getProperties("inbox-directory");
	    inbox_directory = this.resourceDirectoryConvert(inbox_directory);
		xFile inbox_directory_file = new xFile(inbox_directory);
		if(!inbox_directory_file.exists()) inbox_directory_file.makeDirs();
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
				String file_pathname=null;
				if(part.isFile() && (file_pathname=((FilePart)part).getFilePath())!=null){
					file_pathname =file_pathname.replaceAll("\\\\", "/");
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					((FilePart)part).writeTo(baos);
					xFile xfile = new xFile(inbox_directory+file_pathname);
					long length=baos.size();
					xfile.setBytes(baos.toByteArray());
					Element return_file=createElement("file",((FilePart)part).getFileName());
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
