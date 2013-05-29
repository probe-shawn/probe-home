package xlive.method.rst.statistic;


import org.w3c.dom.Element;

import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.SortDirection;

import xlive.google.xMemCache;
import xlive.method.*;
import xlive.method.rst.menu.xBom;
import xlive.xml.xXmlDocument;

public class xCalcBonusProductRankMethod extends xDefaultMethod{
	public Object process()throws xMethodException{
		boolean valid=true;
		String why = "";
		Element head=(Element)this.createElement(xGetMethod.bonusProductRank);
		Element bom_element;
		
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		Query q = new Query("xBom");
		/*
		q.addFilter("type", FilterOperator.EQUAL, Long.valueOf(xBom.bomProduct));
		*/
		q.setFilter(new Query.FilterPredicate("type", FilterOperator.EQUAL, Long.valueOf(xBom.bomProduct)));
		
		q.addSort("bonus", SortDirection.DESCENDING);
		q.addSort("date", SortDirection.DESCENDING);
		PreparedQuery pq = ds.prepare(q);
		int count = 0;
	   	for(Entity found : pq.asIterable(FetchOptions.Builder.withLimit(xGetMethod.rankSize))) {
	   		++count;
	   		xBom bom = new xBom(found);
	   		String mallfid = bom.getMallFid();
	   		if(mallfid != null && mallfid.trim().length() > 0){
		   		head.appendChild(bom_element=this.createElement("bom"));
		   		bom_element.appendChild(this.createElement("mall-fid")).setTextContent(bom.getMallFid());
		   		bom_element.appendChild(this.createElement("key")).setTextContent(KeyFactory.keyToString(bom.entity.getKey()));
		   		bom_element.appendChild(this.createElement("name")).setTextContent(bom.getName());
		   		bom_element.appendChild(this.createElement("bonus")).setTextContent(String.valueOf(bom.getBonus()));
	   		}
	   	}
	   	byte[] bytexml = xXmlDocument.nodeToBytes(head,null);
	   	xMemCache.iMallService().put(xGetMethod.bonusProductRank, new Blob(bytexml));
		//
	   	this.setReturnArguments("count", String.valueOf(count));
		this.setReturnArguments("valid", String.valueOf(valid));
		this.setReturnArguments("why", why);
		return this.getServiceContext().doNextProcess();
	}
}
