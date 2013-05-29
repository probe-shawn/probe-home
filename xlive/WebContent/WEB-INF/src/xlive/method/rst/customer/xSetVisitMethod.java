package xlive.method.rst.customer;

import java.util.Date;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.memcache.Expiration;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

import xlive.method.*;
import xlive.method.rst.mall.xMallDetail;

public class xSetVisitMethod extends xDefaultMethod{
	public Object process()throws xMethodException{
		boolean valid=true;
		String why = "";
		Entity entity =null;
		String fid=this.getArguments("fid");
		String mall_fid = this.getArguments("mall-fid");
		String id=fid+'-'+mall_fid;
		MemcacheService ms=MemcacheServiceFactory.getMemcacheService();
		if(ms.get(id) != null){
			this.setReturnArguments("valid", String.valueOf(valid));
			this.setReturnArguments("why", why);
			return this.getServiceContext().doNextProcess();
		}
		ms.put(id, "true", Expiration.byDeltaSeconds(900));
		//
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		Key key = xVisit.generateKey(fid, mall_fid);
		try {
			entity = ds.get(key);
		} catch (EntityNotFoundException e) {
			entity = new Entity(key);
		}
		xVisit visit = new xVisit(entity);
		visit.setFid(fid);
		visit.setMallFid(mall_fid);
		visit.setName(this.getArguments("name"));
		visit.setMallName(this.getArguments("mall-name"));
		visit.setCount(visit.getCount()+1);
		//Date last=visit.getDate();
		//if(last != null) visit.addDate(new Date());
		visit.setDate(new Date());
		ds.put(visit.entity);
		//
		key=xMallDetail.generateKey(mall_fid);
		try {
			entity = ds.get(key);
			xMallDetail mall=new xMallDetail(entity);
			mall.setVisit(mall.getVisit()+1);
			ds.put(entity);
		} catch (EntityNotFoundException e) {
		}
		
		this.setReturnArguments("valid", String.valueOf(valid));
		this.setReturnArguments("why", why);
		//
		return this.getServiceContext().doNextProcess();
	}
	
}
