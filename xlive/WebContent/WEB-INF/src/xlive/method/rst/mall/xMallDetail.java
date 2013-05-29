package xlive.method.rst.mall;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import xlive.xUtility;
import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

public class xMallDetail { 
	/*
    private String fid;
    private String id;
    private String name;
    private String storeName;
    private String type;
    private String mallType;
    private String desc;
    private Blob descHtml;
    private Date date;
    private String phone;
    private String addr;
    private String openTime;
    private String mail;
    private String note;
    
    private long status; -9 : 停權, -1:休業, 0 :正常, 1 : 待驗.
    private long pause; 0: normal,  1: pause.
    private String pauseMessage;
    
    private Date last;
    private String bomKey;
    private float latitude;
    private float longitude;
    private int likes;
    private List<String> geoCell;
    private String bonusPercent;
    private String bonusProductLimit;
    
    private String services;
    private String buyMessage;
    private String mallBgIcon;
    private String menuBgIcon;
    private String barterBgIcon;
    private String mallIcon;
    private List<String> mallIcons;
    private long tzOffset;
    private long minOutgo;
    private long fareDelivery;
    private long maxDelivery;
    private long visit;
    */
	public Entity entity = null;
    
    public xMallDetail(Entity entity){
    	this.entity = entity;
    }
    public static Key generateKey(String fid){
    	return KeyFactory.createKey(xMallDetail.class.getSimpleName(), fid);
    }
    public String getBomKey(){
    	return (String)entity.getProperty("bomKey");
    }
    public void setBomKey(String bomkey){
    	this.entity.setProperty("bomKey", bomkey);
    }
    public String getFid(){
    	return (String)entity.getProperty("fid");
    }
    public void setFid(String fid){
    	this.entity.setProperty("fid", fid);
    }
    public String getId(){
    	return (String)entity.getProperty("id");
    }
    public void setId(String id){
    	this.entity.setProperty("id", id);
    }
    public String getName(){
    	return (String)entity.getProperty("name");
    }
    public void setName(String name){
    	this.entity.setProperty("name", name);
    }
    public String getStoreName(){
    	return (String)entity.getProperty("storeName");
    }
    public void setStoreName(String store_name){
    	this.entity.setProperty("storeName", store_name);
    }
    public String getMallType(){
    	String mall_type=(String)entity.getProperty("mallType");
    	return (mall_type != null && mall_type.trim().length()>0)? mall_type:"account";
    }
    public void setMallType(String mall_type){
    	mall_type = (mall_type != null && mall_type.trim().length()>0)? mall_type:"account";
    	this.entity.setProperty("mallType", mall_type);
    }
    public String getType(){
    	return (String)entity.getProperty("type");
    }
    public void setType(String type){
    	this.entity.setProperty("type", type);
    }
    public String getDesc(){
    	return (String)entity.getProperty("desc");
    }
    public void setDesc(String desc){
    	this.entity.setProperty("desc", desc);
    }
    public String getDescHtml(){
    	Blob blob = (Blob)this.entity.getProperty("descHtml");
    	try {
			return (blob != null) ? new String(blob.getBytes(),"utf-8") : null;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return "";
    }
    public void setDescHtml(String text){
    	try {
    		Blob blob = (text == null||text.trim().length()==0) ? null: new Blob(text.getBytes("utf-8"));
    		this.entity.setProperty("descHtml", blob);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
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
    
    public String getPhone(){
    	return (String) this.entity.getProperty("phone");
    }
    public void setPhone(String phone){
    	this.entity.setProperty("phone", phone);
    }
    
    public String getAddr(){
    	return (String) this.entity.getProperty("addr");
    }
    public void setAddr(String addr){
    	this.entity.setProperty("addr", addr);
    }
    public String getOpenTime(){
    	return (String)this.entity.getProperty("openTime");
    }
    public void setOpenTime(String open_time){
    	this.entity.setProperty("openTime", open_time);
    }

    public String getMail(){
    	return (String) this.entity.getProperty("mail");
    }
    public void setMail(String mail){
    	this.entity.setProperty("mail", mail);
    }

    public String getNote(){
    	return (String) this.entity.getProperty("note");
    }
    public void setNote(String note){
    	this.entity.setProperty("note", note);
    }
    
    public long getStatus(){
    	Object status=this.entity.getProperty("status");
    	if(status != null && status instanceof String){
    		String status_string=(String)status;	
    		return (status_string.trim().length() > 0) ? Long.valueOf(status_string):0;
    	}
    	Long lstatus=(Long)this.entity.getProperty("status");
    	return (lstatus != null)? lstatus.longValue():0;
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
    public long getPause(){
    	Long pause=(Long)this.entity.getProperty("pause");
    	return (pause != null)? pause.longValue():0;
    }
    public void setPause(long pause){
    	try{
    		this.entity.setProperty("pause", Long.valueOf(pause));
    	}catch(Exception e){}
    }
    public void setPause(String pause){
    	try{
    		this.entity.setProperty("pause", Long.valueOf(pause));
    	}catch(Exception e){}
    }
    public String getPauseMessage(){
    	String msg=(String) this.entity.getProperty("pauseMessage");
    	return (msg != null)?msg:"暫停接單";
    }
    public void setPauseMessage(String pause_message){
    	this.entity.setProperty("pauseMessage", pause_message);
    }
    public Date getLast(){
    	return (Date) this.entity.getProperty("last");
    }
    public String getLastString(){
    	Date date = (Date) this.entity.getProperty("last");
    	return date != null ? xUtility.formatDate(date):"";
    }
    public void setLast(Date last){
    	this.entity.setProperty("last", last);
    }
    public void setLast(String last){
    	Date date = (last != null && last.trim().length() >= 8)? xUtility.parseDate(last):null;
    	this.entity.setProperty("last", date);
    }
    
    public float getLatitude(){
    	Double f = (Double)this.entity.getProperty("latitude");
    	return (f != null) ? f.floatValue() : 0;
    }
    public void setLatitude(double latitude){
    	try{
    		this.entity.setProperty("latitude",Double.valueOf(latitude));
    	}catch(Exception e){}
    }
    public void setLatitude(String latitude){
    	try{
    		this.entity.setProperty("latitude", Double.valueOf(latitude));
    	}catch(Exception e){}
    }
    public double getLongitude(){
    	Double f = (Double)this.entity.getProperty("longitude");
    	return (f != null) ? f.doubleValue() : 0;
    }
    public void setLongitude(double longitude){
    	try{
    		this.entity.setProperty("longitude", Double.valueOf(longitude));
    	}catch(Exception e){}
    }
    public void setLongitude(String longitude){
    	try{
    		this.entity.setProperty("longitude", Double.valueOf(longitude));
    	}catch(Exception e){}
   }
    public long getLikes(){
    	Long likes = (Long)this.entity.getProperty("likes");
    	return (likes != null) ? likes.longValue() : 0;
    }
    public void setLikes(long likes){
    	try{
    		this.entity.setProperty("likes", Long.valueOf(likes));
    	}catch(Exception e){}
    }
    public void setLikes(String likes){
    	try{
    		this.entity.setProperty("likes", Long.valueOf(likes));
    	}catch(Exception e){}
    }
    public String getBonusPercent(){
    	String bonusPercent = (String)this.entity.getProperty("bonusPercent");
    	return (bonusPercent != null) ?bonusPercent:"30%";
    }
    public void setBonusPercent(String bonus_percent){
    	this.entity.setProperty("bonusPercent", bonus_percent);
    }
    public String getBonusProductLimit(){
    	String bonusProductLimit = (String)this.entity.getProperty("bonusProductLimit");
    	return (bonusProductLimit != null) ?bonusProductLimit:"50%";
    }
    public void setBonusProductLimit(String bonus_product_limit){
    	this.entity.setProperty("bonusProductLimit", bonus_product_limit);
    }

    public String getServiceType(){
    	return (String) this.entity.getProperty("serviceType");
    }
    public void setServiceType(String note){
    	this.entity.setProperty("serviceType", note);
    }
    public String getServiceMessage(){
    	String msg=(String) this.entity.getProperty("serviceMessage");
    	return (msg != null)?msg:"";
    }
    public void setServiceMessage(String service_message){
    	this.entity.setProperty("serviceMessage", service_message);
    }
    @SuppressWarnings("unchecked")
	public List<String> getMallIcons(){
    	return (List<String>)this.entity.getProperty("mallIcons");
    }
    public void setMallIcons(List<String> icons){
    	this.entity.setProperty("mallIcons", icons);
    }
    @SuppressWarnings("unchecked")
	public void addIcon(String icon){
    	List<String> icons = (List<String>)this.entity.getProperty("mallIcons");
    	if(icons == null) icons = new ArrayList<String>();
    	icons.add(icon);
    	this.entity.setProperty("mallIcons", icons);
    }
    
    public String getMallBgIcon(){
    	return (String) this.entity.getProperty("mallBgIcon");
    }
    public void setMallBgIcon(String mall_bg_icon){
    	this.entity.setProperty("mallBgIcon", mall_bg_icon);
    }
    public String getMenuBgIcon(){
    	return (String) this.entity.getProperty("menuBgIcon");
    }
    public void setMenuBgIcon(String menu_bg_icon){
    	this.entity.setProperty("menuBgIcon", menu_bg_icon);
    }
    public String getBarterBgIcon(){
    	String bg=(String) this.entity.getProperty("barterBgIcon");
    	return bg;
    	//return(bg!=null && bg.trim().length()>0)?bg:"http://photos-b.ak.fbcdn.net/hphotos-ak-ash4/386534_134507303319848_100002817382902_145688_313704707_s.jpg";
    }
    public void setBarterBgIcon(String barter_bg_icon){
    	this.entity.setProperty("barterBgIcon", barter_bg_icon);
    }
    public long getTzOffset(){
    	Long tzoffset = (Long)this.entity.getProperty("tzOffset");
    	return (tzoffset != null) ? tzoffset.longValue() : -480l;
    }
    public void setTzOffset(long tzoffset){
    	try{
    		this.entity.setProperty("tzOffset", Long.valueOf(tzoffset));
    	}catch(Exception e){}
    }
    public void setTzOffset(String tzoffset){
    	try{
    		this.entity.setProperty("tzOffset", Long.valueOf(tzoffset));
    	}catch(Exception e){}
    }
    //
    public long getMinOutgo(){
    	Long min_outgo = (Long)this.entity.getProperty("minOutgo");
    	return (min_outgo != null) ? min_outgo.longValue() : 0;
    }
    public void setMinOutgo(long min_outgo){
    	try{
    		this.entity.setProperty("minOutgo", Long.valueOf(min_outgo));
    	}catch(Exception e){}
    }
    public void setMinOutgo(String min_outgo){
    	try{
    		this.entity.setProperty("minOutgo", Long.valueOf(min_outgo));
    	}catch(Exception e){}
    }
    public long getMaxDelivery(){
    	Long max_delivery = (Long)this.entity.getProperty("maxDelivery");
    	return (max_delivery != null) ? max_delivery.longValue() : 0;
    }
    public void setMaxDelivery(long max_delivery){
    	try{
    		this.entity.setProperty("maxDelivery", Long.valueOf(max_delivery));
    	}catch(Exception e){}
    }
    public void setMaxDelivery(String max_delivery){
    	try{
    		this.entity.setProperty("maxDelivery", Long.valueOf(max_delivery));
    	}catch(Exception e){}
    }
    public long getFareDelivery(){
    	Long fare_delivery = (Long)this.entity.getProperty("fareDelivery");
    	return (fare_delivery != null) ? fare_delivery.longValue() : 0;
    }
    public void setFareDelivery(long fare_delivery){
    	try{
    		this.entity.setProperty("fareDelivery", Long.valueOf(fare_delivery));
    	}catch(Exception e){}
    }
    public void setFareDelivery(String fare_delivery){
    	try{
    		this.entity.setProperty("fareDelivery", Long.valueOf(fare_delivery));
    	}catch(Exception e){}
    }
    public void setVisit(long visit){
    	try{
    		this.entity.setProperty("visit", Long.valueOf(visit));
    	}catch(Exception e){}
    }
    public long getVisit(){
    	Long visit = (Long)this.entity.getProperty("visit");
    	return (visit != null) ? visit.longValue() : 0;
    }
  

    public static void xmlMallDetail(xMallDetail mall_detail, org.w3c.dom.Element result){
    	//DecimalFormat df = new DecimalFormat("#######.##");
    	org.w3c.dom.Document doc = result.getOwnerDocument();
    	result.appendChild(doc.createElement("key")).setTextContent(KeyFactory.keyToString(mall_detail.entity.getKey()));
    	result.appendChild(doc.createElement("fid")).setTextContent(mall_detail.getFid());
    	result.appendChild(doc.createElement("id")).setTextContent(mall_detail.getId());
    	result.appendChild(doc.createElement("name")).setTextContent(mall_detail.getName());
    	result.appendChild(doc.createElement("store-name")).setTextContent(mall_detail.getStoreName());
    	result.appendChild(doc.createElement("mall-type")).setTextContent(mall_detail.getMallType());
    	result.appendChild(doc.createElement("type")).setTextContent(mall_detail.getType());
    	result.appendChild(doc.createElement("desc")).setTextContent(mall_detail.getDesc());
		result.appendChild(doc.createElement("desc-html")).setTextContent(mall_detail.getDescHtml());
    	result.appendChild(doc.createElement("date")).setTextContent(mall_detail.getDateString());
    	result.appendChild(doc.createElement("phone")).setTextContent(mall_detail.getPhone());
    	result.appendChild(doc.createElement("addr")).setTextContent(mall_detail.getAddr());
    	result.appendChild(doc.createElement("open-time")).setTextContent(mall_detail.getOpenTime());
    	result.appendChild(doc.createElement("mail")).setTextContent(mall_detail.getMail());
       	result.appendChild(doc.createElement("note")).setTextContent(mall_detail.getNote());
       	result.appendChild(doc.createElement("status")).setTextContent(String.valueOf(mall_detail.getStatus()));
       	result.appendChild(doc.createElement("pause")).setTextContent(String.valueOf(mall_detail.getPause()));
       	result.appendChild(doc.createElement("pause-message")).setTextContent(mall_detail.getPauseMessage());
       	result.appendChild(doc.createElement("last")).setTextContent(mall_detail.getLastString());
       	result.appendChild(doc.createElement("bom-key")).setTextContent(mall_detail.getBomKey());
       	result.appendChild(doc.createElement("latitude")).setTextContent(String.valueOf(mall_detail.getLatitude()));
       	result.appendChild(doc.createElement("longitude")).setTextContent(String.valueOf(mall_detail.getLongitude()));
       	result.appendChild(doc.createElement("likes")).setTextContent(String.valueOf(mall_detail.getLikes()));
       	result.appendChild(doc.createElement("bonus-percent")).setTextContent(mall_detail.getBonusPercent());
       	result.appendChild(doc.createElement("bonus-product-limit")).setTextContent(mall_detail.getBonusProductLimit());
       	result.appendChild(doc.createElement("service-type")).setTextContent(mall_detail.getServiceType());
       	result.appendChild(doc.createElement("service-message")).setTextContent(mall_detail.getServiceMessage());
      	//
		org.w3c.dom.Element icons_element = (org.w3c.dom.Element)result.appendChild(doc.createElement("mall-icons"));
        List<String> icons_list = mall_detail.getMallIcons();
        if(icons_list != null){
        	for(String icons_string : icons_list){
        		if(icons_string != null && icons_string.trim().length() > 0)
        			icons_element.appendChild(doc.createElement("mall-icon")).setTextContent(icons_string);
        	}
        }
       	//
       	result.appendChild(doc.createElement("mall-bg-icon")).setTextContent(mall_detail.getMallBgIcon());
       	result.appendChild(doc.createElement("menu-bg-icon")).setTextContent(mall_detail.getMenuBgIcon());
       	result.appendChild(doc.createElement("barter-bg-icon")).setTextContent(mall_detail.getBarterBgIcon());
    	result.appendChild(doc.createElement("tz-offset")).setTextContent(String.valueOf(mall_detail.getTzOffset()));
       	result.appendChild(doc.createElement("min-outgo")).setTextContent(String.valueOf(mall_detail.getMinOutgo()));
       	result.appendChild(doc.createElement("fare-delivery")).setTextContent(String.valueOf(mall_detail.getFareDelivery()));
       	result.appendChild(doc.createElement("max-delivery")).setTextContent(String.valueOf(mall_detail.getMaxDelivery()));
       	
    }

}
