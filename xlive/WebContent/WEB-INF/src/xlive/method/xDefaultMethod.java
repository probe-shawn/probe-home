package xlive.method;

import java.io.File;
import java.util.logging.Level;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.*;

import xlive.*;
import xlive.method.logger.xLogger;
import xlive.xml.*;

public class xDefaultMethod {
	
	private xServiceContext serviceContext;
	private Element objectNode;
	private Element objectPropertiesNode;
	private Element methodNode;
	private String applySuperMethodName=null;
	
	public void setup(xServiceContext service_context, Element method_node, String apply_super_method_name)throws xMethodException{
		serviceContext=service_context;
		methodNode=method_node; 
		objectNode=(Element)evaluate("ancestor::object", methodNode,  XPathConstants.NODE);
		objectPropertiesNode=(Element)evaluate("properties", objectNode, XPathConstants.NODE);
		applySuperMethodName=apply_super_method_name;
		if(methodNode.getAttributeNode("extendArguments") != null)	extendDefaultPropertiesToArguments("extend");
		else if(methodNode.getAttributeNode("overwriteArguments") != null) extendDefaultPropertiesToArguments("overwrite");
		//logSetup();
		serviceContext.incLogDeep();
		logMessage(Level.FINE, null);
	}
	protected void extendDefaultPropertiesToArguments()throws xMethodException{
		extendDefaultPropertiesToArguments("extend");
	}
	protected void overwriteDefaultPropertiesToArguments() throws xMethodException {
		extendDefaultPropertiesToArguments("overwrite");
	}
	private void extendDefaultPropertiesToArguments(String argument_operation) throws xMethodException{
		Element method_properties=(Element)evaluate(getMethodName(), objectPropertiesNode,  XPathConstants.NODE);
		if(method_properties !=null) argumentsOperation(method_properties, argument_operation, true);
	}
	public Object process() throws xMethodException{
		Node node = methodNode.getFirstChild();
		Object result=null;
		while(node != null){
			if(node.getNodeType() == Node.ELEMENT_NODE){
				//result= xWebInformation.processInternalMethod(this.getServiceContext(),this,(Element)node);
				if(!getServiceContext().doNextProcess()) break;
			}
			node=node.getNextSibling();
		}
		return result;
	}
	public void cleanUp(){
		serviceContext.decLogDeep();
		serviceContext=null;
		objectNode=null;
		objectPropertiesNode=null;
		methodNode=null;
	}
	public void logMessage(String message){
		this.logMessage(Level.FINER, message);
	}
	public void logMessage(String level, String message){
		Level log_level=Level.FINER;
		try{log_level=Level.parse(level);}catch(Exception e){}
		logMessage(log_level, message);
	}
	public void logMessage(Level level, String message){
		String method_name=getMethodName();
		String object_name=getObjectPath();
		xLogger.log(level, serviceContext.getSessionId(), object_name, method_name, message, serviceContext.getLogDeep());
	}
	public xServiceContext getServiceContext(){
		return serviceContext;
	}
	public Element getObjectNode(){
		return objectNode;
	}
	public Element getObjectPropertiesNode(){
		return objectPropertiesNode;
	}
	protected String getObjectAttribute(String attribute_name){
		return xXmlDocument.getAttributeString(objectNode, attribute_name, null);
	}
	public String getObjectPath(){
		return getObjectPath(".");
	}
	public String getObjectPath(String separator){
		String name = objectNode.getAttribute("_name");
		if("/".equals(separator)) return name;
		return name.replaceAll("/", separator);
	}
	public Element getMethodNode(){
		return methodNode; 
	}
	public String getMethodAttribute(String attribute_name){
		return xXmlDocument.getAttributeString(methodNode, attribute_name, null);
	}
	public String getMethodName(){
		if(applySuperMethodName != null && applySuperMethodName.trim().length() > 0) return applySuperMethodName;
		String name=methodNode.getNodeName();
		if("method".equals(name)) name=methodNode.getAttribute("name");
		else{
			int index=name.lastIndexOf(".");
			if(index >=0)name=name.substring(index+1);
		}
		return name;
	}
	public String getMethodPath(){
		return getMethodPath(".");
	}
	public String getMethodPath(String separator){
		return this.getObjectPath(separator)+(separator==null ? "." : separator)+getMethodName();
	}
	///////
	protected xMethodException createMethodException(String cause_site, String message){
		return createMethodException(cause_site,message,null,null); 
	}
	protected xMethodException createMethodException(String cause_site, String message, String error_code, String error_state){
		return new xMethodException(getObjectPath(), getMethodName(),cause_site, message, error_code, error_state);
	}
	/////
	protected Element createElement(String element_name) throws xMethodException{
		return createElement(element_name,null);
	}
	protected Element createElement(String element_name, String text) throws xMethodException{
		try{
			return xWebInformation.createElement(element_name, text);
		}catch(Exception e){
			throw createMethodException("createElement ("+element_name+")", e.getLocalizedMessage());
		}
	}
	protected Element[] createElements(String element_names) throws xMethodException{
		return createElements(element_names,null);
	}
	protected Element[] createElements(String element_names, String last_element_text) throws xMethodException {
		try{
			return xWebInformation.createElements(element_names, last_element_text);
		}catch(Exception e){
			throw createMethodException("createElements ("+element_names+")", e.getLocalizedMessage());
		}
	}
	/////
	protected Element argumentsOperation(String argument_names, String last_argument_text, String operation) throws xMethodException{
		return argumentsOperation(argument_names, last_argument_text, operation, false);
	}
	protected Element argumentsOperation(String argument_names, String last_argument_text, String operation, boolean clone_node) throws xMethodException{
		try {
			Element[] elements=xWebInformation.createElements(argument_names,last_argument_text);
			serviceContext.argumentsOperation(elements[0], operation, clone_node);
			return "remove".equals(operation)? null :
					("append".equals(operation) && !clone_node) ? elements[elements.length-1] :
					(Element)serviceContext.getArguments(argument_names, XPathConstants.NODE);
		}catch(XPathExpressionException xee){
			xee.printStackTrace();
			throw createMethodException("argumentsOperation ("+argument_names+")", xee.getLocalizedMessage());
		}catch(Exception e){
			e.printStackTrace();
			throw createMethodException("argumentsOperation ("+argument_names+")", e.getLocalizedMessage());
		}
	}
	protected Element argumentsOperation(Element element, String operation)throws xMethodException{
		return argumentsOperation(element, operation, false);
	}
	protected Element argumentsOperation(Element element, String operation, boolean clone_node) throws xMethodException{
		try{
			serviceContext.argumentsOperation(element, operation, clone_node);
			return "remove".equals(operation)? null :
					("append".equals(operation) && !clone_node) ? element :
					(Element)serviceContext.getArguments(element.getNodeName(), XPathConstants.NODE);
		}catch(XPathExpressionException xee){
			xee.printStackTrace();
			throw createMethodException("argumentsOperation ("+((element==null) ? "null" : element.getNodeName())+")", xee.getLocalizedMessage());
		}catch(Exception e){
			e.printStackTrace();
			throw createMethodException("argumentsOperation ("+((element==null) ? "null" : element.getNodeName())+")", e.getLocalizedMessage());
		}
	}
	/////
	protected String getQNameArguments(String argument_names) throws xMethodException{
		return (String)getQNameArguments(argument_names, XPathConstants.STRING);
	}
	protected Object getQNameArguments(String argument_names, javax.xml.namespace.QName return_type) throws xMethodException{
		try{
			return serviceContext.getArguments(argument_names, return_type);
		}catch(XPathExpressionException xee){
			xee.printStackTrace();
			throw createMethodException("getArguments ("+argument_names+")", xee.getLocalizedMessage());
		}catch(Exception e){
			e.printStackTrace();
			throw createMethodException("getArguments ("+argument_names+")", e.getLocalizedMessage());
		}
	}
	protected String getArguments(String argument_names) throws xMethodException {
		return (String)getArguments(argument_names, XPathConstants.STRING);
	}
	protected Object getArguments(String argument_names, javax.xml.namespace.QName return_type)throws xMethodException{
		try{
			String method_name=getMethodName()+".";
			argument_names=(argument_names.startsWith(method_name)) ? argument_names : method_name+argument_names;
			return serviceContext.getArguments(argument_names, return_type);
		}catch(XPathExpressionException xee){
			xee.printStackTrace();
			throw createMethodException("getMethodArguments ("+argument_names+")", xee.getLocalizedMessage());
		}catch(Exception e){
			e.printStackTrace();
			throw createMethodException("getMethodArguments ("+argument_names+")", e.getLocalizedMessage());
		}
	}
	protected String getMethodArguments(String method_name, String argument_names) throws xMethodException {
		return (String)getMethodArguments(method_name, argument_names, XPathConstants.STRING);
	}
	protected Object getMethodArguments(String method_name, String argument_names, javax.xml.namespace.QName return_type)throws xMethodException{
		try{
			argument_names = (method_name==null || method_name.trim().length()==0) ? argument_names : method_name+"."+argument_names;
			return serviceContext.getArguments(argument_names, return_type);
		}catch(XPathExpressionException xee){
			xee.printStackTrace();
			throw createMethodException("getMethodArguments ("+argument_names+")", xee.getLocalizedMessage());
		}catch(Exception e){
			e.printStackTrace();
			throw createMethodException("getMethodArguments ("+argument_names+")", e.getLocalizedMessage());
		}
	}
	protected Element setQNameArguments(String arguments_names, String last_element_text) throws xMethodException{
		return argumentsOperation(arguments_names, last_element_text, "overwrite");
	}
	protected Element setMethodArguments(String method_name, String method_arguments_names, String last_element_text)throws xMethodException{
		method_arguments_names = (method_name==null||method_name.trim().length() == 0)? method_arguments_names : method_name+"."+method_arguments_names;
		return argumentsOperation(method_arguments_names, last_element_text, "overwrite");
	}
	protected Element setArguments(String method_arguments_names, String last_element_text)throws xMethodException{
		String method_name=getMethodName()+".";
		method_arguments_names=(method_arguments_names.startsWith(method_name)) ? method_arguments_names : method_name+method_arguments_names;
		return argumentsOperation(method_arguments_names, last_element_text, "overwrite");
	}
	//////////////
	protected String getProperties(String properties_names) throws xMethodException{
		return (String)getProperties(properties_names, XPathConstants.STRING);
	}
	protected Object getProperties(String properties, javax.xml.namespace.QName return_type) throws xMethodException{
		try{
			return serviceContext.getProperties(objectNode, objectPropertiesNode, properties, return_type);
		}catch(XPathExpressionException xee){
			xee.printStackTrace();
			throw createMethodException("getProperties ("+properties+")", xee.getLocalizedMessage());
		}catch(Exception e){
			e.printStackTrace();
			throw createMethodException("getProperties ("+properties+")", e.getLocalizedMessage());
		}
	}
	protected Object getPropertiesObject(String properties_names) throws xMethodException{
		return getProperties(properties_names,null);
	}
	///////////////
	protected String getReturnArguments(String return_arguments_name) throws xMethodException{
		return (String)getMethodReturnArguments(null, return_arguments_name, XPathConstants.STRING);
	}
	protected String getMethodReturnArguments(String method_name, String return_arguments_name) throws xMethodException{
		return (String)getMethodReturnArguments(method_name, return_arguments_name, XPathConstants.STRING);
	}
	
