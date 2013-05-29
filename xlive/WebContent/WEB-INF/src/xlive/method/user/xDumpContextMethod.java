package xlive.method.user;

import org.w3c.dom.Element;

import xlive.xWebInformation;
import xlive.method.*;

public class xDumpContextMethod extends xDefaultMethod{

	public Object process() throws xMethodException{
		boolean valid=true;
		String why="";
		Element data=this.setReturnArguments("data","");
		//data.appendChild(xWebInformation.getContextSessionNode(this.getServiceContext().getSessionId()).cloneNode(true));
		setReturnArguments("valid", String.valueOf(valid));
		setReturnArguments("why", why);
		return getServiceContext().doNextProcess();
	}
}
