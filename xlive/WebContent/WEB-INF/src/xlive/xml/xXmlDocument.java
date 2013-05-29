package xlive.xml;

import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import javax.xml.parsers.*;
import org.xml.sax.*;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.w3c.dom.*;

import java.io.*;
import java.util.*;

public class xXmlDocument {
	public static int XML_STATUS_OK = 1;
	public static int XML_STATUS_FAIL = -1;
	public static int XML_STATUS_PCEEXP = -2;
	public static int XML_STATUS_SPEEXP = -3;
	public static int XML_STATUS_SXEEXP = -4;
	public static int XML_STATUS_IOEXP = -5;
	public static int XML_STATUS_TCXEXP = -6;
	public static int XML_STATUS_TEEXP = -7;
	public static int XML_STATUS_FNFEXP = -8;
	
	private int Status = XML_STATUS_OK;
	private String Message = "OK";
    public xXmlDocument(){}
    public int getStatus(){
    	return Status;
    }
    public String getExplain(){
    	return Message;
    }
    public Document createDocument() throws ParserConfigurationException{
		Status = XML_STATUS_OK;
		Message = "OK";
		//try {
    		return DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
    	//}  catch(ParserConfigurationException pce){
    	//	Status = XML_STATUS_PCEEXP;
    	//	Message = pce.getLocalizedMessage();
    	//	return null;
    	//}
    }
	public Document createDocument(String file_name) throws FileNotFoundException, SAXParseException, SAXException, IOException, ParserConfigurationException{
        //try {
	        return createDocument(new FileInputStream(file_name));
        //}
        //catch(FileNotFoundException fnf){
	    //    Status = XML_STATUS_FNFEXP;
	    //	Message = fnf.getLocalizedMessage();
	    //	return null;
        //}
	}
	public Document createDocument(File aFILE) throws FileNotFoundException,SAXParseException, SAXException, IOException, ParserConfigurationException{
        //try {
	        return createDocument(new FileInputStream(aFILE));
        //}
        //catch(FileNotFoundException fnf){
	    //    Status = XML_STATUS_FNFEXP;
	    //	Message = fnf.getLocalizedMessage();
	    //	return null;
        //}
	}
	public Document createDocument(InputStream input_stream) throws SAXParseException, SAXException, IOException, ParserConfigurationException{
		Status = XML_STATUS_OK;
		Message = "OK";
		//try {
	        return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(input_stream);
        //}
        //catch(SAXParseException spe){
    	//	Status = XML_STATUS_SPEEXP;
    	//	Message = spe.getLocalizedMessage();
        //}
        //catch(SAXException sxe) {
    	//	Status = XML_STATUS_SXEEXP;
    	//	Message = sxe.getLocalizedMessage();
        //}
	    //catch(IOException ioe) {
    	//	Status = XML_STATUS_IOEXP;
    	//	Message = ioe.getLocalizedMessage();
	    //}
	    //catch(ParserConfigurationException pce){
    	//	Status = XML_STATUS_PCEEXP;
    	//	Message = pce.getLocalizedMessage();
	    //}
	    //catch(Exception eall){
	    //    Status = XML_STATUS_FAIL;
	    //	Message = eall.getLocalizedMessage();
	    //}
        //return null;    	
    }
	public boolean Transform(Document document, OutputStream output_stream) throws TransformerConfigurationException, TransformerException{
		Status = XML_STATUS_OK;
		Message = "OK";
	    //try {
	        TransformerFactory.newInstance().newTransformer().transform(new DOMSource(document), new StreamResult(output_stream));
	        return true;
	    //} 
	    //catch(TransformerConfigurationException tcx){
	    //    Status = XML_STATUS_TCXEXP;
	    //	Message = tcx.getLocalizedMessage();
	    //}
	    //catch(TransformerException te){
	    //    Status = XML_STATUS_TEEXP;
	    //	Message = te.getLocalizedMessage();
	    //}
	    //return false;

	}
	public boolean Transform(Node node, OutputStream output_stream) throws TransformerConfigurationException, TransformerException{
		Status = XML_STATUS_OK;
		Message = "OK";
	    //try {
	        TransformerFactory.newInstance().newTransformer().transform(new DOMSource(node), new StreamResult(output_stream));
	        return true;
	    //} 
	    //catch(TransformerConfigurationException tcx){
	    //    Status = XML_STATUS_TCXEXP;
	    //	Message = tcx.getLocalizedMessage();
	    //}
	    //catch(TransformerException te){
	    //    Status = XML_STATUS_TEEXP;
	    //	Message = te.getLocalizedMessage();
	    //}
	    //return false;
	}
	public static void extendNodes(Node target, Node source, String operation){
		extendNodes(target, source, operation, true, Integer.MAX_VALUE, false, null, null);
	}
	public static void extendNodes(Node target, Node source, String operation, boolean clone_node){
		extendNodes(target, source, operation, clone_node, Integer.MAX_VALUE, false, null, null);
	}
	public static void extendNodes(Node aTARGET, Node aSOURCE, String aOPERATION, boolean aCLONE_NODE, int aDEEP, boolean aEXTEND_EMPTY, String[] aINCLUDE_NAME, String[] aEXCLUDE_NAME){
		if(aTARGET==null || aSOURCE == null) return;
		if(!aSOURCE.hasChildNodes())return;
		NodeList node_list=aSOURCE.getChildNodes();
		Vector<Node> children=new Vector<Node>(node_list.getLength());
		for(int i=0;i<node_list.getLength();++i)children.add(node_list.item(i));
		Hashtable<Node, String> processed_nodes = new Hashtable<Node, String>();
		for(int i=0;i<children.size();++i){
			Node item=(Node)children.get(i);
			if(item == null)break;
			String name=item.getNodeName();
			boolean do_it=true;
			if(aEXCLUDE_NAME !=null){
				for(int m=0; m<aEXCLUDE_NAME.length; ++m) if(aEXCLUDE_NAME[m].equals(name)) {do_it=false;break;};
			}
			if(aINCLUDE_NAME !=null){
				do_it=false;
				for(int m=0; m<aINCLUDE_NAME.length; ++m) if(aINCLUDE_NAME[m].equals(name)) {do_it=true;break;}
			}
			if(!do_it) continue;
			item.normalize();
			Node same=findSameChild(aTARGET, item, processed_nodes);
			if(same != null){
				processed_nodes.put(same, "");
				if(item.getNodeType() == Node.TEXT_NODE || item.getNodeType()==Node.CDATA_SECTION_NODE || item.getNodeType()==Node.COMMENT_NODE){
					if("overwrite".equals(aOPERATION)) same.setTextContent(item.getTextContent());
					else if("extend".equals(aOPERATION)){
							if(aEXTEND_EMPTY && (same.getTextContent()==null || same.getTextContent().trim().length()==0))
								same.setTextContent(item.getTextContent());
						 }
					else if("append".equals(aOPERATION)){
							if(!same.getTextContent().trim().equals(item.getTextContent().trim())){
								Node new_text=item.cloneNode(true);
								Node new_text_parent=item.getParentNode().cloneNode(false);
								new_text_parent.appendChild(new_text);
								same.getParentNode().getParentNode().appendChild(aTARGET.getOwnerDocument().adoptNode(new_text_parent));
							}
						}
					else if("remove".equals(aOPERATION)){
							Node same_parent = same.getParentNode();
							same_parent.removeChild(same);
							while(same_parent != null && !same_parent.hasChildNodes()) {
								Node node=same_parent;
								same_parent=same_parent.getParentNode();
								same_parent.removeChild(node);
							}
							continue;
						}
				}else if(!item.hasChildNodes()){
					if("remove".equals(aOPERATION)){
						Node same_parent = same.getParentNode();
						same_parent.removeChild(same);
						while(same_parent != null && !same_parent.hasChildNodes()) {
							Node node=same_parent;
							same_parent=same_parent.getParentNode();
							same_parent.removeChild(node);
						}
					}
					if("append".equals(aOPERATION)){
						Node new_node=aTARGET.getOwnerDocument().adoptNode((aCLONE_NODE) ? item.cloneNode(true) : item);
						same.getParentNode().appendChild(new_node);
					}
					continue;
				}
				
				if("overwrite".equals(aOPERATION) || "extend".equals(aOPERATION)) extendAttributes(same, item, aOPERATION, aEXTEND_EMPTY);
				if(aDEEP > 0)extendNodes(same, item, aOPERATION, aCLONE_NODE, aDEEP-1, aEXTEND_EMPTY, null, null);
			}else {
				if(aDEEP > 0){
					if("overwrite".equals(aOPERATION) || "extend".equals(aOPERATION) ||"append".equals(aOPERATION)) {
						Node new_node=aTARGET.getOwnerDocument().adoptNode((aCLONE_NODE) ? item.cloneNode(true) : item);
						aTARGET.appendChild(new_node);
						processed_nodes.put(new_node, "");
					}
				}
			}
		}
	}
	private static Node findSameChild(Node target, Node source, Hashtable<Node, String> processed_node){
		if(!target.hasChildNodes())return null;
		String node_name=source.getNodeName();
		short node_type=source.getNodeType();
		String id=(node_type==Node.ELEMENT_NODE)?((Element)source).getAttribute("id"):null;
		String name=(node_type==Node.ELEMENT_NODE)?((Element)source).getAttribute("name"):null;
		Node child_node=target.getFirstChild();
		do{
			if(child_node==null)break;
			if(processed_node.get(child_node) == null){
				if(node_name.equalsIgnoreCase(child_node.getNodeName()) && node_type==child_node.getNodeType())	{
					if(node_type==Node.ELEMENT_NODE){
						String child_id=((Element)child_node).getAttribute("id");
						String child_name=((Element)child_node).getAttribute("name");
						if(child_id!=null && child_id.trim().length()>0 && !child_id.equals(id))continue; 
						if(id!=null && id.trim().length()>0 && !id.equals(child_id)) continue; 
						if(child_name!=null && child_name.trim().length()>0 && !child_name.equals(name)) continue; 
						if(name!=null && name.trim().length()>0 && !name.equals(child_name)) continue; 
					}
					return child_node;
				}
			}
		}while((child_node=child_node.getNextSibling())!=null);
		return null;
	}
	/*
	private static Node findSameChild(Node target, Node source, Hashtable<Node, String> processed_node){
		if(!target.hasChildNodes())return null;
		NodeList nls = target.getChildNodes();
		String node_name=source.getNodeName();
		short node_type=source.getNodeType();
		String id=(node_type==Node.ELEMENT_NODE)?((Element)source).getAttribute("id"):null;
		String name=(node_type==Node.ELEMENT_NODE)?((Element)source).getAttribute("name"):null;
		for(int i=0,n=nls.getLength(); i<n;++i){
			Node child_node=nls.item(i);
			if(processed_node.get(child_node) != null) continue;
			if(node_name.equalsIgnoreCase(child_node.getNodeName()) && node_type==child_node.getNodeType())	{
				if(node_type==Node.ELEMENT_NODE){
					String child_id=((Element)child_node).getAttribute("id");
					String child_name=((Element)child_node).getAttribute("name");
					if(child_id!=null && child_id.trim().length()>0 && !child_id.equals(id))continue; 
					if(id!=null && id.trim().length()>0 && !id.equals(child_id)) continue; 
					if(child_name!=null && child_name.trim().length()>0 && !child_name.equals(name)) continue; 
					if(name!=null && name.trim().length()>0 && !name.equals(child_name)) continue; 
				}
				return child_node;
			}
		}
		return null;
	}
	*/
	
