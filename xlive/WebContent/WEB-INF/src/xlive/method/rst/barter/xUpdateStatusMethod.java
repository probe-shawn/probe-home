package xlive.method.rst.barter;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.KeyFactory;

import xlive.method.*;

public class xUpdateStatusMethod extends xDefaultMethod{
	public Object process()throws xMethodException{
		boolean valid=true;
		String why = "";
		String keystr=this.getArguments("key");
		String status=this.getArguments("status");
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		Entity entity = null;
		xBarter barter=null;
		long old_status=0;
		if(keystr != null && keystr.trim().length()>0){
			try{
				entity = ds.get(KeyFactory.stringToKey(keystr));
				barter = new xBarter(entity);
				old_status=barter.getStatus();
				barter.setStatus(status);
				ds.put(entity);
			}catch(EntityNotFoundException e){
			}
		}
		if(barter != null){
			this.setReturnArguments("data.barter.key", KeyFactory.keyToString(barter.entity.getKey()));
			this.setReturnArguments("data.barter.old-status", String.valueOf(old_status));
			this.setReturnArguments("data.barter.status", String.valueOf(barter.getStatus()));
			
			String client_id=this.getArguments("client-id");
			//String mall_fid=this.getArguments("mall-fid");
			String key = this.getArguments("key");
			StringBuffer msg =  new StringBuffer();
			msg.append("<message>")
				.append("<target>barter</target>")
				.append("<op>update-status</op>")
				.append("<data>")
					.append("<key>").append(key).append("</key>")
					.append("<status>").append(status).append("</status>")
					.append("<old-status>").append(old_status).append("</old-status>")
				.append("</data>")	
			.append("</message>");
			
			xlive.method.channel.xSendMessageMethod.sendMessage(client_id, null, null, barter.getOwnerFid(), null, msg.toString());
		}
		this.setReturnArguments("valid", String.valueOf(valid));
		this.setReturnArguments("why", why);
		return this.getServiceContext().doNextProcess();
	}
}
