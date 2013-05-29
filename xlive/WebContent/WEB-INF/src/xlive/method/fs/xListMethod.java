package xlive.method.fs;

import java.io.*;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import xlive.method.*;

public class xListMethod extends xDefaultMethod{
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	private FilenameFilter fileNameFilter=null;
	private String filterRegexp=null;
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
		String name=getArguments("name");
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
			fileNameFilter=new FilenameFilter() {
				public boolean accept(File dir, String name){
					return name.matches(filterRegexp);
				}
			};
		}
		boolean existed=false,valid=false;
		String why="";
        File file = null;
        try{
        	file = new File(directory, name);
        	existed=file.exists();
        	if(!existed) why="path not found";
        	else valid=list(file,deep,directory_only,file_only,(Element)setReturnArguments("root",""));
        }catch(Exception e){why=e.getLocalizedMessage();e.printStackTrace();}
		setReturnArguments("valid", valid ? "true":"false");
		setReturnArguments("why", why);
		return getServiceContext().doNextProcess();
	}
    private boolean list(File file, boolean deep, boolean directory_only, boolean file_only,Element root){
        File[] ffs = (fileNameFilter != null) ? file.listFiles(fileNameFilter): file.listFiles();
        Document doc=root.getOwnerDocument();
        Element element;
        for(int i = 0; i < ffs.length; ++i) {
            if(ffs[i].isDirectory()) {
            	element= doc.createElement("directory");
            	element.setAttribute("name", ffs[i].getName());
            	element.setAttribute("length", "0");
            	element.setAttribute("lastModified", sdf.format(new Date(ffs[i].lastModified())));
            	if(deep) list(ffs[i],deep,directory_only,file_only,element);
            	if(file_only) continue;
            	root.appendChild(element);
            }else {
            	if(directory_only)continue;
            	element= doc.createElement("file");
            	element.setAttribute("length", String.valueOf(ffs[i].length()));
            	element.setAttribute("lastModified", sdf.format(new Date(ffs[i].lastModified())));
            	String name=ffs[i].getName();
            	element.setAttribute("name",name);
            	element.setTextContent(name);
            	root.appendChild(element);
            }
        }
        return true;
    }
}
