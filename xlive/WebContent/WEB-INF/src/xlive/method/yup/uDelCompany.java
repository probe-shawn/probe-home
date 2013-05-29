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

public class uDelCompany extends aDefaultMethod{
	@Override
	public Object process(JSONObject server_jso, String method_name,JSONObject client_jso, JSONObject return_jso)throws aMethodException, JSONException {
		String why="";
		boolean valid=true;
		JSONObject arg=client_jso.getJSONObject("arg");
		String cid=arg.optString("cid");
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		ArrayList<Key> del_array = new ArrayList<Key>();
		del_array.add(uGetCompany.generateKey(cid));		
	    Query q = new Query("uStore");	  
	    q.setFilter(new Query.FilterPredicate("cid", FilterOperator.EQUAL, cid));
	    PreparedQuery pq = ds.prepare(q);
		for(Entity found : (Iterable<Entity>)pq.asIterable()){
			
			del_array.add(found.getKey());		
		}	
		ds.delete(del_array);
		return_jso.put("valid", valid).put("why", why);
		return this.getServiceContext().doNextProcess();
	}
}
