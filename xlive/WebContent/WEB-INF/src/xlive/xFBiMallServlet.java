package xlive;

import java.io.*;
import java.net.URLDecoder;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.AsyncDatastoreService;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.KeyFactory;

import xlive.method.rst.barter.xBarter;
import xlive.method.rst.mall.xMallDetail;
import xlive.method.rst.menu.xBom;


@SuppressWarnings("serial")
public class xFBiMallServlet extends HttpServlet {
	
	private String contextPath;
	private String resourceEncoding=null;
	
	public static boolean isGAE(){
		String env = System.getProperty("com.google.appengine.runtime.environment"); 
		return (env != null);
	}
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		contextPath=getServletContext().getContextPath();
		resourceEncoding = config.getInitParameter("resource_encoding");
	}
	public void doPut(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		super.doPut(req,res);
		return;
	}
	public void doDelete(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		super.doDelete(req, res);
		return;
	}
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		doPost(req, res); 
		return;
	}
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String fbns=request.getParameter("fbns");
		String prod=request.getParameter("prod");
		String barter=request.getParameter("barter");
		String meta = null;
		if(fbns!=null && fbns.trim().length() > 0){
			if(prod != null && prod.trim().length()>0) meta=this.getProdMetaProperties(fbns,prod);
			else if(barter != null && barter.trim().length() > 0)meta=this.getBarterMetaProperties(fbns,barter);
			else meta=this.getProdMetaProperties(fbns,null);
		}
		//
		String encoding=request.getCharacterEncoding();
  	  	String request_uri=request.getRequestURI();
  	  	String res_path=request_uri.replaceFirst(contextPath, "");
  	  	//
  	  	res_path="/index.html";
  	  	//
  	  	encoding=(encoding == null) ? "utf-8":encoding;
  	  	res_path = URLDecoder.decode(res_path, encoding);
      	response.setCharacterEncoding("UTF-8");
      	String res_encoding = System.getProperty("xlive.resource.default_encoding"); 
      	if(res_encoding == null || res_encoding.trim().length()==0) res_encoding=resourceEncoding;
      	InputStreamReader input_stream_reader=(res_encoding !=null && res_encoding.trim().length()>0)?
      			new InputStreamReader(xResourceManager.getResourceAsStream(res_path),res_encoding) :
      			new InputStreamReader(xResourceManager.getResourceAsStream(res_path));
      	OutputStreamWriter output_stream_writer= new OutputStreamWriter(response.getOutputStream(), "UTF-8");
      	char[]cbuf=new char[10240];
      	int length=-1;
      	boolean inject=false;
      	while((length=input_stream_reader.read(cbuf)) !=-1){
      		if(!inject){
      			int found=-1;
      			for(int i=10; i<length-6; ++i){
      				if((cbuf[i]=='<') && (cbuf[i+1]=='t') && (cbuf[i+2]=='i') && (cbuf[i+3]=='t') && (cbuf[i+4]=='l') && (cbuf[i+5]=='e') && (cbuf[i+6]=='>')){
      					found=i;
      					break;
      				}
      			}
      			if(found > 0){
      				int pos=found;
      				output_stream_writer.write(cbuf, 0, pos);
      				if(meta != null)output_stream_writer.write(meta.toCharArray());
      				output_stream_writer.write(cbuf, pos,length-pos);
     				inject=true;
      				continue;
      			}
      		}
      		output_stream_writer.write(cbuf, 0, length);
      	}
      	input_stream_reader.close();
      	output_stream_writer.close();
  }
  private String getProdMetaProperties(String fbns,String prod){
	  	String meta="";
	  	AsyncDatastoreService ads = DatastoreServiceFactory.getAsyncDatastoreService();
  		xMallDetail mall_detail = null; 
  		xBom bom = null;
  		
		Future<Entity> future_prod = null;
		try {
			future_prod = (prod != null && prod.trim().length() > 0)? future_prod=ads.get(KeyFactory.stringToKey(prod)):null;
			Entity mall_entity = ads.get(xMallDetail.generateKey(fbns)).get();
			if(mall_entity != null)mall_detail = new xMallDetail(mall_entity);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} 
		if(mall_detail == null) return null;
		if(future_prod != null) {
			try {
				Entity bom_entity = future_prod.get();
				if(bom_entity != null) bom = new xBom(bom_entity);
			} catch (InterruptedException e) {
			} catch (ExecutionException e) {
			}
		}
		String store=mall_detail.getStoreName();
		store =(store.equals(mall_detail.getName()))?"":store;
		store =(store != null && store.trim().length()>0)?"("+store+")":"";
		
		if(bom != null)	meta += "<meta property=\"og:title\" content=\""+mall_detail.getName()+" : "+bom.getName()+"\"/>";
		else meta += "<meta property=\"og:title\" content=\""+mall_detail.getName()+" "+store+"\"/>";
		
		String type=mall_detail.getType();
		type = (type==null ||type.trim().length()==0)? "company" :type;//
		type="article";
		meta += "<meta property=\"og:type\" content=\""+type+"\"/>";
		/*
		if(bom != null)
			meta += "<meta property=\"og:url\" content=\"http://apps.facebook.com/iiimall/imall/"+bom.getKey()+"?fbns="+fbns+"&prod="+bom.getKey()+"\"/>";
		else
			meta += "<meta property=\"og:url\" content=\"http://apps.facebook.com/iiimall/imall/"+fbns+"?fbns="+fbns+"\"/>";
		*/
		
		if(bom != null)
			meta += "<meta property=\"og:url\" content=\"http://xlive-rst2.appspot.com/imall/"+bom.getKey()+"?fbns="+fbns+"&prod="+bom.getKey()+"\"/>";
		else
			meta += "<meta property=\"og:url\" content=\"http://xlive-rst2.appspot.com/imall/"+fbns+"?fbns="+fbns+"\"/>";
		
	    
	    if(bom != null) {
	    	String path=bom.getIcon();
	    	if(path.toLowerCase().startsWith("http"))
	    		 meta += "<meta property=\"og:image\" content=\""+path+"\"/>";
	    	else meta += "<meta property=\"og:image\" content=\"http://xlive-rst2.appspot.com/"+path+"\"/>";
	    }else meta += "<meta property=\"og:image\" content=\"http://graph.facebook.com/"+fbns+"/picture?type=square\"/>";//?type=normal
	    	//meta += "<meta property=\"og:image\" content=\"http://xlive-rst2.appspot.com/xlive/images/rst/cart.png\"/>";
	    
	    meta += "<meta property=\"og:site_name\" content=\""+mall_detail.getName()+" "+store+"\"/>";
	    //meta += "<meta property=\"fb:admins\" content=\""+fbns+",1493024834\"/>";
	    meta += "<meta property=\"fb:app_id\" content=\"209447899065902\"/>";
	    
	    String desc= mall_detail.getDesc();
	    String desc_html = mall_detail.getDescHtml();
	    desc = (desc==null ||desc.trim().length()==0)? "":desc+"\n";
	    desc_html = (desc_html==null ||desc_html.trim().length()==0)? "":desc_html.replaceAll("<[^>]+>", "");
	    desc += desc_html;
	    desc = (desc==null ||desc.trim().length()==0)? "社群新經濟資訊服務平台. 收藏分享, 以物易物, 個體宅經濟, 臉書商城, 以社群人脈經營信任行銷.":desc;
	    
	    String bom_desc=mall_detail.getName()+" "+store+", 嚴選精品 。 ";
	    if(bom !=null){
	    	bom_desc = bom.getBriefHtml();
	    	//bom_desc=(bom_desc != null && bom_desc.trim().length() > 0) ? org.apache.commons.lang3.StringEscapeUtils.escapeHtml4(bom.getBriefHtml()): mall_detail.getName()+store+", 嚴選精品 。 ";
	    	
	    	if(bom_desc != null && bom_desc.trim().length() > 0){
		    	bom_desc =  bom.getBriefHtml().replaceAll("<[^>]+>", "");
		    }else bom_desc = mall_detail.getName()+store+", 嚴選精品 。 ";
		    
	    }
	    
	    if(bom != null) meta += "<meta property=\"og:description\" content=\""+bom_desc+"\"/>";
	    else  meta += "<meta property=\"og:description\" content=\""+desc+"\"/>";
	    /*
	    //Location
	    meta += "<meta property=\"og:latitude\" content=\""+mall_detail.getLatitude()+"\"/>";
	    meta += "<meta property=\"og:longitude\" content=\""+mall_detail.getLongitude()+"\"/>";
	    meta += "<meta property=\"og:street-address\" content=\""+mall_detail.getAddr()+"\"/>";
	    meta += "<meta property=\"og:locality\" content=\""+"TAIPEI"+"\"/>";
	    meta += "<meta property=\"og:region\" content=\""+"TW"+"\"/>";
	    meta += "<meta property=\"og:postal-code\" content=\""+"106"+"\"/>";
	    meta += "<meta property=\"og:country-name\" content=\""+"Taiwan"+"\"/>";
	    //Contact Information
	    meta += "<meta property=\"og:email\" content=\""+mall_detail.getMail()+"\"/>";
	    meta += "<meta property=\"og:phone_number\" content=\""+mall_detail.getPhone()+"\"/>";
	    //meta += "<meta property=\"og:fax_number\" content=\""+mall.getPhone()+"\"/>";
	     */
	    return meta;
  }
  private String getBarterMetaProperties(String fbns,String barter_keystr){
	  	String meta="";
	  	DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		xMallDetail mall_detail = null; 
		xBarter barter = null;
		if(barter_keystr != null && barter_keystr.trim().length() > 0){
			try {
				Entity barter_entity = ds.get(KeyFactory.stringToKey(barter_keystr));
				barter = new xBarter(barter_entity);
			} catch (EntityNotFoundException e) {barter=null;}
		}
		try {
			Entity mall_entity = ds.get(xMallDetail.generateKey(fbns));
			mall_detail = new xMallDetail(mall_entity);
		}catch (EntityNotFoundException e){}
		
		if(mall_detail == null) return null;
		
		String store=mall_detail.getStoreName();
		store =(store.equals(mall_detail.getName()))?"":store;
		store =(store != null && store.trim().length()>0)?"("+store+")":"";
		
		if(barter != null)	meta += "<meta property=\"og:title\" content=\""+mall_detail.getName()+" 分享 : "+barter.getName()+"\"/>";
		else meta += "<meta property=\"og:title\" content=\""+mall_detail.getName()+" 以物易物分享 "+"\"/>";
		
		String type=mall_detail.getType();
		type = (type==null ||type.trim().length()==0)? "company" :type;//
		type="article";
		meta += "<meta property=\"og:type\" content=\""+type+"\"/>";
		/*
		if(bom != null)
			meta += "<meta property=\"og:url\" content=\"http://apps.facebook.com/iiimall/imall/"+bom.getKey()+"?fbns="+fbns+"&prod="+bom.getKey()+"\"/>";
		else
			meta += "<meta property=\"og:url\" content=\"http://apps.facebook.com/iiimall/imall/"+fbns+"?fbns="+fbns+"\"/>";
		*/
		
		if(barter != null)
			meta += "<meta property=\"og:url\" content=\"http://xlive-rst2.appspot.com/imall/"+barter.getKey()+"?fbns="+fbns+"&barter="+barter.getKey()+"\"/>";
		else
			meta += "<meta property=\"og:url\" content=\"http://xlive-rst2.appspot.com/imall/"+fbns+"?fbns="+fbns+"\"/>";
		
	    
	    if(barter != null) {
	    	String path=barter.getIcon();
	    	if(path.toLowerCase().startsWith("http"))
	    		 meta += "<meta property=\"og:image\" content=\""+path+"\"/>";
	    	else meta += "<meta property=\"og:image\" content=\"http://xlive-rst2.appspot.com/"+path+"\"/>";
	    }else meta += "<meta property=\"og:image\" content=\"http://graph.facebook.com/"+fbns+"/picture?type=square\"/>";//?type=normal
	    
	    meta += "<meta property=\"og:site_name\" content=\""+mall_detail.getName()+" 以物易物分享 "+"\"/>";
	    //meta += "<meta property=\"fb:admins\" content=\""+fbns+",1493024834\"/>";
	    meta += "<meta property=\"fb:app_id\" content=\"209447899065902\"/>";
	    
	    String desc= mall_detail.getDesc();
	    String desc_html = mall_detail.getDescHtml();
	    desc = (desc==null ||desc.trim().length()==0)? "":desc+"\n";
	    desc_html = (desc_html==null ||desc_html.trim().length()==0)? "":desc_html.replaceAll("<[^>]+>", "");
	    desc += desc_html;
	    desc = (desc==null ||desc.trim().length()==0)? "iMall for everyone.":desc;
	    
	    String barter_desc=mall_detail.getName()+" 以物易物"+", 收藏分享 。 ";
	    if(barter !=null){
	    	barter_desc = barter.getDescHtml();
	    	//bom_desc=(bom_desc != null && bom_desc.trim().length() > 0) ? org.apache.commons.lang3.StringEscapeUtils.escapeHtml4(bom.getBriefHtml()): mall_detail.getName()+store+", 嚴選精品 。 ";
	    	if(barter_desc != null && barter_desc.trim().length() > 0){
	    		barter_desc =  barter.getDescHtml().replaceAll("<[^>]+>", "");
		    }else barter_desc = mall_detail.getName()+" 以物易物"+", 收藏分享 。 ";
	    }
	    
	    if(barter != null) meta += "<meta property=\"og:description\" content=\""+barter_desc+"\"/>";
	    else  meta += "<meta property=\"og:description\" content=\""+desc+"\"/>";
	    /*
	    //Location
	    meta += "<meta property=\"og:latitude\" content=\""+mall_detail.getLatitude()+"\"/>";
	    meta += "<meta property=\"og:longitude\" content=\""+mall_detail.getLongitude()+"\"/>";
	    meta += "<meta property=\"og:street-address\" content=\""+mall_detail.getAddr()+"\"/>";
	    meta += "<meta property=\"og:locality\" content=\""+"TAIPEI"+"\"/>";
	    meta += "<meta property=\"og:region\" content=\""+"TW"+"\"/>";
	    meta += "<meta property=\"og:postal-code\" content=\""+"106"+"\"/>";
	    meta += "<meta property=\"og:country-name\" content=\""+"Taiwan"+"\"/>";
	    //Contact Information
	    meta += "<meta property=\"og:email\" content=\""+mall_detail.getMail()+"\"/>";
	    meta += "<meta property=\"og:phone_number\" content=\""+mall_detail.getPhone()+"\"/>";
	    //meta += "<meta property=\"og:fax_number\" content=\""+mall.getPhone()+"\"/>";
	     */
	    return meta;
}

  public String getServletInfo() {
        return "xlive.FBiMall Information";
  }
 
}
