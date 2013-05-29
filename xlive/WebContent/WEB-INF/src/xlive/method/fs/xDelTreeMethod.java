package xlive.method.fs;

import java.io.File;
import xlive.method.*;

public class xDelTreeMethod extends xDefaultMethod{
	public Object process() throws xMethodException{
		String root_directory=getProperties("root-directory");
		if(root_directory==null||root_directory.trim().length()==0)	{
			throw this.createMethodException("delTreeMethod", "root-directory is empty");
		}
		String work_directory=this.getArguments("work-directory");
		if(work_directory==null||work_directory.trim().length()==0) work_directory="";
		else work_directory=work_directory.startsWith(File.separator)?work_directory:File.separator+work_directory;
		File directory=directoryResolve(root_directory+work_directory);
		//
		boolean valid=false;
		String why="";
		String name=getArguments("name");
        try{
    		if(name==null || name.trim().length()<=0){
    			why="name is blank";
    		}else {
        		File file = new File(directory,name);
        		if(!file.exists()) {
        			valid=true;
        			why="file or directory not found";
        		}
        		else valid=this.delTree(file);
        	}
        }catch(Exception e){valid=false;why=e.getLocalizedMessage();}
		setReturnArguments("valid", valid ? "true":"false");
		setReturnArguments("why", why);
		return getServiceContext().doNextProcess();
	}
    private boolean delTree(File file){
        File[] ffs = file.listFiles();
        for(int i = 0; i < ffs.length; ++i) {
        	if(ffs[i].isDirectory()) {if(!delTree(ffs[i])) return false;}
            else if(!ffs[i].delete()) return false;
        }
        return file.delete();
    }
}
