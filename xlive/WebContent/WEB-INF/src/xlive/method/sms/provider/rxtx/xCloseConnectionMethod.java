package xlive.method.sms.provider.rxtx;

import xlive.method.*;
import gnu.io.*;

public class xCloseConnectionMethod extends xDefaultMethod{
	/*
	public Object process() throws xMethodException{
		String port_name=getQNameArguments("create-connection.port-name");
		port_name=(port_name==null||port_name.trim().length()==0)? "COM1":port_name;
		//port existed
		SerialPort serial_port = (SerialPort)getPropertiesObject("connection-instance");
		if(serial_port != null){
			serial_port.close();
			setPropertiesObject("connection-instance", null);
			logMessage("close serial port");
			try{
				synchronized(xCreateConnectionMethod.connectionCount){
					--xCreateConnectionMethod.connectionCount;
					xCreateConnectionMethod.connectionCount.notify();
				}
			}catch(IllegalMonitorStateException ims){
			}
		}
		setPropertiesObject("reader-instance", null);
		logMessage("remove reader-instance");
		return getServiceContext().doNextProcess();
	}
	*/
}
