package xlive.method.db.informix;

import java.lang.reflect.InvocationTargetException;
import javax.servlet.http.HttpServletResponse;
import org.w3c.dom.*;

import xlive.xProbeServlet;
import xlive.method.*;

public class xCreateDataSourceMethod extends xDefaultMethod{
	
	public Object process() throws xMethodException{
		if(xProbeServlet.isGAE()){
			return getServiceContext().doNextProcess();
		}

		String message=null;
		String cause_site=null;
		try{  
			String ds_class_name=getArguments("class-name");
			Class class_for_name=Class.forName(ds_class_name.trim());
			Object datasource=class_for_name.newInstance();
			//
			String ifx_host=getArguments("ifx-host");
			java.lang.reflect.Method reflect_method = class_for_name.getMethod("setIfxIFXHOST", String.class);
			reflect_method.invoke(datasource, ifx_host.trim());
			//
			String port_number=getArguments("port-number");
			
			reflect_method = class_for_name.getMethod("setPortNumber", int.class);
			reflect_method.invoke(datasource, Integer.parseInt(port_number));
			//
			String server_name=getArguments("server-name");
			reflect_method = class_for_name.getMethod("setServerName", String.class);
			reflect_method.invoke(datasource, server_name.trim());
			//
			String database_name=getArguments("database-name");
			reflect_method = class_for_name.getMethod("setDatabaseName", String.class);
			reflect_method.invoke(datasource, database_name.trim());
			//
			String user=getArguments("user");
			reflect_method = class_for_name.getMethod("setUser", String.class);
			reflect_method.invoke(datasource, user.trim());
			//
			String password=getArguments("password");
			reflect_method = class_for_name.getMethod("setPassword", String.class);
			reflect_method.invoke(datasource, password.trim());
			//
			//setPropertiesObject("datasource-instance", datasource);
			//
			logMessage("datasource OK");
			
/*			
			Node methods_node=(Node)getArgumentsOrProperties("datasource.methods", XPathConstants.NODE);
			NodeList method_nodelist=(NodeList)evaluate("./child::*", methods_node, XPathConstants.NODESET);
			for(int i=0; i< method_nodelist.getLength();++i){
				Element method_node=(Element)method_nodelist.item(i);
				String method_name=method_node.getAttribute("name");
				NodeList param_nodelist=(NodeList)evaluate("./child::*", method_node, XPathConstants.NODESET);
				Class[] param_classes=new Class[param_nodelist.getLength()];
				Object[] param_objects=new Object[param_nodelist.getLength()];
				for(int p=0; p < param_nodelist.getLength(); ++p){
					Element param_node=(Element)param_nodelist.item(p);
					String param_type = param_node.getAttribute("type");
					String param_value= param_node.getTextContent();
					//????????
					param_classes[p]=Class.forName(param_type);
					////
					java.lang.reflect.Constructor p_c = param_classes[p].getConstructor(String.class);
					param_objects[p]=p_c.newInstance(param_value);
				}
				java.lang.reflect.Method method_set_url = class_for_name.getMethod(method_name, param_classes);
				method_set_url.invoke(datasource, param_objects);
			}
*/			
/*			
			///////////
			String ds_url=getArgumentsOrProperties("datasource.url");
			java.lang.reflect.Method method_set_url = class_for_name.getMethod("setURL", String.class);
			method_set_url.invoke(datasource, ds_url.trim());
			//
			String ds_user=getArgumentsOrProperties("datasource.user");
			if(ds_user != null && ds_user.trim().length() > 0){
				System.out.println("user :"+ds_user);
				java.lang.reflect.Method method_set_user = class_for_name.getMethod("setUser", String.class);
				method_set_user.invoke(datasource, ds_user);
			}
			String ds_password=getArgumentsOrProperties("datasource.password");
			if(ds_password != null && ds_user.trim().length() > 0){
				System.out.println("password :"+ds_password);
				java.lang.reflect.Method method_set_pass = class_for_name.getMethod("setPassword", String.class);
				method_set_pass.invoke(datasource, ds_password);
			}
			String datasource_pool_name=getMethodArguments("datasource.datasource-pool-name");
			Element datasource_object_node=argumentsOperation(datasource_pool_name+".ds-object","", "append");
			String ds_object_name=getObjectPath(".");
			datasource_object_node.setAttribute("name", ds_object_name);
			datasource_object_node.setUserData("object", datasource, null);
			System.out.println("datasource OK :"+ds_object_name);
*/			
			return getServiceContext().doNextProcess();
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
}
