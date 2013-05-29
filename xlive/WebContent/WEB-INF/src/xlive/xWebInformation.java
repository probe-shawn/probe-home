package xlive;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Level;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;


import org.w3c.dom.*;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import xlive.xml.xXmlDocument;
import xlive.method.*;
import xlive.method.logger.xLogger;

public class xWebInformation implements xContextObjectImp {
	
	private final String Tag = "xWebInformation";
	private static ServletContext servletContext=null;
	
	private static String webDirectory=null; //xlive
	private static Hashtable<String, Element> objectTable = new Hashtable<String, Element>();
	private static Document defaultDocument=null;
	//
	private static boolean systemInitialized=false;
	private static boolean serviceStop=false;
	//
	public xWebInformation(ServletContext servlet_context) {
		servletContext = servlet_context;
		servletContext.setAttribute(Tag,this);
		initial();
    }
	public boolean initial(){
		new xlive.method.logger.xLogger(servletContext.getRealPath("/"));
		xLogger.log(Level.INFO, "xWebInformation.initialize start ...");
		//ssl trust
		if(!xProbeServlet.isGAE()) xlive.ssl.NaiveTrustProvider.setAlwaysTrust(true);
		//
		systemInitialized=false;
		webDirectory = servletContext.getRealPath("/");
		webDirectory +=(webDirectory.endsWith(File.separator) ? "":File.separator);
		try {
			defaultDocument = new xXmlDocument().createDocument();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			xSystemException.logSystemException(new xSystemException("system", "initial : ParserConfigurationException", e.getMessage()));
			return false;
		}
		systemInitialized=true;
		xLogger.log(Level.INFO, "xWebInformation.initialized ... done"+"("+xServerConfig.getServerCode()+")");
		System.out.println("xWebInformation.initialized ... done"+"("+xServerConfig.getServerCode()+")");
		return true; 
	}
	public static String getWebDirectory(){
		return webDirectory;
	}
	public static boolean getSystemInitialized(){
		return systemInitialized;
	}
	public static boolean serviceStop(){
		return serviceStop;
	}
	public void destroy(){
		serviceStop=true;
		xLogger.dispose();
		objectTable.clear();
		servletContext=null;
		systemInitialized=false;
	}
	public String getDescription(){
		return "xWebInformation";
	}
	public static boolean dispatch(xServiceContext service_context, String object_path) throws xSystemException{
		xLogger.log(Level.FINE, service_context.getSessionId(), "xWebInformation", "dispatch", "xWebInformation.dispatch : "+object_path, 0);
		//
		if(object_path==null) {
			service_context.doNextProcess(false);
			xMethodException method_exception=new xMethodException(null, null, "dispatch", "object path is null");
			service_context.logMethodException(method_exception);
			throw new xSystemException(service_context.getSessionId(), "dispatch", "methodException : "+method_exception.getMessage());
		}
		//path="/web-inf"+path;
		if(service_context.doNextProcess()){
			Element object_node=xWebInformation.getObjectNode(object_path);
			if(object_node != null)	{
				if(service_context.doNextProcess()){
					try{
						XPath xp = XPathFactory.newInstance().newXPath();
						Element process_xlive_methods=service_context.getProcessXliveMethods();
						if(process_xlive_methods != null){
							NodeList methods=(NodeList)xp.evaluate("./method", process_xlive_methods, XPathConstants.NODESET);
							for(int i =0 ; i < methods.getLength(); ++i){
								Element request_method = (Element)methods.item(i);
								String method_name=request_method.getAttribute("name");
								Element web_method_node=xWebInformation.getMethodNode(object_node, method_name);
								try{
									if(web_method_node != null){
										Element method_arguments=(Element) xp.evaluate("./arguments", request_method, XPathConstants.NODE);
										if(method_arguments != null){
											service_context.argumentsOperation(method_arguments, "overwrite", true);
										}
										xWebInformation.processMethod(service_context, web_method_node);
									}else throw new xMethodException(object_path, null, "dispatch", "URL method ("+method_name+") not found");
								}catch(xMethodException me){
									me.printStackTrace();
									service_context.logMethodException(me);
									service_context.doNextProcess(false);
									throw new xSystemException(service_context.getSessionId(),"dispatch", "methodException :"+me.getMessage());
								}
								if(!service_context.doNextProcess()) break;
							}
						}
					}catch(XPathExpressionException e){
						e.printStackTrace();
						throw new xSystemException(service_context.getSessionId(),"dispatch", "methods :"+e.getMessage());
					}
				}
			}else{
				service_context.doNextProcess(false);
				xMethodException method_exception=new xMethodException(null, null, "dispatch", "object ("+object_path+") not found");
				service_context.logMethodException(method_exception);
				throw new xSystemException(service_context.getSessionId(), "dispatch", "methodException :"+method_exception.getMessage());
			}
		}
		return service_context.doNextProcess(); 
	}
	public static Element getObjectNode(String object_path){
		StringBuffer buf = new StringBuffer(object_path);
		for(int i=0,n=buf.length(); i < n; ++i){
			if(buf.charAt(i)=='.') buf.setCharAt(i, '/');
		}
		String name=buf.toString();
		boolean is_$ = false;
		Element object_element =  xWebInformation.objectTable.get(name);
		if(object_element != null) return object_element;
		String file_name = "/WEB-INF"+(name.startsWith("/")?"":"/")+name+".xml";
		InputStream xml_stream=servletContext.getResourceAsStream(file_name);
		if(xml_stream == null){
			file_name = "/WEB-INF"+(name.startsWith("/")?"":"/")+name+"/$.xml";
			xml_stream=servletContext.getResourceAsStream(file_name);
			is_$ = true;
		}
		if(xml_stream == null) return null;
		try {
			object_element = (Element)xWebInformation.defaultDocument.adoptNode(new xXmlDocument().createDocument(xml_stream).getDocumentElement());
			object_element.setAttribute("_name", name);
			object_element.setAttribute("_resPath", file_name);
			XPath xp = XPathFactory.newInstance().newXPath();
			try {
				Element prop=(Element)xp.evaluate("properties", object_element, XPathConstants.NODE);
				Element get=(Element)xp.evaluate("get-properties", prop, XPathConstants.NODE);
				if(get==null){
					prop.appendChild(xWebInformation.createElement("get-properties"));
					Element method = xWebInformation.createElement("method");
					object_element.appendChild(method);
					method.setAttribute("name", "get-properties");
					method.setAttribute("useClass", "xlive.method.sys.xGetPropertiesMethod");
					method.setAttribute("useType", "apply");
				}
			} catch (XPathExpressionException e) {
			}
			xWebInformation.objectTable.put(name, object_element);
			return object_element;
		} catch (SAXParseException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		return null;
	}
	private static Element getMethodNode(Element object_node, String method_name){
		XPath xp = XPathFactory.newInstance().newXPath();
		try {
			return (Element) xp.evaluate("./method[@name=\""+method_name+"\"]", object_node, XPathConstants.NODE);
		}catch(XPathExpressionException xee){
			xee.printStackTrace();
		}
		return null;
	}
	private static Object processMethod(xServiceContext service_context, Element method)throws xMethodException{
		return processMethod(service_context,method,null,null);
	}
	private static Object processMethod(xServiceContext service_context, Element method, String apply_class_name, String apply_super_method_name) throws xMethodException{
		String class_name=null;
		try{
			Object result=null;
			class_name=apply_class_name;
			if(class_name == null || class_name.trim().length() == 0) class_name=method.getAttribute("useClass");
			if(class_name == null || class_name.trim().length() == 0) class_name="xlive.method.xDefaultMethod";
			xDefaultMethod method_class = (xDefaultMethod)Class.forName(class_name.trim()).newInstance();
			method_class.setup(service_context, method, apply_super_method_name);
			result=method_class.process();
			method_class.cleanUp();
			//
			return result;
		}catch(IllegalAccessException iac){
			iac.printStackTrace();
			String[] path_name=xWebInformation.getObjectPathAndMethodName(method, apply_super_method_name);
			throw new xMethodException(path_name[0], path_name[1],"processMethod", "IllegalAccessException("+class_name+") "+iac.getLocalizedMessage());
		}catch(ClassNotFoundException cnf){
			cnf.printStackTrace();
			String[] path_name=xWebInformation.getObjectPathAndMethodName(method, apply_super_method_name);
			throw new xMethodException(path_name[0], path_name[1],"processMethod", "ClassNotFoundException("+class_name+") "+ cnf.getLocalizedMessage());
		}catch(InstantiationException ine){
			ine.printStackTrace();
			String[] path_name=xWebInformation.getObjectPathAndMethodName(method, apply_super_method_name);
			throw new xMethodException(path_name[0], path_name[1],"processMethod", "InstantiationException("+class_name+") "+ine.getLocalizedMessage());
		}
	}
	private static String[] getObjectPathAndMethodName(Element method_node, String apply_super_method_name){
		String[] path_name = new String[2];
		try{
			XPath xp = XPathFactory.newInstance().newXPath();
			Element object_node=(Element)xp.evaluate("ancestor::object", method_node,XPathConstants.NODE);
			path_name[0]=object_node.getAttribute("_name");
			if(apply_super_method_name != null && apply_super_method_name.trim().length() > 0)
				path_name[1]= apply_super_method_name;
			else{
				path_name[1]=method_node.getNodeName();
				if("method".equals(path_name[1])) path_name[1]=method_node.getAttribute("name");
				else{
					int index=path_name[1].lastIndexOf(".");
					if(index >=0)path_name[1]=path_name[1].substring(index+1);
				}
			}
		}catch(Exception e){}
		return path_name;
	}
	/******************/ 
	/* service public */
	public static Element createElement(String element_name){
		return xWebInformation.createElement(element_name, null);
	}
	public static Element createElement(String element_name, String text){
		Element element=xWebInformation.defaultDocument.createElement(element_name);
		if(text!=null && text.trim().length()>0) element.setTextContent(text);
		return element;
	}
	public static Element[] createElements(String element_names){
		return createElements(element_names, null);
	}
	public static Element[] createElements(String element_names, String last_element_text){
		String[] names = element_names.split("\\.");
		Vector<Element> elements = new Vector<Element>();
		Element current_node=null;
		Element first_node=null;
		for(int i = 0; i < names.length; ++i){
			if(names[i].trim().length()==0)continue;
			Element node = xWebInformation.createElement(names[i],null);
			if(first_node == null) first_node=node;
			if(current_node != null) current_node.appendChild(node);
			current_node=node;
			elements.add(node);
		}
		if(current_node != null && (last_element_text != null && last_element_text.trim().length() > 0)) current_node.setTextContent(last_element_text);
		Element[] a = new Element[elements.size()];
		elements.toArray(a);
		return a;
	}
	public static String xPathValidate(String xpath){
		StringBuffer buf=new StringBuffer(xpath);
		int predicate=0;
		for(int i=0,n=buf.length(); i < n; ++i){
			char cc=buf.charAt(i);
			if(cc=='[')++predicate;
			if(cc==']')--predicate;
			if(predicate != 0)continue;
			if(i!=0 && cc=='.') buf.setCharAt(i, '/');
		}
		if(buf.charAt(0)=='.') return (buf.charAt(1) =='/') ? buf.toString(): buf.insert(1, '/').toString();
		if(buf.charAt(0)=='/') return buf.insert(0, '.').toString();
		return buf.insert(0, "./").toString();
	}
	public static Object processWebObjectMethod(xDefaultMethod default_method,String object_path, String method_name) throws xMethodException{
		Element object_node=(object_path==null)? default_method.getObjectNode() :getObjectNode(object_path);
		if(object_node != null){
			Element method_node=getMethodNode(object_node,method_name);
			if(method_node != null){
				return xWebInformation.processMethod(default_method.getServiceContext(), method_node);
			}
		}
		return null;
	}
	public static boolean isWebObjectExisted(xDefaultMethod default_method,String object_path) throws xMethodException{
		return getObjectNode(object_path) != null;
	}
	public static boolean processInternetWebObjectMethod(String server_url,String object_url, String method_name,xDefaultMethod default_method) throws xMethodException{
		Element[] xlive_process=xWebInformation.createElements("xlive.method");
		xlive_process[xlive_process.length-1].setAttribute("name", method_name);
		Element arguments=null;
		try {
			arguments=(Element)default_method.getServiceContext().getArguments("./return", XPathConstants.NODE);
			arguments=(Element)arguments.getParentNode();
			if(arguments != null)xlive_process[xlive_process.length-1].appendChild(arguments.cloneNode(true));
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		//
		String target_object_url=object_url.replaceAll("\\.","/");
	    boolean valid=true;
	    String connection_url=server_url+"/xlive/web/"+target_object_url;
        URL url = null;
        HttpURLConnection connection = null;
        String why=null;
        try {
            DataOutputStream output = null;
            url = new URL(connection_url);
            connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("X-XLive-Version", "1.0");
            connection.setRequestProperty("X-XLive-Content", "xml");
            connection.setRequestProperty("X-XLive-ServerCode", xServerConfig.getServerCode());
            output = new DataOutputStream(connection.getOutputStream());
    	    TransformerFactory.newInstance().newTransformer().transform(new DOMSource(xlive_process[0]), new StreamResult(output));
            output.flush();
            output.close();
            output = null;
            InputStream in = new DataInputStream(connection.getInputStream());
			Document return_document=new xXmlDocument().createDocument(in);
			Element return_root=return_document.getDocumentElement();
			if("register".equals(method_name) && ("probe.service".equals(object_url) ||"probe/service".equals(object_url))) {
				xServerConfig.setRegisterReturnNode((Element)return_root.cloneNode(true));
			}
			return_document.renameNode(return_root, null,"return");
			XPath xp = XPathFactory.newInstance().newXPath();
			Element arguments_return_node=null;
			try {
				arguments_return_node = (Element)xp.evaluate("./return", arguments, XPathConstants.NODE);
			} catch (XPathExpressionException e) {
				e.printStackTrace();
			}
			if(arguments_return_node!=null) 
				 arguments.replaceChild(arguments.getOwnerDocument().adoptNode(return_root), arguments_return_node);
			else arguments.appendChild(arguments.getOwnerDocument().adoptNode(return_root));
            in.close();
        }catch(SocketException se){
        	se.printStackTrace();
        	valid=false;
        	why="SocketException :"+se.getLocalizedMessage();
        }catch(MalformedURLException mle) {
        	mle.printStackTrace();
        	valid=false;
        	why="MalformedURLException :"+mle.getLocalizedMessage();
        }catch(IOException ioe) {
        	ioe.printStackTrace();
        	valid=false;
        	why="IOException :"+ioe.getLocalizedMessage();
    	}catch(TransformerConfigurationException tcx){
    		tcx.printStackTrace();
    		valid=false;
    		why = "TransformerConfigurationException :"+tcx.getLocalizedMessage();
    	}catch(TransformerException te){
    		te.printStackTrace();
    		valid=false;
    		why = "TransformerException :"+te.getLocalizedMessage();
    	}catch(Exception ex) {
    		ex.printStackTrace();
    		valid=false;
    		why="Exception :"+ex.getLocalizedMessage();
        }finally {
           	try {if(connection != null) connection.disconnect();}catch(Exception econ){}
        }
        Element[] rets=xWebInformation.createElements("arguments.return."+method_name+".valid", String.valueOf(valid));
        default_method.getServiceContext().argumentsOperation(rets[0], "append", false);
        rets=xWebInformation.createElements("arguments.return."+method_name+".why", why);
        default_method.getServiceContext().argumentsOperation(rets[0], "append", false);
		return valid;
	}
	public static void responseNotReadyMessage(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException{
		java.io.OutputStream out=response.getOutputStream();
		response.setContentType("text/xml");
		response.setHeader("Cache-Control", "no-cache");
		Node[] return_nodes=xWebInformation.createElements("xlive.system.return", "System is not ready");
		try {
			new xXmlDocument().Transform(return_nodes[0], out);
			out.flush();
			out.close();
			out=null;
	    }catch(TransformerConfigurationException tcx){
	    	tcx.printStackTrace();
	    }catch(TransformerException te){
	    	te.printStackTrace();
	    }finally{
	    	if(out != null) out.close();
	    }
	}
	public static void responseDisconnectMessage(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException{
		java.io.OutputStream out=response.getOutputStream();
		response.setContentType("text/xml");
		response.setHeader("Cache-Control", "no-cache");
		Node[] return_nodes=xWebInformation.createElements("xlive.system.return", "dispose");
		try {
			new xXmlDocument().Transform(return_nodes[0], out);
			out.flush();
			out.close();
			out=null;
	    }catch(TransformerConfigurationException tcx){
	    	tcx.printStackTrace();
	    }catch(TransformerException te){
	    	te.printStackTrace();
	    }finally{
	    	if(out != null) out.close();
	    }
	}
	/*
	public static void responseSessionControl(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException{
		java.io.OutputStream out=response.getOutputStream();
		response.setContentType("text/xml");
		response.setHeader("Cache-Control", "no-cache");
		xSessionData sessionData=xWebInformation.getSessionData(null, true);
		response.addHeader("xsessionid", sessionData.getSessionId());
		try {
			Node[] return_nodes=xWebInformation.createElements("xlive.xsessionid", sessionData.getSessionId());
			new xXmlDocument().Transform(return_nodes[0], out);
			out.flush();
			out.close();
			out=null;
	    } catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}finally{
	    	if(out != null) out.close();
	    }
	}
	*/
}
