package xlive.method.rowset;

import java.sql.*;
import java.util.Hashtable;
import org.w3c.dom.*;
import xlive.xProbeServlet;
import xlive.method.*;

public class xSqlFetchMethod extends xDefaultMethod{
	/*
	@SuppressWarnings("unchecked")
	public Object process() throws xMethodException{
		if(xProbeServlet.isGAE()){
			String redirect_url = this.getProperties("gae-redirect-url");
			return processInternetWebObjectMethod(redirect_url, getObjectPath(), getMethodName());
		}
		String database_object_name=getProperties("database-object-name");
		String resultset_id=getArguments("resultset-id");
		xSqlResultset x_resultset = null;
		Hashtable<String,xSqlResultset> resultset_table = null;
		synchronized(this.getServiceContext().getSynSessionDbResultset()){
			resultset_table = (Hashtable<String,xSqlResultset>) getWebObjectPropertiesObject(database_object_name,"connection-resultset-instance-table");
			if(resultset_table != null) x_resultset = resultset_table.get(resultset_id);
		}
		if(x_resultset != null && x_resultset.resultset != null){
			int row_fetched=x_resultset.rowFetched;
			ResultSet cached_resultset=x_resultset.resultset;
			try {
				boolean is_eof=false,is_maximum=false;
				int row_fetch=20,row_maximum=0;
				try{
					row_fetch=Integer.parseInt(getArguments("row-fetch"));
				}catch(Exception e){}
				row_fetch=(row_fetch < 0) ? Integer.MAX_VALUE : (row_fetch == 0) ? 20 : row_fetch;
				try{
					row_maximum=Integer.parseInt(getArguments("row-maximum"));
				}catch(Exception e){}
				row_maximum=(row_maximum<0)?0:row_maximum;
				if(row_fetched>0 && row_maximum>0 && row_fetched+row_fetch>row_maximum)	row_fetch=row_maximum-row_fetched;
				String record_tag_name=getArguments("record-tag-name");
				Element result_node=setReturnArguments("data", "");
				int row_real_fetch=outputData(cached_resultset, result_node, -1, row_fetch, record_tag_name);
				is_eof=(row_real_fetch!=row_fetch);
				if(row_maximum > 0&&row_fetched>0)is_maximum=(row_fetched+row_real_fetch>=row_maximum);
				result_node.setAttribute("rowCount", String.valueOf(row_real_fetch));
				if(is_eof||is_maximum) {
					result_node.setAttribute("eof", "true");
					synchronized(this.getServiceContext().getSynSessionDbResultset()){
						resultset_table.remove(resultset_id);
					}
					try{
						Statement statement=cached_resultset.getStatement();
						cached_resultset.close();
						if(statement != null) statement.close();
					}catch(SQLException eignor){}
				}else{
					if(row_fetched>0 && row_maximum>0) x_resultset.rowFetched=row_fetched+row_real_fetch;
				}
			}catch(SQLException se){
				se.printStackTrace();
				throw createMethodException("SQLException", se.getLocalizedMessage(), String.valueOf(se.getErrorCode()), se.getSQLState());
			}
		}
		return getServiceContext().doNextProcess();
	}
	protected int outputData(ResultSet resultset, Node result_node, int row_from, int row_fetch, String record_tag_name) throws SQLException{
		return xSqlUtility.outputData(resultset, result_node, row_from, row_fetch, record_tag_name);
	}
	*/
}
