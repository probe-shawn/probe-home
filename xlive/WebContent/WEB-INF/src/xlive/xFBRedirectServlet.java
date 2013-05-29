package xlive;

import java.io.*;
import java.net.URLDecoder;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class xFBRedirectServlet extends HttpServlet {
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
	  String url=request.getParameter("url");
	  url=URLDecoder.decode(url, "utf-8");
	  response.sendRedirect(url);
  }
  public String getServletInfo() {
        return "xlive.FBRedirectSrvlet Information";
  }
 
}
