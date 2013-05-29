package xlive.method.yup.tag;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import xlive.method.*;

public class uSetTagProduct extends aDefaultMethod{
		@Override
		public Object process(JSONObject server_jso, String method_name, JSONObject client_jso, JSONObject return_jso) throws aMethodException, JSONException {
			String why="";
			boolean valid=true;
			JSONObject tag=client_jso.getJSONObject("arg").getJSONObject("tag");
			String key= tag.optString("key");
			Entity entity =null;
			DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
			if(key != null && key.trim().length()>0){
				try{
					entity = ds.get(KeyFactory.stringToKey(key));
				}catch(EntityNotFoundException e){entity = null;}
			}
			if(entity == null){
				entity = new Entity("uTagProduct");
			}
			JSOToEntity(tag, entity);
			ds.put(entity);	
			//
			return_jso.put("key",KeyFactory.keyToString(entity.getKey())).put("valid", valid).put("why", why);
			return this.getServiceContext().doNextProcess();
		}
		public static Key generateKey(String tag_key, String product_key){
			return KeyFactory.createKey("uTagProduct", tag_key+product_key);
		}
		public static void JSOToEntity(JSONObject jso, Entity entity) throws JSONException{
	    	 Iterator<?> keys = jso.keys();
	    	 while(keys.hasNext()){
	             String key = (String)keys.next();
	             Object value = jso.get(key);
	             if("key".equals(key)) continue;
	             if("sort".equals(key)){
	            	 entity.setProperty(key, Double.valueOf(jso.optString(key, "0")));
	             }else{
	            	 entity.setProperty(key, value);
	             }
	    	 }
	    }
}
