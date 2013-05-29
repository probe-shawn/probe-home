package xlive.method.db;
import java.sql.*;

import xlive.xProbeServlet;
import xlive.method.*;

public class xIsolationLevelMethod extends xDefaultMethod{

	public Object process() throws xMethodException{
		if(xProbeServlet.isGAE()){
			return getServiceContext().doNextProcess();
		}
		Connection connection_instance=(Connection)getPropertiesObject("connection-instance");
		if(connection_instance == null){
			throw createMethodException("connection-instance", "not found");
		}
		int isolation_level=Connection.TRANSACTION_NONE;
		String level=getArguments("level");
		try{
			if(level!=null && level.trim().length() > 0)isolation_level=Integer.parseInt(level);
		}catch(Exception e){}
		try{
			connection_instance.setTransactionIsolation(isolation_level);
		}catch(SQLException se){
			se.printStackTrace();
			throw createMethodException("SQLException", se.getLocalizedMessage(), String.valueOf(se.getErrorCode()), se.getSQLState());
		}
		return getServiceContext().doNextProcess();
	}
}
