package xlive.method.b2b.iobox.google;

import org.w3c.dom.*;

import xlive.*;
import xlive.google.ds.xFile;
import xlive.method.*;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;

public class xFetcherMethod extends xDefaultMethod{
	public Object process()throws xMethodException{
		return synchronized_process();
	}
	private synchronized Object synchronized_process()throws xMethodException{
	    String target_server_url=getProperties("target-server-url");
	    if(target_server_url.trim().length() == 0) return getServiceContext().doNextProcess();
	    String target_object_url=getProperties("target-object-url");
		target_object_url=target_object_url.replaceAll("\\.","/");
		//
	    String target_method_name="supplier";
	    String inbox_directory=getProperties("inbox-directory");
	    inbox_directory=this.resourceDirectoryConvert(inbox_directory);
	    xFile inbox_directory_file = new xFile(inbox_directory);
	    if(!inbox_directory_file.exists()) inbox_directory_file.makeDirs();
	    //
	    boolean valid=true;
    	StringBuffer param = new StringBuffer();
    	String charset="UTF-8";
    	try {
    		param.append("method=").append(URLEncoder.encode(target_method_name, charset));
    	} catch (UnsupportedEncodingException e) {
    		e.printStackTrace();
    	}
	    String connection_url=target_server_url+"/xlive/web/"+target_object_url;
	    valid=fetchFile(connection_url,param.toString(),inbox_directory);
		//
	    if(valid){
	    	try{
	    		boolean do_dispatcher="true".equals(this.getArguments("do-dispatcher"));
	    		if(do_dispatcher) this.processMethod("dispatcher");
	    	}catch(Exception e){}
	    	try{
	    		boolean do_executer=false;
	    		do_executer="true".equals(this.getArguments("do-executer"));
	    		if(do_executer)	this.processMethod("executer");
	    	}catch(Exception e){}
	    }
		return getServiceContext().doNextProcess();
	}
    private boolean fetchFile(String url_string, String param, String inbox_directory)throws xMethodException{
        URL url = null;
        HttpURLConnection connection = null;
        String message=null;
        boolean valid=false;
		Element return_data=setReturnArguments("data", "");
        try {
            DataOutputStream out = null;
            url = new URL(url_string+"?"+param);
            connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("X-XLive-Version", "1.0");
            connection.setRequestProperty("X-XLive-Content", "binary");
            connection.setRequestProperty("X-XLive-ServerCode", xServerConfig.getServerCode());
            out = new DataOutputStream(connection.getOutputStream());
            out.flush();
            out.close();
            out = null;
            InputStream in = new DataInputStream(connection.getInputStream());
            valid="true".equals(connection.getHeaderField("valid"));
            if(valid){
            	int file_count=0;
            	try{
            		file_count=Integer.parseInt(connection.getHeaderField("file-count"));
            	}catch(Exception e){}
            	//
            	for(int i=0; i< file_count;++i){
            		String file_spec=connection.getHeaderField("file-"+i);
            		try {
            			file_spec=URLDecoder.decode(file_spec, "utf-8");
            		} catch (UnsupportedEncodingException e) {
            			e.printStackTrace();
            		}
            		String[] spec=file_spec.split(";");
            		String file_path=(String)spec[0];
            		file_path =file_path.replaceAll("\\\\", "/");
            		ByteArrayOutputStream baos = new ByteArrayOutputStream();
            		long length=Long.parseLong(spec[2]);
            		xUtility.copyStream(in, baos, length);
            		xFile file_output = new xFile(inbox_directory+file_path);
            		file_output.setBytes(baos.toByteArray());
            		//Element return_file=createElement("file",((String)spec[0]).substring(1));
            		Element return_file=createElement("file",file_path);
            		return_file.setAttribute("length", String.valueOf(length));
            		return_data.appendChild(return_file);
            	}
            	if(file_count==0)message="no file found";
            }
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
           	try {if(connection != null) connection.disconnect();}catch(Exception econ){}
        }
        this.setReturnArguments("valid", (valid)? "true":"false");
        this.setReturnArguments("why", message==null ? "" :message);
        return valid;
    }
}
