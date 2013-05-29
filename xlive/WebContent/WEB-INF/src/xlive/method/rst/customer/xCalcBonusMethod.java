package xlive.method.rst.customer;


import java.util.Date;
import xlive.xUtility;
import xlive.method.*;
import xlive.method.rst.bonus.xBonus;
import xlive.method.rst.customer.xCustomer;
import xlive.method.rst.customer.xVisit;
import org.w3c.dom.*;
import com.google.appengine.api.datastore.AsyncDatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.SortDirection;


public class xCalcBonusMethod extends xDefaultMethod{
	
	
	public Object process()throws xMethodException{
		boolean valid=true;
		String why = "";
		String fid=this.getArguments("fid");
		Element data_element = this.setReturnArguments("data", "");
		//
		AsyncDatastoreService ads = DatastoreServiceFactory.getAsyncDatastoreService();
		//
		Query q1 = new Query(xBonus.class.getSimpleName());
    	q1.setFilter(
    			CompositeFilterOperator.and(
    				new Query.FilterPredicate("fromId", FilterOperator.EQUAL, fid),
    				new Query.FilterPredicate("fromType", FilterOperator.EQUAL, xCustomer.bonusType)
    			)
    	);
		Iterable<Entity> q1_result = ads.prepare(q1).asIterable();

		Query q2 = new Query(xBonus.class.getSimpleName());
    	q2.setFilter(
    			CompositeFilterOperator.and(
    				new Query.FilterPredicate("toId", FilterOperator.EQUAL, fid),
    				new Query.FilterPredicate("toType", FilterOperator.EQUAL, xCustomer.bonusType)
    			)
    	);
		Iterable<Entity> q2_result = ads.prepare(q2).asIterable();
		//
		Query q3 = new Query(xVisit.class.getSimpleName());
		q3.setFilter(new Query.FilterPredicate("fid", FilterOperator.EQUAL, fid));
		q3.addSort("date", SortDirection.DESCENDING);
		Iterable<Entity> q3_result = ads.prepare(q3).asIterable(FetchOptions.Builder.withLimit(1));
		//
		//
		long q1_sum = 0;
		for(Entity q1_entity :q1_result)q1_sum += ((Long)q1_entity.getProperty("bonus"));
		long q2_sum = 0;
		for(Entity q2_entity :q2_result)q2_sum += ((Long)q2_entity.getProperty("bonus"));
		String last_date_string = xUtility.formatDate(new Date());
		for(Entity q3_entity :q3_result){
			Date ldate = (Date)q3_entity.getProperty("date");
			if(ldate != null)last_date_string =xUtility.formatDate(ldate);
		}
		long bonus = q2_sum-q1_sum,bonus_total=q2_sum,bonus_used=q1_sum;
		Document doc=data_element.getOwnerDocument();
		data_element.appendChild(doc.createElement("last-date")).setTextContent(last_date_string);
		data_element.appendChild(doc.createElement("bonus-total")).setTextContent(String.valueOf(bonus_total));
		data_element.appendChild(doc.createElement("bonus")).setTextContent(String.valueOf(bonus));
		data_element.appendChild(doc.createElement("bonus-used")).setTextContent(String.valueOf(bonus_used));
		//
		this.setReturnArguments("valid", String.valueOf(valid));
		this.setReturnArguments("why", why);
		return this.getServiceContext().doNextProcess();
	}
	
}
