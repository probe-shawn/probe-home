package xlive.method.rowset;

import java.io.*;
import java.math.BigDecimal;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;



import xlive.xWebInformation;


public class xSqlUtility {
	
	private static SimpleDateFormat formatDate= new SimpleDateFormat("yyyyMMddHHmmss");
	private static SimpleDateFormat formatTime= new SimpleDateFormat("HHmmss");
	private static SimpleDateFormat formatTimestamp= new SimpleDateFormat("yyyyMMddHHmmssS");
	private static String UTF8="UTF-8";
	
    public xSqlUtility(){}
    /*
	public static int outputData(ResultSet resultset, Node result_node, int row_fetch, String record_tag_name) throws SQLException{
		ResultSetMetaData meta_data=resultset.getMetaData();
		int columns_count=meta_data.getColumnCount();
		int count=0;
		Document doc=result_node.getOwnerDocument(); 
		while(resultset.next()){
			Node record_node=doc.createElement(record_tag_name);
			result_node.appendChild(record_node);
			for(int i=1;i<=columns_count;++i){ 
				String name=meta_data.getColumnName(i);
				Node field_node=doc.createElement(name);
				record_node.appendChild(field_node);
				field_node.setTextContent(resultset.getString(i));
			}
			if(++count >= row_fetch) break;;
		}
		return count;
	}
	*/
	public static int outputData(ResultSet resultset, Node result_node, int row_from, int row_fetch, String record_tag_name) throws SQLException{
		return outputDataTreeset(resultset,result_node,row_from,row_fetch,record_tag_name,null);
	}

