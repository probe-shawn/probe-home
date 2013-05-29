package xlive.method.rst.finance;

import java.util.Date;
import xlive.xUtility;
import xlive.method.rst.mall.xMallDetail;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

public class xServiceFee {

public Entity entity = null;
    
    public xServiceFee(Entity entity){
    	this.entity = entity;
    }
    
    public static Key generateKey(String mall_fid,String date,String type){
    	return KeyFactory.createKey(xServiceFee.class.getSimpleName(), mall_fid+date+type);
    }
    
    public String getKeyString(){ 
    	return KeyFactory.keyToString(entity.getKey());
    }
    
    public String getFid(){ //mall_id
    	return (String)entity.getProperty("fid");
    }
    public void setFid(String fid){
    	this.entity.setProperty("fid", fid);
    }
    
    public String getType(){        //0�N��O��έp�O��,1�N��O���X�b��O��
    	return (String) this.entity.getProperty("type");
    }
    public void setType(String type){
    	this.entity.setProperty("type", type);
    }
    
    public Date getCreateDate(){         //�s�J�Ȭ��C��1��,�N��O���έp���
    	return (Date) this.entity.getProperty("create_date");
    }
    public String getCreateDateString(){
    	Date date = (Date) this.entity.getProperty("create_date");
    	return date != null ? xUtility.formatDate(date):"";
    }
    public void setCreateDate(Date date){
    	this.entity.setProperty("create_date", date);
    }
    public void setCreateDate(String date_str){
    	Date date=(date_str != null && date_str.trim().length() >= 8)? xUtility.parseDate(date_str):null;
    	this.entity.setProperty("create_date", date);
    }
    
   
    //����ƶq
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
    
    //�A�ȶO���B
    public long getAmount(){
    	Object amount=this.entity.getProperty("amount");
    	if(amount != null && amount instanceof String){
    		String amt_string=(String)amount;	
    		return (amt_string.trim().length() > 0) ? Long.valueOf(amt_string):0;
    	}
    	Long lamt=(Long)this.entity.getProperty("amount");
    	return (lamt != null)? lamt.longValue():0;
    }
    public void setAmount(long amt){
    	try{
    		this.entity.setProperty("amount", Long.valueOf(amt));
    	}catch(Exception e){}
    }
    public void setAmount(String amt){
    	try{
    		this.entity.setProperty("amount", Long.valueOf(amt));
    	}catch(Exception e){}
    }
    
    
   
    public long getBalance(){ //�֭p���B�]�W�L300�X�b��^
    	Long balance=(Long)this.entity.getProperty("balance");
    	return (balance != null)? balance.longValue():0;
    }
    public void setBalance(long balance){
    	try{
    		this.entity.setProperty("balance", Long.valueOf(balance));
    	}catch(Exception e){}
    }
    public void setBalance(String balance){
    	try{
    		this.entity.setProperty("balance", Long.valueOf(balance));
    	}catch(Exception e){}
    }
    
    //0,1�Ϥ�,�Y�ӵ��O��έp���,1�N���릳�X�b��,�Y�ӵ��O�b���ơA1�N��wú�O����
    public String getDoneFlag(){
    	return (String)entity.getProperty("doneflag");
    }
    public void setDoneFlag(String doneflag){
    	this.entity.setProperty("doneflag", doneflag);
    }
    
    
    public Date getLast(){ //�Ƨǥ����
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
    
public static void xmlServiceFee(xServiceFee sf, org.w3c.dom.Element result){
    	
    	org.w3c.dom.Document doc = result.getOwnerDocument();
    	result.appendChild(doc.createElement("key")).setTextContent(sf.getKeyString());
    	result.appendChild(doc.createElement("fid")).setTextContent(sf.getFid());
    	result.appendChild(doc.createElement("create_date")).setTextContent(sf.getCreateDateString());
    	result.appendChild(doc.createElement("type")).setTextContent(sf.getType());
    	result.appendChild(doc.createElement("qty")).setTextContent(String.valueOf(sf.getQTY()));
    	result.appendChild(doc.createElement("amount")).setTextContent(String.valueOf(sf.getAmount()));
    	result.appendChild(doc.createElement("balance")).setTextContent(String.valueOf(sf.getBalance()));
    	result.appendChild(doc.createElement("doneflag")).setTextContent(sf.getDoneFlag());
    	result.appendChild(doc.createElement("last")).setTextContent(sf.getLastString());
       	
    }
}
