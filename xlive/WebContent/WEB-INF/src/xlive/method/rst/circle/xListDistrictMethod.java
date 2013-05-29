package xlive.method.rst.circle;

import xlive.method.*;
import org.w3c.dom.*;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.FilterOperator;


public class xListDistrictMethod extends xDefaultMethod{
	public Object process()throws xMethodException{
		boolean valid=true;
		String why = "";
		Element districts_element = this.setReturnArguments("districts", "");
    	DatastoreService ds = DatastoreServiceFactory.getDatastoreService(); 
    	
    	String circle_fid=(String)this.getArguments("circle-fid");
    	String mall_fid=(String)this.getArguments("mall-fid");//如果有給mallfid,則須多查詢此mall是否有在各個專區中
    	int count = 0;	
    	String districts="";
    	org.w3c.dom.Document doc=null; 
    	if(valid){   		
    		Query q = new Query(xDistrict.class.getSimpleName());    	
	        	q.setFilter(
	        			CompositeFilterOperator.and(
	        					new Query.FilterPredicate("circleFid", FilterOperator.EQUAL,circle_fid),
	        				new Query.FilterPredicate("status", FilterOperator.EQUAL, "0")
	        			)
	        	);	        	
	    		q.addSort("sort", Query.SortDirection.DESCENDING);
	    	if(mall_fid != null && mall_fid.length() > 0){	
		    	Query q2 = new Query(xDistrictMall.class.getSimpleName());
			    	q2.setFilter(CompositeFilterOperator.and(
							new Query.FilterPredicate("circleFid", FilterOperator.EQUAL,circle_fid),
		    				new Query.FilterPredicate("mallFid", FilterOperator.EQUAL, mall_fid)
					));
			    PreparedQuery pq2 = ds.prepare(q2);
			  
			    for(Entity found : (Iterable<Entity>)pq2.asIterable()) {
			    	districts+=","+new xDistrictMall(found).getDistrictKey();
			    }
			     doc = districts_element.getOwnerDocument();
	    	}
    		PreparedQuery pq = ds.prepare(q);
    		for(Entity found : (Iterable<Entity>)pq.asIterable()) {
    			Element district_element=(Element)districts_element.appendChild(this.createElement("district"));
    			 xDistrict d=new xDistrict(found);
    			 xDistrict.xmlDistrict(d, district_element);
    			if(mall_fid != null && mall_fid.length() > 0){
    				if(districts.indexOf(d.getKey())>0)
    					district_element.appendChild(doc.createElement("checked")).setTextContent("1");
    				else
    					district_element.appendChild(doc.createElement("checked")).setTextContent("0");
    			}
    			 		       
		        ++count;
		    }
    	}
    	this.setReturnArguments("count", String.valueOf(count));
		this.setReturnArguments("valid", String.valueOf(valid));
		this.setReturnArguments("why", why);
		return this.getServiceContext().doNextProcess();
	}
	
}
