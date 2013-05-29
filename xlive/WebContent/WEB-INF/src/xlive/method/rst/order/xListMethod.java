package xlive.method.rst.order;

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
	public Object process()throws xMethodException{
		boolean valid=true;
		String why = "";
		String mall_fid=this.getArguments("mall-fid");
		Element orders = this.setReturnArguments("orders", "");
		
    	DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
    	Query q = new Query("xOrder");
    	/*
    	q.addFilter("processed", FilterOperator.EQUAL, Long.valueOf(0));
    	q.addFilter("mallFid", FilterOperator.EQUAL, mall_fid);
    	*/
    	q.setFilter(
    			CompositeFilterOperator.and(
    				new Query.FilterPredicate("processed", FilterOperator.EQUAL, Long.valueOf(0)),
    				new Query.FilterPredicate("mallFid", FilterOperator.EQUAL, mall_fid)
    			)
    	);

    	q.addSort("id", SortDirection.DESCENDING);
    	
    	PreparedQuery pq = ds.prepare(q);
    	int count = 0;
    	for(Entity found : pq.asIterable()) {
        	Element order_element=(Element)orders.appendChild(this.createElement("order"));
        	xOrder.xmlOrder(new xOrder(found), order_element);
        	order_element.appendChild(this.createElement("goods-list"));
			++count;
    	}
    	orders.setAttribute("count", String.valueOf(count));

		this.setReturnArguments("valid", String.valueOf(valid));
		this.setReturnArguments("why", why);
		return this.getServiceContext().doNextProcess();
	}
}
