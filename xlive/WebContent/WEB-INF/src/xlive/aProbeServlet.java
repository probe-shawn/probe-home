package xlive;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import xlive.method.logger.xLogger;

import java.util.logging.Level;


@SuppressWarnings("serial")
public class aProbeServlet extends HttpServlet {
    
  public static boolean isGAE(){
	  String env = System.getProperty("com.google.appengine.runtime.environment"); 
	  return (env != null);
  }
  public void init(ServletConfig config) throws ServletException {
      super.init(config);
      //new xResourceManager(getServletContext());
      new aServerConfig(getServletContext());
      new aWebInformation(getServletContext());
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
	  aServiceContext service_context=null;
	  try{
		  service_context = new aServiceContext(request, response);
		  if(service_context.doNextProcess(do_next_process)) {
			  aWebInformation.dispatch(service_context, request.getPathInfo());
		  }
	  }catch(aSystemException se){
		  se.printStackTrace();
		  aSystemException.logSystemException(se);
		  do_next_process=false;
	  }
	  try{
		  if(service_context != null){
			  String session_id=service_context.getSessionId();
			  service_context.responseService();
			  service_context.dispose();
			  xLogger.log(Level.FINE, session_id, "aProbeServlet", "doPost", "elapseTime : "+(System.currentTimeMillis()-time1), 0);
		  }
	  }catch(aSystemException se){
		  se.printStackTrace();
		  aSystemException.logSystemException(se);
		  do_next_process=false;
	  }
  }
 
}
