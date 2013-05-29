package xlive.method.yup.tag;

import xlive.xUtility;
import xlive.method.*;

import org.json.JSONException;
import org.json.JSONObject;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.KeyFactory;

public class uGetTagProduct extends aDefaultMethod{
	
	@Override
	public Object process(JSONObject server_jso, String method_name, JSONObject client_jso, JSONObject return_jso) throws aMethodException, JSONException {
		String why="";
		boolean valid=true;
		JSONObject arg=client_jso.getJSONObject("arg");
		String key=arg.optString("key");
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		JSONObject tagproduct = new JSONObject();
    	Entity entity;
		try {
			entity = ds.get(KeyFactory.stringToKey(key));
			EntityToJSO(entity, tagproduct);
			tagproduct.put("key", KeyFactory.keyToString(entity.getKey()));
		} catch (EntityNotFoundException e) {
			valid = false;
			why = "key :"+key+" not found";
		}
		return_jso.put("tagproduct", tagproduct).put("valid", valid).put("why", why);
		return this.getServiceContext().doNextProcess();
	}
	public static void EntityToJSO(Entity entity, JSONObject jso) throws JSONException{
		xUtility.EntityToJSO(entity, jso);
	}

}
