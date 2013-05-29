package xlive.method.fs.google;

import java.util.Iterator;
import java.util.List;

import xlive.google.ds.xFile;
import xlive.method.*;

public class xDelTreeMethod extends xDefaultMethod{
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
    			why="name is blank";
    		}else {
        		xFile xfile = new xFile(directory+name);
        		if(!xfile.exists()) {
        			valid=true;
        			why="file or directory not found";
        		}
        		else valid=this.delTree(xfile);
        	}
        }catch(Exception e){valid=false;why=e.getLocalizedMessage();}
		setReturnArguments("valid", valid ? "true":"false");
		setReturnArguments("why", why);
		return getServiceContext().doNextProcess();
	}
    private boolean delTree(xFile xfile){
    	List<xFile> list = xfile.list();
    	Iterator<xFile> it = list.iterator();
    	while(it.hasNext()){
    		xFile file=it.next();
        	if(file.isDirectory()) {if(!delTree(file)) return false;}
            else if(!file.delete()) return false;
    	}
    	return (xfile.isDirectory())? xfile.rd() : xfile.delete();
    }
}
