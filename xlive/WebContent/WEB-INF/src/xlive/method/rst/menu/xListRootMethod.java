package xlive.method.rst.menu;


import xlive.method.*;
import xlive.method.rst.mall.xMallDetail;
import org.w3c.dom.*;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Entity;


public class xListRootMethod extends xDefaultMethod{
	public Object process()throws xMethodException{
		boolean valid=true;
		String why = "";
		String mall_fid=this.getArguments("mall-fid");
		String comment_key=this.getArguments("comment.key");
		
		Element data = this.setReturnArguments("data", "");
  		String bomkey = null;
  		Entity mall_entity = null;
		Entity bom_entity = null;
		org.w3c.dom.Element root = null;
		
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		try{
			mall_entity = ds.get(xMallDetail.generateKey(mall_fid));
			bomkey = new xMallDetail(mall_entity).getBomKey();
		}catch(EntityNotFoundException e){
			valid = false;
			why = "mall : "+mall_fid+" not found ";
		}
		if(mall_entity != null && valid){
			try{
				bom_entity = ds.get(KeyFactory.stringToKey(bomkey));
				data.appendChild(root=xBom.listBomXml(new xBom(bom_entity),1));
			}catch(EntityNotFoundException e){
				valid = false;
				why = "bom : "+bomkey+" not found ";
			}
    		if(valid && comment_key != null && comment_key.trim().length() > 0){
    			Entity comment_entity = null;
    			try{
    				comment_entity = ds.get(KeyFactory.stringToKey(comment_key));
    			}catch(EntityNotFoundException e){
    				comment_entity = null;
    			}
    			if(comment_entity != null){
	    			org.w3c.dom.Element comment_element=xBom.listBomXml(new xBom(comment_entity), 0);
	    			comment_element.setAttribute("comment", "commnet");
	    			Element parts=(Element)root.getElementsByTagName("parts").item(0);
	    			parts.insertBefore(comment_element,parts.getFirstChild());
    			}
    		}
    		
		}
		this.setReturnArguments("valid", String.valueOf(valid));
		this.setReturnArguments("why", why);
		return this.getServiceContext().doNextProcess();
	}
}
