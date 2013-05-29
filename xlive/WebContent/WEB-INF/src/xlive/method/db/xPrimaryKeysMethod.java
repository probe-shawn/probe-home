package xlive.method.db;

import java.sql.*;
import org.w3c.dom.*;

import xlive.xProbeServlet;
import xlive.method.*;
import java.util.*;

public class xPrimaryKeysMethod extends xDefaultMethod{

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
			String table=getArguments("table");
			table =(table ==null ||table.trim().length()==0)? null : table;
			String catalog=getArguments("catalog");
			catalog=(catalog==null||catalog.trim().length()==0) ? null:catalog;
			String schema=getArguments("schema");
			schema=(schema==null||schema.trim().length()==0) ? null:schema;
			java.sql.DatabaseMetaData meta=connection_instance.getMetaData();
			ResultSet rs = meta.getPrimaryKeys(catalog, schema, table);
			//
			Vector<Object> key_vector=new Vector<Object>();
			Element return_columns=setReturnArguments("columns", "");
            while(rs.next()) {
            	Element column=createElement(rs.getString("COLUMN_NAME"));
            	String key_seq = String.valueOf(rs.getInt("KEY_SEQ"));
                column.setAttribute("keySeq", key_seq);
                key_vector.add(new Object[]{key_seq,column});
                //return_columns.appendChild(column);
                }
            rs.close();
            //
            Collections.sort(key_vector, new Comparator<Object>(){
                public int compare(Object o1, Object o2){
                	String k1=((String)((Object[])o1)[0]);
                	String k2=((String)((Object[])o2)[0]);
                	return k1.compareTo(k2);
                }
            });
            for(int i=0;i<key_vector.size();++i){
            	Object[]obj=(Object[])key_vector.get(i);
            	return_columns.appendChild((Element)obj[1]);
            }
            
		}catch(SQLException se){
			se.printStackTrace();
			throw createMethodException("SQLException", se.getLocalizedMessage(), String.valueOf(se.getErrorCode()), se.getSQLState());
		}finally {
			if(transient_connection) processMethod("close-connection");
		}
		return getServiceContext().doNextProcess();
	}
}
