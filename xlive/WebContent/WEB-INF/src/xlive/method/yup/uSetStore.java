package xlive.method.yup;
import java.util.Date;
import java.util.Iterator;

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

public class uSetStore extends aDefaultMethod{
		@Override
		public Object process(JSONObject server_jso, String method_name, JSONObject client_jso, JSONObject return_jso) throws aMethodException, JSONException {
			String why="";
			boolean valid=true;
			JSONObject store=client_jso.getJSONObject("arg").getJSONObject("store");
			String sid= store.optString("sid");
			Entity entity =null;
			DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
			if(sid != null && sid.trim().length()>0){
				try{
					entity = ds.get(uGetStore.generateKey(sid));
				}catch(EntityNotFoundException e){entity = null;}
			}			
			if(entity == null){
				entity = new Entity(uGetStore.generateKey(sid));
				entity.setProperty("date", new Date());
			}
			JSOToEntity(store, entity);
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
	             if("icon".equals(key)||"admins".equals(key) ||"left_icon".equals(key) ||"right_icon".equals(key) ||"logo_icon".equals(key)){
	            	 String str= (value != null) ? ((JSONObject)value).toString():"{}";
	            	 entity.setProperty(key, new Text(str.trim()));
	             }else if("date".equals(key)){
	            	String date_str=(String) value; 
	             	Date date=(date_str != null && date_str.trim().length() >= 8)? xUtility.parseDate(date_str):null;
	            	entity.setProperty(key, date);
	             }else if("desc".equals(key)){
	            	 String str= jso.optString(key, null);
	            	 entity.setProperty(key, new Text((str==null) ? "" : str));
	             }else if("sort".equals(key)||"latitude".equals(key)||"longitude".equals(key)){
	            	entity.setProperty(key, Double.valueOf(jso.optString(key, "0")));
	             }else{
	            	 entity.setProperty(key, value);
	             }
	    	 }
	    }
}
