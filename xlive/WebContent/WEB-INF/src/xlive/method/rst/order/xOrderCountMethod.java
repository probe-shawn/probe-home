package xlive.method.rst.order;

import xlive.method.*;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query;

public class xOrderCountMethod extends xDefaultMethod{
	public Object process()throws xMethodException{
		boolean valid=false;
		String why="";
		String fid = this.getArguments("customer-fid");
		
    	DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
    	Query q = new Query("xOrder");
    	q.setFilter(new Query.FilterPredicate("customerFid", Query.FilterOperator.EQUAL, fid));
    	q.setKeysOnly();
    	int count = ds.prepare(q).countEntities(FetchOptions.Builder.withDefaults());
    	this.setReturnArguments("data", String.valueOf(count));
 		this.setReturnArguments("valid", String.valueOf(valid));
		this.setReturnArguments("why", why);
		return this.getServiceContext().doNextProcess();
	}
	
}
