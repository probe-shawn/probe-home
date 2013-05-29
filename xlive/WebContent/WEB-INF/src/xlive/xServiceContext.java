package xlive;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import xlive.xml.*;
import xlive.method.*;
import xlive.method.logger.xLogger;
import org.w3c.dom.*;
import javax.xml.parsers.*;

import java.io.*;
import java.util.*;
import java.util.logging.Level;


public class xServiceContext {
	private boolean doNextProcess=true;
	//
	private HttpServletRequest request=null; 
	private HttpServletResponse response=null;
	private String sessionId = "";
	//
	private Node argumentsNode=null;
	//private boolean isAdministrator = false;
	//
	private Element processXliveMethods=null;
	//
	private boolean disposing=false;
	private xMethodException methodException=null;
	//
	private Integer logDeep=0;
	//
	private String styleSheet=null;
	private String reqjLiveVersion=null;
	private String reqxLiveVersion=null;
	private String reqxLiveContent=null;
	private String reqxLiveServerCode=null;
	private boolean reqFromWebService=false;
	private boolean customizeResponse=false;
	//
	public boolean doNextProcess(){
		return doNextProcess;
	}
	public boolean doNextProcess(boolean stop){
		return (doNextProcess &=stop);
	}
	public xServiceContext(Element xlive_document_element) throws xSystemException{
		argumentsNode=xWebInformation.createElement("arguments");
		argumentsNode.appendChild(xWebInformation.createElement("return"));
		if(xlive_document_element != null)parseXmlContent(xlive_document_element);
	}
	public xServiceContext(HttpServletRequest http_request, HttpServletResponse http_response) throws xSystemException{
		argumentsNode=xWebInformation.createElement("arguments");
		argumentsNode.appendChild(xWebInformation.createElement("return"));
		httpRequestInformation(http_request, http_response);
	}
	private String generateSessionId(){
		org.apache.commons.codec.binary.Base64 base64 = new org.apache.commons.codec.binary.Base64();
		return new String(base64.encode(java.util.UUID.randomUUID().toString().getBytes()));
	}

