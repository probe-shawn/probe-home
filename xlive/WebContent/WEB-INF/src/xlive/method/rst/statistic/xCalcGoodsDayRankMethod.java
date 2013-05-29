package xlive.method.rst.statistic;


import java.util.Calendar;
import java.util.Date;

import org.w3c.dom.Element;

import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.SortDirection;

import xlive.xUtility;
import xlive.google.xMemCache;
import xlive.method.*;
import xlive.xml.xXmlDocument;

public class xCalcGoodsDayRankMethod extends xDefaultMethod{
	public Object process()throws xMethodException{
		boolean valid=true;
		String why = "";
		//
		String date_string=this.getArguments("date");
		/*
		String tz_offset=this.getArguments("tz-offset");
		long offset = -480;
		try{
			if(tz_offset != null && tz_offset.trim().length() > 0)offset=Long.valueOf(tz_offset);
		}catch(Exception e){}
		*/
		Element head=(Element)this.createElement(xGetMethod.goodsDayRank);
		Element goods_element;
		Calendar cal = Calendar.getInstance();
		if(date_string != null && date_string.trim().length() >0){
			cal.setTime(xUtility.parseDate(date_string));
		}else{
			cal.add(Calendar.DAY_OF_MONTH, -1);
		}
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND,0);
		Date date = cal.getTime();
		
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		Query q = new Query("xGoodsDay");
		/*
		q.addFilter("date", FilterOperator.EQUAL, date);
		*/
		q.setFilter(new Query.FilterPredicate("date", FilterOperator.EQUAL, date));
		
		q.addSort("qty", SortDirection.DESCENDING);
		PreparedQuery pq = ds.prepare(q);
		int count = 0;
	   	for(Entity found : pq.asIterable(FetchOptions.Builder.withLimit(xGetMethod.rankSize))) {
	   		++count;
	   		xGoodsDay day = new xGoodsDay(found);
	   		head.appendChild(goods_element=this.createElement("goods"));
	   		goods_element.appendChild(this.createElement("mall-fid")).setTextContent(day.getMallFid());
	   		goods_element.appendChild(this.createElement("bom-key")).setTextContent(day.getBomKey());
	   		goods_element.appendChild(this.createElement("icon")).setTextContent(day.getIcon());
	   		goods_element.appendChild(this.createElement("name")).setTextContent(day.getName());
	   		goods_element.appendChild(this.createElement("qty")).setTextContent(String.valueOf(day.getQty()));
	   	}
	   	byte[] bytexml = xXmlDocument.nodeToBytes(head,null);
	   	xMemCache.iMallService().put(xGetMethod.goodsDayRank, new Blob(bytexml));
		//
	   	this.setReturnArguments("count", String.valueOf(count));
	   	this.setReturnArguments("date", xUtility.formatDate(date));
		this.setReturnArguments("valid", String.valueOf(valid));
		this.setReturnArguments("why", why);
		return this.getServiceContext().doNextProcess();
	}
}
