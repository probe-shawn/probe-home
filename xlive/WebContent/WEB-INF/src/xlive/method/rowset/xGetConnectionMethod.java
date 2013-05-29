package xlive.method.rowset;

import xlive.method.*;

public class xGetConnectionMethod extends xDefaultMethod{

	public Object process() throws xMethodException{
		String database_object_name=getProperties("database-object-name");
		String user=getWebObjectProperties("user",database_object_name+".user");
		if(user != null)setQNameArguments("get-connection.user", user);
		String password=getWebObjectProperties("user",database_object_name+".password");
		if(password != null)setQNameArguments("get-connection.password", password);
		return processWebObjectMethod(database_object_name, "get-connection");
	}
}
