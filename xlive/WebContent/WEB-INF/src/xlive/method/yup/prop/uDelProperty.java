package xlive.method.yup.prop;

import org.json.JSONException;
import org.json.JSONObject;

import xlive.method.*;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.KeyFactory;

public class uDelProperty extends aDefaultMethod{
	@Override
	public Object process(JSONObject server_jso, String method_name,JSONObject client_jso, JSONObject return_jso)throws aMethodException, JSONException {
		String why="";
		boolean valid=true;
		JSONObject arg=client_jso.getJSONObject("arg");
		String key=arg.optString("key");
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		ds.delete(KeyFactory.stringToKey(key));
		return_jso.put("valid", valid).put("why", why);
		return this.getServiceContext().doNextProcess();
	}

}
