package xlive.method.rst.finance;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import xlive.xUtility;
import xlive.method.rst.mall.xMallDetail;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

public class xInvalidMailData {
public Entity entity = null;
    
    public xInvalidMailData(Entity entity){
    	this.entity = entity;
    }
          
    public String getFrom(){
    	return (String) this.entity.getProperty("from");
    }
    public void setFrom(String from){
    	this.entity.setProperty("from", from);
    }
    
    public String getFileName(){
    	return (String) this.entity.getProperty("filename");
    }
    public void setFileName(String filename){
    	this.entity.setProperty("filename",filename);
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
    
	public String getRecord(){
    	return (String)this.entity.getProperty("record");
    }
    public void setRecord(String record){
    	this.entity.setProperty("record", record);
    }
       
    //type¤À¶W°ÓÀÉ1¤ÎATMÀÉ2
    public String getType(){
    	return (String) this.entity.getProperty("type");
    }
    public void setType(String type){
    	this.entity.setProperty("type", type);
    }
   
    
    public String getRemark(){
    	return (String)entity.getProperty("remark");
    }
    public void setRemark(String remark){
    	this.entity.setProperty("remark", remark);
    }
    
    
    public String getDoneFlag(){
    	return (String)entity.getProperty("doneflag");
    }
    public void setDoneFlag(String doneflag){
    	this.entity.setProperty("doneflag", doneflag);
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
    
   

}
