package xlive.method.rst.barter;

import java.util.ArrayList;

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
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.SortDirection;

public class xListHotMethod extends xDefaultMethod{
	
	public Object process()throws xMethodException{
		boolean valid=true;
		String why = "";
		String limit_string = this.getArguments("limit");
		String owner_fid= this.getArguments("owner-fid");
		String cursor = this.getArguments("cursor");
		int limit = 8;
		if(limit_string != null && limit_string.length() > 0){
			try{
				limit = Integer.parseInt(limit_string);
			}catch(Exception e){}
		}
		Element data = this.setReturnArguments("barters", "");
    	DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
    	
    	Query q = new Query(xBarter.class.getSimpleName());
    	/*
    	q.addFilter("removed", FilterOperator.EQUAL, 0);
    	q.addFilter("type", FilterOperator.EQUAL, 0);
    	if(owner_fid != null && owner_fid.trim().length() > 0) q.addFilter("ownerFid", FilterOperator.EQUAL, owner_fid);
    	*/
		ArrayList<Query.Filter> filters = new ArrayList<Query.Filter>();
    	filters.add(new Query.FilterPredicate("removed", FilterOperator.EQUAL, 0));
    	filters.add(new Query.FilterPredicate("type", FilterOperator.EQUAL, 0));
    	if(owner_fid != null && owner_fid.trim().length() > 0) filters.add(new Query.FilterPredicate("ownerFid", FilterOperator.EQUAL, owner_fid));
    	q.setFilter(new Query.CompositeFilter(CompositeFilterOperator.AND, filters));
    	
    	q.addSort("date",SortDirection.DESCENDING);
    	PreparedQuery pq = ds.prepare(q);
    	Cursor decoded_cursor=null;
		if(cursor != null && cursor.length() > 0){
			 decoded_cursor = Cursor.fromWebSafeString(cursor);
		}
		QueryResultList<Entity> list_result = pq.asQueryResultList(decoded_cursor != null ? FetchOptions.Builder.withStartCursor(decoded_cursor).limit(limit) : FetchOptions.Builder.withDefaults().limit(limit));
    	int count = 0;
		for(Entity entity : list_result){
			data.appendChild(xBarter.barterXml(new xBarter(entity),false));
    		++count;
    	}
		if(count == limit && list_result != null){
			 this.setReturnArguments("cursor", list_result.getCursor().toWebSafeString());
		}
    	this.setReturnArguments("valid", String.valueOf(valid));
		this.setReturnArguments("why", why);
		return this.getServiceContext().doNextProcess();
		
	}
	
}