	protected Object getMethodReturnArguments(String method_name, String return_arguments_name, javax.xml.namespace.QName return_type) throws xMethodException{
		try{
			method_name=(method_name==null) ? getMethodName()+".return." : (method_name.trim().length()>0) ? method_name+".return." : "";
			String return_method="return."+method_name;
			if(!return_method.endsWith(".")) return_method+=".";
			if(!return_arguments_name.startsWith(return_method)) return_arguments_name=return_method+return_arguments_name;
			return serviceContext.getArguments(return_arguments_name, return_type);
		}catch(XPathExpressionException xee){
			xee.printStackTrace();
			throw createMethodException("getMethodReturnArguments ("+return_arguments_name+")", xee.getLocalizedMessage());
		}catch(Exception e){
			e.printStackTrace();
			throw createMethodException("getMethodReturnArguments ("+return_arguments_name+")", e.getLocalizedMessage());
		}
	}
	protected Element setReturnArguments(String return_arguments_name, String text) throws xMethodException{
		return setMethodReturnArguments(null, return_arguments_name,text);
	}
	protected Element setMethodReturnArguments(String method_name, String return_arguments_name, String text) throws xMethodException{
		try{
			method_name=(method_name==null) ? getMethodName()+".return." : (method_name.trim().length()>0) ? method_name+".return." : "";
			String return_method="return."+method_name;
			if(!return_method.endsWith(".")) return_method+=".";
			if(!return_arguments_name.startsWith(return_method)) return_arguments_name=return_method+return_arguments_name;
			return argumentsOperation(return_arguments_name,text,"overwrite");
		}catch(Exception e){
			e.printStackTrace();
			throw createMethodException("setMethodReturnArguments ("+return_arguments_name+")", e.getLocalizedMessage());
		}
	}
	//////////////
	protected Object evaluate(String xpath, Node node, javax.xml.namespace.QName return_type) throws xMethodException{
		XPath xp = XPathFactory.newInstance().newXPath();
		try{
			return xp.evaluate(xWebInformation.xPathValidate(xpath), node, return_type);
		}catch(XPathExpressionException xee){
			xee.printStackTrace();
			throw createMethodException("evaluate ("+xpath+")", xee.getLocalizedMessage());
		}catch(Exception e){
			e.printStackTrace();
			throw createMethodException("evaluate ("+xpath+")", e.getLocalizedMessage());
		}
	}
	protected String getWebObjectProperties(String object_path, String properties_names) throws xMethodException{
		return (String)getWebObjectProperties(object_path, properties_names, XPathConstants.STRING);
	}
	protected Object getWebObjectProperties(String object_path, String properties_names, javax.xml.namespace.QName return_type) throws xMethodException{
		try{
			return serviceContext.getWebObjectProperties(object_path, properties_names, return_type);
		}catch(XPathExpressionException xee){
			xee.printStackTrace();
			throw createMethodException("getWebObjectProperties ("+object_path+","+properties_names+")", xee.getLocalizedMessage());
		}catch(Exception e){
			e.printStackTrace();
			throw createMethodException("getWebObjectProperties ("+object_path+","+properties_names+")", e.getLocalizedMessage());
		}
	}
	protected Object getWebObjectPropertiesObject(String object_path, String properties_names) throws xMethodException{
		return getWebObjectProperties(object_path, properties_names, null);
	}

