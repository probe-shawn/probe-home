package xlive.method.rst.menu;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import xlive.xUtility;
import xlive.xWebInformation;
import xlive.method.xMethodException;
import xlive.xml.xXmlDocument;
import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.SortDirection;

public class xBom { 
	/*
    private String id;
    private String name;
    private String barcode;
    private float price=0;
    private float listPrice=0;
    private Date createDate;
    private Date date;
    private String icon;
    private String icon2;
    private List<String> icons;
    
    private String markers;
    private Blob briefHtml;
    private Blob descHtml;
    private Blob specHtml;//del
    private Blob specXml;
    private Blob optionXml;
    private Blob addonXml;
    private String options;
    private String addons;
    private int qty=-1;
    private int limitQty=0; // 1-on
    private int limitBuy=0; // 1-on
    private long maxBuy=0; 
    private long fans=0;
    
    private int type=1;//0--directory, 1--product
    private int status=0;
    private int bonus=0;
    */
	public static String bomDirectory="0";
	public static String bomProduct="1";
	public Entity entity;
    public xBom(Entity entity){
    	this.entity=entity;
    }
    public String getKey(){
    	return KeyFactory.keyToString(this.entity.getKey());
    }
    public String getParentKey(){
    	String pkey = (String) this.entity.getProperty("parentKey");
    	return (pkey != null)?pkey :"";
    }
    public void setParentKey(String parent_key){
    	this.entity.setProperty("parentKey", parent_key);
    }
    public String getMallFid(){
    	return (String) this.entity.getProperty("mallFid");
    }
    public void setMallFid(String mallfid){
    	this.entity.setProperty("mallFid",mallfid);
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
    public String getIcon2(){
    	return (String) this.entity.getProperty("icon2");
    }
    public void setIcon2(String icon){
    	this.entity.setProperty("icon2", icon);
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
    public String getBarcode(){
    	return (String) this.entity.getProperty("barcode");
    }
    public void setBarcode(String barcode){
    	this.entity.setProperty("barcode", barcode);
    }
    public float getPrice(){
    	Double price = (Double)this.entity.getProperty("price");
    	return price != null ? price.floatValue() : 0f;
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
    public double getListPrice(){
    	Double price = (Double)this.entity.getProperty("listPrice");
    	return price != null ? price.doubleValue() : 0f;
    }
    public void setListPrice(double price){
    	try{
    		this.entity.setProperty("listPrice", Double.valueOf(price));
    	}catch(Exception e){}
    }
    public void setListPrice(String price){
    	try{
    		this.entity.setProperty("listPrice", Double.valueOf(price));
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
    public String getBriefHtml(){
    	try {
    		Blob briefHtml = (Blob) this.entity.getProperty("briefHtml");
			return (briefHtml != null) ? new String(briefHtml.getBytes(),"utf-8") : null;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return "";
    }
    public void setBriefHtml(String text){
    	try {
			Blob briefHtml = (text == null||text.trim().length()==0) ? null: new Blob(text.getBytes("utf-8"));
			this.entity.setProperty("briefHtml", briefHtml);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
    }
    public String getDescHtml(){
    	try {
    		Blob descHtml = (Blob) this.entity.getProperty("descHtml");
			return (descHtml != null) ? new String(descHtml.getBytes(),"utf-8") : null;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return "";
    }
    public void setDescHtml(String text){
    	try {
			Blob descHtml = (text == null||text.trim().length()==0) ? null: new Blob(text.getBytes("utf-8"));
			this.entity.setProperty("descHtml", descHtml);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
    }
    
    public String getSpecHtml(){
    	try {
    		Blob specHtml = (Blob) this.entity.getProperty("specHtml");
			return (specHtml != null) ? new String(specHtml.getBytes(),"utf-8") : null;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return "";
    }
    public void setSpecHtml(String text){
    	try {
			Blob specHtml = (text == null||text.trim().length()==0) ? null: new Blob(text.getBytes("utf-8"));
			this.entity.setProperty("specHtml", specHtml);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
    }
    public byte[] getSpecXml(){
    	Blob specXml = (Blob) this.entity.getProperty("specXml");
		return (specXml != null) ? specXml.getBytes() : null;
    }
    public void setSpecXml(byte[] xml_bytes){
		Blob specXml = (xml_bytes == null||xml_bytes.length==0) ? null: new Blob(xml_bytes);
		this.entity.setProperty("specXml", specXml);
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
    public String getMarkers(){
    	return (String) this.entity.getProperty("markers");
    }
    public void setMarkers(String markers){
    	this.entity.setProperty("markers", markers);
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
    public long getLimitQty(){
    	Long limitqty = (Long) this.entity.getProperty("limitQty");
    	return limitqty != null ? limitqty.longValue() : 0;
    }
    public void setLimitQty(long limitqty){
    	try{
    		this.entity.setProperty("limitQty", Long.valueOf(limitqty));
    	}catch(Exception e){}
    }
    public void setLimitQty(String limitqty){
    	try{
    		this.entity.setProperty("limitQty", Long.valueOf(limitqty));
    	}catch(Exception e){}
    }
    //
    public long getMaxBuy(){
    	Long maxbuy = (Long) this.entity.getProperty("maxBuy");
    	return maxbuy != null ? maxbuy.longValue() : 0;
    }
    public void setMaxBuy(long maxbuy){
    	try{
    		this.entity.setProperty("maxBuy", Long.valueOf(maxbuy));
    	}catch(Exception e){}
    }
    public void setMaxBuy(String maxbuy){
    	try{
    		this.entity.setProperty("maxBuy", Long.valueOf(maxbuy));
    	}catch(Exception e){}
    }
    public long getFans(){
    	Long fans = (Long) this.entity.getProperty("fans");
    	return fans != null ? fans.longValue() : 0;
    }
    public void setFans(long fans){
    	try{
    		this.entity.setProperty("fans", Long.valueOf(fans));
    	}catch(Exception e){}
    }
    public void setFans(String fans){
    	try{
    		this.entity.setProperty("fans", Long.valueOf(fans));
    	}catch(Exception e){}
    }
    public long getLimitBuy(){
    	Long limitbuy = (Long) this.entity.getProperty("limitBuy");
    	return limitbuy != null ? limitbuy.longValue() : 0;
    }
    public void setLimitBuy(long limitbuy){
    	try{
    		this.entity.setProperty("limitBuy", Long.valueOf(limitbuy));
    	}catch(Exception e){}
    }
    public void setLimitBuy(String limitbuy){
    	try{
    		this.entity.setProperty("limitBuy", Long.valueOf(limitbuy));
    	}catch(Exception e){}
    }
    
    public String getOptions(){
    	return (String) this.entity.getProperty("options");
    }
    public void setOptions(String options){
    	this.entity.setProperty("options",options);
    }
    public String getAddons(){
    	return (String) this.entity.getProperty("addons");
    }
    public void setAddons(String addons){
    	this.entity.setProperty("addons",addons);
    }
    public long getStatus(){
    	Long status = (Long) this.entity.getProperty("status");
    	return status != null ? status.longValue() : 0;
    }
    public void setStatus(long status){
    	try{
    		this.entity.setProperty("status", Long.valueOf(status));
    	}catch(Exception e){}
    }
    public void setStatus(String status){
    	try{
    		this.entity.setProperty("status", Long.valueOf(status));
    	}catch(Exception e){}
    }
    public long getType(){
    	Long type = (Long) this.entity.getProperty("type");
    	return type != null ? type.longValue() : 0;
    }
    public void setType(long type){
    	try{
    		this.entity.setProperty("type", Long.valueOf(type));
    	}catch(Exception e){}
    }
    public void setType(String type){
    	try{
    		this.entity.setProperty("type", Long.valueOf(type));
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
    
	public static org.w3c.dom.Element pseudoBomXml(xBom bom) throws xMethodException{
		DecimalFormat df = new DecimalFormat("#######.##");
		
		org.w3c.dom.Element result = xWebInformation.createElement("bom");
		org.w3c.dom.Document doc = result.getOwnerDocument();
		result.appendChild(doc.createElement("key")).setTextContent("");
		result.appendChild(doc.createElement("parentKey")).setTextContent(bom.getParentKey());
		result.appendChild(doc.createElement("mall-fid")).setTextContent(bom.getMallFid());
		result.appendChild(doc.createElement("id")).setTextContent(bom.getId());
		result.appendChild(doc.createElement("name")).setTextContent(bom.getName());
		result.appendChild(doc.createElement("barcode")).setTextContent(bom.getBarcode());
		
		result.appendChild(doc.createElement("price")).setTextContent(df.format(bom.getPrice()));
		result.appendChild(doc.createElement("list-price")).setTextContent(df.format(bom.getListPrice()));
		
		result.appendChild(doc.createElement("date")).setTextContent(bom.getDateString());
		result.appendChild(doc.createElement("icon")).setTextContent(bom.getIcon());
		result.appendChild(doc.createElement("icon2")).setTextContent(bom.getIcon2());
		org.w3c.dom.Element icons_element = (org.w3c.dom.Element)result.appendChild(doc.createElement("icons"));
        List<String> icons_list = bom.getIcons();
        if(icons_list != null){
        	for(String icons_string : icons_list){
        		if(icons_string != null && icons_string.trim().length() > 0)
        			icons_element.appendChild(doc.createElement("icon")).setTextContent(icons_string);
        	}
        }
		result.appendChild(doc.createElement("brief-html")).setTextContent(bom.getBriefHtml());
		result.appendChild(doc.createElement("desc-html")).setTextContent(bom.getDescHtml());
		result.appendChild(doc.createElement("spec-html")).setTextContent(bom.getSpecHtml());
        byte[] bytexml = bom.getSpecXml();
        if(bytexml != null) result.appendChild(doc.adoptNode(xXmlDocument.bytesToNode(bytexml)));
        else result.appendChild(doc.createElement("spec-xml"));
        bytexml = bom.getOptionXml();
        if(bytexml != null) result.appendChild(doc.adoptNode(xXmlDocument.bytesToNode(bytexml)));
        else result.appendChild(doc.createElement("option-xml"));
        bytexml = bom.getAddonXml();
        if(bytexml != null) result.appendChild(doc.adoptNode(xXmlDocument.bytesToNode(bytexml)));
        else result.appendChild(doc.createElement("addon-xml"));
        
        result.appendChild(doc.createElement("markers")).setTextContent(bom.getMarkers());
        result.appendChild(doc.createElement("qty")).setTextContent(String.valueOf(bom.getQty()));
        result.appendChild(doc.createElement("limitQty")).setTextContent(String.valueOf(bom.getLimitQty()));
        result.appendChild(doc.createElement("max-buy")).setTextContent(String.valueOf(bom.getMaxBuy()));
        result.appendChild(doc.createElement("limitBuy")).setTextContent(String.valueOf(bom.getLimitBuy()));
        result.appendChild(doc.createElement("options")).setTextContent(bom.getOptions());
        result.appendChild(doc.createElement("addons")).setTextContent(bom.getAddons());
        result.appendChild(doc.createElement("type")).setTextContent(String.valueOf(bom.getType()));
        result.appendChild(doc.createElement("status")).setTextContent(String.valueOf(bom.getStatus()));
        result.appendChild(doc.createElement("bonus")).setTextContent(String.valueOf(bom.getBonus()));
        
        org.w3c.dom.Element parts_element = (org.w3c.dom.Element)result.appendChild(doc.createElement("parts"));
        int dirs=0,boms=0,prods=0;
        
        String attr =(prods==0 && dirs > 0)?"dir":(prods>0 && dirs==0)?"bom":(prods != 0 && dirs != 0)?"mix":"empty";
        parts_element.setAttribute("parts", attr);
        parts_element.setAttribute("countBom", String.valueOf(boms-dirs));
        parts_element.setAttribute("countDir", String.valueOf(dirs));
        return result;
	}
    
	public static org.w3c.dom.Element bomXml(xBom bom, int descendants) throws xMethodException{
		DecimalFormat df = new DecimalFormat("#######.##");
		
		org.w3c.dom.Element result = xWebInformation.createElement("bom");
		org.w3c.dom.Document doc = result.getOwnerDocument();
		Key bom_key = bom.entity.getKey();
		result.appendChild(doc.createElement("key")).setTextContent(bom_key != null ? KeyFactory.keyToString(bom_key):"");
		result.appendChild(doc.createElement("parentKey")).setTextContent(bom.getParentKey());
		result.appendChild(doc.createElement("mall-fid")).setTextContent(bom.getMallFid());
		result.appendChild(doc.createElement("id")).setTextContent(bom.getId());
		result.appendChild(doc.createElement("name")).setTextContent(bom.getName());
		result.appendChild(doc.createElement("barcode")).setTextContent(bom.getBarcode());
		
		result.appendChild(doc.createElement("price")).setTextContent(df.format(bom.getPrice()));
		result.appendChild(doc.createElement("list-price")).setTextContent(df.format(bom.getListPrice()));
		
		result.appendChild(doc.createElement("date")).setTextContent(bom.getDateString());
		result.appendChild(doc.createElement("icon")).setTextContent(bom.getIcon());
		result.appendChild(doc.createElement("icon2")).setTextContent(bom.getIcon2());
		org.w3c.dom.Element icons_element = (org.w3c.dom.Element)result.appendChild(doc.createElement("icons"));
        List<String> icons_list = bom.getIcons();
        if(icons_list != null){
        	for(String icons_string : icons_list){
        		if(icons_string != null && icons_string.trim().length() > 0)
        			icons_element.appendChild(doc.createElement("icon")).setTextContent(icons_string);
        	}
        }
		result.appendChild(doc.createElement("brief-html")).setTextContent(bom.getBriefHtml());
		result.appendChild(doc.createElement("desc-html")).setTextContent(bom.getDescHtml());
		result.appendChild(doc.createElement("spec-html")).setTextContent(bom.getSpecHtml());
		
        byte[] bytexml = bom.getSpecXml();
        if(bytexml != null) result.appendChild(doc.adoptNode(xXmlDocument.bytesToNode(bytexml)));
        else result.appendChild(doc.createElement("spec-xml"));
        bytexml = bom.getOptionXml();
        if(bytexml != null) result.appendChild(doc.adoptNode(xXmlDocument.bytesToNode(bytexml)));
        else result.appendChild(doc.createElement("option-xml"));
        bytexml = bom.getAddonXml();
        if(bytexml != null) result.appendChild(doc.adoptNode(xXmlDocument.bytesToNode(bytexml)));
        else result.appendChild(doc.createElement("addon-xml"));
        
        result.appendChild(doc.createElement("markers")).setTextContent(bom.getMarkers());
        result.appendChild(doc.createElement("qty")).setTextContent(String.valueOf(bom.getQty()));
        result.appendChild(doc.createElement("limit-qty")).setTextContent(String.valueOf(bom.getLimitQty()));
        if(bom.getLimitQty() == 1){
        	DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
        	try {
        		Entity bomlimit_entity=ds.get(KeyFactory.createKey(xBomLimit.class.getSimpleName(), bom.getKey()));
        		result.appendChild(doc.createElement("current-qty")).setTextContent(String.valueOf(new xBomLimit(bomlimit_entity).getQty()));
			} catch (EntityNotFoundException e) {
       		 	 result.appendChild(doc.createElement("current-qty")).setTextContent("0");
			}
			result.appendChild(doc.createElement("add-qty")).setTextContent("0");
        }else{
        	result.appendChild(doc.createElement("current-qty")).setTextContent("0");
        	result.appendChild(doc.createElement("add-qty")).setTextContent("0");
        }
        result.appendChild(doc.createElement("max-buy")).setTextContent(String.valueOf(bom.getMaxBuy()));
        result.appendChild(doc.createElement("fans")).setTextContent(String.valueOf(bom.getFans()));
        result.appendChild(doc.createElement("limit-buy")).setTextContent(String.valueOf(bom.getLimitBuy()));
        result.appendChild(doc.createElement("options")).setTextContent(bom.getOptions());
        result.appendChild(doc.createElement("addons")).setTextContent(bom.getAddons());
        result.appendChild(doc.createElement("type")).setTextContent(String.valueOf(bom.getType()));
        result.appendChild(doc.createElement("status")).setTextContent(String.valueOf(bom.getStatus()));
        result.appendChild(doc.createElement("bonus")).setTextContent(String.valueOf(bom.getBonus()));
        
        org.w3c.dom.Element parts_element = (org.w3c.dom.Element)result.appendChild(doc.createElement("parts"));
        int dirs=0,boms=0,prods=0;
        List<Entity> found = null;
        
        if(bom.getType() == 0 && bom_key != null){
        	DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
        	Query q = new Query("xBom");
        	//q.addFilter("parentKey", FilterOperator.EQUAL, KeyFactory.keyToString(bom.entity.getKey()));
        	//q.addFilter("type", FilterOperator.EQUAL, Long.valueOf(0));
        	q.setFilter(
        			CompositeFilterOperator.and(
        				new Query.FilterPredicate("parentKey", FilterOperator.EQUAL, KeyFactory.keyToString(bom.entity.getKey())),
        				new Query.FilterPredicate("type", FilterOperator.EQUAL, Long.valueOf(0))
        			)
        	);
        	q.addSort("date", SortDirection.ASCENDING);
        	q.setKeysOnly();
        	PreparedQuery pq = ds.prepare(q);
        	dirs = pq.countEntities(FetchOptions.Builder.withDefaults());
        	//
        	q = new Query("xBom");
        	//q.addFilter("parentKey", FilterOperator.EQUAL, KeyFactory.keyToString(bom.entity.getKey()));
        	q.setFilter(new Query.FilterPredicate("parentKey", FilterOperator.EQUAL, KeyFactory.keyToString(bom.entity.getKey())));
        	q.addSort("date", SortDirection.ASCENDING);
        	if(descendants > 0){
        		q.addSort("type", SortDirection.ASCENDING); 
        		pq = ds.prepare(q);
        		found = pq.asList(FetchOptions.Builder.withDefaults());
        		boms = found.size();
        	}else{
        		q.setKeysOnly();
        		pq = ds.prepare(q);
        		boms = pq.countEntities(FetchOptions.Builder.withDefaults());
        	}
	        prods=boms-dirs;
        }
        String attr =(prods==0 && dirs > 0)?"dir":(prods>0 && dirs==0)?"bom":(prods != 0 && dirs != 0)?"mix":"empty";
        parts_element.setAttribute("parts", attr);
        parts_element.setAttribute("countBom", String.valueOf(boms-dirs));
        parts_element.setAttribute("countDir", String.valueOf(dirs));
        if(found != null && descendants > 0 && found.size() > 0){
        	for(Entity p : found)parts_element.appendChild(xBom.bomXml( new xBom(p),descendants-1));
        }
        
        return result;
	}
	
	public static org.w3c.dom.Element listBomXml(xBom bom, int descendants) throws xMethodException{
		DecimalFormat df = new DecimalFormat("#######.##");
		
		org.w3c.dom.Element result = xWebInformation.createElement("bom");
		org.w3c.dom.Document doc = result.getOwnerDocument();
		Key bom_key = bom.entity.getKey();
		result.appendChild(doc.createElement("key")).setTextContent(bom_key != null ? KeyFactory.keyToString(bom_key):"");
		result.appendChild(doc.createElement("parentKey")).setTextContent(bom.getParentKey());
		result.appendChild(doc.createElement("mall-fid")).setTextContent(bom.getMallFid());
		result.appendChild(doc.createElement("id")).setTextContent(bom.getId());
		result.appendChild(doc.createElement("name")).setTextContent(bom.getName());
		result.appendChild(doc.createElement("barcode")).setTextContent(bom.getBarcode());
		
		result.appendChild(doc.createElement("price")).setTextContent(df.format(bom.getPrice()));
		result.appendChild(doc.createElement("list-price")).setTextContent(df.format(bom.getListPrice()));
		
		result.appendChild(doc.createElement("date")).setTextContent(bom.getDateString());
		result.appendChild(doc.createElement("icon")).setTextContent(bom.getIcon());
		result.appendChild(doc.createElement("icon2")).setTextContent(bom.getIcon2());
		org.w3c.dom.Element icons_element = (org.w3c.dom.Element)result.appendChild(doc.createElement("icons"));
        List<String> icons_list = bom.getIcons();
        if(icons_list != null){
        	for(String icons_string : icons_list){
        		if(icons_string != null && icons_string.trim().length() > 0)
        			icons_element.appendChild(doc.createElement("icon")).setTextContent(icons_string);
        	}
        }
		result.appendChild(doc.createElement("brief-html")).setTextContent(bom.getBriefHtml());
		result.appendChild(doc.createElement("desc-html")).setTextContent(bom.getDescHtml());
		result.appendChild(doc.createElement("spec-html")).setTextContent(bom.getSpecHtml());
        byte[] bytexml = bom.getSpecXml();
        if(bytexml != null) result.appendChild(doc.adoptNode(xXmlDocument.bytesToNode(bytexml)));
        else result.appendChild(doc.createElement("spec-xml"));
		
        bytexml = bom.getOptionXml();
        if(bytexml != null) result.appendChild(doc.adoptNode(xXmlDocument.bytesToNode(bytexml)));
        else result.appendChild(doc.createElement("option-xml"));
        
        bytexml = bom.getAddonXml();
        if(bytexml != null) result.appendChild(doc.adoptNode(xXmlDocument.bytesToNode(bytexml)));
        else result.appendChild(doc.createElement("addon-xml"));
        
        result.appendChild(doc.createElement("markers")).setTextContent(bom.getMarkers());
        result.appendChild(doc.createElement("qty")).setTextContent(String.valueOf(bom.getQty()));
        result.appendChild(doc.createElement("limit-qty")).setTextContent(String.valueOf(bom.getLimitQty()));
        if(bom.getLimitQty() == 1){
        	DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
        	try {
        		Entity bomlimit_entity=ds.get(KeyFactory.createKey(xBomLimit.class.getSimpleName(), bom.getKey()));
        		result.appendChild(doc.createElement("current-qty")).setTextContent(String.valueOf(new xBomLimit(bomlimit_entity).getQty()));
			} catch (EntityNotFoundException e) {
       		 	 result.appendChild(doc.createElement("current-qty")).setTextContent("0");
			}
			result.appendChild(doc.createElement("add-qty")).setTextContent("0");
        }else{
        	result.appendChild(doc.createElement("current-qty")).setTextContent("0");
        	result.appendChild(doc.createElement("add-qty")).setTextContent("0");
        }
        result.appendChild(doc.createElement("max-buy")).setTextContent(String.valueOf(bom.getMaxBuy()));
        result.appendChild(doc.createElement("fans")).setTextContent(String.valueOf(bom.getFans()));
        result.appendChild(doc.createElement("limit-buy")).setTextContent(String.valueOf(bom.getLimitBuy()));
        result.appendChild(doc.createElement("options")).setTextContent(bom.getOptions());
        result.appendChild(doc.createElement("addons")).setTextContent(bom.getAddons());
        result.appendChild(doc.createElement("type")).setTextContent(String.valueOf(bom.getType()));
        result.appendChild(doc.createElement("status")).setTextContent(String.valueOf(bom.getStatus()));
        result.appendChild(doc.createElement("bonus")).setTextContent(String.valueOf(bom.getBonus()));
        org.w3c.dom.Element parts_element = (org.w3c.dom.Element)result.appendChild(doc.createElement("parts"));
        if(descendants > 0){
            List<Entity> found = null;
            if(bom.getType() == 0 && bom_key != null){
            	DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
            	Query q = new Query("xBom");
            	//q.addFilter("parentKey", FilterOperator.EQUAL, KeyFactory.keyToString(bom.entity.getKey()));
            	q.setFilter(new Query.FilterPredicate("parentKey", FilterOperator.EQUAL, KeyFactory.keyToString(bom.entity.getKey())));
            	q.addSort("date", SortDirection.ASCENDING);
           		q.addSort("type", SortDirection.ASCENDING); 
            	found = ds.prepare(q).asList(FetchOptions.Builder.withDefaults());
            	for(Entity p : found)parts_element.appendChild(xBom.listBomXml( new xBom(p),descendants-1));
            }
        }
        return result;
	}
	/*
	public static org.w3c.dom.Element oneBomXml(xBom bom) throws xMethodException{
		DecimalFormat df = new DecimalFormat("#######.##");
		
		org.w3c.dom.Element result = xWebInformation.createElement("bom");
		org.w3c.dom.Document doc = result.getOwnerDocument();
		Key bom_key = bom.entity.getKey();
		result.appendChild(doc.createElement("key")).setTextContent(bom_key != null ? KeyFactory.keyToString(bom_key):"");
		result.appendChild(doc.createElement("parentKey")).setTextContent(bom.getParentKey());
		result.appendChild(doc.createElement("mall-fid")).setTextContent(bom.getMallFid());
		result.appendChild(doc.createElement("id")).setTextContent(bom.getId());
		result.appendChild(doc.createElement("name")).setTextContent(bom.getName());
		result.appendChild(doc.createElement("barcode")).setTextContent(bom.getBarcode());
		
		result.appendChild(doc.createElement("price")).setTextContent(df.format(bom.getPrice()));
		result.appendChild(doc.createElement("list-price")).setTextContent(df.format(bom.getListPrice()));
		
		result.appendChild(doc.createElement("date")).setTextContent(bom.getDateString());
		result.appendChild(doc.createElement("icon")).setTextContent(bom.getIcon());
		result.appendChild(doc.createElement("icon2")).setTextContent(bom.getIcon2());
		org.w3c.dom.Element icons_element = (org.w3c.dom.Element)result.appendChild(doc.createElement("icons"));
        List<String> icons_list = bom.getIcons();
        if(icons_list != null){
        	for(String icons_string : icons_list){
        		if(icons_string != null && icons_string.trim().length() > 0)
        			icons_element.appendChild(doc.createElement("icon")).setTextContent(icons_string);
        	}
        }
		result.appendChild(doc.createElement("brief-html")).setTextContent(bom.getBriefHtml());
		result.appendChild(doc.createElement("desc-html")).setTextContent(bom.getDescHtml());
		result.appendChild(doc.createElement("spec-html")).setTextContent(bom.getSpecHtml());
        byte[] bytexml = bom.getOptionXml();
        if(bytexml != null) result.appendChild(doc.adoptNode(xXmlDocument.bytesToNode(bytexml)));
        else result.appendChild(doc.createElement("option-xml"));
        bytexml = bom.getAddonXml();
        if(bytexml != null) result.appendChild(doc.adoptNode(xXmlDocument.bytesToNode(bytexml)));
        else result.appendChild(doc.createElement("addon-xml"));
        
        result.appendChild(doc.createElement("markers")).setTextContent(bom.getMarkers());
        result.appendChild(doc.createElement("qty")).setTextContent(String.valueOf(bom.getQty()));
        result.appendChild(doc.createElement("limit-qty")).setTextContent(String.valueOf(bom.getLimitQty()));
        
        if(bom.getLimitQty() == 1){
        	DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
        	try {
        		Entity bomlimit_entity=ds.get(KeyFactory.createKey(xBomLimit.class.getSimpleName(), bom.getKey()));
        		result.appendChild(doc.createElement("current-qty")).setTextContent(String.valueOf(new xBomLimit(bomlimit_entity).getQty()));
			} catch (EntityNotFoundException e) {
       		 	 result.appendChild(doc.createElement("current-qty")).setTextContent("0");
			}
			result.appendChild(doc.createElement("add-qty")).setTextContent("0");
        }else{
        	result.appendChild(doc.createElement("current-qty")).setTextContent("0");
        	result.appendChild(doc.createElement("add-qty")).setTextContent("0");
        }
        
        result.appendChild(doc.createElement("max-buy")).setTextContent(String.valueOf(bom.getMaxBuy()));
        result.appendChild(doc.createElement("fans")).setTextContent(String.valueOf(bom.getFans()));
        result.appendChild(doc.createElement("limit-buy")).setTextContent(String.valueOf(bom.getLimitBuy()));
        result.appendChild(doc.createElement("options")).setTextContent(bom.getOptions());
        result.appendChild(doc.createElement("addons")).setTextContent(bom.getAddons());
        result.appendChild(doc.createElement("type")).setTextContent(String.valueOf(bom.getType()));
        result.appendChild(doc.createElement("status")).setTextContent(String.valueOf(bom.getStatus()));
        result.appendChild(doc.createElement("bonus")).setTextContent(String.valueOf(bom.getBonus()));
        return result;
	}
	*/
}
