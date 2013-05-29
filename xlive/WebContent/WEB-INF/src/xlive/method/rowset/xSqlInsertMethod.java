package xlive.method.rowset;
import java.sql.*;

import javax.xml.xpath.XPathConstants;
import org.w3c.dom.*;

import xlive.xProbeServlet;
import xlive.method.*;
import java.util.*;

public class xSqlInsertMethod extends xDefaultMethod{

	public Object process() throws xMethodException{
		if(xProbeServlet.isGAE()){
			String redirect_url = this.getProperties("gae-redirect-url");
			return processInternetWebObjectMethod(redirect_url, getObjectPath(), getMethodName());
		}

		String database_object_name=getProperties("database-object-name");
		Connection connection_instance = (Connection)getWebObjectPropertiesObject(database_object_name, "connection-instance");
		if(connection_instance == null)	throw createMethodException("connection-instance", "not found");
		//
		/*
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
		*/
		//
		PreparedStatement statement=null;
		ResultSet result_set=null;
		try{
			Vector<Object> param_vector=new Vector<Object>();
			String data_type=null;
			String value=null;
			Boolean is_null=false;
			StringBuffer command=new StringBuffer("insert into ");
			String table=getArguments("table");
			command.append(table).append(" (");
			StringBuffer command2=new StringBuffer(" values (");
			NodeList params_nodelist=(NodeList)getArguments("fields/child::*", XPathConstants.NODESET);
			NodeList constrain_params_nodelist=(NodeList)getArguments("fields-constrain/child::*", XPathConstants.NODESET);
			boolean add_coma=false;
			if(constrain_params_nodelist.getLength() > 0){
				for(int i=0; i<constrain_params_nodelist.getLength(); ++i){
					if(constrain_params_nodelist.item(i).getNodeType() != Node.ELEMENT_NODE)continue;
					Element constrain_field=(Element)constrain_params_nodelist.item(i);
					data_type=constrain_field.getAttribute("dataType");
					if("static".equals(data_type)){
						if(add_coma){
							command.append(" , ");
							command2.append(" , ");
						}
						add_coma=true;
						command.append(constrain_field.getNodeName());
						command2.append(" ").append(constrain_field.getTextContent()).append(" ");
						continue;
					}
					//insert field only in constrain
					value=null;
					for(int j=0; j<params_nodelist.getLength(); ++j){
						if(params_nodelist.item(j).getNodeType() != Node.ELEMENT_NODE)continue;
						Element data_field=(Element)params_nodelist.item(j);
						if(data_field.getNodeName()==constrain_field.getNodeName()){
							value=data_field.getTextContent();
							break;
						}
					}
					if(value==null)continue;
					//
					if(add_coma){
						command.append(" , ");
						command2.append(" , ");
					}
					add_coma=true;
					command.append(constrain_field.getNodeName());
					command2.append(" ? ");
					String tmp=constrain_field.getAttribute("isNull");
					is_null=("1".equals(tmp) || "true".equalsIgnoreCase(tmp));
					param_vector.add(new Object[]{data_type, value, is_null});
				}
			}else{
				for(int i=0; i<params_nodelist.getLength(); ++i){
					if(params_nodelist.item(i).getNodeType() != Node.ELEMENT_NODE)continue;
					if(i != 0){
						command.append(" , ");
						command2.append(" , ");
					}
					Element field=(Element)params_nodelist.item(i);
					command.append(field.getNodeName());
					command2.append(" ? ");
					data_type=field.getAttribute("dataType");
					value=field.getTextContent();
					String tmp=field.getAttribute("isNull");
					is_null=("1".equals(tmp) || "true".equalsIgnoreCase(tmp));
					param_vector.add(new Object[]{data_type, value, is_null});
				}
			}
			
			command.append(")").append(command2).append(")");
			//statement=connection_instance.prepareStatement(command.toString(),resultset_type,resultset_concurrency,resultset_holdability);
			statement=connection_instance.prepareStatement(command.toString(),Statement.RETURN_GENERATED_KEYS);
			
			for(int i=0; i< param_vector.size(); ++i){
				Object[] params=(Object[])param_vector.get(i);
				xSqlUtility.setParamters(statement, i+1, (String)params[0], (String)params[1], (Boolean)params[2]);
			}
			int result=statement.executeUpdate();
			Element return_data=setReturnArguments("data", "");
			result_set=statement.getGeneratedKeys();
			if(result_set != null){
				if(result_set.next()) {
					String record_tag_name=getArguments("record-tag-name");
					Element record_tag_name_node=createElement(record_tag_name);
					return_data.appendChild(record_tag_name_node);
					ResultSetMetaData rsmd=result_set.getMetaData();
					int column_count=rsmd.getColumnCount();
					for(int i=0; i<column_count;++i){
						Element column_node=createElement(rsmd.getColumnName(i+1));
						column_node.setTextContent(result_set.getString(i+1));
						record_tag_name_node.appendChild(column_node);
					}
				}
				result_set.close();
				result_set=null;
			}else{
				return_data.setTextContent(String.valueOf(result));
			}
			statement.close();
			statement=null;
			return getServiceContext().doNextProcess();
		}catch(SQLException se){
			se.printStackTrace();
			throw createMethodException("SQLException", se.getLocalizedMessage(), String.valueOf(se.getErrorCode()), se.getSQLState());
		}finally{
			try{
				if(result_set != null)result_set.close();
				if(statement != null)statement.close();
			}catch(Exception eignor){}
		}
	}
}
