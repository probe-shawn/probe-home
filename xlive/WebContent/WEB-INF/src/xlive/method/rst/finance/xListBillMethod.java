package xlive.method.rst.finance;

import java.util.ArrayList;
import java.util.List;
import xlive.method.*;
import org.w3c.dom.*;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.FetchOptions.Builder;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.FilterOperator;


public class xListBillMethod extends xDefaultMethod{
	public Object process()throws xMethodException{
		boolean valid=true;
		String why = "";
		Element bills_element = this.setReturnArguments("bills", "");
    	DatastoreService ds = DatastoreServiceFactory.getDatastoreService(); 
    	String link=(String)this.getArguments("link");
    	String fid=(String)this.getArguments("fid");
    	String type=(String)this.getArguments("type");
    	int count = 0;		
    	if(valid){   		
    		Query q = new Query(xBillDetail.class.getSimpleName());
    		if(link!=null && link.length()>0)
    			/*
    			q.addFilter("link", FilterOperator.EQUAL, link);//正常狀況下只有一筆
    			*/
    			q.setFilter(new Query.FilterPredicate("link", FilterOperator.EQUAL, link));
    		else{
    			/*
	    		q.addFilter("fid", FilterOperator.EQUAL, fid);
	    		q.addFilter("type", FilterOperator.EQUAL, type);
	    		*/
	        	q.setFilter(
	        			CompositeFilterOperator.and(
	        				new Query.FilterPredicate("fid", FilterOperator.EQUAL, fid),
	        				new Query.FilterPredicate("type", FilterOperator.EQUAL, type)
	        			)
	        	);
	    		q.addSort("bid", Query.SortDirection.DESCENDING);
    		}
    		PreparedQuery pq = ds.prepare(q);
    		for(Entity found : (Iterable<Entity>)pq.asIterable()) {
    			Element bill_element=(Element)bills_element.appendChild(this.createElement("bill"));
    			xBillDetail bill_detail = new xBillDetail(found);
		        bill_element.appendChild(this.createElement("bid")).setTextContent(bill_detail.getBid());
		        bill_element.appendChild(this.createElement("cdate")).setTextContent(bill_detail.getCreateDateString());
		        bill_element.appendChild(this.createElement("type")).setTextContent(bill_detail.getType());
		        bill_element.appendChild(this.createElement("qty")).setTextContent(String.valueOf(bill_detail.getQTY()));
		        bill_element.appendChild(this.createElement("amount")).setTextContent(String.valueOf(bill_detail.getAmount()));
		        bill_element.appendChild(this.createElement("remark")).setTextContent(bill_detail.getRemark());
		        bill_element.appendChild(this.createElement("atmcode")).setTextContent(bill_detail.getATMcode());
		        bill_element.appendChild(this.createElement("barcode1")).setTextContent(bill_detail.getBarcode1());
		        bill_element.appendChild(this.createElement("barcode2")).setTextContent(bill_detail.getBarcode2());
		        bill_element.appendChild(this.createElement("barcode3")).setTextContent(bill_detail.getBarcode3());
		        bill_element.appendChild(this.createElement("writeoff")).setTextContent(String.valueOf(bill_detail.getWriteOff()));
		    
		        ++count;
		    }
    	}
    	this.setReturnArguments("count", String.valueOf(count));
		this.setReturnArguments("valid", String.valueOf(valid));
		this.setReturnArguments("why", why);
		return this.getServiceContext().doNextProcess();
	}
	
}
