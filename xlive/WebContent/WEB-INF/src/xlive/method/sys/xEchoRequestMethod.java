package xlive.method.sys;

import java.sql.Timestamp;
import java.util.Enumeration;
import java.util.Locale;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import org.w3c.dom.Element;

import xlive.method.*;

public class xEchoRequestMethod extends xDefaultMethod{
	public Object process() throws xMethodException{
		HttpServletRequest request=this.getServiceContext().getHttpServletRequest();
		Element data=this.setReturnArguments("data", "");
		data.appendChild(this.createElement("received-at",(new Timestamp(System.currentTimeMillis()).toString())));
		data.appendChild(this.createElement("character-encoding",request.getCharacterEncoding()));
		data.appendChild(this.createElement("content-length",String.valueOf(request.getContentLength())));
		data.appendChild(this.createElement("locale",request.getLocale().toString()));
		Element locales_node=(Element)data.appendChild(this.createElement("locale"));
		String locales_str="";
		Enumeration<Locale> locales = request.getLocales();
		boolean first = true;
	    while(locales.hasMoreElements()) {
	    	Locale locale = (Locale) locales.nextElement();
	      	if(first) first = false;
	      	else  locales_str+=", ";
	      	locales_str+=locale.toString();
	    }
	    locales_node.setTextContent(locales_str);
	    //
	    Element params=(Element)data.appendChild(this.createElement("parameters"));
	    Enumeration<String> names = request.getParameterNames();
	    while(names.hasMoreElements()) {
	      	String name = (String) names.nextElement();
	      	Element p=(Element)params.appendChild(this.createElement("parameter"));
	      	p.setAttribute("name", name);
	      	String value="";
	      	String values[] = request.getParameterValues(name);
	      	for(int i = 0; i < values.length; i++) {
	      		if(i > 0) value+=", ";
	      		value+=values[i];
	      	}
	      	p.setTextContent(value);
	      }
		data.appendChild(this.createElement("protocol",request.getProtocol()));
		data.appendChild(this.createElement("remote-addr",request.getRemoteAddr()));
		data.appendChild(this.createElement("remote-host",request.getRemoteHost()));
		data.appendChild(this.createElement("scheme",request.getScheme()));
		data.appendChild(this.createElement("server-name",request.getServerName()));
		data.appendChild(this.createElement("server-port",String.valueOf(request.getServerPort())));
		data.appendChild(this.createElement("is-secure",String.valueOf(request.isSecure())));
		data.appendChild(this.createElement("context-path",request.getContextPath()));
		Element cookies_node=(Element)data.appendChild(this.createElement("cookies"));
      	Cookie cookies[] = request.getCookies();
	    if(cookies == null) cookies = new Cookie[0];
	    for(int i = 0; i < cookies.length; i++) {
	    	Element cookie=(Element)cookies_node.appendChild(this.createElement("cookie"));
	    	cookie.setAttribute("name", cookies[i].getName());
	    	cookie.setTextContent( cookies[i].getValue());
	    }
	    Element header_node=(Element)data.appendChild(this.createElement("headers"));
        names = request.getHeaderNames();
	    while(names.hasMoreElements()) {
	        String name = (String) names.nextElement();
	        String value = request.getHeader(name);
	    	Element header=(Element)header_node.appendChild(this.createElement("header"));
	    	header.setAttribute("name", name);
	    	header.setTextContent(value);
	    }
		data.appendChild(this.createElement("method",request.getMethod()));
		data.appendChild(this.createElement("path-info",request.getPathInfo()));
		data.appendChild(this.createElement("query-string",request.getQueryString()));
		data.appendChild(this.createElement("remote-user",request.getRemoteUser()));
		data.appendChild(this.createElement("requested-session-id",request.getRequestedSessionId()));
		data.appendChild(this.createElement("request-uri",request.getRequestURI()));
		data.appendChild(this.createElement("servlet-path",request.getServletPath()));
		return getServiceContext().doNextProcess();
	}
}
