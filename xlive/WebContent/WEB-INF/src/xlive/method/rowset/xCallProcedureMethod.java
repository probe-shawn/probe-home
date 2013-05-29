package xlive.method.rowset;

import java.sql.*;
import javax.xml.xpath.XPathConstants;
import org.w3c.dom.*;

import xlive.xProbeServlet;
import xlive.method.*;

public class xCallProcedureMethod extends xDefaultMethod{

	public Object process() throws xMethodException{
		if(xProbeServlet.isGAE()){
			String redirect_url = this.getProperties("gae-redirect-url");
			return processInternetWebObjectMethod(redirect_url, getObjectPath(), getMethodName());
		}

		String database_object_name=getProperties("database-object-name");
		Connection connection_instance = (Connection)getWebObjectPropertiesObject(database_object_name, "connection-instance");
		if(connection_instance == null)	throw createMethodException("connection-instance", "not found");
		//
		CallableStatement call_statement = null;
		ResultSet result_set=null;
		try{
			Element procedure=(Element)getArguments("name", XPathConstants.NODE);
			String procedure_name=getArguments("name");
			int column_return_type=DatabaseMetaData.procedureColumnUnknown;
			int procedure_type=DatabaseMetaData.procedureResultUnknown;
			try{
				procedure_type=Integer.parseInt(procedure.getAttribute("type"));
			}catch(Exception e){}
			
	        StringBuffer command = new StringBuffer();
	        command.append("{call ").append(procedure_name);
			NodeList columns=(NodeList)getArguments("columns/child::*", XPathConstants.NODESET);
	        int columns_count=0;
	        for(int i=0;i<columns.getLength();++i){
	        	Element column=(Element)columns.item(i);
	        	int column_type=DatabaseMetaData.procedureColumnUnknown;
	        	try{
	        		column_type=Integer.parseInt(column.getAttribute("type"));
	        	}catch(Exception e){}
	        	if("return-column".equals(column.getNodeName())) {
	        		column_return_type=column_type;
	        		continue;
	        	}
	            switch(column_type) {
	                case DatabaseMetaData.procedureColumnIn : // 1
	                case DatabaseMetaData.procedureColumnInOut : //2
	                case DatabaseMetaData.procedureColumnOut : //4
	                      if(columns_count == 0) command.append("(?");
	                      else command.append(", ?");
	                      ++columns_count;
	                      break;
	                case DatabaseMetaData.procedureColumnResult : //3
	                case DatabaseMetaData.procedureColumnUnknown : //0
	                case DatabaseMetaData.procedureColumnReturn : //5
	                      break;
	            }
	        }
	        if(columns_count != 0) command.append(")}");
	        else command.append("}");
	        //
	        call_statement = connection_instance.prepareCall(command.toString());
            //
            int index = 1;
	        for(int i=0;i<columns.getLength();++i){
	        	Element column=(Element)columns.item(i);
	        	if("return-column".equals(column.getNodeName())) continue;
	        	int column_type=DatabaseMetaData.procedureColumnUnknown;
	        	try{
	        		column_type=Integer.parseInt(column.getAttribute("type"));
	        	}catch(Exception e){}
	        	int data_type=Integer.parseInt(column.getAttribute("dataType"));
	        	//
                switch(column_type) {
                	case DatabaseMetaData.procedureColumnUnknown :
                    case DatabaseMetaData.procedureColumnResult :
                    case DatabaseMetaData.procedureColumnReturn :
                        break;
                    case DatabaseMetaData.procedureColumnIn :
                    	setParameters(call_statement,index++, String.valueOf(data_type), column.getTextContent(), false);               		
                        break;
                    case DatabaseMetaData.procedureColumnInOut :
                        if(data_type == java.sql.Types.NUMERIC || data_type == java.sql.Types.DECIMAL){
                        	String scale=column.getAttribute("scale");
                        	int column_scale=0;
                        	try{column_scale=Integer.parseInt(scale);}catch(Exception e){}
                        	call_statement.registerOutParameter(index, data_type, column_scale);
                        }else  call_statement.registerOutParameter(index, data_type);
                        ++index;
                        break;
                    case DatabaseMetaData.procedureColumnOut :
                        if(data_type == java.sql.Types.NUMERIC || data_type == java.sql.Types.DECIMAL){
                          	String scale=column.getAttribute("scale");
                      		int column_scale=0;
                      		try{column_scale=Integer.parseInt(scale);}catch(Exception e){}
                      		call_statement.registerOutParameter(index, data_type, column_scale);
                        }else  call_statement.registerOutParameter(index, data_type);
                        ++index;
                        break;
                  }
            }
            ///
	        int update_return=0;
            if(procedure_type == DatabaseMetaData.procedureReturnsResult ||column_return_type==DatabaseMetaData.procedureColumnResult)
            	result_set = call_statement.executeQuery();
            else update_return=call_statement.executeUpdate();
            // 
            Element return_data=setReturnArguments("data", "");
            index = 1;
            for(int i=0;i<columns.getLength();++i){
	        	Element column=(Element)columns.item(i);
	        	if("return-column".equals(column.getNodeName())) continue;
	        	int column_type=DatabaseMetaData.procedureColumnUnknown;
	        	try{
	        		column_type=Integer.parseInt(column.getAttribute("type"));
	        	}catch(Exception e){}
	        	int data_type=Integer.parseInt(column.getAttribute("dataType"));
                switch(column_type) {
                    case DatabaseMetaData.procedureColumnInOut :
                    case DatabaseMetaData.procedureColumnOut :
                    	String value=getOutParameters(call_statement, data_type, index);
                    	Element out_parameter=createElement(column.getNodeName(), value);
                    	return_data.appendChild(out_parameter);
                        ++index;
                        break;
                    case DatabaseMetaData.procedureColumnIn :
                        ++index;
                        break;
                    case DatabaseMetaData.procedureColumnUnknown :
                    case DatabaseMetaData.procedureColumnResult :
                    case DatabaseMetaData.procedureColumnReturn :
                        break;
                    }
                }
            if(result_set != null){
            	outputData(result_set, return_data, -1, Integer.MAX_VALUE, getArguments("record-tag-name"));
            	result_set.close();
            	result_set=null;
            }
	        if(call_statement != null) call_statement.close();
	        call_statement=null;
	        return getServiceContext().doNextProcess();
		}catch(SQLException se){
			se.printStackTrace();
			throw createMethodException("SQLException", se.getLocalizedMessage(), String.valueOf(se.getErrorCode()), se.getSQLState());
		}finally{
			try{
				if(result_set != null) result_set.close();
				if(call_statement != null) call_statement.close(); 
			}catch(Exception ignor){}
		}
		
	}
	protected int outputData(ResultSet resultset, Node result_node, int row_from, int row_fetch, String record_tag_name) throws SQLException{
		return xSqlUtility.outputData(resultset, result_node, row_from, row_fetch, record_tag_name);
	}
	protected boolean outputMetadata(ResultSet resultset, Node result_node) throws SQLException{
		return xSqlUtility.outputMetadata(resultset, result_node);
	}
    protected void setParameters(CallableStatement statement, int index, String data_type, String value, boolean is_null) throws SQLException{
    	xSqlUtility.setParamters(statement, index, data_type, value, is_null);
    }
    protected String getOutParameters(CallableStatement cs, int type, int index) throws SQLException{
        try {
        	switch(type) {
              case java.sql.Types.BLOB : 
              case java.sql.Types.CLOB : 
              case java.sql.Types.LONGVARBINARY : 
              case java.sql.Types.REF : 
              case java.sql.Types.OTHER :
              case java.sql.Types.DISTINCT :
              case java.sql.Types.STRUCT :
              case java.sql.Types.JAVA_OBJECT :
              case java.sql.Types.ARRAY :
              case java.sql.Types.NULL :
              case java.sql.Types.BINARY :
              case java.sql.Types.VARBINARY :
              case java.sql.Types.NCHAR:
              case java.sql.Types.NCLOB:
              case java.sql.Types.NVARCHAR:
              case java.sql.Types.CHAR :
              case java.sql.Types.VARCHAR :
              case java.sql.Types.BIT :
              case java.sql.Types.BIGINT : 
              case java.sql.Types.NUMERIC :
              case java.sql.Types.DECIMAL :
              case java.sql.Types.DOUBLE :
              case java.sql.Types.FLOAT :
              case java.sql.Types.REAL :
              case java.sql.Types.INTEGER :
              case java.sql.Types.SMALLINT :
              case java.sql.Types.TINYINT :
              case java.sql.Types.DATE :
              case java.sql.Types.TIME :
              case java.sql.Types.TIMESTAMP :
            	  return cs.getString(index);
              default:
            	  return cs.getString(index);
        	}
        }catch(Exception e) {
        	e.printStackTrace();
        }
        return "";
    }
}