	public String getSessionId(){
		return this.sessionId;
	}
	public boolean isLogin(){
		return true;
	}
	public void setLogout(){
	}
	public void setLogin(Node data_node){
	}
	public boolean isAuthorized(String resource_path) throws xSystemException{
		return true;
	}
	public boolean isRequestFromWebService(){
		return reqFromWebService;
	}
	public boolean isRequestFromXLive(){
		return (reqxLiveVersion!=null);
	}
	public boolean isRequestFromJLive(){
		return (reqjLiveVersion!=null);
	}
	public boolean isRequestFromXmlContent(){
		return "xml".equals(reqxLiveContent);
	}
	public boolean isRequestFromXLiveServer(){
		return reqxLiveServerCode != null;
	}
	public String getRequestServerCode(){
		return reqxLiveServerCode;
	}
	public HttpServletRequest getHttpServletRequest(){
		return request;
	}
	public HttpServletResponse getHttpServletResponse(){
		return response;
	}
	public HttpServletResponse customizeResponse(){
		customizeResponse=true;
		return response;
	}
	private void httpRequestInformation(HttpServletRequest http_request, HttpServletResponse http_response) throws xSystemException {
		request=http_request;
		response=http_response;
		reqxLiveVersion=(String)request.getHeader("X-XLive-Version");
		reqjLiveVersion=(String)request.getHeader("X-JLive-Version");
		reqxLiveContent=(String)request.getHeader("X-XLive-Content");
		reqxLiveServerCode=(String)request.getHeader("X-XLive-ServerCode");
		//
		String header_sessionid=(String)request.getHeader("xsessionid");
		String cookie_sessionid=null;
		if(!isRequestFromJLive() && !isRequestFromXLive() && header_sessionid==null){
			Cookie[] cookies=request.getCookies();
			for(int i=0;cookies!=null && i<cookies.length; ++i){
				if("xsessionid".equalsIgnoreCase(cookies[i].getName())){
					cookie_sessionid=cookies[i].getValue();
					break;
				}
			}
		}
		String xsession_id=(cookie_sessionid==null) ? header_sessionid : cookie_sessionid;
		xsession_id = (xsession_id==null||xsession_id.trim().length()==0) ? this.generateSessionId() : xsession_id;
		xLogger.log(Level.FINE, xsession_id, "xServiceContext", "contextSessionControl", "SessionId :"+xsession_id, 0);
		Cookie cookie=new Cookie("xsessionid", xsession_id);
		cookie.setPath("/");
		cookie.setMaxAge(-1);
		if(response != null)response.addCookie(cookie);
		if(response != null)response.addHeader("xsessionid", xsession_id);
		//
		try{
			if(request.getContentLength() > 0 && "xml".equals(reqxLiveContent)) {
				Document request_document=new xXmlDocument().createDocument(request.getInputStream());
				parseXmlContent(request_document.getDocumentElement());
			}
		}catch(Exception e){
			e.printStackTrace();
			throw new xSystemException(getSessionId(), "httpRequestInformation[parse request xml]", "exception : "+e.getLocalizedMessage());
		}
		//		
		try{
			if("/ws".equals(http_request.getServletPath())){
				if(request.getParameterValues("wsdl") == null){
					DocumentBuilder builder=DocumentBuilderFactory.newInstance().newDocumentBuilder();
					Document request_document=builder.parse(request.getInputStream());
					parseWebServiceXmlContent(request_document.getDocumentElement());
					reqFromWebService=true;
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			throw new xSystemException(getSessionId(), "httpRequestInformation[parse ws request xml]", "exception : "+e.getLocalizedMessage());
		}
		//
		boolean is_get="get".equalsIgnoreCase(request.getMethod());
		if(!is_get){
			String encode=request.getCharacterEncoding();
			if(encode == null) encode = System.getProperty("file.encoding");
			try{
				request.setCharacterEncoding(encode);
			}catch(Exception e){}
		}
		//
		if(processXliveMethods == null)	processXliveMethods=xWebInformation.createElement("xlive");
		String [] url_method_names=request.getParameterValues("method");
		Enumeration<?> en = request.getParameterNames();
		if(url_method_names!= null && url_method_names.length >0){
			Vector<Element> method_elements = new Vector<Element>();
			for(int f=0; f<url_method_names.length;++f) {
				Element method_element=xWebInformation.createElement("method");
				method_element.setAttribute("name", url_method_names[f]);
				method_elements.add(method_element);
				processXliveMethods.appendChild(method_element);
			}
			while(en.hasMoreElements()){
				String name=(String)en.nextElement();
				if("method".equalsIgnoreCase(name)) continue;
				String[] values=request.getParameterValues(name);
				Element method=null;
				for(int m=0;m<method_elements.size();++m){
					if(name.startsWith(method_elements.get(m).getAttribute("name")+".")){
						method=method_elements.get(m);
						break;
					}
				}
				if(method == null){
					method=method_elements.get(0);
					name=method.getAttribute("name")+"."+name;
				}
				for(int i=0; i<values.length; ++i){
					String value= values[i];
					try{
						if(is_get && !xProbeServlet.isGAE()) {
							value=new String(values[i].getBytes("iso-8859-1"),"utf-8");
						}
					}catch(Exception e){}
					Element[] elements=xWebInformation.createElements(method.getAttribute("name")+".arguments."+name, value);
					xXmlDocument.extendNodes(method, elements[0], "extend", false);
				}
			}
		}else{ //wsdl only
			if(request.getParameterValues("wsdl") != null){
				Element method_element=xWebInformation.createElement("method");
				method_element.setAttribute("name", "wsdl");
				processXliveMethods.appendChild(method_element);
			}
		}
	}
	private void parseXmlContent(Element document_element)throws xSystemException{
		try{
			processXliveMethods=document_element;
		}catch(Exception e){
			e.printStackTrace();
			throw new xSystemException(getSessionId(), "xServiceContext:parseXmlContent", "exception : "+e.getLocalizedMessage());
		}
	}
	private String getLocalPart(String name){
		String[]names=name.split(":");
		return (names.length>1)?names[1]:name;
	}
	private void parseWebServiceXmlContent(Element document_element)throws xSystemException{
		try{
			XPath xp = XPathFactory.newInstance().newXPath();
			processXliveMethods=xWebInformation.createElement("xlive");
			Node node=(Node)xp.evaluate("./Body", document_element,XPathConstants.NODE);
			node=processXliveMethods.getOwnerDocument().adoptNode(node);
			Node child=null;
			while((child=node.getFirstChild()) != null){
				Element method_name=xWebInformation.createElement("method");
				method_name.setAttribute("name", this.getLocalPart(child.getNodeName()));
				
				Element arguments=(Element)method_name.appendChild(xWebInformation.createElement("arguments"));
				Element method=(Element)arguments.appendChild(xWebInformation.createElement(this.getLocalPart(child.getNodeName())));
				//
				Node grand=null;
				while((grand=child.getFirstChild())!=null)method.appendChild(grand);
				processXliveMethods.appendChild(method_name);
				child.getParentNode().removeChild(child);
			}
		}catch(XPathExpressionException xee){
			xee.printStackTrace();
		}catch(NullPointerException npe){
			npe.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
			throw new xSystemException(getSessionId(), "xServiceContext:parseWebServiceXmlContent", "exception : "+e.getLocalizedMessage());
		}
	}
	public void dispose(){
		argumentsNode=null;
		methodException=null;
		processXliveMethods = null;
	}
	private Element getArgumentsReturnNode(){
		XPath xp = XPathFactory.newInstance().newXPath();
		Element arguments_return_node=null;
		try {
			arguments_return_node = (Element)xp.evaluate("./return", argumentsNode, XPathConstants.NODE);
			if(arguments_return_node==null)	arguments_return_node = (Element)xp.evaluate("./xlive", argumentsNode, XPathConstants.NODE);
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
		if(arguments_return_node==null) {
			argumentsNode.appendChild(arguments_return_node=xWebInformation.createElement("return"));
		}
		return arguments_return_node;
	}
	/*
	private void setRequestProperties(String object_path_properties, Object data){
		if(reqProperties == null)reqProperties = new Hashtable<String,Object>();
		if(data != null)reqProperties.put(object_path_properties, data);
		else reqProperties.remove(object_path_properties);
	}
	private Object getRequestProperties(String object_path_properties){
		if(reqProperties == null) return null;
		return reqProperties.get(object_path_properties);
	}
	private boolean addRequestTrace(String path_object_name){
		if(reqTrace == null)reqTrace = new Vector<String>();
		else if(reqTrace.contains(path_object_name)) return false;
		return reqTrace.add(path_object_name);
	}
	private String shiftLastTrace(){
		return (reqTrace != null) ? reqTrace.remove(reqTrace.size()-1):null;
	}
	*/
	public Element getProcessXliveMethods(){
		return processXliveMethods;
	}
	public Element argumentsOperation(Element element, String operation, boolean clone_node){
		Element arguments=element;
		boolean pseudo_parent=false;
		String[] include_node_name=null;
		if(!"arguments".equals(element.getNodeName())){
			arguments=(Element)element.getParentNode();
			if(arguments == null){
				pseudo_parent=true;
				arguments=element.getOwnerDocument().createElement("arguments");
				arguments.appendChild(element);
			}else{
				include_node_name=new String[1];
				include_node_name[0]=element.getNodeName();
			}
		}
		xXmlDocument.extendNodes(argumentsNode,arguments,operation,clone_node,999,true,include_node_name,null);
		if(pseudo_parent && element.getParentNode()==arguments) arguments.removeChild(element);
		return element;
	}
	public Object getArguments(String arguments_names, javax.xml.namespace.QName return_type) throws XPathExpressionException, NullPointerException{
		XPath xp = XPathFactory.newInstance().newXPath();
		return xp.evaluate(xWebInformation.xPathValidate(arguments_names), argumentsNode, return_type);
	}
	public Object getProperties(Element object_node, Element object_properties_node, String properties, javax.xml.namespace.QName return_type) throws XPathExpressionException, NullPointerException{
		XPath xp = XPathFactory.newInstance().newXPath();
		Object result=null;
		if(result == null && return_type != null) result = xp.evaluate(xWebInformation.xPathValidate(properties), object_properties_node, return_type);
		return result;
	}
	public Object getWebObjectProperties(String object_path, String properties, javax.xml.namespace.QName return_type) throws XPathExpressionException, NullPointerException{
		Element object_node=xWebInformation.getObjectNode(object_path);
		XPath xp = XPathFactory.newInstance().newXPath();
		Element object_properties_node=null;
		object_properties_node=(Element)xp.evaluate("./properties", object_node, XPathConstants.NODE);
		return getProperties(object_node, object_properties_node, properties, return_type);
	}
	public void logMethodException(xMethodException method_exception){
		methodException=method_exception;
	}
	public Cookie getCookie(String name){
		if(this.request != null){
			Cookie[] cookies = request.getCookies();
			for(int i = 0; cookies != null && i <cookies.length;++i)
				if(cookies[i].getName().equals(name)) return cookies[i];
		}
		return null;
	}
	public Cookie[] getCookie(){
		return (this.request != null)? request.getCookies() : null;
	}
	public void addCookie(Cookie cookie){
		if(this.response != null) this.response.addCookie(cookie);
	}
	public int getLogDeep(){
		return logDeep;
	}
	public void incLogDeep(){
		++logDeep;
	}
	public void decLogDeep(){
		--logDeep;
	}
	public boolean hasException(){
		return methodException != null;
	}
	public boolean isValid(){
		if(hasException()) return false;
		XPath xp = XPathFactory.newInstance().newXPath();
		try {
			NodeList node_list=(NodeList)xp.evaluate(".//return//valid", argumentsNode, XPathConstants.NODESET);
			for(int i=0;i<node_list.getLength();++i){
				if("false".equalsIgnoreCase(node_list.item(i).getTextContent())) return false;
			}
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
		return true;
	}
	//
	public void setXmlStylesheet(String style_sheet){
		styleSheet=style_sheet;
	}
	public String getHttpContextPath(){
		return (request==null)? null :request.getScheme()+"://"+ request.getServerName()+":"+ request.getServerPort()+request.getContextPath();
	}
	public String getServletPath(){
		return (request==null)? null:request.getServletPath();
	}
	protected void responseWebService() throws ServletException, IOException, xSystemException{
		java.io.OutputStream out=response.getOutputStream();
		response.setContentType("text/xml");
		response.setHeader("Cache-Control", "no-cache");
		argumentsReturnNode2XliveReturnNode();
		addProcessMethodNames();
		Element arguments_return_node = getArgumentsReturnNode();
		Document document = null;
		try{
			document = new xXmlDocument().createDocument();
			Node result=null;
			while((result=arguments_return_node.getFirstChild())!=null){
				document.appendChild(document.adoptNode(result));
			}
		}catch(ParserConfigurationException pce){
			pce.printStackTrace();
			throw new xSystemException(this.getSessionId(), "responseWebService", pce.getLocalizedMessage());
		}
		/* GAE */
		xlive.xResponseWebService ws = new xlive.xResponseWebService();
		ws.responseWebService(out, document, this.getSessionId());
		/*
		try {
			SOAPMessage message = MessageFactory.newInstance().createMessage();
			SOAPPart soap_part = message.getSOAPPart();
			SOAPEnvelope envelope = soap_part.getEnvelope();
			envelope.addNamespaceDeclaration(xSoapClient.XSI_NAMESPACE_PREFIX, xSoapClient.XSI_NAMESPACE_URI);
			envelope.addNamespaceDeclaration(xSoapClient.XSD_NAMESPACE_PREFIX, xSoapClient.XSD_NAMESPACE_URI);
			//SOAPHeader header = envelope.getHeader();
			SOAPBody body = envelope.getBody();
			body.addDocument(document);
			message.saveChanges();
			message.writeTo(out);
		} catch (UnsupportedOperationException e) {
			e.printStackTrace();
			throw new xSystemException(this.getSessionId(), "responseWebService", e.getLocalizedMessage());
		} catch (SOAPException e) {
			e.printStackTrace();
			throw new xSystemException(this.getSessionId(), "responseWebService", e.getLocalizedMessage());
		}
		*/
		out.flush();
		out.close();
	}
	public void responseService() throws ServletException, IOException, xSystemException{
		if(response == null) return;
		if(customizeResponse)return;
		if(isRequestFromWebService()){
			responseWebService();
			return;
		}
		java.io.OutputStream out=response.getOutputStream();
		response.setContentType("text/xml");
		response.setHeader("Cache-Control", "no-cache");
		argumentsReturnNode2XliveReturnNode();
		addProcessMethodNames();
		Element arguments_return_node = getArgumentsReturnNode();
		Document document = null;
		if(styleSheet != null){
			try{
				document = new xXmlDocument().createDocument();
			}catch(ParserConfigurationException pce){}
			String path=getHttpContextPath();
			path = (styleSheet.startsWith("http:")) ? styleSheet :
				   (styleSheet.startsWith("/")) ? path+styleSheet : path+"/"+styleSheet;
			ProcessingInstruction xmlstylesheet = document.createProcessingInstruction("xml-stylesheet","type=\"text/xsl\" href=\""+path+"\"");
			document.insertBefore(xmlstylesheet, document.getDocumentElement());
			document.appendChild(document.adoptNode(arguments_return_node));
		}
		try{
			if(document != null)new xXmlDocument().Transform(document, out);
			else new xXmlDocument().Transform(arguments_return_node, out);
		}catch(TransformerConfigurationException tcx){
			tcx.printStackTrace();
			throw new xSystemException(this.getSessionId(), "responseService", tcx.getLocalizedMessage());
	    }catch(TransformerException te){
	    	te.printStackTrace();
			throw new xSystemException(this.getSessionId(), "responseService", te.getLocalizedMessage());
	    }
		//
		out.flush();
		out.close();
	}
	private void argumentsReturnNode2XliveReturnNode(){
		Element arguments_return_node = getArgumentsReturnNode();
		if("xlive".equals(arguments_return_node.getNodeName())) return;
		if(methodException != null){
			Element exception=xWebInformation.createElement("exception");
			arguments_return_node.appendChild(exception);
			//
			String method_name=methodException.getMethodName();
			if(method_name != null && method_name.trim().length() > 0){
				Element method_exception=xWebInformation.createElement(method_name);
				exception.appendChild(method_exception);
				//
				method_exception.appendChild(xWebInformation.createElement("message",methodException.getMessage()));
				String error_code=methodException.getErrorCode();
				if(error_code != null)	method_exception.appendChild(xWebInformation.createElement("error-code",error_code));
				String error_state=methodException.getErrorState();
				if(error_state != null)	method_exception.appendChild(xWebInformation.createElement("error-state",error_state));
				//
			}else{
				exception.appendChild(xWebInformation.createElement("message",methodException.getMessage()));
				String error_code=methodException.getErrorCode();
				if(error_code != null)	exception.appendChild(xWebInformation.createElement("error-code",error_code));
				String error_state=methodException.getErrorState();
				if(error_state != null)	exception.appendChild(xWebInformation.createElement("error-state",error_state));
			}
			System.out.println("exception :\n"+xXmlDocument.nodeToString(exception));
		}
		arguments_return_node.getOwnerDocument().renameNode(arguments_return_node, null, "xlive");
	}
	private void addProcessMethodNames(){
		Element arguments_return_node = getArgumentsReturnNode();
		try{
			XPath xp = XPathFactory.newInstance().newXPath();
			if(processXliveMethods != null){
				NodeList methods=(NodeList)xp.evaluate("./method", processXliveMethods, XPathConstants.NODESET);
				String method_names="";
				for(int i =0 ; i < methods.getLength(); ++i){
					Element request_method = (Element)methods.item(i);
					String method_name=request_method.getAttribute("name");
					if(method_names.length()>0) method_names +=";";
					method_names += method_name;
				}
				arguments_return_node.setAttribute("methodNames",method_names);
			}
		}catch(XPathExpressionException e){
			e.printStackTrace();
		}
	}
	public void responseToOutputStream(OutputStream output) throws IOException, xSystemException{
		argumentsReturnNode2XliveReturnNode();
		addProcessMethodNames();
		//
		Element arguments_return_node = getArgumentsReturnNode();
		Document document = null;
		if(styleSheet != null){
			try{
				document = new xXmlDocument().createDocument();
			}catch(ParserConfigurationException pce){}
			String path=getHttpContextPath();
			path = (styleSheet.startsWith("http:")) ? styleSheet :
				   (styleSheet.startsWith("/")) ? path+styleSheet : path+"/"+styleSheet;
			ProcessingInstruction xmlstylesheet = document.createProcessingInstruction("xml-stylesheet","type=\"text/xsl\" href=\""+path+"\"");
			document.insertBefore(xmlstylesheet, document.getDocumentElement());
			document.appendChild(document.adoptNode(arguments_return_node));
		}
		try{
			if(document != null)new xXmlDocument().Transform(document, output);
			else new xXmlDocument().Transform(arguments_return_node, output);
		}catch(TransformerConfigurationException tcx){
			tcx.printStackTrace();
			throw new xSystemException(this.getSessionId(), "responseService", tcx.getLocalizedMessage());
	    }catch(TransformerException te){
	    	te.printStackTrace();
			throw new xSystemException(this.getSessionId(), "responseService", te.getLocalizedMessage());
	    }
	}
	///////
	protected void setDisposing(){
		disposing=true;
	}
	public boolean isDisposing(){
		return disposing;
	}
	/*
	public String getLifeCycle(Element object_node){
		String life_cycle=object_node.getAttribute("lifeCycle");
		return (life_cycle!=null && life_cycle.trim().length()>0)?life_cycle:"request";
	}
	protected boolean lifeCycleTraceRegister(Element object_node, String life_cycle){
		if(life_cycle.equals("method")) return true;
		if(life_cycle.equals("server") && !isAdministrator) return false;
		life_cycle=(life_cycle!=null && life_cycle.trim().length()>0)?life_cycle:"request";
		String path_object_name=xWebInformation.nodePathInfo(object_node);
		if("session".equals(life_cycle)) 
			 return sessionData.addTracePath(path_object_name);
		else return addRequestTrace(path_object_name);
	}
	public String shiftLastTraceObject(String life_cycle){
		if(life_cycle.equals("server") && !isAdministrator) return null;
		if("session".equals(life_cycle)) return sessionData.shiftLastTraceObject();
		return shiftLastTrace();
	}
	*/
	////
	public void dumpArguments(){ //processXliveMethods
		System.out.println("\n\nprocessXliveMethods :\n"+xXmlDocument.nodeToString(processXliveMethods));
		System.out.println("\n\nargumentsNode :\n"+xXmlDocument.nodeToString(argumentsNode));
	}
	
}
