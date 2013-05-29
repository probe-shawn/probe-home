package xlive.method.rst.mall;


import xlive.method.*;
import org.w3c.dom.*;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Entity;


public class xGetMallMethod extends xDefaultMethod{
	public Object process()throws xMethodException{
		boolean valid=true;
		String why = "";
		String keystr= this.getArguments("key");
		String fid=this.getArguments("fid");
		Element mall_element = this.setReturnArguments("mall", "");
		Entity entity = null;
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		if(keystr != null && keystr.trim().length()>0){
			try{
				entity = ds.get(KeyFactory.stringToKey(keystr));
			}catch(EntityNotFoundException e){entity = null;}
		}
		if(entity == null && fid != null && fid.trim().length()>0){
			try{
				entity=ds.get(xMallDetail.generateKey(fid));
			}catch(EntityNotFoundException e){entity = null;}
		}
		if(entity == null){
			valid=false;
			why = "mall not found";
		}else{
			xMallDetail.xmlMallDetail(new xMallDetail(entity), mall_element);
			try{
				Entity mall_entity=ds.get(xMall.generateKey(fid));
				xMall.xmlMall(new xMall(mall_entity), mall_element);
			}catch(EntityNotFoundException e){}

		}
		this.setReturnArguments("valid", String.valueOf(valid));
		this.setReturnArguments("why", why);
		return this.getServiceContext().doNextProcess();
	}
	
}
