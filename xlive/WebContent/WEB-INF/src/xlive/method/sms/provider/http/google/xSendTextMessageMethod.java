package xlive.method.sms.provider.http.google;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;

import xlive.xServerConfig;
import xlive.xUtility;
import xlive.xWebInformation;
import xlive.google.ds.xFile;
import xlive.method.*;
import xlive.xml.xXmlDocument;

public class xSendTextMessageMethod extends xDefaultMethod {
	
	private String url;
	private String encoding=null;
	private Element parameters=null;
	private Element smsParameterMap=null;
	
	private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
	private String smsDirectory;
	public Object process()throws xMethodException{
		this.extendDefaultPropertiesToArguments();
		smsDirectory=this.resourceDirectoryConvert(getProperties("sms-store-directory"));
		xFile sms_dir_file = new xFile(smsDirectory);
		if(!sms_dir_file.exists()) sms_dir_file.makeDirs();
		url = getProperties("url");
		encoding=getArguments("encoding");
		parameters=(Element)getArguments("parameters", XPathConstants.NODE);
		if(parameters != null)parameters=(Element)parameters.cloneNode(true);
		smsParameterMap=(Element)getArguments("sms-parameter-map", XPathConstants.NODE);
		if(smsParameterMap != null)smsParameterMap=(Element)smsParameterMap.cloneNode(true);
		//
		String google_task = this.getArguments("google-task");
		String fname = this.getArguments("file-name");
		if("true".equals(google_task) && fname != null){
			if(fname.trim().length()>0){
				xFile xfile = new xFile(fname);
				this.sendMessage(xfile, false);
			}
			return getServiceContext().doNextProcess();
		}
		String start_service=getArguments("start-service");
		if("true".equals(start_service)) return getServiceContext().doNextProcess();
		///
	   	Element arguments_sms_node=(Element)getArguments("sms", XPathConstants.NODE);
	    boolean synchronize = true;
	    try{
	    	synchronize = !"true".equals(getArguments("asyn"));
	    }catch(Exception e){}
	    //
		String comp_code=getArguments("sms.comp-code");
		if(comp_code == null ||comp_code.trim().length() != 4){
			String server_code=xServerConfig.getServerCode();
			String[]splites=server_code.split("@");
			comp_code=splites[0];
		}
     	Date order_date=null;
    	try{
    		order_date=simpleDateFormat.parse(getArguments("sms.order-time"));
    		if(order_date.after(new Date()))synchronize=false;
    	}catch(Exception e){}
    	if(order_date==null) order_date= new Date();
    	//
    	String file_name=((synchronize)? "syn_" : "")+comp_code+"_"+simpleDateFormat.format(order_date)+".xml";
    	xFile xfile = new xFile(smsDirectory+file_name);
    	int count = 1;
    	while(xfile.exists()){
    		file_name=((synchronize)? "syn_" : "")+comp_code+"_"+simpleDateFormat.format(order_date)+"_"+count+".xml";
    		xfile = new xFile(smsDirectory+file_name);
    		++count;
    	}
    	///
    	Element comp_code_element=(Element)getArguments("sms.comp-code", XPathConstants.NODE);
    	if(comp_code_element != null && (comp_code_element.getTextContent()==null||comp_code_element.getTextContent().trim().length()!=4))
    		comp_code_element.setTextContent(comp_code);
    	Element message_id=(Element)getArguments("sms.message-id", XPathConstants.NODE);
    	if(message_id != null && (message_id.getTextContent()==null||message_id.getTextContent().trim().length()==0))
    		message_id.setTextContent(file_name);
    	Element create_time=(Element)getArguments("sms.create-time", XPathConstants.NODE);
    	if(create_time != null && (create_time.getTextContent()==null||create_time.getTextContent().trim().length()==0))
    		create_time.setTextContent(xUtility.formatDate());
    	Element status=(Element)getArguments("sms.status", XPathConstants.NODE);
    	if(status != null) status.setTextContent("1");
    	// cost
    	Element cost=(Element)getArguments("sms.cost", XPathConstants.NODE);
    	if(cost != null) cost.setTextContent("1");
    	///
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			new xXmlDocument().Transform(arguments_sms_node, baos);
			xfile.setBytes(baos.toByteArray());
		}catch(TransformerConfigurationException tcx){
	    	tcx.printStackTrace();
			throw createMethodException("TransformerConfigurationException", tcx.getLocalizedMessage());
	    }catch(TransformerException te){
	    	te.printStackTrace();
			throw createMethodException("TransformerException", te.getLocalizedMessage());
	    }finally{
	    }
	    if(synchronize){
	    	sendMessage(xfile, false);
	    }else {
			Queue queue = QueueFactory.getDefaultQueue();
			String url = "/web/"+this.getObjectPath("/");
			TaskOptions options = TaskOptions.Builder.withUrl(url);
			options=options.param("method", "send-text-message");
			options=options.param("file-name", xfile.getFileName());
			options=options.param("google-task", "true");
			options=options.countdownMillis(order_date.getTime()-System.currentTimeMillis());
			options=options.header("file-path", xfile.getFileName());
		    queue.add(options);
	    }
		setReturnArguments("message-id", file_name);
		return getServiceContext().doNextProcess();
	}
	public static void stopService(){
	}
	private boolean sendMessage(xFile xfile,boolean asynchronized){
		if(xfile == null || !xfile.exists() || xfile.getLength() == 0) return false;
		XPath xp = XPathFactory.newInstance().newXPath();
		int ret_code=-2;
		String ret_msg="";
		String ret_status="";
		String ret_send_time="";
		boolean valid=true;
		String why="";
		try{
			ByteArrayInputStream bais = new ByteArrayInputStream(xfile.getBytes());
			Document xml_doc= new xXmlDocument().createDocument(bais);
			Element root=(Element)xml_doc.getDocumentElement();
			//String phone_number=xp.evaluate(xWebInformation.xPathValidate("./phone-number"), root);
			//String message=xp.evaluate(xWebInformation.xPathValidate("./message"), root);
			//
			StringBuffer result = new StringBuffer();
			StringBuffer param_buf = new StringBuffer();
			if(parameters != null){
				NodeList node_list=parameters.getChildNodes();
				for(int i=0;i<node_list.getLength();i++){
					if(node_list.item(i).getNodeType() != Node.ELEMENT_NODE) continue;
					Element tmp=(Element)node_list.item(i);
					if(tmp.getTextContent().trim().length()>0){
						if(param_buf.length()>0) param_buf.append("&");
						String name=URLEncoder.encode(tmp.getNodeName(),encoding);
						String value=URLEncoder.encode(tmp.getTextContent().trim(),encoding);
						param_buf.append(name).append("=").append(value);
					}
				}
			}
			if(smsParameterMap != null) {
				NodeList node_list=smsParameterMap.getChildNodes();
				for(int i=0;i<node_list.getLength();i++){
					if(node_list.item(i).getNodeType() != Node.ELEMENT_NODE) continue;
					Element tmp=(Element)node_list.item(i);
					if(tmp.getTextContent().trim().length()>0){
						if(param_buf.length()>0) param_buf.append("&");
						String name=URLEncoder.encode(tmp.getNodeName(),encoding);
						String value=xp.evaluate(xWebInformation.xPathValidate("./"+tmp.getTextContent().trim()), root);
						value=URLEncoder.encode(value,encoding);
						param_buf.append(name).append("=").append(value);
	        		}
				}
			}
			valid = postMessage(url, param_buf.toString(),result);
			ret_code=(valid) ? 0 : -2;
			ret_msg=result.toString();
			//
			Element send_time=(Element)xp.evaluate(xWebInformation.xPathValidate("./send-time"), root, XPathConstants.NODE);
			ret_send_time=xUtility.formatDate();
			send_time.setTextContent(ret_send_time);
			Element return_code=(Element)xp.evaluate(xWebInformation.xPathValidate("./return-code"), root, XPathConstants.NODE);
			return_code.setTextContent(String.valueOf(ret_code));
			Element return_message=(Element)xp.evaluate(xWebInformation.xPathValidate("./return-message"), root, XPathConstants.NODE);
			return_message.setTextContent(ret_msg);
			Element status=(Element)xp.evaluate(xWebInformation.xPathValidate("./status"), root, XPathConstants.NODE);
			if(ret_code == 0) ret_status="2";
			else ret_status="-2";
			status.setTextContent(ret_status);
			//
			boolean back_up= !"false".equals(xp.evaluate(xWebInformation.xPathValidate("./back-up"), root));
			if(back_up) {
				xFile bak_sms = new xFile(xfile.getPath()+"/bak/"+xfile.getName());
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				try{
					TransformerFactory.newInstance().newTransformer().transform(new DOMSource(root), new StreamResult(baos));
					bak_sms.setBytes(baos.toByteArray());
					xfile.delete();
				}catch(Exception e){
					e.printStackTrace();
				}finally{
				}
			}else{
				xfile.delete();
			}
		}catch(Exception e){
			valid = false;
			why = e.getLocalizedMessage();
			e.printStackTrace();
			xfile.renameTo(xfile.getPath()+"/error/"+xfile.getName(), true);
			try{
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				xFile log = new xFile(xfile.getPath()+"/log_"+xfile.getName());
				Element[] return_nodes=this.createElements("xlive.return.send-text-message.return");
				return_nodes[return_nodes.length-1].appendChild(this.createElement("valid", "false"));
				return_nodes[return_nodes.length-1].appendChild(this.createElement("why", e.getLocalizedMessage()));
		        TransformerFactory.newInstance().newTransformer().transform(new DOMSource(return_nodes[0]), new StreamResult(baos));
		        log.setBytes(baos.toByteArray());
			}catch(Exception eignor){
				eignor.printStackTrace();
			}finally{
			}
		}
		if(!asynchronized){
			try{
				this.setReturnArguments("valid", String.valueOf(valid));
				this.setReturnArguments("why", why);
				this.setReturnArguments("return-code", String.valueOf(ret_code));
				this.setReturnArguments("return-message", ret_msg);
				this.setReturnArguments("return-status", ret_status);
				this.setReturnArguments("return-send-time", ret_send_time);
				
			}catch(Exception e){
			}
		}
		return valid;
	}
	 private boolean postMessage(String provider_url, String parameters, StringBuffer result){
	    URL url = null;
	    HttpURLConnection con = null;
	    boolean valid = true;
	    try {
	    	DataOutputStream out = null;
	        url = new URL(provider_url);
	        con = (HttpURLConnection)url.openConnection();
	        con.setRequestMethod("POST");
	        con.setDoInput(true);
	        con.setDoOutput(true);
	        con.setUseCaches(false);
	        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
	        out = new DataOutputStream(con.getOutputStream());
	        if(parameters.length() > 0) {
	        	out.writeBytes(parameters);
	        }
	        
	        out.flush();
	        out.close();
	        out = null;
	        DataInputStream in = new DataInputStream(con.getInputStream());
			//String type = con.getContentType().toLowerCase();
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
