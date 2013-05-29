package xlive.method.sms.service;

import xlive.method.*;

public class xSendTextMessageMethod extends xDefaultMethod{
	public Object process()throws xMethodException{
	    String sms_provider_name=getProperties("sms-provider-name");
	    this.processWebObjectMethod(sms_provider_name, "send-text-message");
	    return getServiceContext().doNextProcess();
	}
}
