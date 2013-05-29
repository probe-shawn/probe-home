package xlive.method.rst.customer;

import java.util.Date;
import xlive.xUtility;
import xlive.method.rst.order.xOrder;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;

public class xCustomer {  
	/*
    private Key key;
    private String name;
    private String id;
    private String gid;
    private String fid;
    private Date createDate;
    private int bonus=0;
    private String credit;
    */
    public Entity entity;
    public static String bonusType="cust";
    public xCustomer(Entity entity){
    	this.entity=entity;
    }
    public static Key generateKey(String fid){
    	return KeyFactory.createKey(xCustomer.class.getSimpleName(), fid);
    }
    
    public String getId(){
    	return (String)entity.getProperty("id");
    }
    public void setId(String id){
    	entity.setProperty("id", id);
    }
    public String getName(){
    	return (String)entity.getProperty("name");
    }
    public void setName(String name){
    	entity.setProperty("name", name);
    }
    public String getGid(){
    	return (String)entity.getProperty("gid");
    }
    public void setGid(String gid){
    	entity.setProperty("gid", gid);
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
    public String getCredit(){
    	return (String)entity.getProperty("credit");
    }
    public void setCredit(String credit){
    	entity.setProperty("credit", credit);
    }
    public String getBonusType(){
    	return xCustomer.bonusType;
    }
    public static void xmlCustomer(xCustomer cust, org.w3c.dom.Element result, long bonus,long bonus_total,long bonus_used,String last_date_string){
    	org.w3c.dom.Document doc = result.getOwnerDocument();
    	result.appendChild(doc.createElement("id")).setTextContent(cust.getId());
    	result.appendChild(doc.createElement("name")).setTextContent(cust.getName());
    	result.appendChild(doc.createElement("gid")).setTextContent(cust.getGid());
    	result.appendChild(doc.createElement("fid")).setTextContent(cust.getFid());
    	result.appendChild(doc.createElement("create-date")).setTextContent(cust.getCreateDateString());
    	result.appendChild(doc.createElement("last-date")).setTextContent(last_date_string);
    	result.appendChild(doc.createElement("bonus")).setTextContent(String.valueOf(bonus));
    	result.appendChild(doc.createElement("bonus-total")).setTextContent(String.valueOf(bonus_total));
    	result.appendChild(doc.createElement("bonus-used")).setTextContent(String.valueOf(bonus_used));
    	result.appendChild(doc.createElement("credit")).setTextContent(cust.getCredit());
    	result.appendChild(doc.createElement("bonus-type")).setTextContent(cust.getBonusType());
    }    
    public static void xmlCustomerPxmlForGetCustomer(xCustomer cust, org.w3c.dom.Element result,long bonus,long bonus_total,long bonus_used,String last_date_string){
    	org.w3c.dom.Document doc = result.getOwnerDocument();
    	org.w3c.dom.Element name,last_date,bonus_element,bonus_total_element;//,credit;
    	result.appendChild(doc.createElement("id")).setTextContent(cust.getId());
    	result.appendChild(name=doc.createElement("name")).setTextContent(cust.getName());
    	result.appendChild(doc.createElement("gid")).setTextContent(cust.getGid());
    	result.appendChild(doc.createElement("fid")).setTextContent(cust.getFid());
    	result.appendChild(doc.createElement("create-date")).setTextContent(cust.getCreateDateString());
    	result.appendChild(last_date=doc.createElement("last-date")).setTextContent(last_date_string);
    	result.appendChild(bonus_total_element=doc.createElement("bonus-total")).setTextContent(String.valueOf(bonus_total));
    	result.appendChild(bonus_element=doc.createElement("bonus")).setTextContent(String.valueOf(bonus));
    	result.appendChild(doc.createElement("bonus-used")).setTextContent(String.valueOf(bonus_used));
    	result.appendChild(doc.createElement("credit")).setTextContent(cust.getCredit());
    	result.appendChild(doc.createElement("bonus-type")).setTextContent(cust.getBonusType());
    	//
    	result.setAttribute("pxml", "pxml");
    	result.setAttribute("name", "ユ魁");
    	name.setAttribute("pxml", "text");
    	name.setAttribute("name", "嘿");
    	bonus_element.setAttribute("pxml", "text");
    	bonus_element.setAttribute("name", "緇肂");
    	bonus_total_element.setAttribute("pxml", "text");
    	bonus_total_element.setAttribute("name", "莉秘");
    	last_date.setAttribute("pxml", "date");
    	last_date.setAttribute("name", "程ㄓ砐");
    	last_date.setAttribute("readonly", "true");
    	
    }
    public static void xmlCustomerOrders(xCustomer cust, org.w3c.dom.Element result){
    	org.w3c.dom.Document doc = result.getOwnerDocument();
    	org.w3c.dom.Element orders_element = (org.w3c.dom.Element)result.appendChild(doc.createElement("orders"));
    	
    	DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
    	Query q = new Query("xOrder");
    	q.setFilter(new Query.FilterPredicate("customerFid", Query.FilterOperator.EQUAL, cust.getFid()));
    	
    	PreparedQuery pq = ds.prepare(q);

    	for(Entity found : pq.asIterable()) {
			org.w3c.dom.Element one = (org.w3c.dom.Element)orders_element.appendChild(doc.createElement("order"));
			xOrder.xmlOrder(new xOrder(found), one); 
    	}
     }
    
    public static void xmlCustomerOrdersPxmlForGetCustomer(xCustomer cust, org.w3c.dom.Element result){
    	String order_new="/xlive/images/rst/order_new.png";
    	String order_status="/xlive/images/rst/order_status.png";
    	String order_commit="/xlive/images/rst/order_commit.png";
    	String order_cancel="/xlive/images/rst/order_cancel.png";
    	org.w3c.dom.Document doc = result.getOwnerDocument();
    	org.w3c.dom.Element orders_element = (org.w3c.dom.Element)result.appendChild(doc.createElement("orders"));
    	
    	DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
    	Query q = new Query("xOrder");
    	q.setFilter(new Query.FilterPredicate("customerFid", Query.FilterOperator.EQUAL, cust.getFid()));
    	
    	q.addSort("date", Query.SortDirection.DESCENDING);
    	PreparedQuery pq = ds.prepare(q);
    	int count = 0;
    	org.w3c.dom.Element sect_element = null;
    	int from=0;
    	for(Entity found : pq.asIterable()) {
    		if(count % 10 == 0) {
    			if(sect_element != null){
    				sect_element.setAttribute("name",((from==1)?"程":"ぇ玡") + "("+from+" - "+count+")");
    			}
    			sect_element = (org.w3c.dom.Element)result.appendChild(doc.createElement("order-set"));
    			sect_element.setAttribute("data", "10Ω");
    			result.appendChild(sect_element);
    			sect_element.setAttribute("pxml", "pxml");
    			from = count+1;
    		}
    		xOrder order = new xOrder(found);
			org.w3c.dom.Element one = (org.w3c.dom.Element)sect_element.appendChild(doc.createElement("order"));
			xOrder.xmlOrderPxmlForGetCustomer(order, one);
			one.setAttribute("pxml", "pxml");
			one.setAttribute("name", order.getDateString());
			long process=order.getProcessed();
			//String status = order.getStatus();
			long accept = order.getAccept();
			String icon=(process < 0)?order_cancel:
						(process > 0)?order_commit:
						(accept == 1)?order_status:order_new;	
			one.setAttribute("icon", icon);
			++count;
    	}
		if(sect_element != null){
			sect_element.setAttribute("name",((from==1)?"程":"ぇ玡") + "("+from+" - "+count+")");
			int times = (count%10);
			times =(times==0 && (count>=10 && (count%10)==0))?10:times;
			sect_element.setAttribute("data", times +"Ω");
		}
 		//
		orders_element.setAttribute("pxml", "text");
		orders_element.setAttribute("name", "ユΩ计");
		orders_element.setAttribute("count", String.valueOf(count));
		
		//orders_element.setAttribute("data", String.valueOf(count)+" Ω");
		orders_element.setTextContent(String.valueOf(count)+" Ω");
    }
    
}
