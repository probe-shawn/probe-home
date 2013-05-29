package xlive.ls;
import java.io.*;
import java.util.logging.Level;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import com.google.appengine.api.NamespaceManager;

import xlive.xProbeServlet;
import xlive.method.logger.xLogger;

public class xNamespaceFilter implements Filter{
    
    public void init(FilterConfig aFilterConfig) {
    }
    public void destroy() {
    }
    public void doFilter(ServletRequest request,ServletResponse response,FilterChain chain) throws IOException, ServletException {
    	if(xProbeServlet.isGAE() && request instanceof HttpServletRequest ){
    		String fbns=((HttpServletRequest)request).getHeader("fbns");
    		if(fbns==null || fbns.trim().length()==0){
    			Cookie cookie=this.getCookie((HttpServletRequest)request, "fbns");
    			if(cookie != null) fbns=cookie.getValue();
    		}
    		if(fbns==null || fbns.trim().length()==0){
    			String xml = ((HttpServletRequest)request).getHeader("X-XLive-Content");
    			if(!"xml".equals(xml))fbns=request.getParameter("fbns");
    		}
    		if(fbns != null && fbns.trim().length() != 0){
    			//NamespaceManager.set(fbns);
    			System.out.println("set namespace fbns :"+fbns);
    			xLogger.log(Level.WARNING, "set namespace fbns :"+fbns);
    		}
    		//else  NamespaceManager.set(NamespaceManager.getGoogleAppsNamespace());
    		//xLogger.log(Level.WARNING, "log set namespace fbns :"+fbns+"    uri :"+ ((HttpServletRequest)request).getRequestURI());
    	}
    	chain.doFilter(request, response);
    }
	public Cookie getCookie(HttpServletRequest request,String name){
		if(request != null){
			Cookie[] cookies = request.getCookies();
			for(int i = 0; cookies != null && i <cookies.length;++i)
				if(cookies[i].getName().equals(name)) return cookies[i];
		}
		return null;
	}

}
