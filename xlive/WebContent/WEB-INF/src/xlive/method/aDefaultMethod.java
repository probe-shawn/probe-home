package xlive.method;

import org.json.JSONException;
import org.json.JSONObject;

import xlive.*;
public class aDefaultMethod {
	private aServiceContext serviceContext=null;
	private JSONObject serverJSO =null;
	
	public void setup(aServiceContext context, JSONObject server_jso){
		serviceContext=context;
		serverJSO =server_jso;
	}
	public Object process(JSONObject server_jso, String method_name, JSONObject client_jso, JSONObject return_jso) throws aMethodException, JSONException{
		Object result=null;
		return result;
	}
	public aServiceContext getServiceContext(){
		return serviceContext;
	}
	public JSONObject getServerJSO(){
		return serverJSO;
	}
	public void disposal(){
		serviceContext=null;
		serverJSO =null;
	}
	protected Object processWebObjectMethod(String object_path, String method_name) throws aMethodException{
		return aWebInformation.processWebObjectMethod(serviceContext, object_path, method_name);
	}
	protected Object processMethod(String method_name)throws aMethodException{
		return aWebInformation.processWebObjectMethod(serviceContext, serverJSO, method_name);
	}
	/*
	protected boolean processInternetWebObjectMethod(String server_url, String object_url, String method_name) throws xMethodException{
		return xWebInformation.processInternetWebObjectMethod(server_url, object_url, method_name, this);
	}
	*/
	
}
