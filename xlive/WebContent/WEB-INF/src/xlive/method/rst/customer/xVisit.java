package xlive.method.rst.customer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import xlive.xUtility;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

public class xVisit {  
	/*
    private String fid;
    private Date date;
    private String mallFid;
    */
    public Entity entity;
    public xVisit(Entity entity){
    	this.entity=entity;
    }
    public static Key generateKey(String fid, String mall_fid){
    	return KeyFactory.createKey(xVisit.class.getSimpleName(), fid+"-"+mall_fid);
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
    public String getMallFid(){
    	return (String)entity.getProperty("mallFid");
    }
    public void setMallFid(String mallfid){
    	entity.setProperty("mallFid", mallfid);
    }
    public String getMallName(){
    	return (String)entity.getProperty("mallName");
    }
    public void setMallName(String mallname){
    	entity.setProperty("mallName", mallname);
    }
    public String getType(){
    	return (String)entity.getProperty("type");
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
    public long getCount(){
    	Long count = (Long)entity.getProperty("count");
    	return count != null ? count.longValue() : 0;
    }
    public void setCount(long count){
    	entity.setProperty("count", Long.valueOf(count));
    }
	@SuppressWarnings("unchecked")
	public List<Date> getDateList(){
    	return (List<Date>)this.entity.getProperty("dateList");
    }
    public void setDateList(List<Date> date_list){
    	this.entity.setProperty("dateList", date_list);
    }
	@SuppressWarnings("unchecked")
	public void addDate(Date date){
    	List<Date> date_list = (List<Date>)this.entity.getProperty("dateList");
    	if(date_list == null) date_list = new ArrayList<Date>();
    	date_list.add(date);
    	this.entity.setProperty("dateList", date_list);
    }
    public static void xmlVisit(xVisit visit, org.w3c.dom.Element result){
    	org.w3c.dom.Document doc = result.getOwnerDocument();
    	result.appendChild(doc.createElement("key")).setTextContent(KeyFactory.keyToString(visit.entity.getKey()));
       	result.appendChild(doc.createElement("fid")).setTextContent(visit.getFid());
       	result.appendChild(doc.createElement("name")).setTextContent(visit.getName());
    	result.appendChild(doc.createElement("date")).setTextContent(visit.getDateString());
    	result.appendChild(doc.createElement("mall-fid")).setTextContent(visit.getMallFid());
    	result.appendChild(doc.createElement("mall-name")).setTextContent(visit.getMallName());
    	result.appendChild(doc.createElement("count")).setTextContent(Long.valueOf(visit.getCount()).toString());
    }    
}
