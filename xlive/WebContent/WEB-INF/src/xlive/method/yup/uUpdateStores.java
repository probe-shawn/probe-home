package xlive.method.yup;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Text;
import xlive.method.*;

public class uUpdateStores extends aDefaultMethod{
		@Override
		public Object process(JSONObject server_jso, String method_name, JSONObject client_jso, JSONObject return_jso) throws aMethodException, JSONException {
			String why="";
			boolean valid=true;
			JSONObject arg=client_jso.getJSONObject("arg");
			String yid= arg.optString("yid");
			String stores=arg.optJSONArray("stores").toString();
			Entity entity =null;
			DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
			if(yid != null && yid.trim().length()>0){
				try{
					entity = ds.get(uGetMember.generateKey(yid));
					entity.setProperty("stores", new Text(stores));
					ds.put(entity);	
				}catch(EntityNotFoundException e){
					valid =false;
					why = "key not found : "+yid;
				}
			} 
			return_jso.put("valid", valid).put("why", why);
			return this.getServiceContext().doNextProcess();
		}
}
