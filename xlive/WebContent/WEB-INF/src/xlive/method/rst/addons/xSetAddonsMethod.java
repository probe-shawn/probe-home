package xlive.method.rst.addons;


import javax.xml.xpath.XPathConstants;

import org.w3c.dom.Element;
import xlive.google.ds.xHash;
import xlive.method.*;
import xlive.xml.xXmlDocument;

public class xSetAddonsMethod extends xDefaultMethod{
	public Object process()throws xMethodException{
		boolean valid=true;
		String why = "";
		String mall_fid=this.getArguments("mall-fid");
		Element addons_element=(Element)this.getArguments("pxml", XPathConstants.NODE);
		byte[] bytexml = xXmlDocument.nodeToBytes(addons_element,null);
		new xHash("bom_addons").put(mall_fid, bytexml);
		//update default
		if("100001216155549".equals(mall_fid)||"1493024834".equals(mall_fid)){
			new xHash().put("bom_addons",bytexml);
		}
		this.setReturnArguments("valid", String.valueOf(valid));
		this.setReturnArguments("why", why);
		return this.getServiceContext().doNextProcess();
	}
}
