package xlive.method.b2b.iobox.google;

import javax.xml.xpath.XPathConstants;
import org.w3c.dom.*;
import xlive.*;
import xlive.google.ds.xFile;
import xlive.method.*;
import xlive.xml.xXmlDocument;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

public class xSenderMethod extends xDefaultMethod{
	public Object process()throws xMethodException{
		return synchronized_process();
	}
	private synchronized Object synchronized_process()throws xMethodException{
	    String target_server_url=getProperties("target-server-url");
	    if(target_server_url.trim().length() == 0) return getServiceContext().doNextProcess();
	    String target_object_url=getProperties("target-object-url");
		target_object_url=target_object_url.replaceAll("\\.","/");
		//
	    String target_method_name="receiver";//getProperties("target-method-name");
	    String outbox_directory=getProperties("outbox-directory");
	    outbox_directory = this.resourceDirectoryConvert(outbox_directory);
		xFile outbox_directory_file = new xFile(outbox_directory);
		if(!outbox_directory_file.exists()) outbox_directory_file.makeDirs();
		int max_size=6*1000*1000;
		try{
			max_size=Integer.parseInt(getProperties("outbox-maximum-mb"))*1000*1000;
		}catch(Exception eint){}
	    //
	    Vector<xFile> file_vector = new Vector<xFile>();
	    Vector<xFile> directory_vector = new Vector<xFile>();
	    getFiles(outbox_directory_file, file_vector, max_size, directory_vector);
	    if(file_vector.size()>0){
	    	StringBuffer param = new StringBuffer();
	    	String charset="UTF-8";//"iso-8859-1";
	    	try {
	    		param.append("method=").append(URLEncoder.encode(target_method_name, charset));
	    	} catch (UnsupportedEncodingException e) {
	    		e.printStackTrace();
	    	}
	    	String connection_url=target_server_url+"/xlive/web/"+target_object_url;
	    	if(sendFile(connection_url,param.toString(),file_vector,outbox_directory)){
	    		for(int i=0;i<file_vector.size();++i){
	    			xFile file=file_vector.get(i);
	    			if(!file.isDirectory()) file.delete();
	    		}
	    		for(int i=0;i<directory_vector.size();++i){
	    			xFile file=directory_vector.get(i);
	    			if(file.isDirectory()) removeDirectory(file);
	    		}
	    	}
	    }else{
	    	setReturnArguments("valid", "true");
	    	setReturnArguments("why", "no files be sent");
	    }
		return getServiceContext().doNextProcess();
	}
	private void removeDirectory(xFile directory){
		 List<xFile> files = directory.list();
		 Iterator<xFile> it = files.iterator();
		 while(it.hasNext()){
			 xFile file = it.next();
			 if(file.isDirectory()) removeDirectory(file);
		 }
		 files = directory.list();
		 if(files.isEmpty()) directory.rd();
	}
	private void getFiles(xFile directory, Vector<xFile> vector, int max_size, Vector<xFile> directory_vector){
		List<xFile> files =  directory.list();
		Iterator<xFile> it = files.listIterator();
		while(it.hasNext()){
			xFile xfile = it.next();
			if(xfile.isDirectory()) {
				getFiles(xfile,vector,max_size,null);
				if(directory_vector != null)directory_vector.add(xfile);
			}
			else {
				if(xfile.getLength() < max_size)vector.add(xfile);
			}
		}
	}
    private boolean sendFile(String url_string, String param, Vector<xFile> vector, String outbox_directory)throws xMethodException{
        URL url = null;
        HttpURLConnection connection = null;
        String message=null;
        Document response_document=null;
        ByteArrayInputStream bais = null;
        try {
            DataOutputStream out = null;
            url = new URL(url_string+"?"+param);
            connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setRequestProperty("Content-Type", "multipart/form-data;charset=utf-8;boundary="+xMultiPartsOutputStream.boundary);
            connection.setRequestProperty("X-XLive-Version", "1.0");
            connection.setRequestProperty("X-XLive-Content", "binary");
            connection.setRequestProperty("X-XLive-ServerCode", xServerConfig.getServerCode());
            //connection.setRequestProperty("encoding", "UTF-8");
            out = new DataOutputStream(connection.getOutputStream());
            xMultiPartsOutputStream multi_output=new xMultiPartsOutputStream(out);
            for(int i=0; i< vector.size(); ++i){
            	xFile file=vector.get(i);
            	multi_output.startPart("application/octet-stream");
            	String file_name= file.getFileName();
            	file_name=file_name.substring(outbox_directory.length());
            	multi_output.writeFileParam(file.getName(),file_name);
            	byte[] bytedata = file.getBytes();
            	if(bytedata != null) bais = new ByteArrayInputStream(bytedata);
            	if(bais != null){
            		xUtility.copyStream(bais, out);
            		bais.close();
            		bais=null;
            	}
            	multi_output.endPart();
            }
            multi_output.finish();
            out.flush();
            out.close();
            out = null;
            InputStream in = new DataInputStream(connection.getInputStream());
			response_document=new xXmlDocument().createDocument(in);
            in.close();
        }catch(SocketException se){
        	se.printStackTrace();
        	message="SocketException :"+se.getLocalizedMessage();
        }catch(MalformedURLException mle) {
        	mle.printStackTrace();
        	message="MalformedURLException :"+mle.getLocalizedMessage();
        }catch(IOException ioe) {
        	ioe.printStackTrace();
        	message="IOException :"+ioe.getLocalizedMessage();
        }catch(Exception ex) {
        	ex.printStackTrace();
        	message="Exception :"+ex.getLocalizedMessage();
        }finally {
           	try {if(bais != null) bais.close();}catch(Exception e){}
           	try {if(connection != null) connection.disconnect();}catch(Exception econ){}
        }
        if(message==null && response_document!=null){
        	Node valid_node=setReturnArguments("valid", "");
        	Node return_node=valid_node.getParentNode();
        	return_node.removeChild(valid_node);
        	Element receiver_return_node=(Element)this.evaluate("receiver/return", response_document.getDocumentElement(), XPathConstants.NODE);
        	Node child=null;
        	while((child=receiver_return_node.getFirstChild()) != null) {
        		return_node.appendChild(return_node.getOwnerDocument().adoptNode(child));
        	}
        }else{
        	this.setReturnArguments("valid", message==null? "true":"false");
        	this.setReturnArguments("why", message);
        }
        return "true".equalsIgnoreCase(this.getReturnArguments("valid"));
    }
}
