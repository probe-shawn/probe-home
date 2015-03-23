package xlive;

import java.util.Date;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;

public class aToken {
	public static String cookieName="yup.admin";
	public static String account="yup.admin@gmail.com";
	public static Entity create(HttpServletRequest request, HttpServletResponse response, String cookie_name){
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		Entity entity = new Entity("aToken");
		entity.setProperty("ip", request.getRemoteAddr());
		entity.setProperty("date", new Date());
		ds.put(entity);
		//
		Cookie cookie = new Cookie(cookie_name, KeyFactory.keyToString(entity.getKey()));
		cookie.setPath("/");
		cookie.setMaxAge(-1);
		response.addCookie(cookie);
		return entity;
	}
	public static void removeCookie(HttpServletRequest request, HttpServletResponse response, String cookie_name){
		Cookie[] cookies = request.getCookies();
		for(int i = 0; cookies != null && i <cookies.length;++i){
			if(cookies[i].getName().equals(cookie_name)) {
				cookies[i].setMaxAge(0);
				cookies[i].setValue(null);
				cookies[i].setPath("/");
				response.addCookie(cookies[i]);
			}
		}
	}
	public static Entity get(HttpServletRequest request, HttpServletResponse response, String cookie_name){
		String key=null;
		Cookie[] cookies = request.getCookies();
		for(int i = 0; cookies != null && i <cookies.length;++i)
			if(cookies[i].getName().equals(cookie_name)) {
				key = cookies[i].getValue();
			}
		
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		Entity entity = null;
		if(key != null){
			try {
				entity = ds.get(KeyFactory.stringToKey(key));
			} catch (EntityNotFoundException e) {
				entity = null;
			}
		}
		return entity;
	}

}
