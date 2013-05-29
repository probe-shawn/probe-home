package xlive.method.yup;

import xlive.xUtility;
import xlive.method.*;
import xlive.method.rst.mall.xMallDetail;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

public class uGetStore extends aDefaultMethod{
	
	@Override
	public Object process(JSONObject server_jso, String method_name, JSONObject client_jso, JSONObject return_jso) throws aMethodException, JSONException {
		String why="";
		boolean valid=true;
		JSONObject arg=client_jso.getJSONObject("arg");
		String sid=arg.optString("sid");
		JSONObject store = new JSONObject();
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
    	Entity entity = null;
		try {
			entity = ds.get(uGetStore.generateKey(sid));
		} catch (EntityNotFoundException e) {
		}
    	if(entity != null){
    		EntityToJSO(entity, store);
    	}else{
    		store = FetchFromImall(sid);
    		if(store != null){
    			Entity store_entity = new Entity(uGetStore.generateKey(sid));
    			uSetStore.JSOToEntity(store, store_entity);
    			ds.put(store_entity);
    		}else{
    			valid= false;
    			why=" sid :"+sid+" not found";
    		}
    	}
    	
    	if(arg.optBoolean("company") && store!=null){
 			arg.put("cid",store.optString("cid",sid) );
			this.processWebObjectMethod("yup/company", "get");
	}
    	
		return_jso.put("store", store).put("valid", valid).put("why", why);
		
		return this.getServiceContext().doNextProcess();
	}
	public static Key generateKey(String sid){
		return KeyFactory.createKey("uStore", sid);
	}
	public static void EntityToJSO(Entity entity, JSONObject jso) throws JSONException{
		xUtility.EntityToJSO(entity, jso);
	}
	private JSONObject FetchFromImall(String fid) throws JSONException{
		Entity entity = null;
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		if(entity == null && fid != null && fid.trim().length()>0){
			try{
				entity=ds.get(xMallDetail.generateKey(fid));
			}catch(EntityNotFoundException e){entity = null;}
		}
		if(entity != null){
			JSONObject store = new JSONObject();
			xMallDetail mall = new xMallDetail(entity);
			store.put("cid",fid);
			store.put("sid",fid);
			//store.put("fid",fid);
			store.put("name",mall.getName());
			//store.put("store_name",mall.getStoreName());
			store.put("sub_name",mall.getName());
			store.put("addr",mall.getAddr());
			store.put("phone",mall.getPhone());
			store.put("desc",mall.getDesc());
			store.put("latitude",mall.getLatitude());
			store.put("longitude",mall.getLongitude());
			String icon = mall.getMallBgIcon();
			icon = icon.split(";")[0];
			if(icon != null) icon = "{'url':'"+icon+"'}";
			else icon = "{}";
			store.put("icon",new JSONObject(icon));
			return store;
		}
		return null;
	}

}
