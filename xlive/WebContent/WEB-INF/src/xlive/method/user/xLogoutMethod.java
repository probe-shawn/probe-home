package xlive.method.user;

import xlive.method.*;

public class xLogoutMethod extends xDefaultMethod{

	public Object process() throws xMethodException{
		getServiceContext().setLogout();
		setMethodReturnArguments("", "login.return.valid", "false");
		setMethodReturnArguments("", "login.return.data.authorized", "");
		return getServiceContext().doNextProcess();
	}
}
