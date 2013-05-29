package xlive.method.yup;

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
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.FilterOperator;

public class uListProduct extends aDefaultMethod{
	public Object process(JSONObject server_jso, String method_name, JSONObject client_jso, JSONObject return_jso)throws aMethodException, JSONException{
		String why="";
		boolean valid=true;
		JSONObject arg=client_jso.getJSONObject("arg");
		String cid = arg.optString("cid");
		String sid = arg.optString("sid");
    	DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
    	JSONArray prods = new JSONArray();
    	return_jso.put("products", prods);
    	
	    Query q = new Query("uProduct");
    	if(cid != null && cid.length()>0 && sid != null && sid.length()>0) {
        	q.setFilter(
        			CompositeFilterOperator.or(
        				new Query.FilterPredicate("sid", FilterOperator.EQUAL, sid),
        				new Query.FilterPredicate("sid", FilterOperator.EQUAL, cid)
        			)
        	);
    	}else q.setFilter(new Query.FilterPredicate("cid", FilterOperator.EQUAL, cid));
	    //q.addSort("sort", SortDirection.ASCENDING);
	    int count=0;
	    PreparedQuery pq = ds.prepare(q);
		for(Entity found : (Iterable<Entity>)pq.asIterable()) {
			JSONObject prod = new JSONObject();	
			prods.put(prod);
			uGetProduct.EntityToJSO(found,prod);
			prod.put("key", KeyFactory.keyToString(found.getKey()));
	        ++count;
	    }
		return_jso.put("count",count).put("valid", valid).put("why", why);
		return this.getServiceContext().doNextProcess();

	}
}
