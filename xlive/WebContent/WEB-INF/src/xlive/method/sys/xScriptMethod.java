package xlive.method.sys;


import xlive.method.*;

import javax.script.*;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import xlive.*;

public class xScriptMethod extends xDefaultMethod{
	public Object process()throws xMethodException{
		long start_time=System.currentTimeMillis();		
		String script=getMethodNode().getTextContent().trim();
		
	    ScriptEngineManager factory = new ScriptEngineManager();
	    ScriptEngine engine = factory.getEngineByName("JavaScript");
	    
		//ScriptEngine engine = xWebInformation.getScriptEngine(false);
		Bindings bindings = engine.createBindings();
		bindings.put("thisMethod", this);
		/*
        ScriptContext newContext = new SimpleScriptContext();
        Bindings engineScope = newContext.getBindings(ScriptContext.ENGINE_SCOPE);
        engineScope.put("thisMethod", this);
        */
 	    //engine.put("thisMethod", this);
	    try{
	        //engine.eval("print(thisMethod.getDescription());" +"thisMethod.argumentsOperation('result.script.test', 'SCRIPT', 'extend');");
	        //engine.eval("print(thisMethod.getDescription());" +"var node=thisMethod.getMethodNode();print(node.getNodeName())");
	    	engine.eval(script, bindings);
	    	logMessage("\nscript execute cost :"+(System.currentTimeMillis()-start_time));
	    	return getServiceContext().doNextProcess();
	    }catch(ScriptException se){
	    	se.printStackTrace();
	    	throw this.createMethodException("ScriptException", se.getLocalizedMessage());
	    }
	}
	///////
	public void extendDefaultPropertiesToArguments()throws xMethodException{
		super.extendDefaultPropertiesToArguments();
	}
	public void overwriteDefaultPropertiesToArguments() throws xMethodException {
		super.overwriteDefaultPropertiesToArguments();
	}
	public String getObjectAttribute(String attribute_name){
		return super.getObjectAttribute(attribute_name);
	}
	///////
	public xMethodException createMethodException(String cause_site, String message){
		return super.createMethodException(cause_site,message);
	}
	public xMethodException createMethodException(String cause_site, String message, String error_code, String error_state){
		return super.createMethodException(cause_site,message,error_code,error_state);
	}
	/////
	public Element createElement(String element_name) throws xMethodException{
		return super.createElement(element_name);
	}
	public Element createElement(String element_name, String text) throws xMethodException{
		return super.createElement(element_name,text);
	}
	public Element[] createElements(String element_names) throws xMethodException{
		return super.createElements(element_names);
	}
	public Element[] createElements(String element_names, String last_element_text) throws xMethodException {
		return super.createElements(element_names,last_element_text);
	}
	/////
	public Element argumentsOperation(String argument_names, String last_argument_text, String operation) throws xMethodException{
		return super.argumentsOperation(argument_names,last_argument_text,operation);
	}
	public Element argumentsOperation(String argument_names, String last_argument_text, String operation, boolean clone_node) throws xMethodException{
		return super.argumentsOperation(argument_names,last_argument_text,operation,clone_node);
	}
	public Element argumentsOperation(Element element, String operation)throws xMethodException{
		return super.argumentsOperation(element,operation);
	}
	public Element argumentsOperation(Element element, String operation, boolean clone_node) throws xMethodException{
		return super.argumentsOperation(element,operation,clone_node);
	}
	/////
	public String getQNameArguments(String argument_names) throws xMethodException{
		return super.getQNameArguments(argument_names);
	}
	public Object getQNameArguments(String argument_names, javax.xml.namespace.QName return_type) throws xMethodException{
		return super.getQNameArguments(argument_names,return_type);
	}
	public String getArguments(String argument_names) throws xMethodException {
		return super.getArguments(argument_names);
	}
	public Object getArguments(String argument_names, javax.xml.namespace.QName return_type)throws xMethodException{
		return super.getArguments(argument_names,return_type);
	}
	public String getMethodArguments(String method_name, String argument_names) throws xMethodException {
		return super.getMethodArguments(method_name, argument_names);
	}
	public Object getMethodArguments(String method_name, String argument_names, javax.xml.namespace.QName return_type)throws xMethodException{
		return super.getMethodArguments(method_name, argument_names,return_type);
	}
	public Element setQNameArguments(String arguments_names, String last_element_text) throws xMethodException{
		return super.setQNameArguments(arguments_names,last_element_text);
	}
	public Element setArguments(String method_arguments_names, String last_element_text)throws xMethodException{
		return super.setArguments(method_arguments_names,last_element_text);
	}
	public Element setMethodArguments(String method_name, String method_arguments_names, String last_element_text)throws xMethodException{
		return super.setMethodArguments(method_name, method_arguments_names, last_element_text);
	}
	//////////////
	public String getProperties(String properties_names) throws xMethodException{
		return super.getProperties(properties_names);
	}
	public Object getProperties(String properties, javax.xml.namespace.QName return_type) throws xMethodException{
		return super.getProperties(properties,return_type);
	}
	public Object getPropertiesObject(String properties_names) throws xMethodException{
		return super.getPropertiesObject(properties_names);
	}
	///////////////
	public String getReturnArguments(String return_arguments_name) throws xMethodException{
		return super.getReturnArguments(return_arguments_name);
	}
	public String getMethodReturnArguments(String method_return_name,String return_arguments_name) throws xMethodException{
		return super.getMethodReturnArguments(method_return_name, return_arguments_name);
	}
	public Object getMethodReturnArguments(String method_return_name,String return_arguments_name, javax.xml.namespace.QName return_type) throws xMethodException{
		return super.getMethodReturnArguments(method_return_name, return_arguments_name,return_type);
	}
	public Element setReturnArguments(String return_arguments_name, String text) throws xMethodException{
		return super.setReturnArguments(return_arguments_name,text);
	}
	public Element setMethodReturnArguments(String method_return_name, String return_arguments_name, String text) throws xMethodException{
		return super.setMethodReturnArguments(method_return_name,return_arguments_name,text);
	}
	//////////////
	public Object evaluate(String xpath, Node node, javax.xml.namespace.QName return_type) throws xMethodException{
		return super.evaluate(xpath,node,return_type);
	}
	public String getWebObjectProperties(String object_path, String properties_names) throws xMethodException{
		return super.getWebObjectProperties(object_path,properties_names);
	}
	public Object getWebObjectProperties(String object_path, String properties_names, javax.xml.namespace.QName return_type) throws xMethodException{
		return super.getWebObjectProperties(object_path,properties_names,return_type);		
	}
	public Object getWebObjectPropertiesObject(String object_path, String properties_names) throws xMethodException{
		return super.getWebObjectPropertiesObject(object_path,properties_names);
	}
	public Object processWebObjectMethod(String object_path, String method_name) throws xMethodException{
		return super.processWebObjectMethod(object_path,method_name);
	}
	public boolean isWebObjectExisted(String object_path) throws xMethodException{
		return super.isWebObjectExisted(object_path);
	}
	public Object processMethod(String method_name)throws xMethodException{
		return super.processMethod(method_name);
	}
	public String getObjectRealDirectory(){
		return super.getObjectRealPath();
	}
	
}
