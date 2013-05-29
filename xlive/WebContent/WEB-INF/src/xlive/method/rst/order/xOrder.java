package xlive.method.rst.order;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import xlive.xUtility;
import xlive.method.rst.mall.xMall;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;

public class xOrder {  
	/*
    private Key key;
    private String id;
    private String name;
    private Date date;
    private String phone;
    private String addr;
    private String mail;
    private String deliver;
    private Date date2;
    private String time2;
    private float total=0;
    private int discount=1;
    
    private int bonusM2C=0;
    private int bonusC2M=0;
    
    private String payment;
    private String operator;
    private int processed=0;
    private String note;
    private String status;
    private Date last;
    private String mallFid;
    private String customerFid;
    private int goods;
    private List<String> limitGoods;
    private long fareDelivery;
    private String summary;
    private int accept=0;
    //
    */
	public Entity entity;
    public static String bonusType="order";

    
    public xOrder(Entity entity){
    	this.entity=entity;
    }
    
    public static Key generateKey(String mall_id, String order_id){
    	return KeyFactory.createKey(KeyFactory.createKey(xMall.class.getSimpleName(),mall_id), xOrder.class.getSimpleName(),order_id);
    }
	
    public String getId(){
    	return (String)this.entity.getProperty("id");
    }
    public void setId(String id){
    	this.entity.setProperty("id", id);
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
    
    public String getPhone(){
    	return (String)this.entity.getProperty("phone");
    }
    public void setPhone(String phone){
    	this.entity.setProperty("phone", phone);
    }
    
    public String getAddr(){
    	return (String)this.entity.getProperty("addr");
    }
    public void setAddr(String addr){
    	this.entity.setProperty("addr", addr);
    }

    public String getMail(){
    	return (String)this.entity.getProperty("mail");
    }
    public void setMail(String mail){
    	this.entity.setProperty("mail", mail);
    }

    public String getDeliver(){
    	return (String)this.entity.getProperty("deliver");
    }
    public void setDeliver(String deliver){
    	this.entity.setProperty("deliver", deliver);
    }
    
    public Date getDate2(){
    	return (Date)this.entity.getProperty("date2");
    }
    public String getDate2String(){
    	Date date2 = (Date)this.entity.getProperty("date2");
    	return date2 != null ? xUtility.formatDate(date2):"";
    }
    public void setDate2(Date date2){
    	this.entity.setProperty("date2", date2);
    }
    public void setDate2(String date_str){
    	Date date2=(date_str != null && date_str.trim().length() >= 8)? xUtility.parseDate(date_str):null;
    	this.entity.setProperty("date2", date2);
    }
    public String getTime2(){
    	return (String)this.entity.getProperty("time2");
    }
    public void setTime2(String time2){
    	this.entity.setProperty("time2", time2);
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

    public long getDiscount(){
    	Long discount = (Long) this.entity.getProperty("discount");
    	return discount != null? discount.longValue() : 0;
    }
    public void setDiscount(long discount){
    	try{
    		this.entity.setProperty("discount", Long.valueOf(discount));
    	}catch(Exception e){}
    }
    public void setDiscount(String discount){
    	try{
    		this.entity.setProperty("discount", Long.valueOf(discount));
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
    public String getBonusType(){
    	return xOrder.bonusType;
    }

    public String getPayment(){
    	return (String) this.entity.getProperty("payment");
    }
    public void setPayment(String payment){
    	this.entity.setProperty("payment", payment);
    }
    public String getOperator(){
    	return (String) this.entity.getProperty("operator");
    }
    public void setOperator(String operator){
    	this.entity.setProperty("operator", operator);
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
    public String getNote(){
    	return (String) this.entity.getProperty("note");
    }
    public void setNote(String note){
    	this.entity.setProperty("note", note);
    }
    public String getStatus(){
    	return (String) this.entity.getProperty("status");
    }
    public void setStatus(String status){
    	this.entity.setProperty("status", status);
    }
    public Date getLast(){
    	return (Date)this.entity.getProperty("last");
    }
    public String getLastString(){
    	Date last = (Date)this.entity.getProperty("last");
    	return last != null ? xUtility.formatDate(last):"";
    }
    public void setLast(Date last){
    	this.entity.setProperty("last", last);
    }
    public void setLast(String last_str){
    	Date last=(last_str != null && last_str.trim().length() >= 8)? xUtility.parseDate(last_str):null;
    	this.entity.setProperty("last", last);
    }
    public void setMallFid(String mallfid){
    	this.entity.setProperty("mallFid", mallfid);
    }
    public String getMallFid(){
    	return (String) this.entity.getProperty("mallFid");
    }
    public void setCustomerFid(String custfid){
    	this.entity.setProperty("customerFid", custfid);
    }
    public String getCustomerFid(){
    	return (String) this.entity.getProperty("customerFid");
    }
    public long getGoods(){
    	Long goods = (Long) this.entity.getProperty("goods");
    	return goods != null? goods.longValue() : 0;
    }
    public void setGoods(long goods){
    	try{
    		this.entity.setProperty("goods", Long.valueOf(goods));
    	}catch(Exception e){}
    }
    public void setGoods(String goods){
    	try{
    		this.entity.setProperty("goods", Long.valueOf(goods));
    	}catch(Exception e){}
    }
    public long getFareDelivery(){
    	Long fare_delivery = (Long) this.entity.getProperty("fareDelivery");
    	return fare_delivery != null? fare_delivery.longValue() : 0;
    }
    public void setFareDelivery(long fare_delivery){
    	try{
    		this.entity.setProperty("fareDelivery", Long.valueOf(fare_delivery));
    	}catch(Exception e){}
    }
    public void setFareDelivery(String fare_delivery){
    	try{
    		this.entity.setProperty("fareDelivery", Long.valueOf(fare_delivery));
    	}catch(Exception e){}
    }
    
    public void setSummary(String summary){
    	this.entity.setProperty("summary", summary);
    }
    public String getSummary(){
    	return (String) this.entity.getProperty("summary");
    }
    public long getAccept(){
    	Long accept = (Long) this.entity.getProperty("accept");
    	return accept != null ? accept.longValue() : 0;
    }
    public void setAccept(long accept){
    	try{
    		this.entity.setProperty("accept", Long.valueOf(accept));
    	}catch(Exception e){}
    }
    public void setAccept(String accept){
    	Long acc = null;
    	try{
    		acc = Long.valueOf(accept);
    	}catch(Exception e){acc = Long.valueOf(0);}
    	this.entity.setProperty("accept", acc);
    }

    
	@SuppressWarnings("unchecked")
	public List<String> getLimitGoodsQty(){
    	return (List<String>)this.entity.getProperty("limitGoodsQty");
    }
    public void setLimitGoodsQty(List<String> limit_goods){
    	this.entity.setProperty("limitGoodsQty", limit_goods);
    }
	@SuppressWarnings("unchecked")
	public void addLimitGoodsQty(String goods, long qty){
    	List<String> limit_goods = (List<String>)this.entity.getProperty("limitGoodsQty");
    	if(limit_goods == null) {
    		limit_goods = new ArrayList<String>();
    		limit_goods.add(goods+";"+String.valueOf(qty));
    	}else{
    		long exisetd_qty = 0;
    		for(int i = 0; i < limit_goods.size();++i){
    			String str = limit_goods.get(i);
    			if(str.startsWith(goods+";")){
    				String[] key_qty=str.split(";");
    				exisetd_qty=Long.valueOf(key_qty[1]);
    				limit_goods.remove(i);
    			}
    		}
    		limit_goods.add(goods+";"+String.valueOf(qty+exisetd_qty));
    	}
    	
    	this.entity.setProperty("limitGoodsQty", limit_goods);
    }
    
    public static void xmlOrder(xOrder order, org.w3c.dom.Element result){
    	DecimalFormat df = new DecimalFormat("#######.##");
    	org.w3c.dom.Document doc = result.getOwnerDocument();
    	result.appendChild(doc.createElement("key")).setTextContent(KeyFactory.keyToString(order.entity.getKey()));
    	result.appendChild(doc.createElement("id")).setTextContent(order.getId());
    	result.appendChild(doc.createElement("name")).setTextContent(order.getName());
    	result.appendChild(doc.createElement("date")).setTextContent(order.getDateString());
    	result.appendChild(doc.createElement("phone")).setTextContent(order.getPhone());
    	result.appendChild(doc.createElement("addr")).setTextContent(order.getAddr());
    	result.appendChild(doc.createElement("mail")).setTextContent(order.getMail());
    	result.appendChild(doc.createElement("deliver")).setTextContent(order.getDeliver());
    	result.appendChild(doc.createElement("date2")).setTextContent(order.getDate2String());
    	result.appendChild(doc.createElement("time2")).setTextContent(order.getTime2());
    	result.appendChild(doc.createElement("total")).setTextContent(df.format(order.getTotal()));
    	result.appendChild(doc.createElement("discount")).setTextContent(String.valueOf(order.getDiscount()));
    	result.appendChild(doc.createElement("bonus-m2c")).setTextContent(String.valueOf(order.getBonusM2C()));
       	result.appendChild(doc.createElement("bonus-c2m")).setTextContent(String.valueOf(order.getBonusC2M()));
       	result.appendChild(doc.createElement("bonus-type")).setTextContent(order.getBonusType());
    	result.appendChild(doc.createElement("payment")).setTextContent(order.getPayment());
    	result.appendChild(doc.createElement("operator")).setTextContent(order.getOperator());
    	result.appendChild(doc.createElement("processed")).setTextContent(String.valueOf(order.getProcessed()));
       	result.appendChild(doc.createElement("note")).setTextContent(order.getNote());
       	result.appendChild(doc.createElement("status")).setTextContent(order.getStatus());
       	result.appendChild(doc.createElement("last")).setTextContent(order.getLastString());
       	result.appendChild(doc.createElement("mall-fid")).setTextContent(order.getMallFid());
       	result.appendChild(doc.createElement("customer-fid")).setTextContent(order.getCustomerFid());
       	result.appendChild(doc.createElement("goods")).setTextContent(String.valueOf(order.getGoods()));
       	result.appendChild(doc.createElement("fare-delivery")).setTextContent(String.valueOf(order.getFareDelivery()));
     	result.appendChild(doc.createElement("summary")).setTextContent(order.getSummary());
       	result.appendChild(doc.createElement("accept")).setTextContent(String.valueOf(order.getAccept()));    	
       	long proc=order.getProcessed();
		long accept=order.getAccept();
		String text=(proc < 0)? "已取消":
					(proc > 0)? "已完成":
					(accept==1)?"作業中":"新進待確認";
		result.appendChild(doc.createElement("trans")).setTextContent(text);
       	
       	
   }
    public static void xmlOrderPxmlForGetCustomer(xOrder order, org.w3c.dom.Element result){
    	DecimalFormat df = new DecimalFormat("#######.##");
    	org.w3c.dom.Document doc = result.getOwnerDocument();
    	org.w3c.dom.Element id,name,date,phone,total,note,bonusm2c,bonusc2m,status,trans;
    	result.appendChild(doc.createElement("key")).setTextContent(KeyFactory.keyToString(order.entity.getKey()));
    	result.appendChild(id=doc.createElement("id")).setTextContent(order.getId());
    	result.appendChild(name=doc.createElement("name")).setTextContent(order.getName());
    	result.appendChild(date=doc.createElement("date")).setTextContent(order.getDateString());
    	result.appendChild(phone=doc.createElement("phone")).setTextContent(order.getPhone());
    	result.appendChild(doc.createElement("addr")).setTextContent(order.getAddr());
    	result.appendChild(doc.createElement("mail")).setTextContent(order.getMail());
    	result.appendChild(doc.createElement("deliver")).setTextContent(order.getDeliver());
    	result.appendChild(doc.createElement("date2")).setTextContent(order.getDate2String());
    	result.appendChild(doc.createElement("time2")).setTextContent(order.getTime2());
    	result.appendChild(total=doc.createElement("total")).setTextContent(df.format(order.getTotal()));
    	result.appendChild(doc.createElement("discount")).setTextContent(String.valueOf(order.getDiscount()));
    	result.appendChild(bonusm2c=doc.createElement("bonus-m2c")).setTextContent(String.valueOf(order.getBonusM2C()));
    	result.appendChild(bonusc2m=doc.createElement("bonus-c2m")).setTextContent(String.valueOf(order.getBonusC2M()));
       	result.appendChild(doc.createElement("bonus-type")).setTextContent(order.getBonusType());
    	result.appendChild(doc.createElement("payment")).setTextContent(order.getPayment());
    	result.appendChild(doc.createElement("operator")).setTextContent(order.getOperator());
    	result.appendChild(doc.createElement("processed")).setTextContent(String.valueOf(order.getProcessed()));
    	result.appendChild(note=doc.createElement("note")).setTextContent(order.getNote());
    	result.appendChild(status=doc.createElement("status")).setTextContent(order.getStatus());
    	result.appendChild(doc.createElement("last")).setTextContent(order.getLastString());
       	result.appendChild(doc.createElement("mall-fid")).setTextContent(order.getMallFid());
       	result.appendChild(doc.createElement("customer-fid")).setTextContent(order.getCustomerFid());
       	result.appendChild(doc.createElement("goods")).setTextContent(String.valueOf(order.getGoods()));
       	result.appendChild(doc.createElement("fare-delivery")).setTextContent(String.valueOf(order.getFareDelivery()));
       	result.appendChild(doc.createElement("summary")).setTextContent(order.getSummary());
       	result.appendChild(doc.createElement("accept")).setTextContent(String.valueOf(order.getAccept()));
       	result.appendChild(trans=doc.createElement("trans")).setTextContent("");
    	id.setAttribute("pxml", "text");
    	id.setAttribute("name", "單號");
    	name.setAttribute("pxml", "text");
    	name.setAttribute("name", "名稱");
    	date.setAttribute("pxml", "text");
    	date.setAttribute("name", "日期");
    	phone.setAttribute("pxml", "text");
    	phone.setAttribute("name", "電話");
    	total.setAttribute("pxml", "text");
    	total.setAttribute("name", "金額");
    	bonusc2m.setAttribute("pxml", "text");
    	bonusc2m.setAttribute("name", "使用紅利");
    	bonusm2c.setAttribute("pxml", "text");
    	bonusm2c.setAttribute("name", "獲贈紅利");
    	note.setAttribute("pxml", "text");
    	note.setAttribute("name", "備註");
    	status.setAttribute("pxml", "text");
    	status.setAttribute("name", "狀態");
    	//
    	trans.setAttribute("pxml", "text");
    	trans.setAttribute("name", "訂單交易");
		long proc=order.getProcessed();
		//String stat = order.getStatus();
		long accept=order.getAccept();
		String text=(proc < 0)? "已取消":
					(proc > 0)? "已完成":
					(accept==1)?"作業中":"新進待確認";
		trans.setTextContent(text);

    	
    }
    public static void xmlOrderGoods(xOrder order, org.w3c.dom.Element result){
    	org.w3c.dom.Document doc = result.getOwnerDocument();
    	org.w3c.dom.Element goods_list = (org.w3c.dom.Element)result.appendChild(doc.createElement("goods-list"));
    	
    	DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
    	Query q = new Query(xGoods.class.getSimpleName(), order.entity.getKey());
    	PreparedQuery pq = ds.prepare(q);
    	int qty_count=0;
    	int count = 0;
    	for(Entity found : pq.asIterable()) {
    		org.w3c.dom.Element one = (org.w3c.dom.Element)goods_list.appendChild(doc.createElement("goods"));
    		xGoods goods = new xGoods(found);
    		xGoods.xmlGoods(goods, one);
    		qty_count += goods.getQty();
    		++count; 
    	}
    	goods_list.setAttribute("count", String.valueOf(count));
    	goods_list.setAttribute("countQty", String.valueOf(qty_count));
    }
    public static void xmlOrderDaily(xOrder order, org.w3c.dom.Element result){
    	DecimalFormat df = new DecimalFormat("#######.##");
    	org.w3c.dom.Document doc = result.getOwnerDocument();
    	result.appendChild(doc.createElement("id")).setTextContent(order.getId());
    	result.appendChild(doc.createElement("name")).setTextContent(order.getName());
    	result.appendChild(doc.createElement("date")).setTextContent(order.getDateString());
    	result.appendChild(doc.createElement("total")).setTextContent(df.format(order.getTotal()));
    	result.appendChild(doc.createElement("bonus-m2c")).setTextContent(String.valueOf(order.getBonusM2C()));
       	result.appendChild(doc.createElement("bonus-c2m")).setTextContent(String.valueOf(order.getBonusC2M()));
    	result.appendChild(doc.createElement("processed")).setTextContent(String.valueOf(order.getProcessed()));
       	result.appendChild(doc.createElement("last")).setTextContent(order.getLastString());
       	result.appendChild(doc.createElement("mall-fid")).setTextContent(order.getMallFid());
       	result.appendChild(doc.createElement("customer-fid")).setTextContent(order.getCustomerFid());
       	result.appendChild(doc.createElement("goods")).setTextContent(String.valueOf(order.getGoods()));
       	result.appendChild(doc.createElement("fare-delivery")).setTextContent(String.valueOf(order.getFareDelivery()));
    }

}
