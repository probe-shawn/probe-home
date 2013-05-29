package xlive.method.upos.tag;
import java.util.Date;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.KeyFactory;
import xlive.method.*;

public class uSetProductTagMethod extends xDefaultMethod{
	public Object process()throws xMethodException{
		boolean valid=true;
		String why = "";		
		String key=this.getArguments("tag.key");
		String fid=this.getArguments("tag.fid");
		String name=this.getArguments("tag.name");
		String desc=this.getArguments("tag.desc");
		String icon=this.getArguments("tag.icon");
		String seq=this.getArguments("tag.sort");	
		double sequence=0;	
		try{		
				sequence=Double.parseDouble(seq);
		}catch(Exception e){
			System.out.println("err="+e.getMessage());
		}
		Entity entity =null;
		uProductTag  tag=null;
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		if(entity == null && key != null && key.trim().length()>0){
			try{
				entity = ds.get(KeyFactory.stringToKey(key));
			}catch(EntityNotFoundException e){entity = null;}
		}
		if(entity == null){
			entity = new Entity(uProductTag.class.getSimpleName());
			tag = new uProductTag(entity);
			tag.setOwner(fid);
			tag.setName(name);
			tag.setDesc(desc);
			tag.setDate(new Date());
			tag.setIcon(icon);
			tag.setSort(sequence);
			ds.put(tag.entity);	
			tag.setProductTagID();
			ds.put(tag.entity);	
		}else{
			tag = new uProductTag(entity);
			if(name!=null && name.trim().length()>0)
				tag.setName(name);
			if(desc!=null && desc.trim().length()>0)
				tag.setDesc(desc);
			if(icon!=null && icon.trim().length()>0)
				tag.setIcon(icon);
			tag.setSort(sequence);
			ds.put(tag.entity);	
		}					
		this.setReturnArguments("valid", String.valueOf(valid));
		this.setReturnArguments("why", why);
		this.setReturnArguments("key", tag.getKey());		//
		return this.getServiceContext().doNextProcess();
	}
	
	
	public static void insert(String fid,String name,String icon,double seq){
			
		double sequence=seq;	
		
		Entity entity =null;
		uProductTag  tag=null;
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();		
		
		entity = new Entity(uProductTag.class.getSimpleName());
		tag = new uProductTag(entity);
		tag.setOwner(fid);
		tag.setName(name);
		tag.setDate(new Date());
		tag.setIcon(icon);
		tag.setSort(sequence);
		ds.put(tag.entity);	
		tag.setProductTagID();
		ds.put(tag.entity);	
							
	}
}
