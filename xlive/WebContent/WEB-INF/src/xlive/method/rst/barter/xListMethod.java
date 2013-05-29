package xlive.method.rst.barter;

import java.text.SimpleDateFormat;
import xlive.method.*;
import org.w3c.dom.*;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.SortDirection;

public class xListMethod extends xDefaultMethod{
	SimpleDateFormat sdf=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	
	public Object process()throws xMethodException{
		Element data = this.setReturnArguments("barters", "");
    	DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
    	Query q = new Query(xBarter.class.getSimpleName());
    	/*
    	q.addFilter("removed", FilterOperator.EQUAL, 0);
    	q.addFilter("ownerFid", FilterOperator.EQUAL, this.getArguments("owner-fid"));
    	*/
    	q.setFilter(
    			CompositeFilterOperator.and(
    				new Query.FilterPredicate("removed", FilterOperator.EQUAL, 0),
    				new Query.FilterPredicate("ownerFid", FilterOperator.EQUAL, this.getArguments("owner-fid"))
    			)
    	);
   	
    	q.addSort("squDate",SortDirection.ASCENDING);
    	PreparedQuery pq = ds.prepare(q);
    	for(Entity found : pq.asIterable()) {
    		data.appendChild(xBarter.barterXml(new xBarter(found),false));
    	}
		this.setReturnArguments("valid", "true");
		return this.getServiceContext().doNextProcess();
	}
	
}
