package xlive.method.yup.tag;

import xlive.xUtility;
import xlive.method.*;
import xlive.method.rst.mall.xMallDetail;
import xlive.method.rst.menu.xBom;
import xlive.method.upos.tag.uSetProductTagMethod;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Element;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.SortDirection;

public class uListTag extends aDefaultMethod{
	public Object process(JSONObject server_jso, String method_name, JSONObject client_jso, JSONObject return_jso)throws aMethodException, JSONException{
		String why="";
		boolean valid=true;
		JSONObject arg=client_jso.getJSONObject("arg");
		String cid = arg.optString("cid");
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();    	
		JSONArray tags = new JSONArray();
    	return_jso.put("tags", tags);
	    Query q = new Query("uTag");
	    if(cid != null && cid.length()>0)
	    	q.setFilter(new Query.FilterPredicate("cid", FilterOperator.EQUAL,cid));
	    q.addSort("sort", SortDirection.ASCENDING);
	    int count=0;
	    PreparedQuery pq=null;
	    try{
		    pq = ds.prepare(q);
		    double last_num=0;
		    double d1=0,d2=Math.pow(2,54);
			for(Entity found : (Iterable<Entity>)pq.asIterable()) {
				++count;
				JSONObject tag = new JSONObject();
				tags.put(tag);
				xUtility.EntityToJSO(found,tag);
				tag.put("key", KeyFactory.keyToString(found.getKey()));					
				try{
					d1=((Double)found.getProperty("sort")).doubleValue();				
					if(d1>=d2){
						d1=(last_num+d2)/2;
						tag.put("sort",String.valueOf(d1));
						uSortTag.setSequence(tag.getString("key"),d1);
					}
					last_num=d1;
				}catch(Exception e){
					System.out.println(e.getMessage());
					d1=d2;
				}
		    }
	    }catch(Exception e1){
	    	System.out.println(e1.getMessage());
	    }
	    
	    if(count==0)
	    	defaultTag(cid,tags);
	    
		return_jso.put("count",count).put("valid", valid).put("why", why);
		return this.getServiceContext().doNextProcess();
	}
	
	public static void defaultTag(String cid,JSONArray tags){
		double start_num=0;
		try{
	    	JSONObject tag = new JSONObject();
	    	start_num+=1000;
			tags.put(tag);
			tag.put("cid",cid);
			tag.put("name", "news");
			tag.put("type", "2");
			tag.put("desc", "");
			tag.put("sort", String.valueOf(start_num));
			tag.put("icon", new JSONObject("{'url':'/images/null.gif'}"));
			tag.put("key",uSetTag.insertTag(cid,tag.getString("name"),((JSONObject)tag.get("icon")).toString(),start_num,tag.getString("type"),tag.getString("desc")));
			//put 2
			tag = new JSONObject();
			start_num+=1000;
			tags.put(tag);
			tag.put("cid",cid);
			tag.put("name", "cover story");
			tag.put("type", "1");
			tag.put("desc", "");
			tag.put("sort", String.valueOf(start_num));
			tag.put("icon", new JSONObject("{'url':'/images/null.gif'}"));
			tag.put("key",uSetTag.insertTag(cid,tag.getString("name"),((JSONObject)tag.get("icon")).toString(),start_num,tag.getString("type"),tag.getString("desc")));
			DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
			Query q = new Query("uTag");
			q = new Query("xBom");	    	
			
	        	q.setFilter(
	        			CompositeFilterOperator.and(
	        				new Query.FilterPredicate("mallFid", FilterOperator.EQUAL, cid),
	        				new Query.FilterPredicate("type", FilterOperator.EQUAL, 0)
	        			));	        
	    	
	    	q.addSort("date", SortDirection.DESCENDING);
	    	PreparedQuery pq=null;
	        pq = ds.prepare(q);
	    	for(Entity found : pq.asIterable()) {	    		
	    		xBom xb=new xBom(found);
	    		if(xb.getId()!=null && xb.getId().length()>0){	    			
	    		}else{
	    			tag = new JSONObject();
	    			start_num+=1000;
	    			tags.put(tag);
	    			tag.put("cid",cid);
	    			tag.put("name", xb.getName());
	    			tag.put("type", "0");
	    			tag.put("desc", "");
	    			tag.put("sort", String.valueOf(start_num));
	    			tag.put("icon", new JSONObject("{'url':'"+xb.getIcon()+"'}"));
	    			tag.put("key",uSetTag.insertTag(cid,tag.getString("name"),((JSONObject)tag.get("icon")).toString(),start_num,tag.getString("type"),tag.getString("desc")));
	    			
	    		}
	    	}
		}catch(Exception e1){
		    	
		}
		
	}
	
}
