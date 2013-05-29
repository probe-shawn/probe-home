package xlive.method.authorized;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;

import javax.xml.parsers.*;
import javax.xml.xpath.XPathConstants;

import org.xml.sax.*;
import java.io.IOException;
import org.w3c.dom.*;

import xlive.xProbeServlet;
import xlive.google.ds.xFile;
import xlive.method.*;
import xlive.wrap.xWrapFileOutputStream;
import xlive.xml.xXmlDocument;

public class xChangePasswordMethod extends xDefaultMethod{

	public Object process() throws xMethodException{
		if(xProbeServlet.isGAE()) return gae_process();
		/////
		String directory=getProperties("userdata-directory");
		String user_id=getArguments("user-id");
		String user_pass=getArguments("user-pass");
		String user_pass2=getArguments("user-pass2");
		//
		String user_name=user_id;
		String[] names=user_id.split("@"); 
		if(names.length>=1)	user_name=names[0];
		//
		File userdata_directory=directoryResolve(directory);
		File user_file= new File(userdata_directory,user_name+".xml");
		if(!user_file.exists()){
			setReturnArguments("valid", "false");
			setReturnArguments("why", "user authorized file not found");
			return getServiceContext().doNextProcess(false);
		}
		Element user_element=null;
		try{
			user_element=DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(user_file).getDocumentElement();
		}catch(SAXParseException spe){
			throw createMethodException("xChangePasswordMethod", spe.getLocalizedMessage());
		}catch(SAXException sxe) {
			throw createMethodException("xChangePasswordMethod", sxe.getLocalizedMessage());
		}catch(IOException ioe) {
			throw createMethodException("xChangePasswordMethod", ioe.getLocalizedMessage());
		}catch(ParserConfigurationException pce){
			throw createMethodException("xChangePasswordMethod", pce.getLocalizedMessage());
		}finally{
		}
		if(user_element==null){
			setReturnArguments("valid", "false");
			setReturnArguments("why", "user xml error");
			return getServiceContext().doNextProcess(false);
		}
		String xml_password=(String)evaluate("data/user-pass", user_element,XPathConstants.STRING);
		if(!user_pass.equals(xml_password)){
			setReturnArguments("valid", "false");
			setReturnArguments("why", "user password error");
			return getServiceContext().doNextProcess(false);
		}
		Element pass_node=(Element)evaluate("data/user-pass", user_element,XPathConstants.NODE);
		pass_node.setTextContent(user_pass2);
		OutputStream fo = null;
		try {
			fo = xWrapFileOutputStream.fileOutputStream(user_file);
			new xXmlDocument().Transform(user_element, fo);
		}catch(Exception e){
			throw createMethodException("xChangePasswordMethod", e.getLocalizedMessage());
		}finally{
			try{
				if(fo != null) fo.close();
			}catch(Exception e){}
		}
		setReturnArguments("valid", "true");
		setReturnArguments("why", "");
		return getServiceContext().doNextProcess();
	}
	private Object gae_process() throws xMethodException{
		String userdata_directory=getProperties("userdata-directory");
		userdata_directory=this.resourceDirectoryConvert(userdata_directory);
		String user_id=getArguments("user-id");
		String user_pass=getArguments("user-pass");
		String user_pass2=getArguments("user-pass2");
		//
		String user_name=user_id;
		String[] names=user_id.split("@"); 
		if(names.length>=1)	user_name=names[0];
		//
		xFile user_file= new xFile(userdata_directory+user_name+".xml");
		if(!user_file.exists()){
			setReturnArguments("valid", "false");
			setReturnArguments("why", "user authorized file not found");
			return getServiceContext().doNextProcess(false);
		}
		if(user_file.getLength() == 0){
			setReturnArguments("valid", "false");
			setReturnArguments("why", "authorized xml is empty ");
			getServiceContext().setLogout();
			return getServiceContext().doNextProcess(false);
		}
		Element user_element=null;
		try{
			ByteArrayInputStream bais = new ByteArrayInputStream(user_file.getBytes());
			user_element=DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(bais).getDocumentElement();
		}catch(SAXParseException spe){
			throw createMethodException("xChangePasswordMethod", spe.getLocalizedMessage());
		}catch(SAXException sxe) {
			throw createMethodException("xChangePasswordMethod", sxe.getLocalizedMessage());
		}catch(IOException ioe) {
			throw createMethodException("xChangePasswordMethod", ioe.getLocalizedMessage());
		}catch(ParserConfigurationException pce){
			throw createMethodException("xChangePasswordMethod", pce.getLocalizedMessage());
		}finally{
		}
		if(user_element==null){
			setReturnArguments("valid", "false");
			setReturnArguments("why", "user xml error");
			return getServiceContext().doNextProcess(false);
		}
		String xml_password=(String)evaluate("data/user-pass", user_element,XPathConstants.STRING);
		if(!user_pass.equals(xml_password)){
			setReturnArguments("valid", "false");
			setReturnArguments("why", "user password error");
			return getServiceContext().doNextProcess(false);
		}
		Element pass_node=(Element)evaluate("data/user-pass", user_element,XPathConstants.NODE);
		pass_node.setTextContent(user_pass2);
		
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			new xXmlDocument().Transform(user_element, baos);
			user_file.setBytes(baos.toByteArray());
		}catch(Exception e){
			throw createMethodException("xChangePasswordMethod", e.getLocalizedMessage());
		}
		setReturnArguments("valid", "true");
		setReturnArguments("why", "");
		return getServiceContext().doNextProcess();
	}

}
