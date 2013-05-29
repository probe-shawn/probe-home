package xlive.method.rst.broker;

import java.util.Date;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;


import xlive.method.*;

public class xSetBrokerMethod extends xDefaultMethod{
	public Object process()throws xMethodException{
		boolean valid=true;
		String why = "";
		Entity entity =null;
		String keystr=this.getArguments("broker.key");
		String store_name=this.getArguments("broker.store-name");
		String store_phone=this.getArguments("broker.store-phone");
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		if(keystr != null && keystr.trim().length()>0){ 
			try{
				entity = ds.get(KeyFactory.stringToKey(keystr));
			}catch(EntityNotFoundException e){entity =null;}
		}
		if(entity == null){
			Query q1 = new Query(xBroker.class.getSimpleName());
			/*
			q1.addFilter("storeName", FilterOperator.EQUAL, store_name);
			*/
			q1.setFilter(new Query.FilterPredicate("storeName", FilterOperator.EQUAL, store_name));
			q1.setKeysOnly();
			int q1_count = ds.prepare(q1).countEntities(FetchOptions.Builder.withDefaults());
			
			Query q2 = new Query(xBroker.class.getSimpleName());
			/*
			q2.addFilter("storePhone", FilterOperator.EQUAL, store_phone);
			*/
			q2.setFilter(new Query.FilterPredicate("storePhone", FilterOperator.EQUAL, store_phone));
			
			q2.setKeysOnly();
			int q2_count = ds.prepare(q2).countEntities(FetchOptions.Builder.withDefaults());
			
			if(q1_count > 0){
				why="店名 : "+store_name +" 已被推薦.";
				valid = false;
			}
			if(valid && q2_count > 0){
				why="電話 : "+store_phone +" 已被推薦.";
				valid = false;
			}
		}
		if(valid){
			if(entity == null){
				entity = new Entity(xBroker.class.getSimpleName());
				entity.setProperty("date", new Date());
			}
			xBroker broker = new xBroker(entity);
			broker.setFid(this.getArguments("broker.fid")); 
			broker.setName(this.getArguments("broker.name"));
			broker.setStoreName(this.getArguments("broker.store-name"));
			broker.setStorePhone(this.getArguments("broker.store-phone"));
			broker.setStoreAddr(this.getArguments("broker.store-addr"));
			broker.setMallFid(this.getArguments("broker.mall-fid"));
			broker.setState(this.getArguments("broker.state"));
			broker.setBonus1(this.getArguments("broker.bonus1"));
			broker.setBonus2(this.getArguments("broker.bonus2"));
			broker.setVerifyDate1(this.getArguments("broker.verify-date1"));
			broker.setVerifyDate2(this.getArguments("broker.verify-date2"));
			broker.setOperator(this.getArguments("broker.operator"));
			broker.setNote(this.getArguments("broker.note"));
			ds.put(broker.entity);
		}
		
		this.setReturnArguments("valid", String.valueOf(valid));
		this.setReturnArguments("why", why);
		//
		return this.getServiceContext().doNextProcess();
	}
	
}
