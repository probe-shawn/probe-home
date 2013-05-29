package xlive.method.logger;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.*;

public class xLogFormatter extends Formatter{
	
    private Date date = new Date();
	private SimpleDateFormat simpleDateFormat=new SimpleDateFormat("HH:mm:ss.SSSS");
    private String lineSeparator = (String) System.getProperty("line.separator");

    public synchronized String format(LogRecord record) {
    	StringBuffer sb = new StringBuffer();
    	String message=record.getMessage();
    	Object[] params=record.getParameters();
    	Boolean timestamp=true;
    	Integer deep=0;
    	if(params != null && params.length > 0){
    		if(params[0] instanceof Integer)deep=(Integer)params[0];
    		//if(params.length>1)timestamp=(Boolean)params[1];
    	}
		if(timestamp){
			date.setTime(record.getMillis());
			sb.append("");
			sb.append(simpleDateFormat.format(date));
			sb.append(" ");
		}else sb.append("              ");
		//
		while(deep-->1) sb.append("  ");
    	if(message != null && message.length() > 0){
    		sb.append(":");
    		sb.append(message);
    	}else{
    		sb.append(record.getSourceClassName());
    		sb.append(".");
    		sb.append(record.getSourceMethodName());
    	}
    	sb.append(lineSeparator);
    	if (record.getThrown() != null) {
    	    try {
    	        StringWriter sw = new StringWriter();
    	        PrintWriter pw = new PrintWriter(sw);
    	        record.getThrown().printStackTrace(pw);
    	        pw.close();
    		sb.append(sw.toString());
    	    } catch (Exception ex) {
    	    }
    	}
    	return sb.toString();
        }
}
