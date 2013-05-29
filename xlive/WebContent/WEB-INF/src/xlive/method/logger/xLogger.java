package xlive.method.logger;

import java.util.logging.Level;
import java.util.logging.Logger;
import xlive.xProbeServlet;
import xlive.wrap.xWrapLogger;

public class xLogger {
	private static Object wrapLogger=null;
	private static Logger rootLogger=null;
	
	public xLogger(String xlive_path){
		if(xProbeServlet.isGAE()) rootLogger = java.util.logging.Logger.getLogger("xlive");
		else wrapLogger = new xWrapLogger(xlive_path); 
	}
	public static Logger getLogger(String session_id, String object_name){
		return (wrapLogger == null) ? rootLogger : ((xWrapLogger) wrapLogger).getLogger(session_id, object_name);
	}
	public static Logger getLogger(){
		return (wrapLogger == null) ? rootLogger : ((xWrapLogger) wrapLogger).getLogger();
	}
	public static void log(Level level,String message){
		if(wrapLogger == null) rootLogger.logp(level,"", "", message);
		else ((xWrapLogger) wrapLogger).log(level, message);
	}
	public static void log(Level level, String session_id, String object_name, String method_name, String message, Integer deep){
		if(wrapLogger == null){
			try{
				Object params[] = {deep};
				rootLogger.logp(level,object_name, method_name, message, params);
			}catch(Exception e){
				e.printStackTrace();
			}
		}else{
			((xWrapLogger) wrapLogger).log(level, session_id, object_name, method_name, message, deep);
		}
	}
	public static void setXliveLoggerLevel(Level level){
		if(wrapLogger == null) rootLogger.setLevel(level);
		else ((xWrapLogger) wrapLogger).setXliveLoggerLevel(level);
	}
	public static void setXliveObjectLoggerLevel(Level level){
		if(wrapLogger == null) rootLogger.setLevel(level);
		else ((xWrapLogger) wrapLogger).setXliveObjectLoggerLevel(level);
	}
	public static void setXliveUserLoggerLevel(Level level){
		if(wrapLogger == null) rootLogger.setLevel(level);
		else ((xWrapLogger) wrapLogger).setXliveUserLoggerLevel(level);
	}
	public static boolean isObjctLoggable(String object_name){
		return (wrapLogger == null)? false : ((xWrapLogger) wrapLogger).isObjctLoggable(object_name);
	}
	public static void addObject(String object_name){
		if(wrapLogger != null) ((xWrapLogger) wrapLogger).addObject(object_name);
	}
	public static void removeObject(String object_name){
		if(wrapLogger != null) ((xWrapLogger) wrapLogger).removeObject(object_name);
	}
	public static void removeObjects(){
		if(wrapLogger != null) ((xWrapLogger) wrapLogger).removeObjects();
	}
	public static boolean isUserLoggable(String session_id){
		return (wrapLogger == null)? false : ((xWrapLogger) wrapLogger).isUserLoggable(session_id);
	}
	public static void addUser(String session_id){
		if(wrapLogger != null) ((xWrapLogger) wrapLogger).addUser(session_id);
	}
	public static void removeUser(String session_id){
		if(wrapLogger != null) ((xWrapLogger) wrapLogger).removeUser(session_id);
	}
	public static void removeUsers(){
		if(wrapLogger != null) ((xWrapLogger) wrapLogger).removeUsers();
	}
	public static void dispose(){
		if(wrapLogger != null) ((xWrapLogger) wrapLogger).dispose();
	}
}
