package xlive.method.rst.statistic;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

import xlive.method.*;

import com.google.appengine.api.datastore.AsyncDatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;

public class xCompleteGoodsWeekMethod extends xDefaultMethod{
	private int batchSize = 100;
	public Object process()throws xMethodException{
		boolean valid=true;
		String why = "";
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND,0);
		String date_string = new SimpleDateFormat("yyyyMMdd").format(cal.getTime());
		cal.add(Calendar.DAY_OF_MONTH, -1);
		Date date = cal.getTime();
		cal.add(Calendar.DAY_OF_MONTH, -6);
		Date date_week = cal.getTime();

		Vector<weekObject> weeks = new Vector<weekObject>();
		ArrayList<Entity> put_array = new ArrayList<Entity>();
		ArrayList<Key> del_array = new ArrayList<Key>();
		
		AsyncDatastoreService ads = DatastoreServiceFactory.getAsyncDatastoreService();
		Query q_week = new Query(xGoodsWeek.class.getSimpleName());
		/*
		q_week.addFilter("date", FilterOperator.LESS_THAN, date);
		*/
		q_week.setFilter(new Query.FilterPredicate("date", FilterOperator.LESS_THAN, date));
		
		Iterable<Entity> q_result_week = ads.prepare(q_week).asIterable(FetchOptions.Builder.withLimit(batchSize));
		int count = 0;
		for(Entity q_entity :q_result_week) {
			String bomkey = (String)q_entity.getProperty("bomKey");
			if(bomkey!=null && bomkey.trim().length()>0){
				Query q_day = new Query(xGoodsDay.class.getSimpleName());
				/*
				q_day.addFilter("bomKey", FilterOperator.EQUAL, bomkey);
				q_day.addFilter("date", FilterOperator.GREATER_THAN_OR_EQUAL, date_week);
				*/
	        	q_day.setFilter(
	        			CompositeFilterOperator.and(
	        				new Query.FilterPredicate("bomKey", FilterOperator.EQUAL, bomkey),
	        				new Query.FilterPredicate("date", FilterOperator.GREATER_THAN_OR_EQUAL, date_week)
	        			)
	        	);

				Iterable<Entity> q_result_day = ads.prepare(q_day).asIterable();
				weekObject week = new weekObject();
				week.entity=q_entity;
				week.result=q_result_day;
				weeks.add(week);
				++count;
			}
		}
		for(int i = 0; i < weeks.size();++i){
			weekObject week = weeks.get(i);
			Long sum = 0l;
			for(Entity q_entity :week.result) sum += new xGoodsDay(q_entity).getQty();
			if(sum == 0) del_array.add(week.entity.getKey());
			else{
				xGoodsWeek xweek = new xGoodsWeek(week.entity);
				xweek.setDate(date_string);
				xweek.setQty(sum);
				put_array.add(week.entity);
			}
		}
		if(del_array.size() > 0) ads.delete(del_array);
		if(put_array.size() > 0) ads.put(put_array);
		
		if(count >= batchSize){
			Queue queue = QueueFactory.getDefaultQueue();
			TaskOptions to = TaskOptions.Builder.withUrl("/web/rst/statistic");
			to.param("method", "complete-goods-week");
			queue.add(to);
		}else{
			Queue queue = QueueFactory.getDefaultQueue();
			TaskOptions to = TaskOptions.Builder.withUrl("/web/rst/statistic");
			to.param("method", "calc-goods-week-rank");
			queue.add(to);
		}
		this.setReturnArguments("date", new SimpleDateFormat("yyyyMMdd").format(date));
		this.setReturnArguments("dateWeek", new SimpleDateFormat("yyyyMMdd").format(date_week));
		this.setReturnArguments("count", String.valueOf(count));
		this.setReturnArguments("valid", String.valueOf(valid));
		this.setReturnArguments("why", why);
		return this.getServiceContext().doNextProcess();
	}
	public class weekObject {
		Entity entity;
		Iterable<Entity> result;
	}
	
}
