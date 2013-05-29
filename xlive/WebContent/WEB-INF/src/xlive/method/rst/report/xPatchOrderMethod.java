package xlive.method.rst.report;

import xlive.method.*;
import xlive.method.rst.order.xOrder;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;

public class xPatchOrderMethod extends xDefaultMethod{
	public Object process()throws xMethodException{
		boolean valid=true;
		String why = "";
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
    	Query q = new Query("xOrder");
    	PreparedQuery pq = ds.prepare(q);
    	int total=0;
    	for(Entity found : pq.asIterable()) {
    		xOrder order = new xOrder(found);
    		Query q2 = new Query("xGoods", order.entity.getKey());
    		int count=ds.prepare(q2).countEntities(FetchOptions.Builder.withDefaults());
    		order.setGoods(count);
    		ds.put(order.entity);
    		++total;
    	}
    	this.setReturnArguments("count", String.valueOf(total));
		this.setReturnArguments("valid", String.valueOf(valid));
		this.setReturnArguments("why", why);
		return this.getServiceContext().doNextProcess();
	}
}
