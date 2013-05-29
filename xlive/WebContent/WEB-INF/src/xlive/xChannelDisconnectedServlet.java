package xlive;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.channel.ChannelPresence;
import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.KeyFactory;

@SuppressWarnings("serial")
public class xChannelDisconnectedServlet extends HttpServlet {
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
	  ChannelService channelService = ChannelServiceFactory.getChannelService();
	  ChannelPresence presence = channelService.parsePresence(request);	
	  if(presence != null && !presence.isConnected()){
		  String id = presence.clientId();
		  DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		  ds.delete(KeyFactory.stringToKey(id));
	  }
  }
  public String getServletInfo() {
        return "xlive.ChannelConnected Information";
  }
 
}
