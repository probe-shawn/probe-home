package xlive.method.sms.provider.http;

import xlive.method.*;

public class xStartServiceMethod extends xDefaultMethod {
	public Object process()throws xMethodException{
		this.setQNameArguments("send-text-message.start-service", "true");
		this.processMethod("send-text-message");
		return getServiceContext().doNextProcess();
	}
}
