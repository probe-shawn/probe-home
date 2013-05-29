package xlive.method.channel;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.KeyFactory;

import xlive.method.*;

public class xClosedMethod extends xDefaultMethod{

	public Object process() throws xMethodException{
		boolean valid=true;
		String why="";
		String id=this.getArguments("id");
		try{
			DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
			ds.delete(KeyFactory.stringToKey(id));
		}catch(Exception e){}
		setReturnArguments("valid", String.valueOf(valid));
		setReturnArguments("why", why);
		return getServiceContext().doNextProcess();
	}
}
