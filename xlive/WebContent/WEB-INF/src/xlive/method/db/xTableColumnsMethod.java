package xlive.method.db;

import java.sql.*;

import org.w3c.dom.*;

import xlive.xProbeServlet;
import xlive.method.*;

public class xTableColumnsMethod extends xDefaultMethod{
	public Object process() throws xMethodException{
		if(xProbeServlet.isGAE()){
			String redirect_url = this.getProperties("gae-redirect-url");
			return processInternetWebObjectMethod(redirect_url, getObjectPath(), getMethodName());
		}
		boolean transient_connection=false; 
		Connection connection_instance = (Connection)getPropertiesObject("connection-instance");
		if(connection_instance == null){
			connection_instance = (Connection)this.processMethod("get-connection");
			transient_connection=true;
		}
		try{
			String table_name=getArguments("table");
			String catalog=getArguments("catalog");
			catalog=(catalog==null||catalog.trim().length()==0) ? null:catalog;
			String schema=getArguments("schema");
			schema=(schema==null||schema.trim().length()==0) ? null:schema;
			String column=getArguments("column");
			column=(column==null||column.trim().length()==0) ? null:column;
			java.sql.DatabaseMetaData meta=connection_instance.getMetaData();
			ResultSet rs = meta.getColumns(catalog, schema, table_name,column);
            //
			Element return_columns=setReturnArguments("columns", "");
            while(rs.next()) {
            	String name=rs.getString("COLUMN_NAME");
            	Element column_name=createElement(name);
            	column_name.setAttribute("dataType", String.valueOf(rs.getInt("DATA_TYPE")));
                return_columns.appendChild(column_name);
                }
            rs.close();
		}catch(SQLException se){
			se.printStackTrace();
			throw createMethodException("SQLException", se.getLocalizedMessage(), String.valueOf(se.getErrorCode()), se.getSQLState());
		}finally{
			if(transient_connection)this.processMethod("close-connection");
		}
		return getServiceContext().doNextProcess();
	}
}
