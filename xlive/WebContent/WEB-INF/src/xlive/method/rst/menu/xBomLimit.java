package xlive.method.rst.menu;

import xlive.xWebInformation;
import xlive.method.xMethodException;
import com.google.appengine.api.datastore.Entity;

public class xBomLimit { 
	/*
    private long qty=-1;
    */
	public Entity entity;
    public xBomLimit(Entity entity){
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
	public static org.w3c.dom.Element bomLimitXml(xBomLimit bom_limit) throws xMethodException{
		org.w3c.dom.Element result = xWebInformation.createElement("limit-qty");
		org.w3c.dom.Document doc = result.getOwnerDocument();
        result.appendChild(doc.createElement("qty")).setTextContent(String.valueOf(bom_limit.getQty()));
        return result;
	}

}
