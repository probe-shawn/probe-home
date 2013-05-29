package xlive.method.sms.provider.rxtx;

import xlive.method.*;
import gnu.io.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.TooManyListenersException;

public class xCreateConnectionMethod extends xDefaultMethod{
	/*
	public static Integer connectionCount = new Integer(0);
	
	public Object process() throws xMethodException{
		
		boolean valid=true;
		String why="";
		int wait_timeout=5000;
		try{
			wait_timeout=Integer.parseInt(getArguments("wait-timeout"));
			wait_timeout= wait_timeout<=1000 ? 5000 : wait_timeout;
		}catch(Exception e){}
		int maximum_connection=1;
		try{
			maximum_connection=Integer.parseInt(getProperties("maximum-connection"));
		}catch(Exception e){}
		//
		synchronized(connectionCount){
			try{
				if(connectionCount>=maximum_connection) connectionCount.wait(wait_timeout);
				if(connectionCount>=maximum_connection) throw this.createMethodException("xCreateConnectionMethod", "timeout");
				++connectionCount;
			}catch(InterruptedException e){
				getServiceContext().doNextProcess(false);
				throw this.createMethodException("xCreateConnectionMethod", "InterruptedException");
			}
		}
		String port_name=getArguments("port-name");
		port_name=(port_name==null||port_name.trim().length()==0)? "COM1":port_name;
		//port existed
		if(getPropertiesObject("connection-instance") != null) 
			return getServiceContext().doNextProcess();
		//
		int baud_rate=57600;
		try{
			baud_rate=Integer.parseInt(getArguments("baud-rate"));
		}catch(Exception e){}
		int data_bits=SerialPort.DATABITS_8;
		try{
			data_bits=Integer.parseInt(getArguments("data-bits"));
		}catch(Exception e){}
		int stop_bits=SerialPort.STOPBITS_1;
		try{
			stop_bits=Integer.parseInt(getArguments("stop-bits"));
		}catch(Exception e){}
		int parity=SerialPort.PARITY_NONE;
		try{
			parity=Integer.parseInt(getArguments("parity"));
		}catch(Exception e){}
		//
		CommPort comm_port=null;
		try{
			CommPortIdentifier port_identifier = CommPortIdentifier.getPortIdentifier(port_name);
			if(port_identifier.isCurrentlyOwned()){
				logMessage("Error: Port is currently in use");
			}else {
				///just test remark 
				//comm_port=port_identifier.open(this.getClass().getName(),2000);
				//if(comm_port instanceof SerialPort ) {
					SerialPort serial_port = (SerialPort) comm_port;
					// just test remark
					//serial_port.setSerialPortParams(baud_rate,data_bits,stop_bits,parity);
					////
					setPropertiesObject("connection-instance", serial_port);
					String event_based_read=getArguments("event-based-read");
					//just test add this one
					event_based_read="false";
					//
					if("true".equalsIgnoreCase(event_based_read)){
						String control_bytes=getArguments("data-block.control-bytes");
						//
						boolean	include_control_bytes="true".equalsIgnoreCase(getArguments("data-block.include-control-bytes"));
						int control_by_length=0;
						try{
							control_by_length=Integer.parseInt(getArguments("data-block.control-by-length"));
						}catch(Exception e){}
						
						SerialReader serial_read=new SerialReader(serial_port.getInputStream(),control_bytes,include_control_bytes,control_by_length);
						serial_port.addEventListener(serial_read);
						serial_port.notifyOnDataAvailable(true);
						setPropertiesObject("reader-instance", serial_read);
					}
					logMessage("serial port connection OK");
					return getServiceContext().doNextProcess();
				///just for test	
				//}else {
				//	logMessage("Error: Only serial ports are handled by this example.");
				//}
				//
			}
		}catch(NoSuchPortException nspe){
			valid=false;
			logMessage("Error: NoSuchPortException");
			nspe.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}catch(TooManyListenersException tmle){
			tmle.printStackTrace();
		}
		
		try{
			if(comm_port!=null) comm_port.close();
		}catch(Exception eall){}
		return false;
	}
	public class SerialReader implements SerialPortEventListener {
        private InputStream serialInput;
        //
        private String controlBytes;
        private int controlByLength=0;
        private boolean includeControlBytes=true;
        //
        private LinkedList<byte[]> dataQueue;
        private ByteArrayOutputStream bufferStream;
        int times=0;
        public SerialReader(InputStream input_stream, String control_bytes, boolean include_control_bytes, int control_by_length){
        	serialInput=input_stream;
        	controlBytes=control_bytes;
        	dataQueue = new LinkedList<byte[]>();
        	bufferStream=new ByteArrayOutputStream(1024);
        }
        public void serialEvent(SerialPortEvent arg0){
            int data;
            int match_length=controlBytes.length();
            int match_index=0;
            logMessage("serialEvent :"+(++times));
            try {
            	int line=0;
                while((data = serialInput.read()) > -1){
                	bufferStream.write((byte) data);
                    if(data==(int)controlBytes.charAt(match_index)) {
                    	++match_index;
                    	if(match_index>=match_length){
                    		byte[] data_bytes=bufferStream.toByteArray();
                    		if(!includeControlBytes) data_bytes[data_bytes.length-match_length]=0;
                    		if(data_bytes.length>0) {
                    			synchronized(dataQueue){ 
                    				dataQueue.add(data_bytes);
                    			}
                    		}
                    		bufferStream.reset();
                    		match_index=0;
                    		//
                    		//byte[] test=this.readData();
                    		//logMessage("["+new String(test)+"]"+"databytelength:"+test.length);
                    		//
                    	}
                    }else match_index=0;
                }
            }catch(IOException e){
                e.printStackTrace();
            }             
        }
        public byte[] readData(){
        	synchronized(dataQueue){ 
        		return dataQueue.remove();
        	}
        }
        public byte[][] readAllData(){
        	synchronized(dataQueue){ 
        		int size=dataQueue.size();
        		byte[][] datas=new byte[size][];
        		for(int i=0;i<size;++i)datas[i]=dataQueue.remove();
        		return datas;
        	}
        }
        public int readDataCount(){
        	synchronized(dataQueue){ 
        		return dataQueue.size();
        	}
        }
    }
	*/
	
}
