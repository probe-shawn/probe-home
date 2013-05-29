package xlive;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;

import javax.servlet.ServletContext;
import org.json.JSONException;
import org.json.JSONObject;
import xlive.google.ds.xHash;

public class aServerConfig implements xContextObjectImp {
	
	private final String Tag = "aServerConfig";
	private ServletContext servletContext;
	
	private static String serverCode=null;
	private static JSONObject serverJSON=new JSONObject();
	//
	public aServerConfig(ServletContext servlet_context) {
		servletContext = servlet_context;
		servletContext.setAttribute(Tag, this);
		initial();
	}
	public boolean initial(){
		try{
			return initialize();
		}catch(aSystemException e){
			aSystemException.logSystemException(e);
		}
		return false;
	}
	private boolean initialize() throws aSystemException {
		getServerCode();
		InputStream server_dot_stream=servletContext.getResourceAsStream("/WEB-INF/$.json");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		xUtility.copyStream(server_dot_stream, baos);
		try {
			serverJSON = new JSONObject(baos.toString());
		} catch (JSONException e) {
			e.printStackTrace();
			throw new aSystemException("serverConfig $.xml", "serverConfig", "exception :"+e.getLocalizedMessage());
		}
		return true;
	}
	public void destroy(){
		servletContext=null;
		dispose();
	}
	public String getDescription(){
		return Tag;
	}
	public static void dispose(){
	}
	public static xServiceContext getServiceContext() throws xSystemException{
		return  new xServiceContext(null);
	}
	
	public static JSONObject getConfigInformtion(){
		return serverJSON;
	}
	public static String getServerCode(){
		return getServerCode(false);
	}
	public static String getServerCode(boolean rebuild){
		if(xProbeServlet.isGAE()) {
			if(serverCode != null && !rebuild) return serverCode;
			xHash hash = new xHash("_server");
			serverCode = hash.getString("serverCode");
			if(serverCode != null && !rebuild) 	return serverCode;
			serverCode = String.valueOf(System.currentTimeMillis());
			hash.putString("serverCode", serverCode);
			return serverCode;
		}
		if(serverCode != null && !rebuild) return serverCode;
		try{
			File lib_dir=new File(System.getProperty("java.ext.dirs").split(";")[0]).getParentFile();
			File f1= new File(lib_dir, "sound.properties");
			File f2= new File(lib_dir, "calendars.properties");
			File f3= new File(lib_dir, "flavormap.properties");
			File f4= new File(lib_dir, "content-types.properties");
			File f5= new File(lib_dir, "logging.properties");
			File f6= new File(lib_dir, "net.properties");
			long time1=f1.lastModified();
			long time2=f2.lastModified();
			long time3=f3.lastModified();
			long time4=f4.lastModified();
			long time5=f5.lastModified();
			long time6=f6.lastModified();
			long code=0;
			if(time1%1000==111 && time2%100==99 && !rebuild){
				code=(time2%1000)/100;
				code=code*1000+time3%1000;
				code=code*1000+time4%1000;
				code=code*1000+time5%1000;
				code=code*1000+time6%1000;
			}else{
				code=System.currentTimeMillis();
				String scode=String.valueOf(code);
				f1.setLastModified((time1/1000)*1000+111);
				long sec=Integer.parseInt(scode.substring(0, 1));
				f2.setLastModified((time2/1000)*1000+sec*100+99);
				sec=Integer.parseInt(scode.substring(1, 4));
				f3.setLastModified((time3/1000)*1000+sec);
				sec=Integer.parseInt(scode.substring(4, 7));
				f4.setLastModified((time4/1000)*1000+sec);
				sec=Integer.parseInt(scode.substring(7, 10));
				f5.setLastModified((time5/1000)*1000+sec);
				sec=Integer.parseInt(scode.substring(10));
				f6.setLastModified((time6/1000)*1000+sec);
			}
			serverCode=String.valueOf(code);
			return serverCode;
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}

}
