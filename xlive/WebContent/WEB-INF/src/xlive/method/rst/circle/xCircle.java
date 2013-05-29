package xlive.method.rst.circle;


import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import xlive.xUtility;
import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

public class xCircle { 
	/*
    private String fid;
    private String name;
    private Blob descHtml;
    private Date date;
    private String phone;
    private String addr;
    private String mail;
    private long map;
    
    private Date last;
    private float latitude;
    private float longitude;
    private List<String> geoCell;
    
    private String icon;
    private String bgIcon;
    private List<String> icons;
    private long tzOffset;
    private long visit;
    private String applyMessage;
    */
	public Entity entity = null;
    public xCircle(Entity entity){
    	this.entity = entity;
    }
    public static Key generateKey(String fid){
    	return KeyFactory.createKey(xCircle.class.getSimpleName(), fid);
    }
    public String getFid(){
    	return (String)entity.getProperty("fid");
    }
    public void setFid(String fid){
    	this.entity.setProperty("fid", fid);
    }
    public String getName(){
    	return (String)entity.getProperty("name");
    }
    public void setName(String name){
    	this.entity.setProperty("name", name);
    }
    public String getDescHtml(){
    	Blob blob = (Blob)this.entity.getProperty("descHtml");
    	try {
			return (blob != null) ? new String(blob.getBytes(),"utf-8") : null;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return "";
    }
    public void setDescHtml(String text){
    	try {
    		Blob blob = (text == null||text.trim().length()==0) ? null: new Blob(text.getBytes("utf-8"));
    		this.entity.setProperty("descHtml", blob);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
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
    
    public String getPhone(){
    	return (String) this.entity.getProperty("phone");
    }
    public void setPhone(String phone){
    	this.entity.setProperty("phone", phone);
    }
    
    public String getAddr(){
    	return (String) this.entity.getProperty("addr");
    }
    public void setAddr(String addr){
    	this.entity.setProperty("addr", addr);
    }
    public void setMap(long map){
    	try{
    		this.entity.setProperty("map", Long.valueOf(map));
    	}catch(Exception e){}
    }
    public void setMap(String map){
    	try{
    		this.entity.setProperty("map", Long.valueOf(map));
    	}catch(Exception e){}
    }
    public long getMap(){
    	Long map = (Long)this.entity.getProperty("map");
    	return (map != null) ? map.longValue() : 15;
    }

    public String getMail(){
    	return (String) this.entity.getProperty("mail");
    }
    public void setMail(String mail){
    	this.entity.setProperty("mail", mail);
    }
    public Date getLast(){
    	return (Date) this.entity.getProperty("last");
    }
    public String getLastString(){
    	Date date = (Date) this.entity.getProperty("last");
    	return date != null ? xUtility.formatDate(date):"";
    }
    public void setLast(Date last){
    	this.entity.setProperty("last", last);
    }
    public void setLast(String last){
    	Date date = (last != null && last.trim().length() >= 8)? xUtility.parseDate(last):null;
    	this.entity.setProperty("last", date);
    }
    public float getLatitude(){
    	Double f = (Double)this.entity.getProperty("latitude");
    	return (f != null) ? f.floatValue() : 0;
    }
    public void setLatitude(double latitude){
    	try{
    		this.entity.setProperty("latitude",Double.valueOf(latitude));
    	}catch(Exception e){}
    }
    public void setLatitude(String latitude){
    	try{
    		this.entity.setProperty("latitude", Double.valueOf(latitude));
    	}catch(Exception e){}
    }
    public double getLongitude(){
    	Double f = (Double)this.entity.getProperty("longitude");
    	return (f != null) ? f.doubleValue() : 0;
    }
    public void setLongitude(double longitude){
    	try{
    		this.entity.setProperty("longitude", Double.valueOf(longitude));
    	}catch(Exception e){}
    }
    public void setLongitude(String longitude){
    	try{
    		this.entity.setProperty("longitude", Double.valueOf(longitude));
    	}catch(Exception e){}
    }
    public String getIcon(){
    	return (String) this.entity.getProperty("icon");
    }
    public void setIcon(String icon){
    	this.entity.setProperty("icon", icon);
    }
    public String getBgIcon(){
    	return (String) this.entity.getProperty("bgIcon");
    }
    public void setBgIcon(String icon){
    	this.entity.setProperty("bgIcon", icon);
    }
    @SuppressWarnings("unchecked")
	public List<String> getIcons(){
    	return (List<String>)this.entity.getProperty("icons");
    }
    public void setIcons(List<String> icons){
    	this.entity.setProperty("icons", icons);
    }
    @SuppressWarnings("unchecked")
	public void addIcon(String icon){
    	List<String> icons = (List<String>)this.entity.getProperty("icons");
    	if(icons == null) icons = new ArrayList<String>();
    	icons.add(icon);
    	this.entity.setProperty("icons", icons);
    }
    
    public long getTzOffset(){
    	Long tzoffset = (Long)this.entity.getProperty("tzOffset");
    	return (tzoffset != null) ? tzoffset.longValue() : -480l;
    }
    public void setTzOffset(long tzoffset){
    	try{
    		this.entity.setProperty("tzOffset", Long.valueOf(tzoffset));
    	}catch(Exception e){}
    }
    public void setTzOffset(String tzoffset){
    	try{
    		this.entity.setProperty("tzOffset", Long.valueOf(tzoffset));
    	}catch(Exception e){}
    }
    public void setVisit(long visit){
    	try{
    		this.entity.setProperty("visit", Long.valueOf(visit));
    	}catch(Exception e){}
    }
    public long getVisit(){
    	Long visit = (Long)this.entity.getProperty("visit");
    	return (visit != null) ? visit.longValue() : 0;
    }
    public String getApplyMessage(){
    	return (String) this.entity.getProperty("applyMessage");
    }
    public void setApplyMessage(String addr){
    	this.entity.setProperty("applyMessage", addr);
    }
   //
    public static void xmlCircle(xCircle circle, org.w3c.dom.Element result){
    	org.w3c.dom.Document doc = result.getOwnerDocument();
    	result.appendChild(doc.createElement("fid")).setTextContent(circle.getFid());
    	result.appendChild(doc.createElement("name")).setTextContent(circle.getName());
		result.appendChild(doc.createElement("desc-html")).setTextContent(circle.getDescHtml());
    	result.appendChild(doc.createElement("date")).setTextContent(circle.getDateString());
    	result.appendChild(doc.createElement("phone")).setTextContent(circle.getPhone());
    	result.appendChild(doc.createElement("addr")).setTextContent(circle.getAddr());
    	result.appendChild(doc.createElement("map")).setTextContent(String.valueOf(circle.getMap()));
    	result.appendChild(doc.createElement("mail")).setTextContent(circle.getMail());
       	result.appendChild(doc.createElement("last")).setTextContent(circle.getLastString());
       	result.appendChild(doc.createElement("latitude")).setTextContent(String.valueOf(circle.getLatitude()));
       	result.appendChild(doc.createElement("longitude")).setTextContent(String.valueOf(circle.getLongitude()));
      	//
    	result.appendChild(doc.createElement("icon")).setTextContent(circle.getIcon());
    	result.appendChild(doc.createElement("bg-icon")).setTextContent(circle.getBgIcon());
		org.w3c.dom.Element icons_element = (org.w3c.dom.Element)result.appendChild(doc.createElement("icons"));
        List<String> icons_list = circle.getIcons();
        if(icons_list != null){
        	for(String icons_string : icons_list){
        		if(icons_string != null && icons_string.trim().length() > 0)
        			icons_element.appendChild(doc.createElement("icon")).setTextContent(icons_string);
        	}
        }
    	result.appendChild(doc.createElement("tz-offset")).setTextContent(String.valueOf(circle.getTzOffset()));
    	result.appendChild(doc.createElement("apply-message")).setTextContent(String.valueOf(circle.getApplyMessage()));
       	
    }

}