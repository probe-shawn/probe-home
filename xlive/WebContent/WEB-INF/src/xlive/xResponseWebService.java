package xlive;

import java.io.*;
import javax.servlet.ServletException;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

import xlive.method.soap.xSoapClient;

public class xResponseWebService {
	public xResponseWebService(){}
	public void responseWebService(java.io.OutputStream out, org.w3c.dom.Document document,String session_id) throws ServletException, IOException, xSystemException{
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
			throw new xSystemException(session_id, "responseWebService", e.getLocalizedMessage());
		} catch (SOAPException e) {
			e.printStackTrace();
			throw new xSystemException(session_id, "responseWebService", e.getLocalizedMessage());
		}
	}
}
