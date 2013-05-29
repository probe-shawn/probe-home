package xlive.method;

@SuppressWarnings("serial")
public class xMethodException extends Exception {
	
	private String objectPath=null;
	private String methodName=null;
	private String causeSite=null;
	private String errorCode=null;
	private String errorState=null;
	
    public xMethodException(String object_path, String method_name, String cause_site, String message, Throwable cause) {
    	super("["+object_path+"."+method_name+"]["+cause_site+"] : "+message, cause);
    	objectPath=object_path;
    	methodName=method_name;
    	causeSite=cause_site;
    }
    public xMethodException(String object_path, String method_name, String cause_site, String message, String error_code, String error_state, Throwable cause) {
    	super("["+object_path+"."+method_name+"]["+cause_site+"] : "+message, cause);
    	objectPath=object_path;
    	methodName=method_name;
    	causeSite=cause_site;
    	errorCode=error_code;
    	errorState=error_state;
    }
    public xMethodException(String object_path, String method_name, String cause_site, String message) {
    	super("["+object_path+"."+method_name+"]["+cause_site+"] : "+message);
    	objectPath=object_path;
    	methodName=method_name;
    	causeSite=cause_site;
    }
    public xMethodException(String object_path, String method_name, String cause_site, String message, String error_code, String error_state) {
    	super("["+object_path+"."+method_name+"]["+cause_site+"] : "+message);
    	objectPath=object_path;
    	methodName=method_name;
    	causeSite=cause_site;
    	errorCode=error_code;
    	errorState=error_state;
    }
    public void setObjectPath(String object_path){
    	objectPath=object_path;
    }
    public String getObjectPath(){
    	return objectPath;
    }
    public void setMethodName(String method_name){
    	methodName=method_name;
    }
    public String getMethodName(){
    	return methodName;
    }
    public void setCauseSite(String cause_site){
    	causeSite=cause_site;
    }
    public String getCauseSite(){
    	return causeSite;
    }
    public void setErrorCode(String error_code){
    	errorCode=error_code;
    }
    public String getErrorCode(){
    	return errorCode;
    }
    public void setErrorState(String error_state){
    	errorState=error_state;
    }
    public String getErrorState(){
    	return errorState;
    }
    
}
