package xlive.method.yup;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import xlive.xUtility;
import xlive.method.*;
import xlive.method.yup.tag.uSetTagProduct;

import org.json.JSONArray;
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
import com.google.appengine.api.datastore.Text;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.SortDirection;

public class uBatchStoreSaleRule extends aDefaultMethod{
	public Object process(JSONObject server_jso, String method_name, JSONObject client_jso, JSONObject return_jso)throws aMethodException, JSONException{
	
		String why="";
		boolean valid=true;			
		String sid=client_jso.getJSONObject("arg").optString("sid");
		JSONArray products=client_jso.getJSONObject("arg").getJSONArray("products");
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		ArrayList<Entity> ins_array = new ArrayList<Entity>();		
		for(int i = 0; i< products.length();++i){
			JSONObject obj = products.getJSONObject(i);
			String product_key= obj.optString("product_key");	
			Entity entity = new Entity(uSetStoreSaleRule.generateKey(sid,product_key));
				
			entity.setProperty("sid", sid);
			entity.setProperty("amount", 0);
			JSOToEntity(obj, entity);
			ins_array.add(entity);
		}
		ds.put(ins_array);		
		return_jso.put("valid", valid).put("why", why);
		return this.getServiceContext().doNextProcess();

	}
	public static void JSOToEntity(JSONObject jso, Entity entity) throws JSONException{
   	 Iterator<?> keys = jso.keys();
   	 while(keys.hasNext()){
            String key = (String)keys.next();
            Object value = jso.get(key);
            if("key".equals(key)) continue;
            if("date1".equals(key)||"date2".equals(key)){
           	String date_str=(String) value; 
            	Date date=(date_str != null && date_str.trim().length() >= 8)? xUtility.parseDate(date_str):null;
           	entity.setProperty(key, date);           
            }else{
           	 entity.setProperty(key, value);
            }
   	 }
   }
}
