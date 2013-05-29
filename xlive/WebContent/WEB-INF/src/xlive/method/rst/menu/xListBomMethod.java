package xlive.method.rst.menu;

import java.text.SimpleDateFormat;
import xlive.method.*;
import org.w3c.dom.*;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.SortDirection;

public class xListBomMethod extends xDefaultMethod{
	SimpleDateFormat sdf=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	public Object process()throws xMethodException{
		String key = this.getArguments("key");
		Element data = this.setReturnArguments("data", "");
		
    	DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
    	Query q = new Query("xBom");
    	//q.addFilter("parentKey", FilterOperator.EQUAL, key);
    	q.setFilter(new Query.FilterPredicate("parentKey", FilterOperator.EQUAL, key));
    	q.addSort("type", SortDirection.ASCENDING);
    	q.addSort("date",SortDirection.ASCENDING);
    	PreparedQuery pq = ds.prepare(q);
    	for(Entity found : pq.asIterable()) {
    		data.appendChild(xBom.bomXml(new xBom(found), 0));
    	}
    	boolean eof = true;
        data.setAttribute("eof", String.valueOf(eof));
		this.setReturnArguments("valid", "true");
		return this.getServiceContext().doNextProcess();
	}
	
}
