package xlive.method.rst.statistic;

import java.util.Date;

import com.google.appengine.api.datastore.Entity;

import xlive.xUtility;

public class xCustomerBonus {  
	/*
	private String fid
    private String name;
    private Date createDate;
    private Date lastDate;
    private long m2cBonus;
    private long c2mBonus; 
    */
    public Entity entity;
    public xCustomerBonus(Entity entity){
    	this.entity=entity;
    }
    public String getName(){
    	return (String)entity.getProperty("name");
    }
    public void setName(String name){
    	entity.setProperty("name", name);
    }
    public String getFid(){
    	return (String)entity.getProperty("fid");
    }
    public void setFid(String fid){
    	entity.setProperty("fid", fid);
    }
    public Date getCreateDate(){
    	return (Date) entity.getProperty("createDate");
    }
    public void setCreateDate(Date date){
    	entity.setProperty("createDate",date);
    }
    public void setCreateDate(String date){
    	Date d=(date != null && date.trim().length() >= 8)? xUtility.parseDate(date):null;
    	entity.setProperty("createDate",d);
    }
    public String getCreateDateString(){
    	Date d = (Date)entity.getProperty("createDate");
    	return (d != null)? xUtility.formatDate(d):"";
    }
    public Date getLastDate(){
    	return (Date) entity.getProperty("lastDate");
    }
    public void setLastDate(Date date){
    	entity.setProperty("lastDate",date);
    }
    public void setLastDate(String date){
    	Date d=(date != null && date.trim().length() >= 8)? xUtility.parseDate(date):null;
    	entity.setProperty("lastDate",d);
    }
    public String getLastDateString(){
    	Date d = (Date)entity.getProperty("lastDate");
    	return (d != null)? xUtility.formatDate(d):"";
    }
    public long getM2CBonus(){
    	Long bonus=(Long)this.entity.getProperty("m2cBonus");
    	return (bonus != null)?bonus.longValue():0l;
    }
    public void setM2CBonus(long bonus){
    	try{
    		this.entity.setProperty("m2cBonus", Long.valueOf(bonus));
    	}catch(Exception e){}
    }
    public long getC2MBonus(){
    	Long bonus=(Long)this.entity.getProperty("c2mBonus");
    	return (bonus != null)?bonus.longValue():0l;
    }
    public void setC2MBonus(long bonus){
    	try{
    		this.entity.setProperty("c2mBonus", Long.valueOf(bonus));
    	}catch(Exception e){}
    }
    
}
