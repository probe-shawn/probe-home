package xlive.method.rst.customer;


import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.xml.xpath.XPathConstants;

import xlive.xUtility;
import xlive.google.xMemCache;
import xlive.method.*;
import xlive.method.rst.circle.xCircle;
import xlive.method.rst.mall.xMallDetail;
import xlive.method.rst.statistic.xGetMethod;

import org.w3c.dom.*;

import com.google.appengine.api.datastore.AsyncDatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.SortDirection;


public class xGetCustomerMethod extends xDefaultMethod{
	
	
	public Object process()throws xMethodException{
		boolean valid=true;
		String why = "";
		String fid=this.getArguments("fid");
		String history=this.getArguments("history");
		Element customer_element = this.setReturnArguments("customer", "");
		boolean mall_owner=false;
		//
		AsyncDatastoreService ads = DatastoreServiceFactory.getAsyncDatastoreService();
		//
		Future<Entity> future_customer =null;
		future_customer = ads.get(xCustomer.generateKey(fid));
		//
		/*
		Query q1 = new Query(xBonus.class.getSimpleName());
    	q1.setFilter(
    			CompositeFilterOperator.and(
    				new Query.FilterPredicate("fromId", FilterOperator.EQUAL, fid),
    				new Query.FilterPredicate("fromType", FilterOperator.EQUAL, xCustomer.bonusType)
    			)
    	);
		Iterable<Entity> q1_result = ads.prepare(q1).asIterable();
		Query q2 = new Query(xBonus.class.getSimpleName());
    	q2.setFilter(
    			CompositeFilterOperator.and(
    				new Query.FilterPredicate("toId", FilterOperator.EQUAL, fid),
    				new Query.FilterPredicate("toType", FilterOperator.EQUAL, xCustomer.bonusType)
    			)
    	);
		Iterable<Entity> q2_result = ads.prepare(q2).asIterable();
		*/
		//
		Query q3 = new Query(xVisit.class.getSimpleName());
		q3.setFilter(new Query.FilterPredicate("fid", FilterOperator.EQUAL, fid));
		q3.addSort("date", SortDirection.DESCENDING);
		Iterable<Entity> q3_result = ads.prepare(q3).asIterable(FetchOptions.Builder.withLimit(1));
		//
		Element pages_return = this.setReturnArguments("pages", "");
		Query q4 = new Query(xMallDetail.class.getSimpleName());
		Element pages = (Element)this.getArguments("pages", XPathConstants.NODE);
		NodeList ps=pages.getElementsByTagName("page");
		ArrayList<String> list= new ArrayList<String>();
		list.add(fid);
		Element page_return = this.createElement("page");
		pages_return.appendChild(page_return);
		page_return.setAttribute("name", this.getArguments("name"));
		page_return.setAttribute("id", fid);
		page_return.setAttribute("imall", "false");
		page_return.setAttribute("page", "false");
		for(int i = 0; i <ps.getLength();++i){
			Element p = (Element)ps.item(i);
			list.add(p.getAttribute("id"));
			page_return = this.createElement("page");
			pages_return.appendChild(page_return);
			page_return.setAttribute("name", p.getAttribute("name"));
			page_return.setAttribute("id", p.getAttribute("id"));
			page_return.setAttribute("imall", "false");
			page_return.setAttribute("page", "true");
			page_return.setAttribute("access_token",p.getAttribute("access_token"));
		}
		q4.setFilter(new Query.FilterPredicate("fid", FilterOperator.IN, list));
		Iterable<Entity> q4_result = ads.prepare(q4).asIterable(FetchOptions.Builder.withDefaults());
		//
		Query q5 = new Query(xCircle.class.getSimpleName());
		q5.setFilter(new Query.FilterPredicate("fid", FilterOperator.IN, list));
		Iterable<Entity> q5_result = ads.prepare(q5).asIterable(FetchOptions.Builder.withDefaults());
		//
		Entity cust_entity = null;
		try {
			cust_entity = future_customer.get();
		} catch (InterruptedException e) {
		} catch (ExecutionException e) {
		} 
		//
		boolean new_customer=false;
		if(cust_entity == null){
			cust_entity = new Entity(xCustomer.generateKey(fid));
			cust_entity.setProperty("createDate", new Date());
			xCustomer cust = new xCustomer(cust_entity);
			cust.setId(this.getArguments("id")); 
			cust.setName(this.getArguments("name"));
			cust.setGid(this.getArguments("gid"));
			cust.setFid(this.getArguments("fid"));
			cust.setCredit(this.getArguments("credit"));
			ads.put(cust.entity);
			xMemCache.iMallService().increment(xGetMethod.customerCount, Long.valueOf(1));
			xCustomer.xmlCustomerPxmlForGetCustomer(cust, customer_element,0,0,0,xUtility.formatDate(new Date()));
			new_customer =true;
		}else{
			long q1_sum = 0;long q2_sum = 0;
			String last_date_string = xUtility.formatDate(new Date());
			/*
			for(Entity q1_entity :q1_result)q1_sum += ((Long)q1_entity.getProperty("bonus"));
			for(Entity q2_entity :q2_result)q2_sum += ((Long)q2_entity.getProperty("bonus"));
			*/
			for(Entity q3_entity :q3_result){
				Date ldate = (Date)q3_entity.getProperty("date");
				if(ldate != null)last_date_string =xUtility.formatDate(ldate);
			}
			xCustomer cust = new xCustomer(cust_entity);
			if(fid=="100003606547934"){
				xCustomer.xmlCustomerPxmlForGetCustomer(cust, customer_element,0,0,0,xUtility.formatDate(new Date()));
			}else{
				xCustomer.xmlCustomerPxmlForGetCustomer(cust, customer_element, q2_sum-q1_sum, q2_sum,q1_sum,last_date_string);
				if("true".equals(history)) xCustomer.xmlCustomerOrdersPxmlForGetCustomer(cust, customer_element);
			}
		}
		//
		int count=0;
		for(Entity q4_entity :q4_result){
			String mfid = (String)q4_entity.getProperty("fid");
			Element owner = (Element)this.evaluate("child::page[@id=\'"+mfid+"\']", pages_return, XPathConstants.NODE);
			if(owner != null)owner.setAttribute("imall", "true");
			mall_owner =true;
			++count;
		}
		boolean circle_owner=false;
		for(Entity q5_entity :q5_result){
			String mfid = (String)q5_entity.getProperty("fid");
			Element owner = (Element)this.evaluate("child::page[@id=\'"+mfid+"\']", pages_return, XPathConstants.NODE);
			if(owner != null)owner.setAttribute("circle", "true");
			circle_owner =true;
		}
		pages_return.setAttribute("count", String.valueOf(count));
        this.setReturnArguments("mall-owner", String.valueOf(mall_owner));
        this.setReturnArguments("circle-owner", String.valueOf(circle_owner));
        this.setReturnArguments("new-customer", String.valueOf(new_customer));
		this.setReturnArguments("valid", String.valueOf(valid));
		this.setReturnArguments("why", why);
		return this.getServiceContext().doNextProcess();
	}
	
}
