package xlive.method.xml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathConstants;

import org.w3c.dom.Element;
import xlive.method.*;

public class xSaveXmlMethod extends xDefaultMethod{
	public Object process() throws xMethodException{
		String root_directory=getProperties("root-directory");
		if(root_directory==null||root_directory.trim().length()==0)	{
			throw this.createMethodException("xSaveXmlMethod", "root-directory is empty");
		}
		String work_directory=this.getArguments("work-directory");
		if(work_directory==null||work_directory.trim().length()==0) work_directory="";
		else work_directory=work_directory.startsWith(File.separator)?work_directory:File.separator+work_directory;
		File directory = directoryResolve(root_directory+work_directory);
		//
		if(!directory.exists()) directory.mkdirs();
		String prefix_file_name=this.getArguments("prefix-file-name");
		if(prefix_file_name==null||prefix_file_name.trim().length()==0) prefix_file_name="";
		//
		boolean valid=true;
		String why="";
		//
		String name=this.getArguments("name");
		File file= new File(directory,prefix_file_name+name);
		FileOutputStream output_stream = null;
		try {
			output_stream = new FileOutputStream(file);
			Element xml=(Element)this.getArguments("xml.sheet", XPathConstants.NODE);
			TransformerFactory.newInstance().newTransformer().transform(new DOMSource(xml), new StreamResult(output_stream));
			output_stream.close();
			output_stream=null;
		} catch (FileNotFoundException e) {
			valid=false;
			why=e.getMessage();
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			valid=false;
			why=e.getMessage();
			e.printStackTrace();
		} catch (TransformerException e) {
			valid=false;
			why=e.getMessage();
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			valid=false;
			why=e.getMessage();
			e.printStackTrace();
		} catch (IOException e) {
			valid=false;
			why=e.getMessage();
			e.printStackTrace();
		}finally{
			try{
				if(output_stream!=null)output_stream.close();
			}catch(Exception e){}
		}
		setReturnArguments("valid", valid ? "true":"false");
		setReturnArguments("why", why);
		return getServiceContext().doNextProcess();
	}

}
