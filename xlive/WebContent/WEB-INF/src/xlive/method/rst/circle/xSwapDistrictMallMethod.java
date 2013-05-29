package xlive.method.rst.circle;


import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.KeyFactory;
import xlive.method.*;

public class xSwapDistrictMallMethod extends xDefaultMethod{
	public Object process()throws xMethodException{
		boolean valid=true;
		String why = "";
		String key1=this.getArguments("key1");
		String key2=this.getArguments("key2");
		Entity entity1 =null;
		Entity entity2 =null;
		xDistrictMall dmall1=null;
		xDistrictMall dmall2=null;
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
				dmall1 = new xDistrictMall(entity1);
				dmall2 = new xDistrictMall(entity2);
				long tmp=dmall1.getSort();
				dmall1.setSort(dmall2.getSort());
				dmall2.setSort(tmp);
				ds.put(dmall1.entity);
				ds.put(dmall2.entity);
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
