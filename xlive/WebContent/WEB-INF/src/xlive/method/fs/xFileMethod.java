package xlive.method.fs;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import xlive.method.*;

public class xFileMethod extends xDefaultMethod{
	public Object process() throws xMethodException{
		String root_directory=getProperties("root-directory");
		if(root_directory==null||root_directory.trim().length()==0)	{
			throw this.createMethodException("xFileMethod", "root-directory is empty");
		}
		String work_directory=this.getArguments("work-directory");
		if(work_directory==null||work_directory.trim().length()==0) work_directory="";
		else work_directory=work_directory.startsWith(File.separator)?work_directory:File.separator+work_directory;
		File directory = directoryResolve(root_directory+work_directory);
		//
		boolean existed=false,valid=false;
        File file = null;
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
        		file = new File(directory, name);
        		existed=file.exists();
        	}
        }catch(Exception e){valid=false;why=e.getLocalizedMessage();}
        
		setReturnArguments("exists", existed ? "true":"false");
		if(existed){
			setReturnArguments("is-directory", file.isDirectory() ? "true":"false");
			setReturnArguments("is-hidden", file.isHidden() ? "true":"false");
			setReturnArguments("last-modified", new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSSS").format(new Date(file.lastModified())));
			setReturnArguments("length", String.valueOf(file.length()));
		}
		setReturnArguments("why", why);
		//
		return getServiceContext().doNextProcess();
	}
}
