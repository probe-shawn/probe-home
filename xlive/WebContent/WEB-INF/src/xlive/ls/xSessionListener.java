package xlive.ls;


import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import javax.servlet.*;

import xlive.xWebInformation;




public final class xSessionListener implements ServletContextListener,
		       HttpSessionAttributeListener, HttpSessionListener {
	
	public void attributeAdded(HttpSessionBindingEvent event) {
		//log("attributeAdded('" + event.getSession().getId() + "', '" +
		//    event.getName() + "', '" + event.getValue() + "')");
 
	}
	public void attributeRemoved(HttpSessionBindingEvent event) {

		//log("attributeRemoved('" + event.getSession().getId() + "', '" +
		//    event.getName() + "', '" + event.getValue() + "')");

	}
	public void attributeReplaced(HttpSessionBindingEvent event) {

		//log("attributeReplaced('" + event.getSession().getId() + "', '" +
		//    event.getName() + "', '" + event.getValue() + "')");
 
	}

	public void sessionCreated(HttpSessionEvent event) {
		//log("sessionCreated('" + event.getSession().getId() + "')");
		System.out.println("xSessionListener: sessionCreated : "+event.getSession().getId());
	}

	public void sessionDestroyed(HttpSessionEvent event) {
		System.out.println("xSessionListener: sessionDestroyed : "+event.getSession().getId());
		//xWebInformation web_inf=(xWebInformation)event.getSession().getServletContext().getAttribute("xWebInformation");
		//if(web_inf != null)xWebInformation.detroyContextSession(event.getSession().getId());
		//log("sessionDestroyed('" + event.getSession().getId() + "')");
	}
	
	public void contextDestroyed(ServletContextEvent event) {
    	System.out.println("xSessionListener: contextDestroyed");
		xWebInformation web_inf=(xWebInformation)event.getServletContext().getAttribute("xWebInformation");
		if(web_inf != null)web_inf.destroy();
    	
		//log("contextDestroyed()");
		//this.context = null;

	}

	public void contextInitialized(ServletContextEvent event) {
    	System.out.println("xSessionListener: contextInitialized");

		//this.context = event.getServletContext();
		//log("contextInitialized()");

	}
    

}
