package xlive.method.yup.tag;

import java.util.ArrayList;

import xlive.xUtility;
import xlive.method.*;
import xlive.method.yup.uSetStoreSaleRule;

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

public class uListTagProducts extends aDefaultMethod{
	public Object process(JSONObject server_jso, String method_name, JSONObject client_jso, JSONObject return_jso)throws aMethodException, JSONException{
	
		String why="";
		boolean valid=true;		
		JSONObject arg=client_jso.getJSONObject("arg");
		String tkey = arg.optString("tag_key");
		String sid = arg.optString("sid");//若要取得SALE RULE則需傳入此參數
    	DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
    	
    	JSONArray products = new JSONArray();
    	return_jso.put("products", products);
    	
	    Query q = new Query("uTagProduct");
	    if(tkey != null && tkey.trim().length() > 0){
	    	q.setFilter(new Query.FilterPredicate("tag_key", FilterOperator.EQUAL, tkey));
	    	q.addSort("sort", SortDirection.ASCENDING);
	    }else{
	    	valid = false;
	    	why = "key is empty";
			return_jso.put("valid", valid).put("why", why);
			return this.getServiceContext().doNextProcess();
	    }
	    int count=0;
	    double last_num=0;
	    double d1=0,d2=Math.pow(2,54);
	    ArrayList<Entity> upd_array = new ArrayList<Entity>();
	    PreparedQuery pq = ds.prepare(q);
		for(Entity found : (Iterable<Entity>)pq.asIterable()) {				
			JSONObject product = new JSONObject();
			String pkey=(String)found.getProperty("product_key");
			Entity entity,entity2;
			try {
				entity = ds.get(  KeyFactory.stringToKey( pkey));
			} catch (EntityNotFoundException e) {	
				 entity=null;
			}
			if(sid!=null && sid.length()>0){				
				try {
					entity2 = ds.get(uSetStoreSaleRule.generateKey(sid, pkey));
				} catch (EntityNotFoundException e) {	
					 entity2=null;
				}
				if(entity2!=null){
					JSONObject salerule = new JSONObject();
					xUtility.EntityToJSO(entity2,salerule);
					product.put("salerule", salerule);
				}
			}	
			if(entity!=null){
				products.put(product);
				xUtility.EntityToJSO(entity,product);
				try{
					d1=((Double)found.getProperty("sort")).doubleValue();				
					if(d1>=(d2-1)){ //整大值為2的54次方，因之前可能有﹣1之值，故先用﹣1值替代
						d1=(last_num+d2)/2;			
						found.setProperty("sort", Double.valueOf(d1));
						upd_array.add(found);
					}
					last_num=d1;
				}catch(Exception e2){
					System.out.println(e2.getMessage());
					d1=d2;
					found.setProperty("sort", Double.valueOf(d1));
					upd_array.add(found);
				}
				product.put("sort", d1);
				product.put("key",  pkey);
				++count;				
			}
	    }
		if(upd_array.size()>0)
			ds.put(upd_array);
		return_jso.put("count",count).put("valid", valid).put("why", why);
		return this.getServiceContext().doNextProcess();

	}
}
