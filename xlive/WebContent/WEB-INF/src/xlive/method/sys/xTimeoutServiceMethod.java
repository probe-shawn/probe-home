package xlive.method.sys;

import xlive.xWebInformation;
import xlive.method.*;

public class xTimeoutServiceMethod extends xDefaultMethod{

	public Object process() throws xMethodException{
		setReturnArguments("data", "1");
		return getServiceContext().doNextProcess();
	}
}
