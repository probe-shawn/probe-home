package xlive.method.rst.circle;import xlive.method.*;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.FilterOperator;

public class xDelDistrictMallMethod extends xDefaultMethod{
	public Object process()throws xMethodException{
		boolean valid=true;
		String why = "";
    	DatastoreService ds = DatastoreServiceFactory.getDatastoreService(); 
    	String fid=(String)this.getArguments("circle-fid");//給FID就查整個生活圈
    	String dkey=(String)this.getArguments("district-key");//給專區KEY就只查專區
    	String mall_fid=(String)this.getArguments("mall-fid");//
    	int count = 0;	
    	if(valid){   		
    		Query q = new Query(xDistrictMall.class.getSimpleName());
    		if(mall_fid != null && mall_fid.length() > 0){
    			if(dkey != null && dkey.length() > 0){
    				q.setFilter(CompositeFilterOperator.and(
		    				new Query.FilterPredicate("districtKey", FilterOperator.EQUAL, dkey),
		    				new Query.FilterPredicate("mallFid", FilterOperator.EQUAL, mall_fid)
					));
    			}
    			else{
    				q.setFilter(CompositeFilterOperator.and(
    						new Query.FilterPredicate("circleFid", FilterOperator.EQUAL,fid),
		    				new Query.FilterPredicate("mallFid", FilterOperator.EQUAL, mall_fid)
					));
    			}	
    		}	
    		else{    		
	    		if(dkey != null && dkey.length() > 0)
		        	q.setFilter(new Query.FilterPredicate("districtKey", FilterOperator.EQUAL, dkey));
	    		else
	    			q.setFilter(new Query.FilterPredicate("circleFid", FilterOperator.EQUAL,fid));
    		}
	    
	    	
    		PreparedQuery pq = ds.prepare(q);
    		for(Entity found : (Iterable<Entity>)pq.asIterable()) {   			
    			ds.delete(found.getKey());
    			++count;
		    }
    		
    		
    	}
    	this.setReturnArguments("count", String.valueOf(count));
		this.setReturnArguments("valid", String.valueOf(valid));
		this.setReturnArguments("why", why);
		return this.getServiceContext().doNextProcess();
	}
	
}

