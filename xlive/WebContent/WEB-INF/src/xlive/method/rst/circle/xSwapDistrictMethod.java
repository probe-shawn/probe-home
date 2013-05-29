package xlive.method.rst.circle;


import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.KeyFactory;

import xlive.method.*;

public class xSwapDistrictMethod extends xDefaultMethod{
	public Object process()throws xMethodException{
		boolean valid=true;
		String why = "";
		String key1=this.getArguments("key1");
		String key2=this.getArguments("key2");
		Entity entity1 =null;
		Entity entity2 =null;
		xDistrict district1=null;
		xDistrict district2=null;
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		if(entity1 == null && key1 != null && key1.trim().length()>0){
			try{
				entity1 = ds.get(KeyFactory.stringToKey(key1));
			}catch(EntityNotFoundException e){entity1 = null;}
		}
		if(entity2 == null && key2 != null && key2.trim().length()>0){
			try{
				entity2 = ds.get(KeyFactory.stringToKey(key2));
			}catch(EntityNotFoundException e){entity2 = null;}
		}
		
		if(entity1 == null || entity2 == null ){
			valid=false;
			why="Entity not found!";
		}else{
			try{
				district1 = new xDistrict(entity1);
				district2 = new xDistrict(entity2);
				long tmp=district1.getSort();			
				district1.setSort(district2.getSort());
				district2.setSort(tmp);
				ds.put(district1.entity);
				ds.put(district2.entity);
			}catch(Exception e){
				valid=false;
				why="process error";
			}
		}
			
		this.setReturnArguments("valid", String.valueOf(valid));
		this.setReturnArguments("why", why);
		//
		return this.getServiceContext().doNextProcess();
	}
}
