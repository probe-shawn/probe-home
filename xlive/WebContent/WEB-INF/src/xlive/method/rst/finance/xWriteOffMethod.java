package xlive.method.rst.finance;

import java.text.NumberFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import org.w3c.dom.Element;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import xlive.google.xMemCache;
import xlive.method.*;
import xlive.method.img.barcode.*;
import xlive.method.logger.xLogger;
import xlive.method.rst.finance.xBillDetail;
import xlive.method.rst.mall.xMallDetail;
import xlive.google.ds.xLong;

public class xWriteOffMethod extends xDefaultMethod{
	public Object process()throws xMethodException{
		boolean valid=true;
		//手動銷帳，測試用
		return null;
	}
}
