package xlive.ls;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Timestamp;
import java.util.Enumeration;
import java.util.Locale;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public final class xRequestDumperFilter implements Filter {
    private FilterConfig filterConfig = null;
    private int debug = 0;
    public void destroy() {
        this.filterConfig = null;
    }
    public void doFilter(ServletRequest request, ServletResponse response,FilterChain chain)	throws IOException, ServletException {
        if (filterConfig == null||debug==0){
        	chain.doFilter(request, response);
        	return;
        }
        
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
        /*
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
        */
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
        filterConfig.getServletContext().log(sw.getBuffer().toString());
System.out.println(sw.getBuffer().toString());  
        chain.doFilter(request, response);
    }
    public void init(FilterConfig aFilterConfig) throws ServletException {
    	filterConfig = aFilterConfig;
        if(filterConfig != null) {
            String value = filterConfig.getInitParameter("debug");
            debug=(value!=null)?Integer.parseInt(value):0;
        } 
    }
    public String toString() {
    	if(filterConfig == null) return ("RequestDumperFilter()");
    	StringBuffer sb = new StringBuffer("RequestDumperFilter(");
    	sb.append(filterConfig);
    	sb.append(")");
    	return (sb.toString());
    }
}

