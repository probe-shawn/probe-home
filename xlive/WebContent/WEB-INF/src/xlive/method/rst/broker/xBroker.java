package xlive.method.rst.broker;

import java.util.Date;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.KeyFactory;

import xlive.xUtility;

public class xBroker {  
	/*
				<fid/>
				<name/>
				<date/>
				<store-name pxml="input" name="店名" blank="必要欄位"/>
				<store-phone pxml="input" name="電話" blank="必要欄位"/>
				<store-addr pxml="input" name="店址"/>
				<mall-fid/>
				<state>0</state>
				<bonus-1>0</bonus-1>
				<bonus-2>0</bonus-2>
				<verify-date1/>
				<verify-date2/>
				<operator/>
				<note/>
    */
    public Entity entity;
    public static String bonusType="cust";
    public xBroker(Entity entity){
    	this.entity=entity;
    }
    public String getFid(){
    	return (String)entity.getProperty("fid");
    }
    public void setFid(String fid){
    	entity.setProperty("fid", fid);
    }
    public String getName(){
    	return (String)entity.getProperty("name");
    }
    public void setName(String name){
    	entity.setProperty("name", name);
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
    
    public String getStoreName(){
    	return (String)entity.getProperty("storeName");
    }
    public void setStoreName(String store_name){
    	entity.setProperty("storeName", store_name);
    }
    public String getStorePhone(){
    	return (String)entity.getProperty("storePhone");
    }
    public void setStorePhone(String store_phone){
    	entity.setProperty("storePhone", store_phone);
    }
    public String getStoreAddr(){
    	return (String)entity.getProperty("storeAddr");
    }
    public void setStoreAddr(String store_addr){
    	entity.setProperty("storeAddr", store_addr);
    }
    public String getMallFid(){
    	return (String)entity.getProperty("mallFid");
    }
    public void setMallFid(String mall_fid){
    	entity.setProperty("mallFid", mall_fid);
    }
    public long getState(){
    	Long state = (Long)entity.getProperty("state");
    	return state !=null ? state.longValue():0;
    }
    public void setState(long state){
    	entity.setProperty("state",Long.valueOf(state));
    }
    public void setState(String state){
    	try{
    		entity.setProperty("state",Long.valueOf(state));
    	}catch(Exception e){}
    }
    public long getBonus1(){
    	Long bonus1 = (Long)entity.getProperty("bonus1");
    	return bonus1 !=null ? bonus1.longValue():0;
    }
    public void setBonus1(long bonus){
    	entity.setProperty("bonus1",Long.valueOf(bonus));
    }
    public void setBonus1(String bonus){
    	try{
    		entity.setProperty("bonus1",Long.valueOf(bonus));
    	}catch(Exception e){}
    }
    public long getBonus2(){
    	Long bonus2 = (Long)entity.getProperty("bonus2");
    	return bonus2 !=null ? bonus2.longValue():0;
    }
    public void setBonus2(long bonus){
    	entity.setProperty("bonus2",Long.valueOf(bonus));
    }
    public void setBonus2(String bonus){
    	try{
    		entity.setProperty("bonus2",Long.valueOf(bonus));
    	}catch(Exception e){}
    }
    public Date getVerifyDate1(){
    	return (Date) entity.getProperty("verifyDate1");
    }
    public void setVerifyDate1(Date date){
    	entity.setProperty("verifyDate1",date);
    }
    public void setVerifyDate1(String date){
    	Date d =(date != null && date.trim().length() >= 8)? xUtility.parseDate(date):null;
    	entity.setProperty("verifyDate1",d);
    }
    public String getVerifyDate1String(){
    	Date d = (Date)entity.getProperty("verifyDate1");
    	return (d != null)? xUtility.formatDate(d):"";
    }
    public Date getVerifyDate2(){
    	return (Date) entity.getProperty("verifyDate2");
    }
    public void setVerifyDate2(Date date){
    	entity.setProperty("verifyDate2",date);
    }
    public void setVerifyDate2(String date){
    	Date d =(date != null && date.trim().length() >= 8)? xUtility.parseDate(date):null;
    	entity.setProperty("verifyDate2",d);
    }
    public String getVerifyDate2String(){
    	Date d = (Date)entity.getProperty("verifyDate2");
    	return (d != null)? xUtility.formatDate(d):"";
    }
    public String getOperator(){
    	return (String)entity.getProperty("operator");
    }
    public void setOperator(String operator){
    	entity.setProperty("operator", operator);
    }
    public String getNote(){
    	return (String)entity.getProperty("note");
    }
    public void setNote(String note){
    	entity.setProperty("note", note);
    }
    
    public static void xmlBroker(xBroker broker, org.w3c.dom.Element result){
    	org.w3c.dom.Document doc = result.getOwnerDocument();
    	result.appendChild(doc.createElement("key")).setTextContent(KeyFactory.keyToString(broker.entity.getKey()));
    	result.appendChild(doc.createElement("fid")).setTextContent(broker.getFid());
    	result.appendChild(doc.createElement("name")).setTextContent(broker.getName());
    	result.appendChild(doc.createElement("date")).setTextContent(broker.getDateString());
    	result.appendChild(doc.createElement("store-name")).setTextContent(broker.getStoreName());
    	result.appendChild(doc.createElement("store-phone")).setTextContent(broker.getStorePhone());
    	result.appendChild(doc.createElement("store-addr")).setTextContent(broker.getStoreAddr());
    	result.appendChild(doc.createElement("mall-fid")).setTextContent(broker.getMallFid());
    	
    	result.appendChild(doc.createElement("state")).setTextContent(String.valueOf(broker.getState()));
    	result.appendChild(doc.createElement("bonus-1")).setTextContent(String.valueOf(broker.getBonus1()));
    	result.appendChild(doc.createElement("bonus-2")).setTextContent(String.valueOf(broker.getBonus2()));
    	result.appendChild(doc.createElement("verfy-date1")).setTextContent(broker.getVerifyDate1String());
    	result.appendChild(doc.createElement("verfy-date2")).setTextContent(broker.getVerifyDate2String());
    	result.appendChild(doc.createElement("operator")).setTextContent(broker.getOperator());
    	result.appendChild(doc.createElement("note")).setTextContent(broker.getNote());
    }    
}
