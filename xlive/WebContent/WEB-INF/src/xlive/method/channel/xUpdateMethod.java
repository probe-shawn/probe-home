package xlive.method.channel;


import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.KeyFactory;
import xlive.method.*;

public class xUpdateMethod extends xDefaultMethod{

	public Object process() throws xMethodException{
		boolean valid=true;
		String why="";
		String id= this.getArguments("id");
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		xOnline online;
		try {
			online = new xOnline( ds.get(KeyFactory.stringToKey(id)));
			online.setFid(this.getArguments("fid"));
			online.setName(this.getArguments("name"));
			online.setMallFid(this.getArguments("mall-fid"));
			online.setMallName(this.getArguments("mall-name"));
			online.setBarterFid(this.getArguments("barter-fid"));
			online.setBarterName(this.getArguments("barter-name"));
			ds.put(online.entity);
		} catch (EntityNotFoundException e) {
		}
		setReturnArguments("valid", String.valueOf(valid));
		setReturnArguments("why", why);
		return getServiceContext().doNextProcess();
	}
}
