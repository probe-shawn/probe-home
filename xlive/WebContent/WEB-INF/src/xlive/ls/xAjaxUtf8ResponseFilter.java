package xlive.ls;
import java.io.*;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class xAjaxUtf8ResponseFilter implements Filter{
    private FilterConfig filterConfig = null;
    private int debug = 0;
    private String servletRealPath=null;
    private String contextPath=null;
    public void init(FilterConfig aFilterConfig) {
    	filterConfig = aFilterConfig;
        if(filterConfig != null) {
            String value = filterConfig.getInitParameter("debug");
            debug=(value!=null)?Integer.parseInt(value):0;
            servletRealPath=filterConfig.getServletContext().getRealPath("/");
            contextPath=filterConfig.getServletContext().getContextPath();
        } 
    }
    public void destroy() {
        this.filterConfig = null;
    }
    public void doFilter(ServletRequest request,ServletResponse response,FilterChain chain ) throws IOException, ServletException {
        if(debug > 0) System.out.println("@doFilter");
        boolean utf8_encoding=false;
        if(request instanceof HttpServletRequest) {
            if(debug > 1) System.out.println("requestURI = " + ((HttpServletRequest)request).getRequestURI());
            boolean is_ajax="XMLHttpRequest".equalsIgnoreCase(((HttpServletRequest)request).getHeader("X-Requested-With"));
            String encoding=((HttpServletRequest)request).getCharacterEncoding();
            utf8_encoding=(is_ajax&& "utf-8".equalsIgnoreCase(encoding));
        }
        if(utf8_encoding && response instanceof HttpServletResponse){
        	((HttpServletResponse)response).setCharacterEncoding("UTF-8");
        	String request_uri=((HttpServletRequest)request).getRequestURI();
        	int index=request_uri.indexOf(contextPath);
        	String file_name=servletRealPath+ ((index>=0)?request_uri.substring(index+contextPath.length()) : request_uri);
        	InputStreamReader input_stream_reader=new InputStreamReader(new FileInputStream(file_name));
        	OutputStreamWriter output_stream_writer= new OutputStreamWriter(response.getOutputStream(), "UTF-8");
        	char[]cbuf=new char[1024];
        	int length=-1;
        	while((length=input_stream_reader.read(cbuf)) !=-1)
        		output_stream_writer.write(cbuf, 0, length);
        	input_stream_reader.close();
        	output_stream_writer.close();
        	return;
        }
        chain.doFilter(request, response);
    }
    public void setFilterConfig(FilterConfig aFilterConfig) {
        init(aFilterConfig);
    }
    public FilterConfig getFilterConfig() {
        return filterConfig;
    }

}
