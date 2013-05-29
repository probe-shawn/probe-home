package xlive.method.soap;

import java.io.ByteArrayOutputStream;
import java.util.Properties;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class xOperationInfo {
		private String serviceName="";
		private String targetNamespace="";
		private String schemaElementFormDefault="";
		private String soapAddressLocation = "";
		private String soapBindingStyle = "document";
		private String soapBindingTransport= "";
		private String soapBindingVerb="";
		
		private String operationName = "";
		private String soapOperationStyle="document";
		private String soapOperationSoapAction = "";
		
		private String  inputMessageName = "";
		private Element inputMessage=null;
		private String  inputSoapBodyUse="literal";
		private String  inputSoapBodyNamespace="";
		private String  inputSoapBodyEncodingStyle="";
		
		private String  inputHeaderMessageName = "";
		private Element inputHeaderMessage=null;
		private String  inputSoapHeaderUse="literal";
		
		private String  outputMessageName = "";
		private Element outputMessage=null;
		private String  outputSoapBodyUse="literal";
		private String  outputSoapBodyNamespace="";
		private String  outputSoapBodyEncodingStyle="";
		
		private String  outputHeaderMessageName = "";
		private Element outputHeaderMessage=null;
		private String  outputSoapHeaderUse="literal";
		
		private String faultMessageName="";
		private Element faultMessage=null;
		private String  faultSoapBodyUse="literal";
		
		public xOperationInfo(){}
		public void setServiceName(String value){
			serviceName=value;
		}
		public String getServiceName(){
			return serviceName;
		}
		public void setTargetNamespace(String value){
			targetNamespace=value;
		}
		public String getTargetNamespace(){
			return targetNamespace;
		}
		public void setSchemaElementFormDefault(String value){
			schemaElementFormDefault=value;
		}
		public String getSchemaElementFormDefault(){
			return schemaElementFormDefault;
		}
		public void setSoapAddressLocation(String value){
			soapAddressLocation = value;
		}
		public String getSoapAddressLocation(){
			return soapAddressLocation;
		}
		public void setSoapBindingStyle(String value){
			soapBindingStyle = value;
		}
		public String getSoapBindingStyle(){
			return soapBindingStyle;
		}
		public void setSoapBindingTransport(String value){
			soapBindingTransport = value;
		}
		public String getSoapBindingTransport(){
			return soapBindingTransport;
		}
		public void setSoapBindingVerb(String value){
			soapBindingVerb = value;
		}
		public String getSoapBindingVerb(){
			return soapBindingVerb;
		}
		public void setSoapOperationSoapAction(String value){
			soapOperationSoapAction = value;
		}
		public String getSoapOperationSoapAction(){
			return soapOperationSoapAction;
		}
		public void setSoapOperationStyle(String value){
			soapOperationStyle = value;
		}
		public String getSoapOperationStyle(){
			return soapOperationStyle;
		}
		public void setOperationName(String value){
			operationName = value;
		}
		public String getOperationName(){
			return operationName;
		}
		public void setInputMessageName(String value){
			inputMessageName = value;
		}
		public String getInputMessageName(){
			return inputMessageName;
		}
		public void setInputMessage(Element value){
			inputMessage = value;
		}
		public Element getInputMessage(){
			return inputMessage;
		}
		public void setInputSoapBodyUse(String value){
			inputSoapBodyUse=value;
		}
		public String getInputSoapBodyUse(){
			return inputSoapBodyUse;
		}
		public void setInputSoapBodyNamespace(String value){
			inputSoapBodyNamespace=value;
		}
		public String getInputSoapBodyNamespace(){
			return inputSoapBodyNamespace;
		}
		public void setInputSoapBodyEncodingStyle(String value){
			inputSoapBodyEncodingStyle=value;
		}
		public String getInputSoapBodyEncodingStyle(){
			return inputSoapBodyEncodingStyle;
		}
		
		public void setInputHeaderMessageName(String value){
			inputHeaderMessageName = value;
		}
		public String getInputHeaderMessageName(){
		   return inputHeaderMessageName;
	   	}
		public void setInputHeaderMessage(Element value){
			inputHeaderMessage = value;
		}
		public Element getInputHeaderMessage(){
			return inputHeaderMessage;
		}
		public void setInputSoapHeaderUse(String value){
			inputSoapHeaderUse=value;
		}
		public String getInputSoapHeaderUse(){
			return inputSoapHeaderUse;
		}
		public void setOutputSoapBodyNamespace(String value){
			outputSoapBodyNamespace=value;
		}
		public String getOutputSoapBodyNamespace(){
			return outputSoapBodyNamespace;
		}
		public void setOutputSoapBodyEncodingStyle(String value){
			outputSoapBodyEncodingStyle=value;
		}
		public String getOutputSoapBodyEncodingStyle(){
			return outputSoapBodyEncodingStyle;
		}
		
		
		public void setOutputMessageName(String value){
			outputMessageName = value;
		}
		public String getOutputMessageName(){
		   return outputMessageName;
	   	}
		public void setOutputMessage(Element value){
			outputMessage = value;
		}
		public Element getOutputMessage(){
			return outputMessage;
		}
		public void setOutputSoapBodyUse(String value){
			outputSoapBodyUse=value;
		}
		public String getOutputSoapBodyUse(){
			return outputSoapBodyUse;
		}
		
		public void setOutputHeaderMessageName(String value){
			outputHeaderMessageName = value;
		}
		public String getOutputHeaderMessageName(){
		   return outputHeaderMessageName;
	   	}
		public void setOutputHeaderMessage(Element value){
			outputHeaderMessage = value;
		}
		public Element getOutputHeaderMessage(){
			return outputHeaderMessage;
		}
		public void setOutputSoapHeaderUse(String value){
			outputSoapHeaderUse=value;
		}
		public String getOutputSoapHeaderUse(){
			return outputSoapHeaderUse;
		}
		
		public void setFaultMessageName(String value){
			faultMessageName = value;
		}
		public String getFaultMessageName(){
		   return faultMessageName;
	   	}
		public void setFaultMessage(Element value){
			faultMessage = value;
		}
		public Element getFaultMessage(){
			return faultMessage;
		}
		public void setFaultSoapBodyUse(String value){
			faultSoapBodyUse = value;
		}
		public String getFaultSoapBodyUse(){
			return faultSoapBodyUse;
		}
		private String nodeToString(Node node){
			if(node==null)return "";
			Properties prop = new Properties();
			prop.put("omit-xml-declaration", "yes");
			try {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				Transformer transformer=TransformerFactory.newInstance().newTransformer();
				if(prop != null)transformer.setOutputProperties(prop);
				transformer.transform(new DOMSource(node), new StreamResult(baos));
				return baos.toString();
		    }catch(TransformerConfigurationException tcx){
		    	tcx.printStackTrace();
		    }catch(TransformerException te){
		    	te.printStackTrace();
		    }
		    return "";
		}
		public String toString(){
			String string="\n\n soapAddressLocation :"+soapAddressLocation;
			string+="\n targetNamespace :"+targetNamespace;
			string+="\n schemaElementFormDefault :"+schemaElementFormDefault;

			string+="\n operationName :"+operationName;
			string+="\n soapBindingStyle :"+soapBindingStyle;
			string+="\n soapBindingTransport :"+soapBindingTransport;
			string+="\n soapBindingVerb :"+soapBindingVerb;
			string+="\n soapOperationStyle :"+soapOperationStyle;
			string+="\n soapOperationSoapAction :"+soapOperationSoapAction;
			
			string+="\n inputMessageName :"+inputMessageName;
			string+="\n inputMessage :"+nodeToString(inputMessage);
			string+="\n inputSoapBodyUse :"+inputSoapBodyUse;
			string+="\n inputSoapBodyNamespace :"+inputSoapBodyNamespace;
			string+="\n inputSoapBodyEncodingStyle :"+inputSoapBodyEncodingStyle;
			string+="\n inputHeaderMessageName :"+inputHeaderMessageName;
			string+="\n inputHeaderMessage :"+nodeToString(inputHeaderMessage);
			string+="\n inputSoapHeaderUse :"+inputSoapHeaderUse;

			string+="\n outputMessageName :"+outputMessageName;
			string+="\n outputMessage :"+nodeToString(outputMessage);
			string+="\n outputSoapBodyUse :"+outputSoapBodyUse;
			string+="\n outputSoapBodyNamespace :"+outputSoapBodyNamespace;
			string+="\n outputSoapBodyEncodingStyle :"+outputSoapBodyEncodingStyle;
			string+="\n outputHeaderMessageName :"+outputHeaderMessageName;
			string+="\n outputHeaderMessage :"+nodeToString(outputHeaderMessage);
			string+="\n outputSoapHeaderUse :"+outputSoapHeaderUse;
			
			string+="\n faultMessageName :"+faultMessageName;
			string+="\n faultMessage :"+nodeToString(faultMessage);

			return string;
		}

}