	protected Object processWebObjectMethod(String object_path, String method_name) throws xMethodException{
		return xWebInformation.processWebObjectMethod(this, object_path, method_name);
	}
	protected boolean isWebObjectExisted(String object_path) throws xMethodException{
		return xWebInformation.isWebObjectExisted(this, object_path);
	}
	protected Object processMethod(String method_name)throws xMethodException{
		return xWebInformation.processWebObjectMethod(this, null, method_name);
	}
	protected boolean processInternetWebObjectMethod(String server_url, String object_url, String method_name) throws xMethodException{
		return xWebInformation.processInternetWebObjectMethod(server_url, object_url, method_name, this);
	}
	protected String getObjectRealPath(){
		String respath=getObjectResourcePath();
		String path=xWebInformation.getWebDirectory();
		return (path+(path.endsWith("/")? respath.substring(1):respath));
	}
	protected String getObjectResourcePath(){
		String file_name = objectNode.getAttribute("_resPath");
		int index=file_name.lastIndexOf("/");
		return file_name.substring(0, index);
	}
	protected String getTomcatDirectory(){
		String tomcat_dir=getObjectRealPath();
		int index=tomcat_dir.toLowerCase().indexOf("webapps");
 		return tomcat_dir.substring(0,index);
	}
	protected String getWebDirectory(){
		return xWebInformation.getWebDirectory();
	}
	// ./ 
	// /
	public File directoryResolve(String dir){
		if(dir !=null && dir.trim().length()>0){
	        try{
	        	if(dir.indexOf(":")>0) return new File(dir);
	        	if(dir.startsWith("\\\\")||dir.startsWith("//")){
	        		return (xUtility.isWindows())? new File(dir):new File(xUtility.unixUNC(dir));
	        	}
	        	if(dir.startsWith("/")||dir.startsWith("\\")){
	        		File parent=new File(getWebDirectory());
	        		return new File(parent,dir);
	        	}
	        	return new File(new File(getObjectRealPath()),dir);
	        }catch(Exception e){}
		}
		return null;
	}
	public String resourceDirectoryConvert(String dir){
		String directory = dir;
		if(directory.startsWith("./")) {
			String respath = this.getObjectResourcePath();
			directory=directory.replaceFirst(respath.endsWith("/") ? "./" : ".", respath);
		}
		if(!directory.endsWith("/")) directory=directory+"/";
		return directory;
	}
	
}
