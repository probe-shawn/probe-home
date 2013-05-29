package xlive;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

import javax.servlet.ServletContext;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import xlive.google.ds.xHash;
import xlive.xml.xXmlDocument;

public class xServerConfig implements xContextObjectImp {
	
	private final String Tag = "xServerConfig";
	private ServletContext servletContext;
	
	private static String serverCode=null;
	private static Element configNode=null;
	//
	private static Vector<String> protectedDirectories=null;
	private static Element registerReturnMessage=null; 
	
	public xServerConfig(ServletContext servlet_context) {
		servletContext = servlet_context;
		servletContext.setAttribute(Tag, this);
		initial();
	}
	public boolean initial(){
		try{
			return initialize();
		}catch(xSystemException e){
			xSystemException.logSystemException(e);
		}
		return false;
	}
	private boolean initialize() throws xSystemException {
		getServerCode();
		InputStream server_dot_stream=servletContext.getResourceAsStream("/WEB-INF/$.xml");
		try {
			configNode = (Element) new xXmlDocument().createDocument(server_dot_stream).getDocumentElement();
		} catch (SAXParseException e) {
			e.printStackTrace();
			throw new xSystemException("serverConfig $.xml", "serverConfig", "SAXParseException :"+e.getLocalizedMessage());
		} catch (SAXException e) {
			e.printStackTrace();
			throw new xSystemException("serverConfig $.xml", "serverConfig", "SAXException :"+e.getLocalizedMessage());
		} catch (IOException e) {
			e.printStackTrace();
			throw new xSystemException("serverConfig $.xml", "serverConfig", "IOException :"+e.getLocalizedMessage());
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			throw new xSystemException("serverConfig $.xml", "serverConfig", "ParserConfigurationException :"+e.getLocalizedMessage());
		}catch(Exception e){
			e.printStackTrace();
			throw new xSystemException("serverConfig $.xml", "serverConfig", "exception :"+e.getLocalizedMessage());
		}
		return true;
	}
	public void destroy(){
		servletContext=null;
		dispose();
	}
	public String getDescription(){
		return Tag;
	}

	public static void dispose(){
	}
	public static xServiceContext getServiceContext() throws xSystemException{
		return  new xServiceContext(null);
	}
	public static String getConfigInformtion(String xpath){
		XPath xp = XPathFactory.newInstance().newXPath();	
		try{
			return (String) xp.evaluate(xWebInformation.xPathValidate(xpath), configNode);
		}catch(XPathExpressionException xee){
			xee.printStackTrace();
		}
		return null;
	}
	public static void setRegisterReturnNode(Element node){
		registerReturnMessage=node;
	}
	public static String getRegisterReturnMessage(String xpath){
		if(registerReturnMessage == null)return "";
		XPath xp = XPathFactory.newInstance().newXPath();	
		try{
			return (String) xp.evaluate(xWebInformation.xPathValidate(xpath), registerReturnMessage);
		}catch(XPathExpressionException xee){
			xee.printStackTrace();
		}
		return null;
		
	}
	public static boolean isProtectedDirectory(String path_info){
		if(protectedDirectories==null){
			protectedDirectories= new Vector<String>();
			try{
				XPath xp = XPathFactory.newInstance().newXPath();
				NodeList protected_nodes = (NodeList)xp.evaluate("./protected/directory", configNode, XPathConstants.NODESET);
				for(int i=0;i<protected_nodes.getLength();++i){
					Node item=protected_nodes.item(i);
					String dir=item.getTextContent().toLowerCase();
					dir=dir.startsWith("/") ? dir : "/"+dir;
					dir=dir.endsWith("/")? dir:dir+"/";
					protectedDirectories.add(dir);
				}
			}catch(XPathExpressionException xee){
				xee.printStackTrace();
			}
		}
		path_info=path_info.toLowerCase();
		for(int i=0;i<protectedDirectories.size(); ++i){
			if(path_info.startsWith(protectedDirectories.get(i))) return true;
		}
		return false;
	}
	public static String getServerCode(){
		return getServerCode(false);
	}
	public static String getServerCode(boolean rebuild){
		if(xProbeServlet.isGAE()) {
			if(serverCode != null && !rebuild) return serverCode;
			xHash hash = new xHash("_server");
			serverCode = hash.getString("serverCode");
			if(serverCode != null && !rebuild) 	return serverCode;
			serverCode = String.valueOf(System.currentTimeMillis());
			hash.putString("serverCode", serverCode);
			return serverCode;
		}
		if(serverCode != null && !rebuild) return serverCode;
		try{
			File lib_dir=new File(System.getProperty("java.ext.dirs").split(";")[0]).getParentFile();
			File f1= new File(lib_dir, "sound.properties");
			File f2= new File(lib_dir, "calendars.properties");
			File f3= new File(lib_dir, "flavormap.properties");
			File f4= new File(lib_dir, "content-types.properties");
			File f5= new File(lib_dir, "logging.properties");
			File f6= new File(lib_dir, "net.properties");
			long time1=f1.lastModified();
			long time2=f2.lastModified();
			long time3=f3.lastModified();
			long time4=f4.lastModified();
			long time5=f5.lastModified();
			long time6=f6.lastModified();
			long code=0;
			if(time1%1000==111 && time2%100==99 && !rebuild){
				code=(time2%1000)/100;
				code=code*1000+time3%1000;
				code=code*1000+time4%1000;
				code=code*1000+time5%1000;
				code=code*1000+time6%1000;
			}else{
				code=System.currentTimeMillis();
				String scode=String.valueOf(code);
				f1.setLastModified((time1/1000)*1000+111);
				long sec=Integer.parseInt(scode.substring(0, 1));
				f2.setLastModified((time2/1000)*1000+sec*100+99);
				sec=Integer.parseInt(scode.substring(1, 4));
				f3.setLastModified((time3/1000)*1000+sec);
				sec=Integer.parseInt(scode.substring(4, 7));
				f4.setLastModified((time4/1000)*1000+sec);
				sec=Integer.parseInt(scode.substring(7, 10));
				f5.setLastModified((time5/1000)*1000+sec);
				sec=Integer.parseInt(scode.substring(10));
				f6.setLastModified((time6/1000)*1000+sec);
			}
			serverCode=String.valueOf(code);
			return serverCode;
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}

}
