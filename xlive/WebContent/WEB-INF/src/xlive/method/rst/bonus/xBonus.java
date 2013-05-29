package xlive.method.rst.bonus;

import java.util.Date;
import xlive.xUtility;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.KeyFactory;

public class xBonus {  
	/*
    private String fromId;
    private String formType;
    private String toId;
    private String toType;
    private int bonus = 0;
    private Date date;
    private String note;
    */
	public Entity entity=null;
    public xBonus(Entity entity){
    	this.entity=entity;
    }
    public String getFromId(){
    	return (String) this.entity.getProperty("fromId");
    }
    public void setFromId(String from_id){
    	this.entity.setProperty("fromId", from_id);
    }
    public String getFromType(){
    	return (String) this.entity.getProperty("fromType");
    }
    public void setFromType(String from_type){
    	this.entity.setProperty("fromType", from_type);
    }
    public String getToId(){
    	return (String) this.entity.getProperty("toId");
    }
    public void setToId(String to_id){
    	this.entity.setProperty("toId", to_id);
    }
    public String getToType(){
    	return (String) this.entity.getProperty("toType");
    }
    public void setToType(String to_type){
    	this.entity.setProperty("toType", to_type);
    }
    public long getBonus(){
    	Long bonus = (Long)this.entity.getProperty("bonus");
    	return bonus != null ?bonus.longValue() : 0;
    }
    public void setBonus(long bonus){
    	try{
    		this.entity.setProperty("bonus", Long.valueOf(bonus));
    	}catch(Exception e){}
    }
    public void setBonus(String bonus){
    	try{
    		this.entity.setProperty("bonus", Long.valueOf(bonus));
    	}catch(Exception e){}
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
    
    public String getNote(){
    	return (String) this.entity.getProperty("note");
    }
    public void setNote(String note){
    	this.entity.setProperty("note", note);
    }
    public static void xmlMall(xBonus bonus, org.w3c.dom.Element result){
    	//DecimalFormat df = new DecimalFormat("#######.##");
    	org.w3c.dom.Document doc = result.getOwnerDocument();
    	result.appendChild(doc.createElement("key")).setTextContent(KeyFactory.keyToString(bonus.entity.getKey()));
    	result.appendChild(doc.createElement("from-type")).setTextContent(bonus.getFromType());
    	result.appendChild(doc.createElement("from-id")).setTextContent(bonus.getFromId());
    	result.appendChild(doc.createElement("to-type")).setTextContent(bonus.getToType());
    	result.appendChild(doc.createElement("to-id")).setTextContent(bonus.getToId());
       	result.appendChild(doc.createElement("bonus")).setTextContent(String.valueOf(bonus.getBonus()));
       	result.appendChild(doc.createElement("date")).setTextContent(bonus.getDateString());
       	result.appendChild(doc.createElement("note")).setTextContent(bonus.getNote());
    }

}
