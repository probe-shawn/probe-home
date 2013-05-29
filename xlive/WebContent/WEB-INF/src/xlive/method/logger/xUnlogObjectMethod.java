package xlive.method.logger;


import xlive.method.xDefaultMethod;
import xlive.method.xMethodException;

public class xUnlogObjectMethod  extends xDefaultMethod{
	public Object process()throws xMethodException{
		boolean valid=true;
		String why="";
		String object_name=getArguments("object-name");
		xLogger.removeObject(object_name);
		setReturnArguments("valid", String.valueOf(valid));
		setReturnArguments("why", why);
		return getServiceContext().doNextProcess();
	}
}
