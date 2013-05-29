package xlive.method.yup;

import xlive.xUtility;
import xlive.method.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;

public class uListCompanyStores extends aDefaultMethod{
	public Object process(JSONObject server_jso, String method_name, JSONObject client_jso, JSONObject return_jso)throws aMethodException, JSONException{
	
		String why="";
		boolean valid=true;		
		JSONObject arg=client_jso.getJSONObject("arg");
		String cid = arg.optString("cid");
    	DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
    	
    	JSONArray stores = new JSONArray();
    	return_jso.put("stores", stores);    	
	    Query q = new Query("uStore");
	    if(cid != null && cid.trim().length() > 0){
	    	q.setFilter(new Query.FilterPredicate("cid", FilterOperator.EQUAL,cid));
	    }else{
	    	valid = false;
	    	why = "cid is empty";
			return_jso.put("valid", valid).put("why", why);
			return this.getServiceContext().doNextProcess();
	    }
	    int count=0;	    
	    PreparedQuery pq = ds.prepare(q);
		for(Entity found : (Iterable<Entity>)pq.asIterable()) {				
			JSONObject store = new JSONObject();
				stores.put(store);
				xUtility.EntityToJSO(found,store);
				++count;			
	    }
		return_jso.put("count",count).put("valid", valid).put("why", why);
		return this.getServiceContext().doNextProcess();
	}
}
