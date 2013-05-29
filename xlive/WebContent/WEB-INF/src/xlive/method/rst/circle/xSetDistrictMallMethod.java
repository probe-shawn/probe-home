package xlive.method.rst.circle;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.xpath.XPathConstants;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.KeyFactory;

import xlive.method.*;

public class xSetDistrictMallMethod extends xDefaultMethod{
	public Object process()throws xMethodException{
		boolean valid=true;
		String why = "";
		String key=this.getArguments("key");
		Entity entity =null;
		xDistrictMall dmall=null;
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		if(entity == null && key != null && key.trim().length()>0){
			try{
				entity = ds.get(KeyFactory.stringToKey(key));
			}catch(EntityNotFoundException e){entity = null;}
		}
		if(entity == null){
			entity = new Entity(xDistrictMall.class.getSimpleName());
			dmall = new xDistrictMall(entity);
			dmall.setDate(new Date());
			dmall.setSort(new Date().getTime());
			dmall.setCircleFid(this.getArguments("circle-fid"));
			dmall.setDistrictKey(this.getArguments("district-key"));
			dmall.setMallFid(this.getArguments("mall-fid"));
		}else{
			dmall = new xDistrictMall(entity);
			dmall.setSort(this.getArguments("sort"));
		}
		
		
		
		ds.put(dmall.entity);
		/*
		*/
		this.setReturnArguments("valid", String.valueOf(valid));
		this.setReturnArguments("why", why);
		this.setReturnArguments("key", dmall.getKey());
		//
		return this.getServiceContext().doNextProcess();
	}
}
