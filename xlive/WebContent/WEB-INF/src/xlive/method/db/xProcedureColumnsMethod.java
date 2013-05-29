package xlive.method.db;

import java.sql.*;

import org.w3c.dom.*;

import xlive.xProbeServlet;
import xlive.method.*;

public class xProcedureColumnsMethod extends xDefaultMethod{

	public Object process() throws xMethodException{
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
			String name=getArguments("name");
			name =(name ==null ||name.trim().length()==0)? null : name;
			String catalog=getArguments("catalog");
			catalog=(catalog==null||catalog.trim().length()==0) ? null:catalog;
			String schema=getArguments("schema");
			schema=(schema==null||schema.trim().length()==0) ? null:schema;
			String column_pattern=getArguments("column");
			column_pattern=(column_pattern==null||column_pattern.trim().length()==0) ? null:column_pattern;
			java.sql.DatabaseMetaData meta=connection_instance.getMetaData();
			ResultSet rs = meta.getProcedureColumns(catalog, schema, name, column_pattern);
			//
			Element return_columns=setReturnArguments("columns", "");
            while(rs.next()) {
                String column_name=rs.getString("COLUMN_NAME");
                if(column_name==null) column_name="return-column";
                Element column =createElement(column_name);
                column.setAttribute("type", String.valueOf(rs.getShort("COLUMN_TYPE")));
                column.setAttribute("dataType", String.valueOf(rs.getShort("DATA_TYPE")));
                column.setAttribute("precision", String.valueOf(rs.getInt("PRECISION")));
                column.setAttribute("length", String.valueOf(rs.getInt("LENGTH")));
                column.setAttribute("scale", String.valueOf(rs.getShort("SCALE")));
                return_columns.appendChild(column);
            }
            rs.close();
		}catch(SQLException se){
			se.printStackTrace();
			throw createMethodException("SQLException", se.getLocalizedMessage(), String.valueOf(se.getErrorCode()), se.getSQLState());
		}finally{
			if(transient_connection) processMethod("close-connection");
		}
		return getServiceContext().doNextProcess();
	}
}
