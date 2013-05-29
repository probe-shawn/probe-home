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
		String month_date=this.getArguments("month-date"); //�n�έp����� �榡��yyyyMM01  
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
			d1=cal.getTime();              //�����1��
			cal.add(Calendar.MONTH, 1);
			d2=cal.getTime();				//�U�Ӥ�1��
			cal.add(Calendar.MONTH, -2);
			d0=cal.getTime();				//�W�Ӥ�1��
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
	    	//�p��Ӥ�����檺���
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
    	boolean update=false;//�w�]����Ʒs�W
    	long last_balance=0;//�W�Ӥ몺�֭p
    	try{
			entity = ds.get(xServiceFee.generateKey(mall_fid, d1_str, "0"));
			update=true;
		}catch(EntityNotFoundException e){
			entity=new Entity(xServiceFee.generateKey(mall_fid,d1_str, "0"));
		}
		
		try{
			last_entity = ds.get(xServiceFee.generateKey(mall_fid, d0_str, "0"));//�W�Ӥ몺�έp���
			
			try{
				Entity entity4=ds.get(xServiceFee.generateKey(mall_fid, d0_str, "1"));//�O�_���X�b��
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
    		old_balance=sf.getBalance();//�p�G��UPDATE�h���O�U��֭p�ȡ]�u�ѫ�����Ρ^
    	else
    		if(balance>=300)           //�j�󵥩�300�N�|�X�b��
        		sf.setDoneFlag("1");
        	else	
        		sf.setDoneFlag("0");
    	sf.setBalance(balance);
    	put_array.add(sf.entity);
    	//���D�O�s�W���A�A�_�h�@�ߤ��X�b��(�ȳy���Ĭ�,UPDATE�ɥu�勵�֥��T�p��,�X�b��ѤU�Ӥ�t�ΦA�X)
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
    	//�p�G�Ӥ�OUPDATE,�h�P�_���_�X�b,�Y���勵�X�b�檺�֭p��
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
    	
    	//�p�GUPDATE��s�­Ȥ��@,�|�v�T�U�Ӥ�֭p�����T��,�h�@�����j�I�s�U�Ӥ��s,����t�Φ~���
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
			Date d=cal2.getTime();//�t�Φ~��
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
