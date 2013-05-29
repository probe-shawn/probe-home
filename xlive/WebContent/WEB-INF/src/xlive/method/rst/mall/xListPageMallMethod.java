package xlive.method.rst.mall;

import xlive.method.*;
import org.w3c.dom.*;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.QueryResultList;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.SortDirection;

public class xListPageMallMethod extends xDefaultMethod{
	public Object process()throws xMethodException{
		boolean valid=true;
		String why = "";
		String limit_string = this.getArguments("limit");
		String cursor = this.getArguments("cursor");
		int limit = 25;
		if(limit_string != null && limit_string.length() > 0){
			try{
				limit = Integer.parseInt(limit_string);
			}catch(Exception e){}
		}

		Element malls_element = this.setReturnArguments("malls", "");
    	DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
	    Query q = new Query(xMallDetail.class.getSimpleName());
	    /*
	    q.addFilter("mallType", FilterOperator.EQUAL, "page");
	    */
	    q.setFilter(new Query.FilterPredicate("mallType", FilterOperator.EQUAL, "page"));
	    q.addSort("visit", SortDirection.DESCENDING);
	    
	    
	    
    	PreparedQuery pq = ds.prepare(q);
    	Cursor decoded_cursor=null;
		if(cursor != null && cursor.length() > 0){
			 decoded_cursor = Cursor.fromWebSafeString(cursor);
		}
		QueryResultList<Entity> list_result = pq.asQueryResultList(decoded_cursor != null ? FetchOptions.Builder.withStartCursor(decoded_cursor).limit(limit) : FetchOptions.Builder.withDefaults().limit(limit));
    	int count = 0;
		for(Entity entity : list_result){
	        Element mall_element=(Element)malls_element.appendChild(this.createElement("mall"));
	        xMallDetail mall = new xMallDetail(entity);
	        mall_element.appendChild(this.createElement("fid")).setTextContent(mall.getFid());
	        mall_element.appendChild(this.createElement("name")).setTextContent(mall.getName());
	        mall_element.appendChild(this.createElement("date")).setTextContent(mall.getDateString());
	        mall_element.appendChild(this.createElement("store-name")).setTextContent(mall.getStoreName());
	        mall_element.appendChild(this.createElement("mall-bg-icon")).setTextContent(mall.getMallBgIcon());
	        mall_element.appendChild(this.createElement("desc")).setTextContent(mall.getDesc());
	        mall_element.appendChild(this.createElement("addr")).setTextContent(mall.getAddr());
	        mall_element.appendChild(this.createElement("phone")).setTextContent(mall.getPhone());
	        ++count;
    	}
		if(count == limit && list_result != null){
			 this.setReturnArguments("cursor", list_result.getCursor().toWebSafeString());
		}
	    /*
	    int count= 0;
	    for(Entity found : (Iterable<Entity>)ds.prepare(q).asIterable()) {
	        Element mall_element=(Element)malls_element.appendChild(this.createElement("mall"));
	        xMallDetail mall = new xMallDetail(found);
	        mall_element.appendChild(this.createElement("fid")).setTextContent(mall.getFid());
	        mall_element.appendChild(this.createElement("name")).setTextContent(mall.getName());
	        mall_element.appendChild(this.createElement("date")).setTextContent(mall.getDateString());
	        mall_element.appendChild(this.createElement("store-name")).setTextContent(mall.getStoreName());
	        mall_element.appendChild(this.createElement("addr")).setTextContent(mall.getAddr());
	        ++count;
	    }
	    */
    	malls_element.setAttribute("count", String.valueOf(count));
		this.setReturnArguments("valid", String.valueOf(valid));
		this.setReturnArguments("why", why);
		return this.getServiceContext().doNextProcess();
	}
}