	public static void extendAttributes(Node aTARGET, Node aSOURCE, String aOPERATION, boolean aEXTEND_EMPTY){
		if(aTARGET==null || aSOURCE == null) return;
		if(!aSOURCE.hasAttributes()) return;
		NamedNodeMap map = aSOURCE.getAttributes();
		for(int i=0,n=map.getLength(); i< n; ++i){
			String name = map.item(i).getNodeName();
			if("overwrite".equals(aOPERATION))
				((Element)aTARGET).setAttribute(name, map.item(i).getNodeValue());
			else if("extend".equals(aOPERATION)){
					if(!aEXTEND_EMPTY && ((Element)aTARGET).hasAttribute(name)) continue;
					String value = ((Element)aTARGET).getAttribute(name);
					if(value==null ||value.trim().length()==0) ((Element)aTARGET).setAttribute(name, map.item(i).getNodeValue());
				}
			
		}
	}
	public static String getAttributeString(Element aNODE, String aTTR, String aDEFAULT_STRING){
		String tmp=aNODE.getAttribute(aTTR);
		if(tmp==null || tmp.trim().length()==0) return aDEFAULT_STRING;
		return tmp;
	}
	public static int getAttributeInt(Element aNODE, String aTTR, int aDEFAULT_VALUE){
		String tmp=aNODE.getAttribute(aTTR);
		if(tmp==null||tmp.trim().length()==0)return aDEFAULT_VALUE;
		try{
			int value=Integer.parseInt(tmp);
			return value;
		}catch(Exception e){}
		return aDEFAULT_VALUE;
	}
	public static boolean getAttributeBool(Element node, String attribute, boolean default_bool){
		String tmp=node.getAttribute(attribute);
		return (tmp==null||tmp.trim().length()==0) ? default_bool :
			   ("true".equalsIgnoreCase(tmp) ||"1".equalsIgnoreCase(tmp)) ? true : false;
	}
	public static Node stringToNode(String xml_string){
		try {
			return xXmlDocument.bytesToNode(xml_string.getBytes("utf-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
	public static Node bytesToNode(byte[] xml_bytes){
		try{
			ByteArrayInputStream is = new ByteArrayInputStream(xml_bytes);
			Document doc=new xXmlDocument().createDocument(is);
			if(doc != null) return doc.getDocumentElement();
		}catch(SAXParseException spe){
			spe.printStackTrace();
		}catch(SAXException sxe) {
			sxe.printStackTrace();
		}catch(IOException ioe) {
			ioe.printStackTrace();
		}catch(ParserConfigurationException pce){
			pce.printStackTrace();
		}
		return null;
	}
	public static String nodeToString(Node node){
		Properties prop = new Properties();
		prop.put("omit-xml-declaration", "yes");
		return nodeToString(node,prop);
	}
	public static String nodeToString(Node node, Properties output_properties){
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			Transformer transformer=TransformerFactory.newInstance().newTransformer();
			if(output_properties != null)transformer.setOutputProperties(output_properties);
			transformer.transform(new DOMSource(node), new StreamResult(baos));
			return new String(baos.toByteArray(),"utf-8");
	    }catch(TransformerConfigurationException tcx){
	    	tcx.printStackTrace();
	    }catch(TransformerException te){
	    	te.printStackTrace();
	    } catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	    return null;
	}
	public static byte[] nodeToBytes(Node node, Properties output_properties){
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			Transformer transformer=TransformerFactory.newInstance().newTransformer();
			if(output_properties != null)transformer.setOutputProperties(output_properties);
			transformer.transform(new DOMSource(node), new StreamResult(baos));
			return baos.toByteArray();
	    }catch(TransformerConfigurationException tcx){
	    	tcx.printStackTrace();
	    }catch(TransformerException te){
	    	te.printStackTrace();
	    }
	    return null;
	}

}
