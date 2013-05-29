package xlive.method.rowset;
import java.sql.*;

import javax.xml.xpath.XPathConstants;
import org.w3c.dom.*;

import xlive.xProbeServlet;
import xlive.method.*;
import java.util.*;


public class xSqlDeleteMethod extends xDefaultMethod{

	public Object process() throws xMethodException{
		if(xProbeServlet.isGAE()){
			String redirect_url = this.getProperties("gae-redirect-url");
			return processInternetWebObjectMethod(redirect_url, getObjectPath(), getMethodName());
		}
		String database_object_name=getProperties("database-object-name");
		Connection connection_instance = (Connection)getWebObjectPropertiesObject(database_object_name, "connection-instance");
		if(connection_instance == null)	throw createMethodException("connection-instance", "not found");
		//
		int resultset_type=ResultSet.TYPE_FORWARD_ONLY;
		try{
			String type=getProperties("resultset.type");
			if(type != null && type.trim().length() > 0)resultset_type=Integer.parseInt(type);
		}catch(Exception e){}
		int resultset_concurrency=ResultSet.CONCUR_READ_ONLY;
		try{
			String concurrency=getProperties("resultset.concurrency");
			if(concurrency != null && concurrency.trim().length() > 0)resultset_concurrency=Integer.parseInt(concurrency);
		}catch(Exception e){}
		int resultset_holdability=ResultSet.HOLD_CURSORS_OVER_COMMIT;
		try{
			String holdability=getProperties("resultset.holdability");
			if(holdability != null && holdability.trim().length() > 0)resultset_holdability=Integer.parseInt(holdability);
		}catch(Exception e){}
		//
		PreparedStatement statement=null;
		try{
			Vector<Object> param_vector=new Vector<Object>();
			String sql_type=null;
			String value=null;
			Boolean is_null=false;
			StringBuffer command=new StringBuffer("delete from ");
			String table_name=(String)getArguments("table");
			command.append(table_name).append(" where ");
			NodeList params_nodelist=(NodeList)this.getArguments("key-fields/child::*", XPathConstants.NODESET);
			for(int i=0; i<params_nodelist.getLength(); ++i){
				if(params_nodelist.item(i).getNodeType() != Node.ELEMENT_NODE)continue;
				if(i!=0) command.append(" and ");
				Element field=(Element)params_nodelist.item(i);
				command.append(field.getNodeName()).append(" = ? ");
				sql_type=field.getAttribute("dataType");
				value=field.getTextContent();
				String tmp=field.getAttribute("isNull");
				is_null=("1".equals(tmp) || "true".equalsIgnoreCase(tmp));
				param_vector.add(new Object[]{sql_type, value, is_null});
			}
			statement=connection_instance.prepareStatement(command.toString(),resultset_type,resultset_concurrency,resultset_holdability);
			for(int i=0; i< param_vector.size(); ++i){
				Object[] params=(Object[])param_vector.get(i);
				xSqlUtility.setParamters(statement, i+1, (String)params[0], (String)params[1], (Boolean)params[2]);
			}
			int result=statement.executeUpdate();
			setReturnArguments("data", String.valueOf(result));
			statement.close();
			statement=null;
			return getServiceContext().doNextProcess();
		}catch(SQLException se){
			se.printStackTrace();
			throw createMethodException("SQLException", se.getLocalizedMessage(), String.valueOf(se.getErrorCode()), se.getSQLState());
		}finally{
			try{
				if(statement != null)statement.close();
			}catch(Exception eignor){}
		}
	}
}
