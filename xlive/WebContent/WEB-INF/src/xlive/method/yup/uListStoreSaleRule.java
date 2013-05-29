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

public class uListStoreSaleRule extends aDefaultMethod{
	public Object process(JSONObject server_jso, String method_name, JSONObject client_jso, JSONObject return_jso)throws aMethodException, JSONException{
	
	String why="";
	boolean valid=true;		
	JSONObject arg=client_jso.getJSONObject("arg");
	String sid = arg.optString("sid");
    	DatastoreService ds = DatastoreServiceFactory.getDatastoreService();    	
    	JSONArray products = new JSONArray();
    	return_jso.put("products",products);    	
	    Query q = new Query("uStoreSaleRule");
	    if(sid != null && sid.trim().length() > 0){
	    	q.setFilter(new Query.FilterPredicate("sid", FilterOperator.EQUAL, sid));
	    }else{
	    	valid = false;
	    	why = "sid is empty";
			return_jso.put("valid", valid).put("why", why);
			return this.getServiceContext().doNextProcess();
	    }
	    int count=0;
	    PreparedQuery pq = ds.prepare(q);
		for(Entity found : (Iterable<Entity>)pq.asIterable()) {				
			JSONObject product = new JSONObject();
			
			xUtility.EntityToJSO(found,product);
			/*
			String pkey=(String)found.getProperty("product_key");
			Long amount=(Long)found.getProperty("amount");
			Long sellout=(Long)found.getProperty("sellout");
			product.put("product_key", pkey);
			product.put("amount",amount);
			product.put("sellout",sellout);
			*/
			products.put(product);
			++count;		
	    }		
		return_jso.put("count",count).put("valid", valid).put("why", why);
		return this.getServiceContext().doNextProcess();

	}
}
