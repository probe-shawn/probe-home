package xlive.method.sms.provider.rxtx;


import xlive.xWebInformation;
import xlive.method.*;

import gnu.io.*;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import xlive.method.sms.*;

public class xSendTextServiceMethod extends xDefaultMethod implements Runnable{
	private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
	private static boolean stopThread=false;
	private static boolean startService=false;
	private static LinkedList<shortTextMessage> smsQueue=null;;
	private File smsDirectory;

	public Object process()throws xMethodException {
		smsDirectory=directoryResolve(getProperties("sms-store-directory"));
		if(!smsDirectory.exists()) smsDirectory.mkdirs();
		if(smsQueue != null) return getServiceContext().doNextProcess();
		smsQueue=new LinkedList<shortTextMessage>();
		readSMSFile(smsDirectory);
		if(!startService){
			startService=true;
			new Thread(this).start();
		}
		/*
		try{
			processMethod("create-connection");
			String port_name=getArguments("create-connection.port-name");
			port_name=(port_name==null||port_name.trim().length()==0)? "COM1":port_name;
			SerialPort serial_port=(SerialPort)getPropertiesObject("connection-instance", port_name);
			if(serial_port != null){
				String phone_number=getMethodArguments("sms.phone-number");
				String message=getMethodArguments("sms.message");
				int result=sendTextMessage(phone_number, message, serial_port);
				setReturnArguments("success-count", String.valueOf(result));
			}
		}finally{
			processMethod("close-connection");
		}
		*/
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
	public static shortTextMessage addShortMessage(File file, long time){
		shortTextMessage sms = new shortTextMessage(file, time);
		synchronized(smsQueue){
			int index=0;
			for(;index<smsQueue.size();++index){
				if(smsQueue.get(index).getOrderTime()>time) {
					smsQueue.add(index, sms);
					break;
				}
			}
			if(index >= smsQueue.size()) smsQueue.addLast(sms);
		}
		return sms;
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
				shortTextMessage sms = null;
				SerialPort serial_port=null;
				synchronized(smsQueue){
					while(true){
						long wait_time=0;
						if(smsQueue.isEmpty()) {
							if(serial_port != null) processMethod("create-connection");
							serial_port=null;
							smsQueue.wait();
						}
						sms = smsQueue.getFirst();
						if(sms != null &&(wait_time=sms.getOrderTime() - System.currentTimeMillis()) <= 0){
							if(serial_port == null) {
								processMethod("create-connection");
								String port_name=getQNameArguments("create-connection.port-name");
								port_name=(port_name==null||port_name.trim().length()==0)? "COM1":port_name;
								serial_port=(SerialPort)getPropertiesObject("connection-instance");
							}
							if(serial_port != null) {
								sms=smsQueue.removeFirst();
								break;
							}else{
								sms=null;
								wait_time=30000;
							}
						}
						if(wait_time>0){
							if(serial_port != null) processMethod("create-connection");
							serial_port=null;
							smsQueue.wait(wait_time);
						}
						if(stopThread||xWebInformation.serviceStop())break;
					}
				}
				//if(sms != null)	xSendTextMessageMethod.sendMessage(sms.getFile(), smsConnection, true);
				if(stopThread||xWebInformation.serviceStop())break;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	
	
}
