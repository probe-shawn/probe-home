package xlive.method.yup.tag;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import xlive.method.*;

public class uBatchTagProduct extends aDefaultMethod{
		@Override
		public Object process(JSONObject server_jso, String method_name, JSONObject client_jso, JSONObject return_jso) throws aMethodException, JSONException {
			String why="";
			boolean valid=true;
			JSONArray del=client_jso.getJSONObject("arg").getJSONArray("del");
			ArrayList<Key> del_array = new ArrayList<Key>();
			for(int i = 0; i< del.length();++i){
				JSONObject obj = del.getJSONObject(i);
				String pkey= obj.optString("product_key");
				String tkey= obj.optString("tag_key");
	            del_array.add(uSetTagProduct.generateKey(tkey, pkey));
			}
			DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
			ds.delete(del_array);
			
			JSONArray ins=client_jso.getJSONObject("arg").getJSONArray("ins");
			ArrayList<Entity> ins_array = new ArrayList<Entity>();
			
			for(int i = 0; i< ins.length();++i){
				JSONObject obj = ins.getJSONObject(i);
				String pkey= obj.optString("product_key");
				String tkey= obj.optString("tag_key");
				Entity entity = new Entity(uSetTagProduct.generateKey(tkey, pkey));
				entity.setProperty("product_key", pkey);
				entity.setProperty("tag_key",tkey);
				entity.setProperty("sort", Double.valueOf(obj.optString("sort")));
				if(pkey.length()>0)//¨¾¤î¦³ªÅ¦r¦ê°O¿ý
					ins_array.add(entity);
			}
			ds.put(ins_array);
			return_jso.put("valid", valid).put("why", why);
			return this.getServiceContext().doNextProcess();
		}
}
