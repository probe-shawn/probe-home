package xlive.wrap;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import xlive.method.logger.xLogFormatter;
import xlive.xml.xXmlDocument;

public class xWrapLogger {
	private Hashtable<String,String> logObjects = new Hashtable<String,String>();
	private Hashtable<String,FileHandler> logUsers = new Hashtable<String,FileHandler>();
	private Logger rootLogger;
	private Logger objectLogger;
	private Logger userLogger;
	private String xlivePath;
	private String rootLoggerFileName;
	private String objectLoggerFileName;
	private Thread updateFileNameThread;
	private Boolean stopThread=false;
	
	public xWrapLogger(String xlive_path){
		xlivePath=xlive_path;
		try{
			rootLogger = java.util.logging.Logger.getLogger("xlive");
			rootLogger.setUseParentHandlers(false);
			ConsoleHandler console = new java.util.logging.ConsoleHandler();
			console.setFormatter(new xLogFormatter());
			console.setLevel(Level.ALL);
			rootLogger.addHandler(console);
			XPath xp = XPathFactory.newInstance().newXPath();
			String log_xml=xlive_path+"WEB-INF"+File.separator+"logger/$.xml";
			Document log_xml_doc= new xXmlDocument().createDocument(log_xml);
			Node root_node=log_xml_doc.getDocumentElement();
			String level=xp.evaluate("./properties/xlive-logger/level", root_node);
			rootLogger.setLevel(Level.parse(level));
			rootLoggerFileName=xp.evaluate("./properties/xlive-logger/handler/file-name", root_node);
			String file_name=xlive_path+"WEB-INF"+File.separator+"logger/"+rootLoggerFileName;
			String date=new SimpleDateFormat("yyyyMMdd").format(new Date());
			file_name=file_name+date+".log";
			FileHandler file_handle = new java.util.logging.FileHandler(file_name, true);
			file_handle.setFormatter(new xLogFormatter());
			rootLogger.addHandler(file_handle);
			file_handle.setLevel(rootLogger.getLevel());
			
			String object_level=xp.evaluate("./properties/xlive-object-logger/level", root_node);
			objectLoggerFileName=xp.evaluate("./properties/xlive-object-logger/handler/file-name", root_node);
			objectLogger=Logger.getLogger("xlive.object");
			objectLogger.setLevel(Level.parse(object_level));
			
			String user_level=xp.evaluate("./properties/xlive-user-logger/level", root_node);
			userLogger=Logger.getLogger("xlive.user");
			userLogger.setLevel(java.util.logging.Level.parse(user_level));
		}catch(Exception e){
			e.printStackTrace();
		}
		updateFileNameThread= new Thread(){
			public void run(){
				while(!stopThread){
					long current=System.currentTimeMillis();
					Calendar cal= Calendar.getInstance();
					cal.add(Calendar.DAY_OF_MONTH, 1);
					cal.set(Calendar.HOUR_OF_DAY, 0);
					cal.set(Calendar.MINUTE, 0);
					cal.set(Calendar.SECOND, 0);
					cal.set(Calendar.MILLISECOND, 10);
					long next=cal.getTimeInMillis();
					next = next-current;
					synchronized(stopThread){
						try{
							stopThread.wait(next);
						}catch(Exception e){
							e.printStackTrace();
						}
					}
					if(!stopThread) {
						updateRootFileName();
						updateObjectFileName();
					}
				}
			}
		};
		updateFileNameThread.start();
	}
	private void stopUpdateFileName(){
		try{
			synchronized(stopThread){
				stopThread=true;
				stopThread.notify();
			}
		}catch(Exception e){}
	}

