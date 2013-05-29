package xlive.method.rst.circle;

import java.util.concurrent.Future;

import xlive.method.*;
import xlive.method.rst.mall.xMallDetail;
import xlive.method.sms.shortTextMessage;

import org.w3c.dom.*;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.AsyncDatastoreService;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.KeyFactory;
import java.util.LinkedList;

public class xListDistrictMallMethod extends xDefaultMethod{
	public Object process()throws xMethodException{
		boolean valid=true;
		String why = "";
		Element dmalls_element = this.setReturnArguments("dmalls", "");
    	DatastoreService ds = DatastoreServiceFactory.getDatastoreService(); 
    	String fid=(String)this.getArguments("circle-fid");//給FID就查整個生活圈
    	String dkey=(String)this.getArguments("district-key");//給專區KEY就只查專區
    	String mall_fid=(String)this.getArguments("mall-fid");//給mall-fid就查是否在專區內（或生活圈內）
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
	    	q.addSort("sort", Query.SortDirection.DESCENDING);
    		
	    	
	    	AsyncDatastoreService ads = DatastoreServiceFactory.getAsyncDatastoreService();
	    	LinkedList<Future<Entity>> malls=new LinkedList<Future<Entity>>();
    		PreparedQuery pq = ds.prepare(q);
    		for(Entity found : (Iterable<Entity>)pq.asIterable()) {   			
    			Future<Entity> future_mall = null;
    			try{
    				future_mall=ads.get(xMallDetail.generateKey(new xDistrictMall(found).getMallFid()));
    				malls.add(future_mall);    				
    			}catch(Exception e){}
    			Element dmall_element=(Element)dmalls_element.appendChild(this.createElement("dmall"));
    			xDistrictMall.xmlDistrictMall(new xDistrictMall(found), dmall_element);
		        ++count;
		    }
    		
    		for(int i=0;i<malls.size();i++){
    			Element mall_element=(Element)dmalls_element.appendChild(this.createElement("mall-detail"));
    			try{
    			xMallDetail.xmlMallDetail(new xMallDetail(malls.get(i).get()), mall_element);
    			
    			}catch(Exception e){}
    		}
    	}
    	this.setReturnArguments("count", String.valueOf(count));
		this.setReturnArguments("valid", String.valueOf(valid));
		this.setReturnArguments("why", why);
		return this.getServiceContext().doNextProcess();
	}
	
}
