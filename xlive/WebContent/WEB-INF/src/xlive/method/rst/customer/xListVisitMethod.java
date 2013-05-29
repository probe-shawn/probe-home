package xlive.method.rst.customer;

import xlive.method.*;

import org.w3c.dom.*;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.SortDirection;

public class xListVisitMethod extends xDefaultMethod{
	
	public Object process()throws xMethodException{
		boolean valid=true;
		String why = "";
		String fid= this.getArguments("fid");
		Element data = this.setReturnArguments("data", "");
    	DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
    	Query q = new Query(xVisit.class.getSimpleName());
    	/*
    	q.addFilter("fid", FilterOperator.EQUAL, fid);
    	*/
    	q.setFilter(new Query.FilterPredicate("fid", FilterOperator.EQUAL, fid));
    	q.addSort("count",SortDirection.DESCENDING);
    	PreparedQuery pq = ds.prepare(q);
		Iterable<Entity> list_result = pq.asIterable();
    	int count = 0;
		for(Entity entity : list_result){
			Element visit=data.getOwnerDocument().createElement("visit");
			xVisit.xmlVisit(new xVisit(entity), visit);
			data.appendChild(visit);
    		++count;
    	}
    	this.setReturnArguments("valid", String.valueOf(valid));
		this.setReturnArguments("why", why);
		return this.getServiceContext().doNextProcess();
		
	}
	
}
