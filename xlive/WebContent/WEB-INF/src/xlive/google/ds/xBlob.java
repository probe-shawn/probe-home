package xlive.google.ds;

import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
   
public class xBlob {
	
    public Entity entity;
    
    public xBlob(Entity entity){
    	this.entity=(entity !=null && xBlob.class.getSimpleName().equals(entity.getKind()))?entity:null;
    }
    public xBlob(){
    	this.entity= new Entity(xBlob.class.getSimpleName());
    }
    public xBlob(Key key){
    	this.entity= new Entity(key);
    }
    public xBlob(Key parent, String kind){
    	this.entity= new Entity(xBlob.class.getSimpleName(),parent);
    }
    public xBlob(long id){
    	this.entity= new Entity(xBlob.class.getSimpleName(),id);
    }
    public xBlob(long id,Key parent){
    	this.entity= new Entity(xBlob.class.getSimpleName(),id,parent);
    }
    public xBlob(String namekey){
    	this.entity= new Entity(xBlob.class.getSimpleName(),namekey);
    }
    public xBlob(String namekey, Key parent){
    	this.entity= new Entity(xBlob.class.getSimpleName(),namekey,parent);
    }
    /////////////
    public void setBlobBytes(byte[] bytedata) {
    	this.entity.setProperty("Blob", new Blob(bytedata));
    } 
    public byte[] getBlobBytes() {
    	Blob blob = (Blob) this.entity.getProperty("Blob");
    	return (blob != null) ? blob.getBytes() : null;
    }
    public Blob getBlob(){
    	return (Blob) this.entity.getProperty("Blob");
    }
    public void setBlob(Blob blob){
    	this.entity.setProperty("Blob", blob);
    }

}
