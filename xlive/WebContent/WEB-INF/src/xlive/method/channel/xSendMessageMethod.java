package xlive.method.channel;


import java.util.ArrayList;

import com.google.appengine.api.channel.ChannelMessage;
import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.FilterOperator;
import xlive.method.*;


public class xSendMessageMethod extends xDefaultMethod{

	public Object process() throws xMethodException{
		boolean valid=true;
		String why="";
		String sender_id=this.getArguments("sender.id");
		String id=this.getArguments("receiver.id");
		String fid=this.getArguments("receiver.fid");
		String mall_fid=this.getArguments("receiver.mall-fid");
		String barter_fid=this.getArguments("receiver.barter-fid");
		String message=this.getArguments("message");
		String self=this.getArguments("self");
		if(message==null || !message.startsWith("<message"))message ="<message>"+message+"</message>";
		if("true".equals(self)) sender_id=null;
		xSendMessageMethod.sendMessage(sender_id, id, fid, mall_fid,barter_fid,message);
		setReturnArguments("valid", String.valueOf(valid));
		setReturnArguments("why", why);
		return getServiceContext().doNextProcess();
	}

	public static void sendMessage(String sender_id, String id, String fid, String mall_fid, String barter_fid,String msg){
		ArrayList<Key> put_array = new ArrayList<Key>();
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		if(id != null && id.trim().length() > 0){
			try{
				ChannelService channelService = ChannelServiceFactory.getChannelService();
			    channelService.sendMessage(new ChannelMessage(id, msg));
			}catch(com.google.appengine.api.channel.ChannelFailureException e){
				put_array.add(KeyFactory.stringToKey(id));
			}
		}else{
	    	Query q = new Query(xOnline.class.getSimpleName());
	    	/*
	    	if(mall_fid != null && mall_fid.trim().length()>0) q.addFilter("mallFid", FilterOperator.EQUAL, mall_fid);
	    	if(barter_fid != null && barter_fid.trim().length()>0) q.addFilter("barterFid", FilterOperator.EQUAL, barter_fid);
			if(fid != null && fid.trim().length()>0) q.addFilter("fid", FilterOperator.EQUAL, fid);
			q.addFilter("status", FilterOperator.EQUAL, 1);
			*/
			ArrayList<Query.Filter> filters = new ArrayList<Query.Filter>();
	    	if(mall_fid != null && mall_fid.trim().length()>0) filters.add(new Query.FilterPredicate("mallFid", FilterOperator.EQUAL, mall_fid));
	    	if(barter_fid != null && barter_fid.trim().length()>0) filters.add(new Query.FilterPredicate("barterFid", FilterOperator.EQUAL, barter_fid));
			if(fid != null && fid.trim().length()>0) filters.add(new Query.FilterPredicate("fid", FilterOperator.EQUAL, fid));
			filters.add(new Query.FilterPredicate("status", FilterOperator.EQUAL, 1));
			q.setFilter(new Query.CompositeFilter(CompositeFilterOperator.AND, filters));
			
	    	for(Entity found : ds.prepare(q).asIterable()) {
	    		String keystr=KeyFactory.keyToString(found.getKey());
	    		if(sender_id != null && keystr.equals(sender_id)) continue;
				try{
					ChannelService channelService = ChannelServiceFactory.getChannelService();
				    channelService.sendMessage(new ChannelMessage(keystr, msg));
				}catch(com.google.appengine.api.channel.ChannelFailureException e){
					put_array.add(found.getKey());
				}catch(Exception e){
					put_array.add(found.getKey());
				}
	    	}
		}
		if(put_array.size() > 0) ds.delete(put_array);
	}
}
