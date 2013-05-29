package xlive.method.rst.menu;

import org.w3c.dom.Element;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.KeyFactory;
import xlive.method.*;

public class xGetBomBuyMethod extends xDefaultMethod{
	public Object process()throws xMethodException{
		boolean valid=true;
		String why = "";
		String key=this.getArguments("key");
		Element data = this.setReturnArguments("data", "");
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		if(key != null && key.trim().length()>0){
			try{
				Entity entity = ds.get(KeyFactory.createKey(xBomBuy.class.getSimpleName(),key));
				data.appendChild(xBomBuy.bomBuyXml(new xBomBuy(entity)));
			}catch(EntityNotFoundException e){
			}
		}
		this.setReturnArguments("valid", String.valueOf(valid));
		this.setReturnArguments("why", why);
		return this.getServiceContext().doNextProcess();
	}
}
