package xlive.method.pxml;

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

public class xPxmlMethod extends xDefaultMethod{
	
	public Object process() throws xMethodException{
		if(xProbeServlet.isGAE()) return gae_process();
		////
		String name=getArguments("name");
		if(!name.toLowerCase().endsWith(".xml")) name += ".xml";
		//
		File directory=directoryResolve("./data/");
		File pxml_file= new File(directory,name);
		//
		if(!pxml_file.exists()){
			setReturnArguments("valid", "false");
			setReturnArguments("why", "file not found :"+name);
			return getServiceContext().doNextProcess(false);
		}
		Element pxml_element=null;
		try{
			pxml_element=DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(pxml_file).getDocumentElement();
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
		Element pxml_node=(Element)evaluate("pxml", pxml_element,XPathConstants.NODE);
		Element data=this.setReturnArguments("data", "");
		Document doc = data.getOwnerDocument();
		data.appendChild(doc.adoptNode(pxml_node));
		setReturnArguments("valid", "true");
		setReturnArguments("why", "");
		return getServiceContext().doNextProcess();
	}
	private Object gae_process() throws xMethodException{
		String name=getArguments("name");
		if(!name.toLowerCase().endsWith(".xml")) name += ".xml";
		//
		String directory=this.resourceDirectoryConvert("gae/pxml/data/");
		xFile user_file= new xFile(directory+name+".xml");
		//
		if(!user_file.exists()){
			return gae_process_static();
		}
		if(user_file.getLength() == 0){
			setReturnArguments("valid", "false");
			setReturnArguments("why", "xml is empty :"+name);
			return getServiceContext().doNextProcess(false);
		}
		Element pxml_element=null;
		try{
			ByteArrayInputStream bais = new ByteArrayInputStream(user_file.getBytes());
			pxml_element=DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(bais).getDocumentElement();
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
		Element pxml_node=(Element)evaluate("pxml", pxml_element,XPathConstants.NODE);
		Element data=this.setReturnArguments("data", "");
		Document doc = data.getOwnerDocument();
		data.appendChild(doc.adoptNode(pxml_node));
		setReturnArguments("valid", "true");
		setReturnArguments("why", "");
		return getServiceContext().doNextProcess();
	}
	private Object gae_process_static() throws xMethodException{
		String name=getArguments("name");
		if(!name.toLowerCase().endsWith(".xml")) name += ".xml";
		//
		String directory=this.resourceDirectoryConvert("./data/");
		InputStream inp = xResourceManager.getResourceAsStream(directory+name);
		//
		if(inp == null){
			setReturnArguments("valid", "false");
			setReturnArguments("why", "xml not found :"+name);
			getServiceContext().setLogout();
			return getServiceContext().doNextProcess(false);
		}
		Element pxml_element=null;
		try{
			pxml_element=DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inp).getDocumentElement();
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
		//Element pxml_node=(Element)evaluate("pxml", pxml_element,XPathConstants.NODE);
		Element data=this.setReturnArguments("data", "");
		Document doc = data.getOwnerDocument();
		data.appendChild(doc.adoptNode(pxml_element));
		setReturnArguments("valid", "true");
		setReturnArguments("why", "");
		return getServiceContext().doNextProcess();
	}


}
