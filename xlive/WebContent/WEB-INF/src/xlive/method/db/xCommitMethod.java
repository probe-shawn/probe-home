package xlive.method.db;
import java.sql.*;

import xlive.xProbeServlet;
import xlive.method.*;


public class xCommitMethod extends xDefaultMethod{
	public Object process() throws xMethodException{
		if(xProbeServlet.isGAE()){
			return getServiceContext().doNextProcess();
		}
		Connection connection_instance = (Connection)getPropertiesObject("connection-instance");
		if(connection_instance == null){
			throw createMethodException("connection-instance", "not found");
		}
		try{
			connection_instance.commit();
		}catch(SQLException se){
			se.printStackTrace();
			throw createMethodException("SQLException", se.getLocalizedMessage(), String.valueOf(se.getErrorCode()), se.getSQLState());
		}
		return getServiceContext().doNextProcess();
	}
}
