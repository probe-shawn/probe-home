package xlive;

import java.io.*;
import java.net.URLDecoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

import xlive.method.logger.xLogger;
import xlive.method.rst.circle.xCircle;
import xlive.method.rst.mall.xMallDetail;
/*
import com.google.appengine.repackaged.org.json.JSONException;
import com.google.appengine.repackaged.org.json.JSONObject;
*/
import org.json.*;

import com.google.appengine.api.datastore.AsyncDatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;


@SuppressWarnings("serial")
public class xFBPagesServlet extends HttpServlet {
	
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
		String signed_request =  request.getParameter("signed_request"); //String secrect="1e51607149f342df6cb329ea60fab68d";
		if(signed_request == null)signed_request="";
		String payload = signed_request.split("\\.")[1];
		String mall_fid = getMallFid(payload);
		String meta = "";
		/*
		if(mall_fid != null){
		 	DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		 	try {
		 		xMallDetail mall_detail = null; 
		 		Entity mall_entity = ds.get(xMallDetail.generateKey(mall_fid));
				if(mall_entity != null)mall_detail = new xMallDetail(mall_entity);
				if(mall_detail != null){
					String icon = mall_detail.getMallIcon();
					if(icon != null && icon.trim().length()>0)  meta += "<meta name=\"mall_image\" content=\""+icon+"\"/>";
				}

			} catch (EntityNotFoundException e) {
			}
		}
		*/
		String mall_url = "http://apps.facebook.com/iiimall/imall";
		if(mall_fid != null) {
			AsyncDatastoreService ads = DatastoreServiceFactory.getAsyncDatastoreService();
			Future<Entity> mall_future=null, circle_future=null;
			circle_future = ads.get(xCircle.generateKey(mall_fid));
			mall_future = ads.get(xMallDetail.generateKey(mall_fid));
			Entity mall_entity=null, circle_entity=null;
			try {
				circle_entity = circle_future.get();
				if(circle_entity != null) mall_url += "/"+mall_fid+"?circle="+mall_fid;
			} catch (InterruptedException e) {
				circle_entity=null;
			} catch (ExecutionException e) {
				circle_entity=null;
			}
			if(circle_entity == null){
				try {
					mall_entity = mall_future.get();
					if(mall_entity != null)mall_url += "/"+mall_fid+"?fbns="+mall_fid;
				} catch (InterruptedException e) {
				} catch (ExecutionException e) {
				}
			}
		}
		meta += "<meta name=\"mall_url\" content=\""+mall_url+"\"/>";
		//  
		String encoding=request.getCharacterEncoding();
		String request_uri=request.getRequestURI();
		String res_path=request_uri.replaceFirst(contextPath, "");
		//
		res_path="/page.html";
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
		      	}
		    }
		    output_stream_writer.write(cbuf, 0, length);
		}
		input_stream_reader.close();
		output_stream_writer.close();
  }
  
  public String getServletInfo() {
        return "xlive.FBPagesSrvlet Information";
  }
  public String getMallFid(String payload){
	  String mall_fid=null;
	  payload = payload.replaceAll("-", "+").replaceAll("_", "/").trim();
	  try {
         byte[] decodedPayload = new Base64().decode(payload.getBytes());
         payload = new String(decodedPayload, "UTF8");
         int index1 = payload.indexOf("\"id\":\"");
         int index2 = payload.indexOf("\",",index1);
         mall_fid = payload.substring(index1+6,index2);
	  }catch (Exception e) {
         return null;
	  }
	  return mall_fid;
  }
  	/*
      JSONObject json = jsonExecute(payload);
   	  try {
		  mall_fid = json.getJSONObject("page").getString("id");
	  }catch(JSONException e) {
	  }catch(Exception e){
	  }
 	//boolean ok = OAuth2(signed_request,secrect);
    */
  public JSONObject jsonExecute(String payload)
  {
     JSONObject payloadObject;
     payload = payload.replaceAll("-", "+").replaceAll("_", "/").trim();
     try {
         byte[] decodedPayload = new Base64().decode(payload.getBytes());
         payload = new String(decodedPayload, "UTF8");
         xLogger.log(Level.SEVERE, "payload:"+payload);
     }catch (Exception e) {
    	 xLogger.log(Level.SEVERE, "payload Exception:"+payload);
         return null;
     }
     try {
         payloadObject = new JSONObject(payload);
     }catch (JSONException e){
    	 xLogger.log(Level.SEVERE, "payloadObject Exception:"+e.getMessage());
         return null;
     }
     return payloadObject;
  }
  
  public boolean OAuth2(String signed_request,String secrect)
  {
     String encoded_sig = signed_request.split("\\.")[0];
     String pay = signed_request.split("\\.")[1];
     byte[] expected_sig = new Base64().decode(encoded_sig.replaceAll("-", "+").replaceAll("_", "/").getBytes());
     SecretKeySpec secretKeySpec = new SecretKeySpec(secrect.getBytes(), "HmacSHA256");
     Mac mac = null;
     try {
        mac = Mac.getInstance("HmacSHA256");
     } catch (NoSuchAlgorithmException e1) {
        e1.printStackTrace();
     }
     try {
        mac.init(secretKeySpec);
     } catch (InvalidKeyException e1) {
        e1.printStackTrace();
     }
     byte[] actual_sig = mac.doFinal(pay.getBytes());
     if (!Arrays.equals(actual_sig, expected_sig)) {
        return false;
     }
     return true;
  }

 
}
