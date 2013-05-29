package xlive;

import java.io.*;
import java.net.URLDecoder;

import javax.servlet.*;
import javax.servlet.http.HttpServlet; 
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import xlive.google.ds.xFile;

 
@SuppressWarnings("serial")
public class xGAEDownloadServlet extends HttpServlet {
	
	private String contextPath;
	
	public static boolean isGAE(){
		String env = System.getProperty("com.google.appengine.runtime.environment"); 
		return (env != null);
	}
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		if(xGAEDownloadServlet.isGAE()){
			xFile gae = new xFile("/gae");
			if(!gae.exists()) gae.makeDirs();
		}
		contextPath=getServletContext().getContextPath();
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
		String request_uri=request.getRequestURI();
		String res_path=request_uri.replaceFirst(contextPath, "");
		String encoding=request.getCharacterEncoding();
		encoding=(encoding == null) ? "utf-8":encoding;
		res_path = URLDecoder.decode(res_path, encoding);
		xFile file = new xFile(res_path);
		if(file.exists()){
			OutputStream output_stream= response.getOutputStream();
			output_stream.write(file.getBytes());
			output_stream.close();
		}else{
			 response.sendError(HttpServletResponse.SC_NOT_FOUND);
		}
  }
  public String getServletInfo() {
        return "xlive.GAEDownload Information";
  }
 
}
