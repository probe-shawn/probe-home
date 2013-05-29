package xlive.method.db;

import java.sql.*;
import javax.sql.DataSource;

import xlive.xProbeServlet;
import xlive.method.*;

public class xGetConnectionMethod extends xDefaultMethod{
	public Object process() throws xMethodException{
		return null;
		/*
		if(xProbeServlet.isGAE()){
			return getServiceContext().doNextProcess();
		}
		Object synSessionDbConnection = this.getServiceContext().getSynSessionDbConnection();
		synchronized(synSessionDbConnection){
			return get_process();
		}
		*/
	}
	/*
	private Object get_process() throws xMethodException {
		overwriteDefaultPropertiesToArguments();
		//if connection existed 
		Connection connection_instance=null;
		connection_instance=(Connection)getPropertiesObject("connection-instance");
		if(connection_instance != null) {
			addReferenceCount();
			return connection_instance;
		}
		//data source existed ?
		Object datasource_instance = getPropertiesObject("datasource-instance");
		if(datasource_instance==null){
			datasource_instance = processMethod("create-datasource");
		}
		String user=getArguments("user");
		String password=getArguments("password");
		if(datasource_instance != null) {
			try{
				connection_instance= ((DataSource)datasource_instance).getConnection(user, password);// ???? must add the username and password
				logMessage("OK");
			}catch(SQLException e){
				//e.printStackTrace();
				throw this.createMethodException("SQLException", e.getLocalizedMessage(),String.valueOf(e.getErrorCode()),e.getSQLState());
			}
		}
		if(connection_instance == null) throw this.createMethodException("connection-instance", "create failure");
		addReferenceCount();
		setPropertiesObject("connection-instance", connection_instance);
		return connection_instance;
	}
	private void addReferenceCount()throws xMethodException {
		String ref_count_string = getProperties("connection-reference-count");
		int ref_count=0;
		try{
			ref_count=Integer.parseInt(ref_count_string);
		}catch(Exception e){}
		setProperties("connection-reference-count", String.valueOf(++ref_count));
	}
	*/
}
