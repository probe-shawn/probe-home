package xlive.method.rst.menu;

import java.util.Date;

import xlive.xUtility;
import xlive.xWebInformation;
import xlive.method.xMethodException;
import com.google.appengine.api.datastore.Entity;

public class xBomBuy { 
	/*
    private long qty=-1;
    */
	public Entity entity;
    public xBomBuy(Entity entity){
    	this.entity=entity;
    }
    public long getQty(){
    	Long qty = (Long) this.entity.getProperty("qty");
    	return qty != null ? qty.longValue() : 0;
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
    public Date getDate(){
    	return (Date) this.entity.getProperty("date");
    }
    public String getDateString(){
    	Date date = (Date)this.entity.getProperty("date");
    	return (date != null) ? xUtility.formatDate(date):"";
    }
    public void setDate(Date date){
    	try{
    		this.entity.setProperty("date", date);
    	}catch(Exception e){}
    }
    public void setDate(String date_str){
    	Date date=(date_str != null && date_str.trim().length() >= 8)? xUtility.parseDate(date_str):null;
    	this.entity.setProperty("date", date);
    }
	public static org.w3c.dom.Element bomBuyXml(xBomBuy bom_buy) throws xMethodException{
		org.w3c.dom.Element result = xWebInformation.createElement("bom-buy");
		org.w3c.dom.Document doc = result.getOwnerDocument();
        result.appendChild(doc.createElement("qty")).setTextContent(String.valueOf(bom_buy.getQty()));
        result.appendChild(doc.createElement("date")).setTextContent(String.valueOf(bom_buy.getDateString()));
        return result;
	}

}
