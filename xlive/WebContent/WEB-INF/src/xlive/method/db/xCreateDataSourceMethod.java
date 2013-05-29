package xlive.method.db;

import java.lang.reflect.InvocationTargetException;

import javax.servlet.http.HttpServletResponse;
import org.w3c.dom.*;

import xlive.xProbeServlet;
import xlive.method.*;

public class xCreateDataSourceMethod extends xDefaultMethod{
	public Object process() throws xMethodException{
		return null;
		/*
		if(xProbeServlet.isGAE()){
			return getServiceContext().doNextProcess();
		}
		return synchronized_process();
		*/
	}
	/*
	private synchronized Object synchronized_process() throws xMethodException{
		String message=null;
		String cause_site=null;
		try{  
			String ds_class_name=getProperties("create-datasource.class-name");
			String ds_url=getProperties("create-datasource.url");
			Class class_for_name=Class.forName(ds_class_name.trim());
			//
			java.lang.reflect.Method method_set_url = class_for_name.getMethod("setURL", String.class);
			Object datasource=class_for_name.newInstance();
			method_set_url.invoke(datasource, ds_url.trim());
			//
			String ds_user=getArguments("user");
			if(ds_user != null && ds_user.trim().length() > 0){
				java.lang.reflect.Method method_set_user = class_for_name.getMethod("setUser", String.class);
				method_set_user.invoke(datasource, ds_user);
			}
			String ds_password=getArguments("password");
			if(ds_password != null && ds_user.trim().length() > 0){
				java.lang.reflect.Method method_set_pass = class_for_name.getMethod("setPassword", String.class);
				method_set_pass.invoke(datasource, ds_password);
			}
			setPropertiesObject("datasource-instance", datasource);
			logMessage(ds_class_name+" OK");
			return datasource;
		}catch(IllegalAccessException iac){
			iac.printStackTrace();
			message=iac.getMessage();
			cause_site="IllegalAccessException";
		}catch(ClassNotFoundException cnf){
			cnf.printStackTrace();
			message=cnf.getMessage();
			cause_site="ClassNotFoundException";
		}catch(InstantiationException ine){
			ine.printStackTrace();
			message=ine.getMessage();
			cause_site="InstantiationException";
		}catch(NoSuchMethodException nsm){
			nsm.printStackTrace();
			message=nsm.getMessage();
			cause_site="NoSuchMethodException";
		}catch(InvocationTargetException ivt){
			ivt.printStackTrace();
			message=ivt.getMessage();
			cause_site="InvocationTargetException";
		}
		throw createMethodException(cause_site, message);
	}
	*/
}