	public Logger getLogger(String session_id, String object_name){
		try{
			if(logObjects.size()==0 && logUsers.size()==0) return rootLogger;
			if(logUsers.size()!=0 && session_id != null && session_id.length() > 0){
				if(logUsers.get(session_id) != null) return userLogger;
			}
			if(logObjects.size()!=0 && object_name != null && object_name.length() > 0){
				if(logObjects.get(object_name) != null) return objectLogger;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return rootLogger;
	}
	public Logger getLogger(){
		return rootLogger;
	}
	public void log(Level level,String message){
		rootLogger.logp(level,"", "", message);
	}
	public void log(Level level, String session_id, String object_name, String method_name, String message, Integer deep){
		try{
			Logger logger=this.getLogger(session_id, object_name);
			Object params[] = {deep};
			logger.logp(level,object_name, method_name, message, params);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public void setXliveLoggerLevel(Level level){
		rootLogger.setLevel(level);
		Handler[] handlers=rootLogger.getHandlers();
		for(int i=0;i<handlers.length;++i){
			if(handlers[i] instanceof FileHandler) {
				handlers[i].setLevel(level);
			}
			if(handlers[i] instanceof ConsoleHandler) {
				handlers[i].setLevel(Level.ALL);
			}
		}

	}
	public void setXliveObjectLoggerLevel(Level level){
		objectLogger.setLevel(level);
		Handler[] handlers=objectLogger.getHandlers();
		for(int i=0;i<handlers.length;++i){
			if(handlers[i] instanceof FileHandler) {
				handlers[i].setLevel(level);
			}
		}
	}
	public void setXliveUserLoggerLevel(Level level){
		userLogger.setLevel(level);
		Handler[] handlers=userLogger.getHandlers();
		for(int i=0;i<handlers.length;++i){
			if(handlers[i] instanceof FileHandler) {
				handlers[i].setLevel(level);
			}
		}
	}
	public boolean isObjctLoggable(String object_name){
		return (logObjects.get(object_name)!=null);
	}
	public void addObject(String object_name){
		try{
			if(logObjects.size()==0){
				Handler[] handlers=objectLogger.getHandlers();
				boolean found=false;
				for(int i=0;i<handlers.length;++i){
					if(handlers[i] instanceof FileHandler) {
						found=true;
						break;
					}
				}
				if(!found){
					String file_name=xlivePath+"WEB-INF"+File.separator+"logger/"+objectLoggerFileName;
					String date=new SimpleDateFormat("yyyyMMdd").format(new Date());
					file_name=file_name+date+".log";
					FileHandler file_handle = new FileHandler(file_name, true);
					file_handle.setFormatter(new xLogFormatter());
					objectLogger.addHandler(file_handle);
					file_handle.setLevel(objectLogger.getLevel());
				}
			}
			logObjects.put(object_name,"");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public void removeObject(String object_name){
		logObjects.remove(object_name);
		if(logObjects.size()==0){
			Handler[] handlers=objectLogger.getHandlers();
			for(int i=0;i<handlers.length;++i){
				if(handlers[i] instanceof FileHandler) {
					((FileHandler)handlers[i]).close();
					objectLogger.removeHandler(handlers[i]);
				}
			}
		}
	}
	public void removeObjects(){
		logObjects.clear();
		Handler[] handlers=objectLogger.getHandlers();
		for(int i=0;i<handlers.length;++i){
			if(handlers[i] instanceof FileHandler) {
				((FileHandler)handlers[i]).close();
				objectLogger.removeHandler(handlers[i]);
			}
		}
	}
	public boolean isUserLoggable(String session_id){
		return (logUsers.get(session_id)!=null);
	}
	public void addUser(String session_id){
		if(logUsers.get(session_id)!=null) return;
		try{
			String file_name=xlivePath+"WEB-INF"+File.separator+"logger/"+session_id;
			String date=new SimpleDateFormat("yyyyMMdd").format(new Date());
			file_name=file_name+date+".log";
			FileHandler file_handle = new FileHandler(file_name, true);
			file_handle.setFormatter(new xLogFormatter());
			userLogger.addHandler(file_handle);
			file_handle.setLevel(userLogger.getLevel());
			logUsers.put(session_id,file_handle);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public void removeUser(String session_id){
		FileHandler file_handle=logUsers.remove(session_id);
		if(file_handle != null){
			file_handle.close();
			userLogger.removeHandler(file_handle);
		}
	}
	public void removeUsers(){
		Enumeration<String> key=logUsers.keys();
		while(key.hasMoreElements()){
			String id=key.nextElement();
			this.removeUser(id);
		}
		logUsers.clear();
	}
	public void dispose(){
		Handler[] handlers=rootLogger.getHandlers();
		for(int i=0;i<handlers.length;++i){
			if(handlers[i] instanceof FileHandler) {
				((FileHandler)handlers[i]).close();
				rootLogger.removeHandler(handlers[i]);
			}
		}
		removeObjects();
		removeUsers();
		stopUpdateFileName();
	}
	private void updateRootFileName(){
		Handler[] handlers=rootLogger.getHandlers();
		for(int i=0;i<handlers.length;++i){
			if(handlers[i] instanceof FileHandler){
				try{
					String file_name=xlivePath+"WEB-INF"+File.separator+"logger/"+rootLoggerFileName;
					String date=new SimpleDateFormat("yyyyMMdd").format(new Date());
					file_name=file_name+date+".log";
					((FileHandler)handlers[i]).close();
					rootLogger.removeHandler(handlers[i]);
					FileHandler file_handle = new FileHandler(file_name, true);
					file_handle.setFormatter(new xLogFormatter());
					rootLogger.addHandler(file_handle);
					file_handle.setLevel(rootLogger.getLevel());
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
	}
	private void updateObjectFileName(){
		Handler[] handlers=objectLogger.getHandlers();
		for(int i=0;i<handlers.length;++i){
			if(handlers[i] instanceof FileHandler){
				try{
					String file_name=xlivePath+"WEB-INF"+File.separator+"logger/"+objectLoggerFileName;
					String date=new SimpleDateFormat("yyyyMMdd").format(new Date());
					file_name=file_name+date+".log";
					((FileHandler)handlers[i]).close();
					objectLogger.removeHandler(handlers[i]);
					FileHandler file_handle = new FileHandler(file_name, true);
					file_handle.setFormatter(new xLogFormatter());
					objectLogger.addHandler(file_handle);
					file_handle.setLevel(objectLogger.getLevel());
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
	}
}
