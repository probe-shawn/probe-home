package xlive;

import java.util.logging.Level;

import xlive.method.logger.xLogger;


@SuppressWarnings("serial")
public class aSystemException extends Exception {
	
	private String sessionId=null;
	private String causeSite=null;
	
	public aSystemException(String session_id, String cause_site, String message, Throwable cause) {
	    super("["+session_id+"]["+cause_site+"] : "+message, cause);
	    sessionId=session_id;
	    causeSite=cause_site;
	}
	public aSystemException(String session_id, String cause_site, String message) {
	    super("["+session_id+"]["+cause_site+"] : "+message);
	    sessionId=session_id;
	    causeSite=cause_site;
	}
	public String getSessionId(){
    	return sessionId;
    }
    public String getCauseSite(){
    	return causeSite;
    }
	public static void logSystemException(aSystemException system_exception){
		xLogger.log(Level.SEVERE, system_exception.getSessionId(), "aSystemException", system_exception.getCauseSite(), system_exception.getMessage(), 0);
	}
}
