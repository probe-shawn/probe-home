package xlive;

import java.io.*;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.blobstore.BlobKey;

import xlive.google.ds.xBSF;
import xlive.google.ds.xFile;


@SuppressWarnings("serial")
public class xGAEUploadServlet extends HttpServlet {
  public static boolean isGAE(){
	  String env = System.getProperty("com.google.appengine.runtime.environment"); 
	  return (env != null);
  }
  public void init(ServletConfig config) throws ServletException {
      super.init(config);
      if(xGAEUploadServlet.isGAE()){
    	  xFile gae = new xFile("/gae");
    	  if(!gae.exists())gae.makeDirs();
      }
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
	  	/*
	    UserService userService = UserServiceFactory.getUserService();
	    User user = userService.getCurrentUser();

	    String authURL = userService.createLogoutURL("/");
	    String uploadURL = blobstoreService.createUploadUrl("/post");

	    req.setAttribute("uploadURL", uploadURL);
	    req.setAttribute("authURL", authURL);
	    req.setAttribute("user", user);
	    */
	    String qstr=request.getQueryString();
		Map<String, BlobKey> blobs = xBSF.blobStoreService().getUploadedBlobs(request);
		Iterator<String> names = blobs.keySet().iterator();
		int count = 0;
		while(names.hasNext()){
			String blob_name = names.next();
		    BlobKey blob_key = blobs.get(blob_name);
		    String key_string = blob_key.getKeyString();
		    request.setAttribute("blobKey"+count, key_string);
		    qstr+="&blobKey"+count+"="+key_string;
		    ++count;
		}
		qstr +="&blobKeyCount="+count;
		qstr += "&gaeblobstore=true";
		String gae_redirect = request.getParameter("gae-redirect");
		response.setStatus(HttpServletResponse.SC_FOUND);
		response.sendRedirect(gae_redirect+"?"+qstr);
  }
  public String getServletInfo() {
        return "xlive.GAEUpload Information";
  }
 
}
