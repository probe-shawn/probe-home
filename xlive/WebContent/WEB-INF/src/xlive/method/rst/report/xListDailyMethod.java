package xlive.method.rst.report;

import java.util.Calendar;
import java.util.Date;

import xlive.xUtility;
import xlive.method.*;
import xlive.method.rst.order.xOrder;

import org.w3c.dom.*;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.SortDirection;

public class xListDailyMethod extends xDefaultMethod{
	public Object process()throws xMethodException{
		boolean valid=true;
		String why = "";
		String mall_fid=this.getArguments("mall-fid");
		String tz_offset=this.getArguments("tz-offset");
		long offset=-480;
		try{
			offset=Long.valueOf(tz_offset);
		}catch(Exception e){}
		String date_string=this.getArguments("date");
		Calendar cal = Calendar.getInstance();
		cal.setTime(xUtility.parseDate(date_string));
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND,0);
		cal.setTimeInMillis(cal.getTimeInMillis()+offset*60*1000);
		Date d1=cal.getTime();
		cal.setTimeInMillis(cal.getTimeInMillis()+86400000);
		Date d2=cal.getTime();
		Element orders = this.setReturnArguments("orders", "");
    	DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
    	Query q = new Query("xOrder");
    	/*
    	//q.addFilter("processed", FilterOperator.EQUAL, Long.valueOf(0));
    	q.addFilter("last", FilterOperator.GREATER_THAN_OR_EQUAL, d1);
    	q.addFilter("last", FilterOperator.LESS_THAN, d2);
    	q.addFilter("mallFid", FilterOperator.EQUAL, mall_fid);
    	*/
    	q.setFilter(
    			CompositeFilterOperator.and(
    				new Query.FilterPredicate("last", FilterOperator.GREATER_THAN_OR_EQUAL, d1),
    				new Query.FilterPredicate("last", FilterOperator.LESS_THAN, d2),
    				new Query.FilterPredicate("mallFid", FilterOperator.EQUAL, mall_fid)
    			)
    	);
    	
    	q.addSort("last", SortDirection.ASCENDING);
    	PreparedQuery pq = ds.prepare(q);
    	long commit=0,cancel=0;
    	for(Entity found : pq.asIterable()) {
        	Element order_element=(Element)orders.appendChild(this.createElement("order"));
        	xOrder order=new xOrder(found);
        	xOrder.xmlOrder(order, order_element);
        	long p=order.getProcessed();
			if(p==1) ++commit;
			if(p==0) ++cancel;
    	}
    	orders.setAttribute("commit", String.valueOf(commit));
    	orders.setAttribute("cancel", String.valueOf(cancel));
		this.setReturnArguments("valid", String.valueOf(valid));
		this.setReturnArguments("why", why);
		return this.getServiceContext().doNextProcess();
	}
}
