package xlive.method.sys;

import javax.xml.xpath.XPathConstants;
import xlive.method.*;

public class xArgumentsMethod extends xDefaultMethod{
	public Object process() throws xMethodException{
		String operation=this.getMethodAttribute("operation");
		if(operation==null||operation.trim().length()==0)operation="overwrite";
		logMessage("xArgumentsMethod : "+this.evaluate("./parent::method/@name", getMethodNode(), XPathConstants.STRING));
		getServiceContext().argumentsOperation(getMethodNode(), operation, true);
		return getServiceContext().doNextProcess();
	}
}
