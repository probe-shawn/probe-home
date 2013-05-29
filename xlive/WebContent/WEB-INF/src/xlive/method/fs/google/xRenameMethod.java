package xlive.method.fs.google;

import xlive.google.ds.xFile;
import xlive.method.*;

public class xRenameMethod extends xDefaultMethod{
	public Object process() throws xMethodException{
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
		//
		String to_work_directory=getArguments("to-work-directory");
		if(to_work_directory==null||to_work_directory.trim().length()==0) to_work_directory="";
		else {
			if(to_work_directory.startsWith("/")) to_work_directory=to_work_directory.substring(1);
			if(!to_work_directory.endsWith("/")) to_work_directory=to_work_directory+"/";
		}
		String to_directory = root_directory+to_work_directory;
		//
		boolean valid=true;
		String why="";
		String name=getArguments("name");
		if(name==null || name.trim().length()<=0) {
			why="name is blank";
			valid=false;
		}
		String to_name=getArguments("to-name");
		if(to_name == null || to_name.trim().length() <= 0){
			why="target name is blank";
			valid=false;
		}
        try{
        	if(valid){
        		xFile xfile = new xFile(directory+name);
        		xFile to_xfile = new xFile(to_directory+to_name);
        		if(!xfile.exists()) {
        			why="name not found";
        		}else{
        			if(to_xfile.exists()){
        				why="the target name existed";
        			}else {
        				valid=xfile.renameTo(to_directory+to_name,true);
        			}
        		}
        	}
        }catch(Exception e){valid=false;why=e.getLocalizedMessage();}
		setReturnArguments("valid", valid ? "true":"false");
		setReturnArguments("why", why);
		return getServiceContext().doNextProcess();
	}
}
