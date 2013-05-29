package xlive.method.yup.prop;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import xlive.xUtility;
import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Text;
public class uProperty {
public Entity entity = null;
    
    public uProperty(Entity entity){
    	this.entity = entity;
    }
    public String getKey(){
    	return KeyFactory.keyToString(this.entity.getKey());
    }   
    public String getCid(){
    	return (String)entity.getProperty("cid");
    }
    public void setCid(String cid){
    	this.entity.setProperty("cid", cid);
    }
    public String getIcon(){
    	return (String) this.entity.getProperty("icon");
    }
    public void setIcon(String icon){
    	this.entity.setProperty("icon", icon);
    }
    public Date getDate(){
    	return (Date) this.entity.getProperty("date");
    }
    public String getDateString(){
    	Date date = (Date) this.entity.getProperty("date");
    	return date != null ? xUtility.formatDate(date):"";
    }
    public void setDate(Date date){
    	this.entity.setProperty("date", date);
    }
    public void setDate(String date_str){
    	Date date=(date_str != null && date_str.trim().length() >= 8)? xUtility.parseDate(date_str):null;
    	this.entity.setProperty("date", date);
    }   
    
    public String getName(){
    	return (String) this.entity.getProperty("name");
    }
    public void setName(String name){
    	this.entity.setProperty("name", name);
    }
    public double getSort(){
    	Object sort=this.entity.getProperty("sort");
    	return (sort != null && sort instanceof Double)? ((Double)sort).doubleValue():0;
    }
    public void setSort(double sort){
    	try{
    		this.entity.setProperty("sort", Double.valueOf(sort));
    	}catch(Exception e){}
    }
    public void setSort(String sort){
    	try{
    		this.entity.setProperty("sort", Double.valueOf(sort));
    	}catch(Exception e){}
    } 
    
    public String getMust(){
    	return (String) this.entity.getProperty("must");
    }
    public void setMust(String must){
    	this.entity.setProperty("must", must);
    }
    public String getDataSet(){
    	Object obj = this.entity.getProperty("dataset");
    	if(obj instanceof Blob){
	    	Blob blob = (Blob) obj;
	    	try {
				return (blob != null) ? new String(blob.getBytes(),"utf-8") : null;
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
    	}else{
    		Text text = (Text) obj;
    		return text.getValue();
    	}
		return "";
    }
    public void setDataSet(String dataset){
    	this.entity.setProperty("dataset", new Text(dataset));
    	/*
    	try {
    		Blob blob = (text == null||text.trim().length()==0) ? null: new Blob(text.getBytes("utf-8"));
    		this.entity.setProperty("dataset", blob);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		*/
    }
    public static void xmluProperty(uProperty prop, org.w3c.dom.Element result){
    	org.w3c.dom.Document doc = result.getOwnerDocument();
    	result.appendChild(doc.createElement("key")).setTextContent(prop.getKey());
    	result.appendChild(doc.createElement("cid")).setTextContent(prop.getCid());
    	result.appendChild(doc.createElement("name")).setTextContent(prop.getName());
    	result.appendChild(doc.createElement("must")).setTextContent(prop.getMust());
    	result.appendChild(doc.createElement("sort")).setTextContent(String.valueOf(prop.getSort()));	      	
    	result.appendChild(doc.createElement("icon")).setTextContent(prop.getIcon());
    	result.appendChild(doc.createElement("date")).setTextContent(prop.getDateString());
    	result.appendChild(doc.createElement("dataset")).setTextContent(prop.getDataSet());
 
    }
}
