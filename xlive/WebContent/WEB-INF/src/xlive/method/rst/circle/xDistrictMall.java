package xlive.method.rst.circle;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import xlive.xUtility;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.KeyFactory;
public class xDistrictMall {
public Entity entity = null;
    
    public xDistrictMall(Entity entity){
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
        
    public String getDistrictKey(){
    	return (String) this.entity.getProperty("districtKey");
    }   
    
    public void setDistrictKey(String key){
    	this.entity.setProperty("districtKey", key);
    }
    
    public String getMallFid(){
    	return (String)entity.getProperty("mallFid");
    }
    public void setMallFid(String mall_fid){
    	this.entity.setProperty("mallFid", mall_fid);
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
    
    
    public static void xmlDistrictMall(xDistrictMall dmall, org.w3c.dom.Element result){
    	
    	org.w3c.dom.Document doc = result.getOwnerDocument();
    	result.appendChild(doc.createElement("key")).setTextContent(dmall.getKey());
    	result.appendChild(doc.createElement("circle-fid")).setTextContent(dmall.getCircleFid());
    	result.appendChild(doc.createElement("district-key")).setTextContent(dmall.getDistrictKey());
    	result.appendChild(doc.createElement("mall-fid")).setTextContent(dmall.getMallFid());
    	result.appendChild(doc.createElement("sort")).setTextContent(String.valueOf(dmall.getSort()));    	
    	
    }
}
