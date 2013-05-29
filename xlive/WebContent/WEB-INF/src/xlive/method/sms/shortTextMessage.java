package xlive.method.sms;

import java.io.File;

public class shortTextMessage {
	private File file;
	private long orderTime;
	
	public shortTextMessage(File file, long order_time){
		this.file=file;
		this.orderTime=order_time;
	}
	public File getFile(){
		return file;
	}
	public long getOrderTime(){
		return orderTime;
	}
}
