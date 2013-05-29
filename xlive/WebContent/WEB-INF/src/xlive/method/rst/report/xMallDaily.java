package xlive.method.rst.report;

import java.text.DecimalFormat;
import java.util.Date;
import xlive.xUtility;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

public class xMallDaily {  
	/*
    private String mallFid;
    private Date date;
    private String name;
    private double total;
    private long bonusC2M=0;
    private long bonusM2C=0;
    private long goodsQty=0;
    private Date last;
    private long tzOffset;
    private long processed;//1 or -1
    */
	public Entity entity;
    public xMallDaily(Entity entity){
    	this.entity=entity;
    }
    public static Key generateKey(String mall_id, String date, String processed){
    	return KeyFactory.createKey(xMallDaily.class.getSimpleName(), mall_id+"/"+date+"/"+processed);
    }
    public String getMallFid(){
    	return (String)this.entity.getProperty("mallFid");
    }
    public void setMallFid(String id){
    	this.entity.setProperty("mallFid", id);
    }
    public String getName(){
    	return (String)this.entity.getProperty("name");
    }
    public void setName(String name){
    	this.entity.setProperty("name", name);
    }
    
    public Date getDate(){
    	return (Date)this.entity.getProperty("date");
    }
    public String getDateString(){
    	Date date =(Date)this.entity.getProperty("date");
    	return date != null ? xUtility.formatDate(date):"";
    }
    public void setDate(Date date){
    	this.entity.setProperty("date", date);
    }
    public void setDate(String date_str){
    	Date date=(date_str != null && date_str.trim().length() >= 8)? xUtility.parseDate(date_str):null;
    	this.entity.setProperty("date", date);
    }
    
    public Date getLast(){
    	return (Date)this.entity.getProperty("last");
    }
    public String getLastString(){
    	Date date =(Date)this.entity.getProperty("last");
    	return date != null ? xUtility.formatDate(date):"";
    }
    public void setLast(Date date){
    	this.entity.setProperty("last", date);
    }
    public void setLast(String date_str){
    	Date date=(date_str != null && date_str.trim().length() >= 8)? xUtility.parseDate(date_str):null;
    	this.entity.setProperty("last", date);
    }
    
    public double getTotal(){
    	Double total = (Double)this.entity.getProperty("total");
    	return (total != null) ?total.doubleValue() : 0;
    }
    public void setTotal(double total){
    	try{
    		this.entity.setProperty("total", Double.valueOf(total));
    	}catch(Exception e){}
    }
    public void setTotal(String total){
    	try{
    		this.entity.setProperty("total", Double.valueOf(total));
    	}catch(Exception e){}
    }

    public long getBonusM2C(){
    	Long bonusM2C =(Long) this.entity.getProperty("bonusM2C");
    	return bonusM2C != null ? bonusM2C.longValue() : 0;
    }
    public void setBounsM2C(long bonus){
    	try{
    		this.entity.setProperty("bonusM2C", Long.valueOf(bonus));
    	}catch(Exception e){}
    }
    public void setBounsM2C(String bonus){
    	try{
    		this.entity.setProperty("bonusM2C", Long.valueOf(bonus));
    	}catch(Exception e){}
    }
    public long getBonusC2M(){
    	Long bonusC2M =(Long) this.entity.getProperty("bonusC2M");
    	return bonusC2M != null ? bonusC2M.longValue() : 0;
    }
    public void setBounsC2M(long bonus){
    	try{
    		this.entity.setProperty("bonusC2M", Long.valueOf(bonus));
    	}catch(Exception e){}
    }
    public void setBounsC2M(String bonus){
    	try{
    		this.entity.setProperty("bonusC2M", Long.valueOf(bonus));
    	}catch(Exception e){}
    }

    public long getGoodsQty(){
    	Long qty = (Long) this.entity.getProperty("goodsQty");
    	return qty != null ? qty.longValue() : 0;
    }
    public void setGoodsQty(long qty){
    	try{
    		this.entity.setProperty("goodsQty", Long.valueOf(qty));
    	}catch(Exception e){}
    }
    public void setGoodsQty(String qty){
    	Long goods_qty = null;
    	try{
    		goods_qty = Long.valueOf(qty);
    	}catch(Exception e){goods_qty = Long.valueOf(0);}
    	this.entity.setProperty("goodsQty", goods_qty);
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
    public long getProcessed(){
    	Long processed = (Long) this.entity.getProperty("processed");
    	return processed != null ? processed.longValue() : 0;
    }
    public void setProcessd(long processed){
    	try{
    		this.entity.setProperty("processed", Long.valueOf(processed));
    	}catch(Exception e){}
    }
    public void setProcessed(String processed){
    	Long proc = null;
    	try{
    		proc = Long.valueOf(processed);
    	}catch(Exception e){proc = Long.valueOf(0);}
    	this.entity.setProperty("processed", proc);
    }

    public static void xmlMallDaily(xMallDaily mall_daily, org.w3c.dom.Element result){
    	DecimalFormat df = new DecimalFormat("#######.##");
    	org.w3c.dom.Document doc = result.getOwnerDocument();
    	result.appendChild(doc.createElement("mall-fid")).setTextContent(mall_daily.getMallFid());
    	result.appendChild(doc.createElement("name")).setTextContent(mall_daily.getName());
    	result.appendChild(doc.createElement("date")).setTextContent(mall_daily.getDateString());
    	result.appendChild(doc.createElement("total")).setTextContent(df.format(mall_daily.getTotal()));
    	result.appendChild(doc.createElement("bonus-m2c")).setTextContent(String.valueOf(mall_daily.getBonusM2C()));
       	result.appendChild(doc.createElement("bonus-c2m")).setTextContent(String.valueOf(mall_daily.getBonusC2M()));
       	result.appendChild(doc.createElement("goods-qty")).setTextContent(String.valueOf(mall_daily.getGoodsQty()));
       	result.appendChild(doc.createElement("last")).setTextContent(mall_daily.getLastString());
       	result.appendChild(doc.createElement("tz-offset")).setTextContent(String.valueOf(mall_daily.getTzOffset()));
    	result.appendChild(doc.createElement("processed")).setTextContent(String.valueOf(mall_daily.getProcessed()));
    }
}
