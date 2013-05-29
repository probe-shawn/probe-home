package xlive.method.yup;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import xlive.method.aMethodException;
import xlive.xUtility;
import xlive.method.*;
import org.json.JSONException;
import org.json.JSONObject;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;

public class uForgetPassword extends aDefaultMethod{ 	
	@Override
	public Object process(JSONObject server_jso, String method_name, JSONObject client_jso, JSONObject return_jso) throws aMethodException, JSONException {
		String why="";
		boolean valid=true;
		JSONObject arg=client_jso.getJSONObject("arg");
		String account=arg.optString("account");
		String password="";
		String email="";
		JSONObject member = new JSONObject();		
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		Query q = new Query("uMember");
	    	if(account != null && account.length()>0) {
	        	q.setFilter(new Query.FilterPredicate("account", FilterOperator.EQUAL, account));	        			
	    	}else{
	    		valid=false;why="account error";
	    		return_jso.put("valid", valid).put("why", why);
	    		return this.getServiceContext().doNextProcess();
	    	}
	    int count=0;
	    PreparedQuery pq = ds.prepare(q);
	    Entity mem=null; 
		for(Entity found : (Iterable<Entity>)pq.asIterable()) {
			xUtility.EntityToJSO(found, member);
			mem=found;
	        ++count;
	    }
		if(count!=1){
	    		valid=false;
	    		why="account error";
		}	
	    	else{
	    		email=member.getString("mail_mobile");
	    		if(email==null || email.length()==0)
	    			email=member.getString("mail_pc");
	    		if(email==null || email.length()==0){
	    			valid=false;
		    		why="no email!";
	    		}else{
	    			password=String.valueOf((new Date()).getTime());
	    			mem.setProperty("password", password);
	    			ds.put(mem);
	    			String mail_to = email;
	    			String mail_cc = "probe.steven@gmail.com";
	    			if(mail_to != null && mail_to.trim().length() > 0){
	    		        Properties props = new Properties();
	    		        Session session = Session.getDefaultInstance(props, null);
	    		        try {
	    		            Message msg = new MimeMessage(session);	    		           
	    		            msg.setFrom(new InternetAddress("probe.steven@gmail.com", "administrator"));
	    		            msg.addRecipient(Message.RecipientType.TO,new InternetAddress(mail_to, "Yup System"));
	    		            if(mail_cc != null && mail_cc.trim().length() > 0){
	    		            	 msg.addRecipient(Message.RecipientType.CC,new InternetAddress(mail_cc, "Yup System"));
	    		            }
	    		            msg.setSubject("Yup Password");
	    		            Multipart mp = new MimeMultipart();
	    		            MimeBodyPart htmlPart = new MimeBodyPart();
	    		            htmlPart.setContent("<html>您的帳號："+account+"<br>您新的密碼："+password+"</html>", "text/html");
	    		            mp.addBodyPart(htmlPart);
	    		            msg.setContent(mp);
	    		            Transport.send(msg);	    		            
	    		        } catch (AddressException e) {
	    		            e.printStackTrace();
	    		        } catch (MessagingException e) {
	    		            e.printStackTrace();
	    		        } catch (UnsupportedEncodingException e) {
	    					e.printStackTrace();
	    				}
	    			}
	    		}
	    	}
		
		return_jso.put("valid", valid).put("why", why);
		return this.getServiceContext().doNextProcess();
		
	}

}
