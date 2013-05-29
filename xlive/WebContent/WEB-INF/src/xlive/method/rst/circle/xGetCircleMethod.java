package xlive.method.rst.circle;


import xlive.method.*;
import org.w3c.dom.*;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Entity;


public class xGetCircleMethod extends xDefaultMethod{
	public Object process()throws xMethodException{
		boolean valid=true;
		String why = "";
		String fid=this.getArguments("fid");
		Element circle_element = this.setReturnArguments("circle", "");
		Entity entity = null;
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		if(fid != null && fid.trim().length()>0){
			try{
				entity=ds.get(xCircle.generateKey(fid));
			}catch(EntityNotFoundException e){entity = null;}
		}
		if(entity == null){
			valid=false;
			why = "circle not found";
		}else{
			xCircle.xmlCircle(new xCircle(entity), circle_element);
		}
		this.setReturnArguments("valid", String.valueOf(valid));
		this.setReturnArguments("why", why);
		return this.getServiceContext().doNextProcess();
	}
	
}
