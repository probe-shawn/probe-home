package xlive.method.rst.menu;


import xlive.method.*;

import org.w3c.dom.*;

import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.QueryResultList;


public class xListHotMethod extends xDefaultMethod{
	public Object process()throws xMethodException{
		boolean valid=true;
		String why = "";
		String mall_fid=this.getArguments("mall-fid");
		String limit_string = this.getArguments("limit");
		String cursor = this.getArguments("cursor");
		int limit = 8;
		if(limit_string != null && limit_string.length() > 0){
			try{
				limit = Integer.parseInt(limit_string);
			}catch(Exception e){}
		}
		Element data = this.setReturnArguments("data", "");
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
    	Query q = new Query(xBom.class.getSimpleName());
    	/*
    	if(mall_fid != null && mall_fid.length() > 1) q.addFilter("mallFid", FilterOperator.EQUAL, mall_fid);
    	q.addFilter("type", FilterOperator.EQUAL, 1);
    	*/
    	if(mall_fid != null && mall_fid.length() > 1) {
        	q.setFilter(
        			CompositeFilterOperator.and(
        				new Query.FilterPredicate("mallFid", FilterOperator.EQUAL, mall_fid),
        				new Query.FilterPredicate("type", FilterOperator.EQUAL, 1)
        			)
        	);
    	}else q.setFilter(new Query.FilterPredicate("type", FilterOperator.EQUAL, 1));
    	
    	
    	q.addSort("date", SortDirection.DESCENDING);
    	PreparedQuery pq = ds.prepare(q);
    	Cursor decoded_cursor=null;
		if(cursor != null && cursor.length() > 0){
			 decoded_cursor = Cursor.fromWebSafeString(cursor);
		}
		QueryResultList<Entity> list_result = pq.asQueryResultList(decoded_cursor != null ? FetchOptions.Builder.withStartCursor(decoded_cursor).limit(limit) : FetchOptions.Builder.withDefaults().limit(limit));
    	int count = 0;
		for(Entity entity : list_result){
    		data.appendChild(xBom.listBomXml(new xBom(entity),0));
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
