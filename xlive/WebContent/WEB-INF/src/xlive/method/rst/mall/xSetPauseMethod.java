package xlive.method.rst.mall;

import java.util.Date;

import javax.xml.xpath.XPathConstants;

import org.w3c.dom.Element;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import xlive.method.*;

public class xSetPauseMethod extends xDefaultMethod{
	public Object process()throws xMethodException{
		boolean valid=true;
		String why = "";
		String mall_fid=this.getArguments("mall.mall-fid");
		String pause_string=this.getArguments("mall.pause-string");
		Element pause_string_node=(Element)this.getArguments("mall.pause-string", XPathConstants.NODE);
		long pause=0;
		String opt=pause_string_node.getAttribute("options");
		String[] opts= opt.split(",");
		for(int i = 0; i< opts.length;++i){
			if(pause_string.equals(opts[i])) pause=i;
		}
		String pause_message=this.getArguments("mall.pause-message");
		Entity entity =null;
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		try{
			entity = ds.get(xMallDetail.generateKey(mall_fid));
		}catch(EntityNotFoundException e){entity = null;}
		if(entity != null){
			xMallDetail mall_detail = new xMallDetail(entity);
			mall_detail.setLast(new Date());
			mall_detail.setPause(pause);
			mall_detail.setPauseMessage(pause_message);
			ds.put(mall_detail.entity);
		}else{
			valid = false;
			why="mall not found";
		}
		this.setReturnArguments("valid", String.valueOf(valid));
		this.setReturnArguments("why", why);
		//
		return this.getServiceContext().doNextProcess();
	}
}
