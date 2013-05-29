package xlive.method.fs.google;

import java.text.SimpleDateFormat;

import xlive.google.ds.xFile;
import xlive.method.*;

public class xFileMethod extends xDefaultMethod{
	public Object process() throws xMethodException{
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
		boolean existed=false,valid=false;
        xFile xfile = null;
		String why="";
		String name=getArguments("name");
		if(name!=null&&name.trim().length()>0){
			valid=true;
		}else{
			valid=false;
			why="name is blank";
		}
        try{
        	if(valid){
        		xfile = new xFile(directory+name);
        		existed=xfile.exists();
        	}
        }catch(Exception e){valid=false;why=e.getLocalizedMessage();}
        
		setReturnArguments("exists", existed ? "true":"false");
		if(existed){
			setReturnArguments("is-directory", xfile.isDirectory() ? "true":"false");
			setReturnArguments("is-hidden", "false");
			setReturnArguments("last-modified", new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSSS").format(xfile.getLastModified()));
			setReturnArguments("length", String.valueOf(xfile.getLength()));
		}
		setReturnArguments("why", why);
		//
		return getServiceContext().doNextProcess();
	}
}
