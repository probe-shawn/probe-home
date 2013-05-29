package xlive.method.rst.order;

import xlive.method.*;
import org.w3c.dom.*;

import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.QueryResultList;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.SortDirection;

public class xListCustomerOrderMethod extends xDefaultMethod{
	public Object process()throws xMethodException{
		boolean valid=true;
		String why = "";
		String fid=this.getArguments("fid");
		String cursor=this.getArguments("cursor");
		int limit =10;
		Element orders = this.setReturnArguments("orders", "");
		Cursor decoded_cursor=null;
		if(cursor != null && cursor.length() > 0){
			 decoded_cursor = Cursor.fromWebSafeString(cursor);
		}
    	DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
    	Query q = new Query("xOrder");
    	/*
    	q.addFilter("customerFid", Query.FilterOperator.EQUAL, fid);
    	*/
    	q.setFilter(new Query.FilterPredicate("customerFid", Query.FilterOperator.EQUAL, fid));
    	q.addSort("date", Query.SortDirection.DESCENDING);
    	
    	PreparedQuery pq = ds.prepare(q);
    	if(cursor==null || cursor.length()==0){
	    	PreparedQuery pq2 = ds.prepare(q);
	    	this.setReturnArguments("total",String.valueOf(pq2.countEntities(FetchOptions.Builder.withDefaults())));
    	}
    	int count = 0;  	
    	
    	QueryResultList<Entity> list_result = pq.asQueryResultList(decoded_cursor != null ? FetchOptions.Builder.withStartCursor(decoded_cursor).limit(limit) : FetchOptions.Builder.withDefaults().limit(limit));
    	
    	
    	for(Entity entity : list_result){
        	Element order_element=(Element)orders.appendChild(this.createElement("order"));
        	xOrder.xmlOrder(new xOrder(entity), order_element);
			++count;
    	}
    	if(count == limit && list_result != null){
			 this.setReturnArguments("cursor", list_result.getCursor().toWebSafeString());
		}
    	
    	orders.setAttribute("count", String.valueOf(count));
		this.setReturnArguments("valid", String.valueOf(valid));
		this.setReturnArguments("why", why);
		return this.getServiceContext().doNextProcess();
	}
}
