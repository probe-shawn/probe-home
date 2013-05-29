package xlive.method.rst.circle;

import xlive.method.*;
import xlive.method.rst.mall.xMallDetail;
import org.w3c.dom.*;

import com.google.appengine.api.datastore.AsyncDatastoreService;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.SortDirection;
import java.util.LinkedList;
import java.util.concurrent.Future;

public class xListCircleMallMethod extends xDefaultMethod{
	public Object process()throws xMethodException{
		boolean valid=true;
		String why = "";
		String circle_fid = this.getArguments("circle-fid");
		String mall_fid = this.getArguments("mall-fid");
		Element malls_element = this.setReturnArguments("malls", "");
    	DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
	    Query q = new Query(xCircleMall.class.getSimpleName());
	    if(circle_fid != null && circle_fid.trim().length() > 0)
	    	q.setFilter(new Query.FilterPredicate("circleFid", FilterOperator.EQUAL, circle_fid));
	    if(mall_fid != null && mall_fid.trim().length() > 0)
	    	q.setFilter(new Query.FilterPredicate("mallFid", FilterOperator.EQUAL, mall_fid));
	    q.addSort("date", SortDirection.DESCENDING);	    
	    
	    AsyncDatastoreService ads = DatastoreServiceFactory.getAsyncDatastoreService();
    	LinkedList<Future<Entity>> malls=new LinkedList<Future<Entity>>();
	    PreparedQuery pq = ds.prepare(q);
    	int count = 0;
    	org.w3c.dom.Document doc = malls_element.getOwnerDocument();
		for(Entity entity : pq.asIterable()){
			xCircleMall circle_mall = new xCircleMall(entity);
	        Element cmall =(Element) malls_element.appendChild(this.createElement("circle-mall"));
	        xCircleMall.xmlCircleMall(circle_mall, cmall);
	        Future<Entity> future_mall = null;
			try{
				future_mall=ads.get(xMallDetail.generateKey(circle_mall.getMallFid()));
				malls.add(future_mall);    				
			}catch(Exception e){}
	        try {
	        	if(mall_fid != null && mall_fid.trim().length() > 0){
					Entity circle_entity = ds.get(xCircle.generateKey(circle_mall.getCircleFid()));
					xCircle circle = new xCircle(circle_entity);
					cmall.appendChild(doc.createElement("circle-name")).setTextContent(circle.getName());
					cmall.appendChild(doc.createElement("circle-icon")).setTextContent(circle.getIcon());
				}
	        	/* 暫時用不到
	        	if(circle_fid != null && circle_fid.trim().length() > 0){
					Entity mall_entity=ds.get(xMallDetail.generateKey(circle_mall.getMallFid()));
					xMallDetail mall=new xMallDetail(mall_entity);				
					cmall.appendChild(doc.createElement("mall-name")).setTextContent(mall.getName());
					cmall.appendChild(doc.createElement("mall-storename")).setTextContent(mall.getStoreName());		
	        	}*/
			} catch (EntityNotFoundException e) {
			}
	        
	        ++count;
    	}
		
		for(int i=0;i<malls.size();i++){
			Element mall_element=(Element)malls_element.appendChild(this.createElement("mall-detail"));
			try{
			xMallDetail.xmlMallDetail(new xMallDetail(malls.get(i).get()), mall_element);
			
			}catch(Exception e){}
		}
		
		
		malls_element.setAttribute("count", String.valueOf(count));
		this.setReturnArguments("valid", String.valueOf(valid));
		this.setReturnArguments("why", why);
		return this.getServiceContext().doNextProcess();
	}
}
