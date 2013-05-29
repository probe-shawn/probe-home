package xlive.method.rst.order;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import javax.mail.Session;
import javax.mail.internet.MimeMessage; 
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;

import xlive.xUtility;
import xlive.method.*;
import xlive.method.logger.xLogger;
import xlive.method.rst.mall.xMallDetail;

public class xMailOrderMethod extends xDefaultMethod{
	public Object process()throws xMethodException{
		//System.out.println("test run mail");
		String mall_fid = this.getArguments("mall-fid");
		String order_id=this.getArguments("order-id");
		String type=this.getArguments("type");//0�O����,1�O�s�i,2�O����
		Key mall_key=xMallDetail.generateKey(mall_fid);
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		xMallDetail mall=null;
		try{			
    		Entity mall_entity = ds.get(mall_key);
    		mall = new xMallDetail(mall_entity);
    		if(type.equals("1") && (mall.getMail()==null || mall.getMail().length()<3))
    			return this.getServiceContext().doNextProcess();    		
    	}catch(Exception e){
    		return this.getServiceContext().doNextProcess();
    	}
    	xOrder order=null; 	
	    Entity order_entity =null;	   
	    ArrayList<String> al = new ArrayList<String>();
	    int count=0;
		try{
			order_entity = ds.get( xOrder.generateKey(mall_fid, order_id));
			order = new xOrder(order_entity);
			if(!(type.equals("1")) && (order.getMail()==null || order.getMail().length()<3))
    			return this.getServiceContext().doNextProcess();
			Query q = new Query(xGoods.class.getSimpleName(), order.entity.getKey());
	    	PreparedQuery pq = ds.prepare(q);	    	
	    	for(Entity found : pq.asIterable()) {	    		
	    		xGoods goods = new xGoods(found);
	    		al.add(goods.getId());
	    		al.add(goods.getName());
	    		al.add(String.valueOf(goods.getPrice()));
	    		al.add(String.valueOf(goods.getQty()));	    		 	    		
	    		al.add(goods.getOptions());
	    		al.add(goods.getAddons());
	    		count++;
	    	}
			
		}catch(Exception e){
			return this.getServiceContext().doNextProcess();
		}
		
		long offset = -480;
		String orderdate=order.getDateString();
		
		Calendar cal = Calendar.getInstance();
		if(orderdate != null && orderdate.trim().length() >0){
			cal.setTime(xUtility.parseDate(orderdate));
		}
		cal.setTimeInMillis(cal.getTimeInMillis()-offset*60*1000);
		//cal.set(Calendar.HOUR_OF_DAY, 0);
		//cal.set(Calendar.MINUTE, 0);
		//cal.set(Calendar.SECOND, 0);
		//cal.set(Calendar.MILLISECOND,0);
		Date cal_date=cal.getTime();
		String date_string = new SimpleDateFormat("yyyy/MM/dd-HH:mm").format(cal_date);
		
		String order_html = "";
		
		order_html+="<div id='logo_area' style='position:relative;margin-left:50px;height:140px;color:white;line-height:64px;text-align:center;width:500px;'>";
		order_html+="<img src='http://xlive-rst2.appspot.com/xlive/images/rst/imall_logo.png' style='width:200px;margin-top:20px;'></img>";
		order_html+="</div>";		
		
		if(type.equals("1"))
			order_html+="<div style=\"position:relative;margin:0px 0px 20px 20px;\">iMall�s�i�q��q��</div>";
		if(type.equals("0"))
			order_html+="<div style=\"position:relative;margin:0px 0px 20px 20px;\">�z���q��X�{���D,������N��������,�бz�w�ۻP�Ӯa�q���pô,�H�d����]</div>";
		if(type.equals("2"))
			order_html+="<div style=\"position:relative;margin:0px 0px 20px 20px;\">�z���q��w���\�q���Ӯa,���R�ݰӮa���B�z�γs��</div>";
		if(type.equals("-1"))
			order_html+="<div style=\"position:relative;margin:0px 0px 20px 20px;\">�Ӯa�L�k�������, �бz�w�ۻP�Ӯa�q���pô, �H�d����]</div>";
		if(type.equals("3"))
			order_html+="<div style=\"position:relative;margin:0px 0px 20px 20px;\">�������, �w���~��ϥ�iMall</div>";
		
		
		order_html+="<div style=\"position:relative;margin:0px 0px 20px 20px;\">";
		order_html+="<div><span>Mall�W�� : </span><span>"+mall.getStoreName()+"</span></div>";
		order_html+="<div><span>�����s�� : </span><a href=\"http://apps.facebook.com/iiimall/?fbns="+mall.getFid()+"\">http://apps.facebook.com/iiimall/</a></div>";
		order_html+="<div><span>�q��s�� : </span><span>"+order.getId()+"</span></div>";
		order_html+="<div><span>�q�ʤ�� : </span><span>"+date_string+"</span></div>";
		order_html+="<div><span>�I�ڤ覡 : </span><span>"+order.getPayment()+"</span></div>";
		order_html+="<div><span>�ʶR�H�m�W : </span><span>"+order.getName()+"</span></div>";
		order_html+="<div><span>�s���q�� : </span><span>"+order.getPhone()+"</span></div>";
		if(type.equals("1") && order.getMail()!=null && order.getMail().length()>0)
			order_html+="<div><span>�q�l�H�c : </span><span>"+order.getMail()+"</span></div>";
		if(type.equals("1") && order.getAddr()!=null && order.getAddr().length()>0)
			order_html+="<div><span>�a�} : </span><span>"+order.getAddr()+"</span></div>";
		if(type.equals("1") && order.getNote()!=null && order.getNote().length()>0)
			order_html+="<div><span>�Ƶ� : </span><span>"+order.getNote()+"</span></div>";		
		
		order_html += "</div>";		
		
		order_html +="<div id=\"detail_header\" style=\"position:relative;margin:0px 0px 20px 20px;color:black;font-size:15px;\">";
		order_html +="<div style=\"float:left;width:80px;text-align:center;padding:4px 0px;color:black;background:white;\">���O���B</div>";
		order_html +="<div id=\"sign_1\" style=\"float:left;line-height:16px;margin:4px;width:16px;text-align:center;\">+</div>";
		order_html +="<div style=\"float:left;width:80px;text-align:center;padding:4px 0px;color:black;background:white;\">�B�O</div>";
		order_html +="<div id=\"sign_2\" style=\"float:left;line-height:16px;margin:4px;width:16px;text-align:center;\">+</div>";
		order_html +="<div style=\"float:left;width:80px;text-align:center;padding:4px 0px;color:black;background:white;\">���e�覡</div>";
		order_html +="<div id=\"sign_3\" style=\"float:left;line-height:16px;margin:2px 6px;width:16px;text-align:center;\">-</div>";
		order_html +="<div style=\"float:left;width:80px;text-align:center;padding:4px 0px;background:white;cursor:pointer\" class=\"set_bonus\" id=\"set_bonus\">�ϥά��Q</div>";
		order_html +="<div id=\"sign_4\" style=\"float:left;line-height:16px;margin:4px 6px;width:16px;text-align:center;\">=</div>";
		order_html +="<div style=\"float:left;width:80px;text-align:center;padding:4px 0px;color:black;background:white;\">�����ڶ�</div>";
		order_html +="<div style=\"clear:both;\">&nbsp;</div>";
		order_html+="<div style=\"float:left;width:80px;align:center;margin:0px 0px 20px 20px;\">";
		order_html+="<div style=\"width:80px;text-align:center;margin:0 auto;\">"+String.valueOf(order.getTotal())+"</div>";
		order_html+="</div>";
		order_html+="<div style=\"float:left;width:80px;align:center;margin-left:18px\">";
		order_html+="<div style=\"width:80px;text-align:center;margin:0 auto;\" >"+String.valueOf(order.getFareDelivery())+"</div>";
		order_html+="</div>";	
		order_html+="<div style=\"float:left;width:80px;align:center;margin-left:18px\">";
		order_html+="<div style=\"width:80px;text-align:center;margin:0 auto;\" >"+order.getDeliver()+"</div>";
		order_html+="</div>";	
		order_html+="<div id=\"set_bonus_box\" style=\"float:left;width:80px;text-align:center;margin-left:16px;position:relative;cursor:poniter\" class=\"set_bonus\" >";
		order_html+="<span id=\"bonus\" style=\"cursor:pointer;color:red\" class=\"set_bonus\">"+String.valueOf(order.getBonusC2M())+"</span>";
		order_html+="<img class=\"set_bonus\" style=\"position:absolute;top:-24px;left:18px;cursor:pointer\" src=\"http://xlive-rst2.appspot.com/xlive/images/rst/txt_bonus.png\"/>";
		order_html+="</div>";
		order_html+="<div style=\"float:left;width:80px;align:center;;margin-left:18px\">";
		order_html+="<div style=\"width:80px;text-align:right;font-size:20px;font-weight:bold;margin:0 auto;color:blue;\" id=\"total_bonus\">"+String.valueOf(order.getTotal()-order.getBonusC2M()+order.getFareDelivery())+"</div>";
		order_html+="</div>";		
		order_html +="<div style=\"clear:both\"></div>";
		order_html +="</div>";
		
		order_html+="<div style=\"position:relative;margin:0px 0px 20px 20px;\">";
		order_html+="<table style=\"vertical-align:middle;text-align:center;border:2px solid black;\">";
		order_html+="<tr style=\"background-color:lightgray;margin:3px 0 3px 0;\"><td style=\"width:80px;\">���ؽs��</td><td style=\"width:140px;text-align:left\">���ئW��</td><td style=\"width:70px;\">���</td><td style=\"width:80px;text-align:right;\">�ƶq</td><td style=\"width:70px;\">��ܶ�</td><td style=\"width:70px;\">�[�ȶ�</td></tr>";
		for(int i=0;i<count*6;i+=6){
			order_html+="<tr style=\"margin:3px 0 3px 0;\">";
			if(al.get(i)!=null && al.get(i).length()>0)
				order_html+="<td>"+al.get(i)+"</td>";
			else
				order_html+="<td>"+"(��)"+"</td>";
			order_html+="<td style=\"text-align:left\">"+al.get(i+1)+"</td>";
			order_html+="<td>"+al.get(i+2)+"</td>";
			order_html+="<td style=\"text-align:right\">"+al.get(i+3)+"</td>";
			if(al.get(i+4)!=null && al.get(i+4).length()>0)
				order_html+="<td>"+al.get(i+4)+"</td>";
			else
				order_html+="<td>"+"�L"+"</td>";
			if(al.get(i+5)!=null && al.get(i+5).length()>0)
				order_html+="<td>"+al.get(i+5)+"</td>";	
			else
				order_html+="<td>"+"�L"+"</td>";
			order_html+="</tr>";
		}
		
		
		order_html +="</table></div>";
		order_html +="<div style=\"clear:both\"></div>";
		order_html +="<div style=\"margin:20px 0px 0px 20px;color:red;\">�����t�ΫH,�ФŪ����^�Х��H!</div>";
		
		
	
		System.out.println(order_html);
    	//�H�U�O�oMAIL�@�~
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);
        try {       	
            Message msg = new MimeMessage(session);      
            
            msg.setFrom(new InternetAddress("probe.imall@gmail.com",javax.mail.internet.MimeUtility.encodeText("IMALL�q��t��","big5",null)));
            if(type.equals("1"))
            	msg.addRecipient(Message.RecipientType.TO,new InternetAddress(mall.getMail().trim(), javax.mail.internet.MimeUtility.encodeText(mall.getStoreName(),"big5",null))); 
            else
            	msg.addRecipient(Message.RecipientType.TO,new InternetAddress(order.getMail().trim(),javax.mail.internet.MimeUtility.encodeText(order.getName(),"big5",null))); 
            msg.setSubject(javax.mail.internet.MimeUtility.encodeText(mall.getStoreName()+" �q��q�� :"+order.getId(),"big5",null));
            Multipart mp = new MimeMultipart();
            MimeBodyPart htmlPart = new MimeBodyPart();
            htmlPart.setContent(order_html, "text/html");
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
      
       
		return this.getServiceContext().doNextProcess();
	}
}

