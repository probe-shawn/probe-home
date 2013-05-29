package xlive.method.rowset;

import java.sql.*;

import javax.xml.xpath.XPathConstants;
import org.w3c.dom.*;

import xlive.xProbeServlet;
import xlive.xWebInformation;
import xlive.method.*;
import xlive.method.rowset.xSqlUtility;

public class xSqlStatementMethod extends xDefaultMethod{

	
	public Object process() throws xMethodException{
		if(xProbeServlet.isGAE()){
			String redirect_url = this.getProperties("gae-redirect-url");
			return processInternetWebObjectMethod(redirect_url, getObjectPath(), getMethodName());
		}
		String database_object_name=getProperties("database-object-name");
		Connection connection_instance = (Connection)getWebObjectPropertiesObject(database_object_name, "connection-instance");
		if(connection_instance == null)	throw createMethodException("connection-instance", "not found");
		//
		int resultset_type=ResultSet.TYPE_SCROLL_INSENSITIVE;
		/*
		try{
			String type=getProperties("resultset.type");
			if(type != null && type.trim().length() > 0)resultset_type=Integer.parseInt(type);
		}catch(Exception e){}
		*/
		int resultset_concurrency=ResultSet.CONCUR_READ_ONLY;
		/*
		try{
			String concurrency=getProperties("resultset.concurrency");
			if(concurrency != null && concurrency.trim().length() > 0)resultset_concurrency=Integer.parseInt(concurrency);
		}catch(Exception e){}
		*/
		/*
		int resultset_holdability=ResultSet.HOLD_CURSORS_OVER_COMMIT;
		try{
			String holdability=getProperties("resultset.holdability");
			if(holdability != null && holdability.trim().length() > 0)resultset_holdability=Integer.parseInt(holdability);
		}catch(Exception e){}
		*/
		//
		PreparedStatement statement=null;
		ResultSet result_set=null;
		String command=null;
		try{
			command=getArguments("command").trim();
			boolean is_select=false;
			boolean is_insert=false;
			if(command.length() > 6){
				is_select=command.substring(0, 6).toLowerCase().startsWith("select");
				is_insert=command.substring(0, 6).toLowerCase().startsWith("insert");
			}
			//
			String sql_type=null;
			String value=null;
			Boolean is_null=false;
			statement=(is_insert)?
				connection_instance.prepareStatement(command.toString(),Statement.RETURN_GENERATED_KEYS):
				//connection_instance.prepareStatement(command,resultset_type,resultset_concurrency,resultset_holdability);
				(is_select)?
				connection_instance.prepareStatement(command,resultset_type,resultset_concurrency):
				connection_instance.prepareStatement(command);
			//
			NodeList params_nodelist=(NodeList)getArguments("params/child::*", XPathConstants.NODESET);
			///
			////
			for(int i=0; i<params_nodelist.getLength(); ++i){
				if(params_nodelist.item(i).getNodeType() != Node.ELEMENT_NODE)continue;
				Element field=(Element)params_nodelist.item(i);
				sql_type=field.getAttribute("dataType");
				value=field.getTextContent();
				String tmp=field.getAttribute("isNull");
				is_null=("1".equals(tmp) || "true".equalsIgnoreCase(tmp));
				setParamters(statement, i+1, sql_type, value, is_null);
			}
			if(is_select){
				result_set=statement.executeQuery();
				//JdbcRowSetImpl jdbc_rs = new JdbcRowSetImpl(result_set);
				Element return_data=setReturnArguments("data", "");
				if(return_data != null ){
					int row_fetch=20,row_from=1,row_real_fetch=0,row_maximum=0;
					boolean output_eof_rows="true".equalsIgnoreCase(getArguments("output-eof-rows"));
					try{
						row_fetch=Integer.parseInt(getArguments("row-fetch"));
					}catch(Exception e){}
					row_fetch=(row_fetch < 0) ? Integer.MAX_VALUE : (row_fetch == 0) ? 20 : row_fetch;
					try{
						row_from=Integer.parseInt(getArguments("row-from"));
					}catch(Exception e){}
					row_from=(row_from <= 0) ? 1 : row_from;
					try{
						row_maximum=Integer.parseInt(getArguments("row-maximum"));
					}catch(Exception e){}
					row_maximum=(row_maximum<0)?0:row_maximum;
					if(row_maximum > 0 && row_fetch>row_maximum) row_fetch=row_maximum;
					String record_tag_name=getArguments("record-tag-name");
					if(record_tag_name == null || record_tag_name.trim().length() ==0) record_tag_name="r";
					String convert_to_treeset=getArguments("convert-to-treeset");
					row_real_fetch=outputData(result_set, return_data, row_from, row_fetch, record_tag_name,convert_to_treeset);
					//
					return_data.setAttribute("rowTotal", String.valueOf(row_real_fetch));
					return_data.setAttribute("eof", "true");
					if(output_eof_rows && result_set.getType()!=ResultSet.TYPE_FORWARD_ONLY){
						int eof_rows=0;
						if(result_set.last()) eof_rows=result_set.getRow();
						return_data.setAttribute("eofRows", String.valueOf(eof_rows));
					}
				}
				String output_metadata=getArguments("output-metadata");
				if("true".equalsIgnoreCase(output_metadata)){
					Element return_metadata=setReturnArguments("metadata", "");
					if(return_metadata != null)	outputMetadata(result_set, return_metadata);
				}
			}else{
				int result=statement.executeUpdate();
				Element return_data=setReturnArguments("data", "");
				result_set=(is_insert) ?statement.getGeneratedKeys():null;
				if(result_set != null){
					if(result_set.next()) {
						String record_tag_name=getArguments("record_tag_name");
						if(record_tag_name==null||record_tag_name.trim().length()==0)record_tag_name="r";
						Element record_tag_name_node =createElement(record_tag_name);
						return_data.appendChild(record_tag_name_node);
						ResultSetMetaData rsmd=result_set.getMetaData();
						int column_count=rsmd.getColumnCount();
						for(int i=0; i<column_count;++i){
							Element column_node=xWebInformation.createElement(rsmd.getColumnName(i+1));
							column_node.setTextContent(result_set.getString(i+1));
							record_tag_name_node.appendChild(column_node);
						}
					}
				}else{
					return_data.setTextContent(String.valueOf(result));
				}
			}
			if(result_set != null) result_set.close();
			result_set=null;
			statement.close();
			statement=null;
			return getServiceContext().doNextProcess();
		}catch(SQLException se){
			se.printStackTrace();
			this.getServiceContext().dumpArguments();
			throw createMethodException("SQLException", se.getLocalizedMessage(), String.valueOf(se.getErrorCode()), se.getSQLState());
		}finally{
			try{
				if(result_set != null) result_set.close();
				if(statement != null)statement.close();
			}catch(Exception eignor){}
		}
	}
    protected void setParamters(PreparedStatement statement, int index, String data_type, String value, boolean is_null) throws SQLException{
		xSqlUtility.setParamters(statement, index, data_type, value, is_null);
    }
	protected int outputData(ResultSet resultset, Node result_node, int row_from, int row_fetch, String record_tag_name,String convert_to_treeset) throws SQLException{
		return xSqlUtility.outputDataTreeset(resultset, result_node, row_from, row_fetch, record_tag_name,convert_to_treeset);
	}
	protected boolean outputMetadata(ResultSet resultset, Node result_node) throws SQLException{
		return xSqlUtility.outputMetadata(resultset, result_node);
	}
}
