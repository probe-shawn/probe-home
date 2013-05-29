package xlive.method.rst.statistic;


import java.util.Date;

import xlive.method.*;
import xlive.method.rst.bonus.xBonus;
import xlive.method.rst.customer.xCustomer;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.FilterOperator;

public class xLogCustomerBonusMethod extends xDefaultMethod{
	
	
	public Object process()throws xMethodException{
		boolean valid=true;
		String why = "";
		String fid=this.getArguments("fid");
		if(fid != null && fid.trim().length() > 0){
			DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
			//
			Query qc2m = new Query(xBonus.class.getSimpleName());
			/*
			qc2m.addFilter("fromId", FilterOperator.EQUAL, fid);
			qc2m.addFilter("fromType", FilterOperator.EQUAL, xCustomer.bonusType);
			*/
        	qc2m.setFilter(
        			CompositeFilterOperator.and(
        				new Query.FilterPredicate("fromId", FilterOperator.EQUAL, fid),
        				new Query.FilterPredicate("fromType", FilterOperator.EQUAL, xCustomer.bonusType)
        			)
        	);

			Iterable<Entity> qc2m_result = ds.prepare(qc2m).asIterable();
	
			Query qm2c = new Query(xBonus.class.getSimpleName());
			/*
			qm2c.addFilter("toId", FilterOperator.EQUAL, fid);
			qm2c.addFilter("toType", FilterOperator.EQUAL, xCustomer.bonusType);
			*/
        	qm2c.setFilter(
        			CompositeFilterOperator.and(
        				new Query.FilterPredicate("toId", FilterOperator.EQUAL, fid),
        				new Query.FilterPredicate("toType", FilterOperator.EQUAL, xCustomer.bonusType)
        			)
        	);
			
			Iterable<Entity> qm2c_result = ds.prepare(qm2c).asIterable();
			//
			Entity cust_entity = null;
			try {
				cust_entity = ds.get(xCustomer.generateKey(fid));
			} catch (EntityNotFoundException e) {
				e.printStackTrace();
			} catch(Exception e){
				e.printStackTrace();
			}
			//
			if(cust_entity != null){
				xCustomer customer = new xCustomer(cust_entity);
				long qc2m_sum = 0;
				for(Entity qc2m_entity :qc2m_result)qc2m_sum += ((Long)qc2m_entity.getProperty("bonus"));
				long qm2c_sum = 0;
				for(Entity qm2c_entity :qm2c_result)qm2c_sum += ((Long)qm2c_entity.getProperty("bonus"));
				Date current = new Date();
				Entity cust_bonus_entity =null;
				try {
					cust_bonus_entity = ds.get(KeyFactory.createKey(xCustomerBonus.class.getSimpleName(),fid));
				} catch (EntityNotFoundException e) {
					cust_bonus_entity = new Entity(KeyFactory.createKey(xCustomerBonus.class.getSimpleName(),fid));
					cust_bonus_entity.setProperty("createDate", current);
				}
				xCustomerBonus cust_bonus = new xCustomerBonus(cust_bonus_entity);
				cust_bonus.setFid(fid);
				cust_bonus.setName(customer.getName());
				cust_bonus.setC2MBonus(qc2m_sum);
				cust_bonus.setM2CBonus(qm2c_sum);
				cust_bonus.setLastDate(current);
				ds.put(cust_bonus_entity);
			}
		}else{
			valid = false;
			why = "fid is blank";
		}
		this.setReturnArguments("valid", String.valueOf(valid));
		this.setReturnArguments("why", why);
		return this.getServiceContext().doNextProcess();
	}
	
}
