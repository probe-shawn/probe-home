package xlive.method.sms.provider.cht;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

import xlive.xServerConfig;
import xlive.xUtility;
import xlive.xWebInformation;
import xlive.method.*;
import xlive.xml.xXmlDocument;

public class xSendTextMessageMethod extends xDefaultMethod implements Runnable{
	
	private static LinkedList<shortMessage> smsQueue=null;;
	private static boolean stopThread=false;
	private static boolean startService=false;
	private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
	private File smsDirectory;
	private static int waitTimeout=5000;
	private static int maximumConnection=5;
	private static Integer connectionControl=new Integer(0);
	private static String gatewayIp="";
	private static int gatewayPort=8000;
	private static String userId="";
	private static String userPassword="";

	public Object process()throws xMethodException{
		smsDirectory=directoryResolve(getProperties("sms-store-directory"));
		if(!smsDirectory.exists()) smsDirectory.mkdirs();
		if(smsQueue == null){
			smsQueue=new LinkedList<shortMessage>();
			readSMSFile(smsDirectory);
			try{
				waitTimeout=Integer.parseInt(getProperties("connection-information.wait-timeout"));
			}catch(Exception e){}
			try{
				maximumConnection=Integer.parseInt(getProperties("connection-information.maximum-connection"));
			}catch(Exception e){}
			maximumConnection=maximumConnection-1;
			gatewayIp=getProperties("connection-information.gateway-ip");
			try{
				gatewayPort=Integer.parseInt(getProperties("connection-information.gateway-port"));
			}catch(Exception e){}
			userId=getProperties("connection-information.user-id");
			userPassword=getProperties("connection-information.user-password");
			if(!startService){
				startService=true;
				new Thread(this).start();
			}
			String start_service=getArguments("start-service");
			if("true".equals(start_service)) return getServiceContext().doNextProcess();
		}
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
    	File file = new File(smsDirectory, file_name);
    	int count = 1;
    	while(file.exists()){
    		file_name=((synchronize)? "syn_" : "")+comp_code+"_"+simpleDateFormat.format(order_date)+"_"+count+".xml";
    		file = new File(smsDirectory, file_name);
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
    	FileOutputStream sms_output=null;
		try {
			sms_output= new FileOutputStream(file);
			new xXmlDocument().Transform(arguments_sms_node, sms_output);
			sms_output.close();
			sms_output=null;
		}catch(IOException ioe){
			ioe.printStackTrace();
			throw createMethodException("IOException", ioe.getLocalizedMessage());
		}catch(TransformerConfigurationException tcx){
	    	tcx.printStackTrace();
			throw createMethodException("TransformerConfigurationException", tcx.getLocalizedMessage());
	    }catch(TransformerException te){
	    	te.printStackTrace();
			throw createMethodException("TransformerException", te.getLocalizedMessage());
	    }finally{
	    	try{if(sms_output!=null)sms_output.close();}catch(Exception e){}
	    }
	    if(synchronize){
	    	sms2 sms_conection = getConnection(false);
	    	if(sms_conection != null) sendMessage(file, sms_conection, false);
	    	else{
				File error_dir = new File(file.getParent(), "error");
				if(!error_dir.exists()) error_dir.mkdirs();
				File error_sms= new File(error_dir, file.getName());
				xUtility.moveFile(file, error_sms);
				FileOutputStream output=null;
				try{
					output = new FileOutputStream(new File(error_dir, "log_"+file.getName()));
					this.getServiceContext().responseToOutputStream(output);
				}catch(Exception eignor){
					eignor.printStackTrace();
				}finally{
					try{if(output != null) output.close();}catch(Exception eall){}
				}
	    	}
	    	closeConnection(sms_conection, false);
	    }else {
	    	addShortMessage(file, order_date.getTime());
	    	if(startService) {
	    		synchronized(smsQueue){
	    			smsQueue.notify();
	    		}
	    	}
	    }
		setReturnArguments("message-id", file_name);
		return getServiceContext().doNextProcess();
	}
	public void cleanUp(){
		if(stopThread) super.cleanUp();
	}
	private void readSMSFile(File sms_dir) throws xMethodException{
	    String sms_regex=getProperties("sms-file-regex");
		Pattern pattern = Pattern.compile(sms_regex); 
		File[] files = sms_dir.listFiles();
		for(int i = 0; i<files.length; ++i){
			if(files[i].isDirectory()) continue;
			try{
				String name = files[i].getName();
				Matcher matcher=pattern.matcher(name);
				if(matcher.matches() && matcher.groupCount() > 0){
					String date_string=matcher.group(1);
					Date date = simpleDateFormat.parse(date_string);
					addShortMessage(files[i], date.getTime());
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	private shortMessage addShortMessage(File file, long time){
		shortMessage sms = new shortMessage(file, time);
		synchronized(smsQueue){
			int index=0;
			for(;index<smsQueue.size();++index){
				if(smsQueue.get(index).orderTime>time) {
					smsQueue.add(index, sms);
					break;
				}
			}
			if(index >= smsQueue.size()) smsQueue.addLast(sms);
		}
		return sms;
	}
	class shortMessage{
		File file;
		long orderTime;
		shortMessage(File file, long order_time){
			this.file=file;
			this.orderTime=order_time;
		}
	}
	public static void stopService(){
		stopThread=true;
		notifyService();
	}
	public static void notifyService(){
		try{
			synchronized(smsQueue){
				smsQueue.notify();
			}
		}catch(Exception e){}
	}
	public void run(){
		try{
			while(!stopThread){
				shortMessage sms = null;
				sms2 smsConnection=null;
				synchronized(smsQueue){
					while(true){
						long wait_time=0;
						if(smsQueue.isEmpty()) {
							if(smsConnection != null)closeConnection(smsConnection, true);
							smsConnection=null;
							smsQueue.wait();
						}
						sms = smsQueue.getFirst();
						if(sms != null &&(wait_time=sms.orderTime - System.currentTimeMillis()) <= 0){
							if(smsConnection == null) smsConnection = getConnection(true);
							if(smsConnection != null) {
								sms=smsQueue.removeFirst();
								break;
							}else{
								sms=null;
								wait_time=30000;
							}
						}
						if(wait_time>0){
							if(smsConnection != null) closeConnection(smsConnection, true);
							smsConnection=null;
							smsQueue.wait(wait_time);
						}
						if(stopThread||xWebInformation.serviceStop())break;
					}
				}
				if(sms != null)	sendMessage(sms.file, smsConnection, true);
				if(stopThread||xWebInformation.serviceStop())break;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	private void sendMessage(File file, sms2 smsConnection,boolean asynchronized){
		XPath xp = XPathFactory.newInstance().newXPath();
		int ret_code=-2;
		String ret_msg="";
		String ret_status="";
		String ret_send_time="";
		boolean valid=true;
		String why="";
		try{
			Document xml_doc= new xXmlDocument().createDocument(file);
			Element root=(Element)xml_doc.getDocumentElement();
			String phone_number=xp.evaluate(xWebInformation.xPathValidate("./phone-number"), root);
			String message=xp.evaluate(xWebInformation.xPathValidate("./message"), root);
			//
			ret_code=smsConnection.send_text_message(phone_number, message);
			//
			ret_msg=smsConnection.get_message();
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
				File bak_dir = new File(file.getParent(), "bak");
				if(!bak_dir.exists()) bak_dir.mkdirs();
				File bak_sms= new File(bak_dir,file.getName());
				FileOutputStream output=null;
				try{
					output = new FileOutputStream(bak_sms);
					TransformerFactory.newInstance().newTransformer().transform(new DOMSource(root), new StreamResult(output));
					file.delete();
				}catch(Exception e){
					e.printStackTrace();
				}finally{
					try{if(output != null) output.close();}catch(Exception e){}
				}
			}else{
				file.delete();
			}
		}catch(Exception e){
			valid = false;
			why = e.getLocalizedMessage();
			e.printStackTrace();
			File error_dir = new File(file.getParent(), "error");
			if(!error_dir.exists()) error_dir.mkdirs();
			File error_sms= new File(error_dir, file.getName());
			xUtility.moveFile(file, error_sms);
			FileOutputStream output=null;
			try{
				output = new FileOutputStream(new File(error_dir, "log_"+file.getName()));
				Element[] return_nodes=this.createElements("xlive.return.send-text-message.return");
				return_nodes[return_nodes.length-1].appendChild(this.createElement("valid", "false"));
				return_nodes[return_nodes.length-1].appendChild(this.createElement("why", e.getLocalizedMessage()));
		        TransformerFactory.newInstance().newTransformer().transform(new DOMSource(return_nodes[0]), new StreamResult(output));
			}catch(Exception eignor){
				eignor.printStackTrace();
			}finally{
				try{if(output != null) output.close();}catch(Exception eall){}
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
	}
	private sms2 getConnection(boolean asynchronized) throws xMethodException{
		boolean valid=true;
		String why="";
		int return_code=0;
		String return_message="";
		try{
			if(!asynchronized) {
				synchronized(connectionControl){
					try{
						if(connectionControl>=maximumConnection) connectionControl.wait(waitTimeout);
						++connectionControl;
					}catch(InterruptedException e){
						return null;
					}
				}
			}
			sms2 cht_sms = new sms2();
			return_code = cht_sms.create_conn(gatewayIp, gatewayPort, userId, userPassword);
			return_message=cht_sms.get_message();
			if(return_code == 0) return cht_sms;
			valid = false;
			why="return-code : "+String.valueOf(return_code)+"\n return-message : "+return_message;
		}catch(Exception e){
			e.printStackTrace();
			valid = false;
			why=e.getLocalizedMessage();
		}
		if(!asynchronized){
			this.setReturnArguments("valid", String.valueOf(valid));
			this.setReturnArguments("why", why);
			this.setReturnArguments("return-code", String.valueOf(return_code));
			this.setReturnArguments("return-message", return_message);
		}else{
			//output fatal error 
		}
		return null;
	}
	private void closeConnection(sms2 sms_connection, boolean asynchronized) throws xMethodException{
		if(sms_connection != null) sms_connection.close_conn();
		if(asynchronized) return;
		try{
			synchronized(connectionControl){
				--connectionControl;
				connectionControl.notify();
			}
		}catch(IllegalMonitorStateException ims){
		}
	}


}
