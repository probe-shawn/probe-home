package xlive.method.yup;

import java.util.Date;
import xlive.xUtility;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Text;
public class uProduct {
public Entity entity = null;
    

    public uProduct(Entity entity){
    	this.entity = entity;
    }
    public String getKey(){
    	return KeyFactory.keyToString(this.entity.getKey());
    }   
    public String getPropertyKey(){
    	return (String)entity.getProperty("property_key");
    }
    public void setPropertyKey(String property_key){
    	this.entity.setProperty("property_key", property_key);
    }
    public String getCid(){
    	return (String)entity.getProperty("cid");
    }
    public void setCid(String cid){
    	this.entity.setProperty("cid", cid);
    }
    public String getSid(){
    	return (String)entity.getProperty("sid");
    }
    public void setSid(String sid){
    	this.entity.setProperty("sid", sid);
    }
     public Date getDate(){
    	return (Date) this.entity.getProperty("date");
    }
    public String getDateString(){
    	Object date = this.entity.getProperty("date");
    	return (date != null && date instanceof Date) ? xUtility.formatDate((Date)date):"";
    }
    public void setDate(Date date){
    	this.entity.setProperty("date", date);
    }
    public void setDate(String date_str){
    	Date date=(date_str != null && date_str.trim().length() >= 8)? xUtility.parseDate(date_str):null;
    	this.entity.setProperty("date", date);
    }   
    public String getCode(){
    	return (String) this.entity.getProperty("code");
    }
    public void setCode(String code){
    	this.entity.setProperty("code", code);
    }
    public String getEmphasis(){
    	return (String) this.entity.getProperty("emphasis");
    }
    public void setEmphasis(String emphasis){
    	this.entity.setProperty("emphasis", emphasis);
    }
    public String getName(){
    	return (String) this.entity.getProperty("name");
    }
    public void setName(String name){
    	this.entity.setProperty("name", name);
    }
    
    public String getDisp(){
    	return (String) this.entity.getProperty("disp");
    }
    public void setDisp(String disp){
    	this.entity.setProperty("disp", disp);
    }
    public String getPrint(){
    	return (String) this.entity.getProperty("print");
    }
    public void setPrint(String print){
    	this.entity.setProperty("print", print);
    }
    public String getDesc(){
    	Object text = (Object)this.entity.getProperty("desc");
    	return (text != null && text instanceof Text) ? ((Text)text).getValue() : "";
    }
    public void setDesc(String desc){
    	this.entity.setProperty("desc", new Text(desc));
    }
    public String getSize(){
    	Object text = this.entity.getProperty("size");
    	return (text != null && text instanceof Text) ? ((Text)text).getValue() : "{}";
    }
    public void setSize(String size){
    	this.entity.setProperty("size", new Text(size));
    }

    public String getHH(){
    	return (String) this.entity.getProperty("hh");
    }
    public void setHH(String hh){
    	this.entity.setProperty("hh", hh);
    }
    public String getServiceUse(){
    	return (String) this.entity.getProperty("service_use");
    }
    public void setServiceUse(String service_use){
    	this.entity.setProperty("service_use", service_use);
    }
    public String getPreOrder(){
    	return (String) this.entity.getProperty("pre_order");
    }
    public void setPreOrder(String pre_order){
    	this.entity.setProperty("pre_order", pre_order);
    }
    public String getAllergy(){
    	Object text = this.entity.getProperty("allergy");
    	return (text != null && text instanceof Text) ? ((Text)text).getValue() : "";
    }
    public void setAllergy(String allergy){
    	this.entity.setProperty("allergy", new Text(allergy));
    }
    public String getOnline(){
    	return (String) this.entity.getProperty("online");
    }
    public void setOnline(String online){
    	this.entity.setProperty("online", online);
    }
    
    public Date getDate1(){
    	return (Date) this.entity.getProperty("date1");
    }
    public String getDate1String(){
    	Date date = (Date) this.entity.getProperty("date1");
    	return date != null ? xUtility.formatDate(date):"";
    }
    public void setDate1(Date date){
    	this.entity.setProperty("date1", date);
    }
    public void setDate1(String date_str){
    	Date date=(date_str != null && date_str.trim().length() >= 8)? xUtility.parseDate(date_str):null;
    	this.entity.setProperty("date1", date);
    }
    public Date getDate2(){
    	return (Date) this.entity.getProperty("date2");
    }
    public String getDate2String(){
    	Date date = (Date) this.entity.getProperty("date2");
    	return date != null ? xUtility.formatDate(date):"";
    }
    public void setDate2(Date date){
    	this.entity.setProperty("date2", date);
    }
    public void setDate2(String date_str){
    	Date date=(date_str != null && date_str.trim().length() >= 8)? xUtility.parseDate(date_str):null;
    	this.entity.setProperty("date2", date);
    }

