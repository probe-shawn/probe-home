package xlive.method.yup.tag;

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

public class uSetTag extends aDefaultMethod{
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
			entity = new Entity("uTag");
			entity.setProperty("date", new Date());
		}
		JSOToEntity(tag, entity);
		ds.put(entity);	
		return_jso.put("key",KeyFactory.keyToString(entity.getKey())).put("valid", valid).put("why", why);
		return this.getServiceContext().doNextProcess();		
	}
	public static void JSOToEntity(JSONObject jso, Entity entity) throws JSONException{
    	 Iterator<?> keys = jso.keys();
    	 while(keys.hasNext()){
             String key = (String)keys.next();
             Object value = jso.get(key);
             if("key".equals(key)) continue;
             if("icon".equals(key)){
            	 String str= "";
            	 if(value != null && value instanceof JSONObject) str = ((JSONObject)value).toString();
            	 else str ="{}";
            	 entity.setProperty(key, new Text(str.trim()));
             }else if("date".equals(key)){
            	String date_str=jso.optString(key, null); 
             	Date date=(date_str != null && date_str.trim().length() >= 8)? xUtility.parseDate(date_str):null;
            	entity.setProperty(key, date);
             }else if("sort".equals(key)){
            	entity.setProperty(key, Double.valueOf(jso.optString(key, "0")));
             }else{
            	 entity.setProperty(key, value);
             }
    	 }

    }
	public static String insertTag(String cid,String name,String icon,double seq,String type,String desc){
		
		Entity entity =null;
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();			
		entity = new Entity("uTag");
		entity.setProperty("cid", cid);
		entity.setProperty("icon", new Text(icon.trim()));
		entity.setProperty("name", name);
		entity.setProperty("sort", Double.valueOf(seq));
		entity.setProperty("type", type);
		entity.setProperty("desc", desc);
		entity.setProperty("date", new Date());
		ds.put(entity);	
		return KeyFactory.keyToString(entity.getKey());
							
	}
}
