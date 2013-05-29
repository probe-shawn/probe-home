package xlive.google.ds;


import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Transaction;
   
public class xHash {
	
    private String namespace="_globe";
    
    public xHash(){}
    public xHash(String namespace){
    	this.namespace = namespace;
    }
    private Key generateKey(String key){
    	return KeyFactory.createKey(xHash.class.getSimpleName(),  namespace+"/"+key);
    }
    public String putString(String key, String value){
    	byte[] bytes = this.put(key, value.getBytes());
    	return bytes == null ? null : new String(bytes);
    }
    public byte[] put(String akey, byte[] value){
    	boolean ok = false;
    	Key k = this.generateKey(akey);
    	Blob blob = (value != null && value.length>0) ? new Blob(value):null;
    	int retries = 3;
    	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    	while (true) {
    	    Transaction txn = datastore.beginTransaction();
    	    try {
    	    	Entity entity=null;
    	    	try{
    	    		entity = datastore.get(k);
    	    	}catch(EntityNotFoundException e){
    	    		entity = new Entity(k);
    	    	}
    	        entity.setProperty("blob", blob);
    	        datastore.put(entity);
    	        txn.commit();
    	        ok=true;
    	        break;
    	    } catch (java.util.ConcurrentModificationException  e) {
    	        if(retries == 0) throw e;
    	        --retries;
    	    } finally {
    	        if(txn.isActive())txn.rollback();
    	    }
    	} 
    	return (ok)? value : null;
    }
    public String getString(String key){
    	byte[] bytes = this.get(key);
    	return bytes == null ? null : new String(bytes); 
    }
    public byte[] get(String akey){
    	Key k = this.generateKey(akey);
    	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    	Entity entity;
    	Blob blob =null;
		try {
			entity = datastore.get(k);
			if(entity != null) blob =(Blob)entity.getProperty("blob");
		} catch (EntityNotFoundException e) {
		}
    	return (blob != null)?blob.getBytes():null;
    }
    public boolean delete(String akey){
    	boolean ok = false;
    	Key k = this.generateKey(akey);
    	int retries = 3;
    	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    	while (true) {
    	    Transaction txn = datastore.beginTransaction();
    	    try {
    	    	datastore.delete(k);
    	        txn.commit();
    	        ok=true;
    	        break;
    	    } catch (java.util.ConcurrentModificationException e) {
    	        if(retries == 0) throw e;
    	        --retries;
    	    } finally {
    	        if(txn.isActive())txn.rollback();
    	    }
    	} 
    	return ok;
    }
    public boolean contains(String akey){
    	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    	try {
			datastore.get(this.generateKey(akey));
			return true;
		} catch (EntityNotFoundException e1) {
		}
		return false;
    }
}
