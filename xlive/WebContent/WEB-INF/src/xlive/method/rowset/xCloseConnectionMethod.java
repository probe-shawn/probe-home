package xlive.method.rowset;

import xlive.method.*;

public class xCloseConnectionMethod extends xDefaultMethod{

	public Object process() throws xMethodException{
		String database_object_name=getProperties("database-object-name");
		processWebObjectMethod(database_object_name, "close-connection");
		return getServiceContext().doNextProcess();
	}
}