	public static int outputDataTreeset(ResultSet resultset, Node result_node, int row_from, int row_fetch, String record_tag_name,String convert_to_treeset) throws SQLException{
		ResultSetMetaData meta_data=resultset.getMetaData();
		//
		Element[] index_elements=null;
		Vector<Object[]> index_node_vector=null;
		Vector<String> node_attribute_names=null;
		boolean do_convert=false;
		if(convert_to_treeset != null && convert_to_treeset.trim().length()>0){
			do_convert=true;
			index_node_vector=new Vector<Object[]>();
			node_attribute_names=new Vector<String>();
			row_fetch=Integer.MAX_VALUE;
			//
			String[] names = convert_to_treeset.split("\\.");
			convert_to_treeset="";
			for(int i=0; i<names.length;++i){
				String name=names[i].trim();
				if(name.length()==0)continue;
				String[] fields = name.split("\\["); 
				if(fields.length>0) convert_to_treeset +=fields[0];
				if(fields.length>1) node_attribute_names.add((fields[1].substring(0,fields[1].length()-1)+",").toUpperCase());
				else node_attribute_names.add("");
			}
		}
		//
		int resultset_type=resultset.getType();
		int columns_count=meta_data.getColumnCount();
		if(row_from > 0){
			boolean ok = false;
			int before_from=row_from-1;
			if(resultset_type !=ResultSet.TYPE_FORWARD_ONLY){
				if(before_from > 0){
					ok=resultset.first();
					if(ok) ok &= resultset.absolute(before_from);
				}
			}else{
				for(int i = 0; i < before_from;++i) resultset.next();
			}
		}
		//
		int count=0;
		Document doc=result_node.getOwnerDocument(); 
		while(resultset.next()){
			if(do_convert){
				index_elements=xWebInformation.createElements(convert_to_treeset);
			}
			Node record_node=doc.createElement(record_tag_name);
			for(int i=1;i<=columns_count;++i){ 
				String name=meta_data.getColumnName(i);
				String value="";
				Element field_node=doc.createElement(name);
				record_node.appendChild(field_node);
				switch(meta_data.getColumnType(i)){
	        		case java.sql.Types.NVARCHAR :
		        		value=resultset.getNString(i);
		        		if(resultset.wasNull()||value==null)field_node.setAttribute("isNull", "true");
		        		else field_node.setTextContent(value);
		        		break;
	        		case java.sql.Types.DATE :
	        			Date date=resultset.getDate(i,Calendar.getInstance());
	        			if(resultset.wasNull()||date==null)field_node.setAttribute("isNull", "true");
	        			else field_node.setTextContent(value=formatDate.format(date));
	        			break;
		        	case java.sql.Types.TIME :
		        		Time time=resultset.getTime(i,Calendar.getInstance());
		        		if(resultset.wasNull()||time==null)field_node.setAttribute("isNull", "true");
		        		else field_node.setTextContent(value=formatTime.format(time));
		        		break;
		        	case java.sql.Types.TIMESTAMP :
		        		Timestamp ts=resultset.getTimestamp(i,Calendar.getInstance());
		        		if(resultset.wasNull()||ts==null)field_node.setAttribute("isNull", "true");
		        		else field_node.setTextContent(value=formatTimestamp.format(ts));
		        		break;
		        	case java.sql.Types.BLOB : 
		                Blob blob = resultset.getBlob(i);
		                if(resultset.wasNull() || blob == null)field_node.setAttribute("isNull", "true");
		                else {
		                	InputStream inp = blob.getBinaryStream();
		                	byte[] bytes = new byte[(int)blob.length()];
		                	//byte[] bytes = blob.getBytes(0l, (int) blob.length());
		                	try {
		                		inp.read(bytes);
								field_node.setTextContent(value=new String(bytes, xSqlUtility.UTF8));
							} catch (DOMException e) {
								e.printStackTrace();
							} catch (UnsupportedEncodingException e) {
								e.printStackTrace();
							}catch (IOException e) {
			            		   e.printStackTrace();
			            	}
		                }
		                break;
		            case java.sql.Types.CLOB :
		                Clob clob = resultset.getClob(i);
		               if(resultset.wasNull() || clob == null) field_node.setAttribute("isNull", "true");
		               else {
		            	   Reader reader = clob.getCharacterStream();
		            	   char [] buf = new char[(int) clob.length()];
		            	   try {
		            		   reader.read(buf);
		            		   field_node.setTextContent(value=new String(buf));
		            	   } catch (IOException e) {
		            		   e.printStackTrace();
		            	   }
		               }
		               break;
		        	default:
		        		value=resultset.getString(i);
		        		if(resultset.wasNull()||value==null)field_node.setAttribute("isNull", "true");
		        		else field_node.setTextContent(value);
				}
				if(do_convert){
					setElementAttribute(index_elements,name,(value==null)?"":value,node_attribute_names);
				}
			}
			if(do_convert){
				boolean not_found=true;
				String index_string="";
				for(int i=0;i<index_elements.length;++i){
					index_string+=index_elements[i].getAttribute(index_elements[i].getNodeName())+'.';
				}
				for(int i=0;i<index_node_vector.size();++i){
					Object[]objs=index_node_vector.get(i);
					if(index_string.equals((String)objs[0])){
						Element[]eles=(Element[])objs[1];
						eles[eles.length-1].appendChild(record_node);
						not_found=false;
						break;
					}
				}
				if(not_found){
					index_elements[index_elements.length-1].appendChild(record_node);
					index_node_vector.add(new Object[]{index_string,index_elements});
				}
			}else result_node.appendChild(record_node);
			if(++count >= row_fetch) break;
		}
		if(do_convert){
			for(int i=0;i<index_node_vector.size();++i){
				Element[]eles=(Element[])(index_node_vector.get(i)[1]);
				result_node.appendChild(eles[0]);
			}
		}
		return count;
	}
	private static void setElementAttribute(Element[] elements,String field_name,String field_value,Vector<String> node_attribute_names){
		for(int i=0;i<elements.length;++i){
			if(field_name.equalsIgnoreCase(elements[i].getNodeName()) && !elements[i].hasAttribute(field_name)){
				elements[i].setAttribute(field_name, field_value);
			}
			String attrs=node_attribute_names.get(i);
			if(attrs.indexOf((field_name+",").toUpperCase())>=0){
				elements[i].setAttribute(field_name, field_value);
			}
		}
	}
	public static boolean outputMetadata(ResultSet resultset, Node result_node) throws SQLException{
		ResultSetMetaData md=resultset.getMetaData();
		int count=md.getColumnCount();
		Document doc=result_node.getOwnerDocument();
		Node cols=doc.createElement("columns");
		result_node.appendChild(cols);
		java.util.Hashtable<String, Object[]> table_list= new java.util.Hashtable<String, Object[]>();
		for(int i=1; i<= count;++i){
			Element def=doc.createElement(md.getColumnName(i));
			cols.appendChild(def);
			def.setAttribute("type", String.valueOf(md.getColumnType(i)));
			def.setAttribute("dataType", String.valueOf(md.getColumnType(i)));
			def.setAttribute("typeName", md.getColumnTypeName(i));
			def.setAttribute("label", md.getColumnLabel(i));
			String catalog=md.getCatalogName(i);
			def.setAttribute("catalog", catalog);
			String schema=md.getSchemaName(i);
			def.setAttribute("schema", schema);
			String table_name=md.getTableName(i);
			def.setAttribute("table", table_name);
			def.setAttribute("displaySize", String.valueOf(md.getColumnDisplaySize(i)));
			def.setAttribute("precision", String.valueOf(md.getPrecision(i)));
			def.setAttribute("scale", String.valueOf(md.getScale(i)));
			def.setAttribute("autoIncrement", String.valueOf(md.isAutoIncrement(i)));
			def.setAttribute("caseSensitive", String.valueOf(md.isCaseSensitive(i)));
			def.setAttribute("currency", String.valueOf(md.isCurrency(i)));
			def.setAttribute("nullable", String.valueOf(md.isNullable(i)));
			def.setAttribute("readOnly", String.valueOf(md.isReadOnly(i)));
			def.setAttribute("searchable", String.valueOf(md.isSearchable(i)));
			def.setAttribute("signed", String.valueOf(md.isSigned(i)));
			def.setAttribute("writable", String.valueOf(md.isWritable(i)));
			Object[] value= {catalog,schema,table_name};
			table_list.put(catalog+"."+schema+"."+table_name, value);
		}
		Node tables=doc.createElement("tables");
		result_node.appendChild(tables);
		Enumeration<?> em = table_list.elements();
		while(em.hasMoreElements()){
			Object[] objs = (Object[])em.nextElement();
			Element table = doc.createElement("table");
			table.setAttribute("name", (String)objs[2]);
			table.setAttribute("calalog", (String)objs[0]);
			table.setAttribute("schema", (String)objs[1]);
			tables.appendChild(table);
		}
		return true;
	}
	public static boolean outputMetadata2(ResultSet resultset, Node result_node) throws SQLException{
		ResultSetMetaData md=resultset.getMetaData();
		int count=md.getColumnCount();
		//
		Document doc=result_node.getOwnerDocument();
		for(int i=1; i<= count;++i){
			Node def=doc.createElement("column-definition");
			result_node.appendChild(def);
			//
			def.appendChild(doc.createElement("column-index")).setTextContent(String.valueOf(i));
			def.appendChild(doc.createElement("column-name")).setTextContent(md.getColumnName(i));
			def.appendChild(doc.createElement("column-type")).setTextContent(String.valueOf(md.getColumnType(i)));
			def.appendChild(doc.createElement("column-type-name")).setTextContent(md.getColumnTypeName(i));
			def.appendChild(doc.createElement("catalog-name")).setTextContent(md.getCatalogName(i));
			def.appendChild(doc.createElement("column-label")).setTextContent(md.getColumnLabel(i));
			def.appendChild(doc.createElement("schema-name")).setTextContent(md.getSchemaName(i));
			def.appendChild(doc.createElement("table-name")).setTextContent(md.getTableName(i));
			def.appendChild(doc.createElement("column-display-size")).setTextContent(String.valueOf(md.getColumnDisplaySize(i)));
			def.appendChild(doc.createElement("precision")).setTextContent(String.valueOf(md.getPrecision(i)));
			def.appendChild(doc.createElement("scale")).setTextContent(String.valueOf(md.getScale(i)));
			def.appendChild(doc.createElement("auto-increment")).setTextContent(String.valueOf(md.isAutoIncrement(i)));
			def.appendChild(doc.createElement("case-sensitive")).setTextContent(String.valueOf(md.isCaseSensitive(i)));
			def.appendChild(doc.createElement("currency")).setTextContent(String.valueOf(md.isCurrency(i)));
			def.appendChild(doc.createElement("definitely-writable")).setTextContent(String.valueOf(md.isDefinitelyWritable(i)));
			def.appendChild(doc.createElement("nullable")).setTextContent(String.valueOf(md.isNullable(i)));
			def.appendChild(doc.createElement("read-only")).setTextContent(String.valueOf(md.isReadOnly(i)));
			def.appendChild(doc.createElement("searchable")).setTextContent(String.valueOf(md.isSearchable(i)));
			def.appendChild(doc.createElement("signed")).setTextContent(String.valueOf(md.isSigned(i)));
			def.appendChild(doc.createElement("writable")).setTextContent(String.valueOf(md.isWritable(i)));
		}
		return true;
	}
	
