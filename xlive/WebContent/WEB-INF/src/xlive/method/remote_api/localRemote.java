package xlive.method.remote_api;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.tools.remoteapi.RemoteApiInstaller;
import com.google.appengine.tools.remoteapi.RemoteApiOptions;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;

import xlive.method.*;
import xlive.method.logger.xLogger;
import xlive.method.yup.uGetCompany;
import xlive.method.yup.uGetStore;
import xlive.method.yup.tag.uSetTagProduct;

public class localRemote extends aDefaultMethod{
		
		@Override
		public Object process(JSONObject server_jso, String method_name, JSONObject client_jso, JSONObject return_jso) throws aMethodException, JSONException {
			String why="";
			boolean valid=true;
			JSONObject arg=client_jso.getJSONObject("arg");
			String table_name = arg.optString("table");
			why +="\n"+ putTableEntity(table_name);
			/*
			why +="\n"+ putTableEntity("uCompany");
			why +="\n"+ putTableEntity("uStore");
			why +="\n"+ putTableEntity("uProperty");
			why +="\n"+ putTableEntity("uProduct");
			why +="\n"+ putTableEntity("uTag");
			why +="\n"+ putTableEntity("uTagProduct");
			//why +="\n"+ putTableEntity("uCatalogStore");
			//why +="\n"+ putTableEntity("uMember");
			//why +="\n"+ putTableEntity("uStoreProducts");
			 */
			return_jso.put("valid", valid).put("why", why);
			
			return this.getServiceContext().doNextProcess();
		}
	    private String putTableEntity(String table_name){
	    	String why = "";
			ArrayList<Entity> entity_array = getLocalDatastore(table_name);
			
	    	RemoteApiOptions options = new RemoteApiOptions().server("yupmenu.appspot.com", 443).credentials("probe.rst@gmail.com", "rst86719712");
	        RemoteApiInstaller installer = new RemoteApiInstaller();
	        try {
				installer.install(options);
			} catch (IOException e) {
				e.printStackTrace();
				xLogger.log(Level.SEVERE, "install failure:"+e.getMessage());
				return e.getMessage();
			}
	        try {
					ArrayList<Entity> entity_array2 = new ArrayList<Entity>();
					if(entity_array.size() > 0){
						DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
						for(int i = 0; i < entity_array.size();++i){
							Entity old_entity = entity_array.get(i);
							Entity new_entity = null;
							if("uCompany".equals(table_name)){
								new_entity = new Entity(uGetCompany.generateKey((String)old_entity.getProperty("cid")));
							}else if("uStore".equals(table_name)){
								new_entity = new Entity(uGetStore.generateKey((String)old_entity.getProperty("sid")));
							}else if("uProperty".equals(table_name)) {
								new_entity = new Entity(this.generateKey(old_entity.getKey()));
							}else if("uTag".equals(table_name)) {
								new_entity = new Entity(this.generateKey(old_entity.getKey()));
							}else if("uProduct".equals(table_name)) {
								new_entity = new Entity(this.generateKey(old_entity.getKey()));
							}
							////
							if(new_entity != null)new_entity.setPropertiesFrom(old_entity);
							////
							if("uProduct".equals(table_name)){
								String convert_kind = (String)new_entity.getProperty("_convert_kind");
								if(convert_kind != null && convert_kind.length() > 0){
									Key key = KeyFactory.createKey((String)new_entity.getProperty("_convert_kind"), (Long)new_entity.getProperty("_convert_id"));  
								    new_entity.setProperty("property_key",KeyFactory.keyToString(key));
								    new_entity.removeProperty("_convert_kind");
								    new_entity.removeProperty("_convert_id");
								}
							}else if("uTagProduct".equals(table_name)) {
								String convert_product_key_kind = (String)old_entity.getProperty("_convert_product_key_kind");
								Long convert_product_key_id = (Long)old_entity.getProperty("_convert_product_key_id");
								Key product_key = KeyFactory.createKey(convert_product_key_kind,convert_product_key_id);  
								
								String convert_tag_key_kind = (String)old_entity.getProperty("_convert_tag_key_kind");
								Long convert_tag_key_id = (Long)old_entity.getProperty("_convert_tag_key_id");
								Key tag_key = KeyFactory.createKey(convert_tag_key_kind,convert_tag_key_id);  
								
								new_entity = new Entity(uSetTagProduct.generateKey(KeyFactory.keyToString(tag_key), KeyFactory.keyToString(product_key)));
								new_entity.setPropertiesFrom(old_entity);
								new_entity.setProperty("tag_key",KeyFactory.keyToString(tag_key));
							    new_entity.setProperty("product_key",KeyFactory.keyToString(product_key));
							    new_entity.removeProperty("_convert_product_key_kind");
							    new_entity.removeProperty("_convert_product_key_id");
							    new_entity.removeProperty("_convert_tag_key_kind");
							    new_entity.removeProperty("_convert_tag_key_id");
						}
							///
							entity_array2.add(new_entity);
						}
						ds.put(entity_array2);
						why= "table :"+table_name+" entity : "+entity_array.size();
					}else{
						why= "table :"+table_name+" entity : 0 (null)";
					}
		        	
	        } finally {
	            installer.uninstall();
	        }
	        return why;
	    }
	    private Key generateKey(Key key) {  
	        Key parentKey = key.getParent();
	        if(parentKey == null){
	            return KeyFactory.createKey(key.getKind(), key.getId());  
	        }else{          
	            Key _newParentKey = generateKey(parentKey);         
	            return KeyFactory.createKey(_newParentKey, key.getKind(), key.getId());  
	        }  
	    }

	    
		    
	    private ArrayList<Entity> getLocalDatastore(String table_name)  {
	    	ArrayList<Entity> entity_array = new ArrayList<Entity>();
			DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
			Query q = new Query(table_name);
			PreparedQuery pq = ds.prepare(q);
			Iterable<Entity> entity_iterable =  pq.asIterable();
			for(Entity found : entity_iterable){
				if("uProduct".equals(table_name)){
					try{
						String property_key = (String)found.getProperty("property_key");
						Key key = KeyFactory.stringToKey(property_key);    
						found.setProperty("_convert_kind",(String)key.getKind());
						found.setProperty("_convert_id", key.getId());
					}catch(IllegalArgumentException e){
						found.setProperty("_convert_kind","");
					}catch(Exception e){
						found.setProperty("_convert_kind","");
					}
				}else if("uTagProduct".equals(table_name)){
					try{
						String product_key = (String)found.getProperty("product_key");
						Key key = KeyFactory.stringToKey(product_key);    
						found.setProperty("_convert_product_key_kind",(String)key.getKind());
						found.setProperty("_convert_product_key_id", key.getId());
					}catch(IllegalArgumentException e){
						found.setProperty("_convert_product_key_kind","");
					}catch(Exception e){
						found.setProperty("_convert_product_key_kind","");
					}
					try{
						String tag_key = (String)found.getProperty("tag_key");
						Key key = KeyFactory.stringToKey(tag_key);    
						found.setProperty("_convert_tag_key_kind",(String)key.getKind());
						found.setProperty("_convert_tag_key_id", key.getId());
					}catch(IllegalArgumentException e){
						found.setProperty("_convert_tag_key_kind","");
					}catch(Exception e){
						found.setProperty("_convert_tag_key_kind","");
					}
				}
				entity_array.add(found);
			}
	        return entity_array;
	    }

}
