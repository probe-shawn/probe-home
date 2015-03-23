package xlive;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;
import java.util.Hashtable;
import java.util.logging.Level;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import xlive.method.*;
import xlive.method.logger.xLogger;

public class aWebInformation implements xContextObjectImp {
	
	private final String Tag = "aWebInformation";
	private static ServletContext servletContext=null;
	
	private static String webDirectory=null; //xlive
	private static Hashtable<String, JSONObject> objectTable = new Hashtable<String, JSONObject>();
	//
	private static boolean systemInitialized=false;
	private static boolean serviceStop=false;
	//
	public aWebInformation(ServletContext servlet_context) {
		servletContext = servlet_context;
		servletContext.setAttribute(Tag,this);
		initial();
    }
	public boolean initial(){
		new xlive.method.logger.xLogger(servletContext.getRealPath("/"));
		xLogger.log(Level.INFO, "aWebInformation.initialize start ...");
		//ssl trust
		if(!aProbeServlet.isGAE()) xlive.ssl.NaiveTrustProvider.setAlwaysTrust(true);
		//
		systemInitialized=false;
		webDirectory = servletContext.getRealPath("/");
		webDirectory +=(webDirectory.endsWith(File.separator) ? "":File.separator);
		systemInitialized=true;
		xLogger.log(Level.INFO, "aWebInformation.initialized ... done"+"("+xServerConfig.getServerCode()+")");
		System.out.println("aWebInformation.initialized ... done"+"("+xServerConfig.getServerCode()+")");
		return true; 
	}
	public static String getWebDirectory(){
		return webDirectory;
	}
	public static boolean getSystemInitialized(){
		return systemInitialized;
	}
	public static boolean serviceStop(){
		return serviceStop;
	}
	public void destroy(){
		serviceStop=true;
		xLogger.dispose();
		objectTable.clear();
		servletContext=null;
		systemInitialized=false;
	}
	public String getDescription(){
		return "aWebInformation";
	}
	public static boolean dispatch(aServiceContext service_context, String object_path) throws aSystemException{
		xLogger.log(Level.FINE, service_context.getSessionId(), "aWebInformation", "dispatch", "aWebInformation.dispatch : "+object_path, 0);
		//
		if(object_path==null) {
			service_context.doNextProcess(false);
			aMethodException method_exception=new aMethodException(null, null, "dispatch", "object path is null");
			service_context.logMethodException(method_exception);
			throw new aSystemException(service_context.getSessionId(), "dispatch", "methodException : "+method_exception.getMessage());
		}
		//path="/web-inf"+path;
		if(service_context.doNextProcess()){
			JSONObject json=aWebInformation.getJSONObject(object_path);
			if(json != null) {
				JSONArray methods = service_context.getClientJSO().optJSONArray("method");
				if(methods == null){
					String method = service_context.getClientJSO().optString("method",null);
					methods = new JSONArray();
					if(method != null) methods.put(method);
				}
				/*
				String method = service_context.getClientJSO().optString("method",null);
				if(method != null) methods.put(method);
				else methods = service_context.getClientJSO().optJSONArray("method");
				*/
				if(methods == null || methods.length() == 0) throw new aSystemException(service_context.getSessionId(), "dispatch", "no method in the request");
				for(int i = 0; i < methods.length(); ++i){
					String method_name = methods.optString(i);
					try{
						aWebInformation.processMethod(service_context, json, method_name);
					}catch(aMethodException me){
						me.printStackTrace();
						service_context.logMethodException(me);
						service_context.doNextProcess(false);
						throw new aSystemException(service_context.getSessionId(),"dispatch", "methodException :"+me.getMessage());
					}
					if(!service_context.doNextProcess()) break;
				}
			}else{
				service_context.doNextProcess(false);
				aMethodException method_exception=new aMethodException(null, null, "dispatch", "object ("+object_path+") not found");
				service_context.logMethodException(method_exception);
				throw new aSystemException(service_context.getSessionId(), "dispatch", "methodException :"+method_exception.getMessage());
			}
		}
		return service_context.doNextProcess(); 
	}
	public static JSONObject getJSONObject(String object_path){
		StringBuffer buf = new StringBuffer(object_path);
		for(int i=0,n=buf.length(); i < n; ++i){
			if(buf.charAt(i)=='.') buf.setCharAt(i, '/');
		}
		String name=buf.toString();
		//boolean is_$ = false;
		JSONObject json = aWebInformation.objectTable.get(name);
		if(json != null) return json;
		String file_name = "/WEB-INF"+(name.startsWith("/")?"":"/")+name+".json";
		InputStream json_stream=servletContext.getResourceAsStream(file_name);
		if(json_stream == null){
			file_name = "/WEB-INF"+(name.startsWith("/")?"":"/")+name+"/$.json";
			json_stream=servletContext.getResourceAsStream(file_name);
			//is_$ = true;
		}
		if(json_stream == null) return null;
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			xUtility.copyStream(json_stream, baos);
			json = new JSONObject(baos.toString());
			aWebInformation.objectTable.put(name, json);
			return json;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	private static String getMethodClassName(JSONObject json, String method_name){
		JSONObject methods = json.optJSONObject("methods");
		if(methods != null){
			JSONObject method = methods.optJSONObject(method_name);
			if(method != null) return method.optString("use_class",null);
		}
		return null;
	}
	private static Object processMethod(aServiceContext service_context, JSONObject server_jso, String method_name) throws aMethodException{
		String class_name = aWebInformation.getMethodClassName(server_jso, method_name);
		if(class_name == null || class_name.trim().length() == 0) 
			throw new aMethodException("", "","processMethod", method_name+" : or class name not found ");
		try{
			Object result=null;
			if(class_name == null || class_name.trim().length() == 0) class_name="xlive.method.aDefaultMethod";
			aDefaultMethod method_class = (aDefaultMethod)Class.forName(class_name.trim()).newInstance();
			method_class.setup(service_context, server_jso);
			result=method_class.process(server_jso, method_name, service_context.getClientJSO(), service_context.getReturnJSO());
			method_class.disposal();
			//
			return result;
		}catch(IllegalAccessException iac){
			iac.printStackTrace();
			throw new aMethodException("", "","processMethod", "IllegalAccessException("+class_name+") "+iac.getLocalizedMessage());
		}catch(ClassNotFoundException cnf){
			cnf.printStackTrace();
			throw new aMethodException("", "","processMethod", "ClassNotFoundException("+class_name+") "+ cnf.getLocalizedMessage());
		}catch(InstantiationException ine){
			ine.printStackTrace();
			throw new aMethodException("","","processMethod", "InstantiationException("+class_name+") "+ine.getLocalizedMessage());
		} catch (JSONException e) {
			e.printStackTrace();
			throw new aMethodException("","","processMethod", "JSONException ("+class_name+") "+e.getLocalizedMessage());
		}
	}
	/******************/ 
	/* service public */
	public static Object processWebObjectMethod(aServiceContext service_context,String object_path, String method_name) throws aMethodException{
		JSONObject server_jso = getJSONObject(object_path);
		if(server_jso != null) return aWebInformation.processMethod(service_context, server_jso,method_name);
		return null;
	}
	public static Object processWebObjectMethod(aServiceContext service_context,JSONObject server_jso, String method_name) throws aMethodException{
		return aWebInformation.processMethod(service_context, server_jso,method_name);
	}
	public static boolean isWebObjectExisted(String object_path) throws aMethodException{
		return getJSONObject(object_path) != null;
	}
	
	
	public static boolean processInternetWebObjectMethod(String server_url,String object_url, String method_name,aDefaultMethod default_method) throws aMethodException, JSONException{
		JSONObject json = new JSONObject(default_method.getServiceContext().getClientJSO().toString());
		json.put("method",method_name);
		json.put("_return_", new JSONObject(default_method.getServiceContext().getReturnJSO().toString()));
		//
		String target_object_url=object_url.replaceAll("\\.","/");
	    boolean valid=true;
	    String connection_url="http://"+server_url+"/xlive/jso/"+target_object_url;
        URL url = null;
        HttpURLConnection connection = null;
        String why="";
        try {
            url = new URL(connection_url);
            connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setRequestProperty("Content-Type", "multipart/form-data");
            connection.setRequestProperty("X-XLive-Version", "1.0");
            connection.setRequestProperty("X-Content", "json");
            connection.setRequestProperty("xsessionid", "8788dy");
            connection.setRequestProperty("X-XLive-ServerCode", aServerConfig.getServerCode());
            OutputStreamWriter output_writer = new OutputStreamWriter(connection.getOutputStream(),"UTF-8");
            json.write(output_writer);
            output_writer.close();
            output_writer = null;
            JSONObject reponse_jso = new JSONObject(xUtility.streamToString(connection.getInputStream(),"UTF-8"));
            JSONObject return_jso = default_method.getServiceContext().getReturnJSO();
            for(String key : JSONObject.getNames(reponse_jso)) {
            	return_jso.put(key, reponse_jso.get(key));
            }
         }catch(SocketException e){
        	e.printStackTrace();
        	valid=false;
        	why="SocketException :"+e.getLocalizedMessage();
        }catch(MalformedURLException e) {
        	e.printStackTrace();
        	valid=false;
        	why="MalformedURLException :"+e.getLocalizedMessage();
        }catch(IOException e) {
        	e.printStackTrace();
        	valid=false;
        	why="IOException :"+e.getLocalizedMessage();
    	}catch(Exception e) {
    		e.printStackTrace();
    		valid=false;
    		why="Exception :"+e.getLocalizedMessage();
        }finally {
           	try {if(connection != null) connection.disconnect();}catch(Exception econ){}
        }
        
        JSONObject return_jso = default_method.getServiceContext().getReturnJSO();
        JSONObject method_response = new JSONObject();
        method_response.put("valid", valid);
        method_response.put("why", why);
        return_jso.put(method_name, method_response);
        return_jso.put("valid", valid);
        return_jso.put("why", why);
		return valid;
	}
	
	public static void responseNotReadyMessage(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException, JSONException{
		JSONObject msg = new JSONObject("{'system':'System is not ready'}");
		OutputStreamWriter writer = new OutputStreamWriter(response.getOutputStream(),"UTF-8");
		response.setContentType("text/xml");
		response.setHeader("Cache-Control", "no-cache");
		try {
			msg.write(writer);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		writer.flush();
		writer.close();
	}
	public static void responseDisconnectMessage(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException, JSONException{
		JSONObject msg = new JSONObject("{'system':'dispose'}");
		OutputStreamWriter writer = new OutputStreamWriter(response.getOutputStream(),"UTF-8");
		response.setContentType("text/xml");
		response.setHeader("Cache-Control", "no-cache");
		try {
			msg.write(writer);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		writer.flush();
		writer.close();
	}
}
