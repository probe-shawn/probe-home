package xlive.method.rst.finance;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import xlive.xUtility;
import xlive.method.*;
import xlive.method.logger.xLogger;
import xlive.method.rst.report.*;
import xlive.method.rst.finance.xServiceFee;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;

public class xProcessFeeMethod extends xDefaultMethod{
	public Object process()throws xMethodException{
		String mall_fid=this.getArguments("mall-fid");     
		String month_date=this.getArguments("month-date"); //要統計的月份 格式為yyyyMM01  
		Date d0,d1,d2;
		if(month_date== null || month_date.trim().length()!=8)
			return this.getServiceContext().doNextProcess();
		try{			
			Calendar cal = Calendar.getInstance();
			cal.setTime(xUtility.parseDate(month_date));
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND,0);
			cal.set(Calendar.DAY_OF_MONTH, 1);
			d1=cal.getTime();              //此月份1日
			cal.add(Calendar.MONTH, 1);
			d2=cal.getTime();				//下個月1日
			cal.add(Calendar.MONTH, -2);
			d0=cal.getTime();				//上個月1日
		}catch(Exception e){
			
			return this.getServiceContext().doNextProcess();
		}
		long tot=0;
    	DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
    	try{
    		
	    	Query q = new Query(xMallDaily.class.getSimpleName());
	    	/*
	    	q.addFilter("date", FilterOperator.GREATER_THAN_OR_EQUAL, d1);
	    	q.addFilter("date", FilterOperator.LESS_THAN, d2);
	    	q.addFilter("mallFid", FilterOperator.EQUAL, mall_fid);
	    	*/
        	q.setFilter(
        			CompositeFilterOperator.and(
        				new Query.FilterPredicate("date", FilterOperator.GREATER_THAN_OR_EQUAL, d1),
        				new Query.FilterPredicate("date", FilterOperator.LESS_THAN, d2),
        				new Query.FilterPredicate("mallFid", FilterOperator.EQUAL, mall_fid)
        			)
        	);

	    	
	    	PreparedQuery pq = ds.prepare(q);
	    	//計算該月份成交的件數
	    	for(Entity found : pq.asIterable()) {
	        	xMallDaily day=new xMallDaily(found);
	        	long p=day.getProcessed();
				if(p==1){
					tot+=day.getGoodsQty();
				}
	    	}
    	}catch(Exception e){
    		System.out.println(e.getMessage());
    	}
    	
    	String d1_str=new SimpleDateFormat("yyyyMMdd").format(d1);
    	String d0_str=new SimpleDateFormat("yyyyMMdd").format(d0);
    	Entity entity =null;
    	Entity last_entity =null;
    	boolean update=false;//預設為資料新增
    	long last_balance=0;//上個月的累計
    	try{
			entity = ds.get(xServiceFee.generateKey(mall_fid, d1_str, "0"));
			update=true;
		}catch(EntityNotFoundException e){
			entity=new Entity(xServiceFee.generateKey(mall_fid,d1_str, "0"));
		}
		
		try{
			last_entity = ds.get(xServiceFee.generateKey(mall_fid, d0_str, "0"));//上個月的統計資料
			
			try{
				Entity entity4=ds.get(xServiceFee.generateKey(mall_fid, d0_str, "1"));//是否有出帳單
				xServiceFee sf4=new xServiceFee(entity4);
				last_balance=sf4.getBalance();
				}catch(EntityNotFoundException e){
					last_balance=new xServiceFee(last_entity).getBalance();
				}
		}catch(EntityNotFoundException e){
			last_balance=0;
		}
    	long balance=last_balance+tot;
    	long old_balance=0;
    	ArrayList<Entity> put_array = new ArrayList<Entity>();
    	xServiceFee sf=new xServiceFee(entity);
    	Date date=new Date();
    	sf.setLast(date);
    	sf.setFid(mall_fid);
    	sf.setType("0");
    	sf.setCreateDate(d1);
    	sf.setAmount(tot);
    	sf.setQTY(tot);
    	if(update)
    		old_balance=sf.getBalance();//如果為UPDATE則先記下原累計值（只供後續比對用）
    	else
    		if(balance>=300)           //大於等於300就會出帳單
        		sf.setDoneFlag("1");
        	else	
        		sf.setDoneFlag("0");
    	sf.setBalance(balance);
    	put_array.add(sf.entity);
    	//除非是新增狀態，否則一律不出帳單(怕造成衝突,UPDATE時只改正累正確計值,出帳交由下個月系統再出)
    	if(balance>=300 && update==false){
    		Calendar cal = Calendar.getInstance();
    		cal.setTime(date);
    		cal.setTimeInMillis(cal.getTimeInMillis()+1000);
    		Date date2=cal.getTime();
    		Entity entity2=new Entity(xServiceFee.generateKey(mall_fid,d1_str, "1"));
    		String link=KeyFactory.keyToString(entity2.getKey());
    		xServiceFee sf2=new xServiceFee(entity2);
        	sf2.setLast(date2);
        	sf2.setFid(mall_fid);
        	sf2.setType("1");
        	sf2.setCreateDate(d1);
        	sf2.setAmount(balance);
        	sf2.setQTY(balance);
        	sf2.setBalance(0);
        	sf2.setDoneFlag("0");
        	put_array.add(sf2.entity);
        	
        	xSetBillMethod bill=new xSetBillMethod();
        	put_array.add(bill.getBillEntity(mall_fid,link, String.valueOf(balance)));	
    		
    	}
    	//如果該月是UPDATE,則判斷曾否出帳,若有改正出帳單的累計值
    	if(update){
    		try{
    			Entity entity3 = ds.get(xServiceFee.generateKey(mall_fid, d1_str, "1"));
    			xServiceFee sf3=new xServiceFee(entity3);
    			sf3.setBalance(balance-sf3.getQTY());
    			put_array.add(sf3.entity);
    		}catch(EntityNotFoundException e){    			
    			
    		}
    	}
    	ds.put(put_array);
    	
    	//如果UPDATE後新舊值不一,會影響下個月累計的正確性,則一直遞迴呼叫下個月更新,直到系統年月止
    	if(update && balance!=old_balance){
    		long offset = -480;
    		Calendar cal2 = Calendar.getInstance();    		
			cal2.setTime(new Date());			
			cal2.setTimeInMillis(cal2.getTimeInMillis()-offset*60*1000);
			cal2.set(Calendar.HOUR_OF_DAY, 0);
			cal2.set(Calendar.MINUTE, 0);
			cal2.set(Calendar.SECOND, 0);
			cal2.set(Calendar.MILLISECOND,0);
			cal2.set(Calendar.DAY_OF_MONTH, 1);
			Date d=cal2.getTime();//系統年月
    		if(d.after(d2))
	    		try{
	    			Queue queue = QueueFactory.getDefaultQueue();
	    	    	
	    			    TaskOptions to = TaskOptions.Builder.withUrl("/web/rst/finance");
	    				to.param("method", "process-fee");
	    				to.param("mall-fid",mall_fid);
	    				to.param("month-date",new SimpleDateFormat("yyyyMMdd").format(d2) );
	    				queue.add(to);	
	    			
	    		}catch(Exception e){
	    			xLogger.log(Level.WARNING,"err msg:"+e.getMessage());
	    		}
	    	if(mall_fid.equals("100000023455774"));
	    			xLogger.log(Level.WARNING,"d="+d.toString()+"d2="+d2.toString());
    	}
    	
		return this.getServiceContext().doNextProcess();
		
	}
}
