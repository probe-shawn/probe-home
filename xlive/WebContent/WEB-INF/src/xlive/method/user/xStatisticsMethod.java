package xlive.method.user;

import xlive.xWebInformation;
import xlive.method.*;

public class xStatisticsMethod extends xDefaultMethod{

	public Object process() throws xMethodException{
		boolean valid=true;
		String why="";
		System.gc();
		StringBuffer buf = new StringBuffer();
		long free = Runtime.getRuntime().freeMemory();
		long total = Runtime.getRuntime().totalMemory();
		long max = Runtime.getRuntime().maxMemory();
		float f1 = (float) ((total-free)/100000l) / 10;
		float f2 = (float) (total/100000l) / 10;
		float f3 = (float) (max/100000l) / 10;
		buf.append(f1).append("MB used, ").append(f2).append("MB allocated, ").append(f3).append("MB max");
		this.setReturnArguments("data.memory",buf.toString());
		setReturnArguments("valid", String.valueOf(valid));
		setReturnArguments("why", why);
		return getServiceContext().doNextProcess();
	}
}
