package xlive.method.rst.order;

import xlive.method.*;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Entity;
import org.w3c.dom.*;


public class xGetGoodsMethod extends xDefaultMethod{
	public Object process()throws xMethodException{
		boolean valid=false;
		String why="";
		String key = this.getArguments("key");
		Entity entity = null;
		Element order_element = this.setReturnArguments("order", "");
		
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		try {
			entity = ds.get(KeyFactory.stringToKey(key));
			xOrder.xmlOrderGoods(new xOrder(entity), order_element);
		} catch (EntityNotFoundException e1) {
			valid = false;
			why = "order not found";
		}
		this.setReturnArguments("valid", String.valueOf(valid));
		this.setReturnArguments("why", why);
		return this.getServiceContext().doNextProcess();
	}
	
}
