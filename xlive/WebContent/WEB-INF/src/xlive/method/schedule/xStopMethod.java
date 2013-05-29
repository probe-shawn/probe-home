package xlive.method.schedule;

import xlive.method.*;

public class xStopMethod extends xDefaultMethod{
	public Object process()throws xMethodException{
		xStartMethod.stopService();
		return getServiceContext().doNextProcess();
	}
}
