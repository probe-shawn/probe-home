package xlive.method.rowset;

import java.sql.*;
import java.util.Hashtable;
import java.util.Vector;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.*;

import xlive.xProbeServlet;
import xlive.method.*;

public class xSqlQueryMethod extends xDefaultMethod{
	/*
	@SuppressWarnings("unchecked")
	public Object process() throws xMethodException{
		if(xProbeServlet.isGAE()){
			String redirect_url = this.getProperties("gae-redirect-url");
			return processInternetWebObjectMethod(redirect_url, getObjectPath(), getMethodName());
		}

		String database_object_name=getProperties("database-object-name");
		Connection connection_instance = (Connection)getWebObjectPropertiesObject(database_object_name, "connection-instance");
		if(connection_instance == null)	throw createMethodException("connection-instance", "not found");
		//
		int resultset_type=ResultSet.TYPE_FORWARD_ONLY;
		try{
			String type=getProperties("resultset.type");
			if(type != null && type.trim().length() > 0)resultset_type=Integer.parseInt(type);
		}catch(Exception e){}
		int resultset_concurrency=ResultSet.CONCUR_READ_ONLY;
		try{
			String concurrency=getProperties("resultset.concurrency");
			if(concurrency != null && concurrency.trim().length() > 0)resultset_concurrency=Integer.parseInt(concurrency);
		}catch(Exception e){}
		int resultset_holdability=ResultSet.HOLD_CURSORS_OVER_COMMIT;
		try{
			String holdability=getProperties("resultset.holdability");
			if(holdability != null && holdability.trim().length() > 0)resultset_holdability=Integer.parseInt(holdability);
		}catch(Exception e){}
		//
		ResultSet result_set=null;
		boolean is_eof=false,is_maximum=false;
		try{
			StringBuilder command=new StringBuilder(getArguments("command").trim());
			Vector<Object> param_vector=new Vector<Object>();
			NodeList where_nodes=(NodeList)getArguments("where", XPathConstants.NODESET);
			for(int i=0; i<where_nodes.getLength(); ++i){
				String definition_name=((Element)where_nodes.item(i)).getAttribute("name");
				if(definition_name==null||definition_name.trim().length()==0) definition_name="where";
				StringBuilder sql_where= makeWhere(where_nodes.item(i), param_vector);
				sql_where.insert(0, (sql_where.length() > 0) ? " where " : "");
				String lower_command=command.toString().toLowerCase();
				int index=lower_command.indexOf("["+definition_name+"]");
				if(index < 0) index=lower_command.indexOf(" "+definition_name+" ");
				if(index < 0) command.append(sql_where);
				else command.replace(index, index+definition_name.length()+2, sql_where.toString());
			}
			//  
			NodeList orderby_nodes=(NodeList)getArguments("orderby", XPathConstants.NODESET);
			for(int i=0; i<orderby_nodes.getLength(); ++i){
				String definition_name=((Element)orderby_nodes.item(i)).getAttribute("name");
				if(definition_name==null||definition_name.trim().length()==0) definition_name="order by";
				StringBuilder sql_orderby= makeOrderby(orderby_nodes.item(i));
				sql_orderby.insert(0, (sql_orderby.length() > 0) ? " order by " : "");
				command.append(sql_orderby);
			}
			logMessage("sql-query : "+command.toString());
			
			PreparedStatement statement=connection_instance.prepareStatement(command.toString(), resultset_type, resultset_concurrency, resultset_holdability);
			for(int i=0; i< param_vector.size(); ++i){
				Object[] params=(Object[])param_vector.get(i);
				setParamters(statement, i+1, (String)params[0], (String)params[1], (Boolean)params[2]);
			}
			is_eof=false;
			result_set=statement.executeQuery();
			//Integer row_fetch=20,row_from=1, row_real_fetch=0;
			int row_fetch=20,row_from=1, row_real_fetch=0,row_maximum=0;
			Element return_data=setReturnArguments("data", "");
			if(return_data != null){
				try{
					row_fetch=Integer.parseInt(getArguments("row-fetch"));
				}catch(Exception e){}
				row_fetch=(row_fetch < 0) ? Integer.MAX_VALUE : (row_fetch == 0) ? 20 : row_fetch;
				try{
					row_from=Integer.parseInt(getArguments("row-from"));
				}catch(Exception e){}
				row_from=(row_from <= 0) ? 1 : row_from;
				try{
					row_maximum=Integer.parseInt(getArguments("row-maximum"));
				}catch(Exception e){}
				row_maximum=(row_maximum<0)?0:row_maximum;
				if(row_maximum > 0 && row_fetch>row_maximum) row_fetch=row_maximum;
				//
				String record_tag_name=getArguments("record-tag-name");
				String convert_to_treeset=getArguments("convert-to-treeset");
				row_real_fetch=outputData(result_set, return_data, -1, row_fetch, record_tag_name,convert_to_treeset);
				
				is_eof=(row_real_fetch!=row_fetch);
				if(row_maximum > 0) is_maximum=(row_real_fetch>=row_maximum);
				
				String resultset_id=getArguments("resultset-id");
				if(resultset_id!=null && resultset_id.trim().length() != 0){
					xSqlResultset cached_resultset = null;
					synchronized(this.getServiceContext().getSynSessionDbResultset()){
						Hashtable<String,xSqlResultset> rstable = (Hashtable<String,xSqlResultset>) getWebObjectPropertiesObject(database_object_name,"connection-resultset-instance-table");
						if(rstable != null) cached_resultset = rstable.remove(resultset_id);
					}
					if(cached_resultset!=null && cached_resultset.resultset != null){
						try{
							cached_resultset.resultset.close();
						}catch(SQLException esol){}
					}
				}else resultset_id=String.valueOf(System.currentTimeMillis());
				//////
				return_data.setAttribute("resultsetId", resultset_id);
				return_data.setAttribute("rowTotal", String.valueOf(row_real_fetch));
				////resultset_id
				if(is_eof||is_maximum) return_data.setAttribute("eof", "true");
				else {
					xSqlResultset cached_resultset = new xSqlResultset();
					cached_resultset.rowFetched=row_real_fetch;
					cached_resultset.resultset =result_set;
					Hashtable<String,xSqlResultset> rstable = null;
					synchronized(this.getServiceContext().getSynSessionDbResultset()){
						rstable = (Hashtable<String,xSqlResultset>) getWebObjectPropertiesObject(database_object_name,"connection-resultset-instance-table");
						if(rstable == null) setWebObjectPropertiesObject(database_object_name,"connection-resultset-instance-table",rstable = new Hashtable<String,xSqlResultset>());
						rstable.put(resultset_id, cached_resultset);
					}
				}
			}
			String output_metadata=getArguments("output-metadata");
			if("true".equalsIgnoreCase(output_metadata)){
				Element return_metadata=setReturnArguments("metadata", "");
				outputMetadata(result_set, return_metadata);
			}
			//
			if(result_set!=null && (is_eof||is_maximum)){
				try{
					Statement resultset_statement=result_set.getStatement();
					result_set.close();
					if(resultset_statement != null) resultset_statement.close();
				}catch(SQLException esol){}
				result_set=null;
			}
			return getServiceContext().doNextProcess();
		}catch(SQLException se){
			se.printStackTrace();
			throw createMethodException("SQLException", se.getLocalizedMessage(), String.valueOf(se.getErrorCode()), se.getSQLState());
		}finally{
			try{
				if(result_set != null && is_eof){
					try{
						Statement resultset_statement=result_set.getStatement();
						result_set.close();
						if(resultset_statement != null) resultset_statement.close();
					}catch(SQLException esol){}
					result_set=null;
				}
			}catch(Exception eignor){}
		}
	}
	protected StringBuilder makeWhere(Node operator_node, Vector<Object> param_vector) throws xMethodException{
		StringBuilder buf= new StringBuilder();
		String sql_type=null;
		String value=null;
		Boolean is_null=false;
		String tmp;
		if(operator_node == null ||!operator_node.hasChildNodes()||operator_node.getNodeType()!= Node.ELEMENT_NODE) return buf;
		NodeList node_list=null;
		XPath xp = XPathFactory.newInstance().newXPath();	
		try{
			node_list=(NodeList)xp.evaluate("./child::*", operator_node, XPathConstants.NODESET);
		}catch(XPathExpressionException xee){
			xee.printStackTrace();
		}
		int decode_field_count=0;
		String logic_name=operator_node.getNodeName();
		boolean is_logic_not=false;
		if(logic_name.equals("not-and")) {
			is_logic_not=true;
			logic_name="and";
		}else if(logic_name.equals("not-or")){
			is_logic_not=true;
			logic_name="or";
		}
		for(int i=0;i<node_list.getLength();++i){
			if(node_list.item(i).getNodeType() != Node.ELEMENT_NODE) continue;
			Element define_node=(Element)node_list.item(i);
			String define_node_name=define_node.getNodeName();
			if("and".equals(define_node_name) || "or".equals(define_node_name) || "not-and".equals(define_node_name)||"not_or".equals(define_node_name)	){
				buf.append(makeWhere(define_node,param_vector));
				continue; 
			} 
			String field_name=define_node.getAttribute("fieldName");
			if(field_name==null||field_name.trim().length()==0) field_name=define_node_name;
			boolean always= define_node.hasAttribute("always");
			if(!always && (define_node.getTextContent().trim().length()==0)) continue;
			String operator=define_node.getAttribute("operator");
			//
			if(operator==null||operator.trim().length()==0)operator="=";
			//
			if("between".equals(operator)){
				Element value1=(Element)evaluate("./value1", define_node, XPathConstants.NODE);
				Element value2=(Element)evaluate("./value2", define_node, XPathConstants.NODE);
				value1=(value1!=null && value1.getTextContent().trim().length()==0)?null:value1;
				value2=(value2!=null && value2.getTextContent().trim().length()==0)?null:value2;
				if(value1 == null && value2 == null) continue;
				if(++decode_field_count > 1) buf.append(" ").append(logic_name).append(" ");
				buf.append(" ").append(field_name).append(" ");
				sql_type=define_node.getAttribute("dataType");
				if(value1 != null && value2 != null){
					buf.append(" between ? and ? ");
					value=value1.getTextContent();
					tmp=value1.getAttribute("isNull");
					is_null=("1".equals(tmp) || "true".equalsIgnoreCase(tmp));
					param_vector.add(new Object[]{sql_type, value, is_null});
					value=value2.getTextContent();
					tmp=value2.getAttribute("isNull");
					is_null=("1".equals(tmp) || "true".equalsIgnoreCase(tmp));
					param_vector.add(new Object[]{sql_type, value, is_null});
				}else if(value1 != null){
					buf.append(" >= ? ");
					value=value1.getTextContent();
					tmp=value1.getAttribute("isNull");
					is_null=("1".equals(tmp) || "true".equalsIgnoreCase(tmp));
					param_vector.add(new Object[]{sql_type, value, is_null});
				}else if(value2 != null){
					buf.append(" <= ? ");
					value=value2.getTextContent();
					tmp=value2.getAttribute("isNull");
					is_null=("1".equals(tmp) || "true".equalsIgnoreCase(tmp));
					param_vector.add(new Object[]{sql_type, value, is_null});
				}
			}else if("in".equals(operator)){
				NodeList in_value_nodes=(NodeList)evaluate("./value", define_node, XPathConstants.NODESET);
				if(in_value_nodes==null || in_value_nodes.getLength()==0) continue;
				sql_type=define_node.getAttribute("dataType");
				int in_items_count=0;
				for(int v=0;v<in_value_nodes.getLength(); ++v){
					Element in_item=(Element)in_value_nodes.item(v);
					if(in_item.getTextContent().trim().length()==0)continue;
					if(in_items_count==0){
						if(++decode_field_count > 1) buf.append(" ").append(logic_name).append(" ");
						buf.append(" ").append(field_name).append(" in ( ");
					}else buf.append(" , ");
					++in_items_count;
					buf.append(" ? ");
					value=in_item.getTextContent();
					tmp=in_item.getAttribute("isNull");
					is_null=("1".equals(tmp) || "true".equalsIgnoreCase(tmp));
					param_vector.add(new Object[]{sql_type, value, is_null});
				}
				if(in_items_count > 0)buf.append(" ) ");
			}else {
				Element value_node=define_node;
				value=value_node.getTextContent();
				if(value==null||value.trim().length()==0) continue;
				if(++decode_field_count > 1) buf.append(" ").append(logic_name).append(" ");
				buf.append(" ").append(field_name).append(" ").append(operator).append(" ? ");
				sql_type=define_node.getAttribute("dataType");
				tmp=value_node.getAttribute("isNull");
				is_null=("1".equals(tmp) || "true".equalsIgnoreCase(tmp));
				param_vector.add(new Object[]{sql_type, value, is_null});
			}
		}
		if(decode_field_count > 1) buf.insert(0, "(").append(")");
		if(decode_field_count > 0 && is_logic_not) buf.insert(0, " not ");
		return buf;
	}
	
	
	protected StringBuilder makeOrderby(Node orderby_node){
		StringBuilder buf= new StringBuilder();
		if(orderby_node == null ||!orderby_node.hasChildNodes()||orderby_node.getNodeType()!= Node.ELEMENT_NODE) return buf;
		NodeList node_list=null;
		XPath xp = XPathFactory.newInstance().newXPath();	
		try{
			node_list=(NodeList)xp.evaluate("./child::*", orderby_node, XPathConstants.NODESET);
		}catch(XPathExpressionException xee){
			xee.printStackTrace();
		}
		int decode_field_count=0;
		for(int i=0;i<node_list.getLength();++i){
			if(node_list.item(i).getNodeType() != Node.ELEMENT_NODE) continue;
			Element define_node=(Element)node_list.item(i);
			String define_node_name=define_node.getNodeName();
			String field_name=define_node.getAttribute("fieldName");
			if(field_name==null||field_name.trim().length()==0) field_name=define_node_name;
			//boolean always= define_node.hasAttribute("always");
			Element orderby=define_node;
			//if(orderby == null) continue;
			++decode_field_count;
			boolean desc=orderby.hasAttribute("desc");
			buf.append(decode_field_count > 1 ? ", " : "").append(field_name).append((desc) ? " desc " : "");
		}
		return buf;
	}
    protected void setParamters(PreparedStatement statement, int index, String data_type, String value, boolean is_null) throws SQLException{
		xSqlUtility.setParamters(statement, index, data_type, value, is_null);
    }
	protected int outputData(ResultSet resultset, Node result_node, int row_from, int row_fetch, String record_tag_name,String convert_to_treeset) throws SQLException{
		return xSqlUtility.outputDataTreeset(resultset, result_node, row_from, row_fetch, record_tag_name,convert_to_treeset);
	}
	protected boolean outputMetadata(ResultSet resultset, Node result_node) throws SQLException{
		return xSqlUtility.outputMetadata(resultset, result_node);
	}
	*/
}
