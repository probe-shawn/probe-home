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

public class xCalcBonusCustomerRankMethod extends xDefaultMethod{
	public Object process()throws xMethodException{
		boolean valid=true;
		String why = "";
		Element head=(Element)this.createElement(xGetMethod.bonusCustomerRank);
		Element cust_element;
		
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		Query q = new Query("xCustomerBonus");
		q.addSort("m2cBonus", SortDirection.DESCENDING);
		q.addSort("lastDate", SortDirection.DESCENDING);
		PreparedQuery pq = ds.prepare(q);
		int count = 0;
	   	for(Entity found : pq.asIterable(FetchOptions.Builder.withLimit(xGetMethod.rankSize))) {
	   		++count;
	   		xCustomerBonus cust = new xCustomerBonus(found);
	   		String fid = cust.getFid();
	   		if(fid != null && fid.trim().length() > 0){
	   			head.appendChild(cust_element=this.createElement("customer"));
	   			cust_element.appendChild(this.createElement("fid")).setTextContent(fid);
	   			cust_element.appendChild(this.createElement("name")).setTextContent(cust.getName());
	   			cust_element.appendChild(this.createElement("m2cBonus")).setTextContent(String.valueOf(cust.getM2CBonus()));
	   		}
	   	}
	   	byte[] bytexml = xXmlDocument.nodeToBytes(head,null);
	   	xMemCache.iMallService().put(xGetMethod.bonusCustomerRank, new Blob(bytexml));
		//
	   	this.setReturnArguments("count", String.valueOf(count));
		this.setReturnArguments("valid", String.valueOf(valid));
		this.setReturnArguments("why", why);
		return this.getServiceContext().doNextProcess();
	}
}
