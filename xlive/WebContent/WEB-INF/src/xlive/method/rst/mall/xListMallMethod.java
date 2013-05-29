package xlive.method.rst.mall;


import java.util.Arrays;

import javax.xml.xpath.XPathConstants;

import xlive.method.*;
import xlive.method.rst.barter.xBarter;

import org.w3c.dom.*;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.FilterOperator;


public class xListMallMethod extends xDefaultMethod{
	@SuppressWarnings("unchecked")
	public Object process()throws xMethodException{
		boolean valid=true;
		String why = "";
		Element malls_element = this.setReturnArguments("malls", "");
    	DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

		Element in_node=(Element)this.getArguments("ins", XPathConstants.NODE);
		NodeList node_list = (NodeList)in_node.getElementsByTagName("in");
		
		Object[] results = new Object[node_list.getLength()];
		
		for(int i = 0; i < node_list.getLength();++i){
	    	Query q = new Query("xMallDetail");
	    	String instr = ((Element)node_list.item(i)).getTextContent();
	    	String[] fids = instr.split(",");
	    	/*
	    	q.addFilter("fid", FilterOperator.IN, Arrays.asList(fids));
	    	*/
	    	q.setFilter(new Query.FilterPredicate("fid", FilterOperator.IN, Arrays.asList(fids)));
	    	PreparedQuery pq = ds.prepare(q);
	    	results[i]= pq.asIterable();
		}
		int count = 0;
		for(int j = 0; j <results.length; ++j){
	    	for(Entity found : (Iterable<Entity>)results[j]) {
	        	Element mall_element=(Element)malls_element.appendChild(this.createElement("mall"));
	        	xMallDetail mall = new xMallDetail(found);
	        	mall_element.appendChild(this.createElement("fid")).setTextContent(mall.getFid());
	        	mall_element.appendChild(this.createElement("name")).setTextContent(mall.getName());
	        	mall_element.appendChild(this.createElement("date")).setTextContent(mall.getDateString());
	        	mall_element.appendChild(this.createElement("store-name")).setTextContent(mall.getStoreName());
	        	mall_element.appendChild(this.createElement("mall-bg-icon")).setTextContent(mall.getMallBgIcon());
	        	mall_element.appendChild(this.createElement("menu-bg-icon")).setTextContent(mall.getMenuBgIcon());
	        	mall_element.appendChild(this.createElement("barter-bg-icon")).setTextContent(mall.getBarterBgIcon());
	        	Query q = new Query(xBarter.class.getSimpleName());
	        	/*
	        	q.addFilter("removed", FilterOperator.EQUAL, 0);
	        	q.addFilter("ownerFid", FilterOperator.EQUAL, mall.getFid());
	        	*/
	        	q.setFilter(
	        			CompositeFilterOperator.and(
	        				new Query.FilterPredicate("removed", FilterOperator.EQUAL, 0),
	        				new Query.FilterPredicate("ownerFid", FilterOperator.EQUAL, mall.getFid())
	        			)
	        	);

	        	long barters=ds.prepare(q).countEntities(FetchOptions.Builder.withDefaults());
	        	mall_element.appendChild(this.createElement("barters")).setTextContent(String.valueOf(barters));
	        	++count;
	    	}
		}
    	malls_element.setAttribute("count", String.valueOf(count));
		
		this.setReturnArguments("valid", String.valueOf(valid));
		this.setReturnArguments("why", why);
		return this.getServiceContext().doNextProcess();
	}
	
}
