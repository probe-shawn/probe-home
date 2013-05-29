package xlive.method.yup.tag;

import xlive.method.*;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.SortDirection;

public class uListKeys extends aDefaultMethod{
	public Object process(JSONObject server_jso, String method_name, JSONObject client_jso, JSONObject return_jso)throws aMethodException, JSONException{
		String why="";
		boolean valid=true;
		JSONObject arg=client_jso.getJSONObject("arg");
		String pkey = arg.optString("product_key");
		String tkey = arg.optString("tag_key");
    	DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
    	JSONObject keys = new JSONObject();
    	return_jso.put("relkeys", keys);
	    Query q = new Query("uTagProduct");
	    if(pkey != null && pkey.trim().length() > 0){
	    	q.setFilter(new Query.FilterPredicate("product_key", FilterOperator.EQUAL, pkey));
	    }else if(tkey != null && tkey.trim().length() > 0){
	    	q.setFilter(new Query.FilterPredicate("tag_key", FilterOperator.EQUAL, tkey));
	    	q.addSort("sort", SortDirection.ASCENDING);
	    }else{
	    	valid = false;
	    	why = "key is empty";
			return_jso.put("valid", valid).put("why", why);
			return this.getServiceContext().doNextProcess();
	    }
	    int count=0;
	    PreparedQuery pq = ds.prepare(q);
		for(Entity found : (Iterable<Entity>)pq.asIterable()) {
			 if(pkey != null && pkey.trim().length() > 0){
				 keys.put((String)found.getProperty("tag_key"), (String)found.getProperty("product_key"));
			 }else{
				 keys.put((String)found.getProperty("product_key"), (String)found.getProperty("tag_key"));
			 }
	        ++count;
	    }
		return_jso.put("count",count).put("valid", valid).put("why", why);
		return this.getServiceContext().doNextProcess();

	}
}
