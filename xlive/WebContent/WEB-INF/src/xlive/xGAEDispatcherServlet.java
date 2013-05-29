package xlive;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@SuppressWarnings("serial")
public class xGAEDispatcherServlet extends HttpServlet {
  public static boolean isGAE(){
	  String env = System.getProperty("com.google.appengine.runtime.environment"); 
	  return (env != null);
  }
  public void init(ServletConfig config) throws ServletException {
      super.init(config);
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
	  String uri = request.getRequestURI();
	  uri=uri.replaceFirst("/xlive", "");
	  if(uri.endsWith(".html")||uri.endsWith(".htm")||uri.startsWith("/web")||uri.startsWith("/ws")||uri.startsWith("/jso")){
		  request.getRequestDispatcher(uri).forward(request,response);
		  return;
	  }
	  if(uri.toLowerCase().startsWith("/web-inf")){
		  response.sendError(HttpServletResponse.SC_NOT_FOUND);
		  return;	
	  }
	  response.setContentType(this.getServletContext().getMimeType(uri));
	  InputStream input_stream=xResourceManager.getResourceAsStream(uri);
      OutputStream output_stream= response.getOutputStream();
      byte[] bytes=new byte[10240];
      int length=-1;
      while((length=input_stream.read(bytes)) !=-1) output_stream.write(bytes, 0, length);
      input_stream.close();
      output_stream.close();
  }
  public String getServletInfo() {
        return "xlive.GAEDispatcher Information";
  }
 
}
