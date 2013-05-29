package xlive.method.authorized.forum;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import xlive.xProbeServlet;
import xlive.method.*;

public class xChangePasswordMethod extends xDefaultMethod{

	public Object process() throws xMethodException{
		if(xProbeServlet.isGAE()){
			String redirect_url = this.getProperties("gae-redirect-url");
			return processInternetWebObjectMethod(redirect_url, getObjectPath(), getMethodName());
		}

		String database_object_name=getProperties("database-object-name");
		processWebObjectMethod(database_object_name, "get-connection");
		Connection connection_instance = (Connection)getWebObjectPropertiesObject(database_object_name, "connection-instance");
		if(connection_instance == null)	throw createMethodException("connection-instance", "not found");
		//////
		String user_id=getArguments("user-id");
		String user_pass=getArguments("user-pass");
		String user_pass2=getArguments("user-pass2");
		boolean valid=true;
		String why="";
		//
		boolean auto_commit=true;
		PreparedStatement statement=null;
		try{
			auto_commit=connection_instance.getAutoCommit();
			connection_instance.setAutoCommit(false);
			//
			String command="update xlive_cmm_user set USER_PASS = ? where USER_ID = ? and USER_PASS = ?";
			statement=connection_instance.prepareStatement(command);
			statement.setString(1,user_pass2);
			statement.setString(2,user_id);
			statement.setString(3,user_pass);
			int count = statement.executeUpdate();
			statement.close();
			statement=null;
			if(count != 1){
				valid=false;
				why="user count = "+count;
				connection_instance.rollback();
			}else connection_instance.commit();
		}catch(SQLException se){
			valid=false;
			why=se.getMessage();
			se.printStackTrace();
			throw createMethodException("SQLException", se.getLocalizedMessage(), String.valueOf(se.getErrorCode()), se.getSQLState());
		}finally{
			try{
				if(statement != null)statement.close();
				connection_instance.setAutoCommit(auto_commit);
			}catch(Exception eignor){}
		}
		//
		processWebObjectMethod(database_object_name, "close-connection");
		/////////////////////////////////////////
		setReturnArguments("valid", String.valueOf(valid));
		setReturnArguments("why", why);
		return getServiceContext().doNextProcess();
	}
}
