package xlive.google.ds;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Transaction;

    
public class xLong {  
    private String name;
    
    public xLong(){
    	name="unique";
    }
    public xLong(String name){
    	this.name=name;
    }
    private Key generateKey(String name){
    	return KeyFactory.createKey(xLong.class.getSimpleName(), name);
    }
    /////////////
    public long increase(){
    	Long count = new Long(1);;
    	Key key = this.generateKey(name);
    	int retries = 3;
    	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    	while (true) {
    	    Transaction txn = datastore.beginTransaction();
    	    try {
    	    	Entity entity=null;
    	    	try{
    	    		entity = datastore.get(key);
        	        count = (Long) entity.getProperty("value");
        	        ++count;
    	    	}catch(EntityNotFoundException e){
    	    		entity = new Entity(key);
    	    	}
    	        entity.setProperty("value", count);
    	        datastore.put(entity);
    	        txn.commit();
    	        break;
    	    } catch (java.util.ConcurrentModificationException  e) {
    	        if(retries == 0) throw e;
    	        --retries;
    	    } finally {
    	        if(txn.isActive())txn.rollback();
    	    }
    	} 
    	return count.longValue();
    }
}
