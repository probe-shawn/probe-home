package xlive.method.rst.circle;

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
import com.google.appengine.api.datastore.Query.SortDirection;

public class xListCircleMethod extends xDefaultMethod{
	public Object process()throws xMethodException{
		boolean valid=true;
		String why = "";
		String limit_string = this.getArguments("limit");
		String cursor = this.getArguments("cursor");
		int limit = 25;
		if(limit_string != null && limit_string.length() > 0){
			try{
				limit = Integer.parseInt(limit_string);
			}catch(Exception e){}
		}

		Element circles_element = this.setReturnArguments("circles", "");
    	DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
	    Query q = new Query(xCircle.class.getSimpleName());
	    q.addSort("visit", SortDirection.DESCENDING);
	    
    	PreparedQuery pq = ds.prepare(q);
    	Cursor decoded_cursor=null;
		if(cursor != null && cursor.length() > 0){
			 decoded_cursor = Cursor.fromWebSafeString(cursor);
		}
		QueryResultList<Entity> list_result = pq.asQueryResultList(decoded_cursor != null ? FetchOptions.Builder.withStartCursor(decoded_cursor).limit(limit) : FetchOptions.Builder.withDefaults().limit(limit));
    	int count = 0;
		for(Entity entity : list_result){
	        Element circle_element=(Element)circles_element.appendChild(this.createElement("circle"));
	        xCircle.xmlCircle(new xCircle(entity), circle_element);
	        ++count;
    	}
		if(count == limit && list_result != null){
			 this.setReturnArguments("cursor", list_result.getCursor().toWebSafeString());
		}
		circles_element.setAttribute("count", String.valueOf(count));
		this.setReturnArguments("valid", String.valueOf(valid));
		this.setReturnArguments("why", why);
		return this.getServiceContext().doNextProcess();
	}
}
