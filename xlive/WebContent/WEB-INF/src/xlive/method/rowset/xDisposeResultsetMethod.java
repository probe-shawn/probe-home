package xlive.method.rowset;

import java.sql.*;
import java.util.Hashtable;

import xlive.xProbeServlet;
import xlive.method.*;

public class xDisposeResultsetMethod extends xDefaultMethod{
	/*
	@SuppressWarnings("unchecked")
	public Object process() throws xMethodException{
		if(xProbeServlet.isGAE()){
			String redirect_url = this.getProperties("gae-redirect-url");
			return processInternetWebObjectMethod(redirect_url, getObjectPath(), getMethodName());
		}
		String database_object_name=getProperties("database-object-name");
		String resultset_id=getArguments("resultset-id");
		xSqlResultset x_resultset = null;
		synchronized(this.getServiceContext().getSynSessionDbResultset()){
			Hashtable<String,xSqlResultset> resultset_table = (Hashtable<String,xSqlResultset>) getWebObjectPropertiesObject(database_object_name,"connection-resultset-instance-table");
			if(resultset_table != null) x_resultset = resultset_table.remove(resultset_id);
		}
		if(x_resultset!=null && x_resultset.resultset != null){
			ResultSet cached_resultset = x_resultset.resultset;
			try{
				Statement statement=cached_resultset.getStatement();
				cached_resultset.close();
				if(statement != null) statement.close();
			}catch(SQLException esol){}
		}
		return getServiceContext().doNextProcess();
	}
	*/
}
