package xlive.method.yup;
import org.json.JSONException;
import org.json.JSONObject;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import xlive.method.*;

public class uSetStoreSaleRule extends aDefaultMethod{
		@Override
		public Object process(JSONObject server_jso, String method_name, JSONObject client_jso, JSONObject return_jso) throws aMethodException, JSONException {
			String why="";
			boolean valid=true;
			JSONObject storeproduct=client_jso.getJSONObject("arg").getJSONObject("storeproduct");
			String p_key= storeproduct.optString("product_key");
			String sid=storeproduct.optString("sid");
			DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
			Entity entity = new Entity(uSetStoreSaleRule.generateKey(sid, p_key));
			entity.setProperty("product_key",p_key);
			entity.setProperty("sid",sid);
			entity.setProperty("amount",storeproduct.optInt("amount",0));
			entity.setProperty("sellout",storeproduct.optInt("sellout",1));
			ds.put(entity);	
			return_jso.put("key",KeyFactory.keyToString(entity.getKey())).put("valid", valid).put("why", why);
			return this.getServiceContext().doNextProcess();
		}
		public static Key generateKey(String sid, String p_key){
			return KeyFactory.createKey("uStoreSaleRule",sid+p_key);
		}
		
}
