package xlive.method.rst.statistic;



import org.w3c.dom.Element;

import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;

import xlive.google.xMemCache;
import xlive.method.*;
import xlive.xml.xXmlDocument;

public class xCalcGoodsWeekRankMethod extends xDefaultMethod{
	public Object process()throws xMethodException{
		boolean valid=true;
		String why = "";
		Element head=(Element)this.createElement(xGetMethod.goodsWeekRank);
		Element goods_element;
		
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		Query q = new Query("xGoodsWeek");
		q.addSort("qty", SortDirection.DESCENDING);
		PreparedQuery pq = ds.prepare(q);
		int count = 0;
	   	for(Entity found : pq.asIterable(FetchOptions.Builder.withLimit(xGetMethod.rankSize))) {
	   		++count;
	   		xGoodsWeek week = new xGoodsWeek(found);
	   		head.appendChild(goods_element=this.createElement("goods"));
	   		goods_element.appendChild(this.createElement("mall-fid")).setTextContent(week.getMallFid());
	   		goods_element.appendChild(this.createElement("bom-key")).setTextContent(week.getBomKey());
	   		goods_element.appendChild(this.createElement("icon")).setTextContent(week.getIcon());
	   		goods_element.appendChild(this.createElement("name")).setTextContent(week.getName());
	   		goods_element.appendChild(this.createElement("qty")).setTextContent(String.valueOf(week.getQty()));
	   	}
	   	byte[] bytexml = xXmlDocument.nodeToBytes(head,null);
	   	xMemCache.iMallService().put(xGetMethod.goodsWeekRank, new Blob(bytexml));
		//
	   	this.setReturnArguments("count", String.valueOf(count));
		this.setReturnArguments("valid", String.valueOf(valid));
		this.setReturnArguments("why", why);
		return this.getServiceContext().doNextProcess();
	}
}
