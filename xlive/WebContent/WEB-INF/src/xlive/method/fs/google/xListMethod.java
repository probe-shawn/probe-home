package xlive.method.fs.google;

import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import xlive.xResourceManager;
import xlive.google.ds.xFile;
import xlive.method.*;

public class xListMethod extends xDefaultMethod{
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	private String filterRegexp=null;
	
	public Object process() throws xMethodException{
		this.extendDefaultPropertiesToArguments();
		String static_resource=getProperties("static-resource");
		if("true".equalsIgnoreCase(static_resource)) return static_resource_process();
		return datastore_process();
	}
	
	public Object datastore_process() throws xMethodException{
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
		String name=getArguments("name");
		directory=directory+(name==null?"":name);
		//
		boolean deep=true;
		try{
			deep="true".equalsIgnoreCase(getArguments("deep"));
		}catch(Exception e){}
		boolean directory_only=false;
		boolean file_only=false;
		try{
			String file_or_directory_only=getArguments("file-or-directory-only");
			directory_only="dir".equalsIgnoreCase(file_or_directory_only)||"directory".equalsIgnoreCase(file_or_directory_only);
			file_only="file".equalsIgnoreCase(file_or_directory_only);
		}catch(Exception e){}
		if(file_only) deep=false;
		filterRegexp=getArguments("filter-regexp");
		if(filterRegexp!=null&&filterRegexp.trim().length()>0)
			filterRegexp=filterRegexp.trim();
		else filterRegexp=null;
		//
		boolean valid=false;
		String why="";
        try{
        	xFile wroot = new xFile(directory);
        	if(!wroot.exists()) why="path not found";
        	else valid=datastore_list(wroot,deep,directory_only,file_only,(Element)setReturnArguments("root",""));
        }catch(Exception e){why=e.getLocalizedMessage();e.printStackTrace();}
		setReturnArguments("valid", valid ? "true":"false");
		setReturnArguments("why", why);
		return getServiceContext().doNextProcess();
	}
    private boolean datastore_list(xFile xfile, boolean deep, boolean directory_only, boolean file_only,Element root){
        List<xFile> list= xfile.list();
        if(list.isEmpty()) return true;
        Document doc=root.getOwnerDocument();
        Element element;
        Iterator<xFile> it=list.iterator();
        while(it.hasNext()){
        	xFile file = it.next();
        	String[] names=file.getFileName().split("/");
        	String name=names[names.length-1];
        	if(filterRegexp != null && !name.matches(filterRegexp))continue;
            if(file.isDirectory()) {
            	element= doc.createElement("directory");
            	element.setAttribute("name", name);
            	element.setAttribute("length", "0");
            	element.setAttribute("lastModified", sdf.format(file.getLastModified()));
            	if(deep) datastore_list(file,deep,directory_only,file_only,element);
            	if(file_only) continue;
            	root.appendChild(element);
            }else {
            	if(directory_only)continue;
            	element= doc.createElement("file");
            	element.setAttribute("length", String.valueOf(file.getLength()));
            	element.setAttribute("lastModified", sdf.format(file.getLastModified()));
            	element.setAttribute("name",name);
            	element.setTextContent(name);
            	root.appendChild(element);
            }
        }
        return true;
    }

	public Object static_resource_process() throws xMethodException{
		String root_directory=getProperties("root-directory");
		if(root_directory==null||root_directory.trim().length()==0)	{
			throw this.createMethodException("xFileMethod", "root-directory is empty");
		}
		if(root_directory.startsWith("./"))	root_directory=root_directory.replaceFirst("/", this.getObjectResourcePath());
		if(!root_directory.endsWith("/")) root_directory=root_directory+"/";
		//
		String work_directory=this.getArguments("work-directory");
		if(work_directory==null||work_directory.trim().length()==0) work_directory="";
		else {
			if(work_directory.startsWith("/")) work_directory=work_directory.substring(1);
			if(!work_directory.endsWith("/")) work_directory=work_directory+"/";
		}
		String directory = root_directory+work_directory;
		String name=getArguments("name");
		directory=directory+(name==null?"":name);
		//
		boolean deep=true;
		try{
			deep="true".equalsIgnoreCase(getArguments("deep"));
		}catch(Exception e){}
		boolean directory_only=false;
		boolean file_only=false;
		try{
			String file_or_directory_only=getArguments("file-or-directory-only");
			directory_only="dir".equalsIgnoreCase(file_or_directory_only)||"directory".equalsIgnoreCase(file_or_directory_only);
			file_only="file".equalsIgnoreCase(file_or_directory_only);
		}catch(Exception e){}
		if(file_only) deep=false;
		filterRegexp=getArguments("filter-regexp");
		if(filterRegexp!=null&&filterRegexp.trim().length()>0){
			filterRegexp=filterRegexp.trim();
		}else filterRegexp=null;
		//
		boolean valid=false;
		String why="";
        try{
        	if(xResourceManager.getResourcePaths(directory)==null) why="path not found";
        	else valid=static_resource_list(directory,deep,directory_only,file_only,(Element)setReturnArguments("root",""));
        }catch(Exception e){why=e.getLocalizedMessage();e.printStackTrace();}
        
        
		setReturnArguments("valid", valid ? "true":"false");
		setReturnArguments("why", why);
		return getServiceContext().doNextProcess();
	}
    @SuppressWarnings("unchecked")
	private boolean static_resource_list(String dir, boolean deep, boolean directory_only, boolean file_only,Element root){
        Set paths=xResourceManager.getResourcePaths(dir);
        if(paths==null) return true;
        Document doc=root.getOwnerDocument();
        Element element;
        Iterator it=paths.iterator();
        while(it.hasNext()){
        	String path=(String)it.next();
        	String[] names=path.split("/");
        	String name=names[names.length-1];
        	if(filterRegexp != null && !name.matches(filterRegexp))continue;
            if(path.endsWith("/")) {
            	element= doc.createElement("directory");
            	element.setAttribute("name", name);
            	element.setAttribute("length", "0");
            	element.setAttribute("lastModified","");
            	if(deep) static_resource_list(path,deep,directory_only,file_only,element);
            	if(file_only) continue;
            	root.appendChild(element);
            }else {
            	if(directory_only)continue;
            	element= doc.createElement("file");
            	element.setAttribute("length", "0");
            	element.setAttribute("lastModified", "");
            	element.setAttribute("name",name);
            	element.setTextContent(name);
            	root.appendChild(element);
            }
        }
        return true;
    }
}
