package xlive;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class xLoadBalanceManager implements xContextObjectImp{
	
	private ServletContext servletContext;
	
	public xLoadBalanceManager(ServletContext servlet_context) {
		servletContext = servlet_context;
	}
	public boolean initial(){
		return true;
	}
	public void destroy(){
		servletContext=null;
	}
	public ServletContext getServletContext(){
		return servletContext;
	}
	public String getDescription(){
		return "xLoadBalanceManager";
	}
	public boolean process(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException{
		boolean do_next_process = true;
		return do_next_process;
	}

}
