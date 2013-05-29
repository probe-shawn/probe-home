package xlive.method.rst.circle;

import java.util.Date;

import org.w3c.dom.Element;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.KeyFactory;

import xlive.method.*;

public class xSetCircleMallMethod extends xDefaultMethod{
	public Object process()throws xMethodException{
		boolean valid=true;
		String why = "";
		String key=this.getArguments("key");
		Entity entity =null;
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		if(key != null && key.trim().length()>0){
			try{
				entity = ds.get(KeyFactory.stringToKey(key));
			}catch(EntityNotFoundException e){entity = null;}
		}
		if(entity == null){
			entity = new Entity(xCircleMall.class.getSimpleName());
			entity.setProperty("date", new Date());
		}
		xCircleMall circle_mall=new xCircleMall(entity);
		circle_mall.setCircleFid(this.getArguments("circle-fid"));
		circle_mall.setMallFid(this.getArguments("mall-fid"));
		circle_mall.setStatus(this.getArguments("status"));
		circle_mall.setC2mMessage(this.getArguments("c2m-message"));
		circle_mall.setM2cMessage(this.getArguments("m2c-message"));
		ds.put(circle_mall.entity);
		///////
		Element return_circle_mall = this.setReturnArguments("circle-mall", "");
		xCircleMall.xmlCircleMall(circle_mall, return_circle_mall);
		org.w3c.dom.Document doc = return_circle_mall.getOwnerDocument();
        try {
			Entity circle_entity = ds.get(xCircle.generateKey(circle_mall.getCircleFid()));
			xCircle circle = new xCircle(circle_entity);
			return_circle_mall.appendChild(doc.createElement("circle-name")).setTextContent(circle.getName());
			return_circle_mall.appendChild(doc.createElement("circle-icon")).setTextContent(circle.getIcon());
		} catch (EntityNotFoundException e) {
		}
		this.setReturnArguments("valid", String.valueOf(valid));
		this.setReturnArguments("why", why);
		//
		return this.getServiceContext().doNextProcess();
	}
}
