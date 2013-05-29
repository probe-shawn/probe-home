package xlive.method.rst.mall;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

public class xMall { 
	/*
    private String fid;
    private int bonus;
    private int bonusRefund;
    */
	public Entity entity = null;
    public static String bonusType="mall";
    
    public xMall(Entity entity){
    	this.entity = entity;
    }
    public static Key generateKey(String fid){
    	return KeyFactory.createKey(xMall.class.getSimpleName(), fid);
    }
    public String getFid(){
    	return (String)entity.getProperty("fid");
    }
    public void setFid(String fid){
    	this.entity.setProperty("fid", fid);
    }
    public long getBonus(){
    	Long bonus = (Long)this.entity.getProperty("bonus");
    	return (bonus != null) ? bonus.longValue() : 0;
    }
    public void setBonus(long bonus){
    	try{
    		this.entity.setProperty("bonus", Long.valueOf(bonus));
    	}catch(Exception e){}
    }

    public long getBonusRefund(){
       	Long bonusRefund = (Long)this.entity.getProperty("bonusRefund");
    	return (bonusRefund != null) ? bonusRefund.longValue() : 0;
    }
    public void setBonusRefund(long bonus_refund){
    	try{
    		this.entity.setProperty("bonusRefund", Long.valueOf(bonus_refund));
    	}catch(Exception e){}
    }
    
    public String getBonusType(){
    	return xMall.bonusType;
    }
    public static void xmlMall(xMall mall, org.w3c.dom.Element result){
    	//DecimalFormat df = new DecimalFormat("#######.##");
    	org.w3c.dom.Document doc = result.getOwnerDocument();
       	result.appendChild(doc.createElement("bonus")).setTextContent(String.valueOf(mall.getBonus()));
       	result.appendChild(doc.createElement("bonus-refund")).setTextContent(String.valueOf(mall.getBonusRefund()));
       	result.appendChild(doc.createElement("bonus-type")).setTextContent(mall.getBonusType());
    }

}
