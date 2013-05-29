package xlive.method.rst.menu;


import xlive.method.*;
import org.w3c.dom.*;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Entity;


public class xListDirMethod extends xDefaultMethod{
	public Object process()throws xMethodException{
		boolean valid=true;
		String why = "";
		String key=this.getArguments("key");
		Element data = this.setReturnArguments("data", "");
  		String bomkey = null;
		Entity bom_entity = null;
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		try{
			bom_entity = ds.get(KeyFactory.stringToKey(key));
			data.appendChild(xBom.listBomXml(new xBom(bom_entity),1));
		}catch(EntityNotFoundException e){
			valid = false;
			why = "bom : "+bomkey+" not found ";
		}
		this.setReturnArguments("valid", String.valueOf(valid));
		this.setReturnArguments("why", why);
		return this.getServiceContext().doNextProcess();
	}
}
