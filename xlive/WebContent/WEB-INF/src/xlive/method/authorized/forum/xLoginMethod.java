package xlive.method.authorized.forum;

import javax.xml.xpath.XPathConstants;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import org.w3c.dom.*;
import xlive.method.*;
import xlive.*;

public class xLoginMethod extends xDefaultMethod{
	
	private SimpleDateFormat formatDate= new SimpleDateFormat("yyyyMMddHHmmss");
	private SimpleDateFormat formatTime= new SimpleDateFormat("HHmmss");
	private SimpleDateFormat formatTimestamp= new SimpleDateFormat("yyyyMMddHHmmssS");

	public Object process() throws xMethodException{
		if(xProbeServlet.isGAE()){
			String redirect_url = this.getProperties("gae-redirect-url");
			return processInternetWebObjectMethod(redirect_url, getObjectPath(), getMethodName());
		}

		String database_object_name=getProperties("database-object-name");
		processWebObjectMethod(database_object_name, "get-connection");
		Connection connection_instance = (Connection)getWebObjectPropertiesObject(database_object_name, "connection-instance");
		if(connection_instance == null)	throw createMethodException("connection-instance", "not found");
		//////
		String user_id=getArguments("user-id");
		String user_pass=getArguments("user-pass");
		String hash=getArguments("hash");
		//////

		Element user=this.createElement("user");
		Element data_node=this.createElement("data");
		user.appendChild(data_node);
		boolean user_id_found=false;
		//
		Statement statement=null;
		ResultSet result_set=null;
		try{
			String command="select * from xlive_cmm_user where user_id = '"+user_id+"'";
			statement=connection_instance.createStatement();
			result_set=statement.executeQuery(command);
			ResultSetMetaData meta_data=result_set.getMetaData();
			if(result_set.next()){
				user_id_found=true;
				for(int i = 1; i <= meta_data.getColumnCount(); ++i){
					String name=meta_data.getColumnName(i);
					String value="";
					Element field_node=this.createElement(nodeNameFormat(name));
					data_node.appendChild(field_node);
					switch(meta_data.getColumnType(i)){
		        	case java.sql.Types.DATE :
		        		Date date=result_set.getDate(i);
		        		if(result_set.wasNull()||date==null)field_node.setAttribute("isNull", "true");
		        		else field_node.setTextContent(value=formatDate.format(date));
		        		break;
		        	case java.sql.Types.TIME :
		        		Time time=result_set.getTime(i);
		        		if(result_set.wasNull()||time==null)field_node.setAttribute("isNull", "true");
		        		else field_node.setTextContent(value=formatTime.format(time));
		        		break;
		        	case java.sql.Types.TIMESTAMP :
		        		Timestamp ts=result_set.getTimestamp(i);
		        		if(result_set.wasNull()||ts==null)field_node.setAttribute("isNull", "true");
		        		else field_node.setTextContent(value=formatTimestamp.format(ts));
		        		break;
		        	default:
		        		value=result_set.getString(i);
		        		if(result_set.wasNull()||value==null)field_node.setAttribute("isNull", "true");
		        		else field_node.setTextContent(value);
					}
				}
			}
			if(result_set != null) result_set.close();
			result_set=null;
			statement.close();
			statement=null;
		}catch(SQLException se){
			se.printStackTrace();
			throw createMethodException("SQLException", se.getLocalizedMessage(), String.valueOf(se.getErrorCode()), se.getSQLState());
		}finally{
			try{
				if(result_set != null) result_set.close();
				if(statement != null)statement.close();
			}catch(Exception eignor){}
		}
		//
		String user_key=(String)evaluate("data/user-key", user,XPathConstants.STRING);
		if(user_id_found && user_key != null && user_key.trim().length() >0){
			try{
				String command = "select B.* from xlive_cmm_lnk A left outer join xlive_cmm_aut B on A.rol_key = B.rol_key where A.user_key = "+user_key+" "; 
				Hashtable<String,String> hh = new Hashtable<String,String>(128);
				statement=connection_instance.createStatement();
				result_set=statement.executeQuery(command);
				while(result_set.next()) {
					String aut_name = result_set.getString("AUT_NAME");
					aut_name = aut_name.trim();
					String aut_spc = result_set.getString("AUT_SPC");
					aut_spc = (aut_spc == null) ? "" : aut_spc;
					String aut_type = result_set.getString("AUT_TYPE");
					aut_type = (aut_type == null) ? "" : aut_type;
					String tmp = (String) hh.get(aut_name);
					if(tmp != null) aut_spc = unionString(aut_spc, tmp);
					hh.put(aut_name, aut_spc);
	            }
				if(result_set != null) result_set.close();
				result_set=null;
				statement.close();
				statement = null;
				//
				Element authorized = this.createElement("authorized");
				data_node.appendChild(authorized);
				Iterator<Map.Entry<String,String>> it = hh.entrySet().iterator();
				while(it.hasNext()) {
					Map.Entry<String,String> map = (Map.Entry<String,String>)it.next();
					String aut_name=(String)map.getKey();
					String aut_spc=(String)map.getValue();
					Element aut;
					if(aut_name.startsWith("!")) {
						aut=this.createElement("prohibit");
						aut.setAttribute("name", aut_name.substring(1));
					}else{
						aut=this.createElement("grant", aut_spc);
						aut.setAttribute("name", aut_name);
					}
					authorized.appendChild(aut);
				}
			}catch(SQLException se){
				se.printStackTrace();
				throw createMethodException("SQLException", se.getLocalizedMessage(), String.valueOf(se.getErrorCode()), se.getSQLState());
			}finally{
				try{
					if(result_set != null) result_set.close();
					if(statement != null)statement.close();
				}catch(Exception eignor){}
			}
		}
		processWebObjectMethod(database_object_name, "close-connection");
		/////////////////////////////////////////
		if(!user_id_found){
			setReturnArguments("valid", "false");
			setReturnArguments("why", "user id error, not found ");
			getServiceContext().setLogout();
			return getServiceContext().doNextProcess(false);
		}
		String data_user_pass=(String)evaluate("user-pass", data_node,XPathConstants.STRING);
		String hash_password=data_user_pass;
		if(hash!=null && hash.trim().length() > 0){
			hash_password = hashPassword(data_user_pass);
			hash_password = hashPassword(hash+hash_password);
		}
		if(hash_password.equals(user_pass)){
			setReturnArguments("valid", "true");
			Node return_data=setReturnArguments("data", "");
			data_node=(Element)return_data.getOwnerDocument().adoptNode(data_node);
			getServiceContext().setLogin(data_node);
			data_node=(Element)data_node.cloneNode(true);
			Element user_pass_element=(Element)evaluate("user-pass", data_node,XPathConstants.NODE);
			user_pass_element.setTextContent("");
			return_data.getParentNode().replaceChild(data_node, return_data);
			return getServiceContext().doNextProcess();
		}else{
			setReturnArguments("valid", "false");
			setReturnArguments("why", "password error");
			getServiceContext().setLogout();
			return getServiceContext().doNextProcess(false);
		}
	}
	private String hashPassword(String password){
		return new xMD5().calcMD5(password);
	}
    private String unionString(String str1, String str2){
        StringBuffer buf = new StringBuffer();
        for(int i = 0; i < str2.length(); ++i) {
            char cc = str2.charAt(i);
            if(str1.indexOf(cc) < 0) buf.append(cc);
            }
        buf.append(str1);
        return buf.toString();
    }
    private String nodeNameFormat(String str){
    	return str.toLowerCase().replaceAll("_", "-");
    }

}
