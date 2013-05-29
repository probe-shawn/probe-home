package xlive.method.yup;

import xlive.xUtility;
import xlive.method.*;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.FilterOperator;

public class uLogin extends aDefaultMethod{
	
	@Override
	public Object process(JSONObject server_jso, String method_name, JSONObject client_jso, JSONObject return_jso) throws aMethodException, JSONException {
		String why="";
		boolean valid=true;
		JSONObject arg=client_jso.getJSONObject("arg");
		String account=arg.optString("account");
		String password=arg.optString("password");
		JSONObject member = new JSONObject();		
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		Query q = new Query("uMember");
	    	if(account != null && account.length()>0) {
	        	q.setFilter(new Query.FilterPredicate("account", FilterOperator.EQUAL, account));	        			
	    	}else{
	    		valid=false;why="account error";
	    		return_jso.put("valid", valid).put("why", why);
	    		return this.getServiceContext().doNextProcess();
	    	}
	    int count=0;
	    PreparedQuery pq = ds.prepare(q);
		for(Entity found : (Iterable<Entity>)pq.asIterable()) {
			xUtility.EntityToJSO(found, member);
	        ++count;
	    }
		if(count!=1){
	    		valid=false;
	    		why="account error";
		}	
	    	else{
	    		if(member.getString("password").equals(password)){
	    			member.remove("password");
	    			return_jso.put("member", member);    			
	    		}else{	    			
	    			valid=false;
	    			why="password error";
	    		}
	    	}
		return_jso.put("valid", valid).put("why", why);
		return this.getServiceContext().doNextProcess();
	}
	
	
}
