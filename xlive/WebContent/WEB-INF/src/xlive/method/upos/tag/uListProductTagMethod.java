package xlive.method.upos.tag;

import xlive.method.*;
import xlive.method.rst.mall.xMallDetail;
import xlive.method.rst.menu.xBom;

import org.w3c.dom.*;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.SortDirection;

public class uListProductTagMethod extends xDefaultMethod{
	public Object process()throws xMethodException{
		boolean valid=true;
		String why = "";
		String fid = this.getArguments("tag.fid");		
		Element tags_element = this.setReturnArguments("tags", "");
    	DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
	    Query q = new Query(uProductTag.class.getSimpleName());
	    q.setFilter(new Query.FilterPredicate("owner", FilterOperator.EQUAL,fid));
	    q.addSort("sort", SortDirection.ASCENDING);
	    int count=0;
	    PreparedQuery pq=null;
	    try{
		    pq = ds.prepare(q);
			for(Entity found : (Iterable<Entity>)pq.asIterable()) {
				Element tag_element=(Element)tags_element.appendChild(this.createElement("tag"));
				uProductTag t=new uProductTag(found);
				uProductTag.xmlProductTag(t, tag_element);			 		       
		        ++count;
		    }
	    }catch(Exception e1){
	    	count=0;
	    }
		//test
		if(count==0){
			String bomkey="";
			org.w3c.dom.Document doc=null;
			
			try{
				Entity mall_entity = ds.get(xMallDetail.generateKey(fid));
			    bomkey = new xMallDetail(mall_entity).getBomKey();
				System.out.println(bomkey);
			}catch(EntityNotFoundException e){				
			}
			
			q = new Query("xBom");	    	
			
	        	q.setFilter(
	        			CompositeFilterOperator.and(
	        				new Query.FilterPredicate("mallFid", FilterOperator.EQUAL, fid),
	        				new Query.FilterPredicate("type", FilterOperator.EQUAL, 0)
	        			));	        
	    	
	    	q.addSort("date", SortDirection.DESCENDING);
			
	    //	q.setFilter(new Query.FilterPredicate("parentKey", FilterOperator.EQUAL, bomkey));
	    //	q.addSort("type", SortDirection.ASCENDING);
	    //	q.addSort("date",SortDirection.ASCENDING);
	        pq = ds.prepare(q);
	    	for(Entity found : pq.asIterable()) {	    		
	    		xBom xb=new xBom(found);
	    		if(xb.getId()!=null && xb.getId().length()>0){
	    			
	    		}else{
	    		Element tag_element=(Element)tags_element.appendChild(this.createElement("tag"));
	    		doc =tag_element.getOwnerDocument();
	    		tag_element.appendChild(doc.createElement("key")).setTextContent("testkey");
	    		tag_element.appendChild(doc.createElement("name")).setTextContent(xb.getName());
	    		tag_element.appendChild(doc.createElement("icon")).setTextContent(xb.getIcon());
	    		tag_element.appendChild(doc.createElement("sort")).setTextContent(String.valueOf(++count*1000));
	    		uSetProductTagMethod.insert(fid, xb.getName(), xb.getIcon(), count*1000);
	    		}
	    	}
			
		}
				
		tags_element.setAttribute("count", String.valueOf(count));
		this.setReturnArguments("valid", String.valueOf(valid));
		this.setReturnArguments("why", why);
		return this.getServiceContext().doNextProcess();
	}
}
