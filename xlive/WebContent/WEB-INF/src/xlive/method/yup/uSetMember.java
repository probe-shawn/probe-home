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
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Text;
import com.google.appengine.api.datastore.Query.FilterOperator;

import xlive.xUtility;
import xlive.method.*;

public class uSetMember extends aDefaultMethod{
		@Override
		public Object process(JSONObject server_jso, String method_name, JSONObject client_jso, JSONObject return_jso) throws aMethodException, JSONException {
			String why="";
			boolean valid=true;
			JSONObject member=client_jso.getJSONObject("arg").getJSONObject("member");
			String yid= member.optString("yid");
			String account=member.optString("account");
			Entity entity =null;
			DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
			if(yid != null && yid.trim().length() > 6 && !"0".equals(yid)){
				try{
					entity = ds.get(uGetMember.generateKey(yid));
				}catch(EntityNotFoundException e){entity = null;}
			} 
			if(account != null && account.trim().length()>0){
				Query q = new Query("uMember");		  
		        	q.setFilter(new Query.FilterPredicate("account", FilterOperator.EQUAL,account));	        			
		        	PreparedQuery pq = ds.prepare(q);
		        	int count=0;
		    		for(Entity found : (Iterable<Entity>)pq.asIterable()) {
		    			if(yid != null && yid.trim().length()>0 && yid.equals((String)found.getProperty("yid"))){
		    				//...
		    			}else count++;
		    	    }
		    		if(count>0){
		    			return_jso.put("valid", false).put("why","account_in_use");
		    			return this.getServiceContext().doNextProcess();
		    		}		    		
			}
			if(entity == null){
				yid=String.valueOf((new Date()).getTime());
				entity = new Entity(uGetMember.generateKey(yid));
				entity.setProperty("date", new Date());
				entity.setProperty("yid", yid);
				member.remove("date");
				member.remove("yid");
			}
			JSOToEntity(member, entity);
			ds.put(entity);	
			//
			return_jso.put("key",KeyFactory.keyToString(entity.getKey())).put("yid",(String)entity.getProperty("yid")).put("valid", valid).put("why", why);
			return this.getServiceContext().doNextProcess();
		}
		public static void JSOToEntity(JSONObject jso, Entity entity) throws JSONException{
	    	 Iterator<?> keys = jso.keys();
	    	 while(keys.hasNext()){
	             String key = (String)keys.next();
	             Object value = jso.get(key);
	             if("key".equals(key)) continue;
	             if("icon".equals(key)){
	            	 String str= (value != null) ? ((JSONObject)value).toString():"{}";
	            	 entity.setProperty(key, new Text(str.trim()));
	             }else if("date".equals(key) || "birthday".equals(key)){
	            	String date_str=(String) value; 
	             	Date date=(date_str != null && date_str.trim().length() >= 8)? xUtility.parseDate(date_str):null;
	            	entity.setProperty(key, date);
	             }else if("desc".equals(key)){
	            	 String str= jso.optString(key, null);
	            	 entity.setProperty(key, new Text((str==null) ? "" : str));
	             }else if("stores".equals(key)){
	            	 String str= (value != null) ? ((JSONArray)value).toString():"[]";
	            	 entity.setProperty(key, new Text(str.trim()));
	             }else{
	            	 entity.setProperty(key, value);
	             }
	    	 }
	    }
}
