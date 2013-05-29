package xlive.wrap;

import java.net.InetAddress;
import java.net.UnknownHostException;
/*
 *  wrap fileOutputStream for exclude from GAE
 */
public class xWrapInetAddress {
	
	public static String getHostAddress() throws UnknownHostException{
		return InetAddress.getLocalHost().getHostAddress();
	}
	public static String getHostName() throws UnknownHostException {
		return InetAddress.getLocalHost().getHostName();
	}
}
