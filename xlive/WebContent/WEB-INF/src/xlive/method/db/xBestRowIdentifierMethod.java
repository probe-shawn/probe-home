package xlive.method.db;

import java.sql.*;

import javax.xml.xpath.XPathConstants;

import org.w3c.dom.*;

import xlive.xProbeServlet;
import xlive.method.*;

public class xBestRowIdentifierMethod extends xDefaultMethod{
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
			String table_name=getArguments("table");
			String catalog=getArguments("catalog");
			catalog=(catalog==null||catalog.trim().length()==0) ? null:catalog;
			String schema=getArguments("schema");
			schema=(schema==null||schema.trim().length()==0) ? null:schema;
			Number scope=(Number)getArguments("scope", XPathConstants.NUMBER);
			scope=(scope==null)? DatabaseMetaData.bestRowTemporary : scope;
			Boolean nullable=(Boolean)getArguments("nullable", XPathConstants.BOOLEAN);
			nullable=(nullable==null)?false:nullable;
			java.sql.DatabaseMetaData meta=connection_instance.getMetaData();
			ResultSet rs = meta.getBestRowIdentifier(catalog, schema, table_name, scope.intValue(), nullable.booleanValue());
            //
			Element return_columns=this.setReturnArguments("columns", "");
            while(rs.next()) {
            	String name=rs.getString("COLUMN_NAME");
            	Element column=createElement(name);
                column.setAttribute("dataType", String.valueOf(rs.getInt("DATA_TYPE")));
                //column.setAttribute("typeName", rs.getString("TYPE_NAME"));
                column.setAttribute("columnSize", rs.getString("COLUMN_SIZE"));
                column.setAttribute("decimalDigits", rs.getString("DECIMAL_DIGITS"));
                column.setAttribute("pseudoColumn", rs.getString("PSEUDO_COLUMN"));
                return_columns.appendChild(column);
                }
            rs.close();
		}catch(SQLException e){
			throw createMethodException("SQLException", e.getLocalizedMessage(), String.valueOf(e.getErrorCode()),e.getSQLState());
		}finally{
			if(transient_connection)this.processMethod("close-connection");
		}
		return getServiceContext().doNextProcess();
	}
}
