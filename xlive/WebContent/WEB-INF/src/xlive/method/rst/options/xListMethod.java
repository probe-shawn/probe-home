package xlive.method.rst.options;


import xlive.xWebInformation;
import xlive.google.ds.xHash;
import xlive.method.*;
import xlive.xml.xXmlDocument;

import org.w3c.dom.*;

public class xListMethod extends xDefaultMethod{
	public Object process()throws xMethodException{
		boolean valid=true;
		String why = "";
		Element data= this.setReturnArguments("data", "");

		String mall_fid=this.getArguments("mall-fid");
		Element optionxml,pxml;
		byte[] bytexml = new xHash("bom_options").get(mall_fid);
		if(bytexml==null)bytexml = new xHash().get("bom_options");
		
		if(bytexml != null) {
			optionxml =(Element) data.getOwnerDocument().adoptNode(xXmlDocument.bytesToNode(bytexml));
			data.appendChild(optionxml);
		}else {
	    	data.appendChild(pxml=xWebInformation.createElement("pxml"));
	    	pxml.setAttribute("pxml", "pxml");
	    	pxml.setAttribute("name", "�ӫ~��ܶ�");
	    	//
	    	pxml.appendChild(optionxml = xWebInformation.createElement("options"));
	    	optionxml.setAttribute("pxml", "g");
	    	optionxml.setAttribute("prolog", "��ܶ� :");
	    	optionxml.setAttribute("epilog", "<span style='font-size:12px;color:gray'>�s�W�νs���ܶ���</span>");
	    	optionxml.setAttribute("writable", "true");
	    	optionxml.setAttribute("showValue","false");
	    }
		
		
		this.setReturnArguments("valid", String.valueOf(valid));
		this.setReturnArguments("why", why);
		return this.getServiceContext().doNextProcess();
	}
}
