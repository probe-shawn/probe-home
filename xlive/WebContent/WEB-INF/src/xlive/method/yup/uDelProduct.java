package xlive.method.yup;
import java.util.ArrayList;

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
import com.google.appengine.api.datastore.Key;
import xlive.method.*;

public class uDelProduct extends aDefaultMethod{
	@Override
	public Object process(JSONObject server_jso, String method_name,JSONObject client_jso, JSONObject return_jso)throws aMethodException, JSONException {
		String why="";
		boolean valid=true;
		JSONObject arg=client_jso.getJSONObject("arg");
		String key=arg.optString("key");
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		ArrayList<Key> del_array = new ArrayList<Key>();
		JSONArray relation_tags = new JSONArray();
		del_array.add(KeyFactory.stringToKey(key));		
	    Query q = new Query("uTagProduct");	  
	    q.setFilter(new Query.FilterPredicate("product_key", FilterOperator.EQUAL, key));
	    PreparedQuery pq = ds.prepare(q);
		for(Entity found : (Iterable<Entity>)pq.asIterable()){	
			relation_tags.put(found.getProperty("tag_key"));
			del_array.add(found.getKey());		
		}	
		ds.delete(del_array);
		return_jso.put("relation_tags", relation_tags).put("valid", valid).put("why", why);
		return this.getServiceContext().doNextProcess();
	}
}
