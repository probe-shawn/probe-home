package xlive.method.rst.circle;

import java.util.Date;
import xlive.xUtility;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.KeyFactory;

public class xCircleMall { 
	/*
    private String circleFid;
    private String mallFid;
    private long status; // -1 reject, 0 apply, 1 accept
    private String m2cMessage;
    private String c2mMessage;
    private Date date;
    */
	public Entity entity = null;
    public xCircleMall(Entity entity){
    	this.entity = entity;
    }
    public String getCircleFid(){
    	return (String)entity.getProperty("circleFid");
    }
    public void setCircleFid(String circle_fid){
    	this.entity.setProperty("circleFid", circle_fid);
    }
    public String getMallFid(){
    	return (String)entity.getProperty("mallFid");
    }
    public void setMallFid(String mall_fid){
    	this.entity.setProperty("mallFid", mall_fid);
    }
    public String getC2mMessage(){
    	return (String)entity.getProperty("c2mMessage");
    }
    public void setC2mMessage(String c2m_message){
    	this.entity.setProperty("c2mMessage", c2m_message);
    }
    public String getM2cMessage(){
    	return (String)entity.getProperty("m2cMessage");
    }
    public void setM2cMessage(String m2c_message){
    	this.entity.setProperty("m2cMessage", m2c_message);
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
    public void setStatus(long status){
    	try{
    		this.entity.setProperty("status", Long.valueOf(status));
    	}catch(Exception e){}
    }
    public void setStatus(String status){
    	try{
    		this.entity.setProperty("status", Long.valueOf(status));
    	}catch(Exception e){}
    }
    public long getStatus(){
    	Long status = (Long)this.entity.getProperty("status");
    	return (status != null) ? status.longValue() : 0;
    }
   //
    public static void xmlCircleMall(xCircleMall circle, org.w3c.dom.Element result){
    	org.w3c.dom.Document doc = result.getOwnerDocument();
    	result.appendChild(doc.createElement("key")).setTextContent(KeyFactory.keyToString(circle.entity.getKey()));
    	result.appendChild(doc.createElement("circle-fid")).setTextContent(circle.getCircleFid());
    	result.appendChild(doc.createElement("mall-fid")).setTextContent(circle.getMallFid());
    	result.appendChild(doc.createElement("status")).setTextContent(String.valueOf(circle.getStatus()));
    	result.appendChild(doc.createElement("c2m-message")).setTextContent(circle.getC2mMessage());
    	result.appendChild(doc.createElement("m2c-message")).setTextContent(circle.getM2cMessage());
    	result.appendChild(doc.createElement("date")).setTextContent(circle.getDateString());
    }

}