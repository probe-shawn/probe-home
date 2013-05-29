package xlive.method.yup;
import java.util.Date;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Text;

import xlive.xUtility;
import xlive.method.*;

public class uSetProduct extends aDefaultMethod{
		@Override
		public Object process(JSONObject server_jso, String method_name, JSONObject client_jso, JSONObject return_jso) throws aMethodException, JSONException {
			String why="";
			boolean valid=true;
			JSONObject product=client_jso.getJSONObject("arg").getJSONObject("product");
			String key= product.optString("key");
			Entity entity =null;
			DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
			if(key != null && key.trim().length()>0){
				try{
					entity = ds.get(KeyFactory.stringToKey(key));
				}catch(EntityNotFoundException e){entity = null;}
			}
			if(entity == null){
				entity = new Entity("uProduct");
				entity.setProperty("date", new Date());
			}
			JSOToEntity(product, entity);
			ds.put(entity);	
			//
			return_jso.put("key",KeyFactory.keyToString(entity.getKey())).put("valid", valid).put("why", why);
			return this.getServiceContext().doNextProcess();
		}
		public static void JSOToEntity(JSONObject jso, Entity entity) throws JSONException{
	    	 Iterator<?> keys = jso.keys();
	    	 while(keys.hasNext()){
	             String key = (String)keys.next();
	             Object value = jso.get(key);
	             if("key".equals(key)) continue;
	             if("size".equals(key)||"allergy".equals(key)||"property".equals(key)){
	            	 String str= (value != null) ? ((JSONObject)value).toString():"{}";
	            	 entity.setProperty(key, new Text(str.trim()));
	             }else if("setmenu".equals(key)){
	            	 String str= (value != null) ? ((JSONArray)value).toString():"[]";
	            	 entity.setProperty(key, new Text(str.trim()));
	             }else if("icon".equals(key)){
	            	 String str= "";
	            	 if(value != null && value instanceof JSONObject) str = ((JSONObject)value).toString();
	            	 else if(value != null && value instanceof String) str = (String) value;
	            	 entity.setProperty(key, new Text(str.trim()));	 
	             }else if("date1".equals(key)||"date2".equals(key)){
	            	String date_str=(String) value; 
	             	Date date=(date_str != null && date_str.trim().length() >= 8)? xUtility.parseDate(date_str):null;
	            	entity.setProperty(key, date);
	             }else if("desc".equals(key)){
	            	 String str= jso.optString(key, null);
	            	 entity.setProperty(key, new Text((str==null) ? "" : str));
	             }else if("sort".equals(key)){
	            	 entity.setProperty(key, Double.valueOf(jso.optString(key, "0")));
	             }else{
	            	 entity.setProperty(key, value);
	             }
	    	 }
	    }
}
