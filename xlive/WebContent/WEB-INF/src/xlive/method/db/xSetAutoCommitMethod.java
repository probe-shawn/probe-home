package xlive.method.db;
import java.sql.*;

import xlive.xProbeServlet;
import xlive.method.*;

public class xSetAutoCommitMethod extends xDefaultMethod{
	/*
	public Object process() throws xMethodException{
		if(xProbeServlet.isGAE()){
			return getServiceContext().doNextProcess();
		}
		Connection connection_instance=(Connection)getPropertiesObject("connection-instance");
		if(connection_instance == null){
			throw createMethodException("connection-instance", "not found");
		}
		String on=getArguments("on");
		boolean auto_commit="true".equals(on)||"1".equals(on);
		try{
			connection_instance.setAutoCommit(auto_commit);
			setProperties("auto-commit", (auto_commit) ? "true" :"false");
		}catch(SQLException se){
			se.printStackTrace();
			throw createMethodException("SQLException", se.getLocalizedMessage(), String.valueOf(se.getErrorCode()), se.getSQLState());
		}
		return getServiceContext().doNextProcess();
	}
	*/
}
