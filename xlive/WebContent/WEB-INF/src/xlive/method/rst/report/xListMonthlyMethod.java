package xlive.method.rst.report;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import xlive.xUtility;
import xlive.method.*;
import org.w3c.dom.*;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.SortDirection;

public class xListMonthlyMethod extends xDefaultMethod{
	public Object process()throws xMethodException{
		boolean valid=true;
		String why = "";
		String mall_fid=this.getArguments("mall-fid");
		String date_string=this.getArguments("date");
		String show_type=this.getArguments("type");
		Calendar cal = Calendar.getInstance();
		cal.setTime(xUtility.parseDate(date_string));
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND,0);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		Date d1=cal.getTime();
		cal.add(Calendar.MONTH, 1);
		Date d2=cal.getTime();
		cal.add(Calendar.MONTH,-3);
		Date d3=cal.getTime();
		Element days = this.setReturnArguments("days", "");
    	DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
    	Query q = new Query(xMallDaily.class.getSimpleName());
    	/*
    	if(show_type.equals("1")){
        	q.addFilter("date", FilterOperator.LESS_THAN, d2);//抓三個月有資料的寫法
    	}else{    		
    		q.addFilter("date", FilterOperator.GREATER_THAN_OR_EQUAL, d3);//抓固定三個月的寫法
    		q.addFilter("date", FilterOperator.LESS_THAN, d2);
    	}
    	q.addFilter("mallFid", FilterOperator.EQUAL, mall_fid);
    	*/
    	ArrayList<Query.Filter> filters = new ArrayList<Query.Filter>();
    	if(show_type.equals("1")){
        	filters.add(new Query.FilterPredicate("date", FilterOperator.LESS_THAN, d2));
    	}else{    		
    		filters.add(new Query.FilterPredicate("date", FilterOperator.GREATER_THAN_OR_EQUAL, d3));
    		filters.add(new Query.FilterPredicate("date", FilterOperator.LESS_THAN, d2));
    	}
    	filters.add(new Query.FilterPredicate("mallFid", FilterOperator.EQUAL, mall_fid));
    	q.setFilter(new Query.CompositeFilter(CompositeFilterOperator.AND, filters));

    	
    	
    	q.addSort("date", SortDirection.DESCENDING);
    	PreparedQuery pq = ds.prepare(q);
    	long commit=0,cancel=0;
    	String mmbuffer="";
    	if(show_type.equals("1"))
    		this.setReturnArguments("more","none");    		
    	else{
    		cal.add(Calendar.MONTH,-1);
    		this.setReturnArguments("more",xUtility.formatDate(cal.getTime()).substring(0,7));
    	}	
    	for(Entity found : pq.asIterable()) {        	
        	xMallDaily day=new xMallDaily(found);
        	if(show_type.equals("1")){//抓三個月有資料的寫法
	        	String mm=day.getDateString().substring(0,7);
				if(mmbuffer.indexOf(mm)==-1)
					mmbuffer+=mm+",";	
				if(mmbuffer.length()>25){
					this.setReturnArguments("more", mm);
					break;
				}
        	}
			Element day_element=(Element)days.appendChild(this.createElement("day"));
        	xMallDaily.xmlMallDaily(day, day_element);
        	long p=day.getProcessed();
			if(p==1) ++commit;
			if(p==0) ++cancel;			
    	}
    	days.setAttribute("commit", String.valueOf(commit));
    	days.setAttribute("cancel", String.valueOf(cancel));
		this.setReturnArguments("valid", String.valueOf(valid));
		this.setReturnArguments("why", why);
		return this.getServiceContext().doNextProcess();
	}
}
