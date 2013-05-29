package xlive.method.rst.circle;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import xlive.xUtility;
import xlive.xml.xXmlDocument;

import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.KeyFactory;
public class xDistrict {
public Entity entity = null;
    
    public xDistrict(Entity entity){
    	this.entity = entity;
    }
    public String getKey(){
    	return KeyFactory.keyToString(this.entity.getKey());
    }   
    
    public String getCircleFid(){
    	return (String)entity.getProperty("circleFid");
    }
    public void setCircleFid(String circle_fid){
    	this.entity.setProperty("circleFid", circle_fid);
    }
    
    public String getIcon(){
    	return (String) this.entity.getProperty("icon");
    }
    public void setIcon(String icon){
    	this.entity.setProperty("icon", icon);
    }
    @SuppressWarnings("unchecked")
	public List<String> getIcons(){
    	return (List<String>)this.entity.getProperty("icons");
    }
    public void setIcons(List<String> icons){
    	this.entity.setProperty("icons", icons);
    }
    @SuppressWarnings("unchecked")
	public void addIcon(String icon){
    	List<String> icons = (List<String>)this.entity.getProperty("icons");
    	if(icons == null) icons = new ArrayList<String>();
    	icons.add(icon);
    	this.entity.setProperty("icons", icons);
    }    
    
    public Date getDate(){
    	return (Date) this.entity.getProperty("date");
    }
    public String getDateString(){
    	Date date = (Date) this.entity.getProperty("date");
    	return date != null ? xUtility.formatDate(date):"";
    }
    public void setDate(Date date){
    	this.entity.setProperty("date", date);
    }
    public void setDate(String date_str){
    	Date date=(date_str != null && date_str.trim().length() >= 8)? xUtility.parseDate(date_str):null;
    	this.entity.setProperty("date", date);
    }   
    
    public String getName(){
    	return (String) this.entity.getProperty("name");
    }
    public void setName(String name){
    	this.entity.setProperty("name", name);
    }
    
    public String getDesc(){
    	return (String) this.entity.getProperty("desc");
    }
    public void setDesc(String desc){
    	this.entity.setProperty("desc", desc);
    }
    
    public String getStatus(){
    	return (String) this.entity.getProperty("status");
    }
    public void setStatus(String status){
    	this.entity.setProperty("status", status);
    }
      
    public long getQty(){
    	Long qty=(Long)this.entity.getProperty("qty");
    	return (qty != null)? qty.longValue():0;
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
    
  
    public long getSort(){
    	Long sort=(Long)this.entity.getProperty("sort");
    	return (sort != null)? sort.longValue():0;
    }
    public void setSort(long sort){
    	try{
    		this.entity.setProperty("sort", Long.valueOf(sort));
    	}catch(Exception e){}
    }
    public void setSort(String sort){
    	try{
    		this.entity.setProperty("sort", Long.valueOf(sort));
    	}catch(Exception e){}
    } 
    
    @SuppressWarnings("unchecked")
	public List<String> getNews(){
    	return (List<String>)this.entity.getProperty("news");
    }
    public void setNews(List<String> news){
    	this.entity.setProperty("news", news);
    }
    @SuppressWarnings("unchecked")
	public void addNew(String n){
    	List<String> news = (List<String>)this.entity.getProperty("news");
    	if(news == null) news = new ArrayList<String>();
    	news.add(n);
    	this.entity.setProperty("news", news);
    } 
    public byte[] getSpecXml(){
    	Blob specXml = (Blob) this.entity.getProperty("specXml");
		return (specXml != null) ? specXml.getBytes() : null;
    }
    public void setSpecXml(byte[] xml_bytes){
		Blob specXml = (xml_bytes == null||xml_bytes.length==0) ? null: new Blob(xml_bytes);
		this.entity.setProperty("specXml", specXml);
    }
    
    public static void xmlDistrict(xDistrict district, org.w3c.dom.Element result){
    	
    	org.w3c.dom.Document doc = result.getOwnerDocument();
    	result.appendChild(doc.createElement("key")).setTextContent(district.getKey());
    	result.appendChild(doc.createElement("circle-fid")).setTextContent(district.getCircleFid());
    	result.appendChild(doc.createElement("date")).setTextContent(district.getDateString());
    	result.appendChild(doc.createElement("name")).setTextContent(district.getName());
    	result.appendChild(doc.createElement("desc")).setTextContent(district.getDesc());
    	result.appendChild(doc.createElement("status")).setTextContent(district.getStatus());   	
    	result.appendChild(doc.createElement("qty")).setTextContent(String.valueOf(district.getQty()));
    	result.appendChild(doc.createElement("sort")).setTextContent(String.valueOf(district.getSort()));    	
    	result.appendChild(doc.createElement("icon")).setTextContent(district.getIcon());
    	byte[] bytexml =district.getSpecXml();
        if(bytexml != null) result.appendChild(doc.adoptNode(xXmlDocument.bytesToNode(bytexml)));
        else result.appendChild(doc.createElement("spec-xml"));    	
		org.w3c.dom.Element icons_element = (org.w3c.dom.Element)result.appendChild(doc.createElement("icons"));
        List<String> icons_list = district.getIcons();
        if(icons_list != null){
        	for(String icons_string : icons_list){
        		if(icons_string != null && icons_string.trim().length() > 0)
        			icons_element.appendChild(doc.createElement("icon")).setTextContent(icons_string);
        	}
        }
        org.w3c.dom.Element news_element = (org.w3c.dom.Element)result.appendChild(doc.createElement("news"));
        List<String> news_list = district.getNews();
        if(news_list != null){
        	for(String news_string : news_list){
        		if(news_string != null && news_string.trim().length() > 0)
        			news_element.appendChild(doc.createElement("new")).setTextContent(news_string);
        	}
        }
        
        
    }
}
