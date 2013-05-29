package xlive.method.rst.mall;

import xlive.method.*;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;


public class xCopyMallDetailMethod extends xDefaultMethod{
	public Object process()throws xMethodException{
		boolean valid=true;
		String why = "";
    	DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		Query q = new Query("xMall");
		PreparedQuery pq = ds.prepare(q);
		int count = 0;		
	    for(Entity found : (Iterable<Entity>)pq.asIterable()) {
	    	xMall mall = new xMall(found);
	    	/*
	    	Entity detail = new Entity(xMallDetail.generateKey(mall.getFid()));
	    	detail.setPropertiesFrom(mall.entity);
	    	double lon = mall.getLongitude();
	    	double lat = mall.getLatitude();
	    	if(lon != 0 && lat != 0){
	    		com.beoui.geocell.model.Point point = new com.beoui.geocell.model.Point();
	    		point.setLat(lat);
	    		point.setLon(lon);
	    		List<String> geocell = com.beoui.geocell.GeocellManager.generateGeoCell(point);
	    		detail.setProperty("geoCell", geocell);
	    	}
	    	detail.removeProperty("bonus");
	    	detail.removeProperty("bonusRefund");
	    	ds.put(detail);
	    	*/
	        ++count;
	    }		
		this.setReturnArguments("count", String.valueOf(count));
		this.setReturnArguments("valid", String.valueOf(valid));
		this.setReturnArguments("why", why);
		return this.getServiceContext().doNextProcess();
	}
	
}
