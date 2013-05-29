package xlive.method.rst.finance;

import java.util.ArrayList;
import java.util.List;
import xlive.method.*;
import org.w3c.dom.*;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.FetchOptions.Builder;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;


public class xListServiceFeeMethod extends xDefaultMethod{
	public Object process()throws xMethodException{
		boolean valid=true;
		String why = "";
		Element fees_element = this.setReturnArguments("fees", "");
    	DatastoreService ds = DatastoreServiceFactory.getDatastoreService(); 
    	
    	String fid=(String)this.getArguments("fid");
    	int count = 0;	
    	
    	if(valid){   		
    		Query q = new Query(xServiceFee.class.getSimpleName());
    		/*
    		q.addFilter("fid", FilterOperator.EQUAL, fid);
    		*/
    		q.setFilter(new Query.FilterPredicate("fid", FilterOperator.EQUAL, fid));
    		q.addSort("create_date", Query.SortDirection.DESCENDING);
    		q.addSort("type", Query.SortDirection.DESCENDING);
    		PreparedQuery pq = ds.prepare(q);
    		for(Entity found : (Iterable<Entity>)pq.asIterable()) {
    			Element fee_element=(Element)fees_element.appendChild(this.createElement("fee"));
    			xServiceFee sf = new xServiceFee(found);
		        xServiceFee.xmlServiceFee(sf,fee_element );
		        ++count;
		    }
    	}
    	this.setReturnArguments("count", String.valueOf(count));
		this.setReturnArguments("valid", String.valueOf(valid));
		this.setReturnArguments("why", why);
		return this.getServiceContext().doNextProcess();
	}
	
}
