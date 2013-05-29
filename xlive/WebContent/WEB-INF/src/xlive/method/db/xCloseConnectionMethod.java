package xlive.method.db;

import java.sql.*;
import java.util.Enumeration;
import java.util.Hashtable;

import xlive.xProbeServlet;
import xlive.method.*;
import xlive.method.rowset.xSqlResultset;

public class xCloseConnectionMethod extends xDefaultMethod{
	public Object process() throws xMethodException{
		/*
		if(xProbeServlet.isGAE()){
			return getServiceContext().doNextProcess();
		}
		Object synSessionDbConnection = this.getServiceContext().getSynSessionDbConnection();
		synchronized(synSessionDbConnection){
			return close_process();
		}
		*/
		return null; 
	}
	/*
	@SuppressWarnings("unchecked")
	private Object close_process() throws xMethodException{
		Connection connection_instance = (Connection)getPropertiesObject("connection-instance");
		if(connection_instance != null){
			if(reduceReferenceCount()>0) return getServiceContext().doNextProcess();
			String autocommit=getProperties("auto-commit");
			boolean auto_commit=("true".equals(autocommit)||"1".equals(autocommit));
			String sessionclose = getQNameArguments("session-closed");
			boolean session_close=("true".equals(sessionclose)||"1".equals(sessionclose));
			Hashtable<String,xSqlResultset> resultset_table = null;
			synchronized(this.getServiceContext().getSynSessionDbResultset()){
				resultset_table = (Hashtable<String,xSqlResultset>) getPropertiesObject("connection-resultset-instance-table");
			}
			
			if(!session_close && resultset_table != null && resultset_table.size() > 0) return getServiceContext().doNextProcess();
			//
			if(resultset_table != null){
				Enumeration<xSqlResultset> e = resultset_table.elements();
				while(e.hasMoreElements()){
					xSqlResultset x_resultset = e.nextElement();
					ResultSet resultset_object=x_resultset.resultset;
					try{
						Statement statement=resultset_object.getStatement();
						resultset_object.close();
						if(statement != null)statement.close();
					}catch(SQLException esql){}
				}
				resultset_table.clear();
				synchronized(this.getServiceContext().getSynSessionDbResultset()){
					setPropertiesObject("connection-resultset-instance-table",null);
				}
			}
			// try again;
			if(getReferenceCount()>0 && !session_close)return getServiceContext().doNextProcess();
			if(connection_instance != null &&(auto_commit || session_close)){
				setPropertiesObject("connection-instance",null);
				try{
					if(!connection_instance.getAutoCommit())connection_instance.rollback();
					connection_instance.setAutoCommit(true);
					connection_instance.close();
					logMessage("OK");
				}catch(SQLException e){
					e.printStackTrace();
				}
			}
		}
		return getServiceContext().doNextProcess();
	}
	private int getReferenceCount()throws xMethodException {
		String ref_count_string=getProperties("connection-reference-count");
		int ref_count=0;
		try{
			ref_count=Integer.parseInt(ref_count_string);
		}catch(Exception e){}
		return ref_count;
	}
	private int reduceReferenceCount()throws xMethodException {
		String ref_count_string=getProperties("connection-reference-count");
		int ref_count=0;
		try{
			ref_count=Integer.parseInt(ref_count_string);
		}catch(Exception e){}
		ref_count=(--ref_count) < 0 ? 0 : ref_count; 
		setProperties("connection-reference-count",String.valueOf(ref_count));
		return ref_count;
	}
	*/
}
