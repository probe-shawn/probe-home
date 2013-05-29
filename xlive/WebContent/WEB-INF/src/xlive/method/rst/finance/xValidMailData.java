package xlive.method.rst.finance;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import xlive.xUtility;
import xlive.method.rst.mall.xMallDetail;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

public class xValidMailData {
public Entity entity = null;
    
    public xValidMailData(Entity entity){
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
    
    @SuppressWarnings("unchecked")
	public List<String> getRecords(){
    	return (List<String>)this.entity.getProperty("records");
    }
    public void setRecords(List<String> records){
    	this.entity.setProperty("records", records);
    }
    @SuppressWarnings("unchecked")
	public void addRecord(String record){
    	List<String> records = (List<String>)this.entity.getProperty("records");
    	if(records == null) records = new ArrayList<String>();
    	records.add(record);
    	this.entity.setProperty("records", records);
    }
    
    //type分超商檔1及ATM檔2
    public String getType(){
    	return (String) this.entity.getProperty("type");
    }
    public void setType(String type){
    	this.entity.setProperty("type", type);
    }
        
    //數量
    public long getQTY(){
    	Object qty=this.entity.getProperty("qty");
    	if(qty != null && qty instanceof String){
    		String qty_string=(String)qty;	
    		return (qty_string.trim().length() > 0) ? Long.valueOf(qty_string):0;
    	}
    	Long lqty=(Long)this.entity.getProperty("qty");
    	return (lqty != null)? lqty.longValue():0;
    }
    public void setQTY(long qty){
    	try{
    		this.entity.setProperty("qty", Long.valueOf(qty));
    	}catch(Exception e){}
    }
    public void setQTY(String qty){
    	try{
    		this.entity.setProperty("qty", Long.valueOf(qty));
    	}catch(Exception e){}
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
