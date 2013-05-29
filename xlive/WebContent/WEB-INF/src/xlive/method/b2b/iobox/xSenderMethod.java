package xlive.method.b2b.iobox;

import javax.xml.xpath.XPathConstants;
import org.w3c.dom.*;
import xlive.*;
import xlive.method.*;
import xlive.xml.xXmlDocument;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Vector;

public class xSenderMethod extends xDefaultMethod{
	public Object process()throws xMethodException{
		return synchronized_process();
	}
	private synchronized Object synchronized_process()throws xMethodException{
	    String target_server_url=getProperties("target-server-url");
	    String target_object_url=getProperties("target-object-url");
		target_object_url=target_object_url.replaceAll("\\.","/");
		//
	    String target_method_name="receiver";//getProperties("target-method-name");
	    String outbox_directory=getProperties("outbox-directory");
	    File outbox_file=directoryResolve(outbox_directory);
		int max_size=6*1000*1000;
		try{
			max_size=Integer.parseInt(getProperties("outbox-maximum-mb"))*1000*1000;
		}catch(Exception eint){}
	    //
	    Vector<File> file_vector = new Vector<File>();
	    Vector<File> directory_vector = new Vector<File>();
	    getFiles(outbox_file, file_vector, max_size, directory_vector);
	    if(file_vector.size()>0){
	    	StringBuffer param = new StringBuffer();
	    	String charset="UTF-8";//"iso-8859-1";
	    	try {
	    		param.append("method=").append(URLEncoder.encode(target_method_name, charset));
	    	} catch (UnsupportedEncodingException e) {
	    		e.printStackTrace();
	    	}
	    	String connection_url=target_server_url+"/xlive/web/"+target_object_url;
	    	if(sendFile(connection_url,param.toString(),file_vector,outbox_file.getAbsolutePath())){
	    		for(int i=0;i<file_vector.size();++i){
	    			File file=file_vector.get(i);
	    			if(file.isFile()) file.delete();
	    		}
	    		for(int i=0;i<directory_vector.size();++i){
	    			File file=directory_vector.get(i);
	    			if(file.isDirectory()) removeDirectory(file);
	    		}
	    	}
	    }else{
	    	setReturnArguments("valid", "true");
	    	setReturnArguments("why", "no files be sent");
	    }
		return getServiceContext().doNextProcess();
	}
	private void removeDirectory(File directory){
		 File[] files=directory.listFiles();
		 for(int i=0;i<files.length; ++i){
			 if(files[i].isDirectory()) removeDirectory(files[i]);
		 }
		 files=directory.listFiles();
		 if(files.length == 0) directory.delete();
	}
	private void getFiles(File directory, Vector<File> vector, int max_size, Vector<File> directory_vector){
		File[] files=directory.listFiles();
		for(int i=0;i<files.length;++i){
			if(files[i].isDirectory()) {
				getFiles(files[i],vector,max_size,null);
				if(directory_vector != null)directory_vector.add(files[i]);
			}
			else {
				if(files[i].length() < max_size)vector.add(files[i]);
			}
		}
	}
    private boolean sendFile(String url_string, String param, Vector<File> vector, String outbox_directory)throws xMethodException{
        URL url = null;
        HttpURLConnection connection = null;
        String message=null;
        Document response_document=null;
        FileInputStream file_output=null;
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
            	File file=vector.get(i);
            	multi_output.startPart("application/octet-stream");
            	String file_name= file.getAbsolutePath();
            	file_name=file_name.substring(outbox_directory.length());
            	multi_output.writeFileParam(file.getName(),file_name);
            	file_output=new FileInputStream(vector.get(i));
            	xUtility.copyStream(file_output, out);
            	file_output.close();
            	file_output=null;
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
           	try {if(file_output != null) file_output.close();}catch(Exception e){}
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
