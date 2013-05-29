package xlive.method.rst.menu;

import java.util.Calendar;
import java.util.Date;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.KeyFactory;
import xlive.method.*;

public class xSwapDateMethod extends xDefaultMethod{
	public Object process()throws xMethodException{
		boolean valid=true;
		String why = "";
		String key1=this.getArguments("key1");
		String key2=this.getArguments("key2");
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		Entity entity1 = null;
		if(key1 != null && key1.trim().length()>0){
			try{
				entity1 = ds.get(KeyFactory.stringToKey(key1));
			}catch(EntityNotFoundException e){entity1 = null;}
		}
		Entity entity2 = null;
		if(key2 != null && key2.trim().length()>0){
			try{
				entity2 = ds.get(KeyFactory.stringToKey(key2));
			}catch(EntityNotFoundException e){entity2 = null;}
		}
		if(entity1 != null && entity2 != null){
			Date date1 = (Date)entity1.getProperty("date");
			Date date2 = (Date)entity2.getProperty("date");
			if(date1 == null && date2 != null){
				Calendar cal = Calendar.getInstance();
				cal.setTimeInMillis((date2.getTime()-100));
				date1 =cal.getTime(); 
			}
			if(date1 != null && date2 == null){
				Calendar cal = Calendar.getInstance();
				cal.setTimeInMillis((date1.getTime()+100));
				date2 =cal.getTime(); 
			}
			if(date1 == null && date2 == null){
				Calendar cal = Calendar.getInstance();
				date1 =cal.getTime();
				cal.setTimeInMillis((date1.getTime()+100));
				date2 =cal.getTime(); 
			}
			if(date1 != null && date2 != null ){
				entity1.setProperty("date", date2);
				entity2.setProperty("date", date1);
				ds.put(entity1);
				ds.put(entity2);
			}
		}
		this.setReturnArguments("valid", String.valueOf(valid));
		this.setReturnArguments("why", why);
		//
		return this.getServiceContext().doNextProcess();
	}
}
