package xlive;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import xlive.method.*;
import xlive.method.logger.xLogger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.*;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.*;
import java.util.logging.Level;


public class aServiceContext {
	private boolean doNextProcess=true;
	//
	private HttpServletRequest request=null; 
	private HttpServletResponse response=null;
	private String sessionId = "";
	private JSONObject clientJSO;
	private JSONObject returnJSO = new JSONObject();
	//private String serverCode=null;
	private aMethodException methodException=null;
	private Integer logDeep=0;
	private boolean customizeResponse=false;
	//
	public boolean doNextProcess(){
		return doNextProcess;
	}
	public boolean doNextProcess(boolean stop){
		return (doNextProcess &=stop);
	}
	public aServiceContext(JSONObject client) throws aSystemException{
		clientJSO = client;
	}
	public aServiceContext(HttpServletRequest http_request, HttpServletResponse http_response) throws aSystemException{
		httpRequestInformation(http_request, http_response);
	}
	private String generateSessionId(){
		org.apache.commons.codec.binary.Base64 base64 = new org.apache.commons.codec.binary.Base64();
		return new String(base64.encode(java.util.UUID.randomUUID().toString().getBytes()));
	}
	public String getSessionId(){
		return this.sessionId;
	}
	public HttpServletRequest getHttpServletRequest(){
		return request;
	}
	public HttpServletResponse getHttpServletResponse(){
		return response;
	}
	public HttpServletResponse customizeResponse(){
		customizeResponse=true;
		return response;
	}
	public boolean isURL(String url){
        try {
            InetAddress ia = InetAddress.getLocalHost();
            String ip = ia.getHostAddress();
            if(ip.startsWith(url)) return true;
	      	Enumeration<NetworkInterface> n = NetworkInterface.getNetworkInterfaces();
	        for (; n.hasMoreElements();) {
              	NetworkInterface e = (NetworkInterface)n.nextElement();
                Enumeration<InetAddress> a = e.getInetAddresses();
                for (; a.hasMoreElements();){
                    InetAddress addr = (InetAddress)a.nextElement();
                    ip = addr.getHostAddress();
                    if(ip.startsWith(url)) return true;
                }
	          }
          }catch(Exception e){}
		return false;
	}
	
