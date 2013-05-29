package xlive.method.rowset;

import javax.xml.xpath.XPathConstants;

import xlive.xProbeServlet;
import xlive.method.*;

import org.w3c.dom.*;
import xlive.xml.*;

public class xGetPropertiesMethod extends xDefaultMethod{
	public Object process()throws xMethodException{
		if(xProbeServlet.isGAE()){
			String redirect_url = this.getProperties("gae-redirect-url");
			return processInternetWebObjectMethod(redirect_url, getObjectPath(), getMethodName());
		}
		Node clone_properties= getObjectPropertiesNode().cloneNode(true);
		//
		String table_name=getArguments("table-name");
		String procedure_name=getArguments("procedure-name");
		if(table_name != null && table_name.trim().length()>0)
			makeTablePseudoProperties(table_name, clone_properties);
		else if(procedure_name != null && procedure_name.trim().length() > 0)
			makeProcedurePseudoProperties(procedure_name, clone_properties);
		else {
			Node exclude_node=(Node)getArguments("exclude",XPathConstants.NODE);
			if(exclude_node != null)xXmlDocument.extendNodes(clone_properties, exclude_node, "remove");
		}
		Node return_properties=setMethodReturnArguments("", "properties", "");
		return_properties.getParentNode().replaceChild(clone_properties, return_properties);
		return getServiceContext().doNextProcess();
	}
	private void makeTablePseudoProperties(String table_name, Node properties_node) throws xMethodException{
		//sql-query 
		//command
		Node sql_query=(Node)evaluate("sql-query", properties_node, XPathConstants.NODE);
		Node command=(Node)evaluate("command", sql_query, XPathConstants.NODE);
		if(command != null) sql_query.removeChild(command);
		sql_query.appendChild(command=sql_query.getOwnerDocument().createElement("command"));
		command.appendChild(sql_query.getOwnerDocument().createCDATASection("select * from "+table_name));
		// where
		Node where=(Node)evaluate("where", sql_query, XPathConstants.NODE);
		if(where != null) sql_query.removeChild(where);
		where=sql_query.getOwnerDocument().createElement("where");
		this.setQNameArguments("table-columns.table", table_name);
		String database_object_name=this.getProperties("database-object-name");
		this.processWebObjectMethod(database_object_name, "table-columns");
		Element columns=(Element)this.getMethodReturnArguments("", "table-columns.return.columns", XPathConstants.NODE);
		columns.getOwnerDocument().renameNode(columns,null,"and");
		where.appendChild(columns);
		sql_query.appendChild(where);
		Element table_columns=(Element)this.getMethodReturnArguments("","table-columns", XPathConstants.NODE);
		table_columns.getParentNode().removeChild(table_columns);
		// meta fiels
		Node sql_query_meta=(Node)evaluate("return/metadata", sql_query, XPathConstants.NODE);
		if(sql_query_meta != null){
			Node meta_columns=columns.cloneNode(true);
			meta_columns.getOwnerDocument().renameNode(meta_columns,null,"columns");
			sql_query_meta.appendChild(meta_columns);
		}
		//order by
		Node orderby=(Node)evaluate("orderby", sql_query, XPathConstants.NODE);
		if(orderby != null) sql_query.removeChild(orderby);
		this.setQNameArguments("primary-keys.table", table_name);
		this.processWebObjectMethod(database_object_name, "primary-keys");
		Element pri_columns=(Element)this.getMethodReturnArguments("", "primary-keys.return.columns", XPathConstants.NODE);
		if(pri_columns != null){
			pri_columns.getOwnerDocument().renameNode(pri_columns,null,"orderby");
			sql_query.appendChild(pri_columns);
		}
		Element primary_keys=(Element)this.getMethodReturnArguments("", "primary-keys", XPathConstants.NODE);
		if(primary_keys != null) primary_keys.getParentNode().removeChild(primary_keys);
		////
		//sql-update
		Node sql_update=(Node)evaluate("sql-update", properties_node, XPathConstants.NODE);
		Node sql_table=(Node)evaluate("table", sql_update, XPathConstants.NODE);
		if(sql_table != null) sql_update.removeChild(sql_table);
		sql_update.appendChild(sql_table=sql_update.getOwnerDocument().createElement("table"));
		sql_table.setTextContent(table_name);
		sql_update.appendChild(sql_table);
		//
		Node key_fields=(Node)evaluate("key-fields", sql_update, XPathConstants.NODE);
		this.setQNameArguments("best-row-identifier.table", table_name);
		this.processWebObjectMethod(database_object_name, "best-row-identifier");
		//
		Element key_columns=(Element)this.getMethodReturnArguments("", "best-row-identifier.return.columns", XPathConstants.NODE);
		if(key_columns != null){
			key_fields.getParentNode().removeChild(key_fields);
			key_columns.getOwnerDocument().renameNode(key_columns,null,"key-fields");
			sql_update.appendChild(key_columns);
		}
		Element best_row_identifier=(Element)this.getMethodReturnArguments("", "best-row-identifier", XPathConstants.NODE);
		if(best_row_identifier != null) best_row_identifier.getParentNode().removeChild(best_row_identifier);
		//sql-delete
		Node sql_delete=(Node)evaluate("sql-delete", properties_node, XPathConstants.NODE);
		sql_table=(Node)evaluate("table", sql_delete, XPathConstants.NODE);
		if(sql_table != null) sql_table.setTextContent(table_name);
		else sql_delete.appendChild(createElement("table", table_name));
		//
		if(key_columns != null){
			Node clone_key_columns=key_columns.cloneNode(true);
			sql_delete.appendChild(clone_key_columns);
		}
		//sql-insert
		Node sql_insert=(Node)evaluate("sql-insert", properties_node, XPathConstants.NODE);
		sql_table=(Node)evaluate("table", sql_insert, XPathConstants.NODE);
		if(sql_table != null) sql_table.setTextContent(table_name);
		else sql_insert.appendChild(createElement("table", table_name));
	}
	private void makeProcedurePseudoProperties(String procedure_name, Node properties_node)throws xMethodException{
		//call-procedure
		String database_object_name=this.getProperties("database-object-name");
		Node call_procedure=(Node)evaluate("call-procedure", properties_node, XPathConstants.NODE);
		Node name=(Node)evaluate("name", call_procedure, XPathConstants.NODE);
		if(name == null) call_procedure.appendChild(name=createElement("name"));
		name.setTextContent(procedure_name);
		Node columns=(Node)evaluate("columns", call_procedure, XPathConstants.NODE);
		if(columns != null) call_procedure.removeChild(columns);
		//
		setQNameArguments("procedure-columns.name", procedure_name);
		processWebObjectMethod(database_object_name, "procedure-columns");
		columns=(Element)getMethodReturnArguments("", "procedure-columns.return.columns", XPathConstants.NODE);
		if(columns != null)	call_procedure.appendChild(columns);
		Node procedure_columns=(Element)getMethodReturnArguments("", "procedure-columns", XPathConstants.NODE);
		if(procedure_columns != null)procedure_columns.getParentNode().removeChild(procedure_columns);

		//
	}
}
