package xlive.method.yup;

import java.util.ArrayList;

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
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.SortDirection;

public class uBatchStoreCatalogs extends aDefaultMethod{
	public Object process(JSONObject server_jso, String method_name, JSONObject client_jso, JSONObject return_jso)throws aMethodException, JSONException{
	
		String why="";
		boolean valid=true;		
		JSONArray del=client_jso.getJSONObject("arg").getJSONArray("del");
		String sid=client_jso.getJSONObject("arg").optString("sid");
		String store_key=KeyFactory.keyToString(uGetStore.generateKey(sid));
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		ArrayList<Key> del_array = new ArrayList<Key>();		
		for(int i = 0; i< del.length();++i){
			JSONObject obj = del.getJSONObject(i);
			String catalog_key= obj.optString("catalog_key");			
            del_array.add(uSetCatalogStore.generateKey(catalog_key, store_key));            
		}		
		ds.delete(del_array);		
		JSONArray ins=client_jso.getJSONObject("arg").getJSONArray("ins");
		ArrayList<Entity> ins_array = new ArrayList<Entity>();		
		for(int i = 0; i< ins.length();++i){
			JSONObject obj = ins.getJSONObject(i);
			String catalog_key= obj.optString("catalog_key");	
			Entity entity = new Entity(uSetCatalogStore.generateKey(catalog_key, store_key));
			entity.setProperty("catalog_key", catalog_key);
			entity.setProperty("store_key",store_key);
			entity.setProperty("sort", Double.valueOf(Math.pow(2,54)));
			ins_array.add(entity);
		}
		ds.put(ins_array);
		
		return_jso.put("valid", valid).put("why", why);
		return this.getServiceContext().doNextProcess();

	}
}
