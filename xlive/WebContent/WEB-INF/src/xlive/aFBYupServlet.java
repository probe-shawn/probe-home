package xlive;

import java.io.*;
import java.net.URLDecoder;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.appengine.api.datastore.AsyncDatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.KeyFactory;
import xlive.method.yup.uGetProduct;
import xlive.method.yup.uGetStore;


@SuppressWarnings("serial")
public class aFBYupServlet extends HttpServlet {
	
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
		String fb_ref=request.getParameter("fb_ref");
		String meta = null;
		if(!"execute".equalsIgnoreCase(fb_ref)){
			String prefix = 
			"<!DOCTYPE html>"+
			"<html xmlns=\"http://www.w3.org/1999/xhtml\"  xmlns:og=\"http://ogp.me/ns#\" xmlns:fb=\"https://www.facebook.com/2008/fbml\">"+
			"<head>"+
			"<title>YUP-menu</title>";
			
			String fbns=request.getParameter("fbns");
			String sid=request.getParameter("sid");
			String tag=request.getParameter("tag");
			String prod=request.getParameter("prod");
			String style=request.getParameter("style");
			if(fbns!=null && fbns.trim().length() > 0) sid=fbns;
			if(sid != null && sid.trim().length() > 0)	meta = this.getMetaProperties(sid,tag,prod,style);
			String postfix="</head><body></body></html>";
			
			OutputStreamWriter output_stream_writer= new OutputStreamWriter(response.getOutputStream(), "UTF-8");
			output_stream_writer.write(prefix);
			if(meta != null) output_stream_writer.write(meta);
			output_stream_writer.write(postfix);
			output_stream_writer.close();
			return;
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
      	boolean injected=(meta==null)?true:false;
      	while((length=input_stream_reader.read(cbuf)) !=-1){
      		if(!injected){
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
      				injected=true;
      				continue;
      			}
      		}
      		output_stream_writer.write(cbuf, 0, length);
      	}
      	input_stream_reader.close();
      	output_stream_writer.close();
  }
  private String getMetaProperties(String sid, String tag, String prod,String style){
	  	String meta="";
	  	AsyncDatastoreService ads = DatastoreServiceFactory.getAsyncDatastoreService();
	  	Future<Entity> future_store, future_prod; 
	  	
		future_store = (sid != null && sid.trim().length() > 0)? future_store=ads.get(uGetStore.generateKey(sid)):null;
		future_prod = (prod != null && prod.trim().length() > 0)? future_prod=ads.get(KeyFactory.stringToKey(prod)):null;
			
		JSONObject store = null,product=null;
		Entity store_entity = null, product_entity = null;
		if(future_store != null) {
			try {
				store_entity= future_store.get();
				if(store_entity!=null){
					store = new JSONObject();
					uGetStore.EntityToJSO(store_entity, store);
				}
			} catch (InterruptedException e) {
			} catch (ExecutionException e) {
			} catch (JSONException e) {
			}
		}
		if(future_prod != null) {
			try {
				product_entity= future_prod.get();
				if(product_entity != null){
					product = new JSONObject();
					uGetProduct.EntityToJSO(product_entity, product);
					product.put("key", KeyFactory.keyToString(product_entity.getKey()));
				}
			} catch (InterruptedException e) {
			} catch (ExecutionException e) {
			} catch (JSONException e) {
			}
		}
		if(store_entity == null) return null;
		String title = "";
		if(product != null){
			title = product.optString("name");
		}else{
			String store_name = store.optString("name");
			String store_sub_name = store.optString("sub_name");
			title = store_name;
			if(store_sub_name != null && store_sub_name.length() > 0) title +=" - "+store_sub_name;
		}
		meta += "<meta property=\"og:title\" content=\""+title+"\"/>";
		//
		// type
		String type="article";
		meta += "<meta property=\"og:type\" content=\""+type+"\"/>";
		//
		// url
		String url="";
		if(product != null)
			 url = product.optString("key")+"?sid="+store.optString("sid")+"&tag="+tag+"&prod="+product.optString("key")+"&style="+style;
		else url = store.optString("sid")+"?sid="+store.optString("sid");
		meta += "<meta property=\"og:url\" content=\"http://yupmenu.appspot.com/ayup/"+url+"\"/>";
		//
		//icon 
		String icon="";
	    if(product != null) {
	    	JSONObject obj_icon = product.optJSONObject("icon");
	    	if(obj_icon != null) icon = obj_icon.optString("url");
	    }else {
	    	JSONObject obj_icon = store.optJSONObject("icon");
	    	if(obj_icon != null) icon = obj_icon.optString("url");
	    }
	    icon=icon.replace("_s.", "_n.");
	    
	    meta += "<meta property=\"og:image\" content=\""+icon+"\"/>";
	    //
	    meta += "<meta property=\"og:site_name\" content=\""+store.optString("name")+"\"/>";
	    meta += "<meta property=\"fb:app_id\" content=\"449312651828567\"/>";
	    //
	    String desc= "";
	    if(product !=null) desc = product.optString("desc");
	    else  	desc = store.optString("desc");
	    meta += "<meta property=\"og:description\" content=\""+desc+"\"/>";
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
        return "xlive.FBYup Information";
  }
 
}
