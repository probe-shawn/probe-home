package xlive.method.rst.finance;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.w3c.dom.Element;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.api.datastore.TransactionOptions;
import com.google.appengine.api.datastore.Query.FilterOperator;
import xlive.google.xMemCache;
import xlive.method.*;
import xlive.method.img.barcode.*;
import xlive.method.logger.xLogger;
import xlive.method.rst.bonus.xBonus;
import xlive.method.rst.customer.xCustomer;
import xlive.method.rst.finance.xBillDetail;
import xlive.method.rst.mall.xMall;
import xlive.method.rst.mall.xMallDetail;
import xlive.method.rst.order.xOrder;
import xlive.google.ds.xLong;

public class xProcessIncomeMethod extends xDefaultMethod{
	public Object process()throws xMethodException{
		boolean valid=true;
		
		String keystr=this.getArguments("key");
		Entity entity =null;
		xValidMailData md=null;	
				
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		
		if(keystr != null && keystr.trim().length()>0){
			try{
				entity = ds.get(KeyFactory.stringToKey(keystr));
			}catch(Exception e){
				errorLog(e.getMessage()+"\n"+"銷帳錯誤，找不到有效的MailData");
				return this.getServiceContext().doNextProcess();
			}
		}
		
		md = new xValidMailData(entity);	
		List<String> records =md.getRecords();
		String test="";
		for(int i=0;i<records.size();i++){
			   test+="No"+String.valueOf(i+1)+":"+"\n";
			   String record=null;
			try{
				record="";
				String bid=null;
				xBillDetail bill_detail=null;
				record=records.get(i);
				test+="record="+record+"\n";
				Entity billentity =null;
				if(md.getType().equals("1"))				
					bid=record.substring(24,32);
				else			
					bid=record.substring(62,70);
				test+="bid="+bid+"\n";
				try{
					billentity = ds.get(xBillDetail.generateKey(bid));
					bill_detail = new xBillDetail(billentity);
				}catch(EntityNotFoundException e){
					exceptionRecord(md,record,test+"找不到帳單!");
					continue;//跳出迴圈進行下一筆
				
				}
				//如果此單已被銷過
				if(bill_detail.getWriteOff()!=0){
					if(!record.equals(bill_detail.getIncomeString()))
						exceptionRecord(md,record,test+"重覆繳款");//此情況為使用者重覆繳
					else 
						if(keystr.equals(bill_detail.getLink()))
							exceptionRecord(md,record,test+"重覆繳款");//此情況為使用者重覆繳,而且在同一個檔中
					continue;
					
				}
				
				//下面是處理銷帳作業必要時transation
				//區分新增紅利及核銷服務費．．．
				boolean isok=false;
				if(bill_detail.getType().equals("1"))
					isok=commitBonus(ds,bill_detail,record);
				else
					isok=commitService(ds,bill_detail,record);
				
				if(!isok)
					exceptionRecord(md,record,"銷帳錯誤，異常銷帳字串:"+record);
					
			}catch(Exception e){
				exceptionRecord(md,record,e.getMessage()+"\n"+"銷帳錯誤，異常銷帳字串:"+record);
			}
			
		}
		md.setDoneFlag("1");//代表這個檔已處理完
		md.setLast(new Date());
		ds.put(md.entity);
		return this.getServiceContext().doNextProcess(); 
	}
	
