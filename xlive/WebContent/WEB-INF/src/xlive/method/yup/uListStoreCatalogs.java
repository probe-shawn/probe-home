package xlive.method.yup;

import java.util.ArrayList;

import xlive.xUtility;
import xlive.method.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.SortDirection;

public class uListStoreCatalogs extends aDefaultMethod{
	public Object process(JSONObject server_jso, String method_name, JSONObject client_jso, JSONObject return_jso)throws aMethodException, JSONException{
	
		String why="";
		boolean valid=true;		
		JSONObject arg=client_jso.getJSONObject("arg");
		String sid = arg.optString("sid");
    	DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
    	
    	JSONArray catalogs = new JSONArray();
    	return_jso.put("catalogs", catalogs);
    	
	    Query q = new Query("uCatalogStore");
	    if(sid != null && sid.trim().length() > 0){
	    	q.setFilter(new Query.FilterPredicate("store_key", FilterOperator.EQUAL,KeyFactory.keyToString(uGetStore.generateKey(sid))));
	   
	    }else{
	    	valid = false;
	    	why = "key is empty";
			return_jso.put("valid", valid).put("why", why);
			return this.getServiceContext().doNextProcess();
	    }
	    int count=0;
	   
	    PreparedQuery pq = ds.prepare(q);
		for(Entity found : (Iterable<Entity>)pq.asIterable()) {				
			JSONObject catalog = new JSONObject();
			String ckey=(String)found.getProperty("catalog_key");
			catalog.put("catalog_key",ckey);
			catalogs.put(catalog);
				++count;				
			
	    }
		
		return_jso.put("count",count).put("valid", valid).put("why", why);
		return this.getServiceContext().doNextProcess();

	}
}
