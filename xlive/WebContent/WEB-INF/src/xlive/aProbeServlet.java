package xlive;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;


@SuppressWarnings("serial")
public class aProbeServlet extends HttpServlet {
    
  public static boolean isGAE(){
	  String env = System.getProperty("com.google.appengine.runtime.environment"); 
	  return (env != null);
  }
  public void init(ServletConfig config) throws ServletException {
      super.init(config);
      new aResourceManager(getServletContext());
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
	  if(!aWebInformation.getSystemInitialized()){
		  try {
			aWebInformation.responseNotReadyMessage(request, response);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		  return;
	  }
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
			 // String session_id=service_context.getSessionId();
			  service_context.responseService();
			  service_context.dispose();
		  }
	  }catch(aSystemException se){
		  se.printStackTrace();
		  aSystemException.logSystemException(se);
		  do_next_process=false;
	  }
  }
 
}
