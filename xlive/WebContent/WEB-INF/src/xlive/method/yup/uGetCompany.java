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

public class uGetCompany extends aDefaultMethod{
	
	@Override
	public Object process(JSONObject server_jso, String method_name, JSONObject client_jso, JSONObject return_jso) throws aMethodException, JSONException{
	
		String why="";
		boolean valid=true;
		JSONObject arg=client_jso.getJSONObject("arg");
		String cid=arg.optString("cid");
		
		JSONObject company = new JSONObject();
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		Entity entity = null;
				
		if(cid != null && cid.trim().length()>0)
			try {
				entity = ds.get(uGetCompany.generateKey(cid));
			} catch (EntityNotFoundException e) {
				entity =null;
			}
		
	    	if(entity != null){
	    		EntityToJSO(entity, company);
	    	}else{    		
	    			valid= false;
	    			why=" cid :"+cid+" not found";    		
	    	}
		return_jso.put("company", company).put("valid", valid).put("why", why);
		
		return this.getServiceContext().doNextProcess();
	}
	public static Key generateKey(String cid){
		return KeyFactory.createKey("uCompany", cid);
	}
	public static void EntityToJSO(Entity entity, JSONObject jso) throws JSONException{
		xUtility.EntityToJSO(entity, jso);
	}
}
