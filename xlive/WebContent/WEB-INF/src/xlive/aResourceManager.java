package xlive;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;

import javax.servlet.ServletContext;

public class aResourceManager implements xContextObjectImp{
	
	private final String Tag = "aResourceManager";
	private static ServletContext servletContext;
	
	public aResourceManager(ServletContext servlet_context) {
		servletContext = servlet_context;
		servletContext.setAttribute(Tag, this);
		initial();
	}
	public boolean initial(){
		return true;
	}
	public void destroy(){
		servletContext=null;
	}
	public String getDescription(){
		return Tag;
	}
	public static InputStream getResourceAsStream(String res_path){
		return servletContext.getResourceAsStream(res_path);
	}
	public static String getRealPath(String path){
		return servletContext.getRealPath(path);
	}
	public static URL getResource(String path){
		try {
			return servletContext.getResource(path);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return null;
	}
	public static Set<?> getResourcePaths(String path){
		return servletContext.getResourcePaths(path);
	}
}
