package xlive.method.logger;

import java.util.logging.*;

import xlive.method.xDefaultMethod;
import xlive.method.xMethodException;

public class xSetXliveObjectLoggerLevelMethod  extends xDefaultMethod{
	public Object process()throws xMethodException{
		boolean valid=true;
		String why="";
		String level=this.getArguments("level");
		try{
			Level log_level=Level.parse(level);
			xLogger.setXliveObjectLoggerLevel(log_level);
		}catch(Exception e){
			valid=false;
			why=e.getMessage();
		}
		setReturnArguments("valid", String.valueOf(valid));
		setReturnArguments("why", why);
		return getServiceContext().doNextProcess();
	}
}
