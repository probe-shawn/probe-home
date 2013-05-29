package xlive.method.rst.menu;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;

import xlive.method.*;

public class xRemoveBomMethod extends xDefaultMethod{
	public Object process()throws xMethodException{
		boolean valid=true;
		String why = "";
		String keystr=this.getArguments("key");
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		Entity entity = null;
		
		if(keystr != null && keystr.trim().length()>0){
			try{
				entity = ds.get(KeyFactory.stringToKey(keystr));
			}catch(EntityNotFoundException e){entity = null;}
			if(entity != null)	this.removeBom(new xBom(entity));
		}
		this.setReturnArguments("valid", String.valueOf(valid));
		this.setReturnArguments("why", why);
		//
		return this.getServiceContext().doNextProcess();
	}
	private void removeBom(xBom bom){
		/*
		String icon = bom.getIcon();
		if(icon.startsWith("/gae")){
			new xFile(icon).delete();
		}
		List<String> icons = new ArrayList<String>();
		for(String fname : icons){
			if(fname.startsWith("/gae")){
				new xFile(fname).delete();
			}
		}
		*/
    	DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
    	Query q = new Query("xBom");
    	/*
    	q.addFilter("parentKey", FilterOperator.EQUAL, KeyFactory.keyToString(bom.entity.getKey()));
    	*/
    	q.setFilter(new Query.FilterPredicate("parentKey", FilterOperator.EQUAL, KeyFactory.keyToString(bom.entity.getKey())));
    	PreparedQuery pq = ds.prepare(q);
    	for(Entity found : pq.asIterable()) {
    		this.removeBom(new xBom(found));
    	}
    	try {
    		Entity bomlimit_entity=ds.get(KeyFactory.createKey(xBomLimit.class.getSimpleName(), bom.getKey()));
    		ds.delete(bomlimit_entity.getKey());
		} catch (EntityNotFoundException e) {}
    	
    	ds.delete(bom.entity.getKey());
	}
}