	public boolean commitBonus(DatastoreService ds,xBillDetail bill_detail,String r){
		Date current = new Date();
		boolean valid=true;
		if(bill_detail.getDoneFlag().equals("1")){
			bill_detail.setIncomeString(r);
			bill_detail.setWriteOff(bill_detail.getAmount());
			bill_detail.setLast(current);
			ds.put(bill_detail.entity);
			return valid;
		}
		
		Key mall_key=xMall.generateKey(bill_detail.getFid());
		ArrayList<Entity> put_array = new ArrayList<Entity>();
		TransactionOptions xgoptions = TransactionOptions.Builder.withXG(true);	    
		
		if( bill_detail.getQTY()> 0){ //add bonus
			Entity p2m_entity = new Entity(xBonus.class.getSimpleName(),mall_key);
			xBonus bonus = new xBonus(p2m_entity);
			bonus.setFromId("PROBE");
			bonus.setFromType("PROBE");
			bonus.setBonus(bill_detail.getQTY());
			bonus.setToId(bill_detail.getFid());
			bonus.setToType(xMall.bonusType);
			bonus.setDate(current);
			put_array.add(bonus.entity);
		}
		bill_detail.setDoneFlag("1");
		bill_detail.setIncomeString(r);
		bill_detail.setLast(current);
		bill_detail.setWriteOff(bill_detail.getAmount());
		put_array.add(bill_detail.entity);
		int retries = 3;
		
    	while(valid) {
    		Transaction txn = ds.beginTransaction(xgoptions);
    	    try {
    	    	Entity mall_entity=null;
    	    	
    	    	try{
    	    		mall_entity = ds.get(mall_key);
    	    		xMall mall = new xMall(mall_entity);
    	    		mall.setBonus(mall.getBonus()+bill_detail.getQTY());
    	    		put_array.add(mall_entity);
    	    	}catch(EntityNotFoundException e){
    	    		valid = false;	    	    		
    	    	}
    	    	
    	    	if(valid){
    	    		ds.put(put_array);
    	    		txn.commit();
    	    	}
    	        break;
    	    } catch (java.util.ConcurrentModificationException  e) {
    	        if(retries == 0) throw e;
    	        --retries;
    	    } finally {
    	        if(txn.isActive())txn.rollback();
    	    }
    	} 
    	return valid;
	}
	
	public boolean commitService(DatastoreService ds,xBillDetail bill_detail,String r){
		boolean valid=true;
		ArrayList<Entity> put_array = new ArrayList<Entity>();
		Entity sf_entity =null;
		xServiceFee sf=null;
		try{
    		sf_entity = ds.get(KeyFactory.stringToKey(bill_detail.getLink()));
    		sf = new xServiceFee(sf_entity);
    		sf.setDoneFlag("1");    		
    	}catch(EntityNotFoundException e){
    		valid = false;	    	    		
    	}
    	if(valid){
	    	bill_detail.setDoneFlag("1");
			bill_detail.setIncomeString(r);
			bill_detail.setWriteOff(bill_detail.getAmount());
			put_array.add(bill_detail.entity);
			put_array.add(sf.entity);
			ds.put(put_array);
    	}
    	return valid;//未完成服務費銷帳
	}
	public void errorLog(String err){
		xLogger.log(Level.WARNING,err);
		mailToAdmin(err);
	}
	public void exceptionRecord(xValidMailData md,String r,String msg){
		xLogger.log(Level.WARNING,"exception record:"+r+"\n"+msg);
		
		try{
			DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
			Entity entity =null;		  
			xInvalidMailData imd=null;
			entity = new Entity(xInvalidMailData.class.getSimpleName());
			imd = new xInvalidMailData(entity);			  
			imd.setDate(new Date());
			imd.setLast(new Date());
			imd.setFileName(md.getFileName());
			imd.setType(md.getType());
			imd.setFrom(md.getFrom());
			imd.setRecord(r);
			imd.setDoneFlag("0");
			imd.setRemark(md.getRemark()+msg);			
			ds.put(imd.entity);
			msg+=",異常資料已儲存!";
		}catch(Exception e){
			msg+=",異常資料未儲存!";
		}
		mailToAdmin(msg);
		
	}
	public void mailToAdmin(String remark){	  
		  //有狀況時通知系統管理者
		  
		  Properties props = new Properties();
	      Session session = Session.getDefaultInstance(props, null);
	      try {       	
	          Message msg = new MimeMessage(session);               
	          msg.setSubject("imall mail finance error");
	          msg.setFrom(new InternetAddress("probe.imall@gmail.com", "iMall"));
	          msg.setRecipient(Message.RecipientType.TO, new InternetAddress("probe.steven@gmail.com"));
	          String txt="";
	          txt+=remark;
	          Multipart mp = new MimeMultipart();
	          MimeBodyPart htmlPart = new MimeBodyPart();
	          htmlPart.setContent(txt, "text/html");
	          mp.addBodyPart(htmlPart);
	          msg.setContent(mp);
	          Transport.send(msg);
	      } catch (Exception e) {          
	    	  xLogger.log(Level.WARNING,"mailtoAdmin Exception:"+"\n"+ remark+"\n"+"err msg:"+e.getMessage());
	      }
	    
		  	  
	  }
}
