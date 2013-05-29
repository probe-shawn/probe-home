package xlive.method.logger;


import xlive.method.xDefaultMethod;
import xlive.method.xMethodException;

public class xUnlogAllObjectsMethod  extends xDefaultMethod{
	public Object process()throws xMethodException{
		boolean valid=true;
		String why="";
		xLogger.removeObjects();
		setReturnArguments("valid", String.valueOf(valid));
		setReturnArguments("why", why);
		return getServiceContext().doNextProcess();
	}
}
