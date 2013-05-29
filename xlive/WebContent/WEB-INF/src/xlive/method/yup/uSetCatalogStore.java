package xlive.method.yup;
import org.json.JSONException;
import org.json.JSONObject;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import xlive.method.*;

public class uSetCatalogStore extends aDefaultMethod{
		@Override
		public Object process(JSONObject server_jso, String method_name, JSONObject client_jso, JSONObject return_jso) throws aMethodException, JSONException {
			String why="";
			boolean valid=true;
			JSONObject catalogstore=client_jso.getJSONObject("arg").getJSONObject("catalogstore");
			String catalog_key= catalogstore.optString("catalog_key");
			String store_key=KeyFactory.keyToString(uGetStore.generateKey(catalogstore.optString("sid")));
			DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
			Entity entity = new Entity(uSetCatalogStore.generateKey(catalog_key, store_key));
			entity.setProperty("catalog_key",catalog_key);
			entity.setProperty("store_key",store_key);
			entity.setProperty("sort", Double.valueOf(Math.pow(2,54)));
			ds.put(entity);	
			//
			return_jso.put("key",KeyFactory.keyToString(entity.getKey())).put("valid", valid).put("why", why);
			return this.getServiceContext().doNextProcess();
		}
		public static Key generateKey(String catalog_key, String store_key){
			return KeyFactory.createKey("uCatalogStore", catalog_key+store_key);
		}
		
}
