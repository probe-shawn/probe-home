package xlive.method.db;

import java.sql.*;
import org.w3c.dom.*;

import xlive.xProbeServlet;
import xlive.method.*;

public class xTablesMethod extends xDefaultMethod{

	public Object process() throws  xMethodException{
		if(xProbeServlet.isGAE()){
			String redirect_url = this.getProperties("gae-redirect-url");
			return processInternetWebObjectMethod(redirect_url, getObjectPath(), getMethodName());
		}
		boolean transient_connection=false; 
		Connection connection_instance = (Connection)getPropertiesObject("connection-instance");
		if(connection_instance == null){
			connection_instance = (Connection)processMethod("get-connection");
			transient_connection=true;
		}
		try{
			String table_pattern=getArguments("table");
			table_pattern =(table_pattern ==null ||table_pattern.trim().length()==0)? null : table_pattern;
			String catalog=getArguments("catalog");
			catalog=(catalog==null||catalog.trim().length()==0) ? null:catalog;
			String schema_pattern=getArguments("schema");
			schema_pattern=(schema_pattern==null||schema_pattern.trim().length()==0) ? null:schema_pattern;
			String type=getArguments("types");
			String[] types = {"TABLE", "VIEW"};
			if(type != null && type.trim().length() > 0) types = type.split(",");
	        java.sql.DatabaseMetaData meta=connection_instance.getMetaData();
			ResultSet rs = meta.getTables(catalog, schema_pattern, table_pattern, types);
			Element return_tables=setReturnArguments("tables", "");
            while(rs.next()) {
            	//String name=rs.getString("TABLE_NAME");
            	Element column=createElement("table");
                column.setAttribute("name", rs.getString("TABLE_NAME"));
                column.setAttribute("catalog", rs.getString("TABLE_CAT"));
                column.setAttribute("schema", rs.getString("TABLE_SCHEM"));
                column.setAttribute("type", rs.getString("TABLE_TYPE"));
                column.setAttribute("remarks", rs.getString("REMARKS"));
                return_tables.appendChild(column);
                }
            rs.close();
		}catch(SQLException se){
			se.printStackTrace();
			throw createMethodException("SQLException", se.getLocalizedMessage(), String.valueOf(se.getErrorCode()), se.getSQLState());
		}finally {
			if(transient_connection)this.processMethod("close-connection");
		}
		return getServiceContext().doNextProcess();
	}
}
