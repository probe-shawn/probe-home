package xlive.method.upos.tag;


import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.KeyFactory;

import xlive.method.*;

public class uRemoveTagMethod extends xDefaultMethod{
	public Object process()throws xMethodException{
		boolean valid=true;
		String why = "";
		String key=this.getArguments("tag.key");
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		if(key != null && key.trim().length()>0){
			ds.delete(KeyFactory.stringToKey(key));
		}
		this.setReturnArguments("valid", String.valueOf(valid));
		this.setReturnArguments("why", why);
		//
		return this.getServiceContext().doNextProcess();
	}
}
