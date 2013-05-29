package xlive.method.db;

import java.sql.*;
import org.w3c.dom.*;

import xlive.xProbeServlet;
import xlive.method.*;

public class xProceduresMethod extends xDefaultMethod{

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
			java.sql.DatabaseMetaData meta=connection_instance.getMetaData();
			ResultSet rs = meta.getProcedures(catalog, schema, name);
			//
			Element return_procedures=setReturnArguments("procedures", "");
            while(rs.next()) {
            	String procedure_name=rs.getString("PROCEDURE_NAME");
                int index = procedure_name.indexOf(";");
                if(index > 0) procedure_name = procedure_name.substring(0, index);
                if(procedure_name.indexOf("$") >=0)continue;
                if(procedure_name.indexOf("#") >=0)continue;
                Element procedure=null;
                try{
                	procedure=createElement(procedure_name);
                }catch(Exception e){
                }
                if(procedure != null){
                	procedure.setAttribute("type", String.valueOf(rs.getShort("PROCEDURE_TYPE")));
                	return_procedures.appendChild(procedure);
                }
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
