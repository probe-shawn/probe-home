package xlive.method.yup;

import xlive.xUtility;
import xlive.method.*;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

public class uGetMember extends aDefaultMethod{
	
	@Override
	public Object process(JSONObject server_jso, String method_name, JSONObject client_jso, JSONObject return_jso) throws aMethodException, JSONException {
		String why="";
		boolean valid=true;
		JSONObject arg=client_jso.getJSONObject("arg");
		String yid=arg.optString("yid");
		JSONObject member = new JSONObject();
		return_jso.put("member", member);
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
    	Entity entity = null;
		try {
			entity = ds.get(uGetMember.generateKey(yid));
		} catch (EntityNotFoundException e) {
		}
    	if(entity != null){
    		EntityToJSO(entity, member);
    	}
    	member.remove("password");
		return_jso.put("valid", valid).put("why", why);
		return this.getServiceContext().doNextProcess();
	}
	public static Key generateKey(String yid){
		return KeyFactory.createKey("uMember", yid);
	}
	public static void EntityToJSO(Entity entity, JSONObject jso) throws JSONException{
		xUtility.EntityToJSO(entity, jso);
	}
}
