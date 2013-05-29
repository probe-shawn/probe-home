package xlive.method.rst.statistic;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query;

import xlive.google.xMemCache;
import xlive.method.*;

public class xCalcMallMethod extends xDefaultMethod{
	public Object process()throws xMethodException{
		boolean valid=true;
		String why = "";
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		Query q = new Query("xMall");
		q.setKeysOnly();
		long count = ds.prepare(q).countEntities(FetchOptions.Builder.withLimit(10000000));
		xMemCache.iMallService().put(xGetMethod.mallCount, Long.valueOf(count));
		this.setReturnArguments("count", String.valueOf(count));
		this.setReturnArguments("valid", String.valueOf(valid));
		this.setReturnArguments("why", why);
		return this.getServiceContext().doNextProcess();
	}
}
