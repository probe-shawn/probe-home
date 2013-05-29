package xlive.method.channel;

import java.util.Date;


import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.KeyFactory;

import xlive.method.*;

public class xGetTokenMethod extends xDefaultMethod{

	public Object process() throws xMethodException{
		boolean valid=true;
		String why="";
		xOnline online = new xOnline(new Entity(xOnline.class.getSimpleName()));
		online.setFid(this.getArguments("fid"));
		online.setName(this.getArguments("name"));
		online.setBarterName(this.getArguments("barter-name"));
		online.setBarterFid(this.getArguments("barter-fid"));
		online.setMallName(this.getArguments("mall-name"));
		online.setMallFid(this.getArguments("mall-fid"));
		online.setType(this.getArguments("type"));
		online.setDate(new Date());
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		ds.put(online.entity);
		
	    ChannelService channelService = ChannelServiceFactory.getChannelService();
	    String key=KeyFactory.keyToString(online.entity.getKey());
	    String token = channelService.createChannel(key);
		this.setReturnArguments("token",token);
		this.setReturnArguments("id", key);
		//Element return_online = setReturnArguments("online", "");
		//xOnline.xmlOnline(online, return_online);
		setReturnArguments("valid", String.valueOf(valid));
		setReturnArguments("why", why);
		return getServiceContext().doNextProcess();
	}
}
