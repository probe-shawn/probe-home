package xlive.method.upos.tag;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import xlive.xUtility;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.KeyFactory;
public class uProductTag {
public Entity entity = null;
    
    public uProductTag(Entity entity){
    	this.entity = entity;
    }
    public String getKey(){
    	return KeyFactory.keyToString(this.entity.getKey());
    }   
    
    public String getProductTagID(){
    	return (String)entity.getProperty("ProductTagID");
    }
    public void setProductTagID(String tag_id){
    	this.entity.setProperty("ProductTagID", tag_id);
    }
    public void setProductTagID(){
    	this.entity.setProperty("ProductTagID", getKey());
    }
    
    public String getIcon(){
    	return (String) this.entity.getProperty("icon");
    }
    public void setIcon(String icon){
    	this.entity.setProperty("icon", icon);
    }
    
    public String getOwner(){
    	return (String) this.entity.getProperty("owner");
    }
    public void setOwner(String fid){
    	this.entity.setProperty("owner", fid);
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
    
    public String getDesc(){
    	return (String) this.entity.getProperty("desc");
    }
    public void setDesc(String desc){
    	this.entity.setProperty("desc", desc);
    }
      
    public double getSort(){
    	Double sort=(Double)this.entity.getProperty("sort");
    	return (sort != null)? sort.doubleValue():0;
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
    
    @SuppressWarnings("unchecked")
	public List<String> getProducts(){
    	return (List<String>)this.entity.getProperty("products");
    }
    public void setProducts(List<String> products){
    	this.entity.setProperty("products", products);
    }
    @SuppressWarnings("unchecked")
	public void addProduct(String p){
    	List<String> products = (List<String>)this.entity.getProperty("products");
    	if(products == null) products = new ArrayList<String>();
    	products.add(p);
    	this.entity.setProperty("products",products);
    } 
   
    
    public static void xmlProductTag(uProductTag tag, org.w3c.dom.Element result){
    	
    	org.w3c.dom.Document doc = result.getOwnerDocument();
    	result.appendChild(doc.createElement("key")).setTextContent(tag.getKey());
    	result.appendChild(doc.createElement("ProductTagID")).setTextContent(tag.getProductTagID());
    	result.appendChild(doc.createElement("owner")).setTextContent(tag.getOwner());
    	result.appendChild(doc.createElement("name")).setTextContent(tag.getName());
    	result.appendChild(doc.createElement("desc")).setTextContent(tag.getDesc());
    	result.appendChild(doc.createElement("sort")).setTextContent(String.valueOf(tag.getSort()));	      	
    	result.appendChild(doc.createElement("icon")).setTextContent(tag.getIcon());
    	result.appendChild(doc.createElement("date")).setTextContent(tag.getDateString());
    }
}
