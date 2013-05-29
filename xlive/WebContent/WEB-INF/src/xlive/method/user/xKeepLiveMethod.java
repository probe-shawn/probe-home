package xlive.method.user;

import xlive.method.*;

public class xKeepLiveMethod extends xDefaultMethod{

	public Object process() throws xMethodException{
		setReturnArguments("data", this.getServiceContext().getSessionId());
		return getServiceContext().doNextProcess();
	}
}
