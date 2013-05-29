package xlive.method.rst.report;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import xlive.xUtility;
import xlive.method.*;
import xlive.method.rst.mall.xMallDetail;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;

public class xTaskCalcMallDailyMethod extends xDefaultMethod{
	public Object process()throws xMethodException{
		boolean valid=true;
		String why = "";
		String date=this.getArguments("date");
		String tz_offset=this.getArguments("tz-offset");
		long offset = -480;
		try{
			if(tz_offset != null && tz_offset.trim().length() > 0)offset=Long.valueOf(tz_offset);
		}catch(Exception e){}
		Calendar cal = Calendar.getInstance();
		if(date != null && date.trim().length() >0){
			cal.setTime(xUtility.parseDate(date));
		}
		cal.setTimeInMillis(cal.getTimeInMillis()-offset*60*1000-86400000);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND,0);
		Date cal_date=cal.getTime();
		String daily_date_string = new SimpleDateFormat("yyyy/MM/dd").format(cal_date);
		Queue queue = QueueFactory.getDefaultQueue();
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
    	Query q = new Query(xMallDetail.class.getSimpleName());
    	PreparedQuery pq = ds.prepare(q);
    	int count = 0;
    	for(Entity found : pq.asIterable()) {
    		xMallDetail mall = new xMallDetail(found);
    		TaskOptions to = TaskOptions.Builder.withUrl("/web/rst/report");
    		to.param("method", "calc-mall-daily");
    		to.param("mall-fid", mall.getFid());
    		to.param("date",daily_date_string);
    		queue.add(to);
    		++count;
    	}
    	this.setReturnArguments("count", String.valueOf(count));
    	this.setReturnArguments("date", daily_date_string);
		this.setReturnArguments("valid", String.valueOf(valid));
		this.setReturnArguments("why", why);
		return this.getServiceContext().doNextProcess();
	}
}
