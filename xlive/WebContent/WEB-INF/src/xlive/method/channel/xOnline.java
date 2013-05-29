package xlive.method.channel;

import java.util.Date;

import xlive.xUtility;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.KeyFactory;

public class xOnline {  
	/*
    private String fid;
    private String name;
    private String barterFid;
    private String barterName;
    private String mallFid;
    private String mallName;
    private String type;
    private long status; 0, 1 open 
    private Date date;
    */
	
	public Entity entity;
    
    public xOnline(Entity entity){
    	this.entity = entity;
    }
    public String getFid(){
    	return (String) this.entity.getProperty("fid");
    }
    public void setFid(String fid){
    	this.entity.setProperty("fid", fid);
    }
    public String getName(){
    	return (String) this.entity.getProperty("name");
    }
    public void setName(String name){
    	this.entity.setProperty("name", name);
    }
    public String getBarterFid(){
    	return (String) this.entity.getProperty("barterFid");
    }
    public void setBarterFid(String barter_fid){
    	this.entity.setProperty("barterFid", barter_fid);
    }
    public String getBarterName(){
    	return (String) this.entity.getProperty("barterName");
    }
    public void setBarterName(String barter_name){
    	this.entity.setProperty("barterName", barter_name);
    }
    public String getMallFid(){
    	return (String) this.entity.getProperty("mallFid");
    }
    public void setMallFid(String mall_fid){
    	this.entity.setProperty("mallFid", mall_fid);
    }
    public String getMallName(){
    	return (String) this.entity.getProperty("mallName");
    }
    public void setMallName(String mall_name){
    	this.entity.setProperty("mallName", mall_name);
    }
   public String getType(){
    	return (String) this.entity.getProperty("type");
    }
    public void setType(String type){
    	this.entity.setProperty("type", type);
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
    public long getStatus(){
    	Long status = (Long) this.entity.getProperty("status");
    	return (status != null) ?status.longValue():0;
    }
    public void setStatus(long status){
    	try{
    		this.entity.setProperty("status", Long.valueOf(status));
    	}catch(Exception e){};
    }
    public static void xmlOnline(xOnline online, org.w3c.dom.Element result){
    	org.w3c.dom.Document doc = result.getOwnerDocument();
       	result.appendChild(doc.createElement("id")).setTextContent(KeyFactory.keyToString(online.entity.getKey()));
       	result.appendChild(doc.createElement("fid")).setTextContent(online.getFid());
       	result.appendChild(doc.createElement("name")).setTextContent(online.getName());
       	result.appendChild(doc.createElement("barter-fid")).setTextContent(online.getBarterFid());
       	result.appendChild(doc.createElement("barter-name")).setTextContent(online.getBarterName());
       	result.appendChild(doc.createElement("mall-fid")).setTextContent(online.getMallFid());
       	result.appendChild(doc.createElement("mall-name")).setTextContent(online.getMallName());
       	result.appendChild(doc.createElement("type")).setTextContent(online.getType());
       	result.appendChild(doc.createElement("date")).setTextContent(online.getDateString());
       	result.appendChild(doc.createElement("status")).setTextContent(String.valueOf(online.getStatus()));
    }

    
}
