package xlive.method.authorized;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

import javax.xml.parsers.*;
import javax.xml.xpath.XPathConstants;

import org.xml.sax.*;
import java.io.IOException;
import org.w3c.dom.*;

import xlive.google.ds.xFile;
import xlive.method.*;
import xlive.*;

public class xLoginMethod extends xDefaultMethod{
	
	public Object process() throws xMethodException{
		if(xProbeServlet.isGAE()) return gae_process();
		////
		String user_id=getArguments("user-id");
		String user_pass=getArguments("user-pass");
		String hash=getArguments("hash");
		//
		String user_name=user_id;
		String[] names=user_id.split("@"); 
		if(names.length>=1)	user_name=names[0];
		//
		File userdata_directory=directoryResolve(getProperties("userdata-directory"));
		File user_file= new File(userdata_directory,user_name+".xml");
		//
		if(!user_file.exists()){
			setReturnArguments("valid", "false");
			setReturnArguments("why", "user id error,authorized xml not found ");
			getServiceContext().setLogout();
			return getServiceContext().doNextProcess(false);
		}
		Element user_element=null;
		try{
			user_element=DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(user_file).getDocumentElement();
		}catch(SAXParseException spe){
			throw createMethodException("xLoginMethod", spe.getLocalizedMessage());
		}catch(SAXException sxe) {
			throw createMethodException("xLoginMethod", sxe.getLocalizedMessage());
		}catch(IOException ioe) {
			throw createMethodException("xLoginMethod", ioe.getLocalizedMessage());
		}catch(ParserConfigurationException pce){
			throw createMethodException("xLoginMethod", pce.getLocalizedMessage());
		}finally{
		}
		String xml_password=(String)evaluate("data/user-pass", user_element,XPathConstants.STRING);
		Element data_node=(Element)evaluate("data", user_element,XPathConstants.NODE);
		String hash_password=xml_password;
		if(hash!=null && hash.trim().length() > 0){
			hash_password = hashPassword(xml_password);
			hash_password = hashPassword(hash+hash_password);
		}
		if(hash_password.equals(user_pass)){
			setReturnArguments("valid", "true");
			Node return_data=setReturnArguments("data", "");
			data_node=(Element)return_data.getOwnerDocument().adoptNode(data_node);
			getServiceContext().setLogin(data_node);
			data_node=(Element)data_node.cloneNode(true);
			Element user_pass_element=(Element)evaluate("user-pass", data_node,XPathConstants.NODE);
			user_pass_element.setTextContent("");
			return_data.getParentNode().replaceChild(data_node, return_data);
			return getServiceContext().doNextProcess();
		}else{
			setReturnArguments("valid", "false");
			setReturnArguments("why", "password error");
			getServiceContext().setLogout();
			return getServiceContext().doNextProcess(false);
		}
	}
	private String hashPassword(String password){
		return new xMD5().calcMD5(password);
	}
	private Object gae_process() throws xMethodException{
		String user_id=getArguments("user-id");
		String user_pass=getArguments("user-pass");
		String hash=getArguments("hash");
		//
		String user_name=user_id;
		String[] names=user_id.split("@"); 
		if(names.length>=1)	user_name=names[0];
		//
		String userdata_directory=getProperties("userdata-directory");
		userdata_directory=this.resourceDirectoryConvert(userdata_directory);
		xFile user_file= new xFile(userdata_directory+user_name+".xml");
		//
		if(!user_file.exists()){
			//check static files
			return gae_process_static();
			/*
			setReturnArguments("valid", "false");
			setReturnArguments("why", "user id error,authorized xml not found ");
			getServiceContext().setLogout();
			return getServiceContext().doNextProcess(false);
			*/
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
			throw createMethodException("xLoginMethod", spe.getLocalizedMessage());
		}catch(SAXException sxe) {
			throw createMethodException("xLoginMethod", sxe.getLocalizedMessage());
		}catch(IOException ioe) {
			throw createMethodException("xLoginMethod", ioe.getLocalizedMessage());
		}catch(ParserConfigurationException pce){
			throw createMethodException("xLoginMethod", pce.getLocalizedMessage());
		}finally{
		}
		String xml_password=(String)evaluate("data/user-pass", user_element,XPathConstants.STRING);
		Element data_node=(Element)evaluate("data", user_element,XPathConstants.NODE);
		String hash_password=xml_password;
		if(hash!=null && hash.trim().length() > 0){
			hash_password = hashPassword(xml_password);
			hash_password = hashPassword(hash+hash_password);
		}
		if(hash_password.equals(user_pass)){
			setReturnArguments("valid", "true");
			Node return_data=setReturnArguments("data", "");
			data_node=(Element)return_data.getOwnerDocument().adoptNode(data_node);
			getServiceContext().setLogin(data_node);
			data_node=(Element)data_node.cloneNode(true);
			Element user_pass_element=(Element)evaluate("user-pass", data_node,XPathConstants.NODE);
			user_pass_element.setTextContent("");
			return_data.getParentNode().replaceChild(data_node, return_data);
			return getServiceContext().doNextProcess();
		}else{
			setReturnArguments("valid", "false");
			setReturnArguments("why", "password error");
			getServiceContext().setLogout();
			return getServiceContext().doNextProcess(false);
		}
	}
	private Object gae_process_static() throws xMethodException{
		String user_id=getArguments("user-id");
		String user_pass=getArguments("user-pass");
		String hash=getArguments("hash");
		//
		String user_name=user_id;
		String[] names=user_id.split("@"); 
		if(names.length>=1)	user_name=names[0];
		//
		String userdata_directory=getProperties("userdata-directory");
		userdata_directory=this.resourceDirectoryConvert(userdata_directory);
		//
		InputStream inp = xResourceManager.getResourceAsStream(userdata_directory+user_name+".xml");
		//
		if(inp == null){
			setReturnArguments("valid", "false");
			setReturnArguments("why", "user id error,authorized xml not found ");
			getServiceContext().setLogout();
			return getServiceContext().doNextProcess(false);
		}
		/*
		if(user_file.length() == 0){
			setReturnArguments("valid", "false");
			setReturnArguments("why", "authorized xml is empty ");
			getServiceContext().setLogout();
			return getServiceContext().doNextProcess(false);
		}
		*/
		Element user_element=null;
		try{
			user_element=DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inp).getDocumentElement();
		}catch(SAXParseException spe){
			throw createMethodException("xLoginMethod", spe.getLocalizedMessage());
		}catch(SAXException sxe) {
			throw createMethodException("xLoginMethod", sxe.getLocalizedMessage());
		}catch(IOException ioe) {
			throw createMethodException("xLoginMethod", ioe.getLocalizedMessage());
		}catch(ParserConfigurationException pce){
			throw createMethodException("xLoginMethod", pce.getLocalizedMessage());
		}finally{
		}
		String xml_password=(String)evaluate("data/user-pass", user_element,XPathConstants.STRING);
		Element data_node=(Element)evaluate("data", user_element,XPathConstants.NODE);
		String hash_password=xml_password;
		if(hash!=null && hash.trim().length() > 0){
			hash_password = hashPassword(xml_password);
			hash_password = hashPassword(hash+hash_password);
		}
		if(hash_password.equals(user_pass)){
			setReturnArguments("valid", "true");
			Node return_data=setReturnArguments("data", "");
			data_node=(Element)return_data.getOwnerDocument().adoptNode(data_node);
			getServiceContext().setLogin(data_node);
			data_node=(Element)data_node.cloneNode(true);
			Element user_pass_element=(Element)evaluate("user-pass", data_node,XPathConstants.NODE);
			user_pass_element.setTextContent("");
			return_data.getParentNode().replaceChild(data_node, return_data);
			return getServiceContext().doNextProcess();
		}else{
			setReturnArguments("valid", "false");
			setReturnArguments("why", "password error");
			getServiceContext().setLogout();
			return getServiceContext().doNextProcess(false);
		}
	}


}
