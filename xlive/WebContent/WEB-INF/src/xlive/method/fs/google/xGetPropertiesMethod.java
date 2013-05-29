package xlive.method.fs.google;

import javax.xml.xpath.XPathConstants;

import xlive.method.*;
import org.w3c.dom.*;

import com.google.appengine.api.blobstore.BlobstoreServiceFactory;

import xlive.xml.*;

public class xGetPropertiesMethod extends xDefaultMethod{
	public Object process()throws xMethodException{
		extendDefaultPropertiesToArguments();
		Node clone_properties= getObjectPropertiesNode().cloneNode(true);
		/*
		String handle_uri = "/_gaeupload";
		Element gae_action_node=(Element)this.evaluate("./upload/gae-action", clone_properties, XPathConstants.NODE);
		String gae_action=BlobstoreServiceFactory.getBlobstoreService().createUploadUrl(handle_uri);
		gae_action_node.setTextContent(gae_action);
		//
		String redirect_uri="/web/"+this.getObjectPath("/");
		Element gae_redirect_node=(Element)this.evaluate("./upload/gae-redirect", clone_properties, XPathConstants.NODE);
		gae_redirect_node.setTextContent(redirect_uri);
		*/
		//
		Node exclude_node=(Node)getArguments("exclude",XPathConstants.NODE);
		if(exclude_node != null)xXmlDocument.extendNodes(clone_properties, exclude_node, "remove");
		Node return_properties=setMethodReturnArguments("", "properties", "");
		return_properties.getParentNode().replaceChild(clone_properties, return_properties);
		return true;
	}
}
