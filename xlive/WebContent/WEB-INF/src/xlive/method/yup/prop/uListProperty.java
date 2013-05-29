package xlive.method.yup.prop;

import xlive.method.*;

import org.json.JSONArray;
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

public class uListProperty extends aDefaultMethod{
	public Object process(JSONObject server_jso, String method_name, JSONObject client_jso, JSONObject return_jso)throws aMethodException, JSONException{
		String why="";
		boolean valid=true;
		JSONObject arg=client_jso.getJSONObject("arg");
		String cid = arg.optString("cid");
    	DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
    	
    	JSONArray props = new JSONArray();
    	return_jso.put("properties", props);
    	/*
    	JSONObject props = new JSONObject();
    	return_jso.put("properties", props);
    	*/
	    Query q = new Query("uProperty");
	    if(cid != null && cid.length()>0) q.setFilter(new Query.FilterPredicate("cid", FilterOperator.EQUAL,cid));
	    q.addSort("sort", SortDirection.ASCENDING);
	    
	    int count=0;
	    PreparedQuery pq = ds.prepare(q);
		for(Entity found : (Iterable<Entity>)pq.asIterable()) {
			JSONObject prop = new JSONObject();
			//prods.put("property", prop);
			//props.put(KeyFactory.keyToString(found.getKey()), prop);
			props.put(prop);
			uGetProperty.EntityToJSO(found,prop);
			prop.put("key", KeyFactory.keyToString(found.getKey()));
	        ++count;
	    }
		return_jso.put("count",count).put("valid", valid).put("why", why);
		return this.getServiceContext().doNextProcess();

	}
}
