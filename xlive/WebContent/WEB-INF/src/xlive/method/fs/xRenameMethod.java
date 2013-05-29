package xlive.method.fs;

import java.io.File;
import xlive.method.*;

public class xRenameMethod extends xDefaultMethod{
	public Object process() throws xMethodException{
		String root_directory=getProperties("root-directory");
		if(root_directory==null||root_directory.trim().length()==0)	{
			throw this.createMethodException("xRenameMethod", "root-directory is empty");
		}
		String work_directory=getArguments("work-directory");
		if(work_directory==null||work_directory.trim().length()==0) work_directory="";
		else work_directory=work_directory.startsWith(File.separator)?work_directory:File.separator+work_directory;
		File directory = directoryResolve(root_directory+work_directory);
		//
		String to_work_directory=getArguments("to-work-directory");
		if(to_work_directory==null||to_work_directory.trim().length()==0) to_work_directory="";
		else to_work_directory=to_work_directory.startsWith(File.separator)?to_work_directory:File.separator+to_work_directory;
		File to_directory = directoryResolve(root_directory+to_work_directory);
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
        		File file = new File(directory,name);
        		File to_file = new File(to_directory,to_name);
        		if(!file.exists()) {
        			why="name not found";
        		}else{
        			if(to_file.exists()){
        				why="the target name existed";
        			}else {
        				valid=file.renameTo(to_file);
        			}
        		}
        	}
        }catch(Exception e){valid=false;why=e.getLocalizedMessage();}
		setReturnArguments("valid", valid ? "true":"false");
		setReturnArguments("why", why);
		return getServiceContext().doNextProcess();
	}
}
