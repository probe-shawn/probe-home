package xlive.method.rst.finance;

import java.util.Date;
import xlive.xUtility;
import xlive.method.rst.mall.xMallDetail;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

public class xBillDetail {

public Entity entity = null;
    
    public xBillDetail(Entity entity){
    	this.entity = entity;
    }
    public static Key generateKey(String bid){
    	return KeyFactory.createKey(xBillDetail.class.getSimpleName(), bid);
    }
   
    public String getBid(){
    	return (String)entity.getProperty("bid");
    }
    public void setBid(String bid){
    	this.entity.setProperty("bid", bid);
    }
    public String getFid(){
    	return (String)entity.getProperty("fid");
    }
    public void setFid(String fid){
    	this.entity.setProperty("fid", fid);
    }
    
    
    public String getLink(){
    	return (String)entity.getProperty("link");
    }
    public void setLink(String link){
    	this.entity.setProperty("link", link);
    }
    
    public Date getCreateDate(){
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
    
    public String getType(){
    	return (String) this.entity.getProperty("type");
    }
    public void setType(String type){
    	this.entity.setProperty("type", type);
    }
    //¼Æ¶q
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
    
    //ª÷ÃB
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
    
    
    public String getRemark(){
    	return (String)entity.getProperty("remark");
    }
    public void setRemark(String remark){
    	this.entity.setProperty("remark", remark);
    }
    
    public String getATMcode(){
    	return (String)entity.getProperty("atmcode");
    }
    public void setATMcode(String atmcode){
    	this.entity.setProperty("atmcode", atmcode);
    }
    public String getBarcode1(){
    	return (String)entity.getProperty("barcode1");
    }
    public void setBarcode1(String barcode1){
    	this.entity.setProperty("barcode1", barcode1);
    }
    
    public String getBarcode2(){
    	return (String)entity.getProperty("barcode2");
    }
    public void setBarcode2(String barcode2){
    	this.entity.setProperty("barcode2", barcode2);
    }
    
    public String getBarcode3(){
    	return (String)entity.getProperty("barcode3");
    }
    public void setBarcode3(String barcode3){
    	this.entity.setProperty("barcode3", barcode3);
    }
    
   
    public long getWriteOff(){
    	Long writeoff=(Long)this.entity.getProperty("writeoff");
    	return (writeoff != null)? writeoff.longValue():0;
    }
    public void setWriteOff(long writeoff){
    	try{
    		this.entity.setProperty("writeoff", Long.valueOf(writeoff));
    	}catch(Exception e){}
    }
    public void setWriteOff(String writeoff){
    	try{
    		this.entity.setProperty("writeoff", Long.valueOf(writeoff));
    	}catch(Exception e){}
    }
    
    public String getDoneFlag(){
    	return (String)entity.getProperty("doneflag");
    }
    public void setDoneFlag(String doneflag){
    	this.entity.setProperty("doneflag", doneflag);
    }
    
    public String getIncomeString(){
    	if(entity.hasProperty("incomestring"))
    		return (String)entity.getProperty("incomestring");
    	else    	
    	   return "";
    }
    public void setIncomeString(String istr){
    	this.entity.setProperty("incomestring", istr);
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
    public static void xmlBillDetail(xBillDetail bill_detail, org.w3c.dom.Element result){
    	
    	org.w3c.dom.Document doc = result.getOwnerDocument();
    	result.appendChild(doc.createElement("bid")).setTextContent(bill_detail.getBid());
    	result.appendChild(doc.createElement("cdate")).setTextContent(bill_detail.getCreateDateString());
    	result.appendChild(doc.createElement("type")).setTextContent(bill_detail.getType());
    	result.appendChild(doc.createElement("qty")).setTextContent(String.valueOf(bill_detail.getQTY()));
    	result.appendChild(doc.createElement("amount")).setTextContent(String.valueOf(bill_detail.getAmount()));
    	result.appendChild(doc.createElement("remark")).setTextContent(bill_detail.getRemark());
    	result.appendChild(doc.createElement("atmcode")).setTextContent(bill_detail.getATMcode());
    	result.appendChild(doc.createElement("barcode1")).setTextContent(bill_detail.getBarcode1());
    	result.appendChild(doc.createElement("barcode2")).setTextContent(bill_detail.getBarcode2());
    	result.appendChild(doc.createElement("barcode3")).setTextContent(bill_detail.getBarcode3());
    	result.appendChild(doc.createElement("writeoff")).setTextContent(String.valueOf(bill_detail.getWriteOff()));
       	
    }
   

}
