package xlive.ls;

import javax.servlet.*;



public final class xContextListener implements ServletContextAttributeListener, ServletContextListener {
	
	public void attributeAdded(ServletContextAttributeEvent event) {
		//log("attributeAdded('" + event.getName() + "', '" +
		//    event.getValue() + "')");
		//System.out.println("attributeAdded('" + event.getName() + "', '" +
		//    event.getValue() + "')");

    }
    public void attributeRemoved(ServletContextAttributeEvent event) {

    	//log("attributeRemoved('" + event.getName() + "', '" +
    	//    event.getValue() + "')");
    	//System.out.println("attributeRemoved('" + event.getName() + "', '" +
    	//    event.getValue() + "')");

    }
    public void attributeReplaced(ServletContextAttributeEvent event) {

	//log("attributeReplaced('" + event.getName() + "', '" +
	//    event.getValue() + "')");
    //System.out.println("attributeReplaced('" + event.getName() + "', '" +
    //	    event.getValue() + "')");

    }
    public void contextDestroyed(ServletContextEvent event) {
    	//System.out.println("xContextListener: contextDestroyed");
    	
	//log("contextDestroyed()");
	//this.context = null;

    }
    public void contextInitialized(ServletContextEvent event) {
    	//System.out.println("xContextListener: contextInitialized");
    	
    	/*
    	
    	ServletContext context = event.getServletContext();
    	
    	
  	  System.out.println("context.getServletContextName()");
	  System.out.print(context.getServletContextName());
	  System.out.println();
	  //////////////
	  Enumeration em = context.getAttributeNames();
	  System.out.println("context.getAttributeNames()");
	  while(em.hasMoreElements()){
		  System.out.println(em.nextElement());
	  }
	  System.out.println();
	  ///////////////
	  em = context.getInitParameterNames();
	  System.out.println("context.getInitParameterNames()");
	  while(em.hasMoreElements()){
		  System.out.println(em.nextElement());
	  }
	  System.out.println();
	  /////////////////
	  System.out.println("context.getMajorVersion()");
	  System.out.print(context.getMajorVersion());
	  System.out.println();
	  System.out.println("context.getMinorVersion()");
	  System.out.print(context.getMinorVersion());
	  System.out.println();
	  //////////////
	  System.out.println("context.getServletContextName()");
	  System.out.print(context.getServletContextName());
	  System.out.println();
	  /////
	  System.out.println("context.getServerInfo()");
	  System.out.print(context.getServerInfo());
	  System.out.println();
	  /////////
	  
	  */
	  
     	
	//this.context = event.getServletContext();
	//log("contextInitialized()");

    }


}
