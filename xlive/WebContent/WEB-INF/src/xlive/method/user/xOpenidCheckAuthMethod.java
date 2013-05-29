package xlive.method.user;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;

import javax.xml.xpath.XPathConstants;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import xlive.method.*;

public class xOpenidCheckAuthMethod extends xDefaultMethod{

	public Object process() throws xMethodException{
		this.getServiceContext().dumpArguments();
		Element openid=(Element) this.getArguments("openid", XPathConstants.NODE);
		Element valid = this.setReturnArguments("valid", "true");
		Node return_node = valid.getParentNode();
		return_node.appendChild(openid.cloneNode(true));
		//
		Properties header = new Properties();
		header.put("Authorization", "OAuth");
		//
		
		return getServiceContext().doNextProcess();
	}
	private boolean postAuth(String url_string, Properties header, String parameters, StringBuffer result){
		    URL url = null;
		    HttpURLConnection con = null;
		    boolean valid = true;
		    try {
		    	DataOutputStream out = null;
		        url = new URL(url_string);
		        con = (HttpURLConnection)url.openConnection();
		        con.setRequestMethod("POST");
		        con.setDoInput(true);
		        con.setDoOutput(true);
		        con.setUseCaches(false);
		        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		        Enumeration<?> en = header.propertyNames();
		        while(en.hasMoreElements()){
		        	String key = (String)en.nextElement();
		        	String value = header.getProperty(key);
		        	con.setRequestProperty(key, value);
		        }
		        out = new DataOutputStream(con.getOutputStream());
		        if(parameters.length() > 0) out.writeBytes(parameters);
		        out.flush();
		        out.close();
		        out = null;
		        DataInputStream in = new DataInputStream(con.getInputStream());
				ByteArrayOutputStream baos = new ByteArrayOutputStream(4096);
		        try {
		            byte[] by = new byte[2048];
		            int got = 0;
		            while((got = in.read(by, 0, 2048)) != -1) baos.write(by, 0, got);
		        }catch(IOException e) {
		           	valid = false;
		           	result.append(e.getMessage());
		        }
		        in.close();
		        String charset = con.getContentEncoding();
		        con.disconnect();
		        con = null;
		        if(valid) result.append((charset == null) ? new String(baos.toByteArray()) : new String(baos.toByteArray(), charset));
		        return valid;
		    }catch(SocketException e){
		        valid = false;
		        result.append("SocketException: "+e.getMessage());
		    }catch(MalformedURLException mle) {
		    	valid = false;
		    	result.append("MalformedURLException: "+mle.getMessage());
	        }catch(IOException ioe) {
	        	valid = false;
	        	result.append("IOException: "+ioe.getMessage());
	        }catch(Exception ex1) {
	        	valid = false;
	        	result.append("Exception: "+ex1.getMessage());
		    }finally{
		    	try {if(con != null) con.disconnect();}catch(Exception econ){}
		    }
		    return valid;
	     }

}
