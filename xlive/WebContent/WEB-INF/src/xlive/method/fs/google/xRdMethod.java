package xlive.method.fs.google;

import xlive.google.ds.xFile;
import xlive.method.*;

public class xRdMethod extends xDefaultMethod{
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
		boolean valid=false;
		String why="";
        String name=getArguments("name");
        try{
            if(name==null || name.trim().length()<=0){
            	valid=false;
            	why="name is blank";
            }else{
            	xFile xfile = new xFile(directory+name);
        		//File file = new File(directory, name);
        		if(!xfile.exists()) {
        			valid=true;
        			why="directory not found";
        		}
        		else{
        			if(xfile.isDirectory()) valid=xfile.rd();
        			else why="is not a directory";
        		}
        	}
        }catch(Exception e){valid=false;why=e.getLocalizedMessage();}
		setReturnArguments("valid", valid ? "true":"false");
		setReturnArguments("why", why);
		return getServiceContext().doNextProcess();
	}
}
