package xlive.method.rst.barter;

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

public class xListMyLoveMethod extends xDefaultMethod{
	public Object process()throws xMethodException{
		Element data = this.setReturnArguments("barters", "");
    	DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
    	Query q = new Query(xBarter.class.getSimpleName());
    	/*
    	q.addFilter("removed", FilterOperator.EQUAL, 0);
    	q.addFilter("ownerFid", FilterOperator.EQUAL, this.getArguments("owner-fid"));
    	q.addFilter("status", FilterOperator.EQUAL, 0);
    	*/
    	q.setFilter(
    			CompositeFilterOperator.and(
    				new Query.FilterPredicate("removed", FilterOperator.EQUAL, 0),
    				new Query.FilterPredicate("ownerFid", FilterOperator.EQUAL, this.getArguments("owner-fid")),
    				new Query.FilterPredicate("status", FilterOperator.EQUAL, 0)
    			)
    	);

    	q.addSort("squDate",SortDirection.ASCENDING);
    	PreparedQuery pq = ds.prepare(q);
    	for(Entity found : pq.asIterable()) {
    		xBarter barter = new xBarter(found);
    		String ilove = barter.getILove();
    		if(ilove == null || ilove.trim().length() == 0) data.appendChild(xBarter.barterXml(new xBarter(found),true));
    	}
    	//
    	q = new Query(xBarter.class.getSimpleName());
    	/*
    	q.addFilter("removed", FilterOperator.EQUAL, 0);
    	q.addFilter("ownerFid", FilterOperator.EQUAL, this.getArguments("owner-fid"));
    	q.addFilter("status", FilterOperator.EQUAL, 6);
    	*/
    	q.setFilter(
    			CompositeFilterOperator.and(
    				new Query.FilterPredicate("removed", FilterOperator.EQUAL, 0),
    				new Query.FilterPredicate("ownerFid", FilterOperator.EQUAL, this.getArguments("owner-fid")),
    				new Query.FilterPredicate("status", FilterOperator.EQUAL, 6)
    			)
    	);
   	
    	
    	q.addSort("squDate",SortDirection.ASCENDING);
    	pq = ds.prepare(q);
    	for(Entity found : pq.asIterable()) {
    		xBarter barter = new xBarter(found);
    		String ilove = barter.getILove();
    		if(ilove == null || ilove.trim().length() == 0) data.appendChild(xBarter.barterXml(new xBarter(found),true));
    	}

    	
		this.setReturnArguments("valid", "true");
		return this.getServiceContext().doNextProcess();
	}
	
}
