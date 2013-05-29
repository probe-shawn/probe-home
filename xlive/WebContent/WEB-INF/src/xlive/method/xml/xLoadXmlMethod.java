package xlive.method.xml;

import java.io.File;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import xlive.method.*;
import xlive.xml.xXmlDocument;

public class xLoadXmlMethod extends xDefaultMethod{
	public Object process() throws xMethodException{
		String root_directory=getProperties("root-directory");
		if(root_directory==null||root_directory.trim().length()==0)	{
			throw this.createMethodException("xLoadXmlMethod", "root-directory is empty");
		}
		String work_directory=this.getArguments("work-directory");
		if(work_directory==null||work_directory.trim().length()==0) work_directory="";
		else work_directory=work_directory.startsWith(File.separator)?work_directory:File.separator+work_directory;
		File directory=directoryResolve(root_directory+work_directory);
		//
		String name=getArguments("name"); 
		this.logMessage("name :"+name);
		File file= new File(directory, name);
		//
		String why="";
		boolean valid=true;
		if(!file.exists()){
			why ="file not found :"+name;
			valid=false;
		}
		if(valid && !name.endsWith(".xml")){
			why = "is not a xml file";
			valid = false;
		}
		if(valid){
			try{
				Document doc = new xXmlDocument().createDocument(file);
				Element element = doc.getDocumentElement();
				Element xml=this.setReturnArguments("xml", "");
				xml.appendChild(xml.getOwnerDocument().adoptNode(element));
			}catch(Exception e){
				valid = false;
				why = e.getMessage();
			}
		}
		this.setReturnArguments("why", why);
		this.setReturnArguments("valid", String.valueOf(valid));
		return getServiceContext().doNextProcess();
	}
}
