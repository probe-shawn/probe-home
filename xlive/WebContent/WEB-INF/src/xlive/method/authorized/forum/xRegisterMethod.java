package xlive.method.authorized.forum;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import org.w3c.dom.*;


import xlive.method.*;
import xlive.*;

public class xRegisterMethod extends xDefaultMethod{

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
		boolean valid=true;
		String why="";
		//
		PreparedStatement statement=null;
		ResultSet result_set=null;
		try{
			String command="select * from xlive_cmm_user where user_id = '"+getArguments("data.user-id")+"'";
			statement=connection_instance.prepareStatement(command);
			result_set=statement.executeQuery();
			if(result_set.next()){
				valid=false;
				why="user id existed";
			}
			result_set.close();
			result_set=null;
			statement.close();
			statement=null;
			if(valid){
				command="insert into xlive_cmm_user " +
						"(USER_ID, USER_PASS, USER_NAME, USER_CODE, USER_ICON, USER_EMAIL, USER_COMPANY, USER_BRIEF, COMP_CODE, USER_DUTY, USER_EXCL) " +
						"values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
				statement = connection_instance.prepareStatement(command, Statement.RETURN_GENERATED_KEYS);
				String user_id = getArguments("data.user-id");
				String user_pass = getArguments("data.user-pass");
				String user_name = getArguments("data.user-name");
				String user_code = getArguments("data.user-code");
				String user_icon = getArguments("data.user-icon");
				String user_email = getArguments("data.user-email");
				String user_company = getArguments("data.user-company");
				String user_brief = getArguments("data.user-brief");
				String comp_code = getArguments("data.comp-code");
				String user_duty = getArguments("data.user-duty");
				String user_excl = getArguments("data.user-excl");
				statement.setString(1,user_id);
				statement.setString(2,user_pass);
				statement.setString(3,user_name);
				statement.setString(4,user_code);
				statement.setString(5,user_icon);
				statement.setString(6,user_email);
				statement.setString(7,user_company);
				statement.setString(8,user_brief);
				statement.setString(9,comp_code);
				statement.setString(10,user_duty);
				statement.setString(11,user_excl);
				int result=statement.executeUpdate();
				Element return_data=setReturnArguments("data", "");
				result_set=statement.getGeneratedKeys();
				if(result_set != null){
					if(result_set.next()) {
						ResultSetMetaData rsmd=result_set.getMetaData();
						int column_count=rsmd.getColumnCount();
						for(int i=0; i<column_count;++i){
							Element column_node=xWebInformation.createElement(nodeNameFormat(rsmd.getColumnName(i+1)));
							column_node.setTextContent(result_set.getString(i+1));
							return_data.appendChild(column_node);
						}
					}
				}
				if(result != 1){
					valid=false;
					why="database insertion error : result -->"+result;
				}
			}
			if(result_set != null) result_set.close();
			result_set=null;
			if(statement != null)statement.close();
			statement=null;
		}catch(SQLException se){
			valid=false;
			why=se.getMessage();
			se.printStackTrace();
			throw createMethodException("SQLException", se.getLocalizedMessage(), String.valueOf(se.getErrorCode()), se.getSQLState());
		}finally{
			try{
				if(result_set != null) result_set.close();
				if(statement != null)statement.close();
			}catch(Exception eignor){}
		}
		//
		processWebObjectMethod(database_object_name, "close-connection");
		/////////////////////////////////////////
		setReturnArguments("valid", String.valueOf(valid));
		setReturnArguments("why", why);
		return getServiceContext().doNextProcess();
	}
    private String nodeNameFormat(String str){
    	return str.toLowerCase().replaceAll("_", "-");
    }

}
