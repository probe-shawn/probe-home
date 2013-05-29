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

public class uSortTagProduct extends aDefaultMethod{
		@Override
		public Object process(JSONObject server_jso, String method_name, JSONObject client_jso, JSONObject return_jso) throws aMethodException, JSONException {
			String why="";
			boolean valid=true;
			DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
			JSONObject arg=client_jso.getJSONObject("arg");			
			String pkey= arg.optString("product_key");
			String tkey= arg.optString("tag_key");
			Entity entity = new Entity(uSetTagProduct.generateKey(tkey, pkey));
			entity.setProperty("product_key", pkey);
			entity.setProperty("tag_key",tkey);
			entity.setProperty("sort", Double.valueOf(arg.optString("sort")));
			ds.put(entity);
			return_jso.put("valid", valid).put("why", why);
			return this.getServiceContext().doNextProcess();
		}
}
