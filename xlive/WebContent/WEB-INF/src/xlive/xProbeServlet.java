package xlive;

import java.io.*;
import java.net.URLDecoder;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import xlive.method.logger.xLogger;

import java.util.*;
import java.util.logging.Level;


@SuppressWarnings("serial")
public class xProbeServlet extends HttpServlet {
    private String contextPath;
    //config
    private String loginPleaseHtml=null;
    private String unAuthorizedHtml=null;
    private String resourceEncoding=null;
    
  public static boolean isGAE(){
	  String env = System.getProperty("com.google.appengine.runtime.environment"); 
	  return (env != null);
  }
  public void init(ServletConfig config) throws ServletException {
      super.init(config);
      contextPath=getServletContext().getContextPath();
      new xResourceManager(getServletContext());
      new xServerConfig(getServletContext());
      new xWebInformation(getServletContext());
	  
	  loginPleaseHtml=xServerConfig.getConfigInformtion("security-html.xlive.login-please");
	  unAuthorizedHtml=xServerConfig.getConfigInformtion("security-html.xlive.unauthorized");
	  resourceEncoding = config.getInitParameter("resource_encoding");
  }
  public void doPut(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
	  super.doPut(req,res);
	  return;
  }
  public void doDelete(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
	  super.doDelete(req, res);
	  return;
  }
  public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
      doPost(req, res); 
	  return;
  }
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	  if(!xWebInformation.getSystemInitialized()){
		  xWebInformation.responseNotReadyMessage(request, response);
		  return;
	  }
	  long time1=System.currentTimeMillis();
	  boolean do_next_process = true;
	  xServiceContext service_context=null;
	  try{
		  String request_uri=request.getRequestURI().toLowerCase();
		  if(request_uri.endsWith("/web/disconnect")){
			  //String header_sessionid=(String)request.getHeader("xsessionid");
			  xWebInformation.responseDisconnectMessage(request, response);
			  //if(header_sessionid != null)xWebInformation.detroySessionData(header_sessionid);
			  return;
		  }
		  if(request_uri.endsWith("/web/session_control")){
			  //xWebInformation.responseSessionControl(request, response);
			  return;
		  }
		  //
		  if(request_uri.endsWith(".html")||request_uri.endsWith(".htm")){
			  String res_path=request_uri.replaceFirst(contextPath, "");
			  if(!xServerConfig.isProtectedDirectory(res_path)){
				  Utf8Response(request,response);
				  return;
			  }
			  service_context = new xServiceContext(request, response);
			  if(service_context.isLogin()){
				  if(service_context.isAuthorized(res_path)){
					  Utf8Response(request,response);
					  return;
				  }
				  // no authorized
				  Utf8Response(request,response,unAuthorizedHtml);
				  return;
			  }
			  Utf8Response(request,response,loginPleaseHtml);
			  return;
		  }
		  service_context = new xServiceContext(request, response);
		  if(service_context.doNextProcess(do_next_process)) {
			  xWebInformation.dispatch(service_context, request.getPathInfo());
		  }
	  }catch(xSystemException se){
		  se.printStackTrace();
		  xSystemException.logSystemException(se);
		  do_next_process=false;
	  }
	  try{
		  if(service_context != null){
			  String session_id=service_context.getSessionId();
			  service_context.responseService();
			  service_context.dispose();
			  xLogger.log(Level.FINE, session_id, "xProbeServlet", "doPost", "elapseTime : "+(System.currentTimeMillis()-time1), 0);
		  }
	  }catch(xSystemException se){
		  se.printStackTrace();
		  xSystemException.logSystemException(se);
		  do_next_process=false;
	  }
  }
  @SuppressWarnings("unused")
  private String getMemoryUsed(){
	  System.gc();
	  StringBuffer buf = new StringBuffer();
	  long free = Runtime.getRuntime().freeMemory();
	  long total = Runtime.getRuntime().totalMemory();
	  float f1 = (float) ((total-free)/100000l) / 10;
	  float f2 = (float) (total/100000l) / 10;
	  buf.append(f1).append("MB used, ").append(f2).append("MB allocated");
	  return buf.toString();
  }
  private void Utf8Response(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
	  Utf8Response(request,response, null);
  }
  private void Utf8Response(HttpServletRequest request, HttpServletResponse response, String res_path) throws IOException, ServletException {
	  
	  
      boolean utf8_encoding=false;
      boolean is_ajax="XMLHttpRequest".equalsIgnoreCase(request.getHeader("X-Requested-With"));
      String encoding=request.getCharacterEncoding();
      utf8_encoding=(is_ajax&& "utf-8".equalsIgnoreCase(encoding));
      //
      if(res_path==null){
    	  String request_uri=request.getRequestURI();
    	  res_path=request_uri.replaceFirst(contextPath, "");
    	  encoding=(encoding == null) ? "utf-8":encoding;
    	  res_path = URLDecoder.decode(res_path, encoding);
      }
      if(utf8_encoding){
      	response.setCharacterEncoding("UTF-8");
      	String res_encoding = System.getProperty("xlive.resource.default_encoding"); 
      	if(res_encoding == null || res_encoding.trim().length()==0) res_encoding=resourceEncoding;
      	InputStreamReader input_stream_reader=(res_encoding !=null && res_encoding.trim().length()>0)?
      			new InputStreamReader(xResourceManager.getResourceAsStream(res_path),res_encoding) :
      			new InputStreamReader(xResourceManager.getResourceAsStream(res_path));
      	OutputStreamWriter output_stream_writer= new OutputStreamWriter(response.getOutputStream(), "UTF-8");
      	char[]cbuf=new char[10240];
      	int length=-1;
      	while((length=input_stream_reader.read(cbuf)) !=-1)	output_stream_writer.write(cbuf, 0, length);
      	input_stream_reader.close();
      	output_stream_writer.close();
      }else{
    	  	InputStream input_stream=xResourceManager.getResourceAsStream(res_path);
          	OutputStream output_stream= response.getOutputStream();
          	byte[] bytes=new byte[10240];
          	int length=-1;
          	if(input_stream != null && output_stream != null){
          		while((length=input_stream.read(bytes)) !=-1) output_stream.write(bytes, 0, length);
          	}
          	if(input_stream != null) input_stream.close();
          	if(output_stream != null) output_stream.close();
      }
  }
  /*
  private void dumpRequestInformation(ServletRequest request, ServletResponse response) throws IOException, ServletException {
      // Render the generic servlet request properties
      StringWriter sw = new StringWriter();
      PrintWriter writer = new PrintWriter(sw);
      writer.println("Request Received at " +(new Timestamp(System.currentTimeMillis())));
      writer.println(" characterEncoding=" + request.getCharacterEncoding());
      writer.println("     contentLength=" + request.getContentLength());
      writer.println("       contentType=" + request.getContentType());
      writer.println("            locale=" + request.getLocale());
      writer.print("           locales=");
      Enumeration locales = request.getLocales();
      boolean first = true;
      while(locales.hasMoreElements()) {
      	Locale locale = (Locale) locales.nextElement();
      	if(first) first = false;
      	else  writer.print(", ");
      	writer.print(locale.toString());
      }
      writer.println();
      
      Enumeration names;
      
      names = request.getParameterNames();
      while(names.hasMoreElements()) {
      	String name = (String) names.nextElement();
      	writer.print("         parameter=" + name + "=");
      	String values[] = request.getParameterValues(name);
      	for(int i = 0; i < values.length; i++) {
      		if (i > 0) writer.print(", ");
      		writer.print(values[i]);
      	}
      	writer.println();
      }
      
      writer.println("          protocol=" + request.getProtocol());
      writer.println("        remoteAddr=" + request.getRemoteAddr());
      writer.println("        remoteHost=" + request.getRemoteHost());
      writer.println("            scheme=" + request.getScheme());
      writer.println("        serverName=" + request.getServerName());
      writer.println("        serverPort=" + request.getServerPort());
      writer.println("          isSecure=" + request.isSecure());

      // Render the HTTP servlet request properties
      if(request instanceof HttpServletRequest) {
      	writer.println("---------------------------------------------");
      	HttpServletRequest hrequest = (HttpServletRequest) request;
      	writer.println("       contextPath=" + hrequest.getContextPath());	
      	Cookie cookies[] = hrequest.getCookies();
          if(cookies == null) cookies = new Cookie[0];
          for (int i = 0; i < cookies.length; i++) {
          	writer.println("            cookie=" + cookies[i].getName() +"=" + cookies[i].getValue());
          }
          names = hrequest.getHeaderNames();
          while(names.hasMoreElements()) {
          	String name = (String) names.nextElement();
          	String value = hrequest.getHeader(name);
          	writer.println("            header=" + name + "=" + value);
          }
          writer.println("            method=" + hrequest.getMethod());
          writer.println("          pathInfo=" + hrequest.getPathInfo());
          writer.println("       queryString=" + hrequest.getQueryString());
          writer.println("        remoteUser=" + hrequest.getRemoteUser());
          writer.println("requestedSessionId=" + hrequest.getRequestedSessionId());
          writer.println("        requestURI=" + hrequest.getRequestURI());
          writer.println("       servletPath=" + hrequest.getServletPath());
      }
      writer.println("=============================================");
      writer.flush();
  }
  */
  
  public void destroy(){
	  xLogger.log(Level.INFO, "Servlet destroy");
	  ServletContext context = getServletContext();
	  Enumeration<?> en = context.getAttributeNames();
	  while(en.hasMoreElements()){
		  Object obj= context.getAttribute((String) en.nextElement());
		  if(obj instanceof xContextObjectImp) ((xContextObjectImp)obj).destroy();
	  }
  }
  //Get Servlet information
  public String getServletInfo() {
        return "xlive.ProbeServlet Information";
  }
 
}
