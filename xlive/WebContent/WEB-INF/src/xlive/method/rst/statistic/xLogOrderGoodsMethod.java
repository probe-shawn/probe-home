package xlive.method.rst.statistic;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;

import xlive.method.*;
import xlive.method.rst.order.xGoods;
import xlive.method.rst.order.xOrder;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.FilterOperator;

public class xLogOrderGoodsMethod extends xDefaultMethod{
	SimpleDateFormat sdf =  new SimpleDateFormat("yyyyMMdd");
	public Object process()throws xMethodException{
		boolean valid=true;
		String why = "";
		String mall_fid=this.getArguments("mall-fid");
		String order_id=this.getArguments("order-id");
		ArrayList<Entity> put_array = new ArrayList<Entity>();
		Hashtable<String,String> htable = new Hashtable<String,String>();
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		Query q = new Query(xGoods.class.getSimpleName(),xOrder.generateKey(mall_fid, order_id));
		Iterable<Entity> q_result = ds.prepare(q).asIterable();
		int count = 0;
		for(Entity q_entity :q_result){
			++count;
			xGoods goods = new xGoods(q_entity);
			String bomkey=goods.getBomKey();
			if(bomkey != null && bomkey.trim().length() > 0 && !htable.containsKey(goods.getBomKey())){
				this.logGoods(goods, mall_fid,put_array);
				htable.put(bomkey, "");
			}
		}
		if(put_array.size() > 0)ds.put(put_array);
		this.setReturnArguments("count", String.valueOf(count));
		this.setReturnArguments("valid", String.valueOf(valid));
		this.setReturnArguments("why", why);
		return this.getServiceContext().doNextProcess();
	}
	public void logGoods(xGoods goods, String mall_fid, ArrayList<Entity> put_array){
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		String bomkey =  goods.getBomKey();
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(goods.getDate().getTime());
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND,0);
		Date date_day = cal.getTime();
		String date_string = sdf.format(date_day);
		cal.add(Calendar.DAY_OF_MONTH, 1);
		Date date2 =cal.getTime();
		cal.add(Calendar.DAY_OF_MONTH, -7);
		Date date_week = cal.getTime();
		cal.add(Calendar.DAY_OF_MONTH, -24);
		Date date_month =cal.getTime();
		//System.out.println("day :"+sdf.format(date_day)+"  week:"+sdf.format(date_week)+"  month:"+sdf.format(date_month));
		// count day
		Query q_day = new Query(xGoods.class.getSimpleName());
		/*
		q_day.addFilter("bomKey", FilterOperator.EQUAL, bomkey);
		q_day.addFilter("date", FilterOperator.GREATER_THAN_OR_EQUAL, date_day);
		q_day.addFilter("date", FilterOperator.LESS_THAN, date2);
		*/
    	q_day.setFilter(
    			CompositeFilterOperator.and(
    				new Query.FilterPredicate("bomKey", FilterOperator.EQUAL, bomkey),
    				new Query.FilterPredicate("date", FilterOperator.GREATER_THAN_OR_EQUAL, date_day),
    				new Query.FilterPredicate("date", FilterOperator.LESS_THAN, date2)
    			)
    	);

		
		Iterable<Entity> q_result_day = ds.prepare(q_day).asIterable();
		//
		Query q_week = new Query(xGoods.class.getSimpleName());
		/*
		q_week.addFilter("bomKey", FilterOperator.EQUAL, bomkey);
		q_week.addFilter("date", FilterOperator.GREATER_THAN_OR_EQUAL, date_week);
		q_week.addFilter("date", FilterOperator.LESS_THAN, date2);
		*/
    	q_week.setFilter(
    			CompositeFilterOperator.and(
    				new Query.FilterPredicate("bomKey", FilterOperator.EQUAL, bomkey),
    				new Query.FilterPredicate("date", FilterOperator.GREATER_THAN_OR_EQUAL, date_week),
    				new Query.FilterPredicate("date", FilterOperator.LESS_THAN, date2)
    			)
    	);
		
		
		Iterable<Entity> q_result_week = ds.prepare(q_week).asIterable();
		//
		Query q_month = new Query(xGoods.class.getSimpleName());
		/*
		q_month.addFilter("bomKey", FilterOperator.EQUAL, bomkey);
		q_month.addFilter("date", FilterOperator.GREATER_THAN_OR_EQUAL, date_month);
		q_month.addFilter("date", FilterOperator.LESS_THAN, date2);
		*/
    	q_month.setFilter(
    			CompositeFilterOperator.and(
    				new Query.FilterPredicate("bomKey", FilterOperator.EQUAL, bomkey),
    				new Query.FilterPredicate("date", FilterOperator.GREATER_THAN_OR_EQUAL, date_month),
    				new Query.FilterPredicate("date", FilterOperator.LESS_THAN, date2)
    			)
    	);
	
		Iterable<Entity> q_result_month = ds.prepare(q_month).asIterable();
		//
		
		Long sum_day = 0l;
		for(Entity q_entity :q_result_day) {sum_day += new xGoods(q_entity).getQty();}
		Entity entity = new Entity(KeyFactory.createKey(xGoodsDay.class.getSimpleName(),date_string+bomkey));
		xGoodsDay goods_day = new xGoodsDay(entity);
		goods_day.setMallFid(mall_fid);
		goods_day.setBomKey(bomkey);
		goods_day.setName(goods.getName());
		goods_day.setIcon(goods.getIcon());
		goods_day.setQty(sum_day);
		goods_day.setDate(date_day);
		put_array.add(entity); 
		//
		Long sum_week = 0l;
		for(Entity q_entity :q_result_week){sum_week += new xGoods(q_entity).getQty();}
		entity = new Entity(KeyFactory.createKey(xGoodsWeek.class.getSimpleName(),bomkey));
		xGoodsWeek goods_week = new xGoodsWeek(entity);
		goods_week.setMallFid(mall_fid);
		goods_week.setBomKey(bomkey);
		goods_week.setName(goods.getName());
		goods_week.setIcon(goods.getIcon());
		goods_week.setQty(sum_week);
		goods_week.setDate(date_day);
		put_array.add(entity);
		//

		Long sum_month = 0l;
		for(Entity q_entity :q_result_month){sum_month += new xGoods(q_entity).getQty();}
		entity = new Entity(KeyFactory.createKey(xGoodsMonth.class.getSimpleName(),bomkey));
		xGoodsMonth goods_month = new xGoodsMonth(entity);
		goods_month.setMallFid(mall_fid);
		goods_month.setBomKey(bomkey);
		goods_month.setName(goods.getName());
		goods_month.setIcon(goods.getIcon());
		goods_month.setQty(sum_month);
		goods_month.setDate(date_day);
		put_array.add(entity);
		//
		//System.out.println("day :"+sum_day+"  week:"+sum_week+"  month:"+sum_month);
		//System.out.println("day :"+count_day+"  week:"+count_week+"  month:"+count_month);
		
	}
	
}
