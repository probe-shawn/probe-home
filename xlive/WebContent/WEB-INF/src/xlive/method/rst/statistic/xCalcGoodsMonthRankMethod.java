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

public class xCalcGoodsMonthRankMethod extends xDefaultMethod{
	public Object process()throws xMethodException{
		boolean valid=true;
		String why = "";
		Element head=(Element)this.createElement(xGetMethod.goodsMonthRank);
		Element goods_element;
		
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		Query q = new Query("xGoodsMonth");
		q.addSort("qty", SortDirection.DESCENDING);
		PreparedQuery pq = ds.prepare(q);
		int count = 0;
	   	for(Entity found : pq.asIterable(FetchOptions.Builder.withLimit(xGetMethod.rankSize))) {
	   		++count;
	   		xGoodsMonth month = new xGoodsMonth(found);
	   		head.appendChild(goods_element=this.createElement("goods"));
	   		goods_element.appendChild(this.createElement("mall-fid")).setTextContent(month.getMallFid());
	   		goods_element.appendChild(this.createElement("bom-key")).setTextContent(month.getBomKey());
	   		goods_element.appendChild(this.createElement("icon")).setTextContent(month.getIcon());
	   		goods_element.appendChild(this.createElement("name")).setTextContent(month.getName());
	   		goods_element.appendChild(this.createElement("qty")).setTextContent(String.valueOf(month.getQty()));
	   	}
	   	byte[] bytexml = xXmlDocument.nodeToBytes(head,null);
	   	xMemCache.iMallService().put(xGetMethod.goodsMonthRank, new Blob(bytexml));
		//
	   	this.setReturnArguments("count", String.valueOf(count));
		this.setReturnArguments("valid", String.valueOf(valid));
		this.setReturnArguments("why", why);
		return this.getServiceContext().doNextProcess();
	}
}
