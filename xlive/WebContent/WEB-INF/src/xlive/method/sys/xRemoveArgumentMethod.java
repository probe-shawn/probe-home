package xlive.method.sys;

import xlive.method.*;

public class xRemoveArgumentMethod extends xDefaultMethod{
	public Object process()throws xMethodException{
		String name=getMethodAttribute("name");
		if(name != null && name.trim().length() > 0) argumentsOperation(name, "", "remove");
		return true;
	}
}
