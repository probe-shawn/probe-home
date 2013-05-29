package xlive.method.rst.barter;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.KeyFactory;
import xlive.method.*;

public class xRemoveBarterMethod extends xDefaultMethod{
	public Object process()throws xMethodException{
		boolean valid=true;
		String why = "";
		String keystr=this.getArguments("key");
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		Entity entity = null;
		xBarter barter=null;
		if(keystr != null && keystr.trim().length()>0){
			try{
				entity = ds.get(KeyFactory.stringToKey(keystr));
				barter = new xBarter(entity);
				barter.setRemoved(1);
				ds.put(entity);
			}catch(EntityNotFoundException e){
				valid=false;
				why="not found";
			}
		}
		if(barter != null){
			this.setReturnArguments("data.barter.key", KeyFactory.keyToString(barter.entity.getKey()));
			this.setReturnArguments("data.barter.status", String.valueOf(barter.getStatus()));
			String client_id=this.getArguments("client-id");
			//String mall_fid=this.getArguments("mall-fid");
			String key = this.getArguments("key");
			String status = this.getArguments("status");
			StringBuffer msg =  new StringBuffer();
			msg.append("<message>")
				.append("<target>barter</target>")
				.append("<op>remove</op>")
				.append("<data>")
					.append("<key>").append(key).append("</key>")
					.append("<status>").append(status).append("</status>")
				.append("</data>")	
			.append("</message>");
			xlive.method.channel.xSendMessageMethod.sendMessage(client_id, null, null, null,barter.getOwnerFid(), msg.toString());
			
			/*
			Queue queue = QueueFactory.getDefaultQueue();
			TaskOptions to = TaskOptions.Builder.withUrl("/web/rst/barterchannel");
			to.param("method", "remove-barter");
			to.param("client-id", this.getArguments("client-id"));
			to.param("mall-fid", barter.getOwnerFid());
			to.param("mall-name",barter.getOwnerName());
			to.param("key",barter.getKey());
			to.param("status",String.valueOf(barter.getStatus()));
			queue.add(to);
			*/
		}
		this.setReturnArguments("valid", String.valueOf(valid));
		this.setReturnArguments("why", why);
		//
		return this.getServiceContext().doNextProcess();
	}
}
