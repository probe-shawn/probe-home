package xlive.method.rst.customer;

import java.util.Date;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.KeyFactory;

import xlive.google.xMemCache;
import xlive.method.*;
import xlive.method.rst.statistic.xGetMethod;

public class xSetCustomerMethod extends xDefaultMethod{
	public Object process()throws xMethodException{
		boolean valid=true;
		String why = "";
		
		Entity entity =null;
		String keystr=this.getArguments("customer.key");
		String fid=this.getArguments("customer.fid");
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		
		if(keystr != null && keystr.trim().length()>0){
			try{
				entity = ds.get(KeyFactory.stringToKey(keystr));
			}catch(EntityNotFoundException e){entity =null;}
		}
		if(entity == null && fid != null && fid.trim().length()>0){
			try{
				entity = ds.get(xCustomer.generateKey(fid));
			}catch(EntityNotFoundException e){entity = null;}
		}
		if(entity == null){
			entity = new Entity(xCustomer.generateKey(fid));
			entity.setProperty("createDate", new Date());
		}
		xCustomer cust = new xCustomer(entity);
		cust.setId(this.getArguments("customer.id")); 
		cust.setName(this.getArguments("customer.name"));
		cust.setGid(this.getArguments("customer.gid"));
		cust.setFid(this.getArguments("customer.fid"));
		cust.setCredit(this.getArguments("cusomer.credit"));
		
		ds.put(cust.entity);
		
		if(valid){
			this.setReturnArguments("cust.key", KeyFactory.keyToString(cust.entity.getKey()));
			xMemCache.iMallService().increment(xGetMethod.customerCount, Long.valueOf(1));
		}
		this.setReturnArguments("valid", String.valueOf(valid));
		this.setReturnArguments("why", why);
		//
		return this.getServiceContext().doNextProcess();
	}
	
}
