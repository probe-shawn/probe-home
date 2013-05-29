package xlive.method.fs;

import java.io.File;
import xlive.method.*;

public class xDelMethod extends xDefaultMethod{
	public Object process() throws xMethodException{
		String root_directory=getProperties("root-directory");
		if(root_directory==null||root_directory.trim().length()==0)	{
			throw this.createMethodException("delMethod", "root-directory is empty");
		}
		String work_directory=this.getArguments("work-directory");
		if(work_directory==null||work_directory.trim().length()==0) work_directory="";
		else work_directory=work_directory.startsWith(File.separator)?work_directory:File.separator+work_directory;
		File directory=directoryResolve(root_directory+work_directory);
		
		boolean valid=false;
		String why="";
		String name=getArguments("name");
        try{
    		if(name==null || name.trim().length()<=0){
    			why="name is blank";
    		}else{
        		File file = new File(directory,name);
        		if(!file.exists()) {
        			valid=true;
        			why="file not found";
        		}
        		else{
        			if(file.isFile()) valid=file.delete();
        			else why="is not a file";
        		}
        	}
        }catch(Exception e){valid=false;why=e.getLocalizedMessage();}
		setReturnArguments("valid", valid ? "true":"false");
		setReturnArguments("why", why);
		return getServiceContext().doNextProcess();
	}
}
