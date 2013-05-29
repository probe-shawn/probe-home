package xlive.method.rst.statistic;

import java.util.Date;

import com.google.appengine.api.datastore.Entity;

import xlive.xUtility;

public class xGoodsWeek {  
	/*
	private String mallFid;
	private String bomKey;
	private String name;
    private String icon;
    private Date date;
    private long qty;
    */
    public Entity entity;
    public xGoodsWeek(Entity entity){
    	this.entity=entity;
    }
    public String getMallFid(){
    	return (String)entity.getProperty("mall-fid");
    }
    public void setMallFid(String mall_fid){
    	entity.setProperty("mall-fid", mall_fid);
    }
    public String getBomKey(){
    	return (String)entity.getProperty("bomKey");
    }
    public void setBomKey(String bom_key){
    	entity.setProperty("bomKey", bom_key);
    }
    public String getName(){
    	return (String)entity.getProperty("name");
    }
    public void setName(String name){
    	entity.setProperty("name", name);
    }
    public String getIcon(){
    	return (String)entity.getProperty("icon");
    }
    public void setIcon(String icon){
    	entity.setProperty("icon", icon);
    }
    public Date getDate(){
    	return (Date) entity.getProperty("date");
    }
    public void setDate(Date date){
    	entity.setProperty("date",date);
    }
    public void setDate(String date){
    	Date d=(date != null && date.trim().length() >= 8)? xUtility.parseDate(date):null;
    	entity.setProperty("date",d);
    }
    public String getDateString(){
    	Date d = (Date)entity.getProperty("date");
    	return (d != null)? xUtility.formatDate(d):"";
    }
    public long getQty(){
    	Long qty=(Long)this.entity.getProperty("qty");
    	return (qty != null)?qty.longValue():0l;
    }
    public void setQty(long qty){
    	try{
    		this.entity.setProperty("qty", Long.valueOf(qty));
    	}catch(Exception e){}
    }
    public void setQty(String qty){
    	try{
    		this.entity.setProperty("qty", Long.valueOf(qty));
    	}catch(Exception e){}
    }
    
}