    public static void setParamters(PreparedStatement statement, int index, String data_type, String value, boolean is_null) throws SQLException{
    	//System.out.println("data_type :"+data_type+" value :"+value);
        BigDecimal bigd;
        int type=(data_type==null||data_type.trim().length()==0)? java.sql.Types.VARCHAR : Integer.parseInt(data_type);
        try{
        	Integer.parseInt(data_type);
        }catch(Exception e){}
		switch(type){
	    	case java.sql.Types.BLOB :
	    		if(is_null) statement.setNull(index, type);
	            else {
	            	byte[] byte_value=null;
					try {
						byte_value = value.getBytes(xSqlUtility.UTF8);
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
	                statement.setBinaryStream(index, new ByteArrayInputStream(byte_value), byte_value.length);
	            }
	            break;
            case java.sql.Types.CLOB :
                if(is_null) statement.setNull(index, type);
                else statement.setCharacterStream(index, new StringReader(value));
	        	break;
	        case java.sql.Types.BINARY :
	        case java.sql.Types.VARBINARY :
	        case java.sql.Types.LONGVARBINARY :
	         	if(is_null) statement.setNull(index, type);
	          	else statement.setBytes(index, value.getBytes());
	            break;
	        case java.sql.Types.JAVA_OBJECT :
	        case java.sql.Types.REF :
	        case java.sql.Types.OTHER :
	        case java.sql.Types.ARRAY :
	        case java.sql.Types.DISTINCT :
	        case java.sql.Types.STRUCT :
	        case java.sql.Types.NULL :
	        	statement.setNull(index, type);
	            break;
	        case java.sql.Types.NVARCHAR:
	        case java.sql.Types.NCHAR:
	        case java.sql.Types.NCLOB:
	            try {
	            	if(statement instanceof oracle.jdbc.OraclePreparedStatement)
	                   			((oracle.jdbc.OraclePreparedStatement) statement).setFormOfUse(index, oracle.jdbc.OraclePreparedStatement.FORM_NCHAR);
	            }catch(Exception eora){
	            	eora.printStackTrace();
	            }catch(Error rall){
	            	rall.printStackTrace();
	            }
	        case java.sql.Types.CHAR :
	        case java.sql.Types.VARCHAR :
	        case java.sql.Types.LONGVARCHAR :
	        	statement.setString(index, value);
	            break;
	        case java.sql.Types.BIT :
	        	statement.setBoolean(index, "1".equalsIgnoreCase(value)||"true".equalsIgnoreCase(value));
	            break;
	        case java.sql.Types.NUMERIC :
	        case java.sql.Types.DECIMAL :
	        	try {
	        		bigd = new BigDecimal(is_null ? "0" : value);
	            }catch(Exception dece){bigd = new BigDecimal("0");}
	            statement.setBigDecimal(index, bigd);
	            break;
	        case java.sql.Types.BIGINT :
	            try {
	                bigd = new BigDecimal(is_null ? "0" : value);
	            }catch(Exception dece){bigd = new BigDecimal("0");}
	            try {
	            	statement.setLong(index, bigd.longValue());
	            }catch(Exception ebig){statement.setBigDecimal(index, bigd);}
	            break;
	        case java.sql.Types.DOUBLE :
	           	try {
	                bigd = new BigDecimal(is_null ? "0" : value);
	            }catch(Exception dece){bigd = new BigDecimal("0");}
	            statement.setDouble(index, bigd.doubleValue());
	            break;
	        case java.sql.Types.REAL :
	        case java.sql.Types.FLOAT :
	           	try {
	                bigd = new BigDecimal(is_null ? "0" : value);
	            }catch(Exception dece){bigd = new BigDecimal("0");}
	            statement.setFloat(index, bigd.floatValue());
	            break;
	        case java.sql.Types.INTEGER :
	            try {
	                bigd = new BigDecimal(is_null ? "0" : value);
	            }catch(Exception dece){bigd = new BigDecimal("0");}
	            statement.setInt(index, bigd.intValue());
	            break;
	        case java.sql.Types.TINYINT :      
	        case java.sql.Types.SMALLINT :
	            try {
	                 bigd = new BigDecimal(is_null ? "0" : value);
	            }catch(Exception e){bigd = new BigDecimal("0");}
	            statement.setShort(index, bigd.shortValue());
	            break;
	        case java.sql.Types.DATE :
	        	value=value.trim();
	            if(is_null) statement.setNull(index, type);
	            else {
	            	if(value.length() >= 8) {
	            		statement.setDate(index, xSqlUtility.parseDate(value));
	            	}else statement.setNull(index, type);
	            }
	            break;
	        case java.sql.Types.TIME :
	        	value=value.trim();
	            if(is_null) statement.setNull(index, type);
	            else {
	                if(value.length() >= 6){
	                	statement.setTime(index, xSqlUtility.parseTime(value));
	                }else statement.setNull(index, type);
	            }
	            break;
	        case java.sql.Types.TIMESTAMP :
	        	value=value.trim();
	        	if(is_null) statement.setNull(index, type);
	        	else {
	                 if(value.length() >= 8) {
	                	 statement.setTimestamp(index, xSqlUtility.parseTimestamp(value));
	                 } else statement.setNull(index, type);
	            }
	            break;
	        } 
    }
    
    private static Pattern patternDate=null;
    public static Date parseDate(String date_string){
    	if(patternDate==null){
    		String r="(\\d{4})\\D*(\\d{2}|\\d{1})\\D*(\\d{2}|\\d{1})\\D*(\\d{2}|\\d*)\\D*(\\d{2}|\\d*)\\D*(\\d{2}|\\d*)\\D*(\\d{3}|\\d*)";
    		patternDate = Pattern.compile(r); 
    	}
    	Matcher matcher=patternDate.matcher(date_string);
    	int[] date_value = new int[7];
    	for(int i=0;i<date_value.length;++i)date_value[i]=-1;
    	boolean matches=matcher.matches();
    	if(matches){
    		for(int i=1;i<matcher.groupCount();++i){
    			try{
    				date_value[i-1]=Integer.parseInt(matcher.group(i));
    			}catch(Exception e){
    			}
    		}
    		if(date_value[0]>0 && date_value[1]>0 && date_value[2]>0){
    			Calendar cal=Calendar.getInstance();
    			cal.set(date_value[0],date_value[1]-1,date_value[2]);
    			return new Date(cal.getTimeInMillis());
    		}
    	}
		return null;
    }
    private static Pattern patternTimestamp=null;
    public static Timestamp parseTimestamp(String timestamp_string){
    	if(patternTimestamp==null){
    		String r="(\\d{4})\\D*(\\d{2}|\\d{1})\\D*(\\d{2}|\\d{1})\\D*(\\d{2}|\\d*)\\D*(\\d{2}|\\d*)\\D*(\\d{2}|\\d*)\\D*(\\d{3}|\\d*)";
    		patternTimestamp = Pattern.compile(r); 
    	}
    	Matcher matcher=patternTimestamp.matcher(timestamp_string);
    	boolean matches=matcher.matches();
    	if(matches){
    		int[] date_value = new int[7];
    		for(int i=0;i<date_value.length;++i){
    			date_value[i]=(i<3)?-1:0;
    		}
    		for(int i=1;i<matcher.groupCount();++i){
    			try{
    				date_value[i-1]=Integer.parseInt(matcher.group(i));
    			}catch(Exception e){}
    		}
    		if(date_value[0]>0 && date_value[1]>0 && date_value[2]>0){
    			Calendar cal=Calendar.getInstance();
    			cal.set(date_value[0],date_value[1]-1,date_value[2]);
    			cal.set(Calendar.HOUR_OF_DAY, date_value[3]);
    			cal.set(Calendar.MINUTE, date_value[4]);
    			cal.set(Calendar.SECOND, date_value[5]);
    			cal.set(Calendar.MILLISECOND, date_value[6]);
    			return new Timestamp(cal.getTimeInMillis());
    		}
    	}
		return null;
    }
    
    private static Pattern patternTime=null;
    public static Time parseTime(String time_string){
    	if(patternTime==null){
    		String r="((\\d{2}|\\d*)\\D*(\\d{2}|\\d*)\\D*(\\d{2}|\\d*)\\D*(\\d{3}|\\d*)";
    		patternTime = Pattern.compile(r); 
    	}
    	Matcher matcher=patternTime.matcher(time_string);
    	boolean matches=matcher.matches();
    	if(matches){
    		int[] date_value = new int[4];
    		for(int i=0;i<date_value.length;++i)date_value[i]=0;
    		for(int i=1;i<matcher.groupCount();++i){
    			try{
    				date_value[i-1]=Integer.parseInt(matcher.group(i));
    			}catch(Exception e){}
    		}
    		Calendar cal=Calendar.getInstance();
    		cal.set(Calendar.HOUR_OF_DAY, date_value[0]);
    		cal.set(Calendar.MINUTE, date_value[1]);
    		cal.set(Calendar.SECOND, date_value[2]);
    		cal.set(Calendar.MILLISECOND, date_value[3]);
    		return new Time(cal.getTimeInMillis());
    	}
    	return null;
    }
    
}