    public String getOrderTime(){
    	return (String) this.entity.getProperty("ordertime");
    }
    public void setOrderTime(String ordertime){
    	this.entity.setProperty("ordertime", ordertime);
    }
    public String getTime1(){
    	return (String) this.entity.getProperty("time1");
    }
    public void setTime1(String time1){
    	this.entity.setProperty("time1", time1);
    }
    public String getTime2(){
    	return (String) this.entity.getProperty("time2");
    }
    public void setTime2(String time2){
    	this.entity.setProperty("time2", time2);
    }
    public String getWeekdayOnly(){
    	return (String) this.entity.getProperty("weekday_only");
    }
    public void setWeekdayOnly(String weekday_only){
    	this.entity.setProperty("weekday_only", weekday_only);
    }
    
    public String getExemptCoupon(){
    	return (String) this.entity.getProperty("exempt_coupon");
    }
    public void setExemptCoupon(String exempt_coupon){
    	this.entity.setProperty("exempt_coupon", exempt_coupon);
    }

    public String getIcon(){
    	Text text = (Text)this.entity.getProperty("icon");
    	return (text != null) ? text.getValue() : "";
    }
    public void setIcon(String icon){
    	this.entity.setProperty("icon", new Text(icon));
    }
    
    public String getProperty(){
    	Text text = (Text)this.entity.getProperty("property");
    	return (text != null) ? text.getValue() : "";
    }
    public void setProperty(String property){
    	this.entity.setProperty("property", new Text(property));
    }
    public String getSetMenu(){
    	Text text = (Text)this.entity.getProperty("setmenu");
    	return (text != null) ? text.getValue() : "";
    }
    public void setSetMenu(String setmenu){
    	this.entity.setProperty("setmenu", new Text(setmenu));
    }
    public double getSort(){
    	Double sort=(Double)this.entity.getProperty("sort");
    	return (sort != null)? sort.doubleValue():0;
    }
    public void setSort(double sort){
    	try{
    		this.entity.setProperty("sort", Double.valueOf(sort));
    	}catch(Exception e){}
    }
    public void setSort(String sort){
    	try{
    		this.entity.setProperty("sort", Double.valueOf(sort));
    	}catch(Exception e){}
    } 
    
    public static void xmluProduct(uProduct prod, org.w3c.dom.Element result){
    	
    	org.w3c.dom.Document doc = result.getOwnerDocument();
    	result.appendChild(doc.createElement("key")).setTextContent(prod.getKey());
    	result.appendChild(doc.createElement("cid")).setTextContent(prod.getCid());
    	result.appendChild(doc.createElement("sid")).setTextContent(prod.getCid());
    	result.appendChild(doc.createElement("emphasis")).setTextContent(prod.getEmphasis());
    	result.appendChild(doc.createElement("code")).setTextContent(prod.getCode());
    	result.appendChild(doc.createElement("name")).setTextContent(prod.getName());
    	result.appendChild(doc.createElement("disp")).setTextContent(prod.getDisp());
    	result.appendChild(doc.createElement("print")).setTextContent(prod.getPrint());
    	result.appendChild(doc.createElement("desc")).setTextContent(prod.getDesc());
    	result.appendChild(doc.createElement("date")).setTextContent(prod.getDateString());
    	result.appendChild(doc.createElement("size")).setTextContent(prod.getSize());
    	result.appendChild(doc.createElement("hh")).setTextContent(prod.getHH());
    	result.appendChild(doc.createElement("service_use")).setTextContent(prod.getServiceUse());
    	result.appendChild(doc.createElement("pre_order")).setTextContent(prod.getPreOrder());
    	result.appendChild(doc.createElement("allergy")).setTextContent(prod.getAllergy());
    	result.appendChild(doc.createElement("online")).setTextContent(prod.getOnline());
    	result.appendChild(doc.createElement("date1")).setTextContent(prod.getDate1String());
    	result.appendChild(doc.createElement("date2")).setTextContent(prod.getDate2String());
    	result.appendChild(doc.createElement("ordertime")).setTextContent(prod.getOrderTime());
    	result.appendChild(doc.createElement("time1")).setTextContent(prod.getTime1());
    	result.appendChild(doc.createElement("weekday_only")).setTextContent(prod.getWeekdayOnly());
    	result.appendChild(doc.createElement("exempt_coupon")).setTextContent(prod.getExemptCoupon());
    	result.appendChild(doc.createElement("icon")).setTextContent(prod.getIcon());
    	result.appendChild(doc.createElement("property_key")).setTextContent(prod.getPropertyKey());
    	result.appendChild(doc.createElement("property")).setTextContent(prod.getProperty());
    	result.appendChild(doc.createElement("setmenu")).setTextContent(prod.getSetMenu());
    	result.appendChild(doc.createElement("sort")).setTextContent(String.valueOf(prod.getSort()));	
    	
    	
 	
    }
}
