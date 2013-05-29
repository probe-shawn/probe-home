package xlive.method.b2b.register;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import xlive.xProbeServlet;
import xlive.xResourceManager;
import xlive.google.ds.xFile;
import xlive.method.*;
import xlive.wrap.xWrapFileOutputStream;
import xlive.wrap.xWrapInetAddress;
import xlive.xml.xXmlDocument;

public class xRegisterMethod extends xDefaultMethod {
	public Object process()throws xMethodException{
		this.extendDefaultPropertiesToArguments();
		boolean valid=true;
		String why = "";
		String server_url=this.getProperties("server-url");
        String object_url=this.getProperties("object-url");
        object_url=object_url.replaceAll("\\.","/");
		try{
			this.setArguments("host-name", xWrapInetAddress.getHostName());
			this.setArguments("internal-url", xWrapInetAddress.getHostAddress());
		}catch(Exception e){}
    	if(valid) valid &= this.processInternetWebObjectMethod(server_url, object_url, "register");
    	this.setReturnArguments("valid", String.valueOf(valid));
    	this.setReturnArguments("why", why);
    	return getServiceContext().doNextProcess(valid);
	}
	public Object process2()throws xMethodException{
		this.extendDefaultPropertiesToArguments();
		boolean valid=true;
		StringBuffer why = new StringBuffer();
		if(xProbeServlet.isGAE()) valid=gaePrepareArguments(why);
		else valid = prepareArguments(why);
		this.setArguments("why", "");
		this.setArguments("valid", "");
		String server_url=this.getProperties("server-url");
        String object_url=this.getProperties("object-url");
        object_url=object_url.replaceAll("\\.","/");
    	if(valid) {
    		valid &= this.processInternetWebObjectMethod(server_url, object_url, "register");
    		if(valid){
    			if(xProbeServlet.isGAE()) valid &= gaeSaveRegisterResult(why);
    			else valid &= saveRegisterResult(why);
    		}
    	}
    	this.setReturnArguments("valid", String.valueOf(valid));
    	this.setReturnArguments("why", why.toString());
    	return getServiceContext().doNextProcess(valid);
	}
	private boolean prepareArguments(StringBuffer why) throws xMethodException{
		boolean valid = true;
		try {
			File reg_file = this.directoryResolve("/WEB-INF/xlive-register.xml");
			if(reg_file.exists()){
				Element reg=new xXmlDocument().createDocument(reg_file).getDocumentElement();
				Element arg=this.createElement("arguments");
				arg.appendChild(arg.getOwnerDocument().adoptNode(reg.cloneNode(true)));
				this.argumentsOperation(arg, "overwrite");
			}
			try{
				this.setArguments("host-name", xWrapInetAddress.getHostName());
				this.setArguments("internal-url", xWrapInetAddress.getHostAddress());
			}catch(Exception e){}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (SAXParseException e) {
			e.printStackTrace();
			valid = false;
			why.append(e.getMessage());
		} catch (SAXException e) {
			e.printStackTrace();
			valid = false;
			why.append(e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			valid = false;
			why.append(e.getMessage());
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			valid = false;
			why.append(e.getMessage());
		}
		return valid;
	}
	private synchronized boolean saveRegisterResult(StringBuffer why){
		boolean valid=true;
		OutputStream output=null;
		try {
			File reg_file = this.directoryResolve("/WEB-INF/xlive-register.xml");
			output = xWrapFileOutputStream.fileOutputStream(reg_file);
			Element reg=(Element)this.getServiceContext().getArguments("./return/register/return", XPathConstants.NODE);
			Element output_reg = (Element)reg.cloneNode(true);
			output_reg.getOwnerDocument().renameNode(output_reg, null, "register");
			new xXmlDocument().Transform(output_reg, output);
		} catch (FileNotFoundException e) {
			valid=false;
			why.append(e.getMessage());
			e.printStackTrace();
		} catch (XPathExpressionException e) {
			valid=false;
			why.append(e.getMessage());
			e.printStackTrace();
		} catch (NullPointerException e) {
			valid=false;
			why.append(e.getMessage());
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			valid=false;
			why.append(e.getMessage());
			e.printStackTrace();
		} catch (TransformerException e) {
			valid=false;
			why.append(e.getMessage());
			e.printStackTrace();
		}finally{
			try{
				if(output != null) output.close();
			}catch(Exception e){}
		}
		return valid;
	}
	private boolean gaePrepareArguments(StringBuffer why) throws xMethodException{
		boolean valid = true;
		try {
			Element reg = null;
			xFile reg_file = new xFile("/WEB-INF/xlive-register.xml");
			if(reg_file.exists() && reg_file.getLength() > 0){
				ByteArrayInputStream bais = new ByteArrayInputStream(reg_file.getBytes());
				reg = new xXmlDocument().createDocument(bais).getDocumentElement();
			}else{
				InputStream inp = xResourceManager.getResourceAsStream("/WEB-INF/xlive-register.xml");
				if(inp != null)	reg = new xXmlDocument().createDocument(inp).getDocumentElement();
			}
			if(reg != null){
				Element arg=this.createElement("arguments");
				arg.appendChild(arg.getOwnerDocument().adoptNode(reg));
				this.argumentsOperation(arg, "overwrite");
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (SAXParseException e) {
			e.printStackTrace();
			valid = false;
			why.append(e.getMessage());
		} catch (SAXException e) {
			e.printStackTrace();
			valid = false;
			why.append(e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			valid = false;
			why.append(e.getMessage());
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			valid = false;
			why.append(e.getMessage());
		}
		return valid;
	}
	private synchronized boolean gaeSaveRegisterResult(StringBuffer why){
		boolean valid=true;
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try {
			xFile reg_file = new xFile("/WEB-INF/xlive-register.xml");
			Element reg=(Element)this.getServiceContext().getArguments("./return/register/return", XPathConstants.NODE);
			Element output_reg = (Element)reg.cloneNode(true);
			output_reg.getOwnerDocument().renameNode(output_reg, null, "register");
			new xXmlDocument().Transform(output_reg, output);
			reg_file.setBytes(output.toByteArray());
		} catch (XPathExpressionException e) {
			valid=false;
			why.append(e.getMessage());
			e.printStackTrace();
		} catch (NullPointerException e) {
			valid=false;
			why.append(e.getMessage());
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			valid=false;
			why.append(e.getMessage());
			e.printStackTrace();
		} catch (TransformerException e) {
			valid=false;
			why.append(e.getMessage());
			e.printStackTrace();
		}finally{
			try{
				if(output != null) output.close();
			}catch(Exception e){}
		}
		return valid;
	}

/*
	public Object process2()throws xMethodException{
		File key_xml=this.directoryToFile("./key.xml");
		File status_xml=this.directoryToFile("./status.xml");
		ByteArrayOutputStream key_stream=null;
		ByteArrayOutputStream status_stream=null;
		long key_lastmodified=0;
		try{
			key_lastmodified=Long.parseLong(getProperties("register.license"));
		}catch(Exception e){}
		long status_lastmodified=0;
		try{
			status_lastmodified=Long.parseLong(getProperties("register.status"));
		}catch(Exception e){}
		try {
			if(key_xml.lastModified()!=key_lastmodified){
				key_stream = new ByteArrayOutputStream();
				FileInputStream input_stream = new FileInputStream(key_xml);
				if(xUtility.copyStream(input_stream, key_stream) <= 0)key_stream=null;
				try{input_stream.close();}catch(Exception e){}
				setProperties("register.license", String.valueOf(key_xml.lastModified()));
				//Element key_root=DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(key_input_stream).getDocumentElement();
			}
			if(status_xml.lastModified()!= status_lastmodified){
				status_stream = new ByteArrayOutputStream();
				FileInputStream input_stream = new FileInputStream(status_xml);
				if(xUtility.copyStream(input_stream, status_stream) <= 0)status_stream=null;
				try{input_stream.close();}catch(Exception e){}
				setProperties("register.status", String.valueOf(status_xml.lastModified()));
				//Element status_root=DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(status_input_stream).getDocumentElement();
			}
		}catch(IOException e) {
			e.printStackTrace();
			throw this.createMethodException("xRegisterMethod[IOException]",e.getLocalizedMessage());
		}catch(Exception e){
			e.printStackTrace();
			throw this.createMethodException("xRegisterMethod[SAXParseException]",e.getLocalizedMessage());
		}
		if(key_stream == null && status_stream == null) return getServiceContext().doNextProcess();
		//
		String sever_url=this.getProperties("server-url");
        String object_url=this.getProperties("object-url");
        object_url=object_url.replaceAll("\\.","/");
    	String connection_url=sever_url+"/xlive/web/"+object_url+"?method=register";
    	
    	ByteArrayOutputStream[]key_status=callProbe(connection_url,key_stream,(key_stream != null) ? key_xml.length():0,status_stream, (status_stream!=null)? status_xml.length():0);
    	
    	ByteArrayOutputStream return_key_stream=key_status[0];
        if(return_key_stream != null){
        	try{
        		FileOutputStream key_output_stream=new FileOutputStream(key_xml);
        		return_key_stream.writeTo(key_output_stream);
        		key_output_stream.close();
        		setProperties("register.license", String.valueOf(key_xml.lastModified()));
        		xWebInformation.readKeyXmlFile();
        	}catch(Exception e){
        		e.printStackTrace();
    			throw this.createMethodException("xRegisterMethod[SaveKeyXml]",e.getLocalizedMessage());
        	}
        }
    	ByteArrayOutputStream return_status_stream=key_status[1];
        if(return_status_stream != null){
        	try{
        		FileOutputStream status_output_stream=new FileOutputStream(status_xml);
        		return_status_stream.writeTo(status_output_stream);
        		status_output_stream.close();
        		setProperties("register.status", String.valueOf(status_xml.lastModified()));
        		xWebInformation.readStatusXmlFile();
        	}catch(Exception e){
        		e.printStackTrace();
    			throw this.createMethodException("xRegisterMethod[SaveStatusXml]",e.getLocalizedMessage());
        	}
        }
		return getServiceContext().doNextProcess();
	}
    private ByteArrayOutputStream[] callProbe(String url_string,ByteArrayOutputStream key, long key_size, ByteArrayOutputStream status, long status_size)throws xMethodException{
        URL url = null;
        HttpURLConnection connection = null;
        ByteArrayOutputStream return_key_stream=null;
        ByteArrayOutputStream return_status_stream=null;
        boolean valid=true;
        String why="";
        try {
            DataOutputStream out = null;
            url = new URL(url_string);
            connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setRequestProperty("Content-Type", "application/octet-stream");
            connection.setRequestProperty("X-XLive-Version", "1.0");
            connection.setRequestProperty("X-XLive-Content", "binary");
            connection.setRequestProperty("X-XLive-ServerCode", xWebInformation.getProbeXServerId());
            String xsessionid=xWebInformation.getProbeXSessionId();
            if(xsessionid!=null && xsessionid.trim().length()>0) connection.setRequestProperty("xsessionid", xsessionid);
            if(key != null && key_size>0)connection.setRequestProperty("key", String.valueOf(key_size));
            if(status != null && status_size>0)connection.setRequestProperty("status", String.valueOf(status_size));
            out = new DataOutputStream(connection.getOutputStream());
            if(key != null && key_size>0) key.writeTo(out);
            if(status != null && status_size>0) status.writeTo(out);
            out.flush();
            out.close();
            out = null;
            InputStream in = new DataInputStream(connection.getInputStream());
            String session_id=(String)connection.getHeaderField("xsessionid");
            if(session_id != null) xWebInformation.setProbeXSessionId(session_id);
            String return_key_size = connection.getHeaderField("key");
            if(return_key_size != null && return_key_size.trim().length()>0){
            	try{
            		long length=Long.parseLong(return_key_size);
            		return_key_stream = new ByteArrayOutputStream();
            		xUtility.copyStream(in, return_key_stream, length);
            	}catch(Exception e){}
            }
            String return_status_size = connection.getHeaderField("status");
            if(return_status_size != null && return_status_size.trim().length()>0){
            	try{
            		long length=Long.parseLong(return_status_size);
            		return_status_stream= new ByteArrayOutputStream();
            		xUtility.copyStream(in, return_status_stream, length);
            	}catch(Exception e){}
            }
            in.close();
        }catch(SocketException e){
        	return_key_stream=null;
        	return_status_stream=null;
        	valid=false;
        	why=e.getLocalizedMessage();
        	e.printStackTrace();
        }catch(MalformedURLException e) {
        	return_key_stream=null;
        	return_status_stream=null;
        	valid=false;
        	why=e.getLocalizedMessage();
        	e.printStackTrace();
        }catch(IOException e) {
        	return_key_stream=null;
        	return_status_stream=null;
        	valid=false;
        	why=e.getLocalizedMessage();
        	e.printStackTrace();
        }catch(Exception e) {
        	return_key_stream=null;
        	return_status_stream=null;
        	valid=false;
        	why=e.getLocalizedMessage();
        	e.printStackTrace();
        }finally {
        	try {if(connection != null) connection.disconnect();}catch(Exception e){}
        }
        setReturnArguments("valid", (valid ? "true":"false"));
        setReturnArguments("why", why);
        return new ByteArrayOutputStream[]{return_key_stream,return_status_stream};
    }
	*/
}
