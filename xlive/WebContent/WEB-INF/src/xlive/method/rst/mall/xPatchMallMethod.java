package xlive.method.rst.mall;

import xlive.method.*;
import xlive.method.rst.customer.xVisit;
import xlive.method.rst.menu.xBom;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;


public class xPatchMallMethod extends xDefaultMethod{
	public Object process()throws xMethodException{
		boolean valid=true;
		String why = "";
		//this.xMallPatchRemoveUnused();
		//this.xMallDetailPatchSolomo();
		//this.xMallPatchBarterIOU();
		//this.xRemoveAllVisit();
		//this.xUpdateBomFid("100002980761272", "340578176010461");
		this.setReturnArguments("valid", String.valueOf(valid));
		this.setReturnArguments("why", why);
		return this.getServiceContext().doNextProcess();
	}
	public void xMallPatchRemoveUnused() throws xMethodException{
    	DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		Query q = new Query(xMall.class.getSimpleName());
		PreparedQuery pq = ds.prepare(q);
		int count = 0;		
	    for(Entity found : (Iterable<Entity>)pq.asIterable()) {
	    	/*
	    	if(found.hasProperty("fid")) found.removeProperty("fid");
	    	if(found.hasProperty("type")) found.removeProperty("type");
	    	if(found.hasProperty("id")) found.removeProperty("id");
	    	if(found.hasProperty("name")) found.removeProperty("name");
	    	if(found.hasProperty("storeName")) found.removeProperty("storeName");
	    	if(found.hasProperty("storeType")) found.removeProperty("storeType");
	    	if(found.hasProperty("desc")) found.removeProperty("desc");
	    	if(found.hasProperty("descHtml")) found.removeProperty("descHtml");
	    	if(found.hasProperty("date")) found.removeProperty("date");
	    	if(found.hasProperty("phone")) found.removeProperty("phone");
	    	if(found.hasProperty("addr")) found.removeProperty("addr");
	    	if(found.hasProperty("openTime")) found.removeProperty("openTime");
	    	if(found.hasProperty("mail")) found.removeProperty("mail");
	    	if(found.hasProperty("note")) found.removeProperty("note");
	    	if(found.hasProperty("status")) found.removeProperty("status");
	    	if(found.hasProperty("last")) found.removeProperty("last");
	    	if(found.hasProperty("bomKey")) found.removeProperty("bomKey");
	    	if(found.hasProperty("latitude")) found.removeProperty("latitude");
	    	if(found.hasProperty("longitude")) found.removeProperty("longitude");
	    	if(found.hasProperty("likes")) found.removeProperty("likes");
	    	if(found.hasProperty("bonusPercent")) found.removeProperty("bonusPercent");
	    	if(found.hasProperty("services")) found.removeProperty("services");
	    	*/
	    	ds.put(found);
	        ++count;
	    }		
	    this.setReturnArguments("count", String.valueOf(count));
	}
	public void xMallPatchBarterIOU() throws xMethodException{
    	DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		Query q = new Query(xMallDetail.class.getSimpleName());
		PreparedQuery pq = ds.prepare(q);
		int count = 0;	
	    for(Entity found : (Iterable<Entity>)pq.asIterable()) {
	    	xMallDetail mall = new xMallDetail(found);
	    	if("account".equals(mall.getMallType())){
	    	    xSetMallMethod.createIOUBarter(mall.getFid(),mall.getName());
	    	    xSetMallMethod.createIOUBarter(mall.getFid(),mall.getName());
	    	    xSetMallMethod.createIOUBarter(mall.getFid(),mall.getName());
	    	    xSetMallMethod.createIOUBarter(mall.getFid(),mall.getName());
	    	}
	    	/*
	    	if(found.hasProperty("fid")) found.removeProperty("fid");
	    	if(found.hasProperty("type")) found.removeProperty("type");
	    	if(found.hasProperty("id")) found.removeProperty("id");
	    	if(found.hasProperty("name")) found.removeProperty("name");
	    	if(found.hasProperty("storeName")) found.removeProperty("storeName");
	    	if(found.hasProperty("storeType")) found.removeProperty("storeType");
	    	if(found.hasProperty("desc")) found.removeProperty("desc");
	    	if(found.hasProperty("descHtml")) found.removeProperty("descHtml");
	    	if(found.hasProperty("date")) found.removeProperty("date");
	    	if(found.hasProperty("phone")) found.removeProperty("phone");
	    	if(found.hasProperty("addr")) found.removeProperty("addr");
	    	if(found.hasProperty("openTime")) found.removeProperty("openTime");
	    	if(found.hasProperty("mail")) found.removeProperty("mail");
	    	if(found.hasProperty("note")) found.removeProperty("note");
	    	if(found.hasProperty("status")) found.removeProperty("status");
	    	if(found.hasProperty("last")) found.removeProperty("last");
	    	if(found.hasProperty("bomKey")) found.removeProperty("bomKey");
	    	if(found.hasProperty("latitude")) found.removeProperty("latitude");
	    	if(found.hasProperty("longitude")) found.removeProperty("longitude");
	    	if(found.hasProperty("likes")) found.removeProperty("likes");
	    	if(found.hasProperty("bonusPercent")) found.removeProperty("bonusPercent");
	    	if(found.hasProperty("services")) found.removeProperty("services");
	    	*/
	    	ds.put(found);
	        ++count;
	    }		
	    this.setReturnArguments("count", String.valueOf(count));
	}

	public void xMallDetailPatchSolomo() throws xMethodException{
    	DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
    	try {
			Entity found = ds.get(xMallDetail.generateKey("261184823923305"));
			if(found != null){
				xMallDetail mall = new xMallDetail(found);
				mall.setMallType("page");
				ds.put(mall.entity);
			}
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
		}
	}
	public void xRemoveAllVisit() throws xMethodException{
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		PreparedQuery pq = ds.prepare(new Query(xVisit.class.getSimpleName()));
	    for(Entity found : (Iterable<Entity>)pq.asIterable()) {
	    	ds.delete(found.getKey());
	    }
	}
	public void xUpdateBomFid(String fid, String tofid) throws xMethodException{
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		Query q = new Query(xBom.class.getSimpleName());
		/*
		q.addFilter("mallFid", FilterOperator.EQUAL, fid);
		*/
		q.setFilter(new Query.FilterPredicate("mallFid", FilterOperator.EQUAL, fid));
		PreparedQuery pq = ds.prepare(q);
	    for(Entity found : (Iterable<Entity>)pq.asIterable()) {
	    	found.setProperty("mallFid", tofid);
	    	ds.put(found);
	    }
	}
	
}
