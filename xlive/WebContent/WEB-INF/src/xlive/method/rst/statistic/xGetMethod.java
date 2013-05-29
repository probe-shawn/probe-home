package xlive.method.rst.statistic;

import org.w3c.dom.Element;

import com.google.appengine.api.datastore.AsyncDatastoreService;
import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;

import xlive.google.xMemCache;
import xlive.method.*;
import xlive.xml.xXmlDocument;

public class xGetMethod extends xDefaultMethod{
	public static int rankSize = 25;
	public static String mallCount="mall-count";
	public static String customerCount="customer-count";
	public static String orderCount="order-count";
	public static String bonusProductRank="bonus-product-rank";
	public static String bonusCustomerRank="bonus-customer-rank";
	public static String goodsDayRank="goods-day-rank";
	public static String goodsWeekRank="goods-week-rank";
	public static String goodsMonthRank="goods-month-rank";
	
	
	public Object process()throws xMethodException{
		boolean valid=true;
		String why = "";
		AsyncDatastoreService ads = DatastoreServiceFactory.getAsyncDatastoreService();
		
		Long order_count=(Long) xMemCache.iMallService().get(xGetMethod.orderCount);
		PreparedQuery orderpq=null;
		if(order_count == null){
			Query q = new Query("xOrder");
			q.setKeysOnly();
			orderpq = ads.prepare(q);
		}
		Long cust_count =(Long) xMemCache.iMallService().get(xGetMethod.customerCount);
		PreparedQuery custpq=null;
		if(cust_count == null){
			Query q = new Query("xCustomer");
			q.setKeysOnly();
			custpq = ads.prepare(q);
		}
		Long mall_count=(Long) xMemCache.iMallService().get(xGetMethod.mallCount);
		PreparedQuery mallpq=null;
		if(mall_count == null){
			Query q = new Query("xMall");
			q.setKeysOnly();
			mallpq = ads.prepare(q);
		}
		//
		if(orderpq != null){
			order_count = Long.valueOf(orderpq.countEntities(FetchOptions.Builder.withLimit(10000000)));
			xMemCache.iMallService().put(xGetMethod.orderCount, order_count);
		}
		if(custpq != null){
			cust_count = Long.valueOf(custpq.countEntities(FetchOptions.Builder.withLimit(10000000)));
			xMemCache.iMallService().put(xGetMethod.customerCount, cust_count);
		}
		if(mallpq != null){
			mall_count = Long.valueOf(mallpq.countEntities(FetchOptions.Builder.withLimit(10000000)));
			xMemCache.iMallService().put(xGetMethod.mallCount, mall_count);
		}
		
		Blob blob = (Blob)xMemCache.iMallService().get(xGetMethod.bonusProductRank);
		Element bonus_product_rank =this.setReturnArguments(xGetMethod.bonusProductRank, "");
		if(blob != null){
			byte[] bytes=blob.getBytes();
			Element rank =(Element) bonus_product_rank.getOwnerDocument().adoptNode(xXmlDocument.bytesToNode(bytes));
			bonus_product_rank.getParentNode().appendChild(rank);
			bonus_product_rank.getParentNode().removeChild(bonus_product_rank);
		}
		
		blob = (Blob)xMemCache.iMallService().get(xGetMethod.bonusCustomerRank);
		Element bonus_customer_rank =this.setReturnArguments(xGetMethod.bonusCustomerRank, "");
		if(blob != null){
			byte[] bytes=blob.getBytes();
			Element rank =(Element) bonus_customer_rank.getOwnerDocument().adoptNode(xXmlDocument.bytesToNode(bytes));
			bonus_customer_rank.getParentNode().appendChild(rank);
			bonus_customer_rank.getParentNode().removeChild(bonus_customer_rank);
		}
		
		blob = (Blob)xMemCache.iMallService().get(xGetMethod.goodsDayRank);
		Element goods_day_rank =this.setReturnArguments(xGetMethod.goodsDayRank, "");
		if(blob == null ||blob.getBytes().length ==0){
			this.processMethod("calc-goods-day-rank");
			blob = (Blob)xMemCache.iMallService().get(xGetMethod.goodsDayRank);
		}
		if(blob != null){
			byte[] bytes=blob.getBytes();
			Element rank =(Element) goods_day_rank.getOwnerDocument().adoptNode(xXmlDocument.bytesToNode(bytes));
			goods_day_rank.getParentNode().appendChild(rank);
			goods_day_rank.getParentNode().removeChild(goods_day_rank);
		}
		//
		blob = (Blob)xMemCache.iMallService().get(xGetMethod.goodsWeekRank);
		Element goods_week_rank =this.setReturnArguments(xGetMethod.goodsWeekRank, "");
		if(blob == null ||blob.getBytes().length ==0){
			this.processMethod("calc-goods-week-rank");
			blob = (Blob)xMemCache.iMallService().get(xGetMethod.goodsWeekRank);
		}
		if(blob != null){
			byte[] bytes=blob.getBytes();
			Element rank =(Element) goods_week_rank.getOwnerDocument().adoptNode(xXmlDocument.bytesToNode(bytes));
			goods_week_rank.getParentNode().appendChild(rank);
			goods_week_rank.getParentNode().removeChild(goods_week_rank);
		}
		blob = (Blob)xMemCache.iMallService().get(xGetMethod.goodsMonthRank);
		Element goods_month_rank =this.setReturnArguments(xGetMethod.goodsMonthRank, "");
		if(blob == null ||blob.getBytes().length ==0){
			this.processMethod("calc-goods-month-rank");
			blob = (Blob)xMemCache.iMallService().get(xGetMethod.goodsMonthRank);
		}
		if(blob != null){
			byte[] bytes=blob.getBytes();
			Element rank =(Element) goods_month_rank.getOwnerDocument().adoptNode(xXmlDocument.bytesToNode(bytes));
			goods_month_rank.getParentNode().appendChild(rank);
			goods_month_rank.getParentNode().removeChild(goods_month_rank);
		}
		
		this.setReturnArguments("mall-count", mall_count.toString());
		this.setReturnArguments("customer-count", cust_count.toString());
		this.setReturnArguments("order-count", order_count.toString());
		this.setReturnArguments("valid", String.valueOf(valid));
		this.setReturnArguments("why", why);
		return this.getServiceContext().doNextProcess();
	}
}
