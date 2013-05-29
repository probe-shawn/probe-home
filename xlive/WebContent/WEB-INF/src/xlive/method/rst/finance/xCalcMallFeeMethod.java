package xlive.method.rst.finance;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import xlive.xUtility;
import xlive.method.*;
import xlive.method.logger.xLogger;
import xlive.method.rst.mall.xMallDetail;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;


public class xCalcMallFeeMethod extends xDefaultMethod{
	public Object process()throws xMethodException{
		boolean valid=true;
		String why = "";
		long offset = -480;
		String date_string=this.getArguments("date");//例：若要統計9月，傳入20110901
		Calendar cal = Calendar.getInstance();
		if(date_string==null || date_string.length()<8){
			cal.setTime(new Date());			
			cal.setTimeInMillis(cal.getTimeInMillis()-offset*60*1000);
			cal.set(Calendar.DAY_OF_MONTH, 1);
			cal.add(Calendar.MONTH, -1);	//例：系統呼叫則傳入上個月1日
		}
		else
			cal.setTime(xUtility.parseDate(date_string));
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND,0);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		Date d1=cal.getTime();
		int c=0;
		
		try{
			Queue queue = QueueFactory.getDefaultQueue();
	    	DatastoreService ds = DatastoreServiceFactory.getDatastoreService();     	  		
			Query q = new Query(xMallDetail.class.getSimpleName());
			
			PreparedQuery pq = ds.prepare(q);
			for(Entity found : (Iterable<Entity>)pq.asIterable()) {
				xMallDetail mall=new xMallDetail(found);			
			    TaskOptions to = TaskOptions.Builder.withUrl("/web/rst/finance");
				to.param("method", "process-fee");
				to.param("mall-fid", mall.getFid() );
				to.param("month-date",new SimpleDateFormat("yyyyMMdd").format(d1) );
				queue.add(to);	
				c++;
		    }
			
		}catch(Exception e){
			valid=false;
			
		}
		this.setReturnArguments("valid", String.valueOf(valid));
		this.setReturnArguments("why", why);
		this.setReturnArguments("count", String.valueOf(c));
		return this.getServiceContext().doNextProcess();
	
	}
	
}
