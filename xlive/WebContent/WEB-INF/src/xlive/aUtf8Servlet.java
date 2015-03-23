package xlive;

import java.io.*;
import java.net.URLDecoder;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class aUtf8Servlet extends HttpServlet {
    private String contextPath;
    private String resourceEncoding=null;
    
  public static boolean isGAE(){
	  String env = System.getProperty("com.google.appengine.runtime.environment"); 
	  return (env != null);
  }
  public void init(ServletConfig config) throws ServletException {
      super.init(config);
      contextPath=getServletContext().getContextPath();
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
	  Utf8Response(request,response);
  }
  private void Utf8Response(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
	  Utf8Response(request,response, null);
  }
  private void Utf8Response(HttpServletRequest request, HttpServletResponse response, String res_path) throws IOException, ServletException {
      boolean utf8_encoding=false;
      boolean is_ajax="XMLHttpRequest".equalsIgnoreCase(request.getHeader("X-Requested-With"));
      String encoding=request.getCharacterEncoding();
      utf8_encoding=(is_ajax || "utf-8".equalsIgnoreCase(encoding));
      //
      if(res_path==null){
    	  String request_uri=request.getRequestURI();
    	  res_path=request_uri.replaceFirst(contextPath, "");
    	  encoding=(encoding == null) ? "utf-8":encoding;
    	  res_path = URLDecoder.decode(res_path, encoding);
      }
      if(res_path.endsWith(".js") && isGAE()) utf8_encoding= true;
      if(utf8_encoding){
      	response.setCharacterEncoding("utf-8");
      	String res_encoding = System.getProperty("xlive.resource.default_encoding"); 
      	if(res_encoding == null || res_encoding.trim().length()==0) res_encoding=resourceEncoding;
      	InputStreamReader input_stream_reader=(res_encoding !=null && res_encoding.trim().length()>0)?
      			new InputStreamReader(aResourceManager.getResourceAsStream(res_path),res_encoding) :
      			new InputStreamReader(aResourceManager.getResourceAsStream(res_path));
      	OutputStreamWriter output_stream_writer= new OutputStreamWriter(response.getOutputStream(), "utf-8");
      	char[]cbuf=new char[10240];
      	int length=-1;
      	while((length=input_stream_reader.read(cbuf)) !=-1)	output_stream_writer.write(cbuf, 0, length);
      	input_stream_reader.close();
      	output_stream_writer.close();
      }else{
    	  	InputStream input_stream=aResourceManager.getResourceAsStream(res_path);
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
 
}
