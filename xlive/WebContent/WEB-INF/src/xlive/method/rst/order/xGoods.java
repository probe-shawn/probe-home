package xlive.method.rst.order;

import java.text.DecimalFormat;
import java.util.Date;

import xlive.xUtility;
import xlive.xml.xXmlDocument;
import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.KeyFactory;

public class xGoods { 
	/*
    private Key key;
    private String id;
    private String name;
    private String barcode;
    private float price=0;
    private String icon;
    private Blob optionXml;
    private Blob addonXml;
    private String options;
    private String addons;
    private int qty=0;
    private float optval=0;
    private int bonus=0;
    */
	public Entity entity;
    
    public xGoods(Entity entity){
    	this.entity=entity;
    }
    public String getBomKey(){
    	return (String) this.entity.getProperty("bomKey");
    }
    public void setBomKey(String bomkey){
    	this.entity.setProperty("bomKey", bomkey);
    }
    
    public String getId(){
    	return (String) this.entity.getProperty("id");
    }
    public void setId(String id){
    	this.entity.setProperty("id", id);
    }
    public String getName(){
    	return (String) this.entity.getProperty("name");
    }
    public void setName(String name){
    	this.entity.setProperty("name", name);
    }
    public String getIcon(){
    	return (String) this.entity.getProperty("icon");
    }
    public void setIcon(String icon){
    	this.entity.setProperty("icon", icon);
    }
    
    public String getBarcode(){
    	return (String) this.entity.getProperty("barcode");
    }
    public void setBarcode(String barcode){
    	this.entity.setProperty("barcode", barcode);
    }
    public double getPrice(){
    	Double price = (Double) this.entity.getProperty("price");
    	return price != null?price.doubleValue():0f;
    }
    public void setPrice(double price){
    	try{
    		this.entity.setProperty("price", Double.valueOf(price));
    	}catch(Exception e){}
    }
    public void setPrice(String price){
    	try{
    		this.entity.setProperty("price", Double.valueOf(price));
    	}catch(Exception e){}
    }
    public byte[] getOptionXml(){
    	Blob optionXml = (Blob) this.entity.getProperty("optionXml");
		return (optionXml != null) ? optionXml.getBytes() : null;
    }
    public void setOptionXml(byte[] xml_bytes){
		Blob optionXml = (xml_bytes == null||xml_bytes.length==0) ? null: new Blob(xml_bytes);
		this.entity.setProperty("optionXml", optionXml);
    }
    public byte[] getAddonXml(){
    	Blob addonXml = (Blob) this.entity.getProperty("addonXml");
		return (addonXml != null) ? addonXml.getBytes() : null;
    }
    public void setAddonXml(byte[] xml_bytes){
		Blob addonXml = (xml_bytes == null||xml_bytes.length==0) ? null: new Blob(xml_bytes);
		this.entity.setProperty("addonXml", addonXml);
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
    public String getOptions(){
    	return (String) this.entity.getProperty("options");
    }
    public void setOptions(String options){
    	this.entity.setProperty("options", options);
    }
    public String getAddons(){
    	return (String) this.entity.getProperty("addons");
    }
    public void setAddons(String addons){
    	this.entity.setProperty("addons", addons);
    }
    public double getOptval(){
    	Double optval =(Double) this.entity.getProperty("optval");
    	return optval != null ? optval.doubleValue() : 0;
    }
    public void setOptval(double optval){
    	try{
    		this.entity.setProperty("optval", Double.valueOf(optval));
    	}catch(Exception e){}
    }
    public void setOptval(String optval){
    	try{
    		this.entity.setProperty("optval", Double.valueOf(optval));
    	}catch(Exception e){}
    }
    public long getBonus(){
    	Long bonus = (Long) this.entity.getProperty("bonus");
    	return bonus != null ? bonus.longValue() : 0;
    }
    public void setBonus(long bonus){
    	try{
    		this.entity.setProperty("bonus", Long.valueOf(bonus));
    	}catch(Exception e){}
    }
    public void setBonus(String bonus){
    	try{
    		this.entity.setProperty("bonus", Long.valueOf(bonus));
    	}catch(Exception e){}
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
    public Date getDate(){
    	return (Date)entity.getProperty("date");
    }

    public static void xmlGoods(xGoods goods, org.w3c.dom.Element result){
    	DecimalFormat df = new DecimalFormat("#######.##");
    	org.w3c.dom.Document doc = result.getOwnerDocument();
    	result.appendChild(doc.createElement("key")).setTextContent(KeyFactory.keyToString(goods.entity.getKey()));
    	result.appendChild(doc.createElement("id")).setTextContent(goods.getId());
    	result.appendChild(doc.createElement("name")).setTextContent(goods.getName());
    	result.appendChild(doc.createElement("barcode")).setTextContent(goods.getBarcode());
    	result.appendChild(doc.createElement("price")).setTextContent(df.format(goods.getPrice()));
    	result.appendChild(doc.createElement("icon")).setTextContent(goods.getIcon());
        byte[] bytexml = goods.getOptionXml();
        if(bytexml != null) result.appendChild(doc.adoptNode(xXmlDocument.bytesToNode(bytexml)));
        else result.appendChild(doc.createElement("option-xml"));
        bytexml = goods.getAddonXml();
        if(bytexml != null) result.appendChild(doc.adoptNode(xXmlDocument.bytesToNode(bytexml)));
        else result.appendChild(doc.createElement("addon-xml"));
        result.appendChild(doc.createElement("options")).setTextContent(goods.getOptions());
        result.appendChild(doc.createElement("addons")).setTextContent(goods.getAddons());
        result.appendChild(doc.createElement("qty")).setTextContent(""+goods.getQty());
        result.appendChild(doc.createElement("optval")).setTextContent(df.format(goods.getOptval()));
        result.appendChild(doc.createElement("bonus")).setTextContent(df.format(goods.getBonus()));
    }

    
}