	private void httpRequestInformation(HttpServletRequest http_request, HttpServletResponse http_response) throws aSystemException {
		request=http_request;
		response=http_response;
		//serverCode=(String)request.getHeader("X-ServerCode");
		//
		String header_sessionid=(String)request.getHeader("xsessionid");
		String cookie_sessionid=null;
		if(header_sessionid==null){
			Cookie[] cookies=request.getCookies();
			for(int i=0;cookies!=null && i<cookies.length; ++i){
				if("xsessionid".equalsIgnoreCase(cookies[i].getName())){
					cookie_sessionid=cookies[i].getValue();
					break;
				}
			}
		}
		String xsession_id=(cookie_sessionid==null) ? header_sessionid : cookie_sessionid;
		xsession_id = (xsession_id==null||xsession_id.trim().length()==0) ? this.generateSessionId() : xsession_id;
		xLogger.log(Level.FINE, xsession_id, "aServiceContext", "contextSessionControl", "SessionId :"+xsession_id, 0);
		Cookie cookie=new Cookie("xsessionid", xsession_id);
		cookie.setPath("/");
		cookie.setMaxAge(-1);
		if(response != null)response.addCookie(cookie);
		if(response != null)response.addHeader("xsessionid", xsession_id);
		//
		try{
			if(request.getContentLength() > 0) clientJSO = new JSONObject(xUtility.streamToString(request.getInputStream(),"UTF-8"));
		}catch(Exception e){
			e.printStackTrace();
			throw new aSystemException(getSessionId(), "httpRequestInformation[parse request json]", "exception : "+e.getLocalizedMessage());
		}
		//
		boolean is_get="get".equalsIgnoreCase(request.getMethod());
		if(!is_get){
			String encode=request.getCharacterEncoding();
			if(encode == null) encode = System.getProperty("file.encoding");
			try{
				request.setCharacterEncoding(encode);
			}catch(Exception e){}
		}
		//
		try{
			if(clientJSO == null) clientJSO = new JSONObject();
			String [] url_method_names=request.getParameterValues("method");
			Enumeration<?> en = request.getParameterNames();
			if(url_method_names!= null && url_method_names.length >0){
				JSONArray jms = new JSONArray();
				for(int f=0; f<url_method_names.length;++f)	jms.put( url_method_names[f]);
				clientJSO.put("method", jms);
				JSONObject data = new JSONObject();
				clientJSO.put("arg", data);
				while(en.hasMoreElements()){
					String name=(String)en.nextElement();
					if("method".equalsIgnoreCase(name)) continue;
					String[] values=request.getParameterValues(name);
					if(values.length == 1) data.put(name, values[0]);
					else {
						JSONArray jvs = new JSONArray();
						for(int i=0; i<values.length; ++i){
							String value= values[i];
							try{
								if(is_get && !xProbeServlet.isGAE()) value=new String(values[i].getBytes("iso-8859-1"),"utf-8");
							}catch(Exception e){}
							jvs.put(value);
						}
						data.put(name, jvs);
					}
				}
			}
			//
			JSONObject ret = clientJSO.optJSONObject("_return_"); // previous web/jso
			if(ret != null) {
				this.returnJSO = ret;
				clientJSO.remove("_return_");
			}
			
		}catch(JSONException e){
			e.printStackTrace();
			throw new aSystemException(getSessionId(), "httpRequestInformation[request parameter json]", "exception : "+e.getLocalizedMessage());
		}
	}
	public void dispose(){
		methodException=null;
		clientJSO  = returnJSO = null;
	}
	public JSONObject getClientJSO(){
		return clientJSO;
	}
	public JSONObject getReturnJSO(){
		return returnJSO;
	}
	public void setReturnJSO(JSONObject return_jso){
		returnJSO=return_jso;
	}
	public void logMethodException(aMethodException method_exception){
		methodException=method_exception;
	}
	public Cookie getCookie(String name){
		if(this.request != null){
			Cookie[] cookies = request.getCookies();
			for(int i = 0; cookies != null && i <cookies.length;++i)
				if(cookies[i].getName().equals(name)) return cookies[i];
		}
		return null;
	}
	public Cookie[] getCookie(){
		return (this.request != null)? request.getCookies() : null;
	}
	public void addCookie(Cookie cookie){
		if(this.response != null) this.response.addCookie(cookie);
	}
	public int getLogDeep(){
		return logDeep;
	}
	public void incLogDeep(){
		++logDeep;
	}
	public void decLogDeep(){
		--logDeep;
	}
	public boolean hasException(){
		return methodException != null;
	}
	public boolean isValid(){
		if(hasException()) return false;
		return true;
	}
	public String getHttpContextPath(){
		return (request==null)? null :request.getScheme()+"://"+ request.getServerName()+":"+ request.getServerPort()+request.getContextPath();
	}
	public String getServletPath(){
		return (request==null)? null:request.getServletPath();
	}
	public void responseService() throws ServletException, IOException, aSystemException{
		if(response == null) return;
		if(customizeResponse) return;
		addExceptionMessage();
		
		OutputStreamWriter writer = new OutputStreamWriter(response.getOutputStream(),"UTF-8");
		response.setContentType("text/plain");
		response.setHeader("Cache-Control", "no-cache");
		try {
			returnJSO.write(writer);
		} catch (JSONException e) {
			e.printStackTrace();
			throw new aSystemException(this.getSessionId(), "responseService", e.getLocalizedMessage());
		}
		writer.flush();
		writer.close();
	}
	private void addExceptionMessage(){
		try {
			if(methodException != null){
				JSONObject exp = new JSONObject();
				String method_name=methodException.getMethodName();
				if(method_name != null && method_name.trim().length() > 0) exp.put("method_name", method_name);
				String error_code=methodException.getErrorCode();
				if(error_code != null)	exp.put("error_code", error_code);
				String error_state=methodException.getErrorState();
				if(error_state != null)	exp.put("error_state", error_state);
				System.out.println("exception :\n"+exp.toString());
				returnJSO.put("exception", exp);
			}
		}catch(JSONException e) {
			e.printStackTrace();
		}
	}
}
