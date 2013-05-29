package xlive.method.rst.report;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import xlive.xUtility;
import xlive.method.*;
import xlive.method.rst.mall.xMallDetail;
import xlive.method.rst.order.xOrder;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.SortDirection;

public class xCalcMallDailyMethod extends xDefaultMethod{
	public Object process()throws xMethodException{
		boolean valid=true;
		String why = "";
		String mall_fid=this.getArguments("mall-fid");
		xMallDetail mall=null;
		long offset=-480;
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		try {
			Entity entity=ds.get(xMallDetail.generateKey(mall_fid));
			mall= new xMallDetail(entity);
			offset = mall.getTzOffset();
		} catch (EntityNotFoundException e) {}
		String date_string=this.getArguments("date");
		Calendar cal = Calendar.getInstance();
		cal.setTime(xUtility.parseDate(date_string));
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND,0);
		Date daily_date = cal.getTime();
		String daily_date_string = new SimpleDateFormat("yyyyMMdd").format(daily_date);
		cal.setTimeInMillis(cal.getTimeInMillis()+offset*60*1000);
		Date d1=cal.getTime();
		cal.setTimeInMillis(cal.getTimeInMillis()+86400000);
		Date d2=cal.getTime();
    	Query q = new Query("xOrder");
    	/*
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
    	long c_qty=0,c_total=0,c_c2m=0,c_m2c=0;
    	long n_qty=0,n_total=0,n_c2m=0,n_m2c=0;
    	for(Entity found : pq.asIterable()) {
        	xOrder order=new xOrder(found);
        	if(order.getProcessed()==1){
        		c_qty += order.getGoods();
        		c_total += order.getTotal();
        		c_c2m += order.getBonusC2M();
        		c_m2c += order.getBonusM2C();
        	}
        	if(order.getProcessed()==-1){
        		n_qty += order.getGoods();
        		n_total += order.getTotal();
        		n_c2m += order.getBonusC2M();
        		n_m2c += order.getBonusM2C();
        	}
    	}
    	if(c_qty >0){
    		Entity entity = new Entity(xMallDaily.generateKey(mall_fid, daily_date_string, "1"));
    		xMallDaily md = new xMallDaily(entity);
    		md.setMallFid(mall_fid);
    		md.setName(mall.getName());
    		md.setDate(daily_date);
    		md.setBounsC2M(c_c2m);
    		md.setBounsM2C(c_m2c);
    		md.setGoodsQty(c_qty);
    		md.setTotal(c_total);
    		md.setProcessd(1);
    		md.setLast(new Date());
    		ds.put(entity);
    	}
    	if(n_qty >0){
    		Entity entity = new Entity(xMallDaily.generateKey(mall_fid, daily_date_string, "-1"));
    		xMallDaily md = new xMallDaily(entity);
    		md.setMallFid(mall_fid);
    		md.setName(mall.getName());
    		md.setDate(daily_date);
    		md.setBounsC2M(n_c2m);
    		md.setBounsM2C(n_m2c);
    		md.setGoodsQty(n_qty);
    		md.setTotal(n_total);
    		md.setProcessd(-1);
    		md.setLast(new Date());
    		ds.put(entity);
    	}
    	String commit="qty("+c_qty+"),"+"c2m("+c_c2m+"),"+"m2c("+c_m2c+"),total("+c_total+")";
    	String cancel="qty("+n_qty+"),"+"c2m("+n_c2m+"),"+"m2c("+n_m2c+"),total("+n_total+")";
    	this.setReturnArguments("commit", commit);
    	this.setReturnArguments("cancel", cancel);
		this.setReturnArguments("valid", String.valueOf(valid));
		this.setReturnArguments("why", why);
		return this.getServiceContext().doNextProcess();
	}
}
