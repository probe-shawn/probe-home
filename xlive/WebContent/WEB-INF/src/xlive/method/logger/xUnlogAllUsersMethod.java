package xlive.method.logger;


import xlive.method.xDefaultMethod;
import xlive.method.xMethodException;

public class xUnlogAllUsersMethod  extends xDefaultMethod{
	public Object process()throws xMethodException{
		boolean valid=true;
		String why="";
		xLogger.removeUsers();
		setReturnArguments("valid", String.valueOf(valid));
		setReturnArguments("why", why);
		return getServiceContext().doNextProcess();
	}
}
