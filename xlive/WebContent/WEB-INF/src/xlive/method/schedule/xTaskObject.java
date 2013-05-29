package xlive.method.schedule;


import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

public class xTaskObject {  
    private com.google.appengine.api.datastore.Key key;
    private String name;
    public xTaskObject(String pid, String name) {
    	key = xTaskObject.generateKey(pid, name);
    	this.name=name;
    }
    public static Key generateKey(String pid, String name){
    	return new KeyFactory.Builder(xTaskObject.class.getSimpleName(), "/").addChild(xTaskObject.class.getSimpleName(), pid+name).getKey();
    }
    public com.google.appengine.api.datastore.Key getKey(){
    	return key;
    }
    public void setName(String name) {
        this.name = (name==null)?"":name;
    } 
    public String getName(){
    	return name;
    }
}
