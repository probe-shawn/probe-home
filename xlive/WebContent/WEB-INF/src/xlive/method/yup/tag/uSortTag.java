package xlive.method.yup.tag;

import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Text;

import xlive.method.*;

public class uSortTag extends aDefaultMethod{
	@Override
	public Object process(JSONObject server_jso, String method_name, JSONObject client_jso, JSONObject return_jso) throws aMethodException, JSONException {
		String why="";
		boolean valid=true;
		JSONObject arg=client_jso.getJSONObject("arg");
		String key= arg.optString("key");
		String seq= arg.optString("seq");
		double sequence=0;	
		try{		
			sequence=Double.parseDouble(seq);
		}catch(Exception e){
			System.out.println("err="+e.getMessage());
		}
		Entity entity =null;
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		if(key != null && key.trim().length()>0){
			try{
				entity = ds.get(KeyFactory.stringToKey(key));
			}catch(EntityNotFoundException e){
				entity = null;}
		}
		if(entity != null && sequence>0){
			entity.setProperty("sort", Double.valueOf(sequence));
			ds.put(entity);
		}else{
			valid=false;
			why="EntityNotFound or sequenceError";
		}
		return_jso.put("valid", valid).put("why", why);
		return this.getServiceContext().doNextProcess();
	}
	
	public static void setSequence(String key,double seq){
		Entity entity =null;	
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		if(key != null && key.trim().length()>0){
			try{
				entity = ds.get(KeyFactory.stringToKey(key));
			}catch(EntityNotFoundException e){
				entity = null;}
		}
		if(entity != null && seq>0){
			entity.setProperty("sort",Double.valueOf(seq));
			ds.put(entity);
		}
	}
	
	
	
	
}
