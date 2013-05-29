package xlive.method.logger;


import xlive.method.xDefaultMethod;
import xlive.method.xMethodException;

public class xUnlogUserMethod  extends xDefaultMethod{
	public Object process()throws xMethodException{
		boolean valid=true;
		String why="";
		String session_id=getArguments("session-id");
		if(session_id == null || session_id.trim().length()==0)session_id=this.getServiceContext().getSessionId();
		xLogger.removeUser(session_id);
		setReturnArguments("valid", String.valueOf(valid));
		setReturnArguments("why", why);
		return getServiceContext().doNextProcess();
	}
}
