package xlive.method.rst.barter;

import java.io.UnsupportedEncodingException;
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
import com.google.appengine.api.datastore.KeyFactory;

public class xBarter { 
	/*
    private String id;
    private String name;
    private String creatorFid;
    private String creatorName;
    private String ownerFid;
    private String ownerName;
    private String createDate;
    private Date date;
    private Date squDate;
    private String icon;
    private List<String> icons;
    private Blob descHtml;
    private Blob specHtml;
    private Blob specXml;
    private int type=0; //type==0 normal, type=1 iou
    private int status=0;
    private long removed=0;
    private String ilove;
    private List<String> lovemes;
    private Blob histXml;
    */
	public static long status_normal = 0;
	public static long status_i_love = 1;
	public static long status_love_me = 2;
	public static long status_i_love_me = 3;
	public static long status_barter = 4;
	public static long status_store = 5;
	public static long status_special = 6;
	
	public Entity entity;
    public xBarter(Entity entity){
    	this.entity=entity;
    }
    public String getKey(){
    	return KeyFactory.keyToString(this.entity.getKey());
    }
    public String getCreatorFid(){
    	return (String) this.entity.getProperty("creatorFid");
    }
    public void setCreatorFid(String creatorfid){
    	this.entity.setProperty("creatorFid",creatorfid);
    }
    public String getCreatorName(){
    	return (String) this.entity.getProperty("creatorName");
    }
    public void setCreatorName(String creator_name){
    	this.entity.setProperty("creatorName",creator_name);
    }
    public String getOwnerFid(){
    	return (String) this.entity.getProperty("ownerFid");
    }
    public void setOwnerFid(String ownerfid){
    	this.entity.setProperty("ownerFid",ownerfid);
    }
    public String getOwnerName(){
    	return (String) this.entity.getProperty("ownerName");
    }
    public void setOwnerName(String owner_name){
    	this.entity.setProperty("ownerName",owner_name);
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
    
    public Date getCreateDate(){
    	return (Date) this.entity.getProperty("createDate");
    }
    public String getCreateDateString(){
    	Date date = (Date)this.entity.getProperty("createDate");
    	return (date != null) ? xUtility.formatDate(date):"";
    }
    public void setCreateDate(Date date){
    	try{
    		this.entity.setProperty("createDate", date);
    	}catch(Exception e){}
    }
    public void setCreateDate(String date_str){
    	Date date=(date_str != null && date_str.trim().length() >= 8)? xUtility.parseDate(date_str):null;
    	this.entity.setProperty("createDate", date);
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
    public Date getSquDate(){
    	return (Date) this.entity.getProperty("squDate");
    }
    public String getSquDateString(){
    	Date date = (Date)this.entity.getProperty("squDate");
    	return (date != null) ? xUtility.formatDate(date):"";
    }
   public void setSquDate(Date date){
    	try{
    		this.entity.setProperty("squDate", date);
    	}catch(Exception e){}
    }
    public void setSquDate(String date_str){
    	Date date=(date_str != null && date_str.trim().length() >= 8)? xUtility.parseDate(date_str):null;
    	this.entity.setProperty("squDate", date);
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
    public byte[] getHistXml(){
    	Blob histXml = (Blob) this.entity.getProperty("histXml");
		return (histXml != null) ? histXml.getBytes() : null;
    }
    public void setHistXml(byte[] xml_bytes){
		Blob histXml = (xml_bytes == null||xml_bytes.length==0) ? null: new Blob(xml_bytes);
		this.entity.setProperty("histXml", histXml);
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
    public long getRemoved(){
    	Long removed = (Long) this.entity.getProperty("removed");
    	return removed != null ? removed.longValue() : 0;
    }
    public void setRemoved(String removed){
    	try{
    		this.entity.setProperty("removed", Long.valueOf(removed));
    	}catch(Exception e){}
    }
    public void setRemoved(long removed){
    	try{
    		this.entity.setProperty("removed", Long.valueOf(removed));
    	}catch(Exception e){}
    }
    
    public String getILove(){
    	return (String) this.entity.getProperty("iLove");
    }
    public void setILove(String key){
    	this.entity.setProperty("iLove", key);
    }
    @SuppressWarnings("unchecked")
	public List<String> getLoveMes(){
    	return (List<String>)this.entity.getProperty("loveMes");
    }
    public void setLoveMes(List<String> love_mes){
    	this.entity.setProperty("loveMes", love_mes);
    }
    @SuppressWarnings("unchecked")
	public void addLoveMe(String love_me){
    	List<String> love_mes = (List<String>)this.entity.getProperty("loveMes");
    	if(love_mes == null) love_mes = new ArrayList<String>();
    	love_mes.add(love_me);
    	this.entity.setProperty("loveMes", love_mes);
    }
    @SuppressWarnings("unchecked")
	public void removeLoveMe(String love_me){
    	List<String> love_mes = (List<String>)this.entity.getProperty("loveMes");
    	if(love_mes == null) return;
    	love_mes.remove(love_me);
    	this.entity.setProperty("loveMes", love_mes);
    }
    
	public static org.w3c.dom.Element barterXml(xBarter barter, boolean my_love_only) throws xMethodException{
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		org.w3c.dom.Element result = xWebInformation.createElement("barter");
		org.w3c.dom.Document doc = result.getOwnerDocument();
		result.appendChild(doc.createElement("key")).setTextContent(KeyFactory.keyToString(barter.entity.getKey()));
		result.appendChild(doc.createElement("creator-fid")).setTextContent(barter.getCreatorFid());
		result.appendChild(doc.createElement("creator-name")).setTextContent(barter.getCreatorName());
		result.appendChild(doc.createElement("owner-fid")).setTextContent(barter.getOwnerFid());
		result.appendChild(doc.createElement("owner-name")).setTextContent(barter.getOwnerName());
		result.appendChild(doc.createElement("id")).setTextContent(barter.getId());
		result.appendChild(doc.createElement("name")).setTextContent(barter.getName());
		result.appendChild(doc.createElement("create-date")).setTextContent(barter.getCreateDateString());
		result.appendChild(doc.createElement("date")).setTextContent(barter.getDateString());
		result.appendChild(doc.createElement("icon")).setTextContent(barter.getIcon());
		org.w3c.dom.Element icons_element = (org.w3c.dom.Element)result.appendChild(doc.createElement("icons"));
        List<String> icons_list = barter.getIcons();
        if(icons_list != null){
        	for(String icons_string : icons_list){
        		if(icons_string != null && icons_string.trim().length() > 0)
        			icons_element.appendChild(doc.createElement("icon")).setTextContent(icons_string);
        	}
        }
		result.appendChild(doc.createElement("desc-html")).setTextContent(barter.getDescHtml());
		/*result.appendChild(doc.createElement("spec-html")).setTextContent(barter.getSpecHtml());*/
		
        byte[] bytexml = barter.getSpecXml();
        if(bytexml != null) result.appendChild(doc.adoptNode(xXmlDocument.bytesToNode(bytexml)));
        else result.appendChild(doc.createElement("spec-xml"));
        
        bytexml = barter.getHistXml();
        if(bytexml != null) result.appendChild(doc.adoptNode(xXmlDocument.bytesToNode(bytexml)));
        else result.appendChild(doc.createElement("hist-xml"));

        result.appendChild(doc.createElement("type")).setTextContent(String.valueOf(barter.getType()));
        result.appendChild(doc.createElement("status")).setTextContent(String.valueOf(barter.getStatus()));
        result.appendChild(doc.createElement("removed")).setTextContent(String.valueOf(barter.getRemoved()));
        org.w3c.dom.Element ilove_element = (org.w3c.dom.Element)result.appendChild(doc.createElement("i-love"));
        if(!my_love_only){
	        String keystr = barter.getILove();
	        if(keystr != null && keystr.trim().length() >0){
	        	try {
					Entity entity= ds.get(KeyFactory.stringToKey(keystr));
					ilove_element.appendChild(xBarter.barterXml(new xBarter(entity), true));
				} catch (EntityNotFoundException e) {
				}
	        }
        }
        org.w3c.dom.Element lovemes_element = (org.w3c.dom.Element)result.appendChild(doc.createElement("love-mes"));
        if(!my_love_only){
	        List<String> keystrs = barter.getLoveMes();
	        if(keystrs != null){
	        	for(String keystr : keystrs){
		        	try {
						Entity entity= ds.get(KeyFactory.stringToKey(keystr));
						lovemes_element.appendChild(xBarter.barterXml(new xBarter(entity), true));
					} catch (EntityNotFoundException e) {
					}
	        	}
	        }
        }
        return result;
	}
}
